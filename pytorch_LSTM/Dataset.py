import numpy as np
import torch.utils.data
import os
import pandas as pd
from sklearn.preprocessing import MinMaxScaler, StandardScaler

class ListDataset(torch.utils.data.Dataset):
    def __init__(self, chart_path, window_size):
        self.chart_files = [chart_path+p for p in os.listdir(chart_path)]
        self.charts = []
        self.targets = []

        mm = MinMaxScaler()
        #ss = StandardScaler()
        for f in self.chart_files:
            df = pd.read_csv(f)
            chart = mm.fit_transform(df.iloc[-1000:-200, 1:6]).astype(np.float32)
            for idx in range(0, len(chart)-window_size-1):
                self.charts.append(chart[idx:idx+window_size, :])
                self.targets.append(chart[idx+window_size, 3])

    def __getitem__(self, index):
        return self.charts[index], self.targets[index]

    def __len__(self):
        return len(self.charts)