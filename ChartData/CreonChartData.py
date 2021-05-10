import sys
import win32com.client
import pandas as pd
import os
from pymongo import MongoClient

client = MongoClient('localhost', 27017)
db = client.db

g_objCodeMgr = win32com.client.Dispatch('CpUtil.CpCodeMgr')
g_objCpStatus = win32com.client.Dispatch('CpUtil.CpCybos')

objStockChart = win32com.client.Dispatch("CpSysDib.StockChart")

bConnect = g_objCpStatus.IsConnect
if (bConnect == 0):
    print("PLUS가 정상적으로 연결되지 않음. ")
    exit()

codeList = g_objCodeMgr.GetStockListByMarket(1)
nameList = ['LG이노텍','대한항공','엔씨소프트','삼성전자', 'SK하이닉스', 'NAVER', 'LG화학', '현대차', '셀트리온', '카카오', '기아', 'POSCO']
for code in codeList:
    name = g_objCodeMgr.CodeToName(code)
    if name not in nameList:
        continue
    print(name, code)

    objStockChart.SetInputValue(0, code)  # 종목코드
    objStockChart.SetInputValue(1, ord('1'))  # 기간으로 받기
    objStockChart.SetInputValue(2, 20201231)  # To 날짜
    objStockChart.SetInputValue(3, 20100101)  # From 날짜
    # objStockChart.SetInputValue(4, 500)  # 최근 500일치
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

    dates = []
    opens = []
    highs = []
    lows = []
    closes = []
    vols = []
    diff = [0.0]
    for i in range(len):
        dates.append(objStockChart.GetDataValue(0, i))
        opens.append(objStockChart.GetDataValue(1, i))
        highs.append(objStockChart.GetDataValue(2, i))
        lows.append(objStockChart.GetDataValue(3, i))
        closes.append(objStockChart.GetDataValue(4, i))
        vols.append(objStockChart.GetDataValue(5, i))
        if i > 0:
            diff.append(float((closes[i-1] - closes[i])/closes[i]))

    chartfile = name+'.csv'
    chartData = {'일자': dates,
                 '시가': opens,
                 '고가': highs,
                 '저가': lows,
                 '종가': closes,
                 '거래량': vols,
                 '변화량': diff
                 }

    df = pd.DataFrame(chartData, columns=['일자', '시가', '고가', '저가', '종가', '거래량', '변화량'])
    df = df.set_index('일자')

    # create a Pandas Excel writer using XlsxWriter as the engine.
    #writer = pd.ExcelWriter(chartfile, engine='xlsxwriter')
    # Convert the dataframe to an XlsxWriter Excel object.
    #df.to_excel(writer, sheet_name='Sheet1')
    df.to_csv(chartfile, encoding='utf-8-sig')
    # Close the Pandas Excel writer and output the Excel file.
    #writer.save()
    #os.startfile(chartfile)
