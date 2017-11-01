

//  g++ -o 1 sampleB.C sampleA.C  -L./


// sample2.C
template <typename T, T val>
struct A{
   virtual T func();
   virtual T bar();
}

extern template int A<int,55>::func();

template <class T, T val>
T A<T,val>::func(void){
   return val;
}

template <class T, T val>
T A<T,val>::bar(void){
   return val;
}

int main(void){
   return A<int,55>().bar();
}
