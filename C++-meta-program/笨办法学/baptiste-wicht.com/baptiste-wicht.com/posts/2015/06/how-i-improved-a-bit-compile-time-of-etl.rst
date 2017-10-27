Recently I read several articles about C++ and compile time and I wondered if I could improve the compile time of my Expression Template Library (ETL) project. ETL is a header-only and template-heavy library. I'm not going to the change the design completely or to use type erasure techniques to reduce the compile time, ETL is all about performance.

As a disclaimer, don't expect fancy results from this post, I haven't been able to reduce compile time a lot, but I still wanted to share my experience.

I've used g++-4.9.2 to perform these tests. 

I'm compiling the complete test suite (around 6900 source lines of codes in 36 files) in release mode. Each test file includes the ETL (around 10K SLOC). Each test is run with 8 threads (make -j8). For each result, I have run a complete build 5 times and taken the best result as the final result. Everything is run on a SSD and I have more than enough RAM to handle all the compilation in parallel.

The reference build time was 87.5 seconds.

Compile and generate dependency files at the same time
======================================================

To help write my makefiles, I'm using a set of functions that I have written. This includes automatic dependency generation using -MM -MT options of the compiler. Until now, I had two targets, one to compile the cpp file into the object file and another one to generate the dependency file. I recently saw that compilers were able to do both at the same time! Clang, G++ and the Intel compiler all have a -MD -MF options that lets you generate the dependency file at the same time you compile your file, saving you at least one read of the file.

My compilation rule in my makefile has now become: 

.. code:: makefile

    release/$(1)/%.cpp.o: $(1)/%.cpp
        @ mkdir -p release/$(1)/
        $(CXX) $(CXX_FLAGS) $(RELEASE_FLAGS) $(2) -MD -MF release/$(1)/$$*.cpp.d -o release/$(1)/$$*.cpp.o -c $(1)/$$*.cpp
        @ sed -i -e 's@^\(.*\)\.o:@\1.d \1.o:@' release/$(1)/$$*.cpp.d

This reduced the compilation time to 86.8 seconds. Not that much reduction, but it still is quite nice to know that. I would have expected this to reduce more the compile time.

Use #pragma once
################

Normally, I'm not a fan of #pragma since it is not standard, but for now ETL only supports three compilers and only very recent of them, so I have the guarantee that #pragma once is available, so what the hell!

I've replaced all the include guards by single #pragma once directives. 

Again, the results are not impressive, this reduced the compile time to 86.2 seconds. I would only advise to use this if you are sure of the compilers you want to support and you need the extra time.

Avoid <iostream>
################

I've read that the <iostream> header was one of the slowest to compile of the STL. It is only one that is included several times in my headers only for stream operators and it turns out that there is a <iosfwd> header that forward declares a lot of things from the <iostream> and other I/O headers.

By replacing all <iostream> include by <iosfwd>, compile time has gone down to 84.1 seconds. 

Conclusion
##########

By using the three techniques, I've reduced the compile time from 87.5 to 84.1 seconds. I would have honestly hoped for more improvements, but this is a already a good start.

As a side note, clang compile time is 45.2 seconds under the same conditions (was 46.2 seconds before the optimizations). It is really much faster :) I'm still using GCC a lot since in several cases, it does generate much better code and in average, the generated code if faster (on my benchmarks at least). I don't have the numbers for icc, but icc is definitely the slowest of the three. When I have it available (at work), I use for release build before running something. The generated executables are generally faster (I only use Intel processors) and sometimes the difference can be quite important.

If you have ideas to reduce further the compile time on this test case, I'd be glad to hear them and put them to the test.

I hope that this small experience would be helpful to some of you :)

Other techniques
################

There are several other techniques that you can use to reduce compile time: 

1. Precompiled Headers are supported by both Clang and GCC, altough not in a compatible. I haven't tested this in a while, but it is quite effective and a very interesting technique. The main problem with this is that is not standard and not compatible between compilers. But it probably is the most efficient techniques when you have lots of headers and lots of templates as in my case. 
2. Unity builds can make full rebuild much faster. I personally don't like unity builds especially because it is only really good for full builds and you generally don't do full rebuilds that much (I know, I know, this is also the test done in this article :) ). Moreover, it also sucks at doing parallel builds. 
3. Pimpl idioms and other type erasure techniques can reduce compile time a lot. If it is well done, it can be implemented without so much overhead.
4. Explicit instantiation of templates can also help, but only in the case of a user program. In the case of a library itself, you cannot do anything.
5. Reduce inclusions and use forward declarations, obviously...
6. Use tools like distcc (I very rarely use it) and ccache (I generally use it).
7. Update your compiler
8. Upgrade your computer ;)
9. ...
