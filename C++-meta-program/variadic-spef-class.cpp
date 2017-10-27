#include <iostream>
using namespace std;



template<typename... Args>
struct Sum;

//基本定义
template<typename First, typename... Rest>
struct Sum<First, Rest...>
{
    enum { value = Sum<First>::value + Sum<Rest...>::value };
};

//递归终止
template<typename Last>
struct Sum<Last>
{
    enum { value = sizeof (Last) };
};


int main(void)
{
	Sum<int,int,int> sum1;

   
	return 0;
}
