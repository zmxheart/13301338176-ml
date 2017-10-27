This page lists a few of the projects I'm working on or I have worked in the
past years.

Expression Templates Library (ETL)
++++++++++++++++++++++++++++++++++

ETL is a C++ Expression Templates library for matrix and vector operations. It
has support for multiplication, convolutions and Fast Fourier Transform. It is
highly optimized for CPU and has some GPU support as well.

* `Posts about ETL <http://baptiste-wicht.com/categories/etl.html>`_
* `etl on Github <https://github.com/wichtounet/etl>`_

Deep Learning Library (DLL)
+++++++++++++++++++++++++++

DLL is a C++ machine learning library I have developed for my thesis. It has
principally support for (Convolutional-) Restricted Boltzmann Machine and Deep
Belief Network. But it also has support for standard Artificial Neural Network
(ANN) and Convolutional Neural Network (CNN). It is highly optimized for CPU.

* `Posts about dll <http://baptiste-wicht.com/categories/dll.html>`_
* `dll on Github <https://github.com/wichtounet/dll>`_

Thor Operating System
+++++++++++++++++++++

Thor is a very simple 64bit operating system for Intel processors that I have
been developing to learn about low-level development and operating systems. It
is written in C++ and some assembly. It is currently a multiprocessing operating
system with basic network capabilities. It can read and write to disk. It is
modeled after Linux.

* `Posts about thor <http://baptiste-wicht.com/categories/thor.html>`_
* `thor-os on Github <https://github.com/wichtounet/thor-os>`_

Continuous Perfomance Monitor (CPM)
+++++++++++++++++++++++++++++++++++

cpm is a simple C++ library for benchmarking code. This library is used like
most unit tests library by declaring benchmarks in the code and generating
a main running the benchmark that you wish. At execution, the stats of the
different benchmark are printed and some data files are saved. You can then use
the tool to generate a performance report of your benchmark. Moreover, the data
files can be kept over time to follow the evolution of your code performance
over time.

* `cpm on Github <https://github.com/wichtounet/cpm>`_

eddic Compiler
++++++++++++++

eddic is a small compiler for the EDDI language, written in C++, which I have
developed. There is nothing fancy about the language, it's mostly an inferior
clone of C++. The goal of this project was to learn C++ and Intel assembly. The
compiler is generating Intel 64bit assembly on Linux and is performing quite
a few optimizations like dead-code-elimination, conditional propagation, loop
unrolling or loop-invariant-code-motion.

I'm not currently working on this project anymore, but I may get back to it.

* `Posts about eddic <http://baptiste-wicht.com/categories/eddi.html>`_
* `eddic on Github <https://github.com/wichtounet/eddic>`_

budgetwarrior - command line budget application
+++++++++++++++++++++++++++++++++++++++++++++++

budgetwarrior is a simple command line accounting tool for personal usage. It is
written in C++. The principle is simple, you declare accounts which are your
spending categories and assign a monthly amount for each of these accounts. Then
you add expenses during the month and the tool will show how much is remaining
in each of these accounts for the current month and also gives you some stats on
the year. There are also some smaller features like a wish list that tries to
predict when you should buy some things and objectives tracking.

I'm using this tool as my main budget utility.

.. image:: https://raw.githubusercontent.com/wichtounet/budgetwarrior/develop/screenshots/budget_report.png
   :align: center
   :alt: budgetwarrior report view

* `Posts about budgetwarrior <http://baptiste-wicht.com/categories/budgetwarrior.html>`_
* `budgetwarrior on Github <https://github.com/wichtounet/budgetwarrior>`_
