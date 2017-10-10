 __kernel void remap_cl(__global unsigned char *src,__global unsigned char *dst,__global int  *idx1,__global int  *idx2,__global int *idx3,__global int  *idx4,__global unsigned char  *interx,__global unsigned char  *intery)
    {
	
	int id = get_global_id(0); 
 
	
        unsigned char p1 =src[idx1[id]]; 
	unsigned char p2=src[idx2[id]];
	unsigned char p3=src[idx3[id]];
        unsigned char p4=src[idx4[id]];
	unsigned char **q1=interx[id];  
	unsigned char **q2=intery[id];
	unsigned char r1=q1[p1][p2];
 	dst[id]='f';
	printf("%s = %d,%d\n", "*****crash r1,r2 ",p1,p2);
  	//dst[id]=q2[r1][r2]; // why crash?
	//printf("%s = %d,%d\n", "*****get_global_id ",id,dst[id]);


    }


/*unsigned char p1=src[idx1[i]];
		unsigned char p2=src[idx2[i]];
		unsigned char p3=src[idx3[i]];
		unsigned char p4=src[idx4[i]];
		unsigned char **q1=interx[i]; 
		unsigned char **q2=intery[i]; 
		unsigned char r1=q1[p1][p2];
		unsigned char r2=q2[p3][p4];
		dst[i]=q2[r1][r2];

*/
