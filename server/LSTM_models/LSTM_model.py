import torch.nn as nn

class LSTM(nn.Module):
    def __init__(self, input_size, hidden_size, num_layers):
        super(LSTM, self).__init__()
        self.num_layers = num_layers
        self.input_size = input_size
        self.hidden_size = hidden_size
        self.lstm = nn.LSTM(input_size=input_size, hidden_size=hidden_size,
                            num_layers=num_layers, batch_first=True,
                            dropout=0.3)
        self.fc = nn.Linear(hidden_size, 256)
        self.fc2 = nn.Linear(256, 1)
        self.relu = nn.ReLU()
        self.sigmoid = nn.Sigmoid()

    def forward(self, x):
        output, (hn, cn) = self.lstm(x)
        # out = self.relu(hn[-1])
        # out = self.fc(out)
        # out = self.relu(out)
        # out = self.fc2(out)
        # out = out[:, 0]

        out = self.relu(output)
        out = self.fc(out)
        out = self.relu(out)
        out = self.fc2(out)
        out = out[:, :, 0]

        return out