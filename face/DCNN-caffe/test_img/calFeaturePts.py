caffe_root = '/home/cmcc/caffe/python'
import sys
sys.path.insert(0, caffe_root)
import caffe
caffe.set_mode_gpu()
import cv2
import numpy as np
import timeit
caffe_net_root = "/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/"
caffe_model_level1 = "/model/_iter_1000000.caffemodel"
caffe_model_level23 = "/model/_iter_100000.caffemodel"
#initialize 23 network
#39 * 39
net_F1 = caffe.Net( caffe_net_root + "deploy_net1.prototxt",
                    caffe_net_root + "F1" + caffe_model_level1,
                    caffe.TEST)
#39 * 31
net_EN1 = caffe.Net( caffe_net_root + "deploy_net2.prototxt",
                    caffe_net_root + "EN1" + caffe_model_level1,
                    caffe.TEST)
net_NM1 = caffe.Net( caffe_net_root + "deploy_net2.prototxt",
                    caffe_net_root + "NM1" + caffe_model_level1,
                    caffe.TEST)
#15 * 15
net_LE21 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "LE21" + caffe_model_level23,
                    caffe.TEST)
net_LE22 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "LE22" + caffe_model_level23,
                    caffe.TEST)
net_LE31 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "LE31" + caffe_model_level23,
                    caffe.TEST)
net_LE32 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "LE32" + caffe_model_level23,
                    caffe.TEST)
net_RE21 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "RE21" + caffe_model_level23,
                    caffe.TEST)
net_RE22 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "RE22" + caffe_model_level23,
                    caffe.TEST)
net_RE31 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "RE31" + caffe_model_level23,
                    caffe.TEST)
net_RE32 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "RE32" + caffe_model_level23,
                    caffe.TEST)
net_LM21 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "LM21" + caffe_model_level23,
                    caffe.TEST)
net_LM22 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "LM22" + caffe_model_level23,
                    caffe.TEST)
net_LM31 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "LM31" + caffe_model_level23,
                    caffe.TEST)
net_LM32 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "LM32" + caffe_model_level23,
                    caffe.TEST)
net_RM21 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "RM21" + caffe_model_level23,
                    caffe.TEST)
net_RM22 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "RM22" + caffe_model_level23,
                    caffe.TEST)
net_RM31 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "RM31" + caffe_model_level23,
                    caffe.TEST)
net_RM32 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "RM32" + caffe_model_level23,
                    caffe.TEST)
net_N21 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "N21" + caffe_model_level23,
                    caffe.TEST)
net_N22 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "N22" + caffe_model_level23,
                    caffe.TEST)
net_N31 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "N31" + caffe_model_level23,
                    caffe.TEST)
net_N32 = caffe.Net( caffe_net_root + "deploy_net3.prototxt",
                    caffe_net_root + "N32" + caffe_model_level23,
                    caffe.TEST)
# find the face from the image (lt_x,lt_y)(rd_x,rd_y)
def cropface(img,lt_x,lt_y,rd_x,rd_y):
    mid_x = (lt_x + rd_x)/2
    mid_y = (lt_y + rd_y)/2
    if rd_x - lt_x > rd_y - lt_y:
        side = rd_x - lt_x
    else:
        side = rd_y - lt_y
    lt_x = mid_x - side/2.0
    lt_y = mid_y - side/2.0
    rd_x = mid_x + side/2.0
    rd_y = mid_y + side/2.0
    F1 = img[lt_y:rd_y,lt_x:rd_x]
    F1 = cv2.resize(F1,(39,39))
    EN1 = F1[0:31,0:39]
    NM1 = F1[8:39,0:39]
    return F1,EN1,NM1,side
def cropfaceOrgan(img,pts,rate):
    mid_x = pts[0]
    mid_y = pts[1]
    lt_x = mid_x - 39*rate
    lt_y = mid_y - 39*rate
    rd_x = mid_x + 39*rate
    rd_y = mid_y + 39*rate
    img = img[lt_y:rd_y,lt_x:rd_x]
    img = cv2.resize(img,(15,15))
    return img
#this net is designed for F1
def net1(image,net):
    transformer = caffe.io.Transformer({'data': net.blobs['data'].data.shape})
    transformer.set_transpose('data', (2, 0, 1))
    transformer.set_channel_swap('data', (2, 1, 0))
    net.blobs['data'].data[...] = transformer.preprocess('data', image)
    scores = net.forward()
    pts1 = (float(scores['ip2'][0][0]),float(scores['ip2'][0][1]))
    pts2 = (float(scores['ip2'][0][2]),float(scores['ip2'][0][3]))
    pts3 = (float(scores['ip2'][0][4]),float(scores['ip2'][0][5]))
    pts4 = (float(scores['ip2'][0][6]),float(scores['ip2'][0][7]))
    pts5 = (float(scores['ip2'][0][8]),float(scores['ip2'][0][9]))
    return pts1,pts2,pts3,pts4,pts5
#this net is designed for NM1 and EN1
def net2(image,net):
    transformer = caffe.io.Transformer({'data': net.blobs['data'].data.shape})
    transformer.set_transpose('data', (2, 0, 1))
    transformer.set_channel_swap('data', (2, 1, 0))
    net.blobs['data'].data[...] = transformer.preprocess('data', image)
    scores = net.forward()
    pts1 = (float(scores['ip2'][0][0]),float(scores['ip2'][0][1]))
    pts2 = (float(scores['ip2'][0][2]),float(scores['ip2'][0][3]))
    pts3 = (float(scores['ip2'][0][4]),float(scores['ip2'][0][5]))
    return pts1,pts2,pts3
#this net is designed for 21 22 31 32 net work
def net3(image,net):
    transformer = caffe.io.Transformer({'data': net.blobs['data'].data.shape})
    transformer.set_transpose('data', (2, 0, 1))
    transformer.set_channel_swap('data', (2, 1, 0))
    net.blobs['data'].data[...] = transformer.preprocess('data', image)
    scores = net.forward()
    pts1 = (float(scores['ip2'][0][0]),float(scores['ip2'][0][1]))
    return pts1
def calOriginalPts(point,rate,center):
    point = ((point[0] * 39 * rate * 2) / 15 + center[0] - 39 * rate,
             (point[1] * 39 * rate * 2) / 15 + center[1] - 39 * rate)
    return point
def calFeaturePoint(img,lt_x,lt_y,rd_x,rd_y):
     #set for Level1 input
    F1, EN1, NM1, side = cropface(img,lt_x,lt_y,rd_x,rd_y)
    
    F1_pts1,F1_pts2,F1_pts3,F1_pts4,F1_pts5 = net1(F1,net_F1)
    EN1_pts1,EN1_pts2,EN1_pts3              = net2(EN1,net_EN1)
    NM1_pts3,NM1_pts4,NM1_pts5              = net2(NM1,net_NM1)
    NM1_pts3 = (NM1_pts3[0], NM1_pts3[1] + 8.0)
    NM1_pts4 = (NM1_pts4[0], NM1_pts4[1] + 8.0)
    NM1_pts5 = (NM1_pts5[0], NM1_pts5[1] + 8.0)
    #set for Level2 input
    pts1 = ((EN1_pts1[0] + F1_pts1[0])/2.0,(EN1_pts1[1] + F1_pts1[1])/2.0)
    pts2 = ((EN1_pts2[0] + F1_pts2[0])/2.0,(EN1_pts2[1] + F1_pts2[1])/2.0)
    pts3 = ((F1_pts3[0] + NM1_pts3[0] + EN1_pts3[0])/3.0,
            (F1_pts3[1] + NM1_pts3[1] + EN1_pts3[1])/3.0)
    pts4 = ((NM1_pts4[0] + F1_pts4[0])/2.0,(NM1_pts4[1] + F1_pts4[1])/2.0)
    pts5 = ((NM1_pts5[0] + F1_pts5[0])/2.0,(NM1_pts5[1] + F1_pts5[1])/2.0)
   
    LE21 = cropfaceOrgan(F1,pts1,0.16)
    RE21 = cropfaceOrgan(F1,pts2,0.16)
    N21  = cropfaceOrgan(F1,pts3,0.16)
    LM21 = cropfaceOrgan(F1,pts4,0.16)
    RM21 = cropfaceOrgan(F1,pts5,0.16)
    LE22 = cropfaceOrgan(F1,pts1,0.18)
    RE22 = cropfaceOrgan(F1,pts2,0.18)
    N22  = cropfaceOrgan(F1,pts3,0.18)
    LM22 = cropfaceOrgan(F1,pts4,0.18)
    RM22 = cropfaceOrgan(F1,pts5,0.18)
 
    level21_pts1 = calOriginalPts(net3(LE21,net_LE21),0.16,pts1)
    level21_pts2 = calOriginalPts(net3(RE21,net_RE21),0.16,pts2)
    level21_pts3 = calOriginalPts(net3(N21 ,net_N21 ),0.16,pts3)
    level21_pts4 = calOriginalPts(net3(LM21,net_LM21),0.16,pts4)
    level21_pts5 = calOriginalPts(net3(RM21,net_RM21),0.16,pts5)
    
    level22_pts1 = calOriginalPts(net3(LE22,net_LE22),0.18,pts1)
    level22_pts2 = calOriginalPts(net3(RE22,net_RE22),0.18,pts2)
    level22_pts3 = calOriginalPts(net3(N22 ,net_N22 ),0.18,pts3)
    level22_pts4 = calOriginalPts(net3(LM22,net_LM22),0.18,pts4)
    level22_pts5 = calOriginalPts(net3(RM22,net_RM22),0.18,pts5)
     
    #set for level 3 input
    level2_pts1 = ((level21_pts1[0] + level22_pts1[0])/2.0 + (2*pts1[0]-level21_pts1[0] -level22_pts1[0])/2,
                   (level21_pts1[1] + level22_pts1[1])/2.0 + (2*pts1[1]-level21_pts1[1] -level22_pts1[1])/2)
    level2_pts2 = ((level21_pts2[0] + level22_pts2[0])/2.0 + (2*pts2[0]-level21_pts2[0] -level22_pts2[0])/2,
                   (level21_pts2[1] + level22_pts2[1])/2.0 + (2*pts2[1]-level21_pts2[1] -level22_pts2[1])/2)
    level2_pts3 = ((level21_pts3[0] + level22_pts3[0])/2.0 + (2*pts3[0]-level21_pts3[0] -level22_pts3[0])/2,
                   (level21_pts3[1] + level22_pts3[1])/2.0 + (2*pts3[1]-level21_pts3[1] -level22_pts3[1])/2)
    level2_pts4 = ((level21_pts4[0] + level22_pts4[0])/2.0 + (2*pts4[0]-level21_pts4[0] -level22_pts4[0])/2,
                   (level21_pts4[1] + level22_pts4[1])/2.0 + (2*pts5[1]-level21_pts4[1] -level22_pts4[1])/2)
    level2_pts5 = ((level21_pts5[0] + level22_pts5[0])/2.0 + (2*pts5[0]-level21_pts5[0] -level22_pts5[0])/2,
                   (level21_pts5[1] + level22_pts5[1])/2.0 + (2*pts5[1]-level21_pts5[1] -level22_pts5[1])/2)
    
    LE31 = cropfaceOrgan(F1,level2_pts1,0.11)
    RE31 = cropfaceOrgan(F1,level2_pts2,0.11)
    N31  = cropfaceOrgan(F1,level2_pts3,0.11)
    LM31 = cropfaceOrgan(F1,level2_pts4,0.11)
    RM31 = cropfaceOrgan(F1,level2_pts5,0.11)
    LE32 = cropfaceOrgan(F1,level2_pts1,0.12)
    RE32 = cropfaceOrgan(F1,level2_pts2,0.12)
    N32  = cropfaceOrgan(F1,level2_pts3,0.12)
    LM32 = cropfaceOrgan(F1,level2_pts4,0.12)
    RM32 = cropfaceOrgan(F1,level2_pts5,0.12)
    level31_pts1 = calOriginalPts(net3(LE31,net_LE31),0.11,level2_pts1)
    level31_pts2 = calOriginalPts(net3(RE31,net_RE31),0.11,level2_pts2)
    level31_pts3 = calOriginalPts(net3(N31 ,net_N31 ),0.11,level2_pts3)
    level31_pts4 = calOriginalPts(net3(LM31,net_LM31),0.11,level2_pts4)
    level31_pts5 = calOriginalPts(net3(RM31,net_LM31),0.11,level2_pts5)
    
    level32_pts1 = calOriginalPts(net3(LE32,net_LE32),0.12,level2_pts1)
    level32_pts2 = calOriginalPts(net3(RE32,net_RE32),0.12,level2_pts2)
    level32_pts3 = calOriginalPts(net3(N32 ,net_N32 ),0.12,level2_pts3)
    level32_pts4 = calOriginalPts(net3(LM32,net_LM32),0.12,level2_pts4)
    level32_pts5 = calOriginalPts(net3(RM32,net_LM32),0.12,level2_pts5)
    #final output:
    point = np.zeros((10,))
    point[0] = ((level31_pts1[0] + level32_pts1[0])/2.0 + (2*level2_pts1[0]-level31_pts1[0] -level32_pts1[0])/2.0)*(side/39.0)+ lt_x
    point[1] = ((level31_pts1[1] + level32_pts1[1])/2.0 + (2*level2_pts1[1]-level31_pts1[1] -level32_pts1[1])/2.0)*(side/39.0)+ lt_y
    point[2] = ((level31_pts2[0] + level32_pts2[0])/2.0 + (2*level2_pts2[0]-level31_pts2[0] -level32_pts2[0])/2.0)*(side/39.0)+ lt_x
    point[3] = ((level31_pts2[1] + level32_pts2[1])/2.0 + (2*level2_pts2[1]-level31_pts2[1] -level32_pts2[1])/2.0)*(side/39.0)+ lt_y
    point[4] = ((level31_pts3[0] + level32_pts3[0])/2.0 + (2*level2_pts3[0]-level31_pts3[0] -level32_pts3[0])/2.0)*(side/39.0)+ lt_x
    point[5] = ((level31_pts3[1] + level32_pts3[1])/2.0 + (2*level2_pts3[1]-level31_pts3[1] -level32_pts3[1])/2.0)*(side/39.0)+ lt_y
    point[6] = ((level31_pts4[0] + level32_pts4[0])/2.0 + (2*level2_pts4[0]-level31_pts4[0] -level32_pts4[0])/2.0)*(side/39.0)+ lt_x
    point[7] = ((level31_pts4[1] + level32_pts4[1])/2.0 + (2*level2_pts5[1]-level31_pts4[1] -level32_pts4[1])/2.0)*(side/39.0)+ lt_y
    point[8] = ((level31_pts5[0] + level32_pts5[0])/2.0 + (2*level2_pts5[0]-level31_pts5[0] -level32_pts5[0])/2.0)*(side/39.0)+ lt_x
    point[9] = ((level31_pts5[1] + level32_pts5[1])/2.0 + (2*level2_pts5[1]-level31_pts5[1] -level32_pts5[1])/2.0)*(side/39.0)+ lt_y
    
    return point
if __name__ == "__main__":
    filelist = "/home/cmcc/Desktop/DCNNCascade_pts/test_img/lfpw_test_249_bbox.txt"
    imagepath = "/home/cmcc/Desktop/DCNNCascade_pts/test_img/"
    savepath = "/home/cmcc/Desktop/DCNNCascade_pts/test_img/result/"
    fid = open(filelist,'r')
    lines = fid.readlines()
    fid.close()
    count = 1
    for line in lines:
        words = line.split()
        filename = imagepath + words[0]
        print filename
        img = cv2.imread(filename)/255.0
        lt_x = float(words[1])
        rd_x = float(words[2])
        lt_y = float(words[3])
        rd_y = float(words[4])
        point = calFeaturePoint(img,lt_x,lt_y,rd_x,rd_y)
        print timeit.timeit("calFeaturePoint","from __main__ import calFeaturePoint")
        img = cv2.imread(filename)
        for i in range(0,10,2):
            cv2.circle(img,(int(point[i]),int(point[i+1])),2,(255,255,255))
        cv2.imwrite(savepath + str(count) + ".jpg",img)
        count += 1
    
