If you remember, I recently wrote about `zapcc C++ compilation speed against gcc 5.4 and clang 3.9 <http://baptiste-wicht.com/posts/2016/12/zapcc-cpp-compilation-speed-against-gcc-54-and-clang-39.html>`_ in which I was comparing the beta version of zapcc against gcc and clang.

I just been informed that zapcc was just released in version 1.0. I though it
was a good occasion to test it again. It will be compared against gcc-4.9,
gcc-5.3 and clang-3.9. This version is based on the trunk of clang-5.0.

Again, I will use my Expression Template Library (`ETL
<https://github.com/wichtounet/etl/>`_) project. This is a purely header-only
library with lots of templates. I'm going to compile the full test cases. This
is a perfect example for long compilation times.

The current tests are made on the last version of the library and with slightly
different parameters for compilation, therefore the absolute times are not
comparable, but the speedups should be comparable.

Just like last time, I have configured zapcc to let is use 2Go RAM per caching
server, which is the maximum allowed. Moreover, I killed the servers before each
tests.

Debug results
=============

Let's start with a debug build, with no optimizations enabled. Every build will
use four threads. This is the equivalent of doing make -j4 debug/bin/etl_test
without the link step.

+------------------------+---------+
| Compiler               |         |
+========================+=========+
| g++-4.9.3              | 190.09s |
+------------------------+---------+
| g++-5.3.0              | 200.92s |
+------------------------+---------+
| clang++-3.9            | 313.85  |
+------------------------+---------+
| zapcc++                | 81.25   |
+------------------------+---------+
|     Speedup VS Clang   | 3.86    |
+------------------------+---------+
|     Speedup VS GCC-5.3 | 2.47    |
+------------------------+---------+
|     Speedup VS GCC-4.9 | 2.33    |
+------------------------+---------+

The speedups are even more impressive than last time! zapcc is **almost four
times fast than clang-3.9** and around **2.5 times faster than GCC-5.3**.
Interestingly, we can see that gcc-5.3 is slighly slower than GCC-4.9.

It seems that they have the compiler even faster!

Release results
===============

Let's look now how the results are looking with optimizations enabled. Again,
every build will use four threads. This is the equivalent of doing make -j4
release_debug/bin/etl_test without the link step.

+------------------------+--------+
| Compiler               |        |
+========================+========+
| g++-4.9.3              | 252.99 |
+------------------------+--------+
| g++-5.3.0              | 264.96 |
+------------------------+--------+
| clang++-3.9            | 361.65 |
+------------------------+--------+
| zapcc++                | 237.96 |
+------------------------+--------+
|     Speedup VS Clang   | 1.51   |
+------------------------+--------+
|     Speedup VS GCC-5.3 | 1.11   |
+------------------------+--------+
|     Speedup VS GCC-4.9 | 1.06   |
+------------------------+--------+

We can see that this time the speedups are not as interesting as they were.
Very interestingly, it's the compiler that suffers the more from the
optimization overhead. Indeed, zapcc is three times slower in release mode than
it was in debug mode. Nevertheless, it still manages to beat the three other
compilers, by about 10\% for Gcc and 50\% than clang, which is already
interesting.

Conclusion
++++++++++

To conclude, we have observed that zapcc is always faster than the three
compilers tested in this experiment. Moreover, in debug mode, the speedups are
very significant, it was almost 4 times faster than clang and around 2.5 faster
than gcc.

I haven't seen any problem with the tool, it's like clang and it should generate
code of the same performance, but just compile it much faster. One problem
I have with zapcc is that it is not based on an already released version of
clang but on the trunk. That means it is hard to be compare with the exact same
version of clang and it is also a risk of running into clang bugs.

Although the prices have not been published yet, it is indicated on the website
that zapcc is free for non-commercial entities. Which is really great.

If you want more information, you can go to the
`official website of zapcc <https://www.zapcc.com/>`_
