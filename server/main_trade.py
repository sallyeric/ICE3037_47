from getMACD import getRealTimeMACD
from creonTrade import creonTrade
from FCM import FCM
import sys
from PyQt5.QtWidgets import *

qapp = QApplication(sys.argv)

creonTradeObj = creonTrade(FCM)
realTimeMACDObj = getRealTimeMACD(creonTradeObj)

qapp.exec_()