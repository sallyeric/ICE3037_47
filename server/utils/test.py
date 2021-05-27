import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import tqdm

names = ['기아', 'SK하이닉스', '현대차', 'POSCO', '삼성전자', 'NAVER', '카카오', 'LG화학', '셀트리온']

for name in ['기아']:
    macd = pd.read_csv('./macdData/'+name+'MACD.csv')
    chart = pd.read_csv('./chartData/' + name + '.csv')
    t = macd.iloc[:, 0].values
    osc = macd.iloc[:, 3].values
    origin_prices = chart[::-1].iloc[:, 4].values

    types = [0]
    prices = [0]
    for i in range(1, len(t)):
        type = 0
        price = origin_prices[i]
        if osc[i] > 0 and osc[i-1] < 0: type = 2
        elif osc[i] < 0 and osc[i-1] > 0: type = 1
        types.append(type)
        prices.append(price)
    chartfile = './macdSignalData/'+name+'_MACD_SIGNAL.csv'
    chartData = {'일자': t,
                 '매수매도': types,
                 '가격': prices
                 }
    profit = 0
    save = []
    current = 0
    for i in range(1, len(t)):
        if current != 0:
            profit = current - chartData['가격']
        if chartData['매수매도'] == 2:
            current = chartData['가격']
        elif chartData['매수매도'] == 1:
            if current != 0:
                current = 0
                profit = current - chartData['가격']
        save.append(profit)
    print(save)
    print(chartData['가격'])
    plt.figure(figsize=(20, 5))
    plt.plot([i for i in range(0, len(origin_prices))], origin_prices, label='actual')
    plt.plot([i for i in range(0, len(chartData['가격']))], chartData['가격'], label='predict')
    plt.legend()
    plt.show()
    # df = pd.DataFrame(chartData, columns=['일자', '매수매도', '가격'])
    # df = df.set_index('일자')
    # df.to_csv(chartfile, encoding='utf-8-sig')