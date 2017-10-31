#include <iostream>
#include <mutex>
#include <memory>
class Singleton
{
private:
	Singleton(const Singleton&) = delete;
	Singleton & operator=(const Singleton&) = delete;
	

	static std::unique_ptr<Singleton> instance;
	static std::once_flag onceFlag;
public:
	Singleton() = default;

	static void NofityInit()
	{
		std::cout << "Initializing Singleton" << '\n';
	}
	static Singleton& Singleton::Instance()
	{
		std::call_once(Singleton::onceFlag,[] (){
			NofityInit();
			instance.reset(new Singleton); 
		});

		std::cout << "Getting  Singleton instance" << '\n';
		return *(instance.get());
	}
};

std::unique_ptr<Singleton> Singleton::instance;
std::once_flag Singleton::onceFlag;

int main()
{
	Singleton& s1 = Singleton::Instance();
	Singleton& s2 = Singleton::Instance();

	return 0;
}
