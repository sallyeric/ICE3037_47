import pandas as pd

names = ['기아', 'SK하이닉스', '현대차', 'POSCO', '삼성전자', 'NAVER', '카카오', 'LG화학', '셀트리온']

for name in names:
    macd = pd.read_csv('./macdData/'+name+'MACD.csv')
    chart = pd.read_csv('./chartData/' + name + '.csv')
    t = macd.iloc[:, 0].values
    osc = macd.iloc[:, 3].values
    origin_prices = chart.iloc[:, 4].values

    types = [0]
    prices = [origin_prices[0]]
    for i in range(1, len(t)):
        type = 0
        if osc[i] > 0 and osc[i-1] < 0: type = 2
        elif osc[i] < 0 and osc[i-1] > 0: type = 1
        types.append(type)
        prices.append(origin_prices[i])

    chartfile = './macdSignalData/'+name+'_MACD_SIGNAL.csv'
    chartData = {'일자': t,
                 '매수매도': types,
                 '가격': prices
                 }

    df = pd.DataFrame(chartData, columns=['일자', '매수매도', '가격'])
    df = df.set_index('일자')
    df.to_csv(chartfile, encoding='utf-8-sig')