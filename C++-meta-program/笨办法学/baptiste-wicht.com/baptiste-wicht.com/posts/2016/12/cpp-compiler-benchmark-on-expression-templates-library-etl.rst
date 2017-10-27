.. raw:: html

    <script src="https://code.jquery.com/jquery-3.1.1.min.js" integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8=" crossorigin="anonymous"></script>
    <script src="https://code.highcharts.com/highcharts.js"></script>
    <script src="https://code.highcharts.com/modules/exporting.js"></script>

In my Expression Templates Library (ETL) project, I have a lot of template heavy
code that needs to run as fast as possible and that is quite intensive to
compile. In this post, I'm going to compare the performance of a few of the
kernels produced by different compilers. I've got GCC 5.4, GCC 6.20 and clang
3.9. I also included zapcc which is based on clang 4.0.

These tests have been run on an Haswell processor. The automatic parallelization
of ETL has been turned off for these tests.

Keep in mind that some of the diagrams are presented in logarithmic form.

Vector multiplication
*********************

The first kernel is a very simple one, simple element-wise multiplication of two
vectors. Nothing fancy here.

.. raw:: html

    <div id="mul_container" style="min-width: 310px; height:400px; margin: 0 auto; "></div>
    <script>
    $(function () {
        Highcharts.chart('mul_container', {
            chart: { type: 'column' },
            title: { text: 'Element-wise Vector Multiplication' },
            xAxis: {
                categories: ['10', '100', '1000', '10000', '100000', '1000000']
            },
            yAxis: {
                type: 'logarithmic',
                title: { text: 'Time (us)' },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {valueSuffix: 'us'},
            series: [
            {
                name: 'g++-5.4', data: [0.021, 0.040, 0.215, 2.07, 32.1, 403]
            },
            {
                name: 'g++-6.2', data: [0.021, 0.037, 0.208, 2.17, 32.1, 376]
            },
            {
                name: 'clang-3.9', data: [0.027, 0.045, 0.243, 2.43, 32.7, 389]
            },
            {
                name: 'zapcc-4.0', data: [0.026, 0.047, 0.321, 2.5, 32.8, 411]
            }
            ]
        });
    });
    </script>

For small vectors, clang is significantly slower than gcc-5.4 and gcc6.2. On
vectors from 100'000 elements, the speed is comparable for each compiler,
depending on the memory bandwidth. Overall, gcc-6.2 produces the fastest code
here. clang-4.0 is slightly slower than clang-3.9, but nothing dramatic.

Vector exponentiation
*********************

The second kernel is computing the exponentials of each elements of a vector and
storing them in another vector.

.. raw:: html

    <div id="exp_container" style="min-width: 310px; height:400px; margin: 0 auto; "></div>
    <script>
    $(function () {
        Highcharts.chart('exp_container', {
            chart: { type: 'column' },
            title: { text: 'Element-wise Vector Exponentiation' },
            xAxis: {
                categories: ['10', '100', '1000', '10000', '100000', '1000000']
            },
            yAxis: {
                type: 'logarithmic',
                title: { text: 'Time (us)' },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {valueSuffix: 'us'},
            series: [
            {
                name: 'g++-5.4', data: [0.0478, 0.137, 1.12, 9.79, 97.5, 959]
            },
            {
                name: 'g++-6.2', data: [0.0474, 0.132, 1.11, 9.71, 97, 1000]
            },
            {
                name: 'clang-3.9', data: [0.0492, 0.136, 0.959, 9.24, 92.9, 914]
            },
            {
                name: 'zapcc-4.0', data: [0.0488, 0.142, 0.952, 9.25, 91.9, 915]
            }
            ]
        });
    });
    </script>

Interestingly, this time, clang versions are significantly faster for medium to
large vectors, from 1000 elements and higher, by about 5%. There is no
significant differences between the different versions of each compiler.

Matrix-Matrix Multiplication
****************************

The next kernel I did benchmark with the matrix-matrix multiplication operation.
In that case, the kernel is hand-unrolled and vectorized.

.. raw:: html

    <div id="gemm_container_small" style="min-width: 310px; height:400px; margin: 0 auto; "></div>
    <div id="gemm_container_large" style="min-width: 310px; height:400px; margin: 0 auto; "></div>
    <script>
    $(function () {
        Highcharts.chart('gemm_container_small', {
            chart: { type: 'column' },
            title: { text: 'Matrix Matrix Multiplication (small)', },
            xAxis: {
                categories: ['10x10', '20x20', '40x40', '60x60', '80x80', '100x100']
            },
            yAxis: {
                type: 'logarithmic',
                title: { text: 'Time (us)' },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {valueSuffix: 'us'},
            series: [
            {
                name: 'g++-5.4', data: [0.159, 0.815, 2.637, 13.849, 17.281, 78.903]
            },
            {
                name: 'g++-6.2', data: [0.162, 0.802, 2.431, 13.531, 17.274, 74.02]
            },
            {
                name: 'clang-3.9', data: [0.179, 1.218, 2.391, 14.981, 15.142, 61.548]
            },
            {
                name: 'zapcc-4.0', data: [0.159, 0.836, 2.712, 13.426, 15.114, 62.241]
            }
            ]
        });
        Highcharts.chart('gemm_container_large', {
            chart: { type: 'column' },
            title: { text: 'Matrix Matrix Multiplication (large)', },
            xAxis: {
                categories: ['200x200', '300x300', '400x400', '500x500', '600x600', '700x700', '800x800', '900x900', '1000x1000']
            },
            yAxis: {
                type: 'logarithmic',
                title: { text: 'Time (us)' },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {valueSuffix: 'us'},
            series: [
            {
                name: 'g++-5.4', data: [275.219, 1371, 1837, 5177, 6667, 14981, 17037, 31492, 32813]
            },
            {
                name: 'g++-6.2', data: [267.776, 1362, 1808, 5297, 6859, 15166, 15664, 30666, 33067]
            },
            {
                name: 'clang-3.9', data: [266.033, 1230, 1789, 4825, 6969, 14488, 15916, 30872, 33186]
            },
            {
                name: 'zapcc-4.0', data: [267.806, 1237, 1820, 4909, 7035, 15191, 18193, 33127, 37346]
            }
            ]
        });
    });
    </script>

There are few differences between the compilers. The first thing is that for
some sizes such as 80x80 and 100x100, clang is significantly faster than GCC, by
more than 10%. The other interesting fact is that for large matrices
zapcc-clang-4.0 is always slower than clang-3.9 which is itself on par with the
two GCC versions. In my opinion, it comes from a regression in clang trunk but
it could also come from zapcc itself.

.. raw:: html

    <div id="std_gemm_container_large" style="min-width: 310px; height:400px; margin: 0 auto; "></div>
    <script>
    $(function () {
        Highcharts.chart('std_gemm_container_large', {
            chart: { type: 'column' },
            title: { text: 'Matrix Matrix Multiplication (naive)', },
            xAxis: {
                categories: ['200x200', '300x300', '400x400', '500x500', '600x600', '700x700', '800x800', '900x900', '1000x1000']
            },
            yAxis: {
                type: 'logarithmic',
                title: { text: 'Time (ms)' },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {valueSuffix: 'ms'},
            series: [
            {
                name: 'g++-5.4', data: [1.195, 4.891, 10.467, 22.400, 33.399,
                58.401, 77.150, 121.392, 148.469]
            },
            {
                name: 'g++-6.2', data: [1.109, 4.540, 9.964, 21.359, 31.904,
                55.282, 72.690, 113.52, 143.27]
            },
            {
                name: 'clang-3.9', data: [0.893, 3.710, 7.287, 16.244, 23.920,
                43.342, 56.771, 91.870, 112.309]
            },
            {
                name: 'zapcc-4.0', data: [5.088, 16.909, 39.632, 77.194, 133.15,
                214.539, 316.01, 447.715, 612.255]
            }
            ]
        });
    });
    </script>

The results are much more interesting here! First, there is a huge regression in
clang-4.0 (or in zapcc for that matter). Indeed, it is up to 6 times slower than
clang-3.9. Moreover, the clang-3.9 is always significantly faster than gcc-6.2.
Finally, there is a small improvement in gcc-6.2 compared to gcc 5.4.

Fast-Fourrier Transform
***********************

The following kernel is the performance of a hand-crafted Fast-Fourrier
transform implementation.

.. raw:: html

    <div id="fft_container" style="min-width: 310px; height:400px; margin: 0 auto; "></div>
    <script>
    $(function () {
        Highcharts.chart('fft_container', {
            chart: { type: 'column' },
            title: { text: 'Fast Fourrier Transform', },
            xAxis: {
                categories: ['100', '1000', '10000', '100000', '1000000']
            },
            yAxis: {
                type: 'logarithmic',
                title: { text: 'Time (us)' },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {valueSuffix: 'us'},
            series: [
            {
                name: 'g++-5.4', data: [2.640, 27.515, 308.239, 3427.4, 41695.9]
            },
            {
                name: 'g++-6.2', data: [2.578, 26.194, 298.97, 3348.82, 40783.8]
            },
            {
                name: 'clang-3.9', data: [3.047, 30.514, 333.403, 3569.36,43860.6]
            },
            {
                name: 'zapcc-4.0', data: [3.199,33.304,317.135,4025.18,48445.3]
            }
            ]
        });
    });
    </script>

On this benchmark, gcc-6.2 is the clear winner. It is significantly faster
than clang-3.9 and clang-4.0. Moreover, gcc-6.2 is also faster than gcc-5.4.
On the contrary, clang-4.0 is significantly slower than clang-3.9 except on one
configuration (10000 elements).

1D Convolution
**************

This kernel is about computing the 1D valid convolution of two vectors.

.. raw:: html

    <div id="conv1_container" style="min-width: 310px; height:400px; margin: 0 auto; "></div>
    <script>
    $(function () {
        Highcharts.chart('conv1_container', {
            chart: { type: 'column' },
            title: { text: '1D convolution (optimized)', },
            xAxis: {
                categories: ['1000x500', '2000x1000', '3000x1500', '4000x2000',
                '5000x2500', '6000x3000', '7000x3500', '8000x4000', '9000x4500',
                '10000x5000']
            },
            yAxis: {
                type: 'logarithmic',
                title: { text: 'Time (us)' },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {valueSuffix: 'us'},
            series: [
            {
                name: 'g++-5.4', data: [11.710, 41.002, 91.201, 158.178,
                248.985, 353.695, 486.676, 634.53, 867.101, 1082.62]
            },
            {
                name: 'g++-6.2', data: [9.307, 40.921, 90.327, 158.734, 248.892,
                354.582, 488.38, 636.899, 869.637, 1084.86]
            },
            {
                name: 'clang-3.9', data: [13.404, 41.409, 95.094, 162.339,
                256.143, 362.34, 498.66, 651.352, 886.465, 1092.24]
            },
            {
                name: 'zapcc-4.0', data: [13.528, 40.886, 94.473, 159.917,
                252.992, 356.63, 493.653, 640.348, 872.282, 1091.36]
            }
            ]
        });
    });
    </script>

While clang-4.0 is faster than clang-3.9, it is still slightly slower than both
gcc versions. On the GCC side, there is not a lot of difference except on the
1000x500 on which gcc-6.2 is 25% faster.

And here are the results with the naive implementation:

.. raw:: html

    <div id="std_conv1_container" style="min-width: 310px; height:400px; margin: 0 auto; "></div>
    <script>
    $(function () {
        Highcharts.chart('std_conv1_container', {
            chart: { type: 'column' },
            title: { text: '1D convolution (naive)', },
            xAxis: {
                categories: ['1000x500', '2000x1000', '3000x1500', '4000x2000',
                '5000x2500', '6000x3000', '7000x3500', '8000x4000', '9000x4500',
                '10000x5000']
            },
            yAxis: {
                type: 'logarithmic',
                title: { text: 'Time (ms)' },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {valueSuffix: 'ms'},
            series: [
            {
                name: 'g++-5.4', data: [0.350, 1.452, 3.260, 5.823, 9.116,
                13.155, 17.922, 23.438, 29.705, 36.683]
            },
            {
                name: 'g++-6.2', data: [0.350, 1.457, 3.262, 5.823, 9.120,
                13.152, 17.922, 23.436, 29.687, 36.665]
            },
            {
                name: 'clang-3.9', data: [0.216, 0.873, 1.974, 3.517, 5.501,
                7.921, 10.793, 14.11, 17.867, 22.068]
            },
            {
                name: 'zapcc-4.0', data: [0.215, 0.873, 1.972, 3.514, 5.501,
                7.928, 10.799, 14.11, 17.879, 22.065]
            }
            ]
        });
    });
    </script>

Again, on the naive version, clang is much faster than GCC on the naive, by
about 65%. This is a really large speedup.

2D Convolution
**************

This next kernel is computing the 2D valid convolution of two matrices

.. raw:: html

    <div id="conv2_container" style="min-width: 310px; height:400px; margin: 0 auto; "></div>
    <script>
    $(function () {
        Highcharts.chart('conv2_container', {
            chart: { type: 'column' },
            title: { text: '2D Convolution (optimized)', },
            xAxis: {
                categories: ['100x50', '105x50', '110x55', '115x55', '120x60',
                '125x60', '130x65', '135x65', '140x70']
            },
            yAxis: {
                title: { text: 'Time (us)' },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {valueSuffix: 'us'},
            series: [
            {
                name: 'g++-5.4', data: [327.399, 367.389, 441.457, 576.021,
                762.268, 794, 994.06, 1261.71, 1360.57]
            },
            {
                name: 'g++-6.2', data: [327.764, 367.379, 441.993, 572.241,
                761.741, 784.605, 991.717, 1266.55, 1361.59]
            },
            {
                name: 'clang-3.9', data: [330.199, 364.253, 443.483, 580.676,
                763.772, 777.39, 1000.53, 1267.75, 1375.51]
            },
            {
                name: 'zapcc-4.0', data: [339.358, 364.756, 443.807, 575.917,
                761.248, 784.695, 992.29, 1265.04, 1367.33]
            }
            ]
        });
    });
    </script>

There is no clear difference between the compilers in this code. Every compiler
here has up and down.

Let's look at the naive implementation of the 2D convolution (units are
milliseconds here not microseconds):

.. raw:: html

    <div id="std_conv2_container" style="min-width: 310px; height:400px; margin: 0 auto; "></div>
    <script>
    $(function () {
        Highcharts.chart('std_conv2_container', {
            chart: { type: 'column' },
            title: { text: '2D Convolution (naive)', },
            xAxis: {
                categories: ['100x50', '105x50', '110x55', '115x55', '120x60',
                '125x60', '130x65', '135x65', '140x70']
            },
            yAxis: {
                title: { text: 'Time (ms)' },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {valueSuffix: 'ms'},
            series: [
            {
                name: 'g++-5.4', data: [9.501,11.458,13.888, 16.489, 19.634,
                22.898, 27.012, 31.246, 36.269]
            },
            {
                name: 'g++-6.2', data: [9.502, 11.464, 13.903, 16.484, 19.642,
                22.994, 27.004, 31.248, 36.26]
            },
            {
                name: 'clang-3.9', data: [5.880, 7.136, 8.610, 10.226, 12.164,
                14.247, 17.024, 19.577, 22.510]
            },
            {
                name: 'zapcc-4.0', data: [5.875, 7.091, 8.661, 10.241, 12.218,
                14.302, 16.777, 19.424, 22.472]
            }
            ]
        });
    });
    </script>

This time the difference is very large! Indeed, clang versions are about 60%
faster than the GCC versions! This is really impressive. Even though this does
not comes close to the optimized. It seems the vectorizer of clang is much more
efficient than the one from GCC.

4D Convolution
**************

The final kernel that I'm testing is the batched 4D convolutions that is used a
lot in Deep Learning. This is not really a 4D convolution, but a large number
of 2D convolutions applied on 4D tensors.

.. raw:: html

    <div id="conv4_container" style="min-width: 310px; height:400px; margin: 0 auto; "></div>
    <script>
    $(function () {
        Highcharts.chart('conv4_container', {
            chart: { type: 'column' },
            title: { text: '4D Convolution', },
            xAxis: {
                categories: ['2x6x3x28x16', '2x6x3x28x16', '2x6x3x28x16',
                '2x6x3x28x16', '2x6x3x28x16', '2x6x3x28x16', '2x6x3x28x16',
                '2x6x3x28x16', '2x6x3x28x16']
            },
            yAxis: {
                type: 'logarithmic',
                title: { text: 'Time (ms)' },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {valueSuffix: 'ms'},
            series: [
            {
                name: 'g++-5.4', data: [0.095, 0.402, 1.083, 2.237, 3.988,
                6.474, 9.985, 14.132, 19.539]
            },
            {
                name: 'g++-6.2', data: [0.089, 0.413, 1.081, 2.224, 3.990,
                6.462, 9.815, 14.118, 19.612]
            },
            {
                name: 'clang-3.9', data: [0.090, 0.416, 1.108, 2.277, 4.077,
                6.587, 10.024, 14.359, 20.006]
            },
            {
                name: 'zapcc-4.0', data: [0.088, 0.406, 1.080, 2.237, 3.987,
                6.484, 9.827, 14.130, 19.569]
            }
            ]
        });
    });
    </script>

Again, there are very small differences between each version. The best versions
are the most recent versions of the compiler gcc-6.2 and clang-4.0 on a tie.

Conclusion
**********

Overall, we can see two trends in these results. First, when working with
highly-optimized code, the choice of compiler will not make a huge difference.
On these kind of kernels, gcc-6.2 tend to perform faster than the other
compilers, but only by a very slight margin, except in some cases. On the other
hand, when working with naive implementations, clang versions really did perform
much better than GCC. The clang compiled versions of the 1D and 2D convolutions
are more than 60% faster than their GCC counter parts. This is really
impressive. Overall, clang-4.0 seems to have several performance regressions,
but since it's not still a work in progress, I would not be suprised if these
regressions are not present in the final version. Since the clang-4.0 version is
in fact the clang version used by zapcc, it's also possible that zapcc is
introducing new performance regressions.

Overall, my advice would be to use GCC-6.2 (or 5.4) on hand-optimized kernels
and clang when you have mostly naive implementations. However, keep in mind that
at least for the example shown here, the naive version optimized by the compiler
never comes close to the highly-optimized version.

As ever, takes this with a grain of salt, it's only been tested on one project
and one machine, you may obtain very different results on other projects and on
other processors.
