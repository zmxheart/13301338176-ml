Update: For a comparison against more modern compiler versions, you can read: `zapcc C++ compilation speed against gcc 5.4 and clang 3.9 <http://baptiste-wicht.com/posts/2016/12/zapcc-cpp-compilation-speed-against-gcc-54-and-clang-39.html>`_

I just joined the private beta program of zapcc. Zapcc is a c++ compiler, based
on Clang which aims at being much faster than other C++ compilers. How they are
doing this is using a caching server that saves some of the compiler structures,
which should speed up compilation a lot. The private beta is free, but once the
compiler is ready, it will be a commercial compiler.

Every C++ developer knows that compilation time can quickly be an issue when
programs are getting very big and especially when working with template-heavy
code.

To benchmark this new compiler, I use my Expression Template Library
(`ETL <https://github.com/wichtounet/etl/>`_). This is a purely header-only
library with lots of templates. There are lots of test cases which is what I'm
going to compile. I'm going to compare against Clang-3.7 and gcc-4.9.3.

I have configured zapcc to let is use 2Go RAM per caching server, which is the
maximum allowed. Moreover, I killed the servers before each tests.

Debug build
+++++++++++

Let's start with a debug build. In that configuration, there is no optimization
going on and several of the features of the library (GPU, BLAS, ...) are
disabled. This is the fastest way to compile ETL. I gathered this result on
a 4 core, 8 threads, Intel processor, with an SSD.

The following table presents the results with different number of threads and
the difference of zapcc compared to the other compilers:

+----------------------+------+-------+------+------+------+
| Compiler             | -j1  | -j2   | -j4  | -j6  | -j8  |
+======================+======+=======+======+======+======+
| g++-4.9.3            | 350s | 185s  | 104s | 94s  | 91s  |
+----------------------+------+-------+------+------+------+
| clang++-3.7          | 513s | 271s  | 153s | 145s | 138s |
+----------------------+------+-------+------+------+------+
| zapcc++              | 158s | 87s   | 47s  | 44s  | 42s  |
+----------------------+------+-------+------+------+------+
|     Speedup VS Clang | 3.24 | 3.103 | 3.25 | 3.29 | 3.28 |
+----------------------+------+-------+------+------+------+
|     Speedup VS GCC   | 2.21 | 2.12  | 2.21 | 2.13 | 2.16 |
+----------------------+------+-------+------+------+------+

The result is pretty clear! zapcc is around **three times faster than Clang** and around
**two times faster than GCC**. This is pretty impressive!

For those that think than Clang is always faster than GCC, keep in mind that
this is not the case for template-heavy code such as this library. In all my
tests, Clang has always been slower and much memory hungrier than GCC on
template-heavy C++ code. And sometimes the difference is very significant.

Interestingly, we can also see that going past the physical cores is not really
interesting on this computer. On some computer, the speedups are interesting,
but not on this one. Always benchmark!

Release build
+++++++++++++

We have seen the results on a debug build, let's now compare on something a bit
more timely, a release build with all options of ETL enabled (GPU, BLAS, ...),
which should make it significantly longer to compile.

Again, the table:

+--------------------+------+------+------+------+------+
| Compiler           | -j1  | -j2  | -j4  | -j6  | -j8  |
+====================+======+======+======+======+======+
| g++-4.9.3          | 628s | 336s | 197s | 189s | 184s |
+--------------------+------+------+------+------+------+
| clang++-3.7        | 663s | 388s | 215s | 212s | 205s |
+--------------------+------+------+------+------+------+
| zapcc++            | 515s | 281s | 173s | 168s | 158s |
+--------------------+------+------+------+------+------+
|   Speedup VS Clang | 1.28 | 1.38 | 1.24 | 1.26 | 1.29 |
+--------------------+------+------+------+------+------+
|   Speedup VS GCC   | 1.21 | 1.30 | 1.13 | 1.12 | 1.16 |
+--------------------+------+------+------+------+------+

This time, we can see that the difference is much lower. Zapcc is **between 1.2
and 1.4 times faster than Clang** and **between 1.1 and 1.3 times faster than
GCC**. This shows that most of the speedups from zapcc are in the front end of
the compiler. This is not a lot but still significant over long builds,
especially if you have few threads where the absolute difference would be
higher.

We can also observe that Clang is now almost on par with GCC which shows that
optimization is faster in Clang while front and backend is faster in gcc.

You also have to keep in mind that zapcc memory usage is higher than Clang
because of all the caching. Moreover, the server are still up in between
compilations, so this memory usage stays between builds, which may not be what
you want.

As for runtime, I have not seen any significant difference in performance
between the clang version and the zapcc. According to the official benchmarks
and documentation, there should not be any difference in that between zapcc and
the version of clang on which zapcc is based.

Incremental build
+++++++++++++++++

Normally, zapcc should shine at incremental building, but I was unable to show
any speedup when changing a single without killing the zapcc servers. Maybe
I did something wrong in my usage of zapcc.

Conclusion
++++++++++

In conclusion, we can see that zapcc is always faster than both GCC and Clang,
on my template-heavy library. Moreover, on debug builds, it is much faster than
any of the two compilers, being more than 2 times faster than GCC and more than
3 times faster than clang. This is really great. Moreover, I have not seen any
issue with the tool so far, it can seamlessly replace Clang without problem.

It's a bit weird that you cannot allocate more than 2Go to the zapcc servers.

For a program, that's really impressive. I hope that they are continuing the
good work and especially that this motivates other compilers to improve the
speed of compilation (especially of templates).

If you want more information, you can go to the
`official website of zapcc <https://www.zapcc.com/>`_
