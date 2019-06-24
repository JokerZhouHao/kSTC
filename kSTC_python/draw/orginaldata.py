import matplotlib.pyplot as plt
from utility.io import IterableReader
from utility.io import Global
from utility import myplt
from matplotlib import interactive
import numpy as np
import random
import matplotlib.image as img

import matplotlib.cm as cm
import matplotlib.mlab as mlab

class Scatter:
    colors = ['#6b8ba4', 'b', '#7f2b0a', '#e50000', '#ffcfdc', '#06470c', '#15b01a', '#c7fdb5', '#516572', '#6b8ba4', '#a2cffe', '#e6daa6']
    markers = ['4', 's', '^', '+', '.']

    index_marker = 0
    index_colors = -1

    def __init__(self, fig=None, xs=None, ys=None, xlim=None, ylim=None, title='title', pathBgImg = None):
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
            self.ys = ys;

        self.xlim = xlim
        self.ylim = ylim
        self.pathBgImg = pathBgImg

        plt.rcParams['font.size'] = 20
        if fig is None:
            self.ax = self.fig.add_subplot(111)


    def draw_scatter(self, points, s=1, marker='o', c=None):
        # self.ax.scatter(points[0], points[1], s=s, marker=marker, c=(random.uniform(0, 1), random.uniform(0, 1), random.uniform(0, 1)))
        Scatter.index_colors = Scatter.index_colors + 1
        # self.ax.scatter(points[0], points[1], s=s, marker=marker, c = Scatter.colors[Scatter.index_colors])
        if c is None:
            self.ax.scatter(points[0], points[1], s=s, marker=marker)
        else:
            self.ax.scatter(points[0], points[1], s=s, marker=marker, c=c)

    def show(self):
        if self.xlim != None:
            self.ax.set_xlim(self.xlim)
        if self.ylim != None:
            self.ax.set_ylim(self.ylim)

        # self.ax.set_xlim(self.xs[0], self.xs[len(self.xs)-1])
        # self.ax.set_xticks(self.xs)
        # self.ax.set_ylim(self.ys[0], self.ys[len(self.ys)-1])
        # self.ax.set_yticks(self.ys)
        # self.ax.set_yscale('log')
        # self.ax.set_xscale('log')

        if self.pathBgImg != None:
            imgBg = plt.imread(pathBgImg)
            # self.ax.imshow(imgBg)
            # imshow(img, zorder=0, extent=[left, right, bottom, top])
            # plt.imshow(imgBg, zorder=0, extent=[0, 1, 0, 1])
            # plt.imshow(imgBg, interpolation='bilinear', cmap=cm.gray,
            #     origin='lower', extent=[0, 1, 0, 1])
            plt.imshow(imgBg, aspect='auto', extent=[0, 1, 0, 1])
            # plt.imshow(imgBg)
            # self.ax.figimage(imgBg)
            # self.ax.imshow(imgBg)

        interactive(True)
        plt.show()

    @staticmethod
    def marker():
        if Scatter.index_marker == len(Scatter.markers):
            Scatter.index_marker = 0
        marker = Scatter.markers[Scatter.index_marker]
        Scatter.index_marker += 1
        return marker

    @staticmethod
    def draw_orginal_coord(path, s=1, marker='o', show=True, title='title', pathBgImg = None, xlim=None, ylim=None):
        scatter = Scatter(title=title, xlim=xlim, ylim=ylim, pathBgImg = pathBgImg)

        allCoords = [[], []]
        reader = IterableReader(path)
        i = 0
        for line in reader:
            coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
            allCoords[0].append(float(coords[0]))
            allCoords[1].append(float(coords[1]))
            i = i + 1
        scatter.draw_scatter(allCoords, s=s, marker=marker, c='#AFEEEE')
        if show:
            scatter.show()
        return scatter

    @staticmethod
    def draw_result(all_coord_path, result_path, s=10, show=True, title='title', pathBgImg = None, xlim=None, ylim=None):
        # scatter = Scatter.draw_orginal_coord(all_coord_path, s=s, show=False, title=title, pathBgImg=pathBgImg, xlim=xlim, ylim=ylim)

        Scatter.index_marker = 0
        scatter = Scatter(title=title, xlim=xlim, ylim=ylim, pathBgImg = pathBgImg)

        allCoords = [[], []]
        centerCoords = [[], []]
        reader = IterableReader(result_path)

        for line in reader:
            if line.startswith('qParams'):
                coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
                centerCoords[0].append(float(coords[0]))
                centerCoords[1].append(float(coords[1]))
            elif line.startswith('Cluster'):
                scatter.draw_scatter(allCoords, s=s, marker=Scatter.marker())
                # scatter.draw_scatter(allCoords, s=s)
                allCoords[0].clear()
                allCoords[1].clear()
            elif line.startswith("cluster_num"):
                continue
            else:
                coords = line.split(Global.delimiterLevel1)[2].split(Global.delimiterSpace)
                allCoords[0].append(float(coords[0]))
                allCoords[1].append(float(coords[1]))
        scatter.draw_scatter(allCoords, s=s, marker=Scatter.marker())
        # scatter.draw_scatter(allCoords, s=s)

        scatter.draw_scatter(centerCoords, s=s, marker='*')

        if show:
            scatter.show()
        Scatter.index_colors = -1
        return scatter

    @staticmethod
    def draw_k_nearest_distance(file_path, s=10, show=True, title='title'):
        scatter = Scatter(title=title)

        allCoords = [[], []]
        reader = IterableReader(file_path)
        i = 1
        for line in reader:
                allCoords[0].append(i)
                coords = line.split(Global.delimiterLevel1)
                allCoords[1].append(float(coords[0]))
                i = i + 1
        scatter.draw_scatter(allCoords, s=s, marker='v')

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
    colors = ['#6b8ba4', 'b', '#7f2b0a', '#e50000', '#ffcfdc', '#06470c', '#15b01a', '#c7fdb5', '#516572', '#6b8ba4', '#a2cffe', '#e6daa6']
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
            if line.startswith('-1'):
                orderId = id_2_index[line.split(Global.delimiterLevel1)[1]]
                clusterCoords[0].append(allCoords[0][orderId])
                clusterCoords[1].append(allCoords[1][orderId])
        lines.draw_line(clusterCoords, s=s, c='red')
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
pathCoord = Global.pathCoord + '([-112.41,33.46],[-111.9,33.68])[normalized]'
# Scatter.draw_orginal_coord(pathCoord, s=20, show=False)

###########################  path ##########################################
############   ([-112.41,33.46],[-111.9,33.68])[normalized]  path ##########
# pathBgImg = Global.pathImgs + 'LonLat^-112.41,33.46 -111.90,33.68.png'
# pathBgImg = Global.pathImgs + 'LonLat^-112.41,33.46 -111.90,33.68[gray].png'
# pathCoord = Global.pathCoord + '([-112.41,33.46],[-111.9,33.68])[normalized]'

############   ([-112.41,33.46],[-111.9,33.68])[normalized]  path ##########
pathBgImg = None
pathBgImg = None
pathCoord = Global.pathCoord + '([-112.41,33.46],[-111.9,33.68])[normalized]'


########## draw result data ###############
##############   dbscan #############
# pathResultAlgEucBase = Global.pathOutput + 'result_ecu_base.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucBase, s=50, show=True, title=pathResultAlgEucBase, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])
# #
# pathResultAlgEucFast = Global.pathOutput + 'result_ecu_fast.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucFast, s=50, show=True, title=pathResultAlgEucFast, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])

##############   optic wu #############
pathResultAlgEucBaseOpticsWu = Global.pathOutput + 'result_ecu_base_optics_wu.txt'
Scatter.draw_result(pathCoord, pathResultAlgEucBaseOpticsWu, s=50, show=True, title=pathResultAlgEucBaseOpticsWu, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])
#
pathResultAlgEucAdvancedOpticsWu = Global.pathOutput + 'result_ecu_advanced_optics_wu.txt'
Scatter.draw_result(pathCoord, pathResultAlgEucAdvancedOpticsWu, s=50, show=True, title=pathResultAlgEucAdvancedOpticsWu, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])

##############   optic #############
# pathResultAlgEucBaseOptics = Global.pathOutput + 'result_ecu_base_optics.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucBaseOptics, s=50, show=True, title=pathResultAlgEucBaseOptics, pathBgImg=pathBgImg, xlim=[0, 1], ylim=[0, 1])

# pathResultAlgEucAdvancedOptics = Global.pathOutput + 'result_ecu_advanced_optics.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucAdvancedOptics, s=10, show=True, title=pathResultAlgEucAdvancedOptics)




########## draw_reachability_dis ########
# path_reach_dis = Global.pathOutput + 'order_objects.obj([-125.0, 28.0], [15.0, 60.0])_AlgEucDisBaseOptics'
# Line.draw_reachability_dis(path_reach_dis, s=1, title=path_reach_dis, max_y=0.004)

# path_reach_dis = Global.pathOutput + 'order_objects.obj([-112.41,33.46],[-111.9,33.68])_AlgEucDisBaseOpticsWu'
# Line.draw_reachability_dis(path_reach_dis, s=1, title=path_reach_dis, max_y=0.01)
#
# path_reach_dis = Global.pathOutput + 'order_objects.obj([-112.41,33.46],[-111.9,33.68])_AlgEucDisAdvancedOpticsWu'
# Line.draw_reachability_dis(path_reach_dis, s=1, title=path_reach_dis, max_y=0.01)


########## draw_reachability_dis_cluster ########
# path_reach_dis = Global.pathOutput + 'order_objects.obj([-112.41,33.46],[-111.9,33.68])_AlgEucDisAdvancedOpticsWu'
# pathResultAlgEucFast = Global.pathOutput + 'result_ecu_fast.txt'
# # 将base的结果显示在optic的distance图上
# Line.draw_reachability_dis_base(path_reach_dis, pathResultAlgEucFast, s=1, title=pathResultAlgEucFast, max_y=0.01)


# path_reach_dis = Global.pathOutput + 'order_objects.obj([-112.41,33.46],[-111.9,33.68])_AlgEucDisAdvancedOpticsWu'
# pathResultAlgEucAdvancedOpticsWu = Global.pathOutput + 'result_ecu_advanced_optics_wu.txt'
# # 将optic的结果显示在distance图上
# Line.draw_reachability_dis_cluster(path_reach_dis, pathResultAlgEucAdvancedOpticsWu, s=1, title=pathResultAlgEucAdvancedOpticsWu, max_y=0.01)





















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
