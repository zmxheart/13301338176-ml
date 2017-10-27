For one of my projects, I needed to evaluate a square root at compile-time.
There are several ways to implement it and some are better than the others. 

In this post, I'll show several versions, both with Template Metaprogramming
(TMP) and constexpr functions. 

Naive version
-------------

The easiest way to implement it is to enumerate the integers until we find two
integers that when multiplied are equal to our number. This can easily be 
implemented in C++ with class template and partial specialization: 

.. code:: c++

    template <std::size_t N, std::size_t I=1>
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I> {};

    template<std::size_t N>
    struct ct_sqrt<N,N> : std::integral_constant<std::size_t, N> {};

Really easy, isn't it ? If we test it with 100, it gives 10. But, if we try with
higher values, we are going to run into problem. For instance, when compiled
with 289, here is what clang++ gives me:: 

    src/sqrt/tmp.cpp:5:64: fatal error: recursive template instantiation exceeded maximum depth of 256
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I > {};
                                                                   ^
    src/sqrt/tmp.cpp:5:64: note: in instantiation of template class 'ct_sqrt<289, 257>' requested here
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I > {};
                                                                   ^
    src/sqrt/tmp.cpp:5:64: note: in instantiation of template class 'ct_sqrt<289, 256>' requested here
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I > {};
                                                                   ^
    src/sqrt/tmp.cpp:5:64: note: in instantiation of template class 'ct_sqrt<289, 255>' requested here
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I > {};
                                                                   ^
    src/sqrt/tmp.cpp:5:64: note: in instantiation of template class 'ct_sqrt<289, 254>' requested here
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I > {};
                                                                   ^
    src/sqrt/tmp.cpp:5:64: note: in instantiation of template class 'ct_sqrt<289, 253>' requested here
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I > {};
                                                                   ^
    src/sqrt/tmp.cpp:5:64: note: (skipping 247 contexts in backtrace; use -ftemplate-backtrace-limit=0 to see all)
    src/sqrt/tmp.cpp:5:64: note: in instantiation of template class 'ct_sqrt<289, 5>' requested here
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I > {};
                                                                   ^
    src/sqrt/tmp.cpp:5:64: note: in instantiation of template class 'ct_sqrt<289, 4>' requested here
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I > {};
                                                                   ^
    src/sqrt/tmp.cpp:5:64: note: in instantiation of template class 'ct_sqrt<289, 3>' requested here
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I > {};
                                                                   ^
    src/sqrt/tmp.cpp:5:64: note: in instantiation of template class 'ct_sqrt<289, 2>' requested here
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I > {};
                                                                   ^
    src/sqrt/tmp.cpp:11:18: note: in instantiation of template class 'ct_sqrt<289, 1>' requested here
        std::cout << ct_sqrt<289>::value << std::endl;
                     ^
    src/sqrt/tmp.cpp:5:64: note: use -ftemplate-depth=N to increase recursive template instantiation depth
    struct ct_sqrt : std::integral_constant<std::size_t, (I*I<N) ? ct_sqrt<N,I+1>::value : I > {};
                                                                   ^

And it is only to compute the square root for 289, not a big number. We could of
course increase the template depth limit (-ftemplate-depth=X), but that would
only get us a bit farther. If you try with g++, you should see that this works,
that is because g++ has a higher template depth limit (900 for 4.8.2 on my
machine) where clang has a default limit of 256. It can be noted too that with
g++ no context is skipped, therefore the error is quite long.

Now that C++11 gives us constexpr function, we can rewrite it more cleanly: 

.. code:: c++

    constexpr std::size_t ct_sqrt(std::size_t n, std::size_t i = 1){
        return n == i ? n : (i * i < n ? ct_sqrt(n, i + 1) : i);
    }

Much nicer :) And it works perfectly with 289. And it works quite well up to a
large number. But it still fails once we git large numbers. For instance, here
is what clang++ gives me with 302500 (550*550)::

    src/sqrt/constexpr.cpp:8:36: error: constexpr variable 'result' must be initialized by a constant expression
    static constexpr const std::size_t result = ct_sqrt(SQRT_VALUE);
                                       ^        ~~~~~~~~~~~~~~~~~~~
    src/sqrt/constexpr.cpp:5:38: note: constexpr evaluation exceeded maximum depth of 512 calls
        return n == i ? n : (i * i < n ? ct_sqrt(n, i + 1) : i);
                                         ^
    src/sqrt/constexpr.cpp:5:38: note: in call to 'ct_sqrt(302500, 512)'
    src/sqrt/constexpr.cpp:5:38: note: in call to 'ct_sqrt(302500, 511)'
    src/sqrt/constexpr.cpp:5:38: note: in call to 'ct_sqrt(302500, 510)'
    src/sqrt/constexpr.cpp:5:38: note: in call to 'ct_sqrt(302500, 509)'
    src/sqrt/constexpr.cpp:5:38: note: in call to 'ct_sqrt(302500, 508)'
    src/sqrt/constexpr.cpp:5:38: note: (skipping 502 calls in backtrace; use -fconstexpr-backtrace-limit=0 to see all)
    src/sqrt/constexpr.cpp:5:38: note: in call to 'ct_sqrt(302500, 5)'
    src/sqrt/constexpr.cpp:5:38: note: in call to 'ct_sqrt(302500, 4)'
    src/sqrt/constexpr.cpp:5:38: note: in call to 'ct_sqrt(302500, 3)'
    src/sqrt/constexpr.cpp:5:38: note: in call to 'ct_sqrt(302500, 2)'
    src/sqrt/constexpr.cpp:8:45: note: in call to 'ct_sqrt(302500, 1)'
    static constexpr const std::size_t result = ct_sqrt(SQRT_VALUE);
                                                ^

Again, we run into the limits of the compiler. And again, the limit can be
change with fconstexpr-backtrace-limit=X. With g++, the result is the same
(without the skipped part, which makes the error horribly long), but the command
to change the depth is -fconstexpr-depth=X. 

So, if we need to compute higher square roots at compile-time, we need a better
version. 

Binary Search version
---------------------

To find the good square root, you don't need to iterate through all the numbers
from 1 to N, you can perform a binary search to find the numbers to test. I
found a very nice implementation by John Khvatov (`source <http://jkhvatov.blogspot.ch/2009/11/c-compile-time-square-root-sqrt-using.html>`_). 

Here is an adaptation of its code: 

.. code:: c++

    #define MID(a, b) ((a+b)/2)
    #define POW(a) (a*a)
    
    template<std::size_t res, std::size_t l = 1, std::size_t r = res>
    struct ct_sqrt;
    
    template<std::size_t res, std::size_t r>
    struct ct_sqrt<res, r, r> : std::integral_constant<std::size_t, r> {};
    
    template <std::size_t res, std::size_t l, std::size_t r>
    struct ct_sqrt : std::integral_constant<std::size_t, ct_sqrt<res, 
            (POW(MID(r, l)) >= res ? l : MID(r, l)+1), 
            (POW(MID(r, l)) >= res ? MID(r, l) : r)>::value> {};

With smart binary search, you can reduce A LOT the numbers that needs to be
tested in order to find the answer. It very easily found the answer for 302500.
It can find the square root of almost all integers, until it fails due to
overflows. I think it is really great :) 

Of course, we can also do the constexpr version: 

.. code:: c++

    static constexpr std::size_t ct_mid(std::size_t a, std::size_t b){
        return (a+b) / 2;
    }

    static constexpr std::size_t ct_pow(std::size_t a){
        return a*a;
    }

    static constexpr std::size_t ct_sqrt(std::size_t res, std::size_t l, std::size_t r){
        return
            l == r ? r
            : ct_sqrt(res, ct_pow(
                ct_mid(r, l)) >= res ? l : ct_mid(r, l) + 1, 
                ct_pow(ct_mid(r, l)) >= res ? ct_mid(r, l) : r);
    }

    static constexpr std::size_t ct_sqrt(std::size_t res){
        return ct_sqrt(res, 1, res);
    }

Which is a bit more understandable. It works the same way than the previous one
and is only limited by numeric overflow. 

C++14 Fun
---------

In C++14, the constraints on constexpr functions have been highly relaxed, we
can now use variables, if/then/else statements, loops and so on... in constexpr
functions making them much more readable. Here is the C++14 version of the
previous code: 

.. code:: c++

    static constexpr std::size_t ct_sqrt(std::size_t res, std::size_t l, std::size_t r){
        if(l == r){
            return r;
        } else {
            const auto mid = (r + l) / 2;

            if(mid * mid >= res){
                return ct_sqrt(res, l, mid);
            } else {
                return ct_sqrt(res, mid + 1, r);
            }
        }
    }

    static constexpr std::size_t ct_sqrt(std::size_t res){
        return ct_sqrt(res, 1, res);
    }

I think this version is highly superior than the previous version. Don't you
think ?

It performs exactly the same as the previous. This can only be done in clang for
now, but that will come eventually to gcc too. 

Conclusion
----------

As you saw, there are several ways to compute a square root at compile-time in
C++. The constexpr versions are much more readable and generally more scalable
than the template metaprogramming version. Moreover, now, with C++14, we can
write constexpr functions almost as standard function, which makes really great. 

I hope that is is helpful to some of you :)

All the sources are available on Github: `https://github.com/wichtounet/articles/tree/master/src/sqrt <https://github.com/wichtounet/articles/tree/master/src/sqrt>`_
