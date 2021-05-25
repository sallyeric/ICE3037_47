import win32com.client
import pandas as pd
from datetime import date, timedelta
g_objCodeMgr = win32com.client.Dispatch('CpUtil.CpCodeMgr')
g_objCpStatus = win32com.client.Dispatch('CpUtil.CpCybos')
objStockChart = win32com.client.Dispatch("CpSysDib.StockChart")
names = ['기아', 'SK하이닉스', '현대차', 'POSCO', '삼성전자', 'NAVER', '카카오', 'LG화학', '셀트리온']
#0 일자
#1 시가
#2 고가
#3 저가
#4 종가
#5 거래량
chart = pd.read_csv('./chartData/셀트리온.csv')
chart = chart[::-1]
result = {}
t = chart.iloc[:, 0].values
o = chart.iloc[:, 1].values
h = chart.iloc[:, 2].values
l = chart.iloc[:, 3].values
c = chart.iloc[:, 4].values
v = chart.iloc[:, 5].values
print(t[-1], o[-1], h[-1], l[-1], c[-1], v[-1])
objSeries = win32com.client.Dispatch("CpIndexes.CpSeries")
objIndex = win32com.client.Dispatch("CpIndexes.CpIndex")
for i in range(len(c)):
    objSeries.Add(c[i], o[i], h[i], l[i], v[i])

objIndex.series = objSeries
objIndex.put_IndexKind("MACD")  # 계산할 지표: MACD
objIndex.put_IndexDefault("MACD")  # MACD 지표 기본 변수 자동 세팅

print("MACD 변수", objIndex.get_Term1(), objIndex.get_Term2(), objIndex.get_Signal())

# 지표 데이터 계산 하기
objIndex.Calculate()

cntofIndex = objIndex.ItemCount
print("지표 개수:  ", cntofIndex)
indexName = ["MACD", "SIGNAL", "OSC"]

result['MACD'] = []
result['SIGNAL'] = []
result['OSC'] = []
for index in range(cntofIndex):
    cnt = objIndex.GetCount(index)
    print(cnt)
    for j in range(cnt):
        value = objIndex.GetResult(index, j)
        result[indexName[index]].append(value)
for i in range(len(result['MACD'])):
    print('%2d MACD %.2f SIGNLA %.2f OSC %.2f' % (i, result['MACD'][i], result['SIGNAL'][i], result['OSC'][i]))

chartfile = '셀트리온MACD.csv'
chartData = {'일자': t,
             'MACD':result['MACD'],
             'SIGNAL':result['SIGNAL'],
             'OSC':result['OSC']
             }

df = pd.DataFrame(chartData, columns=['일자', 'MACD', 'SIGNAL', 'OSC'])
df = df.set_index('일자')
df.to_csv(chartfile, encoding='utf-8-sig')