from utility import Global
from utility import PathUtility
from utility.io import IterableReader

class Data:

    def __init__(self, fp):
        self.fp = fp
        self.numSample = int(0)
        self.numRangeRtree = int(0)
        self.numOpticFastRange = int(0)
        self.numOpticLuceneRange = int(0)
        self.numCluster = int(0)
        self.timeTotal = int(0)

    def __str__(self):
        strs = ''
        strs += self.fp + '\n'
        strs += 'numSample numRangeRtree numOpticFastRange numOpticLuceneRange numCluster timeTotal\n'
        strs += '%-10s%-14s%-18s%-20s%-11s%-9s' % (self.numSample, self.numRangeRtree, self.numOpticFastRange,
                                                   self.numOpticLuceneRange, self.numCluster, self.timeTotal) + '\n'
        return strs

    @staticmethod
    def loadCsv(dir, rFanout=50, alpha=0.5, steepD=0.1, h=15, om=1, oe='1.0E-4', ns=50, t=1, k=5000, nw=2, mpts=5,
                    eps='0.001', xi='0.001', maxPNeiByte=2147483631):
        fp = PathUtility.sample_res_path(dir, rFanout, alpha, steepD, h, om, oe, ns, t, k, nw, mpts,eps, xi, maxPNeiByte)
        filename = fp[fp.rindex('res') + 4:]
        alldata = []
        reader = IterableReader(fp)
        index = 0
        for line in reader:
            if index == 0:
                index += 1
                continue
            strArr = line.split(',')
            data = Data(filename)
            data.numSample = 1
            data.numRangeRtree = int(strArr[7])
            data.numOpticFastRange = int(strArr[16])
            data.numOpticLuceneRange = int(strArr[18])
            data.timeTotal = int(strArr[24])
            data.numCluster = int(strArr[25])
            alldata.append(data)
        return alldata

    @staticmethod
    def getData(dir, rFanout=50, alpha=0.5, steepD=0.1, h=15, om=1, oe='1.0E-4', ns=50, t=1, k=5000, nw=2, mpts=5,
                eps='0.001', xi='0.001', maxPNeiByte=2147483631, numMinCluster = -1, timeMaxTotal = 1000000000):
        alldata = Data.loadCsv(dir, rFanout, alpha, steepD, h, om, oe, ns, t, k, nw, mpts, eps, xi, maxPNeiByte)
        res = Data(alldata[0].fp)
        for data in alldata:
            if data.numCluster < numMinCluster or data.timeTotal > timeMaxTotal:
                continue
            res.numSample += 1
            res.numRangeRtree += data.numRangeRtree
            res.numOpticFastRange += data.numOpticFastRange
            res.numOpticLuceneRange += data.numOpticLuceneRange
            res.numCluster += data.numCluster
            res.timeTotal += data.timeTotal

        res.numRangeRtree = (int)(res.numRangeRtree / res.numSample)
        res.numOpticFastRange = (int)(res.numOpticFastRange / res.numSample)
        res.numOpticLuceneRange = (int)(res.numOpticLuceneRange / res.numSample)
        res.numCluster = (int)(res.numCluster / res.numSample)
        res.timeTotal = (int)(res.timeTotal / res.numSample)
        return res


path = 'D:\\kSTC\\sample_result\\res\\008_测试adv1_2_3—ok\\'
print(Data.getData(path))
