.. role:: cpp(code)
   :language: cpp

No, it's not the same post as two days! I've been able to reduce the compilation
time of my test cases by another 16%!

Two days ago, I posted an article about how `I reduced the compilation time of my tests by 13% <http://baptiste-wicht.com/posts/2016/05/speedup-compilation-by-13-by-simplifying-unit-test-with-catch.html>`_, by bypassing the expression deduction from Catch. I came up with the macro :cpp:`REQUIRE_EQUALS`:

.. code:: cpp

    template<typename L, typename R>
    void evaluate_result(Catch::ResultBuilder&& __result, L lhs, R rhs){
        __result.setResultType(lhs == rhs);
        __result.setLhs(Catch::toString(lhs));
        __result.setRhs(Catch::toString(rhs));
        __result.setOp("==");
        __result.endExpression();
        __result.react();
    }

    #define REQUIRE_EQUALS(lhs, rhs) \
        evaluate_result(Catch::ResultBuilder( "REQUIRE", CATCH_INTERNAL_LINEINFO, #lhs " == " #rhs, Catch::ResultDisposition::Normal ), lhs, rhs);

This has the advantage that the left and right hand sides are directly set, not
deduced with templates and operator overloading. This still has exactly the same
features has the original macro, but it is a bit less nice in the test code.
I was quite happy with that optimization, but it turned out, I was not
aggressive enough in my optimizations.

Even though it seems simple, the macro is still bloated. There are two
constructors calls: :cpp:`ResultBuilder` and :cpp:`SourceLineInfo` (hidden behind
:cpp:`CATCH_INTERNAL_LINEINFO`). That means that if you test case has 100
assertions, 200 constructor calls will need to be processed by the compiler.
Considering that I have some test files with around 400 assertions, this is
a lot of overhead for nothing. Moreover, two parameters have always the same
value, no need to repeat them every time.

Simplifying the macro to the minimum led me to this:

.. code:: cpp

    template<typename L, typename R>
    void evaluate_result(const char* file, std::size_t line, const char* exp, L lhs, R rhs){
        Catch::ResultBuilder result("REQUIRE", {file, line}, exp, Catch::ResultDisposition::Flags::Normal);
        result.setResultType(lhs == rhs);
        result.setLhs(Catch::toString(lhs));
        result.setRhs(Catch::toString(rhs));
        result.setOp("==");
        result.endExpression();
        result.react();
    }

    #define REQUIRE_EQUALS(lhs, rhs) \
        evaluate_result(__FILE__, __LINE__, #lhs " == " #rhs, lhs, rhs);

The macro is now a simple function call. Even though the function is a template
function, it will only be compiled for a few types (:cpp:`double` and
:cpp:`float` in my case), whereas the code of the macro would be unconditionally
compiled for each invocation.

With this new macro and function, the compilation time went down from 664
seconds to 554 seconds! This is **more than 16% reduction in compilation
time**. When comparing against the original compilation time (without both
optimizations) of 764 seconds, this is a 27% reduction! And there are absolutely
no difference in features.

This is a really great result, in my opinion. I don't think this can be cut down
more. However, there is still some room for optimization regarding the includes
that Catch need. Indeed, it is very bloated as well. A new test framework,
`doctest <https://github.com/onqtam/doctest>`_ follows the same philosophy, but
has much smaller include overhead. Once all the necessary features are in
doctest, I may consider adapting my macros for it and using it in place of Catch
is there is some substantial reduction in compilation time.

If you want to take a look at the code, you can find the adapted code on `Github <https://github.com/wichtounet/etl/blob/master/test/include/fast_catch.hpp>`_.
