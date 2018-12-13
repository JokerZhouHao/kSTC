import matplotlib.pyplot as plt
from utility.io import IterableReader
from utility.io import Global
from utility import myplt
from matplotlib import interactive
import numpy as np
import random

class Scatter:
    colors = ['#6b8ba4', 'b', '#7f2b0a', '#e50000', '#ffcfdc', '#06470c', '#15b01a', '#c7fdb5', '#516572', '#6b8ba4', '#a2cffe', '#e6daa6']
    index_colors = -1

    def __init__(self, fig=None, xs=None, ys=None, title='title'):
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

        plt.rcParams['font.size'] = 20
        if fig is None:
            self.ax = self.fig.add_subplot(111)


    def draw_scatter(self, points, s=1, marker='o', c='r'):
        # self.ax.scatter(points[0], points[1], s=s, marker=marker, c=(random.uniform(0, 1), random.uniform(0, 1), random.uniform(0, 1)))
        Scatter.index_colors = Scatter.index_colors + 1
        self.ax.scatter(points[0], points[1], s=s, marker=marker, c = Scatter.colors[Scatter.index_colors])

    def show(self):
        # self.ax.set_xlim(self.xs[0], self.xs[len(self.xs)-1])
        # self.ax.set_xticks(self.xs)
        # self.ax.set_ylim(self.ys[0], self.ys[len(self.ys)-1])
        # self.ax.set_yticks(self.ys)
        # self.ax.set_yscale('log')
        # self.ax.set_xscale('log')
        interactive(True)
        plt.show()

    @staticmethod
    def draw_orginal_coord(path, s=1, marker='o', show=True, title='title'):
        scatter = Scatter(title=title)
        allCoords = [[], []]
        reader = IterableReader(path)
        i = 0
        for line in reader:
            coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
            allCoords[0].append(float(coords[0]))
            allCoords[1].append(float(coords[1]))
            i = i + 1
        scatter.draw_scatter(allCoords, s=s, marker=marker)
        if show:
            scatter.show()
        return scatter

    @staticmethod
    def draw_result(all_coord_path, result_path, s=10, show=True, title='title'):
        scatter = Scatter.draw_orginal_coord(all_coord_path, s=s, show=False, title=title)

        allCoords = [[], []]
        centerCoords = [[], []]
        reader = IterableReader(result_path)

        for line in reader:
            if line.startswith('qParams'):
                coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
                centerCoords[0].append(float(coords[0]))
                centerCoords[1].append(float(coords[1]))
            elif line.startswith('Cluster'):
                scatter.draw_scatter(allCoords, s=s, marker='v')
                allCoords[0].clear()
                allCoords[1].clear()
            else:
                coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
                allCoords[0].append(float(coords[0]))
                allCoords[1].append(float(coords[1]))
        scatter.draw_scatter(allCoords, s=s, marker='v')

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


    def draw_line(self, points, s=1, marker='o', c='b'):
        # self.ax.scatter(points[0], points[1], s=s, marker=marker, c=(random.uniform(0, 1), random.uniform(0, 1), random.uniform(0, 1)))
        # self.ax.plot(points[0], points[1], linewidth=s, c=c)
        y0 = []
        for i in range(0, len(points[0])):
            y0.append(0)
        self.ax.vlines(points[0], y0, points[1], linewidth=s)

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



########## draw orginal data ##############
# pathCoord = Global.pathCoord
# pathCoord = Global.pathCoord + '([-125.0, 28.0], [15.0, 60.0])'
pathCoord = Global.pathCoord + '([-125.0, 28.0], [15.0, 60.0])[normalized]'
# Scatter.draw_orginal_coord(pathCoord, s=20, show=False)


########## draw result data ###############
# pathResultAlgEucBase = Global.pathOutput + 'result_ecu_base.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucBase, s=10, show=True, title=pathResultAlgEucBase)
# pathResultAlgEucFast = Global.pathOutput + 'result_ecu_fast.txt'
# Scatter.draw_result(pathCoord, pathResultAlgEucFast, s=10, show=True, title=pathResultAlgEucFast)
pathResultAlgEucBaseOptics = Global.pathOutput + 'result_ecu_base_optics.txt'
Scatter.draw_result(pathCoord, pathResultAlgEucBaseOptics, s=10, show=True, title=pathResultAlgEucBaseOptics)

########## draw k nearest distance ########
path_k_nearest = Global.pathOutput + '3_neighbor_dis.txt'
# Scatter.draw_k_nearest_distance(path_k_nearest, s=1, title=path_k_nearest)
# path_k_nearest = Global.pathOutput + '4_neighbor_dis.txt'
# Scatter.draw_k_nearest_distance(path_k_nearest, s=1, title=path_k_nearest)
# path_k_nearest = Global.pathOutput + '5_neighbor_dis.txt'
# Scatter.draw_k_nearest_distance(path_k_nearest, s=1, title=path_k_nearest)
# path_k_nearest = Global.pathOutput + '20_neighbor_dis.txt'
# Scatter.draw_k_nearest_distance(path_k_nearest, s=1, title=path_k_nearest)


########## draw_reachability_dis ########
path_reach_dis = Global.pathOutput + 'order_objects.obj([-125.0, 28.0], [15.0, 60.0])'
Line.draw_reachability_dis(path_reach_dis, s=1, title=path_reach_dis, max_y=0.004)

plt.pause(1200)
