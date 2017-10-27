If you are doing a lot of template metaprogramming and other template magic stuff, you are likely to miss a :code:`static_if` in the language. Unfortunately, it didn't make the cut for C++11 and it seems unlikely that it will make it in C++17.

static_if
#########

As its name indicates, :code:`static_if` is an if statement but that is done at compile-time. At first, it could seem that the main point is performance, but that is not the case. With recent compilers, if you have an if statement with a compile-time constant, it will never be executed at runtime and only the correct branch will be included in the final executable code. However, even if the compiler knows that a branch will never be executed, it still has to ensure that this branch compiles. This is not the case with :code:`static_if`. With :code:`static_if`, only the valid branch is compiled, the other can contains invalid code. The most common reason to use a :code:`static_if` is inside a template where you perform a test on a template argument and execute code based on this test. :code:`static_if` has another advantage on standard if. Since only one branch is instantiated, it may save quite a lot of compile-time.

Let's say we have to write a template function that, if the template argument is a string, removes the last character of the string argument, otherwise decrement the argument (I know, stupid example, but simple). With :code:`static_if`, you can write it like this:

.. code:: cpp

    template<typename T>
    void decrement_kindof(T& value){
        static_if(std::is_same<std::string, T>::value){
            value.pop_back();
        } else {
            --value;
        }
    }

I think it is quite elegant.

The problem
###########

Some may think, that we could do the same with C++ standard if statement:

.. code:: cpp

    template<typename T>
    void decrement_kindof(T& value){
        if(std::is_same<std::string, T>::value){
            value.pop_back();
        } else {
            --value;
        }
    }

However, this won't work. This template cannot be instantiated for :code:`std::string` since it doesn't have an operator -- and it cannot be instantiated for int since it doesn't have a :code:`pop_back()` function.

There are two solutions in plain C++: specialization and SFINAE. Let's start with specialization:

.. code:: cpp

    template<typename T>
    void decrement_kindof(T& value){
        --value;
    }

    template<>
    void decrement_kindof(std::string& value){
        value.pop_back();
    }

We do a specialization for :code:`std::string` case so that in the general case it uses -- and in the :code:`std::string` case, it uses :code:`pop_back()`. And the SFINAE version:

.. code:: cpp

    template<typename T, std::enable_if_t<!std::is_same<std::string, T>::value, int> = 42>
    void decrement_kindof(T& value){
        --value;
    }

    template<typename T, std::enable_if_t<std::is_same<std::string, T>::value, int> = 42>
    void decrement_kindof(T& value){
        value.pop_back();
    }

The first function is enabled when the type is not a :code:`std::string` and the second function is enabled when the type is a :code:`std::string`.

Both solutions needs two functions to make it work. In this particular case, specialization is easier since the condition states exactly one type. If the condition was more complex for instance testing that a constant inside the type is equals to some value, we could only do it with SFINAE.

Even if both solutions work, both solutions are more complicated than the static_if version and both solutions are creating more functions than what should be necessary.

One solution
############

There is one way to emulate a kind of :code:`static_if` with C++14 generic lambdas. It is kind of using anonymous template function to emulate what we did with the previous solutions but does it behind the scene. Here the code I'm using for this emulation:

.. code:: cpp

    namespace static_if_detail {

    struct identity {
        template<typename T>
        T operator()(T&& x) const {
            return std::forward<T>(x);
        }
    };

    template<bool Cond>
    struct statement {
        template<typename F>
        void then(const F& f){
            f(identity());
        }

        template<typename F>
        void else_(const F&){}
    };

    template<>
    struct statement<false> {
        template<typename F>
        void then(const F&){}

        template<typename F>
        void else_(const F& f){
            f(identity());
        }
    };

    } //end of namespace static_if_detail

    template<bool Cond, typename F>
    static_if_detail::statement<Cond> static_if(F const& f){
        static_if_detail::statement<Cond> if_;
        if_.then(f);
        return if_;
    }

Note: I got the idea (and most of the code) from the `Boost Mailing List <http://lists.boost.org/Archives/boost/2014/08/216607.php>`_.

The condition is passed a non-type template parameter and the code for the branch is a passed a generic lambda functor. The :code:`static_if` function returns a statement structure. We could avoid returning a struct and directly execute, or not, the functor based on the condition, but using a structure allows for the :code:`else_` part which may be practical. The structure :code:`statement` is specialized on the condition. If the condition is true, the right part will execute the functor while the false part will not execute anything. The specialization when the condition is false willl do the contrary. A special point here is the use of the identity function. The function is passed to the lambda. The user can then use this function to make non-dependent type dependent. This is necessary if we want to call functions on non-dependent types and these functions may not exist. For instance, you may want to call a function on :code:`this`, which is not a dependent type.

Here is how the code will look using this solution:

.. code:: cpp

    template<typename T>
    void decrement_kindof(T& value){
        static_if<std::is_same<std::string, T>::value>([&](auto f){
            f(value).pop_back();
        }).else_([&](auto f){
            --f(value);
        });
    }

It is not as elegant as the "real" :code:`static_if` version, but it is closer than the other solutions.

If you don't use the lazy identity function (f), it still works on g++, but not on clang for some reasons.

Conclusion
##########

We saw that there are some solutions to emulate :code:`static_if` in C++ that you may use to make the code easier to read. I'm personally using this trick on branches with few lines of code and when I don't have to use the identity function too much, otherwise it is cleaner to use standard SFINAE functions to do the job. When you only have a if and no else, this trick is even better because that is where it saves the more code.

I hope this can be useful to some of you ;)

You can find my implementation `on Github <https://github.com/wichtounet/cpp_utils/blob/master/static_if.hpp>`_.
