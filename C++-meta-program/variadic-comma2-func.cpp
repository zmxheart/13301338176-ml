#include <iostream>
using namespace std;
template<class F, class... Args>void expand(const F& f, Args&&...args) 
{
  //这里用到了完美转发，关于完美转发，读者可以参考笔者在上一期程序员中的文章《通过4行代码看右值引用》
  initializer_list<int>{(f(std::forward< Args>(args)),0)...};
}

int main() {


	expand([](int i){cout<<i<<endl;}, 1,2,3);
}
