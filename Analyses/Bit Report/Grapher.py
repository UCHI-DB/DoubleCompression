import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import seaborn as sns


g1 = pd.read_csv(r'Raw BitReport.csv')
g2 = pd.read_csv(r'Gorilla BitReport.csv')
g3 = pd.read_csv(r'DFCM Pred Or Xor BitReport.csv')
g4 = pd.read_csv(r'FCM Correct Pred Or Xor BitReport.csv')
g5 = pd.read_csv(r'DFCM Correct Pred Or Xor BitReport.csv')

# g1 = pd.read_csv(r'DFCM1 Full BitReport.csv')
# g2 = pd.read_csv(r'DFCM2 Full BitReport.csv')
# g3 = pd.read_csv(r'DFCM3 Full BitReport.csv')
# g4 = pd.read_csv(r'FCM1 Full BitReport.csv')
# g5 = pd.read_csv(r'FCM2 Full BitReport.csv')
# g6 = pd.read_csv(r'FCM3 Full BitReport.csv')
# g7 = pd.read_csv(r'DFCM1 Gorilla BitReport.csv')
# g8 = pd.read_csv(r'FCM1 Gorilla BitReport.csv')
# g9 = pd.read_csv(r'Gorilla BitReport.csv')
# g10 = pd.read_csv(r'Raw BitReport.csv')
# g11 = pd.read_csv(r'Differences BitReport.csv')

data = pd.concat([g1,g2])

sns.lineplot(x="Position", y="Percentage", hue="Method", data=data)
plt.show()