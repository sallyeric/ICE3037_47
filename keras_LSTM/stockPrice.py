import self as self
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


class StockPrediction:
    def __init__(self):
        self.model = Sequential()
        self.data = None
        self.x_train, self.x_valid, self.y_train, self.y_valid = None, None, None, None
        self.train_feature, self.train_label, self.test_feature, self.test_label = None, None, None, None

        self.test_date = None
        self.pred, self.real, self.yields = None, None, None

        self.base_path = "C:/Users/YOO/PycharmProjects/종설프/"

        # learning parameter
        self.using_data_start = 5258
        self.using_data_end = 0
        self.window_size = 40
        self.num_epochs = 200
        self.batch_size = 64
        self.num_test = 10
        self.test_size = 100

    def set_data(self):
        self.data = pd.read_csv(os.path.join(self.base_path, '005930.csv'), encoding='utf8')[
                    self.using_data_end:self.using_data_start][::-1]

        self.data['일자'] = pd.to_datetime(self.data['일자'], format='%Y%m%d')
        self.data['연도'] = self.data['일자'].dt.year
        self.data['월'] = self.data['일자'].dt.month
        self.data['일'] = self.data['일자'].dt.day
        self.data['dif'] = 0

        self.test_date = self.data['일자'][-self.num_test * self.test_size:]

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
        self.data = self.normalize(self.data)

        # train / test 데이터 분리
        train = self.data[:-self.num_test * self.test_size]
        test = self.data

        feature_cols = ['시가', '고가', '저가', '종가', '거래량']
        label_cols = ['종가']

        # dataset 생성
        self.train_feature, self.train_label = self.make_dataset(train[feature_cols], train[label_cols],
                                                                 self.window_size)
        self.test_feature, self.test_label = self.make_dataset(test[feature_cols], test[label_cols], self.window_size)

        # train, validation set 생성
        self.x_train, self.x_valid, self.y_train, self.y_valid = train_test_split(self.train_feature, self.train_label,
                                                                                  test_size=0.2)

    def learn(self):
        if self.x_train is None:
            print("need to set data")
            return

        self.model = Sequential()
        self.model.add(LSTM(self.batch_size,
                       input_shape=(self.train_feature.shape[1], self.train_feature.shape[2]),
                       activation='relu',
                       return_sequences=False)
                  )

        self.model.add(Dense(1))

        self.model.compile(loss='mean_squared_error', optimizer='adam')
        early_stop = EarlyStopping(monitor='val_loss', patience=5)
        filename = os.path.join(self.base_path, 'models/tmp_checkpoint.h5')
        checkpoint = ModelCheckpoint(filename, monitor='val_loss', verbose=1, save_best_only=True, mode='auto')

        history = self.model.fit(self.x_train, self.y_train,
                                 epochs=self.num_epochs,
                                 batch_size=self.batch_size,
                                 validation_data=(self.x_valid, self.y_valid),
                                 callbacks=[checkpoint])

        self.model.load_weights(filename)

    def predict(self):
        if self.train_feature is None:
            print("need to set data")
            return
        # 학습 모델 생성
        self.model = Sequential()
        self.model.add(LSTM(self.batch_size,
                            input_shape=(self.train_feature.shape[1], self.train_feature.shape[2]),
                            activation='relu',
                            return_sequences=False)
                       )
        self.model.add(Dense(1))
        self.model.load_weights(os.path.join(self.base_path, 'models/tmp_checkpoint.h5'))

        self.pred = scaler.inverse_transform(self.model.predict(self.test_feature))
        self.real = scaler.inverse_transform(self.test_label)

    def calculate_yield(self, tax=False, useData="pred"):
        self.print_parameter()
        if self.pred is None:
            print("need to predict")
            return
        self.yields = []
        start = len(self.test_label) - self.num_test * self.test_size - 1

        if useData == "real":
            useData = self.real
        else:
            useData = self.pred

        for i in range(self.num_test):
            capital = 100000000
            stock = 0

            for j in range(self.test_size):
                d = start + i * self.test_size + j
                today = useData[d][0]
                tomorrow = self.pred[d + 1][0]

                if tomorrow > today:
                    stock += capital // self.real[d][0]
                    if tax:
                        j = 0
                        while capital < ((capital // self.real[d][0]) - j) * self.real[d][0] + self.fees(
                                ((capital // self.real[d][0]) - j) * self.real[d][0]):
                            j += 1
                        capital -= ((capital // self.real[d][0]) - j) * self.real[d][0] + self.fees(
                            ((capital // self.real[d][0]) - j) * self.real[d][0])
                    else:
                        capital -= (capital // self.real[d][0]) * self.real[d][0]

                elif tomorrow < today:
                    if tax:
                        capital += stock * self.real[d][0] - self.fees(stock * self.real[d][0]) - 0.0023 * stock * \
                                   self.real[d][0]
                    else:
                        capital += stock * self.real[d][0]
                    stock -= stock

                self.yields.append((capital + (stock * self.real[d][0]) - 100000000) / 100000000)

            print("TEST" + str(i + 1) + " 수익률 :", round(self.yields[len(self.yields) - 1]*100, 2))

    def graph(self):
        if self.yields is None:
            print("need to calculate yield")
            return
        plt.figure(figsize=(20, 10))
        plt.title('Time-Series Prediction (Keras)')

        plt.subplot(211)
        for e in range(self.num_test + 1):
            plt.axvline(x=e * 100, c='black', linewidth=1)
        plt.plot(self.real[-1000:], label='Actual Data')
        # plt.axvline(x=len(self.test_label) - self.num_test * self.test_size, c='r', linestyle='--')
        plt.plot(self.pred[-1000:], label='predicted Data')
        plt.ylabel('Price')
        plt.legend()
        ax = plt.gca()
        ax.axes.xaxis.set_visible(False)

        plt.subplot(212)
        plt.axhline(y=0, c='black', linewidth=1)
        # for e in range(self.num_test):
        #     plt.axvline(x=self.test_date[e * 100], c='black', linewidth=1)
        # sns.lineplot(y=self.yields, x=self.test_date)
        for e in range(self.num_test + 1):
            plt.axvline(x=e * 100, c='black', linewidth=1)
        plt.plot(self.yields, label='yield')
        plt.xlabel('Date')
        plt.ylabel('Yield')

        plt.show()

    def print_parameter(self):
        print("1. data start :", self.using_data_start)
        print("2. data end :", self.using_data_end)
        print("3. window size :", self.window_size)
        print("4. num epochs :", self.num_epochs)
        print("5. batch size :", self.batch_size)
        print("6. num of test :", self.num_test)
        print("7. test size :", self.test_size)

    def set_parameter(self, par, value):
        if par == "1":
            self.using_data_start = int(value)
        elif par == "2":
            self.using_data_end = int(value)
        elif par == "3":
            self.window_size = int(value)
        elif par == "4":
            self.num_epochs = int(value)
        elif par == "5":
            self.batch_size = int(value)
        elif par == "6":
            self.num_test = int(value)
        elif par == "7":
            self.test_size = int(value)
        self.print_parameter()

    def toQuote(self, price):

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

    def fees(self, price):
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

    def normalize(self, data):
        '''
        정규화함수
        :param data: pandas 로 읽어온 csv 파일의 내용
        :return: 0~1 사이의 값으로 정규화하여 반환
        '''
        scale_cols = ['시가', '고가', '저가', '종가', '거래량']
        closing_price = ['종가']
        df_scaled = scaler.fit_transform(data[scale_cols])
        scaler.fit_transform(data[closing_price])
        df_scaled = pd.DataFrame(df_scaled)
        df_scaled.columns = scale_cols

        return df_scaled

    def make_dataset(self, data, label, window_size=20):
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
            feature_list.append(np.array(data.iloc[i:i + window_size]))
            label_list.append(np.array(label.iloc[i + window_size]))
        return np.array(feature_list), np.array(label_list)


sp = StockPrediction()

while True:
    action = input("\nInput key to action"
                   "\n1 : parameter setting"
                   "\n2 : set data"
                   "\n3 : learn"
                   "\n4 : predict"
                   "\n5 : calculate yield"
                   "\n6 : drawing graph"
                   "\nexit : Exit\n")
    if action == "1":
        sp.print_parameter()
        sp.set_parameter(input(), input())
    elif action == "2":
        sp.set_data()
    elif action == "3":
        sp.learn()
    elif action == "4":
        sp.predict()
    elif action == "5":
        sp.calculate_yield()
    elif action == "6":
        sp.graph()
    elif action == "exit":
        exit()
