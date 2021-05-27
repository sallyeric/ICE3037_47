import pandas as pd
import matplotlib.pyplot as plt
from matplotlib import font_manager, rc
font_path = "C:/Windows/Fonts/gulim.ttc"
font = font_manager.FontProperties(fname=font_path).get_name()
rc('font', family=font)

names = ['기아', 'SK하이닉스', '현대차', 'POSCO', '삼성전자', 'NAVER', '카카오', 'LG화학', '셀트리온']
for name in names:
    macd = pd.read_csv('./macdSignalData/'+name+'_MACD_SIGNAL.csv')
    osc = pd.read_csv('./macdData/'+name+'MACD.csv')
    lstm = pd.read_csv('./lstmSignalData/'+name+'_LSTM_SIGNAL.csv')
    vola = pd.read_csv('./volatilitySignalData/'+name+'_VOLATILITY_SIGNAL.scv')

    t = macd.iloc[:, 0].values
    types = macd.iloc[:, 1].values
    lstmtypes = lstm.iloc[:, 1].values
    volatypes = vola.iloc[:, 1].values
    prices = macd.iloc[:, 2].values
    oscs = osc.iloc[:, 3].values
    init_price = prices[0]

    profits = [100.0]
    g_prices = [100.0]
    money = 1000000.0
    own_amount = 0
    own_type = -1
    for i in range(1, len(t)):
        if types[i] == 2 and own_amount == 0:# and oscs[i] - oscs[i-1] > 200:
            own_amount = money / prices[i]
            own_type = 0
        if types[i] == 1 and own_type == 0 and own_amount != 0:# and oscs[i-1] - oscs[i] > 200:
            # if prices[i] * own_amount > money:
            #     money = prices[i] * own_amount
            #     own_amount = 0
            # elif prices[i] * own_amount < money * 0.9:
            #     money = prices[i] * own_amount
            #     own_amount = 0
            money = prices[i] * own_amount
            own_amount = 0
        if lstmtypes[i] == 2 and own_amount == 0:
            own_amount = money / prices[i]
            own_type = 1
        if lstmtypes[i] == 1 and own_type == 1 and own_amount != 0:
            money = prices[i] * own_amount
            own_amount = 0
        if volatypes[i] == 2 and own_amount == 0:
            own_amount = money / prices[i]
            own_type = 2
        if volatypes[i] == 1 and own_type == 2 and own_amount != 0:
            money = prices[i] * own_amount
            own_amount = 0

        profits.append(money/10000)
        g_prices.append(prices[i]/init_price*100)


    plt.figure(figsize=(20, 5))
    plt.plot(profits, label='profits (%)')
    plt.plot(g_prices, label='stock growth (%)')
    plt.title(name)
    plt.ylabel('(%)')
    plt.axhline(y=100, color='red', linestyle='--')
    plt.savefig('./combined_profit_graphs/'+name+'_profit_graph.png')
    plt.clf()