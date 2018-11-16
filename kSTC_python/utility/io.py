class Global:
    datasetType = 'yelp_academic_dataset_business'
    pathInput = 'D:\\kSTC\\Dataset\\' + datasetType + '\\input\\'
    pathOutput = 'D:\\kSTC\\Dataset\\' + datasetType + '\\output\\'

    pathName = pathInput + 'id_name.txt'
    pathCoord = pathInput + 'id_coord_longtitude_latitude.txt'
    pathText = pathInput + 'id_text.txt'

    delimiterLevel1 = ": "
    delimiterLevel2 = ","
    delimiterSpace = " "
    delimiterPound = "#"


class IterableReader:
    def __init__(self, path, skipDelimiterPound = True):
        self.fp = open(path)
        self.skipDelimiterPound = skipDelimiterPound

    def __iter__(self):
        return self

    def __next__(self):
        while True:
            line = self.fp.readline()
            if line == '':
                self.fp.close()
                raise StopIteration()
            else:
                if self.skipDelimiterPound and line[0] == Global.delimiterPound:
                    continue
                else:
                    return line[:-1]


# fp = open(Path.pathInput + 'sample.json')
# print(fp.readline())

# reader = IterableReader(Global.pathCoord, False)
# i = 0
# for line in reader:
#     print(line)
#     i = i + 1
#     if 10 == i:
#         break


