
/*



https://www.eriksmistad.no/getting-started-with-google-test-on-ubuntu/

1. sudo apt-get install libgtest-dev
2. cd /usr/src/gtest
3. sudo cmake CMakeLists.txt
4. sudo make
# copy or symlink libgtest.a and libgtest_main.a to your /usr/lib folder
5.sudo cp *.a /usr/lib







http://blog.csdn.net/bertzhang/article/details/7401955
g++  -o 2  test_main.cc  my_test1.cc  my_test2.cc -lgtest -lpthread


g++ -o 3   test_main.cc  -l:/home/haijunz/13301338176_ML/gtest/libgtest_main.a   -l:/home/haijunz/13301338176_ML/gtest/libgtest.a  -lpthread
g++ -o 1  test_main.cc -lgtest -lpthread

haijunz@haijunz-ThinkPad-T420:~/13301338176_ML/gtest$ ./1
[==========] Running 2 tests from 1 test case.
[----------] Global test environment set-up.
[----------] 2 tests from MyTestCase
[ RUN      ] MyTestCase.FirstTest
[       OK ] MyTestCase.FirstTest (0 ms)
[ RUN      ] MyTestCase.SecondTest
[       OK ] MyTestCase.SecondTest (0 ms)
[----------] 2 tests from MyTestCase (0 ms total)

[----------] Global test environment tear-down
[==========] 2 tests from 1 test case ran. (1 ms total)
[  PASSED  ] 2 tests.




*/
#include <gtest/gtest.h>  
TEST(MyTestCase1, FirstTest) {  
      EXPECT_EQ(2,2);  
    }  
 TEST(MyTestCase2, SecondTest) {  
      EXPECT_EQ(2,2);  
    }  
int main(int argc, char **argv) {  
      ::testing::InitGoogleTest(&argc, argv);  
      return RUN_ALL_TESTS();  
}  
