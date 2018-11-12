import numpy as np
import matplotlib.pyplot as plt
import time
import random
from matplotlib import interactive

class Bar:
    width = 0.148
    span = 0.05
    spanTotal = width + span
    base_startx = 1 - 2*width - span/2*3
    hatchx = 'xx'
    hatchxes = ['xx', '.', '+', 'O']
    ylim = 1000000

    def __init__(self, xLabel=None, yLabel=None ,is_stack=False, title=None, xs=None, x_txts=None, ys=None, yscale=None, y_type=None, f_type=None, fpath='test.pdf'):
        self.xLabel = xLabel
        self.yLabel = yLabel
        self.y_type = y_type
        self.is_stack= is_stack
        if xs==None:    self.xs = [0.5, 1, 2, 3, 4, 5, 6, 7, 7.5]
        else:   self.xs = xs
        if x_txts==None:    self.x_txts = [1.0, 3.0, 5.0, 8.0, 10.0, 15.0, 20.0]
        else: self.x_txts = x_txts
        if ys==None:    self.ys = [i**10 for i in  range(5)]
        else:   self.ys = ys
        if yscale==None:    self.yscale='log'
        else: self.yscale = yscale
        self.fig = plt.figure(random.randint(1, 10000), figsize=(10.1023, 6.5), tight_layout=True)
        if title==None: self.fig.canvas.set_window_title('Test')
        else:   self.fig.canvas.set_window_title(title)
        self.f_type = f_type
        plt.rcParams['font.size'] = 20
        self.ax = self.fig.add_subplot(111)
        self.fpath = fpath

    # 画柱状图
    def draw_bar(self, start_index, hs, ys=None, label=None, faceColor='#CCCCCC', hatch=None, linestyle='solid', edgecolor='black'):
        startx = Bar.base_startx + start_index * Bar.spanTotal
        if ys==None:
            ys = [0 for i in range(len(hs))]
        for i in range(len(hs)):
            bxs = []
            bxs.append(startx + i)
            bxs.append(startx + i + Bar.width)
            bxs.append(startx + i + Bar.width)
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

            yPos = self.ys[0] - 11
            if self.f_type=='radius_len_SPTD*':
                yPos = self.ys[0] - 13
            elif self.f_type=='radius_len_SPTR*':
                yPos = self.ys[0] - 14

            # if self.yscale != 'log': yPos=-30
            # if self.y_type=='TQSP': yPos = 0.7
            # if self.y_type=='RTree': yPos = 0.7
            # if self.y_type=='NW': yPos = -35

            # if label!=None:
            #     self.ax.text((startx + i + startx + i + Bar.width)/2, yPos, label, fontsize=20, ha='center', rotation=0)

    def show(self):
        # 设置legend
        if self.is_stack:
            self.ax.fill([-1, 0, 0, -1], [-1, -1, -1.1, -1.1], label='Other Time', fc='white', ec='black')
            self.ax.fill([-1, 0, 0, -1], [-1, -1, -1.1, -1.1], hatch=Bar.hatchx, label='Semantic Time', fc='#CCCCCC', ec='black')
            self.ax.legend(loc=2, frameon=False, prop={'size': 9})
        elif self.f_type.startswith('Alpha'):
            self.ax.legend(loc=2, frameon=False, prop={'size': 20})

        # 设置X、Y限制
        self.ax.tick_params(axis='x', direction='in', width=3, length=8, which='major')
        self.ax.tick_params(axis='y', direction='out', width=3, length=8, which='major')
        self.ax.tick_params(direction='out', width=2, length=4, which='minor')
        self.ax.set_xticks(self.xs)
        self.ax.set_xlim(self.xs[0], self.xs[-1])
        if self.yscale=='log':
            self.ax.set_ylim(1, Bar.ylim)
            self.ax.set_yscale('log')
        else:
            self.ax.set_yticks(self.ys)
            self.ax.set_ylim(self.ys[0], self.ys[-1])

        # 设置下方X、Y轴标题位置
        xLabel_x = (self.xs[0] + self.xs[-1]/5*4)/2;
        # xLabel_x = 3
        xLabel_y = 0.3
        # xLabel_y = 1
        if self.yscale!='log':  xLabel_y -= 120
        if self.y_type=='TQSP': xLabel_y = 0.5
        if self.y_type=='RTree': xLabel_y = 0.3
        # if self.y_type=='NW':
        #     xLabel_x = 3
        #     xLabel_y = -35 - 100
        # if self.xLabel!=None:
            # self.ax.text(xLabel_x, xLabel_y, self.xLabel)
            # self.ax.set_xlabel(self.xLabel, va='bottom', visible=True)
            # self.ax.set_title(self.xLabel, fontsize=9, verticalalignment='bottom', pad=17)
        if self.yLabel!=None:   self.ax.set_ylabel(self.yLabel)
        if self.xLabel != None:  self.ax.set_xlabel(self.xLabel)

        # 隐藏下方x轴的label
        yPos = None
        if self.f_type.startswith('radius_len'):
            yPos = -0.015
        xaxis = self.ax.get_xaxis()
        x_labels = xaxis.get_ticklabels()
        for i in range(len(x_labels)):
            if i==0 or i==len(x_labels)-1:
                x_labels[i].set_visible(False)
            else:
                pos = x_labels[i].get_position()
                print(pos)
                print(x_labels[i].get_verticalalignment())
                if yPos!=None:
                    pos = (pos[0], yPos)
                x_labels[i].set_position(pos)
                x_labels[i].set_verticalalignment('top')
                x_labels[i].set_text(self.x_txts[i-1])
        xaxis.set_ticklabels(x_labels)
        # 隐藏下方x轴的刻度线
        x_lines = xaxis.get_ticklines()
        for ln in x_lines:
            ln.set_visible(False)

        # 设置右边Y轴刻度
        # self.ax_x = self.ax.twinx()
        # right_y_axis = self.ax_x.get_yaxis()
        # yticks = right_y_axis.get_ticklabels()
        # for tk in yticks:
        #     tk.set_visible(False)
        # right_y_axis.set_ticklabels(yticks)

        # if self.yscale=='log':
        #     self.ax_x.set_ylim(1, Bar.ylim)
        #     self.ax_x.set_yscale('log')
        # else:
        #     self.ax_x.set_yticks(self.ys)
        #     self.ax_x.set_ylim(self.ys[0], self.ys[-1])

        # 设置上方X轴刻度
        # self.ax_y = self.ax.twiny()
        #
        # if self.xLabel != None:
        #     self.ax_y.set_xlabel(self.xLabel)
        #
        # self.ax_y.tick_params(axis='x', direction='in', width=3, length=8, which='major')
        # self.ax_y.set_xticks(self.xs)
        # self.ax_y.set_xlim(self.xs[0], self.xs[-1])
        #
        # # 改变上方x轴的labels
        # xaxis = self.ax_y.get_xaxis()
        # # 替换刻度
        # x_labels = xaxis.get_ticklabels()
        # for i in range(len(x_labels)):
        #     if i==0 or i==len(x_labels)-1:
        #         x_labels[i].set_visible(False)
        #     else:
        #         x_labels[i].set_text(self.x_txts[i-1])
        # xaxis.set_ticklabels(x_labels)
        # # 隐藏刻度线
        # x_lines = xaxis.get_ticklines()
        # for i in range(len(x_lines)):
        #     if i==1 or i==len(x_labels)*2-1:
        #         x_lines[i].set_visible(False)

        # self.ax.grid('on')

        interactive(True)
        plt.show()
        self.fig.savefig(self.fpath)

# 折线图
class LineChart:
    ylim = 10000
    line_types = ["$\\bigcirc$", "$+$", "$\\bigtriangledown$", "$\\ast$", '$\\diamondsuit$', "$\\bigtriangleup$", '$\\times$', "<", ">", "H"]
    line_type_index = 0
    line_color = 'black'

    def __init__(self, xs, x_txts, xLabel=None, yLabel=None ,title=None, ys=None, yscale=None, ylim=None, y_type=None, xlabel_rotation=0, fpath='test.pdf'):
        self.xLabel = xLabel
        self.yLabel = yLabel
        self.y_type = y_type
        if xs==None:    self.xs = [0.5, 1, 2, 3, 4, 5, 6, 7, 7.5]
        else:   self.xs = xs
        if x_txts==None:    self.x_txts = [1.0, 3.0, 5.0, 8.0, 10.0, 15.0, 20.0]
        else: self.x_txts = x_txts
        if ys==None:    self.ys = [0.5, 1, 2, 3, 4, 5, 6, 7, 7.5]
        else:   self.ys = ys
        if yscale==None:    self.yscale='log'
        else: self.yscale = yscale
        self.fig = plt.figure(random.randint(1, 10000), figsize=(10.1023, 6.5), tight_layout=True)
        if title==None: self.fig.canvas.set_window_title('Test')
        else:   self.fig.canvas.set_window_title(title)
        self.xlabel_rotation = xlabel_rotation
        plt.rcParams['font.size'] = 20
        self.ax = self.fig.add_subplot(111)
        self.ylim = ylim
        self.fpath = fpath

    # 画折线图
    def draw_line(self, ys, label):
        self.ax.plot(self.xs, ys, label=label, marker=self.line_types[self.line_type_index], c='black', markersize=18)
        self.line_type_index += 1

    def show(self):
        if self.yLabel.find('TQTSP') != -1:
            # self.ax.legend(loc=0, prop={'size': 15}, bbox_to_anchor=(0.97, 0.84))
            self.ax.legend(loc=0, prop={'size': 15})
        else:
            self.ax.legend(loc=0, prop={'size': 15})

        # 设置下方X轴刻度
        self.ax.tick_params(axis='x', direction='in', width=3, length=8, which='major')
        self.ax.tick_params(axis='y', direction='out', width=3, length=8, which='major')
        self.ax.tick_params(direction='out', width=2, length=4, which='minor')
        self.ax.set_xticks(self.xs)
        self.ax.set_xlim(self.xs[0], self.xs[-1])

        # 设置Y轴的刻度
        if self.yscale=='log':
            if self.ylim != None:
                self.ax.set_ylim(self.ylim)
            else:   self.ax.set_ylim(1, Bar.ylim)
            self.ax.set_yscale('log')
        else:
            self.ax.set_yticks(self.ys)
            self.ax.set_ylim(self.ys[0], self.ys[-1])

        xaxis = self.ax.get_xaxis()
        # 隐藏下方x轴的刻度线
        # x_lines = xaxis.get_ticklines()
        # for ln in x_lines:
        #     ln.set_visible(False)

        # 设置下方x轴的label
        x_labels = xaxis.get_ticklabels()
        for i in range(len(x_labels)):
            x_labels[i].set_text(self.x_txts[i])
            x_labels[i].set_rotation(self.xlabel_rotation)
        xaxis.set_ticklabels(x_labels)

        # 设置下方x轴标题位置
        xLabel_x = (self.xs[0] + self.xs[-1])/2
        xLabel_y = -0.5
        if self.xLabel!=None:
            # self.ax.text(xLabel_x, xLabel_y, self.xLabel)
            # self.ax.set_xlabel(self.xLabel)
            # self.ax.set_title(self.xLabel, fontsize=20, verticalalignment='bottom')
            self.ax.set_xlabel(self.xLabel, fontsize=20)
        if self.yLabel!=None:   self.ax.set_ylabel(self.yLabel)

        # 添加右边y轴的刻度
        # self.ax_x = self.ax.twinx()
        # self.ax_x.tick_params(axis='y', direction='out', width=3, length=8, which='major')
        # self.ax_x.tick_params(direction='out', width=2, length=4, which='minor')
        #
        # if self.yscale=='log':
        #     self.ax_x.set_ylim(1, Bar.ylim)
        #     self.ax_x.set_yscale('log')
        # else:
        #     self.ax_x.set_yticks(self.ys)
        #     self.ax_x.set_ylim(self.ys[0], self.ys[-1])

        interactive(True)
        plt.show()
        self.fig.savefig(self.fpath)

# 散点图
class Scatter:
    ylim = 10000

    def __init__(self, xLabel=None, yLabel=None, xs=None, x_txts=None, ys=None, ylim=None, yscale=None, title=None, xlabel_rotation=0):
        self.xLabel = xLabel
        self.yLabel = yLabel
        if xs==None:    self.xs = [0.5, 1, 2, 3, 4, 5, 6, 7, 7.5]
        else:   self.xs = xs
        if x_txts==None:    self.x_txts = [1.0, 3.0, 5.0, 8.0, 10.0, 15.0, 20.0]
        else: self.x_txts = x_txts
        self.ylim = ylim
        if ys==None:    self.ys = [0.5, 1, 2, 3, 4, 5, 6, 7, 7.5]
        else:   self.ys = ys
        if yscale==None:    self.yscale='log'
        else: self.yscale = yscale
        self.fig = plt.figure(random.randint(1, 10000), figsize=(10.1023, 6.5), tight_layout=True)
        if title==None: self.fig.canvas.set_window_title('Test')
        else:   self.fig.canvas.set_window_title(title)
        self.xlabel_rotation = xlabel_rotation
        plt.rcParams['font.size'] = 20
        self.ax = self.fig.add_subplot(111)

    # 画折线图
    def draw_scatter(self, xs, ys, label=None, color='black', lw=1):
        # self.ax.plot(self.xs, ys, label=label, marker=self.line_types[self.line_type_index], c='black', markersize=18)
        self.ax.scatter(xs, ys, color=color, linewidths=lw)

    def show(self):

        # 设置下方X轴刻度
        self.ax.tick_params(axis='x', direction='in', width=3, length=8, which='major')
        self.ax.tick_params(axis='y', direction='out', width=3, length=8, which='major')
        self.ax.tick_params(direction='out', width=2, length=4, which='minor')
        self.ax.set_xticks(self.xs)
        self.ax.set_xlim(self.xs[0], self.xs[-1])

        # 设置Y轴的刻度
        if self.yscale=='log':
            if self.ylim != None:
                self.ax.set_ylim(self.ylim)
            else:   self.ax.set_ylim(1, Scatter.ylim)
            self.ax.set_yscale('log')
        else:
            self.ax.set_yticks(self.ys)
            self.ax.set_ylim(self.ys[0], self.ys[-1])



        xaxis = self.ax.get_xaxis()
        # 设置下方x轴的label
        x_labels = xaxis.get_ticklabels()
        for i in range(len(x_labels)):
            x_labels[i].set_text(self.x_txts[i])
            x_labels[i].set_rotation(self.xlabel_rotation)
        xaxis.set_ticklabels(x_labels)

        # 设置下方x轴标题位置
        if self.xLabel!=None:
            self.ax.set_xlabel(self.xLabel, fontsize=20)
        if self.yLabel!=None:   self.ax.set_ylabel(self.yLabel)

        # interactive(True)
        plt.show()

