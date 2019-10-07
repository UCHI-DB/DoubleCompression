import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import seaborn as sns
import math

data = pd.read_csv(r'Composite.csv', low_memory=False)
FC = data.loc[data['Method'] == "Front Coding"]
FC = FC["Compression Ratio"].dropna()
NFC = data.loc[data['Method'] == "Not Front Coding"]
NFC = NFC["Compression Ratio"].dropna()

sns.distplot(FC, color="b", hist=False, label="Front Coding")
sns.distplot(NFC, color="r", hist=False, label="Not Front Coding")
plt.show()