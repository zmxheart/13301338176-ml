#include <unistd.h> 
#include <iostream>
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


static double
now_ms(void)
{
    struct timespec res;
    clock_gettime(CLOCK_REALTIME, &res);
    return 1000.0*res.tv_sec + (double)res.tv_nsec/1e6;
}



class Base1
{
public:
    ~Base1() // note: virtual
    {
        std::cout << "Calling ~Base1()" << std::endl;
    }
};
 
class Derived1: public Base1
{
private:
    int* m_array;
 
public:
    Derived1(int length)
    {
        m_array = new int[length];
    }
 
    ~Derived1() // note: virtual
    {
        std::cout << "Calling ~Derived1()" << std::endl;
        delete[] m_array;
    }
};


class Base
{
public:
    virtual ~Base() // 
    {
        std::cout << "Calling ~Base()" << std::endl;
    }
};

class Derived: public Base
{
private:
    int* m_array;

public:
    Derived(int length)
    {
        m_array = new int[length];
        
    }

    ~Derived() // note: virtual
    {
        std::cout << "Calling ~Derived()" << std::endl;
        delete[] m_array;
    }
};





int test(int size) {

    Derived *derived = new Derived(10);
    Base *base = derived;
    delete base;
    return 0;


}


int test1(int size) {

    Derived1 *derived1 = new Derived1(10);
    delete derived1;
    return 0;

}
 
int main()
{
    

   int i;
   double  t0, t1; 
   double sum{0.0},sum1{0.0}; 
   for (i = 10; i < 11000; i=i+100) {
    t0 = now_ms();
    test(i);
    t1 = now_ms();
    sum=sum+(t1-t0);
    printf("time  virual     was %g  szie=%d bytes\n", t1-t0,i*4);
    t0 = now_ms();
    test1(i);
    t1 = now_ms();
    sum1=sum1+(t1-t0);
    printf("time  no virual  was %g  szie=%d bytes \n", t1-t0,i*4);

    }
   
    printf("======sum=%g,  sum1=%g,  100*(sum-sum1)/sum1 = %g \n",sum, sum1, (sum-sum1)*100/sum1);
 
    return 0;
}

