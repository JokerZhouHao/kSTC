import matplotlib.pyplot as plt
from matplotlib.patches import Ellipse, Circle
from utility.io import IterableReader
from utility.io import Global
from utility import myplt
from matplotlib import interactive
import numpy as np
import random

# fig = plt.figure(random.randint(1, 10000), figsize=(10.1023, 6.5), tight_layout=True)
fig = plt.figure()
ax = fig.add_subplot(111)

xs = []
for i in  range(0, 9):
    xs.append(i * 0.1)
ys = xs

ax.set_xlim(xs[0], xs[len(xs) - 1])
ax.set_xticks(xs)
ax.set_ylim(ys[0], ys[len(ys) - 1])
ax.set_yticks(ys)
ax.grid()

cir = Circle(xy = (0.35, 0.47), radius=0.15, alpha=0.5)
# ax.add_patch(cir)
ax.add_patch(cir)


plt.show()
