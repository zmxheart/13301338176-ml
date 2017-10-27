`Bullseye <http://www.bullseye.com/>`_ is a commercial Code Coverage analyzer.
It is fully-featured with an export to HTML, to XML and even a specific GUI to
see the application.It costs about 800$, with a renewal fee of about 200$ per
year.

I'm currently using gcov and passing the results to Sonar. This works well, but
there are several problems. First, I need to use gcovr to generate the XML file,
that means two tools. Then, gcov has no way to merge coverage reports. In my
tests of ETL, I have seven different profiles being tested and I need the
overall coverage report. lcov has a merge feature but it is slow as hell (it
takes longer to merge the coverage files than to compile and run the complete
test suite seven times...). For now, I'm using a C++ program that I wrote to
combine the XML files or a Python script that does that, but neither are perfect
and it needs maintenance. Finally, it's impossible to exclude some code from the
coverage report (there is code that isn't meant to be executed (exceptional
code)). For now, I'm using yet another C++ program  that I wrote to do this from
comments in code.

Bullseye does have all these feature, so I got an evaluation license online and
tried this tool and wrote a short review of it.

Usage
#####

The usage is pretty simple. You put the coverage executables in your PATH
variable and activate coverage globally. Then, we you compile, the compiler
calls will be intercepted and a coverage file will be generated. When the
compilation is done, run the program and the coverage measurements will be
filled.

The coverage results can then be exported to HTML (or XML) or visualized using
the CoverageBrowser tool:

.. figure:: /images/bullseye_view.png
   :align: center
   :alt: Screenshot of Bullseye main coverage view

   The main view of the Bullseye tool code coverage results

It's a pretty good view of the coverage result. You have a breakdown by folders,
by file, by function and finally by condition. You can view directly the source
code:

.. figure:: /images/bullseye_source_view.png
   :align: center
   :alt: Screenshot of Bullseye source code coverage view

   The source view of the Bullseye tool code coverage results

If you want to exclude some code from your coverage reports, you can use
a pragma:

.. code:: cpp

    switch (n) {
        case 1: one++; break;
        case 2: two++; break;
        case 3: three++; break;
        #pragma BullseyeCoverage off
        default: abort();
        #pragma BullseyeCoverage on
    }

So that the condition won't be set as uncovered.

As for the coverage, it's pretty straightforward. For example:

.. code:: bash

   covmerge -c -ffinal.cov sse.cov avx.cov

and it's really fast. Unfortunately, the merging is only done at the function
level, not at the statement or at the condition level. This is a bit
disappointing, especially from a commercial tool. Nevertheless, it works well.

Conclusion
##########

To conclude, Bullseye seems to be a pretty good tool. It has more features than
standard gcov coverage and all features are well integrated together. I have
only covered the features I was interested in, there are plenty of other things
you can look at on the `official website <http://www.bullseye.com/>`_.

However, if you don't need the extra features such as the visualizer (or use
something like Sonar for this), or the merge or code excluding, it's probably
not worth paying the price for it. In my case, since the merge is not better
than my C++ tool (both do almost the same and my tool does some basic line
coverage merging as well) and I don't need the visualizer, I won't pay the price
for it. Moreover, they don't have student or open source licensing, therefore,
I'll continue with my complicated toolchain :)
