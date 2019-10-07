import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import seaborn as sns

data = pd.read_csv(r'Composite.csv')

g = sns.lmplot(x="Compression Ratio", y="Compression Throughput (mb/s)", 
	hue="Method", data=data, truncate=True, line_kws={"alpha":0})
plt.show()