import pandas as pd
import matplotlib.pyplot as plt
from matplotlib import font_manager, rc
font_path = "C:/Windows/Fonts/gulim.ttc"
font = font_manager.FontProperties(fname=font_path).get_name()
rc('font', family=font)

names = ['기아', 'SK하이닉스', '현대차', 'POSCO', '삼성전자', 'NAVER', '카카오', 'LG화학', '셀트리온']
for name in names:
    macd = pd.read_csv('./macdSignalData/'+name+'_MACD_SIGNAL.csv')
    t = macd.iloc[:, 0].values
    types = macd.iloc[:, 1].values
    prices = macd.iloc[:, 2].values
    init_price = prices[0]

    profits = []
    g_prices = []
    money = 1000000.0
    own_amount = 0
    for i in range(0, len(t)):
        if types[i] == 2 and own_amount == 0:
            own_amount = money / prices[i]
        if types[i] == 1 and own_amount != 0:
            if prices[i] * own_amount > money:
                money = prices[i] * own_amount
                own_amount = 0
            elif prices[i] * own_amount < money * 0.9:
                money = prices[i] * own_amount
                own_amount = 0
            # money = prices[i] * own_amount
            # own_amount = 0
        profits.append(money/10000)
        g_prices.append(prices[i]/init_price*100)


    plt.figure(figsize=(20, 5))
    plt.plot(profits, label='profits (%)')
    plt.plot(g_prices, label='stock growth (%)')
    plt.title(name)
    plt.ylabel('(%)')
    plt.axhline(y=100, color='red', linestyle='--')
    plt.savefig('./조건추가_profit_graphs/'+name+'_profit_graph.png')
    plt.clf()