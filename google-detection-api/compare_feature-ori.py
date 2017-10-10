import os,sys
import numpy as np
import cv2
from scipy import spatial


f1 = sys.argv[1]
f2 = sys.argv[2]
file_pc = open(f1)
file_snpe = open(f2)

feat_pc = np.fromfile(file_pc,np.float32)
feat_snpe = np.fromfile(file_snpe, np.float32)
# feat_snpe = np.reshape(feat_snpe, (33,33,48, 7))
# feat_snpe = np.transpose(feat_snpe, (1,0,2, 3))
# feat_snpe = np.reshape(feat_snpe, (52272*7))

image_num = 1

print len(feat_pc)/image_num
print len(feat_snpe)/image_num

feat_len = len(feat_snpe)/image_num

for i in range(0,image_num):
	# for j in range(0,7):
	l_pc = feat_pc[i*feat_len :(i+1)*feat_len]
	l_snpe = feat_snpe[i*feat_len :(i+1)*feat_len]

	l1 = sum(abs(l_pc - l_snpe))
	cos = 1 - spatial.distance.cosine(l_pc,l_snpe)
	l2 = spatial.distance.euclidean(l_pc,l_snpe)

	print i
	print '\tdistance:\tcos:',cos, '\tl1:',l1,  '\tl2:',l2
	
	# for f in l_pc:
	# 	print f,
	# for f in l_snpe:
	# 	print f,
	# print '\n'

	mean_pc = np.mean(l_pc)
	mean_snpe = np.mean(l_snpe)
	std_pc = np.std(l_pc)
	std_snpe = np.std(l_snpe)


	print '\tmean:',mean_pc,mean_snpe, mean_pc - mean_snpe 
	print '\tstd:',std_pc,std_snpe, std_pc - std_snpe
