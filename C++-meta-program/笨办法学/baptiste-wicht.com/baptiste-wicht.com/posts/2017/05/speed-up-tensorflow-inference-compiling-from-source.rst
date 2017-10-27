The most simple way to install TensorFlow is to work in a virtual Python
environment and simply to use either the TensorFlow official packages in pip or
use one of the official wheels for distributions.  There is one big problem with
that technique and it's the fact that the binaries are precompiled so that they
fit as many hardware configuration as possible. This is normal from Google since
generating precompiled binaries for all the possible combinations of processor
capabilities would be a nightmare. This is not a problem for GPU
since the CUDA Libraries will take care of the difference from one graphics card
to another. But it is a problem with CPU performance. Indeed, different
processors have different capabilities. For instance, the vectorization
capabilities are different from processor to processor (SSE, AVX, AVX2,
AVX-512F, FMA, ...). All those options can make a significant difference in the
performance of the programs. Although most of the machine learning training
occurs on GPU most of the time, the inference is mostly done on the CPU.
Therefore, it probably remains important to be as fast as possible on CPU.

So if you care about performance on CPU, you should install TensorFlow from
sources directly yourself. This will allow compilation of the TensorFlow sources
with -march=native which will enable all the hardware capabilities of machine on
which you are compiling the library.

Depending on your problem, this may give you some nice speedup. In my case, on
a very small Recurrent Neural Network, it made inference about 20% faster.  On
a larger problem and depending on your processor, you may gain much more than
that. If you are training on CPU, this may make a very large difference in total
time.

Installing TensorFlow is sometimes a bit cumbersome. You'll likely have to
compile Bazel from sources as well and depending on your processor, it may take
a long time to finish. Nevertheless, I have successfully compiled TensorFlow
from sources on several machines now without too many problems. Just pay close
attention to the options you are setting while configuring TensorFlow, for
instance CUDA configuration if you want GPU support.

I hope this little trick will help you gain some time :)

Here is the `link to compile TensorFlow from source
<https://www.tensorflow.org/install/install_sources>`_.
