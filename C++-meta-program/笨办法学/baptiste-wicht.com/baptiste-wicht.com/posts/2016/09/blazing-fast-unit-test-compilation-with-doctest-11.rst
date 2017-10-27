You may remember `my quest for faster compilation times <http://baptiste-wicht.com/posts/2016/06/reduce-compilation-time-by-another-16-with-catch.html>`_. I had made several changes to the Catch test framework macros in order to save some compilation at the expense of my test code looking a bit less nice:

.. code:: cpp

   REQUIRE(a == 9); //Before
   REQUIRE_EQUALS(a, 9); //After

The first line is a little bit better, but using several optimizations, I was
able to dramatically change the compilation time of the test cases of ETL. In
the end, I don't think that the difference between the two lines justifies the
high overhead in compilation times.

doctest
+++++++

`doctest <https://github.com/onqtam/doctest>`_ is a framework quite similar to
Catch but that claims to be much lighter. I tested doctest 1.0 early on, but at
this point it was actually slower than Catch and especially slower than my
versions of the macro.

Today, doctest 1.1 was released with promises of being even lighter than before
and providing several new ways of speeding up compilation. If you want the
results directly, you can take a look at the next section.

First of all, this new version improved the basic macros to make expression
decomposition faster. When you use the standard REQUIRE macro, the expression is
composed by using several template techniques and operator overloading. This is
really slow to compile. By removing the need for this decomposition, the fast
Catch macros are much faster to compile.

Moreover, doctest 1.1 also introduces CHECK_EQ that does not any expression
decomposition. This is close to what I did in my macros expect that it is
directly integrated into the framework and preserves all its features. It is
also possible to bypass the expression checking code by using FAST_CHECK_EQ
macro. In that case, the exceptions are not captured. Finally, a new
configuration option is introduced (DOCTEST_CONFIG_SUPER_FAST_ASSERTS) that
removes some features related to automatic debugger breaks. Since I don't use
the debugger features and I don't need to capture exception everywhere (it's
sufficient for me that the test fails completely if an exception is thrown), I'm
more than eager to use these new features.

Results
+++++++

For evaluation, I have compiled the complete test suite of ETL, with 1 thread,
using gcc 4.9.3 with various different options, starting from Catch to doctest
1.1 with all compilation time features. Here are the results, in seconds:

+--------------------+--------+----------+---------------+----------------+
| Version            | Time   | VS Catch | VS Fast Catch | VS doctest 1.0 |
+====================+========+==========+===============+================+
| Catch              | 724.22 |          |               |                |
+--------------------+--------+----------+---------------+----------------+
| Fast Catch         | 464.52 | -36%     |               |                |
+--------------------+--------+----------+---------------+----------------+
| doctest 1.0        | 871.54 | +20%     | +87%          |                |
+--------------------+--------+----------+---------------+----------------+
| doctest 1.1        | 614.67 | -16%     | +32%          | -30%           |
+--------------------+--------+----------+---------------+----------------+
| REQUIRE_EQ         | 493.97 | -32%     | +6%           | -43%           |
+--------------------+--------+----------+---------------+----------------+
| FAST_REQUIRE_EQ    | 439.09 | -39%     | -6%           | -50%           |
+--------------------+--------+----------+---------------+----------------+
| SUPER_FAST_ASSERTS | 411.11 | -43%     | -12%          | -53%           |
+--------------------+--------+----------+---------------+----------------+

As you can see, doctest 1.1 is much faster to compile than doctest 1.0! This is
really great news. Moreover, it is already 16% faster than Catch. When all the
features are used, doctest is 12% faster than my stripped down versions of Catch
macros (and 43% faster than Catch standard macros). This is really cool! It
means that I don't have to do any change in the code (no need to strip macros
myself) and I can gain a lot of compilation time compared to the bare Catch
framework.

I really think the author of doctest did a great job with the new version.
Although this was not of as much interest for me, there are also a lot of
other changes in the new version. You can consult the
`changelog <https://github.com/onqtam/doctest/blob/master/CHANGELOG.md>`_ if you want more information.

Conclusion
++++++++++

Overall, doctest 1.1 is much faster to compile than doctest 1.0. Moreover, it
offers very fast macros for test assertions that are much faster to compile
than Catch versions and even faster than the versions I created myself to reduce
compilation time. I really thing this is a great advance for doctest. When
compiling with all the optimizations, doctest 1.1 saves me 50 seconds in
compilation time compared to the fast version of Catch macro and more than
5 minutes compared to the standard version of Catch macros.

I'll probably start using doctest on my development machine. For now, I'll keep
Catch as well since I need it to generate the unit test reports in XML format
for Sonarqube. Once this feature appears in doctest, I'll probably drop Catch
from ETL and DLL

If you need blazing fast compilation times for your unit tests, doctest 1.1 is
probably the way to go.
