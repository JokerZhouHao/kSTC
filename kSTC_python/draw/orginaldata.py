import matplotlib.pyplot as plt
from utility.io import IterableReader
from utility.io import Global
from utility import myplt

class OrginalDataDraw:

    @staticmethod
    def drawCoord(path):
        allCoords = [[], []]
        reader = IterableReader(path)
        i = 0
        for line in reader:
            coords = line.split(Global.delimiterLevel1)[1].split(Global.delimiterSpace)
            allCoords[0].append(float(coords[0]))
            allCoords[1].append(float(coords[1]))
            i = i + 1

        allCoordsCopy = [[], []]
        for i in range(0, len(allCoords[0]), 1):
            allCoordsCopy[0].append(allCoords[0][i])
            allCoordsCopy[1].append(allCoords[1][i])

        plt.scatter(allCoords[0], allCoords[1], s=1)

        plt.show()

# pathCoord = Global.pathCoord
# OrginalDataDraw.drawCoord(pathCoord)

pathCoord = Global.pathCoord + '([-125.0, 28.0], [15.0, 60.0])'
OrginalDataDraw.drawCoord(pathCoord)


