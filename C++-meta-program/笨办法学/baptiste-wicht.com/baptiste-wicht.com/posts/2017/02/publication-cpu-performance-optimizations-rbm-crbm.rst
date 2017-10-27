Recently, we have published a paper about performance optimizations that may
interest you.

The paper is `On CPU Performance Optimizations for Restricted Boltzmann Machine and Convolutional RBM <https://www.researchgate.net/publication/307908790_On_CPU_Performance_Optimization_of_Restricted_Boltzmann_Machine_and_Convolutional_RBM>`__, published in the Proceedings of the Artificial Neural Networks and Pattern Recognition workshop (ANNPR-2016). I've presented this paper in Germany, at Ulm.

Although most of the performance research going on is focused on GPU, there are
still of research laboratories that are only equipped with CPU and it remains
important to be as fast as possible on CPU. Moreover, this is something
I really like.

For this publication, I have tried to make my Restricted Boltzmann Machine (RBM)
and Convolutional RBM (CRBM) implementations in my DLL library as fast as
possible.

The first part of the article is about Restricted Boltzmann Machine (RBM) which
are a form of dense Artificial Neural Network (ANN). Their training is very
similar to that of the ANN with Gradient Descent. Four different network
configurations are being tested.

First, mini-batch training is shown to be much faster than online training, even
when online training is performed in parallel. Once mini-batch training is used,
BLAS operations are used in order to get as much performance as possible on the
different operations, mainly the Matrix Matrix Multiplication with the use of
the GEMM operation from the Intel Math Kernel Library (MKL). Moreover, the
parallel version of the MKL is also used to get even more performance. When all
these optimizations are performed, speedups of 11 to 30 are obtained compared to
the online training, depending on the network configurations. This final version
is able  to perform one epoch of Contrastive Divergence in 4 to 15 seconds
depending on the network, for 60000 images.

The second part of the article is about Convolutional Restricted Boltzmann
Machine (CRBM). This is almost the equivalent of a Convolutional Neural Network
(CNN). Again four different networks are evaluated.

The main problem with CRBM is that there are no standard implementations of the
convolution operation that is really fast. Therefore, it is not possible to
simply use a BLAS library to make the computation as fast as possible. The first
optimization that was tried is to vectorize the convolutions. With this, the
speedups have been between 1.1 and 1.9 times faster. I'm not really satisfied
with these results since in fact per convolution the speedups are much better.
Moreover, I have since been able to obtain better speedups but the deadline was
too short to include them in this paper. I'll try to talk about these
improvements in more details on this blog. What is more interesting to to
parallellize the different convolutions since they are mostly independent. This
can bring a speedup of the amount of cores available on the machine. Since
convolutions are extremely memory hungry, virtual cores with Hyper Threading
generally does not help. An interesting optimization is to use a Matrix
Multiplication to compute several valid convolutions at once.  This can give an
additional speedup between 1.6 and 2.2 compared to the vectorized version. While
it is possible to use the FFT to reduce the full convolution as well, in our
experiment the images were not big enough for this to be interesting. The final
speedups are about 10 times faster with these optimizations.

We have obtained pretty good and I'm happy we have been published. However, I'm
not very satisfied with these results since I've been able to get even faster
since this and when compared with other frameworks, DLL is actually quite
competitive. I'll try to publish something new in the future.

If you want more information, you can have a look at the paper. If you want to
look at the code, you can have a look at my projects:

* `Expression Templates Library (ETL) <https://github.com/wichtounet/etl>`_: For
  the Matrix Multiplication and Convolutions
* `Deep Learning Library (DLL) <https://github.com/wichtounet/dll>`_: For the RBM
  and CRBM implementations

Don't hesitate to ask any questions if you want more information :)
