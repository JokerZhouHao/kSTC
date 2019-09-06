import matplotlib.pyplot as plt
import random
from matplotlib import interactive
from utility import PathUtility
from utility.io import Global
from utility.io import IterableReader

class Line:
    def __init__(self, title='title',
                 xlabel=None, xs=None,
                 ylable=None, yscale='linear', ys=None, ylim=None,
                 fName='test.pdf'):
        self.linestyles = ['-', '--', '-.', ':']
        self.indexLineStyle = 0
        self.dashess = [[2, 2, 2, 2], [4, 4, 4, 4], [6, 6, 6, 6],
                        [6, 2, 6, 2], [10, 5, 10, 5], [12, 12, 12, 12]]
        self.indexDash = 0

        self.title = title

        self.xlabel = xlabel
        self.xs = xs

        self.ylable = ylable
        self.yscale = yscale
        self.ys = ys
        self.ylim = ylim

        self.fpath = PathUtility.figure_path() + fName

        # self.fig = plt.figure(random.randint(1, 10000), figsize=(10.1023, 6.5), tight_layout=True)
        self.fig = plt.figure(random.randint(1, 10000), figsize=(10, 8), tight_layout=True)
        self.fig.canvas.set_window_title(title)

        plt.rcParams['font.size'] = 20
        self.ax = self.fig.add_subplot(111)

    def linestyle(self):
        ls = self.linestyles[self.indexLineStyle]
        self.indexLineStyle += 1
        if self.indexLineStyle == len(self.linestyles):
            self.indexLineStyle = 0
        return ls

    def dash(self):
        dash = self.dashess[self.indexDash]
        self.indexDash += 1
        if self.indexDash == len(self.dashess):
            self.indexDash = 0
        return dash

    def show(self):
        # legend设置
        self.ax.legend(loc=2, frameon=False, prop={'size': 18}, markerscale = 1)

        # x轴
        self.ax.set_xlabel(self.xlabel)
        if self.xs != None:
            self.ax.set_xticks(self.xs)
            self.ax.set_xlim(self.xs[0], self.xs[-1])

        # y轴
        self.ax.set_ylabel(self.ylable)
        self.ax.set_yscale(self.yscale)
        if self.yscale == 'linear':
            if self.ys != None:
                self.ax.set_yticks(self.ys)
                self.ax.set_ylim([self.ys[0], self.ys[-1]])
            else:
                self.ax.set_ylim(self.ylim)
        else:
            if self.ylim != None:
                self.ax.set_ylim(self.ylim)

        # 设置x轴对应txt
        # self.xTxts = ['0', '', '', '', '1e6', '', '', '', '2e6', '', '']
        # self.xTxts = ['0', '', '', '', r'$1 \times 10^6$', '', '', '', r'$2 \times 10^6$', '', '']
        # self.xTxts = ['0', '', r'$1 \times 10^6$', '', r'$2 \times 10^6$', '', '', '', '', '', '']
        self.xTxts = ['0', '', '', '', r'$1$', '', '', '', r'$2$', '', '']
        xaxis = self.ax.get_xaxis()
        x_labels = xaxis.get_ticklabels()
        for i in range(len(x_labels)):
            pos = x_labels[i].get_position()
            x_labels[i].set_position(pos)
            x_labels[i].set_verticalalignment('top')
            x_labels[i].set_text(self.xTxts[i-1])
        xaxis.set_ticklabels(x_labels)

        # 设置y轴对应txt
        # self.yTxts = ['0', '1.4e-2', '1.4e-1', '1.4', '1.4e1', '1.4e2', '1.4e3', '1.4e4', '1.4e5', '1.4e6', '1.4e6', '1.4e7']
        self.yTxts = ['$0$', r'$1.4 \times 10^{-2}$', r'$1.4 \times 10^{-1}$', r'$1.4$', r'$1.4 \times 10^1$', r'$1.4 \times 10^2$',
                      r'$1.4 \times 10^3$', r'$1.4 \times 10^4$', r'$1.4 \times 10^5$', r'$1.4 \times 10^6$', r'$1.4 \times 10^6$', r'$1.4 \times 10^7$']
        yaxis = self.ax.get_yaxis()
        y_labels = yaxis.get_ticklabels()
        for i in range(len(y_labels)):
            pos = y_labels[i].get_position()
            y_labels[i].set_position(pos)
            # y_labels[i].set_verticalalignment('top')
            y_labels[i].set_text(self.yTxts[i-1])
        yaxis.set_ticklabels(y_labels)

        interactive(True)
        plt.show()

        # 保存图像
        self.fig.savefig(self.fpath)

    def drawLine(self, xs, ys, label, linewidth=1, linestyle=None, marker=None, markersize=2, dashes=None):
        self.ax.plot(xs, ys, label=label,
                     linewidth=linewidth, color='black',
                     marker=marker, markersize=markersize,
                     dashes=dashes)


    @staticmethod
    def draw_k_nearest_distance(file_paths, title='title', fName='test.pdf'):
        upRate = 1000000000
        myline = Line(title, fName=fName, yscale='log', ylim=[1, upRate], xlabel='number of object (million)', ylable='distance (m)')
        for file_path in file_paths:
            allCoords = [[], []]
            reader = IterableReader(file_path)
            i = 1
            for line in reader:
                allCoords[0].append(i)
                coords = line.split(Global.delimiterLevel1)
                allCoords[1].append(float(coords[0]) * upRate + 1)
                i = i + 1
            myline.drawLine(allCoords[0], allCoords[1],
                            label="k = " + file_path[file_path.rindex("_") + 1:file_path.rindex(".")],
                            linewidth = 2,
                            dashes = myline.dash())

        myline.show()
        return myline

# line = Line()
# xs = [1, 1.3, 2]
# ys = [1, 1.6, 2]
# line.drawLine(xs, ys, 'test', linestyle='--', marker="o")
# line.show()


########## draw k nearest distance ########
Global.pathOutput = 'D:\kSTC\Dataset\places_dump_20110628\[-125.0,31.2],[-109.0,42.2]\output\\'
k_paths = []
k_paths.append(Global.pathOutput + 'KNNNeighborDis_3.txt')
k_paths.append(Global.pathOutput + 'KNNNeighborDis_4.txt')
k_paths.append(Global.pathOutput + 'KNNNeighborDis_5.txt')
k_paths.append(Global.pathOutput + 'KNNNeighborDis_6.txt')
# k_paths.append(Global.pathOutput + 'KNNNeighborDis_10.txt')
# k_paths.append(Global.pathOutput + 'KNNNeighborDis_20.txt')
# k_paths.append(Global.pathOutput + 'KNNNeighborDis_50.txt')
# k_paths.append(Global.pathOutput + 'KNNNeighborDis_100.txt')
Line.draw_k_nearest_distance(k_paths, title="KNNNeighborDis", fName="KNNNeighborDis.pdf")







plt.pause(3600)
