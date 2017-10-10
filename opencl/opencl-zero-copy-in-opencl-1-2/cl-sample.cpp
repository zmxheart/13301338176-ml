// g++  -g  -o 1 cl-sample.cpp         -lOpenCL -IMali_OpenCL_SDK_v1.1.0/include   -std=c++11

//http://gameeer.com/2015/01/09/opencl-env/

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <CL/opencl.h>
#include <iostream>
// OpenCL kernel. Each work item takes care of one element of c
/* const char *kernelSource =                                          "\n" \  
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


*/

// OpenCL kernel. Each work item takes care of one element of c
const char *kernelSource =                                          "\n" \
    "__kernel void vecMul(  __global int *a,           \n" \
    "                       __global int *b,                       \n" \
    "                       __global int *c,                       \n" \
    "                       const unsigned int n)                    \n" \
    "{                                                               \n" \
    "    //Get our global thread ID                                  \n" \
    "    int id = get_global_id(0);                                  \n" \
    "                                                                \n" \
    "    //Make sure we do not go out of bounds                      \n" \
    "    if (id < n)                                                 \n" \
    "        c[id] = a[id] * b[id];                                  \n" \
    "}                                                               \n" \
    "\n" ;






int main( int argc, char* argv[] )  
{
    int len = 10;
    cl_mem d_a;
    cl_mem d_b;
    cl_mem d_c;
    cl_platform_id cpPlatform;        // OpenCL 平台
    cl_device_id device_id;           // device ID
    cl_context context;               // context
    cl_command_queue queue;           // command queue
    cl_program program;               // program
    cl_kernel kernel;                 // kernel
    size_t bytes = len*sizeof(int);
    /*h_a = (int*)malloc(bytes);
    h_b = (int*)malloc(bytes);
    h_c = (int*)malloc(bytes);*/
    size_t globalSize, localSize;
    cl_int err;
    localSize = 2;
    globalSize = (size_t)ceil(len/(float)localSize)*localSize;
    err = clGetPlatformIDs(1, &cpPlatform, NULL);
    err = clGetDeviceIDs(cpPlatform, CL_DEVICE_TYPE_CPU, 1, &device_id, NULL);
    context = clCreateContext(0, 1, &device_id, NULL, NULL, &err);
    queue = clCreateCommandQueue(context, device_id, 0, &err);
    program = clCreateProgramWithSource(context, 1, (const char **) & kernelSource, NULL, &err);
    clBuildProgram(program, 0, NULL, NULL, NULL, NULL);
    kernel = clCreateKernel(program, "vecMul", &err);
    d_a = clCreateBuffer(context, CL_MEM_READ_ONLY|CL_MEM_ALLOC_HOST_PTR, bytes, NULL, NULL);
    d_b = clCreateBuffer(context, CL_MEM_READ_ONLY|CL_MEM_ALLOC_HOST_PTR, bytes, NULL, NULL);
    d_c = clCreateBuffer(context, CL_MEM_WRITE_ONLY|CL_MEM_ALLOC_HOST_PTR, bytes, NULL, NULL);
    // （将向量信息写入设备缓冲）
    /*err = clEnqueueWriteBuffer(queue, d_a, CL_TRUE, 0,
        bytes, h_a, 0, NULL, NULL);
    err = clEnqueueWriteBuffer(queue, d_b, CL_TRUE, 0,
        bytes, h_b, 0, NULL, NULL);
    // （设置计算内核的参数）*/
    err = clSetKernelArg(kernel, 0, sizeof(cl_mem), &d_a);
    err = clSetKernelArg(kernel, 1, sizeof(cl_mem), &d_b);
    err = clSetKernelArg(kernel, 2, sizeof(cl_mem), &d_c);
    err = clSetKernelArg(kernel, 3, sizeof(int), &len);



int *mappedBuffer_a = NULL;
int *mappedBuffer_b = NULL;
int *mappedBuffer_c = NULL;

mappedBuffer_a = (int *)clEnqueueMapBuffer(queue, d_a, CL_TRUE, CL_MAP_WRITE, 0, bytes, 0, NULL, NULL, NULL);
mappedBuffer_b = (int *)clEnqueueMapBuffer(queue, d_b, CL_TRUE, CL_MAP_WRITE, 0, bytes, 0, NULL, NULL, NULL);
mappedBuffer_c = (int *)clEnqueueMapBuffer(queue, d_c, CL_TRUE, CL_MAP_READ, 0, bytes, 0, NULL, NULL, NULL);
clFinish(queue);

for (int j=1;j<10;j++) {

     for( int i = 0; i < len; i++ )
    {
        mappedBuffer_a[i] = j+1;
	mappedBuffer_b[i] = j;
    }

    err = clEnqueueNDRangeKernel(queue, kernel, 1, NULL, &globalSize, &localSize,0, NULL, NULL);
    clFinish(queue);
    /*clEnqueueReadBuffer(queue, d_c, CL_TRUE, 0,
        bytes, h_c, 0, NULL, NULL );
    float sum = 0;
    for(i=0; i<n; i++)
        printf("%d =====",h_c[i]);
    */

	 std::cout<<"+++c=(a*b)++++"<<std::endl;
         for( int i = 0; i < len; i++ )
    {
        std::cout<<mappedBuffer_c[i]<<"=("<< mappedBuffer_a[i]<<"*"<<mappedBuffer_b[i]<<") |  ";
    }
         std::cout<<"\n"<<"========"<<std::endl;


}


clEnqueueUnmapMemObject(queue, d_c, mappedBuffer_c, 0, NULL, NULL);
clEnqueueUnmapMemObject(queue, d_a, mappedBuffer_a, 0, NULL, NULL);
clEnqueueUnmapMemObject(queue, d_b, mappedBuffer_b, 0, NULL, NULL);
clFinish(queue);
    clReleaseMemObject(d_a);
    clReleaseMemObject(d_b);
    clReleaseMemObject(d_c);
    clReleaseProgram(program);
    clReleaseKernel(kernel);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);
    /*free(h_a);
    free(h_b);
    free(h_c);
    return 0;*/
}
