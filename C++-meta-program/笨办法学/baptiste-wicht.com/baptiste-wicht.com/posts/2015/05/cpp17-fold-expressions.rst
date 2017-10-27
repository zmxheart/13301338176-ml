Variadic Templates
++++++++++++++++++

C++11 introduced variadic template to the languages. This new feature allows to write template functions and classes taking an arbitrary number of template parameters. This a feature I really like and I already used it quite a lot in my different libraries. Here is a very simple example computing the sum of the parameters: 

.. code:: c++

    auto old_sum(){
        return 0;
    }

    template<typename T1, typename... T>
    auto old_sum(T1 s, T... ts){
        return s + old_sum(ts...);;
    }

What can be seen here is a typical use of variadic templates. Almost all the time, is is necessary to use recursion and several functions to unpack the parameters and process them. There is only one way to unpack the arguments, by using the ... operator that simply put comma between arguments. Even if it works well, it is a bit heavy on the code. This will likely be completely optimized to a series of addition by the compiler, but it may still happen in more complicated functions that this is not done. Moreover, the intent is not always clear with that. 

That is why C++17 introduced an extension for the variadic template, fold expressions. 

Fold expressions
++++++++++++++++

Fold expressions are a new way to unpack variadic parameters with operators. For now, only Clang 3.6 supports C++17 fold expression, with the -std=c++1z flag. That is the compiler I used to validate the examples of this post. 

The syntax is bit disturbing at first but quite logical: 

.. code:: c++

    ( pack op ... )             //(1)
    ( ... op pack )             //(2)
    ( pack op ... op init )     //(3)
    ( init op ... op pack )     //(4)

Where *pack* is an unexpanded parameter pack, *op* an operator and *init* a value. The version (1) is a right fold that is expanded like (P1 op (P2 op (P3 ... (PN-1 op PN)))). The version (2) is a left fold where the expansion is taken from the left. The (3) and (4) versions are almost the value except for an init value. Only some operators (+,*,&,|,&&,||, ,) have defined init values and can be used with the versions (1) and (2). The other operators can only be used with an init value. 

For instance, here is how we could write the sum functions with fold expressions: 

.. code:: c++

    template<typename... T>
    auto fold_sum_1(T... s){
        return (... + s);
    }

I personally think it is much better, it clearly states our intent and does not need recursion. By default, the init value used for addition is 0, but you can change it: 

.. code:: c++

    template<typename... T>
    auto fold_sum_2(T... s){
        return (1 + ... + s);
    }

This will yield the sum of the elements plus one.

This can be also very practical to print some elements for instance:

.. code:: c++

    template<typename ...Args>
    void print_1(Args&&... args) {
        (std::cout << ... << args) << '\n';
    }

And this can even be used when doing Template Metaprogramming, for instance here is a TMP version of the and operator: 

.. code:: c++

    template<bool... B>
    struct fold_and : std::integral_constant<bool, (B && ...)> {};


Conclusion
++++++++++

C++17 fold expressions are a really nice additions to the language that makes working with variadic templates much easier. This already makes me wish for C++17 release :)

The source code for the examples are available on Github: https://github.com/wichtounet/articles/blob/master/src/fold_expressions.cpp  
