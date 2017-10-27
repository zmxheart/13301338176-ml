It's been a while since I've `released the version 1.0 of ETL <https://baptiste-wicht.com/posts/2016/09/expression-templates-library-etl-10.html>`_. There is some work to do before I release the next version, but I wanted to give you a quick update on what has been going on for ETL in the last months. There has been a lot of changes in the library and the next version will be a major update when I'm done with some refactorings and improvements.

Thanks to my thesis supervisor, the project now has a logo:

.. image:: /images/logo.png
   :align: center
   :alt: ETL Logo

There are quite a few new features, although probably nothing really major. The
support for square root has been improved with cubic root and inverse root.
Vectors can now be transformed using floor and ceil. Cross product of vector has
been implemented as well. Batched outer product and batched bias averaging (for
machine learning) are now supported. Reductions have also been improved with
absolute sum and mean (asum/asum) support and min_index and max_index. argmax
can now be used to get the max index in each sub dimensions. Matrix can now be
decomposed into their Q/R decomposition rather than only their PALU
decomposition. The matrices can now be sliced by getting only a sub part of the
matrix. The pooling operators  have also been improved with stride and padding
support. Matrices and vectors can also be shuffled. Moreover, a few adapters
are now available for hermitian matrices, symmetric matrices and lower and upper
matrices. So far the support for these adapters is not huge, but they are
guaranteed to validate their constraints.

Several operations have been optimized for speed. All the pooling and upsample
operators are now parallelized and the most used kernel (2x2 pooling) is now
more optimized. 4D convolution kernels (for machine learning) have been greatly
improved. There are now very specialized vectorized kernels for classic kernel
configurations (for instance 3x3 or 5x5) and the selection of implementations is
now smarter than before. The support of padding now much better than before for
small amount of padding. Moreover, for small kernels the full convolution can
now be evaluated using the valid convolution kernels directly with some padding,
for much faster overall performance. Matrix-matrix multiplication with
transposed matrices is now much faster when using BLAS kernels. Indeed, the
transposition is not performed but handled inside the kernels. Moreover, the
performance of the transposition itself is also much faster. Finally, accesses
to 3D and 4D matrices is now much faster than before.

The parallelization feature of ETL has been completely reworked. Before, there
was a thread pool for each algorithm that was parallelized. Now, there is
a global thread engine with one thread pool. Since parallelization is not nested
in ETL, this improves performance slightly by greatly diminishing the number of
threads that are created throughout an application.

Vectorization has also been greatly improved in ETL. Integer operations are now
automatically vectorized on processors that support this. The automatic
vectorizer now is able to use non-temporal stores for very large operations.
A non-temporal store bypasses the cache, thus gaining some time. Since very
large matrices do not fit in cache, this is a net gain. Moreover, the alignment
detection in the automatic vectorizer has also been improved. Support for
Fused-Multiply-Add (FMA) operations has also been integrated in the algorithms
that can make use of it. The matrix-matrix multiplications and vector-matrix
multiplications now have optimized vectorized kernels. They also have versions
for column-major matrices now. The old egblas version of the gemm, based on BLIS
kernels, has been removed since it was only supporting double-precision and was
not faster than the new vectorized algorithm. I plan to reintegrate a version of
the GEMM based on BLIS in the future but with more optimizations and support for
all precisions and integers. The sum and the dot product now also have
specialized vectorized implementations. The min and max operations are now
automatically-vectorized.

The GPU has also been almost completely reworked. Now, operations can be chained
without any copies between GPU and CPU. Several new operations have also been
added with support to GPU. Moreover, to complement operations that are not
available in any of the supported NVIDIA libraries, I've created a simple
library that can be used to add a few more GPU operations. Nevertheless a lot of
operations are still missing and only algorithms are available not expressions
(such as c = a + b * 1.0) that are entirely computed on CPU. I have plans to
improve that further, but probably not before the version 1.2.

There also have been a lot of refactorings in the code of the library. A lot of
expressions now have less overhead and are specialized for performance.
Moreover, temporary expressions are currently being reworked in order to be more
simple and maintainable and easier to optimize in the future.

Finally, there also was quite a few bug fixes. Most of them have been found by
the use of the library in the Deep Learning Library (DLL) project.
