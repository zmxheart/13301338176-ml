Often if you write templated code, you have to write and use a lot of different
traits. In this article, I'll focus on the traits that are representing values,
typically a boolean value. For instance, std::is_const, std::is_same or
std::is_reference are type traits provided by the STL. They are giving you some
information at compile time for a certain type. If you need to write a type
traits, let's say is_float, here is how you would maybe do it in C++11:

.. code:: c++

   template <typename T>
   struct is_float {
       static constexpr bool value = std::is_same<T, float>::value;
   };

or a bit nicer with a template type alias and std::integral constant:

.. code:: c++

   template <typename T>
   using is_float = std::integral_constant<bool, std::is_same<T, float>::value>;

or since is_same is itself a type traits, you can also directly alias it:

.. code:: c++

   template <typename T>
   using is_float = std::is_same<T, float>;

This makes for some very nice syntax, but we still have a type rather than a value.

Note that in some cases, you cannot use the using technique since it cannot be
specialized and you often need specialization to write some more advanced
traits.

And then you would use your traits to do something specific based on that
information. For instance with a very basic example:

.. code:: C++

    template <typename T>
    void test(T t){
        if (is_float<T>::value){
            std::cout << "I'm a float" << std::endl;
        } else {
            std::cout << "I'm not a float" << std::endl;
        }
    }

Really nothing fancy here, but that will be enough as examples.

Even though all this works pretty, it can be made better on two points. First,
every time you use a traits, you need to use the value member (via ::value).
Secondly, every time you declare a new traits, you have to declare a new type or
a type alias. But all you want is a boolean value.

C++14 introduced a new feature, variable templates. As their name indicates,
they are variables, parametrized with a type. This allows us to write type
traits without using a type alias or struct, meaning we have a real value
instead of a type. If we rewrite our is_float traits with variable templates, we
have the following:

.. code:: c++

   template <typename T>
   constexpr bool is_float = std::is_same<T, float>::value;

I think it's much nicer, the intent is clearly stated and there is no
unnecessary code. Moreover, it's also nicer to use:

.. code:: C++

    template <typename T>
    void test(T t){
        if (is_float<T>){
            std::cout << "I'm a float" << std::endl;
        } else {
            std::cout << "I'm not a float" << std::endl;
        }
    }

No more ::value everywhere :) I think it's really cool.

Note that, unlike type alias template, they can be specialized, either fully or
partially, so no more limitation on that side.

Interestingly, variable templates are used in C++17 to provide helpers for each
type traits with values. For instance, std::is_same will have a std::is_same_v
helper that is a variable template. With that, we can simplify our traits a bit
more:

.. code:: c++

   template <typename T>
   constexpr bool is_float = std::is_same_v<T, float>;

Personally, I replaced all the type traits inside ETL using variable templates.
If you don't want to do it, you can also introduce helpers like in the C++17 STL
and start using the wrappers when you see fit so that you don't break any code.

If you want to use this feature, you need a C++14 compiler, such as any version
from GCC5 family or clang 3.6. Although I haven't tested, it should also work on
Microsoft VS2015 Update 2.

Unfortunately there is a bug in both clang (fixed in clang 3.7) and GCC (fixed
in GCC 6 only) that you may encounter if you start using variable templates in
template classes or variable templates used in another variable templates. If
you plan to use variable templates inside a template, such as something like
this:

.. code:: c++

    template <typename T>
    struct outer_traits {
        template <typename X>
        static constexpr bool sub_traits = std::is_same<T, X>::value;
    };

    template <typename T, typename X>
    constexpr bool outer_helper = outer_traits<T>::template sub_traits<X>;

    int main(){
        std::cout << outer_helper<float, float>;

        return 0;
    }

You will encounter a not-helpful at all error message with GCC5 family, such as:

.. code::

    test.cpp: In instantiation of ‘constexpr const bool outer_helper<float, float>’:
    test.cpp:14:22:   required from here
    test.cpp:11:20: error: ‘template<class X> constexpr const bool outer_traits<float>::sub_traits<X>’ is not a function template
         constexpr bool outer_helper = outer_traits<T>::template sub_trait
                        ^
    test.cpp:11:20: error: ‘sub_traits<X>’ is not a member of ‘outer_traits<float>’

It comes from a bug in the handling of variable templates as dependent names. If
you don't come in this cases, you can use GCC5 family directly, otherwise,
you'll have to use GCC6 family only.

I hope this can help some of you to improve your type traits or at least to
discover the power of the new variable templates. Personally, I've rewritten all
the traits from the ETL library using this new feature and I'm pretty satisfied
with the result. Of course, that means that the compiler support was reduced,
but since I don't have many users, it's not a real issue.
