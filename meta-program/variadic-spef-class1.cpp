#include <iostream>
using namespace std;


template<typename First, typename... Args>
struct Sum;

//基本定义
template<typename First, typename... Rest>
struct Sum<First, Rest...> : std::integral_constant<int, Sum<First>::value + Sum<Rest...>::value>
{
};

//递归终止
template<typename Last>
struct Sum<Last> : std::integral_constant<int, sizeof(Last)>
{
};

int main(void)
{
	//Sum<int,int,int> sum1;
	Sum<int,double,short>::value;
   
	return 0;
}
