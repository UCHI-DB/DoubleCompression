import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import seaborn as sns

g1 = pd.read_csv(r'avgNBits1.csv')
g2 = pd.read_csv(r'avgNBits2.csv')
g3 = pd.read_csv(r'avgNBits3.csv')
g4 = pd.read_csv(r'avgNBits4.csv')
g5 = pd.read_csv(r'avgNBits5.csv')
g6 = pd.read_csv(r'avgNBits6.csv')
g7 = pd.read_csv(r'avgNBits7.csv')
g8 = pd.read_csv(r'avgNBits8.csv')

data = pd.concat([g1,g2,g3,g4,g5,g6,g7,g8])

sns.boxplot(x="Block Size", y="Average nBits", showfliers=False, showmeans=True, data=data)
plt.show()