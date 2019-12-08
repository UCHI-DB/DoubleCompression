from scipy.stats import pearsonr
import matplotlib.pyplot as plt
import seaborn as sns; sns.set(style='white')
import pandas as pd

# def corrfunc(x,y, ax=None, **kws):
#     """Plot the correlation coefficient in the top left hand corner of a plot."""
#     r, _ = pearsonr(x, y)
#     ax = ax or plt.gca()
#     # Unicode for lowercase rho (ρ)
#     rho = '\u03C1'
#     ax.annotate(f'{rho} = {r:.2f}', xy=(.7, .9), xycoords=ax.transAxes)

# df = pd.read_csv("SubsetsCleaned.csv")
# df = df[df["Smoothness"] != "Infinity"]
# df.Smoothness = df.Smoothness.astype(float);
# df = df[df["Integer Percentage"] < 0.5]
# df = df[:1326]
# print(df);
# g = sns.pairplot(df,kind="reg",x_vars=["Gorilla CR","Sprintz CR","ToBinary CR","gzip CR"],y_vars=["Average Run Length"])
# g.set(xlim=(0,1))
# g.map(corrfunc)
# plt.show()

df = pd.read_csv("Subsets.csv")
df = df[df["Dataset"] != "argonne"].dropna()
print(df)
# df["DFCMGorilla CR"] = (df["ToBinary CR"] - df["DFCMGorilla CR"])/df["ToBinary CR"]
# df["DFCMSprintz CR"] = (df["ToBinary CR"] - df["DFCMSprintz CR"])/df["ToBinary CR"]
df["Gorilla CR"] = (df["ToBinary CR"] - df["Gorilla CR"])/df["ToBinary CR"]
df["Sprintz CR"] = (df["ToBinary CR"] - df["Sprintz CR"])/df["ToBinary CR"]
df["gzip CR"] = (df["ToBinary CR"] - df["gzip CR"])/df["ToBinary CR"]
df["Gorilla CR"] = df["Gorilla CR"] / df["gzip CR"]
# df["DFCMGorilla CR"] = df["DFCMGorilla CR"] / df["gzip CR"]
# df["DFCMSprintz CR"] = df["DFCMSprintz CR"] / df["gzip CR"]
# df["Sprintz CR"] = df["Sprintz CR"] / df["gzip CR"]
# sns.distplot(df["DFCMGorilla CR"], label="DFCMGorilla", hist=False)
# sns.distplot(df["DFCMSprintz CR"], label="DFCMSprintz", hist=False)
sns.distplot(df["Gorilla CR"], label="Gorilla", hist=False)
sns.distplot(df["Sprintz CR"], label="Sprintz", hist=False)
# sns.distplot(df["gzip CR"], label="gzip9", hist=False)
# print(df["Gorilla CR"].mean())
print(df["Sprintz CR"].mean())
# plt.xlim(-0.2,1.4)
# plt.legend()
# plt.title("Integer-Like")
# plt.xlabel("")
# plt.show()

# df = pd.read_csv("CRBarplot.csv")
# # print(sns.color_palette())
# b = (0.2980392156862745, 0.4470588235294118, 0.6901960784313725)
# o = (0.8666666666666667, 0.5176470588235295, 0.3215686274509804)
# g = (0.3333333333333333, 0.6588235294117647, 0.40784313725490196)
# r = (0.7686274509803922, 0.3058823529411765, 0.3215686274509804)
# colors=[r, b, o, g, (0.5058823529411764, 0.4470588235294118, 0.7019607843137254), (0.5764705882352941, 0.47058823529411764, 0.3764705882352941), (0.8549019607843137, 0.5450980392156862, 0.7647058823529411), (0.5490196078431373, 0.5490196078431373, 0.5490196078431373), (0.8, 0.7254901960784313, 0.4549019607843137), (0.39215686274509803, 0.7098039215686275, 0.803921568627451)]
# sns.barplot(x="Dataset",hue="Method",y="CR",palette=colors,data=df)
# plt.show()
