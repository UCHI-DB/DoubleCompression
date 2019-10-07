import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import seaborn as sns

data = pd.read_csv(r'Composite.csv')

g = sns.boxplot(x="Level", y="Decompression Throughput (mb/s)", 
	hue="Method",
	data=data,
	showfliers=False)
plt.show()