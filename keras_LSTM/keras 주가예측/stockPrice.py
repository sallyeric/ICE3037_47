from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split

from keras.models import Sequential
from keras.layers import Dense
from keras.callbacks import EarlyStopping, ModelCheckpoint
from keras.layers import LSTM

import seaborn as sns
import pandas as pd
import numpy as np
import os
import matplotlib.pyplot as plt

scaler = MinMaxScaler()

def toQuote(price):

    Q = [1, 5, 10, 50, 100, 500, 1000]

    if price < 1000:
        quote = Q[0]
    elif price < 5000:
        quote = Q[1]
    elif price < 10000:
        quote = Q[2]
    elif price < 50000:
        quote = Q[3]
    elif price < 100000:
        quote = Q[4]
    elif price < 500000:
        quote = Q[5]
    else:
        quote = Q[6]

    return (price // quote) * quote + (0 if (price - (price // quote) * quote) < 0.5 else quote)

def fees(price):
    fee = [0.004972959, 0.001672959, 0.001572959, 0.001472959, 0.001372959, 0.001272959, 0.000972959, 0.000772959]
    alpha = [0, 700, 900, 1000, 1200, 1500, 0, 0]

    if price < 200000:
        return price * fee[0] + alpha[0]
    elif price < 1000000:
        return price * fee[1] + alpha[1]
    elif price < 5000000:
        return price * fee[2] + alpha[2]
    elif price < 10000000:
        return price * fee[3] + alpha[3]
    elif price < 30000000:
        return price * fee[4] + alpha[4]
    elif price < 50000000:
        return price * fee[5] + alpha[5]
    elif price < 200000000:
        return price * fee[6] + alpha[6]
    else:
        return price * fee[7] + alpha[7]

def normalize(data):
    '''
    정규화함수
    :param data: pandas 로 읽어온 csv 파일의 내용
    :return: 0~1 사이의 값으로 정규화하여 반환
    '''
    scale_cols = ['시가', '고가', '저가', '종가', '거래량']
    closing_price = ['종가']
    df_scaled = scaler.fit_transform(data[scale_cols])
    scaler.fit_transform(data[closing_price])
    # unnormalize_factor = [data['시가'][len(data) - 1] / df_scaled[0][0],
    #                       data['고가'][len(data) - 1] / df_scaled[0][1],
    #                       data['저가'][len(data) - 1] / df_scaled[0][2],
    #                       data['종가'][len(data) - 1] / df_scaled[0][3],
    #                       data['거래량'][len(data) - 1] / df_scaled[0][4]
    #                       ]
    # print(max(data['종가']))
    # print(data)
    # print(df_scaled)
    # exit()
    df_scaled = pd.DataFrame(df_scaled)
    df_scaled.columns = scale_cols

    return df_scaled

def make_dataset(data, label, window_size=20):
    '''
    pandas 객체의 데이터를 numpy 객체로 변환하여 학습데이터를 형성
    :param data: pandas 객체
    :param label: pandas 객체
    :param window_size: 예측에 사용하는 데이터의 양
    :return: numpy 객체
    '''
    feature_list = []
    label_list = []
    for i in range(len(data) - window_size):
        feature_list.append(np.array(data.iloc[i:i+window_size]))
        label_list.append(np.array(label.iloc[i+window_size]))
    return np.array(feature_list), np.array(label_list)

# 데이터 경로
base_path = "D:/성균관대 강의/4학년 1학기/종합설계프로젝트/CODE/ICE3037_47/keras_LSTM/"

# ============학습 변수 =============
window_size = 40
num_epochs = 200
batch_size = 16
num_test = 75
# =================================

# data = pd.read_csv(os.path.join(base_path, '005930.csv'), encoding='utf8')[1236:2307][::-1]
data = pd.read_csv(os.path.join(base_path, '005930.csv'), encoding='utf8')[:1000][::-1]

data['일자'] = pd.to_datetime(data['일자'], format='%Y%m%d')
data['연도'] = data['일자'].dt.year
data['월'] = data['일자'].dt.month
data['일'] = data['일자'].dt.day
data['dif'] = 0

print(data)

test_date = data['일자'][-num_test + window_size + 1:]

# sim = pd.read_csv(os.path.join(base_path, 'samsung_news_similiarity.csv'), encoding='utf8')
#
# sim['date'] = pd.to_datetime(sim['date'], format='%Y-%m-%d')
# sim['연도'] = sim['date'].dt.year
# sim['월'] = sim['date'].dt.month
# sim['일'] = sim['date'].dt.day
#
# ssf = []
# j = 0
# for i, d in enumerate(data['일자']):
#     while sim['date'][j] != d:
#         if j >= len(sim):
#             break
#         j += 1
#     # print(sim['date'][j])
#     # print(d)
#     # print(sim['date'][j] != d)
#
#     data['dif'][i] = sim['diff'][j]
#
# print(data)
# exit()

# 마지막 2000일치의 데이터를 시간순서대로 정규화
data = normalize(data)

# train / test 데이터 분리
train = data[:-num_test]
test = data

feature_cols = ['시가', '고가', '저가', '종가', '거래량']
label_cols = ['종가']

# dataset 생성
train_feature, train_label = make_dataset(train[feature_cols], train[label_cols], window_size)
test_feature, test_label = make_dataset(test[feature_cols], test[label_cols], window_size)

# train, validation set 생성
x_train, x_valid, y_train, y_valid = train_test_split(train_feature, train_label, test_size=0.2)

model = Sequential()
filename = os.path.join(base_path, 'models/tmp_checkpoint.h5')

# 학습 모델 생성
model.add(LSTM(batch_size,
               input_shape=(train_feature.shape[1], train_feature.shape[2]),
               activation='relu',
               return_sequences=False)
          )
model.add(Dense(1))

if input("new learn? (y to yes) ") == "y":
    # 학습
    model.compile(loss='mean_squared_error', optimizer='adam')
    early_stop = EarlyStopping(monitor='val_loss', patience=5)
    checkpoint = ModelCheckpoint(filename, monitor='val_loss', verbose=1, save_best_only=True, mode='auto')

    history = model.fit(x_train, y_train,
                        epochs=num_epochs,
                        batch_size=batch_size,
                        validation_data=(x_valid, y_valid),
                        callbacks=[checkpoint])
                        # callbacks=[checkpoint])

else:
    # weight 로딩
    model.load_weights(filename)

# 일반 예측
pred = model.predict(test_feature)

# 예측한 값을 활용한 예측
# res = []
# for i in range(num_test - window_size):
#     pred2 = float(model.predict(test_feature)[0][0])
#     res.append(pred2)
#     test_feature = np.insert(test_feature, window_size, [pred2], axis=1)
#     test_feature = np.delete(test_feature, 0, axis=1)

real = scaler.inverse_transform(test_label)
pred = scaler.inverse_transform(pred)
evals = []

print(real)
print(pred)

# for price in pred:
#     price[0] = toQuote(price[0])

capital = 100000000
stock = 0

for i in range(len(test_label) - num_test, len(test_label) - 1):
    # today = pred[i][0]  # 예측한 오늘의 주가
    today = real[i][0]  # 실제 오늘의 주가
    tomorrow = pred[i + 1][0]

    if tomorrow > today:
        stock += capital // real[i][0]
        j = 0
        while capital < ((capital // real[i][0])-j) * real[i][0] + fees(((capital // real[i][0]) - j) * real[i][0]):
            j += 1
        capital -= ((capital // real[i][0])-j) * real[i][0] + fees(((capital // real[i][0]) - j) * real[i][0])

    elif tomorrow < today:
        capital += stock * real[i][0] - fees(stock * real[i][0]) - 0.0023 * stock * real[i][0]
        stock -= stock

    # print("\nDay"+str(i))
    # print("capital :", capital)
    # print("stock :", stock)
    # print("evaluation :", capital + stock * today)
    evals.append((capital + stock * real[i][0] - 100000000)/100000000)
    eval_ = capital + stock * real[i][0]
    print(eval_)

# print(evals)
print((scaler.inverse_transform(test_label)[num_test-window_size-1][0] - scaler.inverse_transform(test_label)[800][0])/scaler.inverse_transform(test_label)[800][0])
print((pred[num_test-window_size-1][0] - pred[800][0])/pred[800][0])

plt.figure(figsize=(12, 9))

# plt.subplot(211)
plt.plot(scaler.inverse_transform(test_label), label='Actual Data')
plt.axvline(x=len(test_label)-num_test, c='r', linestyle='--')
plt.plot(pred, label='predicted Data')
plt.xlabel('Date')
plt.ylabel('Price')
plt.title('Time-Series Prediction (Keras)')
plt.legend()
# ax = plt.gca()
# ax.axes.xaxis.set_visible(False)

# plt.subplot(212)
# sns.lineplot(y=evals, x=test_date)
# plt.xlabel('Date')
# plt.ylabel('Yield')

plt.show()


