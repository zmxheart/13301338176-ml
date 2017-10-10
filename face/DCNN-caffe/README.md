# DCNN-caffe
## Introduction
This project is related to CVPR2013: [Deep Convolutional Network Cascade for Facial Point Detection](http://www.cv-foundation.org/openaccess/content_cvpr_2013/papers/Sun_Deep_Convolutional_Network_2013_CVPR_paper.pdf). In this paper, it focus on the structural design of individual networks and their combining strategies.

There are five facial points to be detected: left eye center (LE), right eye center (RE), nose tip (N), left mouth corner (LM), and right mouth corner (RM). The paper cascade three levels of convolutional networks to make `coarse-to-fine` prediction. At the first level, the paper employ three deep convolutional networks, F1, EN1, and NM1, whose input regions cover the whole face (F1), eyes and nose (EN1), nose and mouth (NM1). Each network simultaneously predicts multiple facial points. 

## Why Cascade? Why not VGG?
Speed is importent. VGG is accurate but not fast enough. Everyone would not love to stand in front of the camera for more than 1 second I think. That too stupid!!! This project can detect 40 image/second, and 60+ frame/second in video because no need for level one detection!

Cascade several network is a `Coarse-to-Fine` approach. It split a deep network to several shallow ones and calculate each one stage by stage. Cascade network can fast the speed without lossing accuracy!!!

And this project is a network cascade, not just layer cascade !!!


## Dependences
* [Caffe](http://caffe.berkeleyvision.org)
* [python 2.7](https://www.python.org)
* [opencv 2.4.13](http://opencv.org)

## What in the folder?
* `personal expression about paper work(Chinese)` : a brief introduction and interpretation about the paper
* `Sun_Deep_Convolutional_Network_2013_CVPR_paper`
* tools
 * `cropFace.py` : crop 39×39 or 39×31 face from image
 * `cropFaceOrgan.py` : crop 15×15 face organ from image
 * `show_face_key_points.py` : check out if cropface is correct yourself
 * `convert_imagelist_2_hdf5.py` : make caffe data type
 * `convert_imagelist_2_hdf5_organ.py` : make caffe data type
 * `draw_net.py` : caffe official tool, show the net work. eg. net.png
* test_img 
* train_img
 * org_data_img
 * preprocessed_img : F1 ... RM32 (23 net work)

## How to train your own model
In this part, I will use F1 model as an example.

The structure of deep convolutional network F1. Sizes of input, convolution, and max pooling layers are illustrated as follow.
![](https://github.com/CongWeilin/DCNN-caffe/blob/master/intro_img/intro2.png)

1. Prepare Train Img ( This part provide data link ): 
  Unzip the [train Img](https://pan.baidu.com/s/1bpwxqDT) to the `org_data_img` folder !!!
 
2. crop the images
  Open `./DCNN-caffe/tools/cropFace.py` , and you need to change `filelist``filelistesave``savePath` in line 83
  This tools will give you [2 imagelist and 2 folder contains size(39×39) images](https://pan.baidu.com/s/1nuYwxuD).
  Then use `show_face_key_points.py` to take a look whether these point are correct !!! 
  
3. make caffe data:
  Feature points detection can't use lmdb-datatype, since lmdb only contains 1 label which is used for face detection better. In this project, we use [HDF5](http://www.hdfgroup.org/HDF5/). Use `convert_imagelist_2_hdf5.py` to convert. 
  
4. Go to `./DCNN-caffe/train_img/preprocessed_img/F1/caffe/` and run `./train.sh`, you will get the caffemodel you want.

## What if I am lazy to train ?
Each folder's `model` contains a `caffemodel` which is I have trained before. The loss(Euclidean) of F1 is about 4, NM1 and EN1 is about 2 and others is about 0.002.

Prepare the [test Image](https://pan.baidu.com/s/1slcrYQL) and ran `calFeaturePts.py` in `test_img` folder you can take a look by yourself.

Result image is as follow:<br>
![](https://github.com/CongWeilin/DCNN-caffe/blob/master/test_img/result.jpg)
## Questions to ask :
Email: cong_weilin@qq.com.

## Thanks to:
Thanks for CMCC research Lab for high quality equipments.

Thanks for upperclassman's patient for answering my questions.

Thanks to Xiao CongCong's NeiNei😘.
