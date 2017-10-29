class Animal
{
  public:
    void die();
    string name;
    int age;
};

class Dog : public Animal
{
  public:
    void bark();
};

class Cat : public Animal
{
  public:
    void meow();
};
