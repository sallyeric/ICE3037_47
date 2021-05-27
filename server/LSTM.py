import torch
import threading
from torch.autograd import Variable
from sklearn.preprocessing import StandardScaler, MinMaxScaler
from LSTM_models.LSTM_model import LSTM
from datetime import datetime, timedelta

class LSTM:
    def __init__(self, creonTradeObj, getChartObj):
        self.timeToTrade = datetime.datetime.now().strftime("%Y%m%d")+'1500'
        self.codes = ['A000270', 'A000660', 'A005380', 'A005490', 'A005930', 'A035420', 'A035720', 'A051910', 'A068270']
        self.names = ['기아', 'SK하이닉스', '현대차', 'POSCO', '삼성전자', 'NAVER', '카카오', 'LG화학', '셀트리온']
        codeToName = {}
        nameToCode = {}
        for i in range(9):
            codeToName[self.codes[i]] = self.names[i]
            nameToCode[self.names[i]] = self.codes[i]
        self.creonTradeObj = creonTradeObj
        self.getChartObj = getChartObj
    def predictAndTrade(self, name, chartData):
        input_size = 5
        hidden_size = 256
        num_layers = 3

        device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")  # device
        model = LSTM(input_size, hidden_size, num_layers).to(device)
        model.load_state_dict(torch.load('./'+name+'.pt')['model_state_dict'])
        model.eval()

        mm = MinMaxScaler()
        chartData = MinMaxScaler().fit_transform(chartData)
        chartData = Variable(torch.tensor(chartData).to(device))
        chartData = chartData.reshape(1, chartData.shape[0], chartData.shape[1]).float()

        today_predict = model(chartData[:, -50:, :])
        tommorow_predict = model(chartData[:, -51:-1, :])

        if today_predict < tommorow_predict:
            self.creonTradeObj.buyOrder(self.nameToCode[name], 2)
        elif today_predict > tommorow_predict:
            self.creonTradeObj.sellOrder(self.nameToCode[name], 2)

    def run(self):
        nowDay = datetime.now().strftime("%Y%m%d%H%M")
        if nowDay == self.timeToTrade:
            self.timeToTrade = datetime.now().strftime("%Y%m%d%H%M") + timedelta(days=1)
            for name in self.names:
                self.predictAndTrade(name, self.getChartObj.getLSTMChartData(name))
        threading.Timer(60, self.run).start()