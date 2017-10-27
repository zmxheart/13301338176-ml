I've just released the first official version of my Expression Templates Library
(ETL for short): The version 1.0.

Until now, I was using a simple rolling release model, but I think it's now time
to switch to some basic versioning. The project is now at a stable state.

ETL 1.0 has the following main features:

* Smart Expression Templates
* Matrix and vector (runtime-sized and compile-time-sized)
* Simple element-wise operations
* Reductions (sum, mean, max, ...)
* Unary operations (sigmoid, log, exp, abs, ...)
* Matrix multiplication
* Convolution (1D and 2D and higher variations)
* Max Pooling
* Fast Fourrier Transform
* Use of SSE/AVX to speed up operations
* Use of BLAS/MKL/CUBLAS/CUFFT/CUDNN libraries to speed up operations
* Symmetric matrix adapter (experimental)
* Sparse matrix (experimental)

Examples
========

Here is an example of expressions in ETL:

.. code:: cpp

    etl::fast_matrix<float, 2, 2, 2> a = {1.1, 2.0, 5.0, 1.0, 1.1, 2.0, 5.0, 1.0};
    etl::fast_matrix<float, 2, 2, 2> b = {2.5, -3.0, 4.0, 1.0, 2.5, -3.0, 4.0, 1.0};
    etl::fast_matrix<float, 2, 2, 2> c = {2.2, 3.0, 3.5, 1.0, 2.2, 3.0, 3.5, 1.0};

    etl::fast_matrix<float, 2, 2, 2> d(2.5 * ((a >> b) / (log(a) >> abs(c))) / (1.5 * scale(a, sign(b)) / c) + 2.111 / log(c));

Or another I'm using in my neural networks library:

.. code:: cpp

    h = etl::sigmoid(b + v * w)

In that case, the vector-matrix multiplication will be executed using a BLAS
kernel (if ETL is configured correclty) and the assignment, the sigmoid and the
addition will be automatically vectorized to use either AVX or SSE depending
on the machine.

Or with a convolutional layer and a ReLU activation function:

.. code:: cpp

    etl::reshape<1, K, NH1, NH2>(h_a) = etl::conv_4d_valid_flipped(etl::reshape<1, NC, NV1, NV2>(v_a), w);
    h = max(b_rep + h_a, 0.0);

This will automatically be computed either with NVIDIA CUDNN (if available) or
with optimized SSE/AVX kernels.

For more information, you can take a look at the `Reference <https://github.com/wichtounet/etl/wiki>`_ on the wiki.

Next version
============

For the next version, I'll focus on several things:

* Improve matrix-matrix multiplication kernels when BLAS is not available. There
  is a lot of room for improvement here
* Complete support for symmetric matrices (currently experimental)
* Maybe some new adapters such as Hermitian matrices
* GPU improvements for some operations that can be done entirely on GPU
* New convolution performanceimprovements
* Perhaps more complete parallel support for some implementations
* Drop some compiler support to use full C++14 support

Download ETL
============

You can download ETL `on Github <https://github.com/wichtounet/etl>`_. If you
only interested in the 1.0 version, you can look at the
`Releases pages <https://github.com/wichtounet/etl/releases>`_ or clone the tag
1.0. There are several branches:

* *master* Is the eternal development branch, may not always be stable
* *stable* Is a branch always pointing to the last tag, no development here

For the future release, there always will tags pointing to the corresponding
commits. I'm not following the git flow way, I'd rather try to have a more
linear history with one eternal development branch, rather than an useless
develop branch or a load of other branches for releases.

Don't hesitate to comment this post if you have any comment on this library or
any question. You can also open an Issue on Github if you have a problem using
this library or propose a Pull Request if you have any contribution you'd like
to make to the library.

Hope this may be useful to some of you :)
