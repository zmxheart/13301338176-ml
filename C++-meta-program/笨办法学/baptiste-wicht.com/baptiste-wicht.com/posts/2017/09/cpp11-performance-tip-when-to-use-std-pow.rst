Update: I've added a new section for larger values of :code:`n`.

Recently, I've been wondering about the performance of :code:`std::pow(x, n)`.
I'm talking here about the case when :code:`n` is an integer. In the case when
:code:`n` is not an integer, I believe, you should always use :code:`std::pow`
or use another specialized library.

In case when n is an integer, you can actually replace it with the direct
equivalent (for instance :code:`std::pow(x, 3) = x * x x`). If n is very large,
you'd rather write a loop of course ;) In practice, we generally use powers of
two and three much more often than power of 29, although that could happen. Of
course, it especially make sense to wonder about this if the pow is used inside
a loop. If you only use it once outside a loop, that won't be any difference on
the overall performance.

Since I'm mostly interested in single precision performance (neural networks are
only about single precision), the first benchmarks will be using :code:`float`.

.. TEASER_END

std::pow performances
#####################

So let's see what are the differences between :code:`std::pow(x, 2)` and
:code:`x * x`. All the code will be compiled in C++11. For the sake of it,
I'll also check the performance of the C :code:`pow` function.
Let's start with GCC-6.4 and -O2:

.. raw:: html

    <div id="graph_std_pow_2" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_std_pow_2" type="button" value="Logarithmic scale">

First, there is no difference between C pow function and std::pow, which is
expected. On the other hand, it's interesting to see that there is a definite
difference in performance between :code:`x * x` and :code:`std::pow(x, 2)`. This
is not a huge difference, but still around 2.5 times slower for
:code:`std::pow`.

Let's see if the difference is the same for bigger exponent.

.. raw:: html

    <div id="graph_std_pow_3" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_std_pow_3" type="button" value="Logarithmic scale">

This time the difference is very significant. :code:`x * x * x` is two orders
of magnitude faster than :code:`std::pow(x, n)`. It seems that the algorithm
used for bigger power is much less efficient. In any case, we can see that this
is not optimized for integer values of exponent values.

Let's see if this changes for a power of 4:

.. raw:: html

    <div id="graph_std_pow_4" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_std_pow_4" type="button" value="Logarithmic scale">

The numbers are almost the same. It seems the algorithm used for approximation
of the power does not depend on the exponent itself (at least between 3 and 4).

fast math
#########

One compiler option is very important to consider here: -ffast-math. This
compiler option will make some math operations much faster, but will also
violate a lot of IEEE compliance. In most cases, it does not matter since this
will mostly cover edge cases resulting in infinities and Not-A-Numbers. It also
will reduce the accuracy of some operations. If you really care about precise
computation, you should not use -ffast-math, but in most case, I think it's fine.
Any way, let's see if that changes anything:

.. raw:: html

    <div id="graph_std_pow_fast_2" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_std_pow_fast_2" type="button" value="Logarithmic scale">

For second power, it does not change anything. Let's see about the third power:

.. raw:: html

    <div id="graph_std_pow_fast_3" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_std_pow_fast_3" type="button" value="Logarithmic scale">

For the third power, for :code:`std::pow(x, 3)`, it is now much faster than
before. Even though it's still faster to use :code:`x * x * x` than
:code:`std::pow(x, 3)`, the difference is *only* around 2.5 times slower.

Clang
#####

I've tested several version of G++ (4.9.4, 5.4.0 and 6.4.0) and I've not seen
any significant difference in performance. Let's see if there are differences
between clang-3.9 and GCC 6.4.0. Since we are using a function from the library,
let's see if using libc++ makes any difference.

.. raw:: html

    <div id="graph_std_pow_compilers_1" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_std_pow_compilers_1" type="button" value="Logarithmic scale">

As it turns out, there is not much difference between the two compilers, but
clang is actually around 15% slower here. Moreover, there is no difference
between the two libraries. Let's see if this makes a difference for third power:

.. raw:: html

    <div id="graph_std_pow_compilers_2" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_std_pow_compilers_2" type="button" value="Logarithmic scale">

This time, the difference between the two compilers is more significant, clang
is about 50% slower than GCC on this. Again, there is no significant difference
between the two C++ libraries. I was expecting more of a difference between the
two, but it seems they are using a similar implementations, if not the same.

double precision
################

As said earlier, all the tests were run in single precision (:code:`float`).
Let's see now if it's any different with double precision (:code:`double`).
Again, I'll use G++ 5.4.0 to start with.

Here are the results first without -ffast-math:

.. raw:: html

    <div id="graph_std_pow_double_2" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_std_pow_double_2" type="button" value="Logarithmic scale">

This is very interesting! Here there is no overhead of using :code:`std::pow`
compared to direct multiplication (:code:`x * x`). It seems that most of the
overhead of this function for single precision was in fact in conversion to
double since it seems that the algorithm itself is only implemented for double
precision. Let's see about third power now:

.. raw:: html

    <div id="graph_std_pow_double_3" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_std_pow_double_3" type="button" value="Logarithmic scale">

As seen before, with third power, the overhead is actually huge. Although this
is slightly faster than when using single precision, it is still 2 orders of
magnitude slower than direct multiplication :code:`x * x * x`. Let's see what
happens with -ffast-math:

.. raw:: html

    <div id="graph_std_pow_double_4" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_std_pow_double_4" type="button" value="Logarithmic scale">

With -ffast-math, there is absolutely no overhead anymore for :code:`std::pow(x, n)`
even for third power. The results are the same for clang. I've checked for
higher values of the exponent and the result is also the same.

Bigger exponents
################

Now, let's try to test for which :code:`n` is :code:`code:std::pow(x, n)`
becoming faster than multiplying in a loop. Since std::pow is using a special
algorithm to perform the computation rather than be simply loop-based
multiplications, there may be a point after which it's more interesting to use
the algorithm rather than a loop.

First, our pow function:

.. code:: c++

    double my_pow(double x, size_t n){
        double r = 1.0;

        while(n > 0){
            r *= x;
            --n;
        }

        return r;
    }

And now, let's see the performance. I've compiled my benchmark with GCC 4.9.3
and running on my old Sandy Bridge processor. Here are the results for 1000
calls to each functions:

.. raw:: html

    <div id="graph_std_pow_my_pow_1" style="width: 700px; height: 400px;"></div>

We can see that between :code:`n=100` and :code:`n=110`, :code:`std::pow(x, n)`
starts to be faster than :code:`my_pow(x, n)`. At this point, you should only
use :code:`std::pow(x, n)`.  Interestingly too, the time for :code:`std::pow(x,
n)` is decreasing. Let's see how is the performance with higher range of
:code:`n`:

.. raw:: html

    <div id="graph_std_pow_my_pow_2" style="width: 700px; height: 400px;"></div>

We can see that the pow function time still remains stable while our loop-based
pow function still increases linearly. At :code:`n=1000`, :code:`std::pow` is
one order of magnitude faster than :code:`my_pow`.

Overall, if you do not care much about extreme accuracy, you may consider using
you own pow function for small-ish (integer) :code:`n` values. After
:code:`n=100`, it becomes more interesting to use :code:`std::pow`.

Conclusion
##########

If you are using double precision (:code:`double`), :code:`std::pow(x, n)` will
be slower than the handcrafted equivalent unless you use -ffast-math, in which
case, there is absolutely no overhead. The overhead without using the compiler
option is quite large, around 2 orders of magnitude, starting from the third
power. With or without -ffast-math, std::pow(x, 2) has no overhead compared to
:code:`x * x`.

For single precision, it's another story! For the two compilers that have been
tested and for small integer values of n (but I think it's stays the same for
large integer values of n), it's always faster to use direct multiplication
rather than exponentiation via :code:`std::pow(x, n)`. Indeed, it seems that
there is no optimization for the case when n is an integer. When -ffast-math is
used, the difference it not very big, around 2.5 times slower for GCC and around
3.5 times slower for clang. I'm a bit disappointed by the lack of
single-precision performance for :code:`std::pow`. Basically, you should not use
:code:`std::pow` if you want single-precision powers.

I hope you found this benchmark interesting :)

For those interested in the code of the benchmark, it's available
`on Github <https://github.com/wichtounet/articles>`_.

.. raw:: html

    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">google.load('visualization', '1.0', {'packages':['corechart']});</script>
    <script type="text/javascript">
    function draw_graph_pow_2(){
    var data = google.visualization.arrayToDataTable([
    ['N', 'pow(x, 2)', 'std::pow(x, 2)', 'x * x'],
    ['100',      0.1,     0.1,      0.1],
    ['1000',     3,     3,      1],
    ['10000',    32,    32,     14],
    ['100000',   326,   323,    147],
    ['1000000',  3253,  3238,   1471],
    ['10000000', 32457, 32465,  14756],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_2'));
    var options = {curveType: "function",title: "std::pow(x, 2) (float)",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_std_pow_2');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_graph_pow_3(){
    var data = google.visualization.arrayToDataTable([
    ['N', 'pow(x, 3)', 'std::pow(x, 3)', 'x * x * x'],
    ['100',      29,      15,       0.1],
    ['1000',     123,     119,      1],
    ['10000',    1193,    1192,     14],
    ['100000',   11998,   11942,    147],
    ['1000000',  125629,  125245,   1473],
    ['10000000', 1213756, 1217333,  14756],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_3'));
    var options = {curveType: "function",title: "std::pow(x, 3) (float)",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_std_pow_3');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_graph_pow_4(){
    var data = google.visualization.arrayToDataTable([
    ['N', 'pow(x, 4)', 'std::pow(x, 4)', 'x * x * x * x'],
    ['100',      19,      17,       0.1],
    ['1000',     123,     122,      1],
    ['10000',    1203,    1219,     14],
    ['100000',   12223,   11981,    147],
    ['1000000',  121036,  119601,   1472],
    ['10000000', 1198359, 1196447,  14728],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_4'));
    var options = {curveType: "function",title: "std::pow(x, 4) (float)",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_std_pow_4');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_graph_pow_fast_2(){
    var data = google.visualization.arrayToDataTable([
    ['N', 'pow(x, 2)', 'std::pow(x, 2)', 'x * x'],
    ['100',      0.1,     0.1,      0.1],
    ['1000',     3,       3,        1],
    ['10000',    32,      32,       14],
    ['100000',   326,     324,      147],
    ['1000000',  3239,    3238,     1473],
    ['10000000', 32398,   32384,    14756],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_fast_2'));
    var options = {curveType: "function",title: "std::pow(x, 2) (float) -ffast-math",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_std_pow_fast_2');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_graph_pow_fast_3(){
    var data = google.visualization.arrayToDataTable([
    ['N', 'pow(x, 2)', 'std::pow(x, 2)', 'x * x'],
    ['100',      0.1,     0.1,      0.1],
    ['1000',     3,       3,        1],
    ['10000',    32,      32,       14],
    ['100000',   324,     324,      147],
    ['1000000',  3249,    3243,     1472],
    ['10000000', 32491,   32519,    14718],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_fast_3'));
    var options = {curveType: "function",title: "std::pow(x, 2) (float) -ffast-math",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_std_pow_fast_3');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_graph_pow_compilers_1(){
    var data = google.visualization.arrayToDataTable([
    ['N', 'g++-6.4', 'clang-3.9', 'clang-3.9 libc++'],
    ['100',      0.1,     0.1,      0.1],
    ['1000',     3,       3,        3],
    ['10000',    32,      37,       37],
    ['100000',   324,     374,      370],
    ['1000000',  3249,    3714,     3716],
    ['10000000', 32491,   37132,    37136],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_compilers_1'));
    var options = {curveType: "function",title: "std::pow(x, 2) (float)",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_std_pow_compilers_1');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_graph_pow_compilers_2(){
    var data = google.visualization.arrayToDataTable([
    ['N', 'g++-6.4', 'clang-3.9', 'clang-3.9 libc++'],
    ['100',      0.1,     0.1,      0.1],
    ['1000',     3,       4,        5],
    ['10000',    32,      48,       48],
    ['100000',   325,     482,      481],
    ['1000000',  3244,    4828,     4824],
    ['10000000', 32489,   48242,    48332],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_compilers_2'));
    var options = {curveType: "function",title: "std::pow(x, 3) (float)",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_std_pow_compilers_2');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_graph_pow_double_2(){
    var data = google.visualization.arrayToDataTable([
    ['N', 'pow(x, 2)', 'std::pow(x, 2)', 'x * x'],
    ['100',      0.1,   0.1,    0.1],
    ['1000',     1,     1,      1],
    ['10000',    16,    12,     12],
    ['100000',   122,   122,    122],
    ['1000000',  1228,  1228,   1228],
    ['10000000', 12323, 12714,  12380],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_double_2'));
    var options = {curveType: "function",title: "std::pow(x, 2) (double)",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_std_pow_double_2');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_graph_pow_double_3(){
    var data = google.visualization.arrayToDataTable([
    ['N', 'pow(x, 3)', 'std::pow(x, 3)', 'x * x * x'],
    ['100',      27,      16,       0.1],
    ['1000',     122,     119,      1],
    ['10000',    1179,    1181,     13],
    ['100000',   12181,   11794,    129],
    ['1000000',  124035,  122994,   1298],
    ['10000000', 1194969, 1195090,  13017],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_double_3'));
    var options = {curveType: "function",title: "std::pow(x, 3) (double)",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_std_pow_double_3');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_graph_pow_double_4(){
    var data = google.visualization.arrayToDataTable([
    ['N', 'pow(x, 3)', 'std::pow(x, 3)', 'x * x * x'],
    ['100',      0.1,   0.1,    0.1],
    ['1000',     1,     1,      1],
    ['10000',    14,    14,     14],
    ['100000',   147,   147,    147],
    ['1000000',  1471,  1473,   1473],
    ['10000000', 14744, 14740,  14745],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_double_4'));
    var options = {curveType: "function",title: "std::pow(x, 3) (double) -ffast-math",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_std_pow_double_4');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_graph_pow_my_pow_1(){
    var data = google.visualization.arrayToDataTable([
    ['n', 'my_pow(x, n)', 'std::pow(x, n)'],
    ['10',   2,     127],
    ['20',   17,     123],
    ['30',   26,     127],
    ['40',   36,     123],
    ['50',   43,     123],
    ['60',   55,     123],
    ['70',   72,     123],
    ['80',   85,     123],
    ['90',   102,    126],
    ['100',  114,    125],
    ['110',  131,    115],
    ['120',  144,    111],
    ['130',  165,    111],
    ['140',  173,    108],
    ['150',  189,    107],
    ['160',  202,    112],
    ['170',  219,    106],
    ['180',  232,    105],
    ['190',  249,    108],
    ['200',  261,    105],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_my_pow_1'));
    var options = {curveType: "function",title: "std::pow(x, 2) (float)",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    }
    function draw_graph_pow_my_pow_2(){
    var data = google.visualization.arrayToDataTable([
    ['n', 'my_pow(x, n)', 'std::pow(x, n)'],
    ['100',  114,    125],
    ['200',  261,    105],
    ['300',  410,    104],
    ['400',  558,    104],
    ['500',  708,    104],
    ['600',  855,    104],
    ['700',  1002,   104],
    ['800',  1148,   104],
    ['900',  1300,   104],
    ['1000', 1442,   104],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_std_pow_my_pow_2'));
    var options = {curveType: "function",title: "std::pow(x, 2) (float)",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    }
    function draw_all(){
    draw_graph_pow_2();
    draw_graph_pow_3();
    draw_graph_pow_4();
    draw_graph_pow_fast_2();
    draw_graph_pow_fast_3();
    draw_graph_pow_compilers_1();
    draw_graph_pow_compilers_2();
    draw_graph_pow_double_2();
    draw_graph_pow_double_3();
    draw_graph_pow_double_4();
    draw_graph_pow_my_pow_1();
    draw_graph_pow_my_pow_2();
    }
    google.setOnLoadCallback(draw_all);
    </script>
