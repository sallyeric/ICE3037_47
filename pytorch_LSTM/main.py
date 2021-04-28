import numpy as np
import pandas as pd
import pandas_datareader.data as pdr
import matplotlib.pyplot as plt

import datetime

import torch
import torch.nn as nn
from torch.autograd import Variable

import torch.optim as optim
from torch.utils.data import Dataset, DataLoader

from sklearn.preprocessing import StandardScaler, MinMaxScaler

from LSTM_model import LSTM

start = (2012, 1, 1)  # 2020년 01년 01월
start = datetime.datetime(*start)
#end = datetime.date.today()  # 현재
end = (2016, 4, 10)
end = datetime.datetime(*end)
# yahoo 에서 삼성 전자 불러오기
df = pdr.DataReader('005930.KS', 'yahoo', start, end)

X = df
X = df.rolling(window=5).mean()
y = df.iloc[:, 5:6].rolling(window=5).mean()

X = X[5:]
y = y[5:]

mm = MinMaxScaler()
ss = StandardScaler()

X_ss = ss.fit_transform(X)
y_mm = mm.fit_transform(y)

train_num = 800
# Train Data
X_train = X_ss[:train_num, :]
X_test = X_ss[train_num:-1, :]

# Test Data
"""
( 굳이 없어도 된다. 하지만 얼마나 예측데이터와 실제 데이터의 정확도를 확인하기 위해 
from sklearn.metrics import accuracy_score 를 통해 정확한 값으로 확인할 수 있다. )
"""
y_train = y_mm[1:train_num+1, :]
y_test = y_mm[train_num+1:, :]


"""
torch Variable에는 3개의 형태가 있다. 
data, grad, grad_fn 한 번 구글에 찾아서 공부해보길 바랍니다. 
"""
X_train_tensors = Variable(torch.Tensor(X_train))
X_test_tensors = Variable(torch.Tensor(X_test))

y_train_tensors = Variable(torch.Tensor(y_train))
y_test_tensors = Variable(torch.Tensor(y_test))

X_train_tensors_final = torch.reshape(X_train_tensors,   (X_train_tensors.shape[0], 1, X_train_tensors.shape[1]))
X_test_tensors_final = torch.reshape(X_test_tensors,  (X_test_tensors.shape[0], 1, X_test_tensors.shape[1]))

print(X_train_tensors_final.shape)
device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")  # device

num_epochs = 10000 #1000 epochs
learning_rate = 0.0001 #0.001 lr

input_size = 6 #number of features
hidden_size = 256 #number of features in hidden state
num_layers = 3 #number of stacked lstm layers

num_classes = 1 #number of output classes
lstm1 = LSTM(num_classes, input_size, hidden_size, num_layers, X_train_tensors_final.shape[1]).to(device)

loss_function = torch.nn.MSELoss()    # mean-squared error for regression
optimizer = torch.optim.Adam(lstm1.parameters(), lr=learning_rate)  # adam optimizer
scheduler = torch.optim.lr_scheduler.StepLR(optimizer=optimizer, step_size=1000, gamma=0.9)
max_profit = 0
max_epoch = 0
for epoch in range(num_epochs):
    lstm1.train()
    outputs = lstm1.forward(X_train_tensors_final.to(device))  # forward pass
    optimizer.zero_grad()  # caluclate the gradient, manually setting to 0

    # obtain the loss function
    loss = loss_function(outputs[:train_num], y_train_tensors.to(device))

    loss.backward()  # calculates the loss of the loss function

    optimizer.step()  # improve from loss, i.e backprop
    scheduler.step()
    if epoch % 100 == 0:
        print("Epoch: %d, loss: %1.5f" % (epoch, loss.item()))

    lstm1.eval()
    df_X_ss = ss.transform(df)

    df_y_mm = mm.transform(df.iloc[1:, 5:6])

    df_X_ss = Variable(torch.Tensor(df_X_ss))  # converting to Tensors
    df_y_mm = Variable(torch.Tensor(df_y_mm))

    # reshaping the dataset
    df_X_ss = torch.reshape(df_X_ss, (df_X_ss.shape[0], 1, df_X_ss.shape[1]))
    train_predict = lstm1(df_X_ss.to(device))  # forward pass
    data_predict = train_predict.data.detach().cpu().numpy()  # numpy conversion
    dataY_plot = df_y_mm.data.numpy()
    data_predict = mm.inverse_transform(
    data_predict[:int(data_predict.shape[0] / num_layers)])  # reverse transformation
    dataY_plot = mm.inverse_transform(dataY_plot)

    money1 = 10000000
    save1 = 0
    list1 = []
    for i in range(800, 1050):
        today = dataY_plot[i]
        today_p = data_predict[i]
        tomorrow = data_predict[i + 1]

        if (today_p < tomorrow and save1 == 0):
            save1 = today
        elif (save1 > 0):
            money1 += (today - save1) / save1 * money1
            save1 = 0
        list1.append((money1 - 10000000)/100000)
    list1 = np.array(list1).reshape(-1, 1)
    profit = (money1 - 10000000) / 100000
    if max_profit < profit:
        plt.plot(list1)
        plt.savefig(
            f'./profit/profit{profit}_epoch{epoch}_numLayers{num_layers}_lr{learning_rate}_hiddenSize{hidden_size}.png')
        plt.clf()
        plt.plot(dataY_plot[800:], label='actual data')
        plt.plot(data_predict[800:], label='predict data')
        plt.savefig(
            f'./graph/graph_profit{profit}_epoch{epoch}_numLayers{num_layers}_lr{learning_rate}_hiddenSize{hidden_size}.png')
        plt.clf()
        max_profit = profit
        max_epoch = epoch
        print(f'epoch : {epoch}, profit : {profit}')

df_X_ss = ss.transform(df)

df_y_mm = mm.transform(df.iloc[1:, 5:6])

df_X_ss = Variable(torch.Tensor(df_X_ss)) #converting to Tensors
df_y_mm = Variable(torch.Tensor(df_y_mm))

#reshaping the dataset
df_X_ss = torch.reshape(df_X_ss, (df_X_ss.shape[0], 1, df_X_ss.shape[1]))
train_predict = lstm1(df_X_ss.to(device))#forward pass
data_predict = train_predict.data.detach().cpu().numpy() #numpy conversion
dataY_plot = df_y_mm.data.numpy()
data_predict = mm.inverse_transform(data_predict[:int(data_predict.shape[0]/num_layers)]) #reverse transformation
dataY_plot = mm.inverse_transform(dataY_plot)
plt.figure(figsize=(10,6)) #plotting
plt.axvline(x=train_num, c='r', linestyle='--') #size of the training set

#data_predict = data_predict-dataY_plot
#plt.plot(dataY_plot, label='Actuall Data') #actual plot
#plt.plot(data_predict, label='Predicted Data') #predicted plot
#plt.plot(data_predict, label='different') #predicted plot
#plt.title('Time-Series Prediction')
#plt.legend()
#plt.show()

money1 = 10000000
save1 = 0

list1 = []
for i in range(800, 1050):
    today = dataY_plot[i]
    today_p = data_predict[i]
    tomorrow = data_predict[i+1]

    if(today_p < tomorrow and save1 == 0):
        save1 = today
    elif(save1 > 0):
        money1 += (today - save1)/save1 * money1
        save1 = 0
    list1.append(money1-10000000)

print((money1 - 10000000)/100000)
list1 = np.array(list1).reshape(-1, 1)
list1 = list1/100000
print(list1.shape, len(list1))
plt.figure(figsize=(10,6)) #plotting
plt.plot(list1, label='profit') #actual plot
plt.ylabel('profit (%)')
plt.title('Time-Series Profit')
plt.show()