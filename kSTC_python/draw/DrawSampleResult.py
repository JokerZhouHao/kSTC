import matplotlib.pyplot as plt
import random
from matplotlib import interactive
from utility import PathUtility
from Data.StatisticData import Data

BasePathDbscan = 'D:\\kSTC\\sample_result\\res\\000_dataset\\yelp_buss[-125.0,28.0],[15.0,60.0]\\dbscan\\'

class Bar:
    hatchxes = ['x', '.', '+', 'O']
    hatchIndex = 0

    def __init__(self, numBar, title=None, widthBar=0.14, spanBars=0.03,
                 xlabel=None, xTxts=None, xTxtY = None, xRotateAngle=0,
                 ylabel=None, yscale='linear', ys=None, ylim=None,
                 fName=None):
        self.title = title

        self.numBar = numBar
        self.widthBar = widthBar
        self.spanBars = spanBars    # 两个相邻bar之间相隔的宽度
        self.baseStartXOfBar = 1 - (widthBar * numBar + spanBars * (numBar - 1)) / 2;
        self.spanNextBar = widthBar + spanBars  # 两个相邻bar的左边x坐标之差

        self.xlabel = xlabel
        self.xTxts = xTxts
        self.xTxtY = xTxtY  # 设置x轴的txt所在y
        self.xRotateAngle = xRotateAngle

        self.ylabel = ylabel
        self.yscale = yscale
        self.ys = ys
        self.ylim = ylim

        self.fpath = PathUtility.figure_path() + fName

        # figure设置
        # self.fig = plt.figure(random.randint(1, 10000), figsize=(10.1023, 6.5), tight_layout=True)
        self.fig = plt.figure(random.randint(1, 10000), figsize=(10, 8), tight_layout=True)
        if title==None: self.fig.canvas.set_window_title('Test')
        else:   self.fig.canvas.set_window_title(title)
        # 设置全局字体大小
        plt.rcParams['font.size'] = 25
        self.ax = self.fig.add_subplot(111)

    def show(self):
        # legend设置
        self.ax.legend(loc=1, frameon=False, prop={'size': 18}, markerscale = 1)

        # 设置x、y的刻度
        self.ax.tick_params(axis='x', direction='in', width=3, length=8, which='major')
        self.ax.tick_params(axis='y', direction='out', width=3, length=8, which='major')
        self.ax.tick_params(direction='out', width=2, length=4, which='minor')

        # 设置x轴
        self.ax.set_xlabel(self.xlabel)
        numX = len(self.xTxts) + 2
        xs = []
        for x in range(numX):
            xs.append(x)
        self.ax.set_xticks(xs)
        self.ax.set_xlim(xs[0], xs[-1])
        # 设置刻度对应txt
        xaxis = self.ax.get_xaxis()
        x_labels = xaxis.get_ticklabels()
        for i in range(len(x_labels)):
            if i==0 or i==len(x_labels)-1:
                x_labels[i].set_visible(False)
            else:
                pos = x_labels[i].get_position()
                if self.xTxtY != None:
                    pos = (pos[0], self.xTxtY)
                x_labels[i].set_position(pos)
                x_labels[i].set_verticalalignment('top')
                x_labels[i].set_text(self.xTxts[i-1])
                x_labels[i].set_rotation(self.xRotateAngle)
        xaxis.set_ticklabels(x_labels)
        # 隐藏下方x轴的刻度线
        x_lines = xaxis.get_ticklines()
        for ln in x_lines:
            ln.set_visible(False)

        # 设置y轴
        self.ax.set_ylabel(self.ylabel)
        if self.yscale == 'linear':
            if self.ys != None:
                self.ax.set_ylim(self.ys[0], self.ys[-1])
                self.ax.set_yticks(self.ys)
        else:
            if self.ylim != None:
                self.ax.set_ylim(self.ylim)
        self.ax.set_yscale(self.yscale)

        interactive(True)
        plt.show()

        # 保存图像
        self.fig.savefig(self.fpath)

    def hatch(self):
        htch = Bar.hatchxes[Bar.hatchIndex]
        Bar.hatchIndex += 1
        if Bar.hatchIndex == len(Bar.hatchxes):
            Bar.hatchIndex = 0
        return htch

    # 画柱状图
    def drawBar(self, label, startIndex, hs, ys=None, faceColor='#CCCCCC', hatch=None, linestyle='solid', edgecolor='black'):
        if hatch == None:
            hatch = self.hatch()

        startx = self.baseStartXOfBar + startIndex * self.spanNextBar
        if ys == None:
            ys = [0 for i in range(len(hs))]
        for i in range(len(hs)):
            bxs = []
            bxs.append(startx + i)
            bxs.append(startx + i + self.widthBar)
            bxs.append(startx + i + self.widthBar)
            bxs.append(startx + i)
            bys = []
            bys.append(ys[i])
            bys.append(ys[i])
            bys.append(ys[i] + hs[i])
            bys.append(ys[i] + hs[i])
            if i==0:
                self.ax.fill(bxs, bys, hatch=hatch, fc=faceColor, ls=linestyle, ec=edgecolor, lw=1, label=label)
            else:
                self.ax.fill(bxs, bys, hatch=hatch, fc=faceColor, ls=linestyle, ec=edgecolor, lw=1)

    # dbscan --- minpts
    @staticmethod
    def dMinpts(fName='test.pdf'):
        rFanout=50
        alpha=0.5
        steepD=0.1
        h=15
        om=1
        oe='1.0E-4'
        ns=50

        t=1
        ts = [1, 2, 3, 4]

        k=5000
        nw=2

        mpts=5
        mptss = [10, 20, 50, 100, 200]

        eps='0.001'
        xi='0.001'
        maxPNeiByte=2147483631

        # 设置bar参数
        barlabels = ['Basic', 'Adv1', 'Adv2', 'Adv3']
        xlabel = 'minpts'
        xTxts = ['10', '20', '50', '100', '200']
        ylabel = 'milliseconds'

        # 创建bar对象
        bar = Bar(4,
                  xlabel=xlabel, xTxts=xTxts,
                  ylabel=ylabel, yscale='log', ylim=[1000, 100000],
                  fName = fName)

        dir = BasePathDbscan + 'minpts\\'

        for tIndex in range(len(ts)):
            timeTotals = []
            for mptIndex in range(len(mptss)):
                data = Data.getData(dir, rFanout, alpha, steepD, h, om, oe, ns, ts[tIndex], k, nw, mptss[mptIndex], eps, xi, maxPNeiByte)
                print(data)
                timeTotals.append(data.timeTotal)
            bar.drawBar(barlabels[tIndex], tIndex, timeTotals)

        bar.show()



######################    dbscan   #########################
#######    dbscan_runtime_minpts  ########
Bar.dMinpts('dbscan_runtime_minpts.pdf')











































plt.pause(3600)
