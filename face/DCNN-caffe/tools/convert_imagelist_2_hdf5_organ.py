import os
import random
import h5py
import numpy as np
from skimage import io
from skimage import transform as tf

def save2hdf5(X,Y,filename):
    with h5py.File(filename, 'w') as f:
        f['data'] = X
        f['label'] = Y
        print 'having saving a hdf5 file : '+ filename

def convert(source_path,pairlist,savepath,hdf5list,w,h):
    step = 5000

    fid=open(pairlist)
    lines= fid.readlines()
    fid.close()
    X=np.empty((step,3,w,h),dtype=np.float)
    Y=np.empty((step,2,1,1),dtype=np.float)
    i=0
    t=1

    random.shuffle(lines)
    for line in lines:
        words=line.split()
        inputimage=words[0]
        #image
        points = np.zeros((2,))
        
        points[0]=float(words[1])
        points[1]=float(words[2])
	'''
        points[2]=float(words[3])
        points[3]=float(words[4])
        points[4]=float(words[5])
        points[5]=float(words[6])
	      
	points[6]=float(words[7])
        points[7]=float(words[8])
        points[8]=float(words[9])
        points[9]=float(words[10])
        '''
        im=io.imread(source_path+inputimage)
        im=tf.resize(im,(w,h))
        
        X[i,0,:,:]=im[:,:,0]
        X[i,1,:,:]=im[:,:,1]
        X[i,2,:,:]=im[:,:,2]
        Y[i,:,0,0]=points
        i=i+1
        if i==step:
            filename = os.path.join(savepath, str(t)+ '.h5')
            save2hdf5(X,Y,filename)
            with open(os.path.join(savepath,hdf5list), 'a') as f:
                f.write(filename + '\n')
            i=0
            t=t+1
    if i > 0:
        filename = os.path.join(savepath, str(t)+ '.h5')
        save2hdf5(X[0:i,:,:,:],Y[0:i,:,:,:],filename)
        with open(os.path.join(savepath,hdf5list), 'a') as f:
            f.write(filename + '\n')

if __name__=='__main__':
    w=15
    h=15
    organ = "LE"
    #train
    source_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'21/train/'
    save_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'21/train_h5'
    hdf5list = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'21/train_hdf5.txt'
    filelist = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'21/train.list'
    convert(source_path,filelist,save_path,hdf5list,w,h)

    #validation
    source_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'21/val/'
    save_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'21/val_h5'
    hdf5list = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'21/validation_hdf5.txt'
    filelist = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'21/test.list'
    convert(source_path,filelist,save_path,hdf5list,w,h)
#train
    source_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'22/train/'
    save_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'22/train_h5'
    hdf5list = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'22/train_hdf5.txt'
    filelist = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'22/train.list'
    convert(source_path,filelist,save_path,hdf5list,w,h)

    #validation
    source_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'22/val/'
    save_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'22/val_h5'
    hdf5list = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'22/validation_hdf5.txt'
    filelist = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'22/test.list'
    convert(source_path,filelist,save_path,hdf5list,w,h)
'''
#train
    source_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'31/train/'
    save_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'31/train_h5'
    hdf5list = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'31/train_hdf5.txt'
    filelist = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'31/train.list'
    convert(source_path,filelist,save_path,hdf5list,w,h)

    #validation
    source_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'31/val/'
    save_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'31/val_h5'
    hdf5list = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'31/validation_hdf5.txt'
    filelist = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'31/test.list'
    convert(source_path,filelist,save_path,hdf5list,w,h)
#train
    source_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'32/train/'
    save_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'32/train_h5'
    hdf5list = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'32/train_hdf5.txt'
    filelist = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'32/train.list'
    convert(source_path,filelist,save_path,hdf5list,w,h)

    #validation
    source_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'32/val/'
    save_path = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'32/val_h5'
    hdf5list = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'32/validation_hdf5.txt'
    filelist = '/home/cmcc/Desktop/DCNNCascade_pts/train_img/processed_img/' + organ +'32/test.list'
    convert(source_path,filelist,save_path,hdf5list,w,h)
'''
