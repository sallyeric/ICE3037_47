import sys
import win32com.client
import ctypes
from pymongo import MongoClient
from datetime import datetime


def InitPlusCheck(g_objCpStatus, g_objCpTrade):
    # 프로세스가 관리자 권한으로 실행 여부
    if ctypes.windll.shell32.IsUserAnAdmin():
        print('정상: 관리자권한으로 실행된 프로세스입니다.')
    else:
        print('오류: 일반권한으로 실행됨. 관리자 권한으로 실행해 주세요')
        return False

    # 연결 여부 체크
    if (g_objCpStatus.IsConnect == 0):
        print("PLUS가 정상적으로 연결되지 않음. ")
        return False

    # 주문 관련 초기화
    if (g_objCpTrade.TradeInit(0) != 0):
        print("주문 초기화 실패")
        return False

    return True

class CpRPCurrentPrice:
    def __init__(self):
        self.objStockMst = win32com.client.Dispatch('DsCbo1.StockMst')
        return

    def Request(self, code):
        self.objStockMst.SetInputValue(0, code)
        ret = self.objStockMst.BlockRequest()
        if self.objStockMst.GetDibStatus() != 0:
            print('통신상태', self.objStockMst.GetDibStatus(), self.objStockMst.GetDibMsg1())
            return False

        item = {}
        item['code'] = code
        item['cur'] = self.objStockMst.GetHeaderValue(11)  # 종가
        item['diff'] = self.objStockMst.GetHeaderValue(12)  # 전일대비
        item['vol'] = self.objStockMst.GetHeaderValue(18)  # 거래량

        # 10차호가
        for i in range(10):
            key1 = 'offer%d' % (i + 1)
            key2 = 'bid%d' % (i + 1)
            item[key1] = (self.objStockMst.GetDataValue(0, i))  # 매도호가
            item[key2] = (self.objStockMst.GetDataValue(1, i))  # 매수호가
        curData = {}
        curData['sellPrice'] = item['bid1']
        curData['buyPrice'] = item['offer1']
        return curData

class creonTrade():
    def __init__(self):
        print('init creonTrade')
        self.g_objCpStatus = win32com.client.Dispatch('CpUtil.CpCybos')
        self.g_objCpTrade = win32com.client.Dispatch('CpTrade.CpTdUtil')
        InitPlusCheck(self.g_objCpStatus, self.g_objCpTrade)

        self.acc = self.g_objCpTrade.AccountNumber[0]  # 계좌번호
        self.accFlag = self.g_objCpTrade.GoodsList(self.acc, 1)  # 주식상품 구분
        print(self.acc, self.accFlag[0])
        self.objOrder = win32com.client.Dispatch("CpTrade.CpTd0311")  # 매수
        self.currentPrice = CpRPCurrentPrice()
        self.client = MongoClient("mongodb+srv://yoo:789retry@cluster0.pidsj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
        self.codes = ['A000270', 'A000660', 'A005380', 'A005490', 'A005930', 'A035420', 'A035720', 'A051910', 'A068270']
        self.names = ['기아', 'SK하이닉스', '현대차', 'POSCO', '삼성전자', 'NAVER', '카카오', 'LG화학', '셀트리온']
        self.types = ['macd', 'volat', 'lstm']
        self.codeNames = {}
        for i in range(9): self.codeNames[self.codes[i]] = self.names[i]
    def buyOrder(self, code, type, userId=None):
        # 주식 매수 주문
        db = self.client.Project
        users = db.userData.find()
        price = self.currentPrice.Request(code)['buyPrice']
        for user in users:
            if userId is not None and user['userId'] != userId: continue
            name = self.codeNames[code]
            company_active = user['active'].get(name)
            company_own = user['own']['stocks'].get(name)
            if company_active is None: continue
            if company_own: continue
            if company_active[self.types[type]] == False: continue
            amount = int(company_active['current'] / price)
            if amount <= 0: continue
            print(user['userId'], "신규 매수", code, price, amount)

            self.objOrder.SetInputValue(0, "2")  # 2: 매수
            self.objOrder.SetInputValue(1, self.acc)  # 계좌번호
            self.objOrder.SetInputValue(2, self.accFlag[0])  # 상품구분 - 주식 상품 중 첫번째
            self.objOrder.SetInputValue(3, code)  # 종목코드
            self.objOrder.SetInputValue(4, amount)  # 매수수량
            self.objOrder.SetInputValue(5, price)  # 주문단가
            self.objOrder.SetInputValue(7, "0")  # 주문 조건 구분 코드, 0: 기본 1: IOC 2:FOK
            self.objOrder.SetInputValue(8, "01")  # 주문호가 구분코드 - 01: 보통

            # 매수 주문 요청
            ret = self.objOrder.BlockRequest()
            if ret == 4:
                remainTime = self.g_objCpStatus.LimitRequestRemainTime
                print('주의: 주문 연속 통신 제한에 걸렸음. 대기해서 주문할 지 여부 판단이 필요 남은 시간', remainTime)
                return False

            rqStatus = self.objOrder.GetDibStatus()
            rqRet = self.objOrder.GetDibMsg1()
            print("통신상태", rqStatus, rqRet)
            if rqStatus != 0:
                return False

            db.userData.update_one({'userId': user['userId']},
                                   {'$set': {'active.' + name+'.current': company_active['current'] - price * amount}})
            db.userData.update_one({'userId': user['userId']},
                                   {'$set': {'own.stocks.' + name: {'size':amount, 'price':price, 'type':type}}})
            db.userData.update_one({'userId': user['userId']},
                                   {'$push': {'history': {'name':name,
                                                          'size':amount,
                                                          'price':price,
                                                          'date':datetime.now().strftime('%Y%m%d%H%M'),
                                                          'type':1}}})
        return True

    def sellOrder(self, code, type, userId=None):
        # 주식 매도 주문
        db = self.client.Project
        users = db.userData.find()
        price = self.currentPrice.Request(code)['sellPrice']
        for user in users:
            if userId is not None and user['userId'] != userId: continue
            name = self.codeNames[code]
            company_own = user['own']['stocks'].get(name)
            if company_own is None: continue
            if company_own['type'] != type: continue
            amount = company_own['size']
            print("신규 매도", code, price, amount)
            price = self.currentPrice.Request(code)['sellPrice']
            self.objOrder.SetInputValue(0, "1")  # 1: 매도
            self.objOrder.SetInputValue(1, self.acc)  # 계좌번호
            self.objOrder.SetInputValue(2, self.accFlag[0])  # 상품구분 - 주식 상품 중 첫번째
            self.objOrder.SetInputValue(3, code)  # 종목코드
            self.objOrder.SetInputValue(4, amount)  # 매수수량
            self.objOrder.SetInputValue(5, price)  # 주문단가
            self.objOrder.SetInputValue(7, "0")  # 주문 조건 구분 코드, 0: 기본 1: IOC 2:FOK
            self.objOrder.SetInputValue(8, "01")  # 주문호가 구분코드 - 01: 보통

            # 매도 주문 요청
            ret = self.objOrder.BlockRequest()
            rqStatus = self.objOrder.GetDibStatus()
            rqRet = self.objOrder.GetDibMsg1()
            print("통신상태", rqStatus, rqRet)
            if rqStatus != 0:
                return False

            db.userData.update_one({'userId': user['userId']},
                                   {'$push': {'history': {'name': name,
                                                          'size': amount,
                                                          'price': price,
                                                          'diff': price-company_own['price'],
                                                          'date': datetime.now().strftime('%Y%m%d%H%M'),
                                                          'type': 0}}})
            db.userData.update_one({'userId': user['userId']},
                                   {'$set': {'active.'+name+'.current': user['active'][name]['current'] + amount * price}})
            db.userData.update_one({'userId': user['userId']},
                                   {'$unset':{'own.stocks.'+name:1}})

        return True