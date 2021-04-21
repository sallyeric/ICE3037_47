from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split

from keras.models import Sequential
from keras.layers import Dense
from keras.callbacks import EarlyStopping, ModelCheckpoint
from keras.layers import LSTM

import pandas as pd
import numpy as np
import os
import matplotlib.pyplot as plt

def normalize(data):
    '''
    정규화함수
    :param data: pandas 로 읽어온 csv 파일의 내용
    :return: 0~1 사이의 값으로 정규화하여 반환
    '''
    scaler = MinMaxScaler()
    scale_cols = ['시가', '고가', '저가', '종가', '거래량']
    df_scaled = scaler.fit_transform(data[scale_cols])

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
base_path = "C:/Users/YOO/PycharmProjects/종설프/"

# ============학습 변수 =============
window_size = 20
num_epochs = 200
batch_size = 16
num_test = 200
# ==================================

# 마지막 2000일치의 데이터를 시간순서대로 정규화
data = normalize(pd.read_csv(os.path.join(base_path, '005930.csv'), encoding='utf8')[:2000][::-1])

# train / test 데이터 분리
train = data[:-num_test]
test = data[-num_test:]
# test2는 첫 예측에 필요한 데이터만을 가짐
test2 = data[-num_test:-num_test+window_size+1]

# feature_cols = ['시가', '고가', '저가', '종가', '거래량']
feature_cols = ['종가']
label_cols = ['종가']

# dataset 생성
train_feature, train_label = make_dataset(train[feature_cols], train[label_cols], window_size)
t1, test_label = make_dataset(test[feature_cols], test[label_cols], window_size)
test_feature, t2 = make_dataset(test2[feature_cols], test2[label_cols], window_size)

# train, validation set 생성
x_train, x_valid, y_train, y_valid = train_test_split(train_feature, train_label, test_size=0.2)

# 학습 모델 생성
model = Sequential()
model.add(LSTM(batch_size,
               input_shape=(train_feature.shape[1], train_feature.shape[2]),
               activation='relu',
               return_sequences=False)
          )

model.add(Dense(1))

# 학습
model.compile(loss='mean_squared_error', optimizer='adam')
early_stop = EarlyStopping(monitor='val_loss', patience=5)
filename = os.path.join(base_path, 'models/tmp_checkpoint.h5')
checkpoint = ModelCheckpoint(filename, monitor='val_loss', verbose=1, save_best_only=True, mode='auto')

history = model.fit(x_train, y_train,
                    epochs=num_epochs,
                    batch_size=batch_size,
                    validation_data=(x_valid, y_valid),
                    callbacks=[early_stop, checkpoint])

# weight 로딩
model.load_weights(filename)

# 일반 예측
pred = model.predict(t1)

# 예측한 값을 활용한 예측
res = []
for i in range(num_epochs - window_size):
    pred2 = float(model.predict(test_feature)[0][0])
    res.append(pred2)
    test_feature = np.insert(test_feature, window_size, [pred2], axis=1)
    test_feature = np.delete(test_feature, 0, axis=1)


plt.figure(figsize=(12, 9))
plt.plot(test_label, label='actual')
# 일반 예측 결과
plt.plot(pred, label='prediction')
# 예측한 값을 활용한 예측 결과
plt.plot(res, label='prediction2')
plt.legend()
plt.show()