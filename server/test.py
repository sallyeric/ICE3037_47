

bConnect = g_objCpStatus.IsConnect
if (bConnect == 0):
    print("PLUS가 정상적으로 연결되지 않음. ")
    return False

self.objStockChart.SetInputValue(0, code)  # 종목코드
#self.objStockChart.SetInputValue(1, ord('2'))  # 개수로 받기
self.objStockChart.SetInputValue(1, ord('1'))  # 기간으로 받기
self.objStockChart.SetInputValue(2, 20210501)  # To 날짜
self.objStockChart.SetInputValue(3, 20201101)  # From 날짜
#self.objStockChart.SetInputValue(4, 5000)  # 조회 개수
self.objStockChart.SetInputValue(5, [0, 1, 2, 3, 4, 5, 8])  # 요청항목 - 날짜, 시간,시가,고가,저가,종가,거래량
self.objStockChart.SetInputValue(6, dwm)  # '차트 주기 - 분/틱
self.objStockChart.SetInputValue(7, 5)  # 분틱차트 주기
self.objStockChart.SetInputValue(9, ord('1'))  # 수정주가 사용
self.objStockChart.BlockRequest()

rqStatus = self.objStockChart.GetDibStatus()
rqRet = self.objStockChart.GetDibMsg1()
print("통신상태", rqStatus, rqRet)
if rqStatus != 0:
    exit()

len = self.objStockChart.GetHeaderValue(3)

caller.dates = []
caller.opens = []
caller.highs = []
caller.lows = []
caller.closes = []
caller.vols = []
caller.times = []
for i in range(len):
    caller.dates.append(self.objStockChart.GetDataValue(0, i))
    caller.times.append(self.objStockChart.GetDataValue(1, i))
    caller.opens.append(self.objStockChart.GetDataValue(2, i))
    caller.highs.append(self.objStockChart.GetDataValue(3, i))
    caller.lows.append(self.objStockChart.GetDataValue(4, i))
    caller.closes.append(self.objStockChart.GetDataValue(5, i))
    caller.vols.append(self.objStockChart.GetDataValue(6, i))

print(len)