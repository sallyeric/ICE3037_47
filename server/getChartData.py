import threading
import win32com.client
from pymongo import MongoClient
import datetime
from dateutil.relativedelta import relativedelta
import tqdm

class CpCybos:
    def __init__(self):
        self.objCpCybos = win32com.client.Dispatch("CpUtil.CpCybos")
        bConnect = self.objCpCybos.IsConnect
        if (bConnect == 0):
            print("PLUS가 정상적으로 연결되지 않음. ")

class getRealTimeChartData:
    def __init__(self):
        self.codes = ['A000270','A000660','A005380','A005490','A005930','A035420','A035720','A051910','A068270']
        self.datas = dict()

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
            code = self.objRq.GetDataValue(0, i)
            name = self.objRq.GetDataValue(5, i)
            time = self.objRq.GetDataValue(1, i)
            price = self.objRq.GetDataValue(4, i)
            diff = self.objRq.GetDataValue(3, i)
            data = {'code':code, 'name':name, 'time':time, 'price':price, 'diff':diff}
            self.datas[name] = data.copy()
            print(f'{i}: {code} {name} {time} {price} {diff}')
        threading.Timer(60, self.run).start()

class getDayChartData:
    def __init__(self):
        self.codes = ['A000270', 'A000660', 'A005380', 'A005490', 'A005930', 'A035420', 'A035720', 'A051910', 'A068270']
        self.names = ['기아', 'SK하이닉스', '현대차', 'POSCO', '삼성전자', 'NAVER', '카카오', 'LG화학', '셀트리온']
        self.client = MongoClient("mongodb+srv://choi:zeKf2E10mHYA9Ivu@cluster0.pidsj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
        self.db = self.client.chartData
        self.updateTime = datetime.datetime.now().strftime("%Y%m%d")
        self.datas = {}
        self.getOldDatas(30)
        dates = [(datetime.datetime.now() - relativedelta(days=i)).strftime("%Y%m%d") for i in range(60)]
        for name in tqdm.tqdm(self.names, desc='getChartDataFromDB'):
            cnt = 0
            self.datas[name] = []
            for date in dates:
                c = self.db[name].find_one({'date': int(date)})
                if c:
                    cnt+=1
                    self.datas[name].append(c)
                if cnt >= 30: break
        print(self.datas)


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
            print(k, code, self.names[k])
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

                find = self.db[self.names[k]].find_one({'date':date})
                if find:
                    print('avoid data', data)
                else:
                    self.db[self.names[k]].insert_one(data)
                    print('insert data', data)