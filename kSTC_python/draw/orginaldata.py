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


# pathCoord = Global.pathCoord
# pathCoord = Global.pathCoord + '([-125.0, 28.0], [15.0, 60.0])'
pathCoord = Global.pathCoord + '([-125.0, 28.0], [15.0, 60.0])[normalized]'
# Scatter.draw_orginal_coord(pathCoord, s=20, show=False)


pathResultAlgEucBase = Global.pathOutput + 'result_ecu_base.txt'
pathResultAlgEucFast = Global.pathOutput + 'result_ecu_fast.txt'
Scatter.draw_result(pathCoord, pathResultAlgEucBase, s=10, show=True, title=pathResultAlgEucBase)
Scatter.draw_result(pathCoord, pathResultAlgEucFast, s=10, show=True, title=pathResultAlgEucFast)



plt.pause(1200)
