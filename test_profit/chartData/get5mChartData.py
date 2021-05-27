import win32com.client
import pandas as pd
from datetime import date, timedelta
objStockChart = win32com.client.Dispatch("CpSysDib.StockChart")
codes = ['A000270', 'A000660', 'A005380', 'A005490', 'A005930', 'A035420', 'A035720', 'A051910', 'A068270']
names = ['기아', 'SK하이닉스', '현대차', 'POSCO', '삼성전자', 'NAVER', '카카오', 'LG화학', '셀트리온']

for j in range(9):
    dates = []
    opens = []
    highs = []
    lows = []
    closes = []
    vols = []
    times = []
    curDate = date(2021, 5, 1)
    for i in range(0, 9):

        objStockChart.SetInputValue(0, codes[j])  # 종목코드
        #objStockChart.SetInputValue(1, ord('2'))  # 개수로 받기
        objStockChart.SetInputValue(1, ord('1'))  # 기간으로 받기
        objStockChart.SetInputValue(2, curDate.strftime('%Y%m%d'))  # To 날짜
        objStockChart.SetInputValue(3, (curDate-timedelta(days=30)).strftime('%Y%m%d'))  # From 날짜
        #objStockChart.SetInputValue(4, 5000)  # 조회 개수
        objStockChart.SetInputValue(5, [0, 1, 2, 3, 4, 5, 8])  # 요청항목 - 날짜, 시간,시가,고가,저가,종가,거래량
        objStockChart.SetInputValue(6, ord('m'))  # '차트 주기 - 분/틱
        objStockChart.SetInputValue(7, 5)  # 분틱차트 주기
        objStockChart.SetInputValue(9, ord('1'))  # 수정주가 사용
        objStockChart.BlockRequest()
        curDate = curDate - timedelta(days=31)
        rqStatus = objStockChart.GetDibStatus()
        rqRet = objStockChart.GetDibMsg1()
        print("통신상태", rqStatus, rqRet)
        if rqStatus != 0:
            exit()

        length = objStockChart.GetHeaderValue(3)


        for k in range(length):
            t = str(objStockChart.GetDataValue(1, k))
            if len(t) == 3: t = "0"+t
            dates.append(str(objStockChart.GetDataValue(0,k))+t)
            opens.append(objStockChart.GetDataValue(2, k))
            highs.append(objStockChart.GetDataValue(3, k))
            lows.append(objStockChart.GetDataValue(4, k))
            closes.append(objStockChart.GetDataValue(5, k))
            vols.append(objStockChart.GetDataValue(6, k))
        print(names[j], length)

    chartfile = './chartData/'+names[j] + '.csv'
    chartData = {'일자': dates[::-1],
                 '시가': opens[::-1],
                 '고가': highs[::-1],
                 '저가': lows[::-1],
                 '종가': closes[::-1],
                 '거래량': vols[::-1]
                 }

    df = pd.DataFrame(chartData, columns=['일자', '시가', '고가', '저가', '종가', '거래량'])
    df = df.set_index('일자')
    df.to_csv(chartfile, encoding='utf-8-sig')