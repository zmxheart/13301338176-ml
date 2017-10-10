    #include <gtest/gtest.h>  
    TEST(MyTestCase3, ThirdTest) {  
      ASSERT_EQ(3,3) << "3 is not equal to 4";  
      char* p = NULL;  
      ASSERT_STREQ(NULL, p);  
    }  
    TEST(YourTestCase4, FirstTest) {  
      EXPECT_EQ(3,3);  
    }  
