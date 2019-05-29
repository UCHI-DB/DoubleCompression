import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import seaborn as sns

data = pd.read_csv(r'CompositeGraph.csv')

g = sns.catplot(x="Level", y="Percentage", 
	hue="Category", col="Type", 
	data=data, kind="box",
	height=4, aspect=.7,
	showfliers=False, showmeans=True)
plt.show()