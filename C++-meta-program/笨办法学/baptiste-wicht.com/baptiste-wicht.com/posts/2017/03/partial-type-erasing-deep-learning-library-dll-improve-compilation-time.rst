In a previous post, I compared the `compilation time on my Deep Learning Library (DLL) project with different compilers <https://baptiste-wicht.com/posts/2017/03/disappointing-zapcc-performance-on-deep-learning-library-dll.html>`_. I realized that the compilation times were quickly going unreasonable for this library, especially for compiling the unit cases which clearly hurts the development of the library. Indeed, you want to be able to run the unit tests reasonably quickly after you integrated new changes.

Reduce the compilation time
+++++++++++++++++++++++++++

The first thing I did was to split the compilation in three executables: one for
the unit tests, one for the various performance tests and one for the various other
miscellaneous tests. With this, it is much faster to compile only the unit test
cases.

But this can be improved significantly more. In DLL a network is a variadic
template containing the list of layers, in order. In DLL, there are two main
different ways of declaring a neural networks. In the first version, the fast
version, the layers directly know their sizes:

.. code:: cpp

    using network_t =
        dll::dbn_desc<
            dll::dbn_layers<
                dll::rbm_desc<28 * 28, 500, dll::momentum, dll::batch_size<64>>::layer_t,
                dll::rbm_desc<500    , 400, dll::momentum, dll::batch_size<64>>::layer_t,
                dll::rbm_desc<400    , 10,  dll::momentum, dll::batch_size<64>, dll::hidden<dll::unit_type::SOFTMAX>>::layer_t>,
            dll::trainer<dll::sgd_trainer>, dll::batch_size<64>>::dbn_t;

    auto network = std::make_unique<network_t>();
    network->pretrain(dataset.training_images, 10);
    network->fine_tune(dataset.training_images, dataset.training_labels, 10);

In my opinion, this is the best way to use DLL. This is the fastest and the
clearest. Moreover, the dimensions of the network can be validated at compile
time, which is always better than at runtime. However, the dimensions of the
network cannot be changed at runtime.  For this, there is a different version,
the dynamic version:

.. code:: cpp

    using network_t =
        dll::dbn_desc<
            dll::dbn_layers<
                dll::dyn_rbm_desc<dll::momentum>::layer_t,
                dll::dyn_rbm_desc<dll::momentum>::layer_t,
                dll::dyn_rbm_desc<dll::momentum, dll::hidden<dll::unit_type::SOFTMAX>>::layer_t>,
            dll::batch_size<64>, dll::trainer<dll::sgd_trainer>>::dbn_t;

    auto network = std::make_unique<network_t>();

    network->template layer_get<0>().init_layer(28 * 28, 500);
    network->template layer_get<1>().init_layer(500, 400);
    network->template layer_get<2>().init_layer(400, 10);
    network->template layer_get<0>().batch_size = 64;
    network->template layer_get<1>().batch_size = 64;
    network->template layer_get<2>().batch_size = 64;

    network->pretrain(dataset.training_images, 10);
    network->fine_tune(dataset.training_images, dataset.training_labels, 10);

This is a bit more verbose, but the configuration can be changed at runtime with
this system. Moreover, this is also faster to compile. On the other hand, there
is some performance slowdown.

There is also a third version that is a hybrid of the first version:

.. code:: cpp

    using network_t =
        dll::dyn_dbn_desc<
            dll::dbn_layers<
                dll::rbm_desc<28 * 28, 500, dll::momentum, dll::batch_size<64>>::layer_t,
                dll::rbm_desc<500    , 400, dll::momentum, dll::batch_size<64>>::layer_t,
                dll::rbm_desc<400    , 10,  dll::momentum, dll::batch_size<64>, dll::hidden<dll::unit_type::SOFTMAX>>::layer_t>,
            dll::trainer<dll::sgd_trainer>, dll::batch_size<64>>::dbn_t;

    auto network = std::make_unique<network_t>();
    network->pretrain(dataset.training_images, 10);
    network->fine_tune(dataset.training_images, dataset.training_labels, 10);

Only one line was changed compared to the first version, :code:`dbn_desc`
becomes :code:`dyn_dbn_desc`. What this changes is that all the layers are
automatically transformed into their dynamic versions and all the parameters are
propagated at runtime. This is a form a type erasing since the sizes will not be
propagated at compilation time. But this is simple since the types are simply
transformed from one type to another directly. Behind the scene, it's the
dynamic version using the front-end of the fast version. This is almost as fast
to compile as the dynamic version, but the code is much better. It executes the
same as the dynamic version.

If we compare the compilation time of the three versions when compiling a single
network and 5 different networks with different architectures, we get the
following results (with clang):

+-----------+----------+
| Model     | Time [s] |
+===========+==========+
| 1 Fast    | 30       |
+-----------+----------+
| 1 Dynamic | 16.6     |
+-----------+----------+
| 1 Hybrid  | 16.6     |
+-----------+----------+
| 5 Fast    | 114      |
+-----------+----------+
| 5 Dynamic | 16.6     |
+-----------+----------+
| 5 Hybrid  | 21.9     |
+-----------+----------+

Even with one single network, the compilation time is reduced by 44%. When five
different networks are compilation, time is reduced by 85%. This can be
explained easily. Indeed, for the hybrid and dynamic versions, the layers will
have the same type and therefore a lot of template instantiations will only be
done once instead of five times. This makes a lot of difference since almost
everything is template inside the library.

Unfortunately, this also has an impact on the runtime of the network:

+---------+--------------+-----------+
| Model   | Pretrain [s] | Train [s] |
+=========+==============+===========+
| Fast    | 195          | 114       |
+---------+--------------+-----------+
| Dynamic | 203          | 123       |
+---------+--------------+-----------+
| Hybrid  | 204          | 122       |
+---------+--------------+-----------+

On average, for dense models, the slowdown is between 4% and 8%. For
convolutional models, it is between 10% and 25%. I will definitely work on
trying to make the dynamic and especially the hybrid version faster in the
future, most on the work should be on the matrix library (ETL) that is used.

Since for test cases, a 20% increase in runtime is not really a problem, tests
being fast already, I decided to add an option to DLL so that everything can be
compiled by default in hybrid model. By using a compilation flag, all the
:code:`dbn_desc` are becoming :code:`dyn_dbn_desc` and therefore each used
network is becoming a hybrid network. Without a single change in the code, the
compilation time of the entire library can be significantly improved, as seen in
the next section.  This can also be used in user code to improve compilation
time during debugging and experiments and can be turned off for the final
training.

On my Continuous Integration system, I will build the system in both
configurations. This is not really an issue, since my personal machine at home
is more powerful than what I have available here.

Results
+++++++

On a first experiment, I measured the difference before and after this change on
the three executables of the library, with gcc:

+---------+----------+----------+----------+
| Model   | Unit [s] | Perf [s] | Misc [s] |
+=========+==========+==========+==========+
| Before  | 1029     | 192      | 937      |
+---------+----------+----------+----------+
| After   | 617      | 143      | 619      |
+---------+----------+----------+----------+
| Speedup | 40.03%   | 25.52%   | 33.93%   |
+---------+----------+----------+----------+

It is clear that the speedups are very significant! The compilation is between
25% and 40% faster with the new option. Overall, this is a speedup of 36%!
I also noticed that the compilation takes significantly less memory than before.
Therefore, I decided to rerun the compiler benchmark on the library. In the
previous experiment, zapcc was taking so much memory that it was impossible to
use more than one thread. Let's see how it is faring now. The time to compile
the full unit tests is computed for each compiler. Let's start in debug mode:

+-----------+---------+---------+---------+---------+
| Debug     | -j1     | -j2     | -j3     | -j4     |
+===========+=========+=========+=========+=========+
| clang-3.9 | 527     | 268     | 182     | 150     |
+-----------+---------+---------+---------+---------+
| gcc-4.9.3 | 591     | 303     | 211     | 176     |
+-----------+---------+---------+---------+---------+
| gcc-5.3.0 | 588     | 302     | 209     | 175     |
+-----------+---------+---------+---------+---------+
| zapcc-1.0 | **375** | **187** | **126** | **121** |
+-----------+---------+---------+---------+---------+

This time, zapcc is able to scale to four threads without problems. Moreover, it
is always the fastest compiler, by a significant margin, in this configuration.
It is followed by clang and then by gcc for which both versions are about the
same speed.

If we compile again in release mode:

+-----------+---------+---------+---------+-------+
| Release   | -j1     | -j2     | -j3     | -j4   |
+===========+=========+=========+=========+=======+
| clang-3.9 | 1201    | 615     | 421     | 356   |
+-----------+---------+---------+---------+-------+
| gcc-4.9.3 | 1041    | 541     | 385     | 321   |
+-----------+---------+---------+---------+-------+
| gcc-5.3.0 | 1114    | 579     | 412     | 348   |
+-----------+---------+---------+---------+-------+
| zapcc-1.0 | **897** | **457** | **306** | *306* |
+-----------+---------+---------+---------+-------+

The difference in compilation time is very large, it's twice slower to compile
with all optimizations enabled. It also takes significantly more memory. Indeed,
zapcc was not able to compile with 4 threads. Nevertheless, even the results
with three threads are better than the other compilers using four threads. zapcc
is clearly the winner again on this test, followed by gcc4-9 which is faster
than gcc-5.3 which is itself faster than clang. It seems that while clang is
better at frontend than gcc, it is slower for optimizations. Note that this may
also be an indication that clang performs more optimizations than gcc and may
not be slower.

Conclusion
++++++++++

By using some form of type erasing to simplify the templates types at compile
time, I was able to reduce the overall compilation time of my Deep Learning
Library (DLL) by 36%. Moreover, this can be done by switching a simple
compilation flag. This also very significantly reduce the memory used during the
compilation, allowing zapcc to to compile with up to three threads, compared
with only one before. This makes zapcc the fastest compiler again on this
benchmark. Overall, this will make debugging much easier on this library and
will save me a lot of time.

In the future, I plan to try to improve compilation time even more. I have a few
ideas, especially in ETL that should significantly improve the compilation time
but that will require a lot of time to implement, so that will likely have to
wait a while. In the coming days, I plan to work on the performance of DLL,
especially for stochastic gradient descent.

If you want more information on DLL, you can check out the
`dll Github repository <https://github.com/wichtounet/dll>`_.
