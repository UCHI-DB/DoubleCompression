import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import seaborn as sns

g1 = pd.read_csv(r'../Reports/gzip1 Report.csv')
g2 = pd.read_csv(r'../Reports/gzip2 Report.csv')
g3 = pd.read_csv(r'../Reports/gzip3 Report.csv')
g4 = pd.read_csv(r'../Reports/gzip4 Report.csv')
g5 = pd.read_csv(r'../Reports/gzip5 Report.csv')
g6 = pd.read_csv(r'../Reports/gzip6 Report.csv')
g7 = pd.read_csv(r'../Reports/gzip7 Report.csv')
g8 = pd.read_csv(r'../Reports/gzip8 Report.csv')
g9 = pd.read_csv(r'../Reports/gzip9 Report.csv')

data = pd.concat([g1,g2,g3,g4,g5,g6,g7,g8,g9])

sns.boxplot(x="Method", y="Compression Ratio", showfliers=False, showmeans=True, data=data)
plt.show()
sns.boxplot(x="Method", y="Compression Throughput (mb/s)", data=data)
plt.show()