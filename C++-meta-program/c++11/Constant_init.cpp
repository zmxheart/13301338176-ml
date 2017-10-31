#include <chrono>
#include <iostream>
#include <future>

struct MyDouble{
  constexpr MyDouble(double v): val(v){}
  constexpr double getValue(){ return val; }
private:
  double val;
};

int main() {
constexpr MyDouble myDouble(10.5);
std::cout << myDouble.getValue() << std::endl;
}
