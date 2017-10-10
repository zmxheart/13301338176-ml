import numpy as np
import cv2

def face_prepare(filelist,fileout,savePath,w,h):
  
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
    	rate = side/w
        imcrop = im[lt_y:rd_y,lt_x:rd_x]
       
        point = np.zeros((6,))
        '''
        point[0]=(float(words[5])-lt_x)/rate
        point[1]=(float(words[6])-lt_y)/rate 
        point[2]=(float(words[7])-lt_x)/rate
        point[3]=(float(words[8])-lt_y)/rate 
	'''
        point[0]=(float(words[9])-lt_x)/rate 
        point[1]=(float(words[10])-lt_y)/rate - 8
        point[2]=(float(words[11])-lt_x)/rate 
        point[3]=(float(words[12])-lt_y)/rate - 8
        point[4]=(float(words[13])-lt_x)/rate 
        point[5]=(float(words[14])-lt_y)/rate - 8
        imcrop = cv2.resize(imcrop,(39,39))
	imcrop = imcrop[8:39,0:39]
        fid.write(str(count)+'.jpg')
        for i in range(0,6,2):
	    #cv2.circle(imcrop,(int(point[i]),int(point[i+1])), 1,(255,0,0))
            fid.write('\t'+str(point[i]))
            fid.write('\t'+str(point[i+1]))
	#cv2.imshow("test",imcrop)
	#cv2.waitKey(0)        
	fid.write('\n')  
        
       
        imcrop_flip = cv2.flip(imcrop,1)
        fid.write(str(count)+'_flip.jpg')
	'''      
        fid.write('\t'+str(w-point[2]-1))
        fid.write('\t'+str(point[3]))
        fid.write('\t'+str(w-point[0]-1))
        fid.write('\t'+str(point[1]))
	'''
        fid.write('\t'+str(w-point[0]-1))
        fid.write('\t'+str(point[1]))
        fid.write('\t'+str(w-point[4]-1))
        fid.write('\t'+str(point[5]))
        fid.write('\t'+str(w-point[2]-1))
        fid.write('\t'+str(point[3]))
        fid.write('\n') 
      
        cv2.imwrite(savePath+str(count)+'_flip.jpg',imcrop_flip)
        cv2.imwrite(savePath+str(count)+'.jpg',imcrop)
        count = count + 1
    fid.close()
  
        


if __name__ == "__main__":
    #train
    w = 39
    h = 31
    filelist="../train_img/org_data_img/trainImageList.txt"
    filelistesave = "../train_img/processed_img/NM1/train.list"
    savePath="../train_img/processed_img/NM1/train/"

    face_prepare(filelist,filelistesave,savePath,w,h)
    filelist="../train_img/org_data_img/testImageList.txt"
    filelistesave = "../train_img/processed_img/NM1/test.list"
    savePath="../train_img/processed_img/NM1/val/"
    face_prepare(filelist,filelistesave,savePath,w,h)

