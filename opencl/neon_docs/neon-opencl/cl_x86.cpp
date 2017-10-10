/* ======================opencl

g++  -g  -o 1 cl_x86.cpp     -lOpenCL -IMali_OpenCL_SDK_v1.1.0/include   -std=c++11

https://software.intel.com/en-us/articles/opencl-drivers#ubuntu64

OpenCL™ Runtime for Intel® Core™ and Intel® Xeon® Processors

http://yuleiming.com/install-intel-opencl-on-ubuntu-14-04/
https://xinxiwang.wordpress.com/tag/linux/

Download sdk.

$wget -c http://registrationcenter-download.intel.com/akdlm/irc_nas/8555/intel_sdk_for_opencl_2016_ubuntu_6.0.0.1049_x64.tgz

Download code builder.

$wget -c http://registrationcenter.intel.com/irc_nas/5193/intel_code_builder_for_opencl_2015_ubuntu_5.0.0.43_x64.tgz

Install rpm package.

$sudo apt-get install -y rpm alien libnuma1

cd intel_code_builder_for_opencl_2015_ubuntu_5.0.0.43_x64

sudo rpm –import PUBLIC_KEY.PUB

cd rpm

fakeroot alien –to-deb opencl-1.2-base-5.0.0.43-1.x86_64.rpm

fakeroot alien –to-deb opencl-1.2-intel-cpu-5.0.0.43-1.x86_64.rpm

sudo dpkg -i opencl-1.2-base_5.0.0.43-2_amd64.deb

sudo dpkg -i opencl-1.2-intel-cpu_5.0.0.43-2_amd64.deb

sudo nano /etc/ld.so.conf.d/intelOpenCL.conf

/opt/intel/opencl-1.2-5.0.0.43/lib64

sudo ln /opt/intel/opencl-1.2-5.0.0.43/etc/intel64.icd /etc/OpenCL/vendors/intel64.icd

sudo ldconfig

 

cd intel_sdk_for_opencl_2016_ubuntu_6.0.0.1049_x64

$sudo ./install.sh

Missing optional prerequisites

— Missing libraries

Press “Enter” to automatically install the missing libs

Then in your ~/.bashrc, add the following lines.

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/intel/opencl-1.2-5.0.0.43/lib64    

export PATH=$PATH:/opt/intel/intel-opencl-1.2-6.0.0.1049/opencl-sdk/include

Now, you should see your intel opencl for your cpu.

sudo clinfo | grep Intel
=============================*/









/*=====================Neon




https://github.com/thenifty/neon-guide

float32x4_t vmulq_n_f32(float32x4_t a, float32_t b);
float32x4_t v = { 1.0, 2.0, 3.0, 4.0 };
float32_t s = 3.0;
float32x4_t prod = vmulq_n_f32(ary1, s);
// => prod = { 3.0, 6.0, 9.0, 12.0 }


http://codereview.stackexchange.com/questions/84327/bilinear-interpolation-using-neon-intrinsics

float32x4_t vmulq_f32(float32x4_t a, float32x4_t b); // VMUL.F32 q0,q0,q0
float32x4_t v1 = { 1.0, 2.0, 3.0, 4.0 }, v2 = { 1.0, 1.0, 1.0, 1.0 };
float32x4_t prod = vmulq_f32(v1, v2);
// => prod = { 1.0, 2.0, 3.0, 4.0 }

float32x4_t vmlsq_n_f32 (float32x4_t, float32x4_t, float32_t)

sudo apt-get install gcc-arm-linux-gnueabi
    sudo apt-get install g++-arm-linux-gnueabi
arm-linux-gnueabihf-g++ -mfpu=neon  -static -o neon_my neon_my   -std=c++11


g++  -mfpu=neon   -o 1 neon_example.c  -std=c++11

gcc -S -O3 -mcpu=cortex-a8 -mfpu=neon -ftree-vectorize -ftree-vectorizer-verbose=6 test.c

=========================== */
#include <stdint.h>
#include <stdio.h>
#include <assert.h>
#include <time.h>
#include <vector>
#include <random>
#include <climits>
#include <algorithm>
#include <functional>
#include <iostream>
#include <math.h>       /* floor */
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <CL/opencl.h>
#include <stdio.h>
  #include <stdlib.h>



size_t globalSize, localSize;
cl_int err;
 int w=640;
int h=480;
int LEN=w*h;
double  t0, t1, time_c, time_neon;






cl_platform_id cpPlatform;        // OpenCL 平台
cl_device_id device_id;           // device ID
cl_context context;               // context
cl_command_queue queue;           // command queue
cl_program program;               // program
cl_kernel kernel;                 // kernel



int *h_a;
int *h_b;
int *h_c;
int *h_c_cl;
cl_mem d_a;
cl_mem d_b;
cl_mem d_c;
size_t bytes = LEN*sizeof(int);




int *indBL1,*indBL2,*indBL3,*indBL4;
unsigned char ***interx;
unsigned char ***intery;
unsigned char **lookup[32];
unsigned char *src,*dst,*dst_cl;


cl_mem d_indBL1;
cl_mem d_indBL2;
cl_mem d_indBL3;
cl_mem d_indBL4;
cl_mem d_interx;
cl_mem d_intery;
cl_mem d_src;
cl_mem d_dst;




        // OpenCL kernel. Each work item takes care of one element of c
        const char *kernelSource =                                          "\n" \
            "__kernel void vecAdd(  __global float *a,           \n" \
            "                       __global float *b,                       \n" \
            "                       __global float *c,                       \n" \
            "                       const unsigned int n)                    \n" \
            "{                                                               \n" \
            "    //Get our global thread ID                                  \n" \
            "    int id = get_global_id(0);                                  \n" \
            "                                                                \n" \
            "    //Make sure we do not go out of bounds                      \n" \
            "    if (id < n)                                                 \n" \
            "        c[id] = a[id] + b[id];                                  \n" \
            "}                                                               \n" \
            "\n" ;





        void vecAdd_cl(int *a , int *b, int *c,  const unsigned int n) {

            err = clSetKernelArg(kernel, 0, sizeof(cl_mem), &d_a);
            err = clSetKernelArg(kernel, 1, sizeof(cl_mem), &d_b);
            err = clSetKernelArg(kernel, 2, sizeof(cl_mem), &d_c);
            err = clSetKernelArg(kernel, 3, sizeof(int), &LEN);
            err = clEnqueueNDRangeKernel(queue, kernel, 1, NULL, &globalSize, &localSize,
                0, NULL, NULL);
            clFinish(queue);
            clEnqueueReadBuffer(queue, d_c, CL_TRUE, 0, bytes, h_c_cl, 0, NULL, NULL );
             return;

        }








//#include <inttypes.h>
/* return current time in milliseconds */
static double
now_ms(void)
{
    struct timespec res;
    clock_gettime(CLOCK_REALTIME, &res);
    return 1000.0*res.tv_sec + (double)res.tv_nsec/1e6;
}


void remap(const unsigned char *src, unsigned char *dst, const int *idx1, const int *idx2, const int *idx3, const int *idx4, unsigned char ***interx, unsigned char ***intery, const int w, const int h) {

	int i;
	int all=w*h;
	for(i=0;i<all;++i) {
		
		unsigned char p1=src[idx1[i]];
		unsigned char p2=src[idx2[i]];
		unsigned char p3=src[idx3[i]];
		unsigned char p4=src[idx4[i]];
		unsigned char **q1=interx[i]; 
		unsigned char **q2=intery[i]; 
		unsigned char r1=q1[p1][p2];
		unsigned char r2=q1[p3][p4];
		dst[i]=q2[r1][r2];

	}

}





void remap_cl(unsigned char *src, unsigned char *dst_cl, int *indBL1, int *indBL2, int *indBL3, int *indBL4, unsigned char ***interx, unsigned char ***intery, int w, int h) {


    err = clEnqueueWriteBuffer(queue, d_indBL1, CL_TRUE, 0, LEN*sizeof(int), indBL1, 0, NULL, NULL);
    err = clEnqueueWriteBuffer(queue, d_indBL2, CL_TRUE, 0, LEN*sizeof(int), indBL2, 0, NULL, NULL);
    err = clEnqueueWriteBuffer(queue, d_indBL3, CL_TRUE, 0, LEN*sizeof(int), indBL3, 0, NULL, NULL);
    err = clEnqueueWriteBuffer(queue, d_indBL4, CL_TRUE, 0, LEN*sizeof(int), indBL4, 0, NULL, NULL);
    err = clEnqueueWriteBuffer(queue,  d_interx, CL_TRUE, 0, LEN*sizeof(unsigned char **), interx, 0, NULL, NULL);
    err = clEnqueueWriteBuffer(queue,  d_intery, CL_TRUE, 0, LEN*sizeof(unsigned char **), intery, 0, NULL, NULL);
    err = clEnqueueWriteBuffer(queue,  d_src, CL_TRUE, 0, LEN*sizeof(unsigned char), src, 0, NULL, NULL);


    err = clSetKernelArg(kernel, 0, sizeof(cl_mem), &d_src);
    std::cout <<"clSetKernelArg 0............."<<err<<std::endl; 

	err = clSetKernelArg(kernel, 1, sizeof(cl_mem), &d_dst);
	 std::cout <<"clSetKernelArg 1............."<<err<<std::endl;
    err = clSetKernelArg(kernel, 2, sizeof(cl_mem), &d_indBL1);
 std::cout <<"clSetKernelArg 2............."<<err<<std::endl;
    err = clSetKernelArg(kernel, 3, sizeof(cl_mem), &d_indBL2);
 std::cout <<"clSetKernelArg 3............."<<err<<std::endl;
    err = clSetKernelArg(kernel, 4, sizeof(cl_mem), &d_indBL3);
 std::cout <<"clSetKernelArg 4............."<<err<<std::endl;
    err = clSetKernelArg(kernel, 5, sizeof(cl_mem), &d_indBL4);
 std::cout <<"clSetKernelArg 5............."<<err<<std::endl;
 err = clSetKernelArg(kernel, 6, sizeof(cl_mem), &d_interx);
 std::cout <<"clSetKernelArg 6............."<<err<<std::endl;
 err = clSetKernelArg(kernel, 7, sizeof(cl_mem), &d_intery);
 std::cout <<"clSetKernelArg 7............."<<err<<std::endl;

    err = clEnqueueNDRangeKernel(queue, kernel, 1, NULL, &globalSize, &localSize, 0, NULL, NULL);
 std::cout <<"clEnqueueNDRangeKernel............."<<err<<std::endl;
   err= clFinish(queue);

std::cout <<"clFinish(queue)............."<<err<<std::endl;
   err= clEnqueueReadBuffer(queue, d_dst, CL_TRUE, 0,LEN*sizeof(unsigned char), dst_cl, 0, NULL, NULL );
std::cout <<"clEnqueueReadBuffer............."<<err<<std::endl;
    return;

}





int main()
{
	


    { // cl env setup
    //localSize = 2;
    localSize = 64;	
    globalSize = (size_t)ceil(LEN/(float)localSize)*localSize;
    std::cout<<globalSize<<std::endl;
    err = clGetPlatformIDs(1, &cpPlatform, NULL);
    err = clGetDeviceIDs(cpPlatform, CL_DEVICE_TYPE_CPU, 1, &device_id, NULL);
    context = clCreateContext(0, 1, &device_id, NULL, NULL, &err);
    queue = clCreateCommandQueue(context, device_id, 0, &err);




    }






     //（为每个向量分配内存）
     h_a = (int*)malloc(bytes);
     h_b = (int*)malloc(bytes);
     h_c = (int*)malloc(bytes);
     h_c_cl = (int*)malloc(bytes);

     //（初始化向量）
     int i;
     for( i = 0; i < LEN; i++ )
     {
         h_a[i] = i;
         h_b[i] = i;
         h_c[i]=h_a[i]+h_b[i];
         h_c_cl[i] =0;
     }


     {
          program = clCreateProgramWithSource(context, 1, (const char **) & kernelSource, NULL, &err);
           clBuildProgram(program, 0, NULL, NULL, NULL, NULL);
           kernel = clCreateKernel(program, "vecAdd", &err);
           d_a = clCreateBuffer(context, CL_MEM_READ_ONLY, bytes, NULL, NULL);
           d_b = clCreateBuffer(context, CL_MEM_READ_ONLY, bytes, NULL, NULL);
           d_c = clCreateBuffer(context, CL_MEM_WRITE_ONLY, bytes, NULL, NULL);
           err = clEnqueueWriteBuffer(queue, d_a, CL_TRUE, 0, bytes, h_a, 0, NULL, NULL);
          err = clEnqueueWriteBuffer(queue, d_b, CL_TRUE, 0, bytes, h_b, 0, NULL, NULL);
          t0 = now_ms();
          vecAdd_cl(h_a ,h_b, h_c,LEN);
            t1 = now_ms();
          time_neon= t1 - t0;
          printf(" vecAdd_cl  was %g  ms \n", time_neon);

     }


     /*for( i = 0; i < LEN; i++ )
     {
         if (h_c_cl [i] != h_c[i] ) {
             std::cout<<"fail    i="<<i<<"      h_c[i]="<< h_c[i]<<"     h_c_cl [i]="<<h_c_cl[i]<<std::endl;
         } else {
                std::cout<<"pass   i="<<i<<"      h_c[i]="<< h_c[i]<<"     h_c_cl [i]="<<h_c_cl[i]<<std::endl;
         }
     }
 */


    //=========================================================




     indBL1=new int[w*h];
     indBL2=new int[w*h];
     indBL3=new int[w*h];
     indBL4=new int[w*h];
     src=new unsigned char[w*h];
     dst=new unsigned char[w*h];
	dst_cl=new unsigned char[LEN];
     interx=new unsigned char**[w*h];
     intery=new unsigned char**[w*h];


     for( int i = 0; i < LEN; i++ )
     {
	indBL1[i] = i;
	indBL2[i] = i;
	indBL3[i] = i;
	indBL4[i] = i;	
	src[i]='a';	
	dst[i]='a'; 
	dst_cl[i]='b';

     }



     for (int i=0;i<32;i++) {
         lookup[i]=new unsigned char*[256];
         for (int j=0;j<256;j++) {
             lookup[i][j]=new unsigned char[256];
         }

     }

     for (int i=0;i<32;i++) {
         for (int j=0;j<256;j++) {
             for (int k=0;k<256;k++) {
                 lookup[i][j][k]=(unsigned char)((i*k +(32 -i)*j) /32);

             }

         }

     }


      for (int i=0;i<h;i++) {
                 for (int j=0;j<w;j++) {
             interx[i*w+j]=lookup[1];
             intery[i*w+j]=lookup[2];

                 }

         }




{

	t0 = now_ms();
	remap(src, dst, indBL1,indBL2,indBL3,indBL4,interx,intery,640,480);
	t1 = now_ms();
	time_neon= t1 - t0;
	printf(" remap  was %g  ms \n", time_neon);
}



  {

      FILE *fp;
      char fileName[] = "./remap.cl";
      char *source_str;
      size_t source_size;
       #define MAX_SOURCE_SIZE (0x100000)
      /* Load the source code containing the kernel*/
      fp = fopen(fileName, "r");
      if (!fp) {
      fprintf(stderr, "Failed to load kernel.\n");
      exit(1);
      }
      source_str = (char*)malloc(MAX_SOURCE_SIZE);
      source_size = fread(source_str, 1, MAX_SOURCE_SIZE, fp);
      fclose(fp);



      /* Create Kernel Program from the source */
      program = clCreateProgramWithSource(context, 1, (const char **)&source_str, (const size_t *)&source_size, &err);
      err=clBuildProgram(program, 0, NULL, NULL, NULL, NULL);
      kernel = clCreateKernel(program, "remap_cl", &err);

      std::cout <<"kernel sucess .............."<<err<<std::endl;
    d_indBL1= clCreateBuffer(context, CL_MEM_READ_ONLY, LEN*sizeof(int), NULL, NULL);
    d_indBL2= clCreateBuffer(context, CL_MEM_READ_ONLY, LEN*sizeof(int), NULL, NULL);
    d_indBL3= clCreateBuffer(context, CL_MEM_READ_ONLY, LEN*sizeof(int), NULL, NULL);
    d_indBL4= clCreateBuffer(context, CL_MEM_READ_ONLY,LEN*sizeof(int), NULL, NULL);
    d_interx= clCreateBuffer(context, CL_MEM_READ_ONLY, LEN*sizeof(unsigned char **), NULL, NULL);
    d_intery= clCreateBuffer(context, CL_MEM_READ_ONLY, LEN*sizeof(unsigned char **), NULL, NULL);
    d_src    = clCreateBuffer(context, CL_MEM_READ_ONLY, LEN*sizeof(unsigned char), NULL, NULL);
    d_dst= clCreateBuffer(context, CL_MEM_WRITE_ONLY, LEN*sizeof(unsigned char), NULL, NULL);

     t0 = now_ms();
      remap_cl(src, dst_cl, indBL1,indBL2,indBL3,indBL4,interx,intery,640,480);
      t1 = now_ms();
      time_neon= t1 - t0;
      printf(" remap_cl  was %g  ms \n", time_neon);


}


/*


     for( i = 0; i < LEN; i++ )
     {
       
	  
	if (dst[i] != dst_cl[i] ) {
             std::cout<<"fail===";
         } else {
		std::cout<<"pass====";

         };

         std::cout<<"i="<<i<<"    dst[i]="<<dst[i]<<"     dst_cl[i]="<<dst_cl[i]<<std::endl;
     }


*/



     ///////////////////////////////////////////////////////////
        clReleaseMemObject(d_a);
        clReleaseMemObject(d_b);
        clReleaseMemObject(d_c);
	clReleaseMemObject(d_indBL1);
        clReleaseMemObject(d_indBL2);
        clReleaseMemObject(d_indBL3);
	clReleaseMemObject(d_indBL4);
        clReleaseMemObject(d_interx);
        clReleaseMemObject(d_intery);
        clReleaseMemObject(d_src);
        clReleaseMemObject(d_dst);




        clReleaseProgram(program);
        clReleaseKernel(kernel);
        clReleaseCommandQueue(queue);
        clReleaseContext(context);
        


	free(indBL1);
        free(indBL2);
        free(indBL3);
	free(indBL4);
        free(src);
        free(dst);
	free(dst_cl);

	 for (int i=0;i<32;i++) {
         for (int j=0;j<256;j++) {
             free(lookup[i][j]);
         }};


	 for (int i=0;i<32;i++) {
          free(lookup[i]);

        };

	free(intery);
	free(interx);       

        std::cout<<"++++++++++end++++++++++++=...."<<std::endl;
	return 0;

}
