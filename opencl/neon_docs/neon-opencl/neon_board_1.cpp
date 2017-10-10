/* 

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
arm-linux-gnueabihf-g++ -mfpu=neon  -static -o 1 neon_example1.cpp -std=c++11


g++  -mfpu=neon   -o 1 neon_example.c  -std=c++11

gcc -S -O3 -mcpu=cortex-a8 -mfpu=neon -ftree-vectorize -ftree-vectorizer-verbose=6 test.c

 */
#include <stdint.h>
#include <stdio.h>
#include <assert.h>
#include <arm_neon.h>
#include <time.h>

#include <vector>
#include <random>
#include <climits>
#include <algorithm>
#include <functional>
#include <iostream>
#include <math.h>       /* floor */


int w=640;
int h=480;
int LEN=w*h;


	float32x4x2_t x_32f_8;
        float32x4x2_t y_32f_8;
        float32x4x2_t v11_32f_8;
        float32x4x2_t v12_32f_8;
        float32x4x2_t v21_32f_8;
        float32x4x2_t v22_32f_8;


        const float32_t INIT_ONE[4] = {1.0, 1.0, 1.0, 1.0};
        float32x4_t ONE = vld1q_f32(INIT_ONE);
        float32x4_t tmp1,tmp2,tmp3,tmp4,tmp5;
        float32x4x2_t tmp;
        float32x4_t fx ;
        float32x4_t fy;
        float32x4_t one_fx;
        float32x4_t  one_fy;


 	uint8x8_t V11;
        uint8x8_t V12;
        uint8x8_t V21;
        uint8x8_t V22;
        uint16x8_t v11_16;
        uint16x8_t v12_16;
        uint16x8_t v21_16;
        uint16x8_t v22_16;
        uint16x4_t v_16_low ;
        uint16x4_t v_16_high;
        uint32x4_t v_32_low ;
        uint32x4_t v_32_high;




double  t0, t1, time_c, time_neon;
        int sum=0;


//#include <inttypes.h>
/* return current time in milliseconds */
static double
now_ms(void)
{
    struct timespec res;
    clock_gettime(CLOCK_REALTIME, &res);
    return 1000.0*res.tv_sec + (double)res.tv_nsec/1e6;
}

/* fill array with increasing integers beginning with 0 */
void fill_array(int16_t *array, int size)
{    int i;
    for (i = 0; i < size; i++)
    {
         array[i] = i;
    }
}







inline void bilinear_interp1(float* srcx, float* srcy, float* src11, float* src12, float* src21, float* src22) {

for(int i = 0; i < LEN; i+=1) {
    (src11[i] * (1 - srcy[i]) + src12[i] * srcy[i]) * (1 - srcx[i]) + (src21[i] * (1 - srcy[i]) + src22[i] * srcy[i]) * srcx[i];
  };

}


void remap(unsigned char *src, unsigned char *dst, int *index_1, int *index_2, int *index_3, int *index_4, unsigned char ***interx, unsigned char ***intery, int w, int h) {

	int i;
	int all=w*h;
	for(i=0;i<all;++i) {
		
		unsigned char p1=src[index_1[i]];
		unsigned char p2=src[index_2[i]];
		unsigned char p3=src[index_3[i]];
		unsigned char p4=src[index_4[i]];
		unsigned char **q1=interx[i]; 
		unsigned char **q2=intery[i]; 
		unsigned char r1=q1[p1][p2];
		unsigned char r2=q1[p3][p4];
		dst[i]=q2[r1][r2];

	}



}


void remap_neon(unsigned char *src, unsigned char *dst_neon, int *index_1, int *index_2, int *index_3, int *index_4, unsigned char ***interx, unsigned char ***intery, int w, int h) {

        int i;
        int all=w*h;
	uint8x16x4_t p_lane;
	unsigned char p1_16[16]={0};
	unsigned char p2_16[16]={0};
	unsigned char p3_16[16]={0};
        unsigned char p4_16[16]={0};
/*	unsigned char q1_16[16];
        unsigned char q2_16[16];
        unsigned char r1_16[16];
        unsigned char r2_16[16];
*/

	t0 = now_ms();
        unsigned char p1=src[index_1[i]];
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("unsigned char p1=src[index_1[i]]; %g  ms  \n", time_neon);


	t0 = now_ms();
       	p_lane.val[0]= vld1q_u8 (&src[index_1[i]]);
	vst1q_u8 (p1_16, p_lane.val[0]);   
	p_lane.val[1]= vld1q_u8 (&src[index_2[i]]);
        vst1q_u8 (p2_16, p_lane.val[1]);

        t1 = now_ms();
        time_neon= t1 - t0;
        printf("vld1q_u8 and  vst1q_u8 %g  ms  \n", time_neon);




        for(i=0;i<all;i=i+16) {

		p_lane.val[0]= vld1q_u8 (&src[index_1[i]]);
        	vst1q_u8 (p1_16, p_lane.val[0]);
		p_lane.val[1]= vld1q_u8 (&src[index_2[i]]);
                vst1q_u8 (p2_16, p_lane.val[1]);
		p_lane.val[2]= vld1q_u8 (&src[index_3[i]]);
                vst1q_u8 (p3_16, p_lane.val[3]);
		p_lane.val[3]= vld1q_u8 (&src[index_4[i]]);
                vst1q_u8 (p4_16, p_lane.val[3]);

		for (int j=0;j<16;j++){
			unsigned char **q1=interx[i+j];
                	unsigned char **q2=intery[i+j];
                	unsigned char r1=q1[p1_16[j]][p2_16[j]];
                	unsigned char r2=q1[p3_16[j]][p4_16[j]];
                	dst_neon[i+j]=q2[r1][r2];

		}



        }



}






inline void bilinear_interp_NEON_char(float* srcx, float* srcy, unsigned char* src11, unsigned char* src12, unsigned char* src21, unsigned char* src22) 

{

	int i=0;
	t0 = now_ms();
        (src11[i] * (1 - srcy[i]) + src12[i] * srcy[i]) * (1 - srcx[i]) + (src21[i] * (1 - srcy[i]) + src22[i] * srcy[i]) * srcx[i];
 		t1 = now_ms();
        time_neon= t1 - t0;
        printf(" XXXXXXXXXXXXXXXXXX  was %g  ms  \n", time_neon);

	t0 = now_ms();
        V11=vld1_u8(src11+i);
        t1 = now_ms();
        time_neon= t1 - t0;
	printf("V11=vld1_u8(src11+i);   was %g  ms  \n", time_neon);

        t0 = now_ms();
	x_32f_8.val[1]=vld1q_f32(srcx);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("   x_32f_8.val[1]=vld1q_f32(srcx)  was %g  ms  \n", time_neon);


	t0 = now_ms();
        v11_16 = vmovl_u8(V11); 
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("  v11_16 = vmovl_u8(V11);  was %g  ms  \n", time_neon);


	t0 = now_ms();
        v_16_low = vget_low_u16(v11_16);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("  v_16_low = vget_low_u16(v11_16); was %g  ms  \n", time_neon);

	t0 = now_ms();
        v_32_low = vmovl_u16(v_16_low); 
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("  v_32_low = vmovl_u16(v_16_low); was %g  ms  \n", time_neon);

	t0 = now_ms();
       	v11_32f_8.val[0]=vcvtq_f32_u32(v_32_low); 
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("   v11_32f_8.val[0]=vcvtq_f32_u32(v_32_low); %g  ms  \n", time_neon);


	
	t0 = now_ms();
        one_fx=vmlsq_f32(ONE,fx, ONE); 
        t1 = now_ms();
        time_neon= t1 - t0;
        printf(" one_fx=vmlsq_f32(ONE,fx, ONE)  was %g  ms  \n", time_neon);

	t0 = now_ms();
        tmp1 = vmulq_f32(v11_32f_8.val[0], one_fy);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("tmp1 = vmulq_f32(v11_32f_8.val[0], one_fy)  was %g  ms  \n", time_neon);

	t0 = now_ms();
	tmp2 = vmulq_f32(v22_32f_8.val[0],fy);
	t1 = now_ms();
        time_neon= t1 - t0;
        printf("tmp2 = vmulq_f32(v22_32f_8.val[0],fy) %g  ms  \n", time_neon);


	t0 = now_ms();
        tmp3 = vaddq_f32(tmp1, tmp2); 
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("tmp3 = vaddq_f32(tmp1, tmp2); was %g  ms  \n", time_neon);


	t0 = now_ms();
	tmp4 = vmulq_f32(tmp3, one_fx);        
	t1 = now_ms();
        time_neon= t1 - t0;
        printf(" tmp4 = vmulq_f32(tmp3, one_fx); %g  ms  \n", time_neon);


	t0 = now_ms();
        tmp5 = vmulq_f32(tmp3,fx);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("  tmp5 = vmulq_f32(tmp3,fx); %g  ms  \n", time_neon);
 


	t0 = now_ms();
        tmp.val[0] = vaddq_f32(tmp4,tmp5); 
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("tmp.val[0] = vaddq_f32(tmp4,tmp5); %g  ms  \n", time_neon);




     /* counting backwards gives better code */
//for (; LEN != 0; LEN -= 8) {
for(int i = 0; i < LEN; i+=8) {
	V11=vld1_u8(src11+i);
     	V12=vld1_u8(src12+i);
     	V21=vld1_u8(src21+i);
     	V22=vld1_u8(src22+i);
     	x_32f_8.val[0]=vld1q_f32(srcx+i);
     	x_32f_8.val[1]=vld1q_f32(srcx+4+i);
     	y_32f_8.val[0]=vld1q_f32(srcy+i);
     	y_32f_8.val[1]=vld1q_f32(srcy+4+i);
	

	v11_16 = vmovl_u8(V11);
	v12_16 = vmovl_u8(V12);
	v21_16 = vmovl_u8(V21);
	v22_16 = vmovl_u8(V22);


    ///convert v11 to two float32x4
	v_16_low = vget_low_u16(v11_16);
	v_16_high = vget_high_u16(v11_16);
	v_32_low = vmovl_u16(v_16_low);
	v_32_high = vmovl_u16(v_16_high);
	v11_32f_8.val[0]=vcvtq_f32_u32(v_32_low);
	v11_32f_8.val[1]=vcvtq_f32_u32(v_32_high);

    //convert v12 to two float32x4

	v_16_low = vget_low_u16(v12_16);
	v_16_high = vget_high_u16(v12_16);
    	v_32_low = vmovl_u16(v_16_low);
    	v_32_high = vmovl_u16(v_16_high);
        v12_32f_8.val[0]=vcvtq_f32_u32(v_32_low);
        v12_32f_8.val[1]=vcvtq_f32_u32(v_32_high);

	 //convert v21 to two float32x4
        v_16_low = vget_low_u16(v21_16);
        v_16_high = vget_high_u16(v21_16);
        v_32_low = vmovl_u16(v_16_low);
        v_32_high = vmovl_u16(v_16_high);
        v21_32f_8.val[0]=vcvtq_f32_u32(v_32_low);
        v21_32f_8.val[1]=vcvtq_f32_u32(v_32_high);
 
	//convert v22 to two float32x4

        v_16_low = vget_low_u16(v22_16);
        v_16_high = vget_high_u16(v22_16);
        v_32_low = vmovl_u16(v_16_low);
        v_32_high = vmovl_u16(v_16_high);
        v22_32f_8.val[0]=vcvtq_f32_u32(v_32_low);
        v22_32f_8.val[1]=vcvtq_f32_u32(v_32_high);


//========= first lane  32x4

        fx = x_32f_8.val[0];
        fy = y_32f_8.val[0];
        one_fx=vmlsq_f32(ONE,fx, ONE);
        one_fy =vmlsq_f32(ONE,fy, ONE);
	tmp1 = vmulq_f32(v11_32f_8.val[0], one_fy);
	tmp2 = vmulq_f32(v12_32f_8.val[0], fy);
	tmp3 = vaddq_f32(tmp1, tmp2);
	tmp4 = vmulq_f32(tmp3, one_fx);
	tmp1 = vmulq_f32(v21_32f_8.val[0],one_fy); 
	tmp2 = vmulq_f32(v22_32f_8.val[0],fy);
	tmp3 = vaddq_f32(tmp1, tmp2);
	tmp5 = vmulq_f32(tmp3,fx);

    	tmp.val[0] = vaddq_f32(tmp4,tmp5);

//==========second  lane  32x4

	fx = x_32f_8.val[1];
        fy = y_32f_8.val[1];

        one_fx=vmlsq_f32(ONE,fx, ONE);
        one_fy =vmlsq_f32(ONE,fy, ONE);
        tmp1 = vmulq_f32(v11_32f_8.val[0], one_fy);
        tmp2 = vmulq_f32(v12_32f_8.val[0], fy);
        tmp3 = vaddq_f32(tmp1, tmp2);
        tmp4 = vmulq_f32(tmp3, one_fx);
        tmp1 = vmulq_f32(v21_32f_8.val[0],one_fy);
        tmp2 = vmulq_f32(v22_32f_8.val[0],fy);
        tmp3 = vaddq_f32(tmp1, tmp2);
        tmp5 = vmulq_f32(tmp3,fx);

        tmp.val[1] = vaddq_f32(tmp4,tmp5);
	//std::cout<<i<<std::endl;
}

}



inline void bilinear_interp_NEON_float(float* srcx, float* srcy, float* src11, float* src12, float* src21, float* src22) 

{

   

	int i=0;
	t0 = now_ms();
        (src11[i] * (1 - srcy[i]) + src12[i] * srcy[i]) * (1 - srcx[i]) + (src21[i] * (1 - srcy[i]) + src22[i] * srcy[i]) * srcx[i]; 		t1 = now_ms();
        time_neon= t1 - t0;
        printf(" XXXXXXXXXXXXXXXXXX  was %g  ms  \n", time_neon);

	t0 = now_ms();
        x_32f_8.val[0]=vld1q_f32(srcx);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf(" x_32f_8.val[0]=vld1q_f32(srcx+i)  was %g  ms  \n", time_neon);

        t0 = now_ms();
	x_32f_8.val[1]=vld1q_f32(srcx);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("   x_32f_8.val[1]=vld1q_f32(srcx)  was %g  ms  \n", time_neon);
	
	t0 = now_ms();
        one_fx=vmlsq_f32(ONE,fx, ONE); 
        t1 = now_ms();
        time_neon= t1 - t0;
        printf(" one_fx=vmlsq_f32(ONE,fx, ONE)  was %g  ms  \n", time_neon);

	t0 = now_ms();
        tmp1 = vmulq_f32(v11_32f_8.val[0], one_fy);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("tmp1 = vmulq_f32(v11_32f_8.val[0], one_fy)  was %g  ms  \n", time_neon);

	t0 = now_ms();
	tmp2 = vmulq_f32(v22_32f_8.val[0],fy);
	t1 = now_ms();
        time_neon= t1 - t0;
        printf("tmp2 = vmulq_f32(v22_32f_8.val[0],fy) %g  ms  \n", time_neon);


	t0 = now_ms();
        tmp3 = vaddq_f32(tmp1, tmp2); 
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("tmp3 = vaddq_f32(tmp1, tmp2); was %g  ms  \n", time_neon);


	t0 = now_ms();
	tmp4 = vmulq_f32(tmp3, one_fx);        
	t1 = now_ms();
        time_neon= t1 - t0;
        printf(" tmp4 = vmulq_f32(tmp3, one_fx); %g  ms  \n", time_neon);


	t0 = now_ms();
        tmp5 = vmulq_f32(tmp3,fx);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("  tmp5 = vmulq_f32(tmp3,fx); %g  ms  \n", time_neon);
 


	t0 = now_ms();
        tmp.val[0] = vaddq_f32(tmp4,tmp5); 
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("tmp.val[0] = vaddq_f32(tmp4,tmp5); %g  ms  \n", time_neon);


     /* counting backwards gives better code */
//for (; LEN != 0; LEN -= 8) {
for(int i = 0; i < LEN; i+=8) {

/*	v11_32f_8.val[0]=vld1q_f32(src11+i);
        v11_32f_8.val[1]=vld1q_f32(src11+4+i);
	v12_32f_8.val[0]=vld1q_f32(src12+i);
        v12_32f_8.val[1]=vld1q_f32(src12+4+i);
	v21_32f_8.val[0]=vld1q_f32(src21+i);
        v21_32f_8.val[1]=vld1q_f32(src21+4+i);
	v22_32f_8.val[0]=vld1q_f32(src22+i);
        v22_32f_8.val[1]=vld1q_f32(src22+4+i);
     	x_32f_8.val[0]=vld1q_f32(srcx+i);
     	x_32f_8.val[1]=vld1q_f32(srcx+4+i);
     	y_32f_8.val[0]=vld1q_f32(srcy+i);
     	y_32f_8.val[1]=vld1q_f32(srcy+4+i);
	
*/
//========= first lane  32x4

        fx = x_32f_8.val[0];
        fy = y_32f_8.val[0];
        one_fx=vmlsq_f32(ONE,fx, ONE);
        one_fy =vmlsq_f32(ONE,fy, ONE);
	tmp1 = vmulq_f32(v11_32f_8.val[0], one_fy);
	tmp2 = vmulq_f32(v12_32f_8.val[0], fy);
	tmp3 = vaddq_f32(tmp1, tmp2);
	tmp4 = vmulq_f32(tmp3, one_fx);
	tmp1 = vmulq_f32(v21_32f_8.val[0],one_fy); 
	tmp2 = vmulq_f32(v22_32f_8.val[0],fy);
	tmp3 = vaddq_f32(tmp1, tmp2);
	tmp5 = vmulq_f32(tmp3,fx);

    	tmp.val[0] = vaddq_f32(tmp4,tmp5);

//==========second  lane  32x4

	fx = x_32f_8.val[1];
        fy = y_32f_8.val[1];

        one_fx=vmlsq_f32(ONE,fx, ONE);
        one_fy =vmlsq_f32(ONE,fy, ONE);
        tmp1 = vmulq_f32(v11_32f_8.val[0], one_fy);
        tmp2 = vmulq_f32(v12_32f_8.val[0], fy);
        tmp3 = vaddq_f32(tmp1, tmp2);
        tmp4 = vmulq_f32(tmp3, one_fx);
        tmp1 = vmulq_f32(v21_32f_8.val[0],one_fy);
        tmp2 = vmulq_f32(v22_32f_8.val[0],fy);
        tmp3 = vaddq_f32(tmp1, tmp2);
        tmp5 = vmulq_f32(tmp3,fx);

        tmp.val[1] = vaddq_f32(tmp4,tmp5);
	//std::cout<<i<<std::endl;


}

}






int sum_array(int16_t *array, int size)
{
	int sum=0;
	for (int i=0; i<size; i++) {
          sum=sum+array[i];
     	}

	return sum;	

}


/* return the sum of all elements in an array. This works by calculating 4 totals (one for each lane) and adding those at the end to get the final total */
int  sum_array_neon(int16_t *array, int size)
{
	 /* initialize the accumulator vector to zero */
     int16x4_t acc = vdup_n_s16(0);
     int32x2_t acc1;
     int64x1_t acc2;
     /* this implementation assumes the size of the array is a multiple of 4 */
     assert((size % 4) == 0);
     /* counting backwards gives better code */
     for (; size != 0; size -= 4)
     {
          int16x4_t vec;
          /* load 4 values in parallel from the array */
          vec = vld1_s16(array);
          /* increment the array pointer to the next element */
          array += 4;
          /* add the vector to the accumulator vector */
          acc = vadd_s16(acc, vec);
      }
      /* calculate the total */
      acc1 = vpaddl_s16(acc);
      acc2 = vpaddl_s32(acc1);
      /* return the total as an integer */
      return (int)vget_lane_s64(acc2, 0);
}


/* main function */
int main()
{
	int16_t my_array[100];
	fill_array(my_array, 100);
	t0 = now_ms();
        sum=sum_array(my_array, 100);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("sum_array  was %g  ms  sum=%d\n", time_neon,sum);
	sum=0;
       	t0 = now_ms();
       	sum=sum_array_neon(my_array, 100);
       	t1 = now_ms();
    	time_neon= t1 - t0;	
	printf("sum_array_neon was %g  ms  sum=%d\n", time_neon,sum);

	/*using random_bytes_engine = std::independent_bits_engine<std::default_random_engine, CHAR_BIT, unsigned char>;
	
    	random_bytes_engine rbe;
   	std::vector<unsigned char> data(640*480);
	std::generate(begin(data), end(data), std::ref(rbe));
	for (auto i:data) {std::cout<<i<<",";};
*/

	 assert((LEN % 8) == 0);
	std::vector<float> x(LEN),y(LEN); 
	float result,RESULT;	
	std::vector<float> v11(LEN);
	std::vector<float> v12(LEN);
	std::vector<float> v21(LEN);
	std::vector<float> v22(LEN);	

	for(int i = 0; i < LEN; i+=1) {

	x[i]=i*0.1;
	y[i]=i*0.1;
	v11[i]=  i;
	v12[i]=  i;
	v21[i]=  i;
	v22[i]=  i;
        };




	std::vector<unsigned char> v11_char(LEN);
	std::vector<unsigned char> v12_char(LEN);
	std::vector<unsigned char> v21_char(LEN);
	std::vector<unsigned char> v22_char(LEN);	

	for(int i = 0; i < LEN; i+=1) {

	v11[i]= (unsigned char) i;
	v12[i]= (unsigned char) i;
	v21[i]= (unsigned char) i;
	v22[i]= (unsigned char) i;
        };




	uint8_t * __restrict src11_char;
        uint8_t * __restrict src12_char;
        uint8_t * __restrict src21_char;
        uint8_t * __restrict src22_char;
        src11_char=v11_char.data();
        src12_char=v12_char.data();
        src21_char=v21_char.data();
        src22_char=v22_char.data();



	float * __restrict src11;
        float * __restrict src12;
        float * __restrict src21;
        float * __restrict src22;
	float * __restrict srcx;
	float * __restrict srcy;
        static_assert(std::is_same<unsigned char, uint8_t>::value, "uint8_t is n ot unsigned char");

        src11=v11.data();
        src12=v12.data();
        src21=v21.data();
        src22=v22.data();
	srcx=x.data();
	srcy=y.data();
     

	//vst1_u8(RESULT,bilinear_interp_NEON(x, y,V11,V12,V21,V22));



	/*float x_32f[8]={1,2,3,4,5,6,7,8};
        float32x4x2_t x_32f_8;
        x_32f_8.val[0]=vld1q_f32(x_32f);
	x_32f_8.val[1]=vld1q_f32(x_32f+4);

	float buff[4];
	vst1q_f32(buff, x_32f_8.val[1]);
	for (auto i:buff) {
		std::cout <<i<<std::endl; 
	
        };*/



	t0 = now_ms();
        bilinear_interp1(srcx,srcy,src11,src12,src21,src22);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf("bilinear_interp1  was %g  ms  result=%g\n", time_neon,result);


	t0 = now_ms();
        bilinear_interp_NEON_char(srcx,srcy,src11_char,src12_char,src21_char,src22_char);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf(" bilinear_interp_NEON_char  was %g  ms  RESULT=%g\n", time_neon,RESULT);



	t0 = now_ms();
        bilinear_interp_NEON_float(srcx,srcy,src11,src12,src21,src22);
        t1 = now_ms();
        time_neon= t1 - t0;
	printf(" bilinear_interp_NEON_float  was %g  ms  RESULT=%g\n", time_neon,RESULT);

	
	int *index_1,*index_2,*index_3,*index_4;	
	unsigned char ***interx;
	unsigned char ***intery;
	unsigned char **lookup[32];	

	unsigned char *src,*dst,*dst_neon;
	
	index_1=new int[w*h];
	index_2=new int[w*h];
	index_3=new int[w*h];
	index_4=new int[w*h];

	src=new unsigned char[w*h];
	dst=new unsigned char[w*h];
	dst_neon=new unsigned char[w*h];

	interx=new unsigned char**[w*h];
	intery=new unsigned char**[w*h];



	for( int i = 0; i < LEN; i++ )
     {
        index_1[i] = i-1;
        index_2[i] = i;
        index_3[i] = i+1;
        index_4[i] = i;
        src[i]='f';
        dst[i]='c';
        dst_neon[i]='b';

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

	t0 = now_ms();
	remap(src, dst, index_1,index_2,index_3,index_4,interx,intery,640,480);
	t1 = now_ms();
        time_neon= t1 - t0;
        printf(" remap  was %g  ms \n", time_neon);

	t0 = now_ms();
        remap_neon(src, dst_neon, index_1,index_2,index_3,index_4,interx,intery,640,480);
        t1 = now_ms();
        time_neon= t1 - t0;
        printf(" remap_neon  was %g  ms \n", time_neon);


	for(int  i = 0; i < LEN; i++ )
    	 {
       
          
        if (dst[i] != dst_neon[i] ) {
             std::cout<<"fail===";
         } else {
                std::cout<<"pass====";

         };

         std::cout<<"i="<<i<<"    dst[i]="<<dst[i]<<"     dst_neon[i]="<<dst_neon[i]<<std::endl;
     }

	return 0;
}
