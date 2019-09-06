import matplotlib.pyplot as plt
import random
from matplotlib import interactive
from utility import PathUtility
from Data.StatisticData import Data

BasePathDbscan = 'D:\\kSTC\\sample_result\\res\\000_dataset\\yelp_buss[-125.0,28.0],[15.0,60.0]\\dbscan\\'
BasePathOptic = 'D:\\kSTC\\sample_result\\res\\000_dataset\\yelp_buss[-125.0,28.0],[15.0,60.0]\\optic\\'

class Bar:
    def __init__(self, numBar, title=None, widthBar=0.14, spanBars=0.03,
                 xlabel=None, xTxts=None, xTxtY = None, xRotateAngle=0,
                 ylabel=None, yscale='linear', ys=None, ylim=None,
                 loc=1,
                 fName=None):
        self.hatchxes = ['x', '.', '+', 'O']
        self.hatchIndex = 0

        if title==None: self.title = fName

        self.numBar = numBar
        self.widthBar = widthBar
        self.spanBars = spanBars    # 两个相邻bar之间相隔的宽度
        self.spanXTick = 5 / (len(xTxts) + 1)
        self.xs = [self.spanXTick * i for i in range(len(xTxts) + 2)]
        self.baseStartXOfBar = self.xs[1] - (widthBar * numBar + spanBars * (numBar - 1)) / 2;
        self.spanNextBar = widthBar + spanBars  # 两个相邻bar的左边x坐标之差

        self.xlabel = xlabel
        self.xTxts = xTxts
        self.xTxtY = xTxtY  # 设置x轴的txt所在y
        self.xRotateAngle = xRotateAngle

        self.ylabel = ylabel
        self.yscale = yscale
        self.ys = ys
        self.ylim = ylim

        self.loc = loc

        self.fpath = PathUtility.figure_path() + fName

        # figure设置
        # self.fig = plt.figure(random.randint(1, 10000), figsize=(10.1023, 6.5), tight_layout=True)
        self.fig = plt.figure(random.randint(1, 10000), figsize=(10, 8), tight_layout=True)
        # self.fig = plt.figure(random.randint(1, 10000), figsize=(8, 6), tight_layout=True)
        self.fig.canvas.set_window_title(self.title)
        # 设置全局字体大小
        plt.rcParams['font.size'] = 25
        self.ax = self.fig.add_subplot(111)

    def show(self):
        # legend设置
        self.ax.legend(loc=self.loc, frameon=False, prop={'size': 18}, markerscale = 1)

        # 设置x、y的刻度
        self.ax.tick_params(axis='x', direction='in', width=3, length=8, which='major')
        self.ax.tick_params(axis='y', direction='out', width=3, length=8, which='major')
        self.ax.tick_params(direction='out', width=2, length=4, which='minor')

        # 设置x轴
        self.ax.set_xlabel(self.xlabel)
        # numX = len(self.xTxts) + 2
        # xs = []
        # for x in range(numX):
        #     xs.append(x)
        self.ax.set_xticks(self.xs)
        self.ax.set_xlim(self.xs[0], self.xs[-1])
        # 设置刻度对应txt
        xaxis = self.ax.get_xaxis()
        x_labels = xaxis.get_ticklabels()
        for i in range(len(x_labels)):
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
        self.ax.set_yscale(self.yscale)
        if self.yscale == 'linear':
            if self.ys != None:
                self.ax.set_yticks(self.ys)
                self.ax.set_ylim(self.ys[0], self.ys[-1])
        else:
            if self.ylim != None:
                self.ax.set_ylim(self.ylim)

        interactive(True)
        plt.show()

        # 保存图像
        self.fig.savefig(self.fpath)

    def hatch(self):
        htch = self.hatchxes[self.hatchIndex]
        self.hatchIndex += 1
        if self.hatchIndex == len(self.hatchxes):
            self.hatchIndex = 0
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
            bxs.append(startx + i * self.spanXTick)
            bxs.append(startx + i * self.spanXTick + self.widthBar)
            bxs.append(startx + i * self.spanXTick + self.widthBar)
            bxs.append(startx + i * self.spanXTick)
            bys = []
            bys.append(ys[i])
            bys.append(ys[i])
            bys.append(ys[i] + hs[i])
            bys.append(ys[i] + hs[i])
            if i==0:
                self.ax.fill(bxs, bys, hatch=hatch, fc=faceColor, ls=linestyle, ec=edgecolor, lw=1, label=label)
            else:
                self.ax.fill(bxs, bys, hatch=hatch, fc=faceColor, ls=linestyle, ec=edgecolor, lw=1)

    # dbscan --- nword
    @staticmethod
    def dNword(fName='test.pdf', type=1):
        rFanout=50
        alpha=0.5
        steepD=0.1
        h=14
        om=1
        oe='0.001'
        ns=100

        t=1
        ts = [1, 2, 3, 4]

        k=10
        nw=2
        nws = [1, 2, 3, 4]

        mpts=50

        eps='0.001'
        xi='0.001'
        maxPNeiByte=2147483631

        # 设置bar参数
        barlabels = ['Basic', 'Adv1', 'Adv2', 'Adv3']
        xlabel = 'number of keyword'
        xTxts = ['1', '2', '3', '4']
        if type==1:
            ylabel = 'milliseconds'
            ylim=[100, 100000]
        elif type==2:
            ylabel = 'number of range queries'
            ylim=[100, 100000]

        # 创建bar对象
        bar = Bar(4,
                  xlabel=xlabel, xTxts=xTxts,
                  ylabel=ylabel, yscale='log', ylim=ylim,
                  fName = fName)

        dir = BasePathDbscan + 'nw_eps_mpt\\'

        for tIndex in range(len(ts)):
            total = []
            for nwIndex in range(len(nws)):
                data = Data.getData(dir, rFanout, alpha, steepD, h, om, oe, ns, ts[tIndex],
                                    k, nws[nwIndex], mpts, eps, xi, maxPNeiByte)
                print(data)
                if type==1: total.append(data.timeTotal)
                elif type==2: total.append(data.numRangeRtree)
            bar.drawBar(barlabels[tIndex], tIndex, total)

        bar.show()

    # dbscan --- epsilon
    @staticmethod
    def dEpsilon(fName='test.pdf'):
        rFanout=50
        alpha=0.5
        steepD=0.1
        h=14
        om=1
        oe='0.001'
        ns=100

        t=1
        ts = [1, 2, 3, 4]

        k=10
        nw=2

        mpts=50

        eps='0.001'
        epss = ['1.0E-4', '5.0E-4', '0.001', '0.005', '0.01']

        xi='0.001'
        maxPNeiByte=2147483631

        # 设置bar参数
        barlabels = ['Basic', 'Adv1', 'Adv2', 'Adv3']
        xlabel = '$\epsilon$'
        xTxts = ['0.0001', '0.0005', '0.001', '0.005', '0.01']
        ylabel = 'milliseconds'

        # 创建bar对象
        bar = Bar(4,
                  xlabel=xlabel, xTxts=xTxts, xRotateAngle=90,
                  ylabel=ylabel, yscale='log', ylim=[1000, 1000000],
                  loc=2,
                  fName = fName)

        dir = BasePathDbscan + 'nw_eps_mpt\\'

        for tIndex in range(len(ts)):
            timeTotals = []
            for epsIndex in range(len(epss)):
                data = Data.getData(dir, rFanout, alpha, steepD, h, om, oe, ns, ts[tIndex],
                                    k, nw, mpts, epss[epsIndex], xi, maxPNeiByte)
                print(data)
                timeTotals.append(data.timeTotal)
            bar.drawBar(barlabels[tIndex], tIndex, timeTotals)

        bar.show()

    # dbscan --- minpts
    @staticmethod
    def dMinpts(fName='test.pdf'):
        rFanout=50
        alpha=0.5
        steepD=0.1
        h=14
        om=1
        oe='0.001'
        ns=100

        t=1
        ts = [1, 2, 3, 4]

        k=10
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

        dir = BasePathDbscan + 'nw_eps_mpt\\'

        for tIndex in range(len(ts)):
            timeTotals = []
            for mptIndex in range(len(mptss)):
                data = Data.getData(dir, rFanout, alpha, steepD, h, om, oe, ns, ts[tIndex],
                                    k, nw, mptss[mptIndex], eps, xi, maxPNeiByte)
                print(data)
                timeTotals.append(data.timeTotal)
            bar.drawBar(barlabels[tIndex], tIndex, timeTotals)

        bar.show()

    # dbscan --- h all
    @staticmethod
    def dHAll(fName='test.pdf', isAll=False):
        rFanout=50
        alpha=0.5
        steepD=0.1

        h=15
        hs = ['4', '6', '8', '10', '12', '14', '16']

        om=1
        oe='0.001'
        ns=100

        t=1
        ts = None
        if isAll:   ts = [1, 2, 3, 4]
        else:   ts = [1, 4]

        k=10
        nw=2

        mpts=50

        eps='0.001'
        xi='0.001'
        maxPNeiByte=2147483631

        # 设置bar参数
        if isAll:
            barlabels = ['Basic', 'Adv1', 'Adv2', 'Adv3']
        else:
            barlabels = ['Basic', 'Adv3']
        xlabel = 'h'
        ylabel = 'milliseconds'

        # 创建bar对象
        numBar = 2
        if isAll:  numBar = 4
        if isAll:
            bar = Bar(numBar, widthBar=0.1, spanBars=0.02,
                      xlabel=xlabel, xTxts=hs,
                      ylabel=ylabel, yscale='log', ylim=[1000, 100000],
                      fName = fName)
        else:
            bar = Bar(numBar,
                      xlabel=xlabel, xTxts=hs,
                      ylabel=ylabel, yscale='log', ylim=[1000, 100000],
                      fName = fName)

        dir = BasePathDbscan + 'h_all\\'

        for tIndex in range(len(ts)):
            timeTotals = []
            for hIndex in range(len(hs)):
                data = Data.getData(dir, rFanout, alpha, steepD, hs[hIndex], om, oe, ns, ts[tIndex],
                                    k, nw, mpts, eps, xi, maxPNeiByte)
                print(data)
                timeTotals.append(data.timeTotal)
            bar.drawBar(barlabels[tIndex], tIndex, timeTotals)

        bar.show()

    # optic --- nword
    @staticmethod
    def oNword(fName='test.pdf', type=1):
        rFanout=50
        alpha=0.5
        steepD=0.1
        h=14
        om=1
        oe='1.0E-4'
        ns=100

        t=1
        ts = [11, 12]

        k=10
        nw=2
        nws = [1, 2, 3, 4]

        mpts=50

        eps='1.0E-4'
        xi='1.0E-4'
        maxPNeiByte=2147483631

        # 设置bar参数
        barlabels = ['Basic', 'Adv1']
        xlabel = 'number of keyword'
        xTxts = ['1', '2', '3', '4']
        if type==1:
            ylabel = 'milliseconds'
            ys = [i * 500 for i in range(5)]
            # ylim=[10, 10000]
        elif type==2:
            ylabel = 'number of range queries'
            ylim=[100, 100000]

        # 创建bar对象
        bar = Bar(len(ts),
                  xlabel=xlabel, xTxts=xTxts,
                  ylabel=ylabel, yscale='linear', ys=ys,
                  fName = fName)

        dir = BasePathOptic + 'nword\\'

        for tIndex in range(len(ts)):
            total = []
            for nwIndex in range(len(nws)):
                data = Data.getData(dir, rFanout, alpha, steepD, h, om, oe, ns, ts[tIndex],
                                    k, nws[nwIndex], mpts, eps, xi, maxPNeiByte)
                print(data)
                if type==1: total.append(data.timeTotal)
                elif type==2: total.append(data.numRangeRtree)
            bar.drawBar(barlabels[tIndex], tIndex, total)

        bar.show()


######################    dbscan   #########################
# Bar.dNword('dbscan_nword_runtime.pdf')
# Bar.dNword('dbscan_nword_numrangequery.pdf', type=2)
# Bar.dMinpts('dbscan_minpts_runtime.pdf')
# Bar.dEpsilon('dbscan_epsilon_runtime.pdf')
# Bar.dHAll('dbscan_h_runtime.pdf')
# Bar.dHAll('dbscan_h_all_runtime.pdf', True)


######################     optic   #########################
# Bar.oNword('optic_nword_runtime.pdf')
Bar.o




































plt.pause(3600)
