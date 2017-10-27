#include <iostream>
using namespace std;
//递归终止函数
/*void recusive()
{
   cout << "empty" << endl;
}
*/

template <class T>
void recusive(T t)
{
   cout << t << endl;
}




//展开函数
template <class T, class ...Args>
void recusive(T head, Args... rest)
{
   cout << "parameter " << head << endl;
   recusive(rest...);
}



template<typename T>
T sum(T t)
{
    return t;
}
template<typename T, typename ... Types>
T sum (T first, Types ... rest)
{
    return first + sum<T>(rest...);
}




int main(void)
{
   recusive(1,2,3,4);
cout<<	sum(1,2,3,4)<<endl; 
   return 0;
}
