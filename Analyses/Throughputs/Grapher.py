import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import seaborn as sns


g2 = pd.read_csv(r'../Reports/Gorilla Report.csv')
g3 = pd.read_csv(r'../Reports/FCMGorilla Report.csv')
g4 = pd.read_csv(r'../Reports/DFCMGorilla Report.csv')
g5 = pd.read_csv(r'../Reports/Sprintz2 Report.csv')
g6 = pd.read_csv(r'../Reports/gzip9 Report.csv')

data = pd.concat([g2,g3,g4,g5,g6])

sns.boxplot(x="Method", y="Compression Throughput (mb/s)", showfliers=False, 
	showmeans=True, data=data)
plt.show()