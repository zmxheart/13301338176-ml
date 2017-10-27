I recently started a bit of work on my compiler (eddic) again. I started by adapting it to build on CLang with libc++. There was some minor adaptions to make it compile, but nothing really fancy. It now compiles and runs fine on LLVM/Clang 3.4 with the last version of libc++. I'm gonna use some features of C++14 in it and I plan to refactor some parts to make it more *STL-correct*. I also plan to use only CLang on eddic right now, since C++14 support of GCC is not released right now. 

I decided it was a good time to try again the CLang static analyzer. 

## Installation

If, like me, you're using Gentoo, the static analyzer is directly installed with the *sys-devel/clang* package, unless you disabled the *static-analyzer* USE flag. 

If your distribution does not ship the static analyzer directly with CLang, you'll have to install it manually. To install it from sources, I advise you to follow the [Official Installations instruction](http://clang-analyzer.llvm.org/installation.html). 

## Usage

The usage of CLang static analyzer can be a bit disturbing at first. Most static analysis tools generally takes the sources directly and do their stuff. But that is not how Clang Static Analyzer works. It works as a kind of monitor in top of building the program, using *scan-build*. When you are analyzing a program, you are also building the program. 

For instance, if you are compiling a source file like that: 

```
clang [clang-options] source_file.cpp
```

you can perform static analysis like that: 

```
scan-build [scan-build-options] clang [clang-options] source_file.cpp
```

scan-build works by replacing calls to the compiler by calls to *ccc-analyzer *. This works generally well, but there are some cases where that things get a bit more complicated. That is the case of CMake where the paths to the compiler are hardcoded in the generated makefiles. 

For that, you have to run *cmake* and *make* with *scan-build*: 

```
export CCC_CC=clang
export CCC_CXX=clang++
scan-build cmake -DCMAKE_CXX_COMPILER=clang++ -DCMAKE_C_COMPILER=clang .
scan-build make
```

This can take a very long time. On eddic, it is about three times slower than a normal compilation. An important point to note about performance, is that you can run compilations in parallel (-j option of make) and that it is supported by scan-build quite well. 

Once analysis is performed, the found bugs are put into an HTML report. By default, the HTML report is created in */tmp/*, but you can specificy the folder with -o option of scan-build. 

You can enable or disable checker with the -enable-checker and -disable-checker options of scan-build. 

## Results on eddic

Several versions of Clang ago, I tried the static analyzer on eddic, but it failed on several source files without producing any results. Moreover, at this time, I don't think there was any nice HTML report at this time. 

I ran it again on eddic with the last versions. Here is a picture of the generated report: 

![CLang Static Analyzer eddic results](/images/eddic_results.png)

As you can see, 14 bugs have been found. Unfortunately, none of them is a real bug on my code, but they are not all false positives neither. For instance, here is some unreachable code report: 

![CLang Static Analyzer eddic bug](/images/eddic_results_bug.png)

It is indeed an unreachable statement, but it is expected, since it is an assert to ensure that the code is unreachable. But that proves that the analysis works ;) 

Even if it didn't found anything, this time it worked much better than the last time I checked and the HTML results are just really good. 

I hope you found this article interesting. If you happen to have interesting results on your codebase with the CLang static analyzer, I'd be glad to hear about them ;)