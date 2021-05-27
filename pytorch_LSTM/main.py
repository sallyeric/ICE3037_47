import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import tqdm

import torch
import torch.optim as optim
from torch.autograd import Variable
from torch.utils.data import DataLoader
from sklearn.preprocessing import StandardScaler, MinMaxScaler

from LSTM_model import LSTM
from Dataset import ListDataset

window_size = 100
dataSet = ListDataset('./chart/', window_size)
dataLoader = DataLoader(dataSet, batch_size=64, shuffle=True, num_workers=0, pin_memory=True, collate_fn=None)

num_epochs = 100
learning_rate = 0.001
input_size = 5
hidden_size = 128
num_layers = 3

device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")  # device
model = LSTM(input_size, hidden_size, num_layers).to(device)
loss_function = torch.nn.MSELoss()    # mean-squared error for regression
optimizer = optim.Adam(model.parameters(), lr=learning_rate)  # adam optimizer
scheduler = optim.lr_scheduler.StepLR(optimizer=optimizer, step_size=30, gamma=0.9)
# losses = []
# minLoss = 1
# model.train()
# for epoch in tqdm.tqdm(range(num_epochs), desc='Epoch'):
#     for chart, target in dataLoader:
#         output = model.forward(Variable(chart).to(device))
#
#         optimizer.zero_grad()
#         loss = loss_function(output, target.to(device))
#         loss.backward()
#         optimizer.step()
#         #scheduler.step()
#     losses.append(loss.item())
#     print(f'Epoch : {epoch}, loss : {loss.item()}')
#     if(loss < minLoss):
#         minLoss = loss
#         torch.save({'epoch': epoch,
#                     'model_state_dict': model.state_dict(),
#                     'optimizer_state_dict': optimizer.state_dict()},
#                     './best_saved_model.pt')

checkpoint = torch.load('./best_saved_model.pt')
model.load_state_dict(checkpoint['model_state_dict'])
model.eval()
testDf = pd.read_csv('./chart/기아.csv')
mm = MinMaxScaler()
test_x = testDf.iloc[-200:, 1:6]
mm.fit(testDf.iloc[-200:, 4:5])
test_x = MinMaxScaler().fit_transform(test_x)
test_x = Variable(torch.tensor(test_x).to(device))
test_x = test_x.reshape(1, test_x.shape[0], test_x.shape[1]).float()
predict_y = [testDf.iloc[-100:, 4].tolist()[0] for i in range(window_size)]
predict = [model(test_x[:, idx:idx+window_size, :]) for idx in range(test_x.shape[1]- window_size - 1)]
predict = mm.inverse_transform(np.array(predict).reshape(-1, 1))
predict_y.extend(predict[:, 0])
predict_y = predict_y[-100:]
print(len(predict_y))
x = [p-x for x, p in zip(testDf.iloc[-100:, 4].tolist(), predict_y)]
plt.figure(figsize=(20,5))
plt.plot([i for i in range(0, len(testDf.iloc[-100:, 4]))], testDf.iloc[-100:, 4], label='actual')
plt.plot([i for i in range(0, len(predict_y))], predict_y, label='predict')
plt.plot([i for i in range(0, len(x))], x, label='diff')
print(np.sum(np.power(x,2))/len(x))
print(x)
plt.legend()
plt.show()