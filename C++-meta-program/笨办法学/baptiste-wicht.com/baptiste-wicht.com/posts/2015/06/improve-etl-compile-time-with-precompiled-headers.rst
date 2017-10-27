Very recently, I started trying to improve the compile-time of the ETL test suite. While not critical, it is always better to have tests that compile as fast as possible. In a `previous post <http://baptiste-wicht.com/posts/2015/06/how-i-improved-a-bit-compile-time-of-etl.html>`_, I was able to improve the time a bit by improve the makefile, using pragra once and avoiding `<iostream>` headers. With these techniques, I reduced the compile-time from 87.5 to 84.1, which is not bad, but not as good as I would have expected. 

In the previous, I had not tried to use Precompiled Headers (PCH) to improve the compile time, so I thought it would be a good time to do it.

Precompiled Headers
###################

Precompiled Headers are an option of the compiler, where one header gets compiled. Normally, you only compile source files into object files, but you can also compile headers, although it is not the same thing. When a compiler compiles a header, it can do a lot of preprocessing (macros, includes, AST, symbols) and then store all the results into a precompiled header file. Once you compile the source files, the compiler will try to use the precompiled header file instead of the real header file. Of course, this can breaks the C++ standard since with that a header can not have different behaviour based on macros for instance. For these reasons (and probably implementation reasons as well), precompiled headers are really limited. 

If we take the case of G++, G++ will consider the precompiled header file instead of the standard header only if (for a complete list, take a look at the GCC docs): 

* The same compilation flags are the same between the two compilations
* The same compiler binary is used for the compilations
* Only one precompiled header can be used in each compilation
* The same macros must be defined
* The include of the header must be before every possible C/C++ token

If all these conditions are met and you try to `#include "header.hpp` and there is a header.hpp.gch (the precompiled file) available in the search path, then the precompiled header will be taken instead of the standard one. 

With clang, it is a bit different because the precompiled header cannot be included automatically, but has to be included explicitely in the source code, meaning you have to modify your code for this technique to work. This is a bad thing in my opinion, you never should have to modify your code to profit from a compiler feature. This is why I haven't used and don't plan to use precompiled headers with clang. 

How-to
######

Once you know all the conditions for a precompiled header to be automatically included, it is quite straightforward to use them.

To generate a PCH file is easy::

    g++ options header.hpp

This will generate header.hpp.gch. When you compile your source file using header.hpp, you don't have anything to do, you just have to compile it as usually and if all the conditions are met, the PCH file will be used instead of the other header. 

Results and conclusion
######################

I added precompiled header support into my `make-utils <https://github.com/wichtounet/make-utils>`_ collection of Makefile utilities and tested it on ETL. I have precompiled a header that itself included Catch and ETL. Almost all test files are including this header. With this change, I went from 84 seconds to 78seconds. Headers are taking 1.5seconds to be precompiled. This is a nice result I think. If your application is not as template-heavy as mine or if you have more source files, you should expect better improvements.

To conclude, even if precompiled headers are a sound way to reduce compile-time, they are really limited to some cases. I'm not a fan of the feature overally. It is not portable between compilers and not standard. Anyway, if you are really in need of saving some time, you should not hesitate too much ;)
