def sample_res_path(dir, rFanout=50, alpha=0.5, steepD=0.1, h=15, om=1, oe='1.0E-4', ns=50, t=1, k=5000, nw=2, mpts=5,
                    eps='0.001', xi='0.001', maxPNeiByte=2147483631):
    # testSampleResultFile.SPBest.nwlen=1000000.mds=1000.t=0.ns=200.r=2.k=5.nw=5.wf=0
    fp = dir
    fp += 'rFanout=' + str(rFanout) + '.'
    fp += 'alpha=' + str(alpha) + '.'
    fp += 'steepD=' + str(steepD) + '.'
    fp += 'h=' + str(h) + '.'
    fp += 'om=' + str(om) + '.'
    fp += 'oe=' + str(oe) + '.'
    fp += 'ns=' + str(ns) + '.'
    fp += 't=' + str(t) + '.'
    fp += 'k=' + str(k) + '.'
    fp += 'nw=' + str(nw) + '.'
    fp += 'mpts=' + str(mpts) + '.'
    fp += 'eps=' + str(eps) + '.'
    fp += 'xi=' + str(xi) + '.'
    fp += 'maxPNeiByte=' + str(maxPNeiByte)
    return fp + '.csv'



def figure_path():
    return '..\\Data\\figures\\'

# base_dir = 'D:\\nowMask\\KnowledgeBase\\sample_result\\yago2s_single_date\\'
# print(sample_res_path(base_dir, 'SPBest', 1000, 4, 0, 200, 2, 5, 5, 3, 3))

# path = 'D:\\kSTC\\sample_result\\res\\008_测试adv1_2_3—ok\\'
# fp = sample_res_path(path)
#
# with open(fp) as  f:
#     f.readline()
