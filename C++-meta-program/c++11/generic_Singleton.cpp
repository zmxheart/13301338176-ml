#include <iostream>

template <class T>
class Singleton
{
public:
  template <typename... Args>
  static
  T* get_instance(Args... args)
  {
    if (!instance_)
      {
        instance_ = new T(std::forward<Args>(args)...);
      }

    return instance_;
  }

  static
  void destroy_instance()
  {
    delete instance_;
    instance_ = nullptr;
  }

private:
  static T* instance_;
};

template <class T> T*  Singleton<T>::instance_ = nullptr;

class Map: public Singleton<Map>
{
  friend class Singleton<Map>;
private:
  Map(int size_x, int size_y): size_x_{size_x}, size_y_{size_y} {}

public:
  int size_x_;
  int size_y_;
};

int main()
{
  Map* m = Map::get_instance(4, 5);

  std::cout << m->size_y_ << std::endl; // Outputs 5.

  Map::destroy_instance();
}
