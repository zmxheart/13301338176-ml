My Deep Learning Library (DLL) project is a C++ library for training and using
artificial neural networks (you can take a look at
`this post about DLL <https://baptiste-wicht.com/posts/2017/07/update-on-deep-learning-library-dll-dropout-batch-normalization-adaptive-learning-rates.html>`_
if you want more information).

While I made a lot of effort to make it as fast as possible to train and run
neural networks, the compilation time has been steadily going up and is becoming
quite annoying. This library is heavily templated and all the matrix operations
are done using my Expression Templates Library (ETL) which is more than
template-heavy itself.

In this post, I'll present two techniques with which I've been able to reduce
the total compilation of the DLL unit tests by up to 38%.

.. TEASER_END

Reduce overhead of Expression Templates Library
###############################################

One of the feature I'm using a lot in ETL is that the implementation of each
algorithm can be chosen in the code directly.

For example, for a matrix multiplication, to force the vectorized
implementation:

.. code:: c++

    C = selected_helper(etl::gemm_impl::VEC, A * B);

This works great and simplifies a lot the testing and benchmarking of the
application. Nevertheless, this complicates a lot the selection of the algorithm
and incurs quite some overhead on compilation time. I've come to realize that
for most usage of the library this is not necessary. This is something that
should only be used by the tests and benchmark. Therefore, I disabled this
behaviour by default and added a macro to enable this behaviour
(:code:`ETL_MANUAL_SELECT`). This simplifies a lot the code in the case the
macro is not defined. In fact, it also greatly reduces the compilation time of
ETL usages where manual selection is not enabled, basically all ETL usages. In
one of my ETL examples, the compilation time has gone down from about 30 seconds
to about 15 seconds. I was actually quite surprised by the impact of the change.
Therefore, I updated DLL to see the difference with this new version of ETL.
I tested the difference with GCC 7.1 and clang 3.9 with all the possible options
from ETL. Nothing changed in DLL, only the ETL library was updated. Here are the
results with DLL's unit tests:

+------------------+-----------+-------------+-------------+---------------+
| Compiler         | GCC Debug | GCC Release | Clang Debug | Clang Release |
+==================+===========+=============+=============+===============+
| Base version     | 560s      | 1188s       | 861s        | 1179s         |
+------------------+-----------+-------------+-------------+---------------+
| Manual selection | 490s      | 866s        | 704s        | 813s          |
+------------------+-----------+-------------+-------------+---------------+
| Improvement      | 12.5%     | 27.5%       | 18%         | 31%           |
+------------------+-----------+-------------+-------------+---------------+

As you can see, the compilation time improvement are quite substantial :)

I'm really happy with these results. With not that much changes in ETL, the
compilation time of DLL has been nicely reduced.

Use if constexpr for algorithm selection
########################################

Since C++17 has been supported in compilers, I've been wanting to play around
with :code:`if constexpr`. Before, when necessary, I've been emulated if
constexpr with SFINAE and a lambda, but it's really not nice code. However, it
already helped me in the past, for instance
`a single static_if reduced the compilation time of DLL by about 30% <https://baptiste-wicht.com/posts/2016/01/improve-dll-and-etl-compile-time-further.html>`_.
I'm trying not to abuse it in the code, I've reserved it for a very instances in
DLL's code. However, :code:`if constexpr` is much nicer than an emulated
version. The only problem with it is that you need a really recent compiler for
it. Especially with GCC, you need GCC 7.1 that has been available since May this
year only. I'd rather not force too strong constraints on DLL requirements.

Nevertheless, I've been annotating a lot of my :code:`if` with constexpr
annotations in the form of comments (:code:`if /*constexpr*/`) so that I can
switch back and forth to see the impact of C++17 :code:`if constexpr` on my
compilation time. Interestingly, only enabling C++17 as a compiler option made
compilation slower by about 2%. Moreover, I've also found a few places in my
code where C++17 was breaking. For instance, C++17 Ranges is adding a function
:code:`std::size(range)` that is ambiguous with :code:`etl::size(matrix)` in
some cases where ADL is concerned.

Now that selection is much simpler in ETL, the complete selection can now be
made constexpr and resolved at compile time.And therefore, all the branches can
be resolved at compile time. Hopefully, this should avoid a lot of algorithms
implementation to be instantiated.

Here are the results I've obtained:

+--------------------------+-----------+-------------+-------------+---------------+
| Compiler                 | GCC Debug | GCC Release | Clang Debug | Clang Release |
+==========================+===========+=============+=============+===============+
| Base version             | 560s      | 1188s       | 861s        | 1179s         |
+--------------------------+-----------+-------------+-------------+---------------+
| Manual selection         | 490s      | 866s        | 704s        | 813s          |
+--------------------------+-----------+-------------+-------------+---------------+
| C++17 if constexpr (ETL) | 444s      | 767s        | 663s        | 731s          |
+--------------------------+-----------+-------------+-------------+---------------+
| Improvement              | 10%       | 11.5%       | 6%          | 10%           |
+--------------------------+-----------+-------------+-------------+---------------+

By enabling C++17 and transforming some :code:`if` into :code:`if constexpr`,
compilation time was reducing by up to 11.5%. It's pretty good, but I would have
expected a bit more. Nevertheless, it's still an upgrade :) We can see that the
impact is more important for release compilation. It makes sense since it should
remove quite a lot of code hard to optimize.

I've also started experimenting with :code:`if constexpr` in DLL itself.
Unfortunately, this didn't have as much effect as I wanted. Here are some
preliminary results:

+--------------------------+-----------+-------------+-------------+---------------+
| Compiler                 | GCC Debug | GCC Release | Clang Debug | Clang Release |
+==========================+===========+=============+=============+===============+
| Base version             | 560s      | 1188s       | 861s        | 1179s         |
+--------------------------+-----------+-------------+-------------+---------------+
| Manual selection         | 490s      | 866s        | 704s        | 813s          |
+--------------------------+-----------+-------------+-------------+---------------+
| C++17 if constexpr (ETL) | 444s      | 767s        | 663s        | 731s          |
+--------------------------+-----------+-------------+-------------+---------------+
| C++17 if constexpr (DLL) | 434s      | 765s        | 658s        | 725s          |
+--------------------------+-----------+-------------+-------------+---------------+
| Improvement              | 2.3%      | 0.3%        | 0.75%       | 1%            |
+--------------------------+-----------+-------------+-------------+---------------+

As you can see, the improvements are pretty small, almost not significant in
some cases. Nevertheless, I believe there is some improvements that is possible.
I've got a few other cases that I plan to refactor to allow for more
:code:`if constexpr` action. I'll see about that in the coming months.

Conclusion
##########

By disabling manual selection by default in my Expression Templates Library
(ETL) and by making use of C++17 :code:`if constexpr` feature, I've been able to
reduce the compilation time of DLL's unit test suite by up to 38% in the best
case.

So far, only the improvements to ETL are online, the C++17 improvements are only
present on the form of comments that I switch on and off when I do tests like
that. I think I'm still gonna wait a bit until I enable C++17 for ETL, but it's
probably gonna be enabled for ETL 1.3. And, by the way, the version 1.2 of ETL
should soon be released.

You can take a look at my libraries on Github:

* `Expression Templates Library (ETL) <https://github.com/wichtounet/etl>`_
* `Deep Learning Library (DLL) <https://github.com/wichtounet/dll>`_

As ever, don't hesitate if you have any question or comment on the matter, to
post a comment on this blog or on Github :)
