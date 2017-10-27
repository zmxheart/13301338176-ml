A week ago, I compared the `compilation time performance of zapcc against gcc-4.9.3 and clang-3.7 <http://baptiste-wicht.com/posts/2016/11/zapcc-a-faster-cpp-compiler.html>`_. On debug builds, zapcc was about 2 times faster than gcc and 3 times faster than clang. In this post, I'm going to try some more recent compilers, namely gcc 5.4 and clang 3.9 on the same project. If you want more information on zapcc, read the previous posts, this post will concentrate on results.

Again, I use my Expression Template Library
(`ETL <https://github.com/wichtounet/etl/>`_). This is a purely header-only
library with lots of templates. I'm going to compile the full test cases.

The results of the two articles are not directly comparable, since they were
obtained on two different computers. The one on which the present results are
done has a less powerful and only 16Go of RAM compared to the 32Go of RAM of my
build machine. Also take into account that that the present results were
obtained on a Desktop machine, there can be some perturbations from background
tasks.

Just like on the previous results, it does not help using more threads than
physical cores, therefore, the results were only computed on up to 4 cores on
this machine.

The link time is not taken into account on the results.

Debug build
+++++++++++

Let's start with the result of the debug build.

+----------------------+------+------+------+
| Compiler             | -j1  | -j2  | -j4  |
+======================+======+======+======+
| g++-5.4.0            | 469s | 230s | 130s |
+----------------------+------+------+------+
| clang++-3.9          | 710s | 371s | 218s |
+----------------------+------+------+------+
| zapcc++              | 214s | 112s | 66s  |
+----------------------+------+------+------+
|     Speedup VS Clang | 3.31 | 3.31 | 3.3  |
+----------------------+------+------+------+
|     Speedup VS GCC   | 2.19 | 2.05 | 1.96 |
+----------------------+------+------+------+

The results are almost the same as the previous test. zapcc is 3.3 times faster
to compile than Clang and around 2 times faster than GCC. It seems that GCC 5.4
is a bit faster than GCC 4.9.3 while clang 3.9 is a bit slower than clang 3.7,
but nothing terribly significant.

Overall, for debug builds, zapcc can bring a very significant improvement to
your compile times.

Release build
+++++++++++++

Let's see what is the status of Release builds. Since the results are comparable
between the numbers of threads, the results here are just for one thread.

This is more time consuming since a lot of optimizations are enabled and more
features from ETL are enabled as well.

+----------------------+------+
| Compiler             | -j1  |
+======================+======+
| g++-5.4.0            | 782s |
+----------------------+------+
| clang++-3.9          | 960s |
+----------------------+------+
| zapcc++              | 640s |
+----------------------+------+
|     Speedup VS Clang | 1.5  |
+----------------------+------+
|     Speedup VS GCC   | 1.22 |
+----------------------+------+

On a release build, the speedups are much less interesting. Nevertheless, they
are still significant. zapcc is still 1.2 times faster than gcc and 1.5 times
faster than clang. Then speedup against clang 3.9 is significantly higher than
it was on my experiment with clang 3.7, it's possible that clang 3.9 is slower
or simply has new optimization passes.

Conclusion
++++++++++

The previous conclusion still holds with modern version of compilers: zapcc is
much faster than other compilers on Debug builds of template heavy code. More
than 3 times faster than clang-3.9 and about 2 times faster than gcc-5.4. Since
it's based on clang, there should not be any issue compiling projects that
already compile with a recent clang. Even though the speedups are less
interesting on a release build, it is still significantly, especially compared
against clang.

I'm really interested in finding out what will be the pricing for zapcc once
out of the beta or if they will be able to get even faster!

For the comparison with gcc 4.9.3 and clang 3.7, you can have a look at
`this article <http://baptiste-wicht.com/posts/2016/11/zapcc-a-faster-cpp-compiler.html>`_.

If you want more information about zapcc, you can go to the
`official website of zapcc <https://www.zapcc.com/>`_
