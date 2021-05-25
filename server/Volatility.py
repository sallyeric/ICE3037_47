import win32com.client
import datetime

from pymongo import MongoClient

import creonTrade
import threading
import pythoncom

class Volatility(threading.Thread):
    def __init__(self, creonTradeObj, interval=30):
        threading.Thread.__init__(self)
        self.interval = interval
        self.creonTradeObj = creonTradeObj
        self.date = 0
        self.range = dict()
        self.companyCodes = ['A000270', 'A000660', 'A005380', 'A005490', 'A005930', 'A035420', 'A035720', 'A051910', 'A068270']
        self.isBuying = dict()
        for code in self.companyCodes:
            self.isBuying[code] = False

    def updateVolatility(self, code):
        objCpCybos = win32com.client.Dispatch("CpUtil.CpCybos")
        bConnect = objCpCybos.IsConnect
        if (bConnect == 0):
            print("PLUS가 정상적으로 연결되지 않음. ")
            return False

        # 일자별 object 구하기
        objStockWeek = win32com.client.Dispatch("DsCbo1.StockWeek")
        objStockWeek.SetInputValue(0, code)  # 종목 코드 - 삼성전자

        objStockWeek.BlockRequest()

        # 통신 결과 확인
        rqStatus = objStockWeek.GetDibStatus()
        rqRet = objStockWeek.GetDibMsg1()
        # print("통신상태", rqStatus, rqRet)
        if rqStatus != 0:
            return False

        date = objStockWeek.GetDataValue(0, 1)  # 일자
        high = objStockWeek.GetDataValue(2, 1)  # 고가
        low = objStockWeek.GetDataValue(3, 1)  # 저가

        self.date = date
        self.range[code] = high - low

    def checkVolatility(self, code, user):
        if not self.isBuying[code]:
            objCpCybos = win32com.client.Dispatch("CpUtil.CpCybos")
            bConnect = objCpCybos.IsConnect
            if (bConnect == 0):
                print("PLUS가 정상적으로 연결되지 않음. ")
                return False

            # 현재가 객체 구하기
            objStockMst = win32com.client.Dispatch("DsCbo1.StockMst")
            objStockMst.SetInputValue(0, code)  # 종목 코드 - 삼성전자
            objStockMst.BlockRequest()

            # 현재가 통신 및 통신 에러 처리
            rqStatus = objStockMst.GetDibStatus()
            rqRet = objStockMst.GetDibMsg1()
            # print("통신상태", rqStatus, rqRet)
            if rqStatus != 0:
                return False

            # 현재가 정보 조회
            open = objStockMst.GetHeaderValue(13)  # 시가
            cprice = objStockMst.GetHeaderValue(11)  # 종가

            print(open, cprice)

            if cprice >= open + self.range[code]:
                if self.creonTradeObj.buyOrder(code, 1, user):
                    self.isBuying[code] = True

    def run(self):
        pythoncom.CoInitialize()

        client = MongoClient("mongodb+srv://yoo:789retry@cluster0.pidsj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
        db = client.Project
        users = db.userData.find()
        for user in users:
            for code in self.companyCodes:
                if self.date < int(datetime.datetime.now().strftime("%Y%m%d")):
                    if self.isBuying[code]:
                        
                        if self.creonTradeObj.sellOrder(code, 1, user):
                            self.updateVolatility(code)
                else:
                    self.checkVolatility(code, user)

        pythoncom.CoUninitialize()
        threading.Timer(self.interval, self.run).start()
