In the previous two days, I've working on improving compilation time of my
project Expression Templates Library (ETL). I have been able to reduce the
compilation time of the complete test suite from 794 seconds to 764 seconds
(using only one thread). Trying to get further, I started checking what was
taking the most time in a test case when I saw that the REQUIRE calls of **the
test library were taking a large portion of the compilation time!**

I have been `using Catch as my test framework <http://baptiste-wicht.com/posts/2014/07/catch-powerful-yet-simple-cpp-test-framework.html>`_
for more than two years and it's really been great overall. It is a great tool,
header-only, fully-featured, XML reporting for Sonar, ... It really has
everything I need from a test framework.

Contrary to some popular test frameworks that provides ASSERT_EQUALS,
ASSERT_GREATER and all fashion of assert macros, Catch only provides one
version: REQUIRE. For instance:

.. code:: cpp

    REQUIRE(x == 1.0);
    REQUIRE(y < 5.5);
    REQUIRE((z + x) != 22.01f);

The left and right part are detected with some smart template and operator
overloading techniques and this makes for very nice test output in case of
errors, for instance:

.. code::

    test/src/dyn_matrix.cpp:16: FAILED:
      REQUIRE( test_matrix.rows() == 2UL )
    with expansion:
      3 == 2

I think this is pretty nice and the tests are really clear. However, *it comes
with a cost* and I underestimated this at first.

To overcome this, I create two new macros (and few other variations)
REQUIRE_EQUALS and REQUIRE_DIRECT that simply bypass Catch deduction of the
expression:

.. code:: cpp

    inline void evaluate_result_direct(Catch::ResultBuilder&& __result, bool value){
        __result.setResultType(value);
        __result.setLhs(value ? "true" : "false");
        __result.setOp("");
        __result.endExpression();
    }

    template<typename L, typename R>
    void evaluate_result(Catch::ResultBuilder&& __result, L lhs, R rhs){
        __result.setResultType(lhs == rhs);
        __result.setLhs(Catch::toString(lhs));
        __result.setRhs(Catch::toString(rhs));
        __result.setOp("==");
        __result.endExpression();
    }

    #define REQUIRE_DIRECT(value) \
        evaluate_result_direct(Catch::ResultBuilder( "REQUIRE", CATCH_INTERNAL_LINEINFO, #value, Catch::ResultDisposition::Normal ), value);

    #define REQUIRE_EQUALS(lhs, rhs) \
        evaluate_result(Catch::ResultBuilder( "REQUIRE", CATCH_INTERNAL_LINEINFO, #lhs " == " #rhs, Catch::ResultDisposition::Normal ), lhs, rhs);

There is really nothing too special about it, I simply followed the macros and
functions in Catch source code until I found out what to bypass.

And now, we use them directly:

.. code:: cpp

   REQUIRE_DIRECT(am_i_true());
   REQUIRE_EQUALS(x, 1.0);

This is a bit less nice and it requires to know a few more macros, I admit, but
it turns out to be much faster (and who really cares about the beauty of test
code anyway...). Indeed, the total compilation time of the tests went from 764
seconds to 664 seconds!  This is **a 13% reduction of the compilation time**!
I really am impressed of the overhead of this technique. I cannot justify this
slowdown just for a bit nicer test code. Finally, the output in case of error
remains exactly the same as before.

This proves that sometimes the bottlenecks are not where we expect them :)

If you are interested, you can find the adapted code on `Github <https://github.com/wichtounet/etl/blob/master/test/include/fast_catch.hpp>`_.
