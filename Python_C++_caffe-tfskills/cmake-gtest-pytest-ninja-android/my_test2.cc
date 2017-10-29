    #include <gtest/gtest.h>  
    #include "my_class.h"  
    class MyClassTest : public ::testing::Test {  
     public:  
      virtual void SetUp() {  
        my_class_ = new MyClass(10);  
      }  
      virtual void TearDown() {  
        delete my_class_;  
      }  
      MyClass* my_class_;  
    };  
    TEST_F(MyClassTest, FirstTest) {  
      EXPECT_EQ(10, my_class_->GetX());  
      my_class_->SetX(2);  
    }  
    TEST_F(MyClassTest, SecondTest) {  
      EXPECT_EQ(10, my_class_->GetX());  
      my_class_->SetX(2);  
    }  
