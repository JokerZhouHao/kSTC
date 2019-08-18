import matplotlib.pyplot as plt
from utility.io import IterableReader
from utility.io import Global
from utility import myplt
from matplotlib import interactive
import numpy as np
import random
import matplotlib.image as img
from utility import PathUtility
import matplotlib as mpl

import matplotlib.cm as cm
import matplotlib.mlab as mlab

class Scatter:
    colors = ['#FF1493', '#0000FF', '#00BFFF', '#8B658B', '#008B8B', '#8B008B', '#00BFFF', '#FF00FF', '#FF4500']
    # colors = ['#6b8ba4']
    markers = ['o', 'v', 's', 'p', 'P']

    index_marker = 0
    index_colors = 0

    def __init__(self, fig=None, xs=None, ys=None, xlim=None, ylim=None, title='title', pathBgImg = None, yscale = 'linear',
                 showXY = True,
                 markerscale = 1, fName = 'test.pdf'):

        plt.rcParams['axes.ymargin'] = 0
        plt.rcParams['axes.autolimit_mode'] = 'round_numbers'

        if fig is None:
            # self.fig = plt.figure(random.randint(1, 10000), figsize=(10.1023, 6.5), tight_layout=True)
            self.fig = plt.figure(random.randint(1, 10000), figsize=(8, 6.5), tight_layout=True)
        else:
            self.fig = fig
        # self.fig = plt.figure(random.randint(1, 10000))
        self.fig.canvas.set_window_title(title)

        # #  = None
        # if yscale != None:
        #     self.yscale = yscale
        # else:
        #     self.yscale = 'linear'
        self.markerscale = markerscale
        self.yscale = yscale
        self.xs = xs
        self.ys = ys
        self.xlim = xlim
        self.ylim = ylim
        self.pathBgImg = pathBgImg

        self.showXY = showXY

        self.fpath = PathUtility.figure_path() + fName

        plt.rcParams['font.size'] = 20
        plt.subplots_adjust(left=0, right=1, bottom=0, top=1)
        plt.margins(0)
        if fig is None:
            self.ax = self.fig.add_subplot(111)

        # plt.setp(self.ax.get_xaxis().get_offset_text(), visible=False)
        # plt.setp(self.ax.get_yaxis().get_offset_text(), visible=False)


    def draw_scatter(self, points, s=1, marker='o', c=None, label = None):
        # self.ax.scatter(points[0], points[1], s=s, marker=marker, c=(random.uniform(0, 1), random.uniform(0, 1), random.uniform(0, 1)))
        # Scatter.index_colors = Scatter.index_colors + 1
        # self.ax.scatter(points[0], points[1], s=s, marker=marker, c = Scatter.colors[Scatter.index_colors])
        self.ax.scatter(points[0], points[1], s=s, marker=marker, c=c, label=label)

    def show(self):
        # self.ax.legend(loc=2, prop={'size': 15}, markerscale = self.markerscale)
        if self.xs != None:
            self.ax.set_xticks(self.xs)
            self.ax.set_xlim(self.xs[0], self.xs[len(self.xs) - 1])
        if self.ys != None:
            self.ax.set_yticks(self.ys)
            self.ax.set_ylim(self.ys[0], self.ys[len(self.ys) - 1])
        if self.xlim != None:
            self.ax.set_xlim(self.xlim)
        if self.ylim != None:
            self.ax.set_ylim(self.ylim)
        self.ax.set_yscale(self.yscale)

        if self.showXY == False:
            # self.ax.set_xlabel([])
            # self.ax.set_ylabel([])
            # self.ax.set_xticks([])
            # self.ax.set_yticks([])
            self.ax.set_yticks([])
            self.ax.set_xticks([])
            self.ax.axis('off')

        # plt.autoscale(enable=True, axis='y', tight=True)

        # 全屏显示
        # mng = plt.get_current_fig_manager()
        # mng.full_screen_toggle()

        if self.pathBgImg != None:
            imgBg = plt.imread(pathBgImg)
            # self.ax.imshow(imgBg)
            # imshow(img, zorder=0, extent=[left, right, bottom, top])
            # plt.imshow(imgBg, zorder=0, extent=[0, 1, 0, 1])
            # plt.imshow(imgBg, interpolation='bilinear', cmap=cm.gray,
            #     origin='lower', extent=[0, 1, 0, 1])
            self.ax.imshow(imgBg, aspect='auto', extent=[self.xlim[0], self.xlim[1], self.ylim[0], self.ylim[1]])
            # plt.imshow(imgBg)
            # self.ax.figimage(imgBg)
            # self.ax.imshow(imgBg)

        interactive(True)
        plt.show()

        # 保存图像
        self.fig.savefig(self.fpath, bbox_inches = 'tight', pad_inches = 0)

    @staticmethod
    def marker():
        if Scatter.index_marker == len(Scatter.markers):
            Scatter.index_marker = 0
        marker = Scatter.markers[Scatter.index_marker]
        Scatter.index_marker += 1
        return marker

    @staticmethod
    def color():
        if Scatter.index_colors >= len(Scatter.colors):
            Scatter.index_colors = 0
        color = Scatter.colors[Scatter.index_colors]
        Scatter.index_colors += 1
        return color

    @staticmethod
    def draw_orginal_coord(path, s=1, marker='o', show=True, title='title', pathBgImg = None, xlim=None, ylim=None, scala = 10000000000):
        scatter = Scatter(title=title, xlim=xlim, ylim=ylim, pathBgImg = pathBgImg)

        allCoords = [[], []]
        reader = IterableReader(path)
        i = 0
        recAccCoords = set()
        for line in reader:
            coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
            lon = float(coords[0])
            lon = int(lon * scala) / float(scala)
            lat = float(coords[1])
            lat = int(lat * scala) / float(scala)
            if lon * lat in recAccCoords:   continue
            else: recAccCoords.add(lon * lat)
            allCoords[0].append(lon)
            allCoords[1].append(lat)
            i = i + 1
        scatter.draw_scatter(allCoords, s=s, marker=marker, c='#AFEEEE')
        print("num coord: " + str(i))
        if show:
            scatter.show()
        return scatter

    @staticmethod
    def draw_result(all_coord_path, result_path, s=10, show=True, title='title', pathBgImg = None,
                    xlim=None, ylim=None, fName = 'test.pdf', showXY = False):
        # scatter = Scatter.draw_orginal_coord(all_coord_path, s=s, show=False, title=title, pathBgImg=pathBgImg, xlim=xlim, ylim=ylim)
        #  if xs is None:
        #     self.xs = []
        #     for i in range(0, 11, 1):
        #         self.xs.append(i * 0.1)
        # else:
        #     self.xs = xs;
        # if ys is None:
        #     self.ys = []
        #     for i in range(0, 11, 1):
        #         self.ys.append(i * 0.1)
        # else:
        #     self.ys = ys;

        xs = []
        for i in range(0, 11, 1):
            xs.append(i * 0.1)
        ys = xs
        # xlim = [0, 1]
        # ylim = [0, 1]

        Scatter.index_marker = 0
        scatter = Scatter(title=title[title.rindex('\\') + 1:], xs=xs, ys=ys, pathBgImg = pathBgImg,
                          xlim=xlim, ylim=ylim, fName=fName, showXY=showXY)

        allCoords = [[], []]
        centerCoords = [[], []]
        reader = IterableReader(result_path)

        for line in reader:
            if line.startswith('qParams'):
                coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
                # if float(coords[0]) >= scatter.xlim[0] and float(coords[0]) <= scatter.xlim[1] and float(coords[1]) >= scatter.ylim[0] and float(coords[1]) <= scatter.ylim[1]:
                #     continue
                centerCoords[0].append(float(coords[0]))
                centerCoords[1].append(float(coords[1]))
            elif line.startswith('Cluster'):
                scatter.draw_scatter(allCoords, s=s, marker=Scatter.marker(), c=Scatter.color())
                # scatter.draw_scatter(allCoords, s=s)
                allCoords[0].clear()
                allCoords[1].clear()
            elif line.startswith("cluster_num"):
                continue
            else:
                coords = line.split(Global.delimiterLevel1)[2].split(Global.delimiterSpace)
                # if float(coords[0]) >= scatter.xlim[0] and float(coords[0]) <= scatter.xlim[1] and float(coords[1]) >= scatter.ylim[0] and float(coords[1]) <= scatter.ylim[1]:
                #     continue
                allCoords[0].append(float(coords[0]))
                allCoords[1].append(float(coords[1]))
        scatter.draw_scatter(allCoords, s=s, marker=Scatter.marker(), c=Scatter.color())
        # scatter.draw_scatter(allCoords, s=s)

        scatter.draw_scatter(centerCoords, s=s, marker='*')

        if show:
            scatter.show()
        Scatter.index_colors = -1
        return scatter

    @staticmethod
    def draw_k_nearest_distance(file_paths, s=10, show=True, title='title'):
        upRate = 1000000000
        scatter = Scatter(title=title, ylim=[1, upRate], yscale ='log', markerscale=8)
        for file_path in file_paths:
            allCoords = [[], []]
            reader = IterableReader(file_path)
            i = 1
            for line in reader:
                    allCoords[0].append(i)
                    coords = line.split(Global.delimiterLevel1)
                    allCoords[1].append(float(coords[0]) * upRate + 1)
                    i = i + 1
            scatter.draw_scatter(allCoords, s=s, marker='o',
                                 label="k = " + file_path[file_path.rindex("_") + 1:file_path.rindex(".")])

        if show:
            scatter.show()
        Scatter.index_colors = -1
        return scatter

    @staticmethod
    def draw_reachability_dis(file_path, s=10, show=True, title='title'):
        scatter = Scatter(title=title)

        allCoords = [[], []]
        reader = IterableReader(file_path)
        i = 1
        for line in reader:
                allCoords[0].append(i)
                coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
                allCoords[1].append(float(coords[1]))
                i = i + 1
        scatter.draw_scatter(allCoords, s=s, marker='v')

        if show:
            scatter.show()
        Scatter.index_colors = -1
        return scatter


class Line:
    colors = ['red', 'blue', 'green']
    index_colors = -1

    def __init__(self, fig=None, xs=None, ys=None, title='title', max_y = None):
        if fig is None:
            self.fig = plt.figure(random.randint(1, 10000), figsize=(10.1023, 6.5), tight_layout=True)
        else:
            self.fig = fig
        # self.fig = plt.figure(random.randint(1, 10000))
        self.fig.canvas.set_window_title(title)

        if xs is None:
            self.xs = []
            for i in range(0, 11, 1):
                self.xs.append(i * 0.1)
        else:
            self.xs = xs;
        if ys is None:
            self.ys = []
            for i in range(0, 11, 1):
                self.ys.append(i * 0.1)
        else:
            self.ys = ys
        if max_y is not None:
            self.max_y = max_y
        else:
            self.max_y = None

        plt.rcParams['font.size'] = 20
        if fig is None:
            self.ax = self.fig.add_subplot(111)



    def draw_line(self, points, s=1, marker='o', c='#6b8ba4'):
    # def draw_line(self, points, s=1, marker='o', c='#000000'):
        # self.ax.scatter(points[0], points[1], s=s, marker=marker, c=(random.uniform(0, 1), random.uniform(0, 1), random.uniform(0, 1)))
        # self.ax.plot(points[0], points[1], linewidth=s, c=c)
        y0 = []
        for i in range(0, len(points[0])):
            y0.append(0)
        self.ax.vlines(points[0], y0, points[1], linewidth=s, colors=c)

    def show(self):
        # self.ax.set_xlim(self.xs[0], self.xs[len(self.xs)-1])
        # self.ax.set_xticks(self.xs)

        if self.max_y is not None:
            self.ax.set_ylim(0, self.max_y)
        # self.ax.set_yticks(self.ys)
        # self.ax.set_yscale('log')
        # self.ax.set_xscale('log')
        interactive(True)
        plt.show()

    @staticmethod
    def color():
        if Line.index_colors >= len(Line.colors):
            Line.index_colors = 0
        color = Line.colors[Line.index_colors]
        Line.index_colors += 1
        return color

    @staticmethod
    def draw_reachability_dis(file_path, s=1, show=True, title='title', max_y=None):
        lines = Line(title=title, max_y=max_y)

        allCoords = [[], []]
        reader = IterableReader(file_path)
        i = 0
        for line in reader:
                allCoords[0].append(i)
                coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
                allCoords[1].append(float(coords[1]))
                i = i + 1
        lines.draw_line(allCoords, s=s)

        if show:
            lines.show()
        Scatter.index_colors = -1
        return lines

    # 将optic的结果显示在distance图上
    @staticmethod
    def draw_reachability_dis_cluster(dis_path, result_path, s=1, show=True, title='title', max_y=None):
        lines = Line(title=title, max_y=max_y)

        # 画所有的dis
        allCoords = [[], []]
        reader = IterableReader(dis_path)
        i = 0
        for line in reader:
                allCoords[0].append(i)
                coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
                allCoords[1].append(float(coords[1]))
                i = i + 1
        lines.draw_line(allCoords, s=s)

        # 画聚类出的dis
        clusterCoords = [[], []]
        reader = IterableReader(result_path)

        for line in reader:
            if line.startswith('qParams'):
                continue
            elif line.startswith('Cluster'):
                if len(clusterCoords) != 0:
                    lines.draw_line(clusterCoords, s=s, c='red')
                clusterCoords = [[], []]
            elif line.startswith('cluster_num'):
                continue
            else:
                orderId = int(line.split(Global.delimiterLevel1)[0])
                clusterCoords[0].append(allCoords[0][orderId])
                clusterCoords[1].append(allCoords[1][orderId])
        lines.draw_line(clusterCoords, s=s, c='red')
        if show:
            lines.show()
        return lines

    # 将base的结果显示在optic的distance图上
    @staticmethod
    def draw_reachability_dis_base(dis_path, base_result_path, s=1, show=True, title='title', max_y=None):
        lines = Line(title=title, max_y=max_y)

        # 画所有的dis
        allCoords = [[], []]
        reader = IterableReader(dis_path)
        i = 0
        id_2_index = {}
        for line in reader:
                allCoords[0].append(i)
                coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
                allCoords[1].append(float(coords[1]))
                id_2_index[line.split(Global.delimiterLevel1)[0]] = i
                i = i + 1
        lines.draw_line(allCoords, s=s)

        # 画base_result对于的dis
        clusterCoords = [[], []]
        reader = IterableReader(base_result_path)
        for line in reader:
            if line.startswith('Cluster'):
                lines.draw_line(clusterCoords, s=s, c=Line.color())
                clusterCoords = [[], []]
            if line.startswith('-1'):
                orderId = id_2_index[line.split(Global.delimiterLevel1)[1]]
                clusterCoords[0].append(allCoords[0][orderId])
                clusterCoords[1].append(allCoords[1][orderId])

        if show:
            lines.show()
        return lines


#######################  经纬suffixFile = ([-112.41,33.46], [-111.90,33.68]) 的测试code  ###########################
########## draw coordinate data ##############
# pathCoord = Global.pathCoord
# Scatter.draw_orginal_coord(pathCoord, s=20, show=False)
# pathCoord = Global.pathCoord + '([-112.41,33.46],[-111.9,33.68])'
# Scatter.draw_orginal_coord(pathCoord, s=20, show=False)
# pathBgImg = Global.pathImgs + 'LonLat^-112.41,33.46 -111.90,33.68.png'
# pathBgImg = Global.pathImgs + 'LonLat^-112.41,33.46 -111.90,33.68[gray].png'
# pathBgImg = Global.pathImgs + 'test.png'
# pathCoord = Global.pathCoord + '([-112.41,33.46],[-111.9,33.68])[normalized]'
# pathCoord = 'D:\\kSTC\\Dataset\\places_dump_20110628\\id_coord_longtitude_latitude.txt'
# pathCoord = 'D:\\kSTC\\Dataset\\meetup\\id_coord_longtitude_latitude.txt'

# Scatter.draw_orginal_coord(pathCoord, s=20, show=False, scala=10)

###########################  path ##########################################
############   ([-112.41,33.46],[-111.9,33.68])[normalized]  path ##########
# pathBgImg = Global.pathImgs + 'LonLat^-112.41,33.46 -111.90,33.68.png'
# pathBgImg = Global.pathImgs + 'LonLat^-112.41,33.46 -111.90,33.68[gray].png'
# pathCoord = Global.pathCoord + '([-112.41,33.46],[-111.9,33.68])[normalized]'

############   ([-112.41,33.46],[-111.9,33.68])[normalized]  path ##########
# pathBgImg = None
# pathBgImg = None
# pathCoord = Global.pathCoord + '([-112.41,33.46],[-111.9,33.68])[normalized]'

############   [-125.0,28.0],[15.0,60.0] [normalized]  path ##########
pathBgImg = None
pathBgImg = None
pathCoord = Global.pathCoord + '[normalized]'

############################################# draw result data start #############################################
Global.pathOutput = 'D:\kSTC\Dataset\places_dump_20110628\[-114.0,31.0],[-108.0,37.0]\output\\res\\'
pathBgImg = None
pathBgImg = 'D:\kSTC\Dataset\places_dump_20110628\[-114.0,31.0],[-108.0,37.0]\input\\bg1_gray.png'
xlim = [0, 1]
ylim = [0, 1]

# xlim = [0.0909, 0.0926]
# ylim = [0.1740, 0.1775]

xlim = [0.525914, 0.530711]
ylim = [0.1980, 0.2016]


# xlim = [0.09137, 0.09155]
# ylim = [0.1769, 0.1773]

##############   dbscan #############
# pathResultAlgEucBase = Global.pathOutput + 'result_ecu_base.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucBase, s=20, show=True, title=pathResultAlgEucBase, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])

pathResultAlgEucFast = Global.pathOutput + 'result_ecu_fast_rFanout=50.alpha=0.5.steepD=0.1.h=10.om=1.oe=0.001.ns=200.t=4.k=5000.nw=2.mpts=20.eps=0.001.xi=0.001.maxPNeiByte=2147483631'
Scatter.draw_result(pathCoord, pathResultAlgEucFast, s=150, show=True, title=pathResultAlgEucFast, pathBgImg=pathBgImg,
                    xlim=xlim, ylim=ylim, showXY=False,
                    fName='arizona_case_dbscan_minpts20.pdf')

pathResultAlgEucFast = Global.pathOutput + 'result_ecu_fast_rFanout=50.alpha=0.5.steepD=0.1.h=10.om=1.oe=0.001.ns=200.t=4.k=5000.nw=2.mpts=10.eps=0.001.xi=0.001.maxPNeiByte=2147483631'
Scatter.draw_result(pathCoord, pathResultAlgEucFast, s=150, show=True, title=pathResultAlgEucFast, pathBgImg=pathBgImg,
                    xlim=xlim, ylim=ylim, showXY=False,
                    fName='arizona_case_dbscan_minpts10.pdf')

pathResultAlgEucFast = Global.pathOutput + 'result_ecu_fast_rFanout=50.alpha=0.5.steepD=0.1.h=10.om=1.oe=0.001.ns=200.t=4.k=5000.nw=2.mpts=5.eps=0.001.xi=0.001.maxPNeiByte=2147483631'
Scatter.draw_result(pathCoord, pathResultAlgEucFast, s=150, show=True, title=pathResultAlgEucFast, pathBgImg=pathBgImg,
                    xlim=xlim, ylim=ylim, showXY=False,
                    fName='arizona_case_dbscan_minpts5.pdf')

##############    optic   #############
pathResultAlgEucBaseOptics = Global.pathOutput + 'result_ecu_base_optics_rFanout=50.alpha=0.5.steepD=0.1.h=10.om=1.oe=0.001.ns=200.t=4.k=5000.nw=2.mpts=5.eps=0.001.xi=0.001.maxPNeiByte=2147483631'
Scatter.draw_result(pathCoord, pathResultAlgEucBaseOptics, s=150, show=True, title=pathResultAlgEucBaseOptics, pathBgImg=pathBgImg,
                    xlim=xlim, ylim=ylim, showXY=False,
                    fName='arizona_case_optic_minpts5.pdf')

##############   optic wu #############
pathResultAlgEucBaseOpticsWu = Global.pathOutput + 'result_ecu_base_optics_wu_rFanout=50.alpha=0.5.steepD=0.1.h=10.om=1.oe=0.001.ns=200.t=4.k=5000.nw=2.mpts=5.eps=0.001.xi=0.001.maxPNeiByte=2147483631'
Scatter.draw_result(pathCoord, pathResultAlgEucBaseOpticsWu, s=150, show=True, title=pathResultAlgEucBaseOpticsWu, pathBgImg=pathBgImg,
                    xlim=xlim, ylim=ylim, showXY=False,
                    fName='arizona_case_opticwu_minpts5.pdf')
#
# pathResultAlgEucBaseOpticsWu = Global.pathOutput + 'result_ecu_base_optics_wu_rFanout=50.alpha=0.5.steepD=0.1.h=8.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=1.mpts=5.eps=0.001.xi=0.001.maxPNeiByte=2147483631'
# Scatter.draw_result(pathCoord, pathResultAlgEucBaseOpticsWu, s=20, show=True, title=pathResultAlgEucBaseOpticsWu, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])
#
# pathResultAlgEucBaseOpticsWu = Global.pathOutput + 'result_ecu_base_optics_wurFanout=50.alpha=0.5.steepD=0.1.h=10.ns=200.t=0.k=10.nw=1.mpts=5.eps=1.0E-4.xi=1.0E-4.maxPNeiByte=2147483631'
# Scatter.draw_result(pathCoord, pathResultAlgEucBaseOpticsWu, s=20, show=True, title=pathResultAlgEucBaseOpticsWu, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])
#
# pathResultAlgEucBaseOpticsWu = Global.pathOutput + 'result_ecu_base_optics_wurFanout=50.alpha=0.5.steepD=0.1.h=10.ns=200.t=0.k=100000.nw=1.mpts=5.eps=1.0E-4.xi=1.0E-4.maxPNeiByte=2147483631'
# Scatter.draw_result(pathCoord, pathResultAlgEucBaseOpticsWu, s=20, show=True, title=pathResultAlgEucBaseOpticsWu, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])

# pathResultAlgEucAdvancedOpticsWu = Global.pathOutput + 'result_ecu_advanced_optics_wu.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucAdvancedOpticsWu, s=20, show=True, title=pathResultAlgEucAdvancedOpticsWu, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])

##############   optic #############
# pathResultAlgEucBaseOptics = Global.pathOutput + 'result_ecu_base_optics_rFanout=50.alpha=0.5.steepD=0.1.h=8.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=1.mpts=5.eps=0.001.xi=0.001.maxPNeiByte=2147483631'
# Scatter.draw_result(pathCoord, pathResultAlgEucBaseOptics, s=10, show=True, title=pathResultAlgEucBaseOptics, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])

# pathResultAlgEucBaseOptics = Global.pathOutput + 'result_ecu_base_optics_rFanout=50.alpha=0.5.steepD=0.3.h=8.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=1.mpts=5.eps=0.001.xi=0.001.maxPNeiByte=2147483631'
# Scatter.draw_result(pathCoord, pathResultAlgEucBaseOptics, s=10, show=True, title=pathResultAlgEucBaseOptics, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])


# pathResultAlgEucAdvancedOptics = Global.pathOutput + 'result_ecu_base_optics_wu_rFanout=50.alpha=0.5.steepD=0.1.h=8.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=1.mpts=5.eps=0.001.xi=0.001.maxPNeiByte=2147483631'
# Scatter.draw_result(pathCoord, pathResultAlgEucAdvancedOptics, s=10, show=True, title=pathResultAlgEucAdvancedOptics)


############################################# draw result data end #############################################




########## draw_reachability_dis ########
# path_reach_dis = Global.pathOutput + 'order_objects.obj([-125.0, 28.0], [15.0, 60.0])_AlgEucDisBaseOptics'
# Line.draw_reachability_dis(path_reach_dis, s=1, title=path_reach_dis, max_y=0.004)

# path_reach_dis = Global.pathOutput + 'order_objects.obj_AlgEucDisBaseOptics_rFanout=50.alpha=0.5.steepD=0.1.h=14.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=2.mpts=5.eps=1.0E-4.xi=1.0E-4.maxPNeiByte=2147483631'
# Line.draw_reachability_dis(path_reach_dis, s=1, title=path_reach_dis, max_y=0.0002)
# #
# path_reach_dis = Global.pathOutput + 'order_objects.obj_AlgEucDisBaseOpticsWu_rFanout=50.alpha=0.5.steepD=0.1.h=14.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=2.mpts=5.eps=1.0E-4.xi=1.0E-4.maxPNeiByte=2147483631'
# Line.draw_reachability_dis(path_reach_dis, s=1, title=path_reach_dis, max_y=0.0002)


########## draw_reachability_dis_cluster ########
# #     将dbscan的结果显示在optic的distance图上
# path_reach_dis = Global.pathOutput + 'order_objects.obj_AlgEucDisBaseOptics_rFanout=50.alpha=0.5.steepD=0.1.h=14.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=2.mpts=5.eps=1.0E-4.xi=1.0E-4.maxPNeiByte=2147483631'
# pathResultAlgEucFast = Global.pathOutput + 'result_ecu_fast_rFanout=50.alpha=0.5.steepD=0.1.h=14.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=2.mpts=5.eps=1.0E-4.xi=1.0E-4.maxPNeiByte=2147483631'
# Line.draw_reachability_dis_base(path_reach_dis, pathResultAlgEucFast, s=1, title=pathResultAlgEucFast, max_y=0.0002)


# #     将optic的结果显示在distance图上
# path_reach_dis = Global.pathOutput + 'order_objects.obj_AlgEucDisBaseOpticsWu_rFanout=50.alpha=0.5.steepD=0.1.h=14.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=2.mpts=5.eps=1.0E-4.xi=1.0E-4.maxPNeiByte=2147483631'
# pathResultAlgEucAdvancedOpticsWu = Global.pathOutput + 'result_ecu_base_optics_rFanout=50.alpha=0.5.steepD=0.1.h=14.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=2.mpts=5.eps=1.0E-4.xi=1.0E-4.maxPNeiByte=2147483631'
# Line.draw_reachability_dis_cluster(path_reach_dis, pathResultAlgEucAdvancedOpticsWu, s=1, title=pathResultAlgEucAdvancedOpticsWu, max_y=0.0002)
#

# #     将optic_wu的结果显示在distance图上
# path_reach_dis = Global.pathOutput + 'order_objects.obj_AlgEucDisBaseOpticsWu_rFanout=50.alpha=0.5.steepD=0.1.h=14.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=2.mpts=5.eps=1.0E-4.xi=1.0E-4.maxPNeiByte=2147483631'
# pathResultAlgEucAdvancedOpticsWu = Global.pathOutput + 'result_ecu_base_optics_wu_rFanout=50.alpha=0.5.steepD=0.1.h=14.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=2.mpts=5.eps=1.0E-4.xi=1.0E-4.maxPNeiByte=2147483631'
# Line.draw_reachability_dis_cluster(path_reach_dis, pathResultAlgEucAdvancedOpticsWu, s=1, title=pathResultAlgEucAdvancedOpticsWu, max_y=0.0002)


########## draw k nearest distance ########
# k_paths = []
# k_paths.append(Global.pathOutput + 'KNNNeighborDis_3.txt')
# k_paths.append(Global.pathOutput + 'KNNNeighborDis_5.txt')
# k_paths.append(Global.pathOutput + 'KNNNeighborDis_10.txt')
# k_paths.append(Global.pathOutput + 'KNNNeighborDis_50.txt')
# k_paths.append(Global.pathOutput + 'KNNNeighborDis_100.txt')
# Scatter.draw_k_nearest_distance(k_paths, s=1, title="KNNNeighborDis")







































#######################  经纬suffixFile = ([-125.0, 28.0], [15.0, 60.0]) 的测试code  ###########################
########## draw coordinate data ##############
# pathCoord = Global.pathCoord
# pathCoord = Global.pathCoord + '([-125.0, 28.0], [15.0, 60.0])'
# Scatter.draw_orginal_coord(pathCoord, s=20, show=False)
# pathCoord = Global.pathCoord + '([-125.0, 28.0], [15.0, 60.0])[normalized]'
# Scatter.draw_orginal_coord(pathCoord, s=20, show=False)

########## draw result data ###############
# pathResultAlgEucBase = Global.pathOutput + 'result_ecu_base.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucBase, s=10, show=True, title=pathResultAlgEucBase)
# pathResultAlgEucFast = Global.pathOutput + 'result_ecu_fast.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucFast, s=10, show=True, title=pathResultAlgEucFast)
# pathResultAlgEucBaseOptics = Global.pathOutput + 'result_ecu_base_optics.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucBaseOptics, s=10, show=True, title=pathResultAlgEucBaseOptics)
# pathResultAlgEucAdvancedOptics = Global.pathOutput + 'result_ecu_advanced_optics.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucAdvancedOptics, s=10, show=True, title=pathResultAlgEucAdvancedOptics)
# pathResultAlgEucAdvancedOpticsWu = Global.pathOutput + 'result_ecu_advanced_optics_wu.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucAdvancedOpticsWu, s=10, show=True, title=pathResultAlgEucAdvancedOpticsWu)

########## draw k nearest distance ########
# path_k_nearest = Global.pathOutput + '3_neighbor_dis.txt'
# Scatter.draw_k_nearest_distance(path_k_nearest, s=1, title=path_k_nearest)
# path_k_nearest = Global.pathOutput + '4_neighbor_dis.txt'
# Scatter.draw_k_nearest_distance(path_k_nearest, s=1, title=path_k_nearest)
# path_k_nearest = Global.pathOutput + '5_neighbor_dis.txt'
# Scatter.draw_k_nearest_distance(path_k_nearest, s=1, title=path_k_nearest)
# path_k_nearest = Global.pathOutput + '20_neighbor_dis.txt'
# Scatter.draw_k_nearest_distance(path_k_nearest, s=1, title=path_k_nearest)

########## draw_reachability_dis ########
# path_reach_dis = Global.pathOutput + 'order_objects.obj([-125.0, 28.0], [15.0, 60.0])_AlgEucDisBaseOptics'
# Line.draw_reachability_dis(path_reach_dis, s=1, title=path_reach_dis, max_y=0.004)

########## draw_reachability_dis_cluster ########
# path_reach_dis = Global.pathOutput + 'order_objects.obj([-125.0, 28.0], [15.0, 60.0])_AlgEucDisAdvancedOpticsWu'
# pathResultAlgEucFast = Global.pathOutput + 'result_ecu_fast.txt'
# Line.draw_reachability_dis_base(path_reach_dis, pathResultAlgEucFast, s=1, title=pathResultAlgEucFast, max_y=0.004)

# path_reach_dis = Global.pathOutput + 'order_objects.obj([-125.0, 28.0], [15.0, 60.0])_AlgEucDisAdvancedOptics'
# pathResultAlgEucAdvancedOptics = Global.pathOutput + 'result_ecu_advanced_optics.txt'
# Line.draw_reachability_dis_cluster(path_reach_dis, pathResultAlgEucAdvancedOptics, s=1, title=path_reach_dis, max_y=0.004)

# path_reach_dis = Global.pathOutput + 'order_objects.obj([-125.0, 28.0], [15.0, 60.0])_AlgEucDisAdvancedOpticsWu'
# pathResultAlgEucAdvancedOpticsWu = Global.pathOutput + 'result_ecu_advanced_optics_wu.txt'
# Line.draw_reachability_dis_cluster(path_reach_dis, pathResultAlgEucAdvancedOpticsWu, s=1, title=pathResultAlgEucAdvancedOpticsWu, max_y=0.004)


plt.pause(40000)
