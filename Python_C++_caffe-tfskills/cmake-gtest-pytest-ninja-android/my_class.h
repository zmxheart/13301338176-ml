    #include <stdio.h>  
    class MyClass {  
     public:  
      MyClass(int x) : x_(x) {}  
      void SetX(int x) {  
        x_ = x;  
      }  
      int GetX() const {  
        return x_;  
      }  
     private:  
      int x_;  
    };  
