import numpy as np
import cv2

def face_prepare(filelist,fileout,savePath,w):
  
    fid = open(filelist,'r')
    lines = fid.readlines()
    fid.close()
    
    fid = open(fileout,'w')
    
    count = 1
    for line in lines:
        words = line.split()
        filename = "../train_img/org_data_img/" + words[0]

	print filename
        im=cv2.imread(filename)
	lt_x = float(words[1])
        rd_x = float(words[2])
        lt_y = float(words[3])
        rd_y = float(words[4])
	if rd_x - lt_x > rd_y - lt_y:
		side = rd_x - lt_x
	else:
		side = rd_y - lt_y
	#read central pts1
	pts_x = float(words[7])
	pts_y = float(words[8])
	#crop box
        lt_x = pts_x - side * w
        rd_x = pts_x + side * w
        lt_y = pts_y - side * w
        rd_y = pts_y + side * w

        imcrop = im[lt_y:rd_y,lt_x:rd_x]
        
        imcrop = cv2.resize(imcrop,(15,15))

        fid.write(str(count)+'.jpg')
        fid.write('\t'+str(7.5))
        fid.write('\t'+str(7.5))
        fid.write('\n')  
        cv2.imwrite(savePath+str(count)+'.jpg',imcrop)

        #read central pts2
	pts_x = float(words[5])
	pts_y = float(words[6])
	#crop box
        lt_x = pts_x - side * w
        rd_x = pts_x + side * w
        lt_y = pts_y - side * w
        rd_y = pts_y + side * w

        imcrop = im[lt_y:rd_y,lt_x:rd_x]
        
        imcrop = cv2.resize(imcrop,(15,15))
        imcrop_flip = cv2.flip(imcrop,1)
	fid.write(str(count)+'_flip.jpg')
	fid.write('\t'+str(7.5))
        fid.write('\t'+str(7.5))
        fid.write('\n')  
      
        cv2.imwrite(savePath+str(count)+'_flip.jpg',imcrop_flip)
        
        count = count + 1
    fid.close()
  
        


if __name__ == "__main__":
    #train
    organ = "RE"

    w = 0.16
    filelist="../train_img/org_data_img/trainImageList.txt"
    filelistesave = "../train_img/processed_img/" + organ + "21/train.list"
    savePath="../train_img/processed_img/" + organ + "21/train/"
    face_prepare(filelist,filelistesave,savePath,w)
    filelist="../train_img/org_data_img/testImageList.txt"
    filelistesave = "../train_img/processed_img/" + organ + "21/test.list"
    savePath="../train_img/processed_img/" + organ + "21/val/"
    face_prepare(filelist,filelistesave,savePath,w)
    
    w = 0.18
    filelist="../train_img/org_data_img/trainImageList.txt"
    filelistesave = "../train_img/processed_img/" + organ + "22/train.list"
    savePath="../train_img/processed_img/" + organ + "22/train/"
    face_prepare(filelist,filelistesave,savePath,w)
    filelist="../train_img/org_data_img/testImageList.txt"
    filelistesave = "../train_img/processed_img/" + organ + "22/test.list"
    savePath="../train_img/processed_img/" + organ + "22/val/"
    face_prepare(filelist,filelistesave,savePath,w)

    w = 0.11
    filelist="../train_img/org_data_img/trainImageList.txt"
    filelistesave = "../train_img/processed_img/" + organ + "31/train.list"
    savePath="../train_img/processed_img/" + organ + "31/train/"
    face_prepare(filelist,filelistesave,savePath,w)
    filelist="../train_img/org_data_img/testImageList.txt"
    filelistesave = "../train_img/processed_img/" + organ + "31/test.list"
    savePath="../train_img/processed_img/" + organ + "31/val/"
    face_prepare(filelist,filelistesave,savePath,w)

    w = 0.12
    filelist="../train_img/org_data_img/trainImageList.txt"
    filelistesave = "../train_img/processed_img/" + organ + "32/train.list"
    savePath="../train_img/processed_img/" + organ + "32/train/"
    face_prepare(filelist,filelistesave,savePath,w)
    filelist="../train_img/org_data_img/testImageList.txt"
    filelistesave = "../train_img/processed_img/" + organ + "32/test.list"
    savePath="../train_img/processed_img/" + organ + "32/val/"
    face_prepare(filelist,filelistesave,savePath,w)

