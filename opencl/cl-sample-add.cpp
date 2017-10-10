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



////////////////////////////////////////////////////////////////////////////////   
// Use a static data size for simplicity   
//   
#define IMAGE_X_PIXELS          176   
#define IMAGE_Y_PIXELS          144   
#define IMAGE_SOURCE1_LIMPID    0.5f   
#define IMAGE_SOURCE2_LIMPID    0.5f   

////////////////////////////////////////////////////////////////////////////////   

// Simple compute kernel which computes the square of an input array    
//   
const char *KernelSource1 = "\n" \
"#define IMAGE_Y_PIXELS   144                                           \n" \
"#define IMAGE_SOURCE1_LIMPID   0.5f                                    \n" \
"#define IMAGE_SOURCE2_LIMPID   0.5f                                    \n" \
"                                                                       \n" \
"__kernel void Limpid(                                                  \n" \
"   __global float image1[][IMAGE_Y_PIXELS],                            \n" \
"   __global float image2[][IMAGE_Y_PIXELS],                            \n" \
"   __global float output[][IMAGE_Y_PIXELS])                            \n" \
"{                                                                      \n" \
"   int x = get_global_id(0);                                           \n" \
"   int y = get_global_id(1);                                           \n" \
"   output[x][y] = image1[x][y] * IMAGE_SOURCE1_LIMPID + image2[x][y] * IMAGE_SOURCE2_LIMPID; \n" \
"}                                                                      \n" \
"\n";


////////////////////////////////////////////////////////////////////////////////  


int main( int argc, char* argv[] )  
{
    int len = 10;
    cl_mem d_a;
    cl_mem d_b;
    cl_mem d_c;
    cl_platform_id cpPlatform;        // OpenCL 平台
    cl_device_id device_id;           // device ID
    cl_context context,context1;               // context
    cl_command_queue queue,queue1;           // command queue
    cl_program program,program1;               // program
    cl_kernel kernel,kernel1;                 // kernel
    size_t bytes = len*sizeof(int);
    /*h_a = (int*)malloc(bytes);
    h_b = (int*)malloc(bytes);
    h_c = (int*)malloc(bytes);*/
    size_t globalSize, localSize;
    cl_int err;
    localSize = 2;
    globalSize = (size_t)ceil(len/(float)localSize)*localSize;
    





      
    float *image1, *image2;             // original data set given to device   
    float *results;                     // results returned from device   
    unsigned int correct;               // number of correct results returned   
  
    size_t global;                      // global domain size for our calculation   
    size_t local;                       // local domain size for our calculation   
    cl_mem input1, input2;              // device memory used for the input array   
    cl_mem output;                      // device memory used for the output array   
  
  
  
// Initialize the original data buffer and the result buffer   
    image1 = (float*)malloc(IMAGE_X_PIXELS * IMAGE_Y_PIXELS * sizeof(*image1));   
    image2 = (float*)malloc(IMAGE_X_PIXELS * IMAGE_Y_PIXELS * sizeof(*image2));   
    results = (float*)malloc(IMAGE_X_PIXELS * IMAGE_Y_PIXELS * sizeof(*results));   
    const unsigned int count = IMAGE_X_PIXELS * IMAGE_Y_PIXELS;   
  
// Automatically generate 2 images   
    for(int i = 0; i < count; i++) {   
        image1[i] = rand() / (float)RAND_MAX;   
        image2[i] = rand() / (float)RAND_MAX;   
    }   










    err = clGetPlatformIDs(1, &cpPlatform, NULL);
    err = clGetDeviceIDs(cpPlatform, CL_DEVICE_TYPE_CPU, 1, &device_id, NULL);
    context = clCreateContext(0, 1, &device_id, NULL, NULL, &err);
    context1 = clCreateContext(0, 1, &device_id, NULL, NULL, &err);

    queue = clCreateCommandQueue(context, device_id, 0, &err);
queue1 = clCreateCommandQueue(context1, device_id, 0, &err);
    program = clCreateProgramWithSource(context, 1, (const char **) & kernelSource, NULL, &err);
program1 = clCreateProgramWithSource(context1, 1, (const char **) & KernelSource1, NULL, &err);
    clBuildProgram(program, 0, NULL, NULL, NULL, NULL);
    clBuildProgram(program1, 0, NULL, NULL, NULL, NULL);
    kernel = clCreateKernel(program, "vecMul", &err);
    kernel1 = clCreateKernel(program1, "Limpid", &err);

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






// Create the input and output arrays in device memory for our calculation   
    //   
    input1 = clCreateBuffer(context1,  CL_MEM_READ_ONLY,  sizeof(float) * count, NULL, NULL);   
    input2 = clCreateBuffer(context1,  CL_MEM_READ_ONLY,  sizeof(float) * count, NULL, NULL);   
    output = clCreateBuffer(context1, CL_MEM_WRITE_ONLY, sizeof(float) * count, NULL, NULL);   
    if (!input1 || !input2 || !output)   
    {   
printf("Error: Failed to allocate device memory!\n");   
        exit(1);   
    }       
  
// Write our data set into the input array in device memory    
    //   
    err = clEnqueueWriteBuffer(queue1, input1, CL_TRUE, 0, sizeof(float) * count, image1, 0, NULL, NULL);   
    err |= clEnqueueWriteBuffer(queue1, input2, CL_TRUE, 0, sizeof(float) * count, image2, 0, NULL, NULL);   
    if (err != CL_SUCCESS)   
    {   
printf("Error: Failed to write to source array!\n");   
        exit(1);   
    }   
  
// Set the arguments to our compute kernel   
    //   
    err = 0;   
    err = clSetKernelArg(kernel1, 0, sizeof(cl_mem), &input1);   
    err |= clSetKernelArg(kernel1, 1, sizeof(cl_mem), &input2);   
    err |= clSetKernelArg(kernel1, 2, sizeof(cl_mem), &output);   
    if (err != CL_SUCCESS)   
    {   
printf("Error: Failed to set kernel arguments! %d\n", err);   
        exit(1);   
    }   
  
// Get the maximum work group size for executing the kernel on the device   
    //   
    err = clGetKernelWorkGroupInfo(kernel1, device_id, CL_KERNEL_WORK_GROUP_SIZE, sizeof(local), &local, NULL);   
    if (err != CL_SUCCESS)   
    {   
printf("Error: Failed to retrieve kernel work group info! %d\n", err);   
        exit(1);   
    }   
    else   
printf("The number of work items in a work group is: %lu\r\n", local);   
  
// Execute the kernel over the entire range of our 1d input data set   
// using the maximum number of work group items for this device   
    //   
    global = count; 






















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





  err = clEnqueueNDRangeKernel(queue, kernel, 1, NULL, &globalSize, &localSize,0, NULL, NULL);
   size_t globalSize1[2] = {IMAGE_X_PIXELS, IMAGE_Y_PIXELS};
   size_t  localSize1[2]={22, 12};
   err = clEnqueueNDRangeKernel(queue1, kernel1, 2, NULL, globalSize1, localSize1,0, NULL, NULL);
    if (err)   
    {   
printf("Error: Failed to execute kernel!\n");   
return EXIT_FAILURE;   
    }   
  
// Wait for the command commands to get serviced before reading back results   
    //   
    clFinish(queue1);   
  
// Read back the results from the device to verify the output   
    //   
    err = clEnqueueReadBuffer(queue1, output, CL_TRUE, 0, sizeof(float) * count, results, 0, NULL, NULL );     
    if (err != CL_SUCCESS)   
    {   
printf("Error: Failed to read output array! %d\n", err);   
        exit(1);   
    }   
  
// Validate our results   
    //   
    correct = 0;   
    for(int i = 0; i < count; i++)   
    {   
        if(results[i] == image1[i] * IMAGE_SOURCE1_LIMPID  + image2[i] * IMAGE_SOURCE2_LIMPID)   
            correct++;   
    }   
  
// Print a brief summary detailing the results   
    //   
printf("Computed '%d/%d' correct values!\n", correct, count);   









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

clReleaseMemObject(input1);   
clReleaseMemObject(input2);   
clReleaseMemObject(output);   
clReleaseProgram(program1);   
clReleaseKernel(kernel1);   
clReleaseCommandQueue(queue1);   
clReleaseContext(context1);   
  
free(image1);   
free(image2);   
free(results);   




}
