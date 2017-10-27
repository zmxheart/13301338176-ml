A few days ago, I published a post comparing the
`performance of std::pow against direct multiplications <https://baptiste-wicht.com/posts/2017/09/cpp11-performance-tip-when-to-use-std-pow.html>`_. When not compiling with -ffast-math, direct multiplication was significantly faster than :code:`std::pow`, around two orders of magnitude faster when comparing :code:`x * x * x` and :code:`code:std::pow(x, 3)`.
One comment that I've got was to test for which :code:`n` is
:code:`code:std::pow(x, n)` becoming faster than multiplying in a loop. Since
std::pow is using a special algorithm to perform the computation rather than be
simply loop-based multiplications, there may be a point after which it's more interesting to use the
algorithm rather than a loop. So I decided to do the tests. You can also find
the result in the original article, which I've updated.

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

If you want more results on the subject, you take a look at the
`original article <https://baptiste-wicht.com/posts/2017/09/cpp11-performance-tip-when-to-use-std-pow.html>`_.

If you are interested in the code of this benchmark, it's available online:
`bench_pow_my_pow.cpp <https://github.com/wichtounet/articles/blob/master/src/bench_pow_my_pow.cpp>`_

.. raw:: html

    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">google.load('visualization', '1.0', {'packages':['corechart']});</script>
    <script type="text/javascript">
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
    draw_graph_pow_my_pow_1();
    draw_graph_pow_my_pow_2();
    }
    google.setOnLoadCallback(draw_all);
    </script>
