import sys
from PyQt5.QtWidgets import *
import win32com.client
import ctypes
import time

################################################
# CpEvent: 실시간 이벤트 수신 클래스
class CpEvent:
    def set_params(self, client, name, caller):
        self.client = client  # CP 실시간 통신 object
        self.name = name  # 서비스가 다른 이벤트를 구분하기 위한 이름
        self.caller = caller  # callback 을 위해 보관

    def OnReceived(self):
        # 실시간 처리 - 현재가 체결 데이터
        if self.name == 'stockcur':
            code = self.client.GetHeaderValue(0)  # 초
            name = self.client.GetHeaderValue(1)  # 초
            timess = self.client.GetHeaderValue(18)  # 초
            exFlag = self.client.GetHeaderValue(19)  # 예상체결 플래그
            cprice = self.client.GetHeaderValue(13)  # 현재가
            diff = self.client.GetHeaderValue(2)  # 대비
            cVol = self.client.GetHeaderValue(17)  # 순간체결수량
            vol = self.client.GetHeaderValue(9)  # 거래량

            if exFlag != ord('2'):
                return

            item = {}
            item['code'] = code
            item['time'] = timess
            item['diff'] = diff
            item['cur'] = cprice
            item['vol'] = cVol

            # 현재가 업데이트
            self.caller.updateCurData(item)

            return


################################################
# plus 실시간 수신 base 클래스
class CpPublish:
    def __init__(self, name, serviceID):
        self.name = name
        self.obj = win32com.client.Dispatch(serviceID)
        self.bIsSB = False

    def Subscribe(self, var, caller):
        if self.bIsSB:
            self.Unsubscribe()

        if (len(var) > 0):
            self.obj.SetInputValue(0, var)

        handler = win32com.client.WithEvents(self.obj, CpEvent)
        handler.set_params(self.obj, self.name, caller)
        self.obj.Subscribe()
        self.bIsSB = True

    def Unsubscribe(self):
        if self.bIsSB:
            self.obj.Unsubscribe()
        self.bIsSB = False


################################################
# CpPBStockCur: 실시간 현재가 요청 클래스
class CpPBStockCur(CpPublish):
    def __init__(self):
        super().__init__('stockcur', 'DsCbo1.StockCur')


# MACD 지표 계산
class CMACD:
    def __init__(self):
        self.objSeries = win32com.client.Dispatch("CpIndexes.CpSeries")
        self.objIndex = win32com.client.Dispatch("CpIndexes.CpIndex")

    # 차트 데이터 세팅 하기
    def setChartData(self, chartData):
        nLen = len(chartData['T'])
        for i in range(nLen):
            self.objSeries.Add(chartData['C'][i], chartData['O'][i], chartData['H'][i], chartData['L'][i],
                               chartData['V'][i])

        return

    # 기존 차트 데이터에 새로 들어온 신규 데이터 추가
    def addLastData(self, chartData):
        self.objSeries.Add(chartData['C'][-1], chartData['O'][-1], chartData['H'][-1], chartData['L'][-1],
                           chartData['V'][-1])

    # MACD 계산
    def makeMACD(self):
        result = {}
        # 지표 계산 object
        self.objIndex.series = self.objSeries
        self.objIndex.put_IndexKind("MACD")  # 계산할 지표: MACD
        self.objIndex.put_IndexDefault("MACD")  # MACD 지표 기본 변수 자동 세팅

        print("MACD 변수", self.objIndex.get_Term1(), self.objIndex.get_Term2(), self.objIndex.get_Signal())

        # 지표 데이터 계산 하기
        self.objIndex.Calculate()

        cntofIndex = self.objIndex.ItemCount
        print("지표 개수:  ", cntofIndex)
        indexName = ["MACD", "SIGNAL", "OSC"]

        result['MACD'] = []
        result['SIGNAL'] = []
        result['OSC'] = []
        for index in range(cntofIndex):
            cnt = self.objIndex.GetCount(index)
            for j in range(cnt):
                value = self.objIndex.GetResult(index, j)
                result[indexName[index]].append(value)
            # for j in range(cnt) :
            #    value = self.objIndex.GetResult(index,j)
            # print(indexName[index], value)  # 지표의 최근 값 표시

        print('MACD %.2f SIGNLA %.2f OSC %.2f' % (result['MACD'][-1], result['SIGNAL'][-1], result['OSC'][-1]))
        return (True, result)

    # MACD 업데이트(차트 데이터 개수에 변화가 없을 경우에만 사용)
    def updateMACD(self, chartData):
        result = {}
        # 지표 데이터 update
        self.objSeries.update(chartData['C'][-1], chartData['O'][-1], chartData['H'][-1], chartData['L'][-1],
                              chartData['V'][-1])
        self.objIndex.update()
        cntofIndex = self.objIndex.ItemCount
        print("지표 개수:  ", cntofIndex)

        indexName = ["MACD", "SIGNAL", "OSC"]

        result['MACD'] = []
        result['SIGNAL'] = []
        result['OSC'] = []

        for index in range(cntofIndex):
            cnt = self.objIndex.GetCount(index)
            for j in range(cnt):
                value = self.objIndex.GetResult(index, j)
                result[indexName[index]].append(value)

        print('MACD %.2f SIGNLA %.2f OSC %.2f' % (result['MACD'][-1], result['SIGNAL'][-1], result['OSC'][-1]))
        return (True, result)


# 분차트 관리 클래스
#   주어진 주기로 분차트 조회 , 실시간 분차트 데이터 생성, MACD 계산 호출
class CMinchartData:
    def __init__(self, interval, creonTradeObj):
        # interval : 분차트 주기
        self.interval = interval
        self.objCur = {}
        self.data = {}
        self.code = ''
        self.objMACD = CMACD()
        self.LASTTIME = 1530

        # 오늘 날짜
        now = time.localtime()
        self.todayDate = now.tm_year * 10000 + now.tm_mon * 100 + now.tm_mday
        print(self.todayDate)

        self.creonTradeObj = creonTradeObj

    def MonCode(self, code):
        self.data = {}
        self.code = code

        self.data['O'] = []
        self.data['H'] = []
        self.data['L'] = []
        self.data['C'] = []
        self.data['V'] = []
        self.data['D'] = []
        self.data['T'] = []
        self.data['MACD'] = []
        self.data['SIGNAL'] = []
        self.data['OSC'] = []

        # 차트 기본 통신
        self.rqChartMinData(code, self.interval)

        # MACD 클래스에 수신 받은 차트 데이터 세팅
        self.objMACD.setChartData(self.data)
        # MACD 계산 하기
        ret, result = self.objMACD.makeMACD()

        self.data['MACD'] = result['MACD']
        self.data['SIGNAL'] = result['SIGNAL']
        self.data['OSC'] = result['OSC']

        # 실시간 시세 요청
        if (code not in self.objCur):
            self.objCur[code] = CpPBStockCur()
            self.objCur[code].Subscribe(code, self)

    def stop(self):
        for k, v in self.objCur.items():
            v.Unsubscribe()
        self.objCur = {}

    # 분차트 - 코드, 주기, 개수
    def rqChartMinData(self, code, interval):
        objRq = win32com.client.Dispatch("CpSysDib.StockChart")

        objRq.SetInputValue(0, code)  # 종목 코드
        objRq.SetInputValue(1, ord('2'))  # 개수로 조회
        objRq.SetInputValue(4, 500)  # 통신 개수 - 500 개로 고정
        objRq.SetInputValue(5, [0, 1, 2, 3, 4, 5, 8])  # 날짜,시간, 시가,고가,저가,종가,거래량
        objRq.SetInputValue(6, ord('m'))  # '차트 주가 - 분 데이터
        objRq.SetInputValue(7, interval)  # 차트 주기
        objRq.SetInputValue(9, ord('1'))  # 9 - 수정주가(char)

        totlen = 0
        objRq.BlockRequest()
        rqStatus = objRq.GetDibStatus()
        rqRet = objRq.GetDibMsg1()
        print("통신상태", rqStatus, rqRet)
        if rqStatus != 0:
            exit()

        len = objRq.GetHeaderValue(3)
        print(totlen)
        totlen += len

        print("날짜", "시가", "고가", "저가", "종가", "거래량")
        print("==============================================-")

        for i in range(len):
            day = objRq.GetDataValue(0, i)
            time = objRq.GetDataValue(1, i)
            open = objRq.GetDataValue(2, i)
            high = objRq.GetDataValue(3, i)
            low = objRq.GetDataValue(4, i)
            close = objRq.GetDataValue(5, i)
            vol = objRq.GetDataValue(6, i)

            self.data['D'].append(day)
            self.data['T'].append(time)
            self.data['O'].append(open)
            self.data['H'].append(high)
            self.data['L'].append(low)
            self.data['C'].append(close)
            self.data['V'].append(vol)

        # 수신된 역순으로 넣는다 -> 최근 날짜가 맨 뒤로 가도록
        self.data['D'].reverse()
        self.data['T'].reverse()
        self.data['O'].reverse()
        self.data['H'].reverse()
        self.data['L'].reverse()
        self.data['C'].reverse()
        self.data['V'].reverse()

    # 가격 실시간 변경 시 분차트 데이터 재 계산
    def updateCurData(self, item):
        time = item['time']
        self.cur = cur = item['cur']
        vol = item['vol']
        self.makeMinchart(time, cur, vol)

    def getHMTFromTime(self, time):
        hh, mm = divmod(time, 10000)
        mm, tt = divmod(mm, 100)
        return (hh, mm, tt)

    def getChartTime(self, time):
        # time 600 (10:00 인 경우 600)
        lChartTime = time + self.interval

        # 630 ==> 1030 분으로 변경
        hour, min = divmod(lChartTime, 60)
        lCurTime = hour * 100 + min

        if (lCurTime > self.LASTTIME):
            lCurTime = self.LASTTIME

        return lCurTime

    # 실시간 데이터를 통해 분차트 업데이트
    def makeMinchart(self, time, cur, vol):
        # time 분해 ==> 시, 분, 초
        hh, mm, tt = self.getHMTFromTime(time)
        # hhmm = hh * 100 + mm
        # 1000 ==> 600 분
        converedMintime = hh * 60 + mm

        bFind = False
        nLen = len(self.data['T'])

        # 분차트 주기 기준으로 나눠서 시간 차트 시간 계산
        #   1분 봉의 경우 14:10분 봉: 14:09:00~14:09:59
        #   5분 봉의 경우 14:10분 봉 : 14:05:00~ 14:09:59
        a, b = divmod(converedMintime, self.interval)
        intervaltime = a * self.interval
        lCurTime = self.getChartTime(intervaltime)
        print('차트 시간 계산 : 들어온 시간 %d, 차트 시간 %d' % (time, lCurTime))

        if (nLen > 0):
            lLastTime = self.data['T'][-1]
            if (lLastTime == lCurTime):
                bFind = True

                self.data['C'][-1] = cur
                if (self.data['H'][-1] < cur):
                    self.data['H'][-1] = cur
                if (self.data['L'][-1] > cur):
                    self.data['L'][-1] = cur
                self.data['V'][-1] += vol
                print('들어온 시간 %d ==> 마지막 분차트 시간 %d 에 업데이트' % (time, lLastTime))

                ret, result = self.objMACD.updateMACD(self.data)
                self.data['MACD'] = result['MACD']
                self.data['SIGNAL'] = result['SIGNAL']
                self.data['OSC'] = result['OSC']

        # 신규 봉이 추가
        if bFind == False:
            print('들어온 시간 %d ==> 새로운 분차트 시간 %d 에 업데이트' % (time, lCurTime))
            self.data['D'].append(self.todayDate)
            self.data['T'].append(lCurTime)
            self.data['O'].append(cur)
            self.data['H'].append(cur)
            self.data['L'].append(cur)
            self.data['C'].append(cur)
            self.data['V'].append(vol)

            # 데이터 추가 - MACD 계산 모듈에 차트 데이터 추가
            self.objMACD.addLastData(self.data)
            # MACD 계산
            ret, result = self.objMACD.makeMACD()

            self.data['MACD'] = result['MACD']
            self.data['SIGNAL'] = result['SIGNAL']
            self.data['OSC'] = result['OSC']

            # MACD 의 신호가 교차됐는지 체크
            self.checkMACD()

        return

    def checkMACD(self):
        if (len(self.data['OSC']) < 5):
            return
        # 현재 시점에서 이전 봉(-2) 가 매수신호/매도 신호 발생했는 지 체크
        # -1 : 현재 시점 -2: 바로 직전 봉 -3 그 전 봉
        print('osc', self.data['OSC'][-3], self.data['OSC'][-2], self.data['OSC'][-1])
        if self.data['OSC'][-3] < 0:
            if self.data['OSC'][-2] > 0:
                ## 매수신호
                self.creonTradeObj.buyOrder(self.code)
                print('MACD 매수, 종목코드 %d, 시간 %d, 가격 %d' % (self.code, self.data['T'][-1], self.data['C'][-1]))
        elif self.data['OSC'][-3] > 0:
            if self.data['OSC'][-2] < 0:
                ## 매도신호
                self.creonTradeObj.sellOrder(self.code)
                print('MACD 매도, 종목코드 %d, 시간 %d, 가격 %d' % (self.code, self.data['T'][-1], self.data['C'][-1]))


################################################
class MACD_calculator():
    def __init__(self, code, creonTradeObj):
        self.minData = CMinchartData(5, creonTradeObj)
        self.minData.MonCode(code)


class getRealTimeMACD():
    def __init__(self, creonTradeObj):
        print('init getRealTimeMACD')
        self.codes = ['A000270', 'A000660', 'A005380', 'A005490', 'A005930', 'A035420', 'A035720', 'A051910', 'A068270']
        self.objs = [MACD_calculator(code, creonTradeObj) for code in self.codes]
