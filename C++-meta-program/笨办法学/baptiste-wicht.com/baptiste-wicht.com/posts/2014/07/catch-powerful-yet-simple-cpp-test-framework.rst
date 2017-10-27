Recently, I came accross a new test framework for C++ program: Catch. 

Until I found Catch, I was using Boost Test Framework. It is working quite well,
but the problem is that you need to build Boost and link to the Boost Test
Framework, which is not highly convenient. I wanter something lighter and easier
to integrate.

Catch is header only, you only have to include one header for each test file.
Moreover, it is very easy to combine several source files without linking
problems.

Usage
*****

The usage is really simple. Here is a basic example: 

.. code:: cpp

    #define CATCH_CONFIG_MAIN
    #include "catch.hpp"

    TEST_CASE( "stupid/1=2", "Prove that one equals 2" ){
        int one = 1;
        REQUIRE( one == 2 );
    }

The define is made to ensure that Catch will generate a main for you. This
should only defined in one of your tests files if you have several. You define a
new test case using the :code:`TEST_CASE` macro. There are two parameters, the
first one is the name of the test case, you can use any name, you don't have to
use a valid C++ name. The second parameter is a longer description of the test
case.

You then use :code:`REQUIRE` to verify a condition. You can also use
:code:`CHECK` to verify a condition, the difference being that it does not stop
if the condition is not true. :code:`CHECK` is a good tool to put together some
conditions that are related. There also exists :code:`REQUIRE_FALSE` and
:code:`CHECK_FALSE` versions.

As you can see, there are no :code:`REQUIRE_EQUALS` or things like that, you can
use any comparison operator you want in the :code:`REQUIRE`. 

This produces an executable that will, by default, run every test contained in
the executable. You can also configure the output report to be XML or JUnit if
you want or run a subset of your tests. Take a look at the command line usage by
running the executable with the -h option if you want more information. 

Here is the result of the previous test::


    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    catch_test_1 is a Catch v1.0 b52 host application.
    Run with -? for options

    -------------------------------------------------------------------------------
    stupid/1=2
    -------------------------------------------------------------------------------
    src/catch/test1.cpp:4
    ...............................................................................

    src/catch/test1.cpp:6: FAILED:
      REQUIRE( one == 2 )
    with expansion:
      1 == 2

    ===============================================================================
    test cases: 1 | 1 failed
    assertions: 1 | 1 failed

For each failed condition, the source location is printed as well as some
information on the test that failed. What is also interesting is the "with
expansion" information that shows LHS and RHS of the comparison operator.

You can also check for exceptions with several macros:

* :code:`REQUIRE_THROWS(expression)` and :code:`CHECK_THROWS(expression)`
  verify that an exception is thrown when the given expresssion is evaluated. 
* :code:`REQUIRE_THROWS_AS(expression, exception_type)` and
  :code:`CHECK_THROWS_AS(expression, exception_type)` verify the the given
  exception is thrown. 
* :code:`REQUIRE_NOTHROW(expression)` and :code:`CHECK_NOTHROW(expression)`
  verify that no exception is thrown. 

Conclusion
**********

I have only covered the most basic features, there is more that you can do with
Catch: fixtures, logging and BDD-style test cases for instance. For more
information you can read the `reference documentation
<https://github.com/philsquared/Catch/blob/master/docs/reference-index.md>`_.

I'm really satisfied with this framework. It also can be used for Objective-C if
you are interested. You can download Catch `on Github
<https://github.com/philsquared/Catch>`_.

If you want more examples, you can take a look at the `ETL
<https://github.com/wichtounet/etl/tree/master/test>`_ tests that are all
made with Catch. 
