import threading
import win32com.client
from pymongo import MongoClient
import datetime

class CpCybos:
    def __init__(self):
        self.objCpCybos = win32com.client.Dispatch("CpUtil.CpCybos")
        bConnect = self.objCpCybos.IsConnect
        if (bConnect == 0):
            print("PLUS가 정상적으로 연결되지 않음. ")

class getRealTimeChartData:
    def __init__(self):
        self.codes = ['A000270','A000660','A005380','A005490','A005930','A035420','A035720','A051910','A068270']
        self.datas = [{} for i in range(9)]

        # 현재가 객체 구하기
        self.rqField = [0, 1, 2, 3, 4, 17]
        self.objRq = win32com.client.Dispatch("CpSysDib.MarketEye")
        self.objRq.SetInputValue(0, self.rqField)
        self.objRq.SetInputValue(1, self.codes)
        self.objRq.BlockRequest()

        # 현재가 통신 및 통신 에러 처리
        rqStatus = self.objRq.GetDibStatus()
        rqRet = self.objRq.GetDibMsg1()
        print("통신상태", rqStatus, rqRet)
        if rqStatus != 0:
            exit()
    def run(self):
        print('checking real time chart')

        cnt = self.objRq.GetHeaderValue(2)
        for i in range(cnt):
            code = self.objRq.GetDataValue(0, i)  # 종목코드
            name = self.objRq.GetDataValue(5, i)  # 종목명
            time = self.objRq.GetDataValue(1, i)  # 시간
            price = self.objRq.GetDataValue(4, i)  # 현재가
            diff = self.objRq.GetDataValue(3, i)  # 현재가
            data = {'code':code, 'name':name, 'time':time, 'price':price, 'diff':diff}
            self.datas[i] = data.copy()
            print(f'{i}: {code} {name} {time} {price} {diff}')
        threading.Timer(60, self.run).start()

class getDayChartData:
    def __init__(self):
        self.codes = ['A000270', 'A000660', 'A005380', 'A005490', 'A005930', 'A035420', 'A035720', 'A051910', 'A068270']
        self.name = ['kia', 'sk', 'hyundai', 'posco', 'samsung', 'naver', 'kakao', 'lg', 'celltrion']
        self.client = MongoClient('localhost', 27017)
        self.db = self.client.db
        self.updateTime = datetime.datetime.now().strftime("%Y%m%d")
    def run(self):
        print('checking day chart')
        nowDay = datetime.datetime.now().strftime("%Y%m%d")
        if nowDay != self.updateTime:
            print(f'updating day chart, before : {self.updateTime}, after : {nowDay}')
            self.getOldDatas(1)
            self.updateTime = nowDay

        threading.Timer(60, self.run).start()
    def getOldDatas(self, days):
        objStockChart = win32com.client.Dispatch("CpSysDib.StockChart")
        for k, code in enumerate(self.codes):
            print(k, code, self.name[k])
            objStockChart.SetInputValue(0, code)  # 종목코드
            objStockChart.SetInputValue(1, ord('2'))  # 기간으로 받기
            #objStockChart.SetInputValue(2, now)  # To 날짜
            #objStockChart.SetInputValue(3, 20000101)  # From 날짜
            objStockChart.SetInputValue(4, days)  # 최근 500일치
            objStockChart.SetInputValue(5, [0, 2, 3, 4, 5, 8])  # 날짜,시가,고가,저가,종가,거래량
            objStockChart.SetInputValue(6, ord('D'))  # '차트 주기 - 일간 차트 요청
            objStockChart.SetInputValue(9, ord('1'))  # 수정주가 사용
            objStockChart.BlockRequest()
            rqStatus = objStockChart.GetDibStatus()
            rqRet = objStockChart.GetDibMsg1()
            print("통신상태", rqStatus, rqRet)
            if rqStatus != 0:
                exit()
            len = objStockChart.GetHeaderValue(3)
            for i in range(len):
                date = objStockChart.GetDataValue(0, i)
                open = objStockChart.GetDataValue(1, i)
                high = objStockChart.GetDataValue(2, i)
                low = objStockChart.GetDataValue(3, i)
                close = objStockChart.GetDataValue(4, i)
                vols = objStockChart.GetDataValue(5, i)
                data = {'date':date,
                        'open':open,
                        'high':high,
                        'low':low,
                        'close':close,
                        'vols':vols}
                print('insert data', data)
                self.db[self.name[k]].insert_one(data)