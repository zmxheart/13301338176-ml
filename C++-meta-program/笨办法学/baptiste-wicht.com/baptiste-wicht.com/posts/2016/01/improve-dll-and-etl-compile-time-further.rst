For a while, the compilation time of my matrix/vector computation library (ETL), based on Expression Templates has become more and more problematic. I've already worked on this problem `here <http://baptiste-wicht.com/posts/2015/06/how-i-improved-a-bit-compile-time-of-etl.html>`_ and `there <http://baptiste-wicht.com/posts/2015/06/improve-etl-compile-time-with-precompiled-headers.html>`_, using some general techniques (pragmas, precompiled headers, header removals and so on). On this post, I'll talk about two major improvements I have been able to do directly in the code.

Use of static_if
################

Remember `static_if <http://baptiste-wicht.com/posts/2015/07/simulate-static_if-with-c11c14.html>`_ ? I was able to use it to really reduce the compile time of DLL.

I wrote a script to time each test case of the DLL project to find the test cases that took the longest to compile. Once I found the best candidate, I isolated the functions that took the longest to compile. It was quite tedious and I did it by hand, primarily by commenting parts of the code and going deeper and deeper in the code. I was quite suprised to find that a single function call (template function of course ;) ) was responsible for 60\% of the compilation time of my candidate test case. The function was instantiating a whole bunch of expression templates (to compute the free energy of several models). The function itself was not really optimizable, but what was really interesting is that this function was only used in some very rare cases and that these cases were known at compile-time :) This was a perfect case to use a static_if. And once the call was inside the static_if, the test case was indeed about 60% faster. **This reduced the overall compilation time of DLL by about 30%**!

This could also of course also have been achieved by using two functions, one with the call, one empty and selected by SFINAE (Substitution Failure Is Not An Error). I prefer the statif_if version since this really shows the intent and hides SFINAE behind nicer syntax.

I was also able to use static_if at other places in the DLL code to avoid instantiating some templates, but the improvements were much less dramatic (about 1% of the total compilation time). I was very lucky to find a single function that accounted for so much compile time. After some more tests, I concluded that much of the compilation time of DLL was spent compiling the Expression Templates from my ETL library so I decided to delve into ETL code directly.

Removal of std::async
#####################

The second improvement was very surprising. I was working on improving the compilation of ETL and found out that the sum and average reductions of matrices were dramatically slow, about an order of magnitude slower than standard operations on matrices. In parallel (but the two facts are linked), I also found out another weird fact when splitting a file into 10 parts (the file was comprised of 10 test cases). Compiling the 10 parts separarely (and sequentially, not multiple threads) was about 40% faster than compiling the complete file. There was no swapping so it was not a memory issue. This is not expected. Generally, it is faster to compile a big file than to compile its parts separately. The advantage of smaller files is that you can compile them in parallel and that incremental builds are faster (only compile a small part).

By elimination, I found out that most of the time was spent inside the function that was dispatching in parallel the work for accumulating the sum of a matrix. Here is the function:

.. code:: cpp

    template <typename T, typename Functor, typename AccFunctor>
    inline void dispatch_1d_acc(bool p, Functor&& functor, AccFunctor&& acc_functor, std::size_t first, std::size_t last){
        if(p){
            std::vector<std::future<T>> futures(threads - 1);

            auto n = last - first;
            auto batch = n / threads;

            for(std::size_t t = 0; t < threads - 1; ++t){
                futures[t] = std::async(std::launch::async, functor, first + t * batch, first + (t+1) * batch);
            }

            acc_functor(functor(first + (threads - 1) * batch, last));

            for(auto& fut : futures){
                acc_functor(fut.get());
            }
        } else {
            acc_functor(functor(first, last));
        }
    }

There isn't anything really fancy about this function. This takes one functor that will be done in parallel and one function for accumulation.  It dispatches all the work in batch and then accumulates the results. I tried several things to optimize the compilation time of this function, but nothing worked. The line that was consuming all the time was the std::async line. This function was using std::async because the thread pool that I'm generally using does not support returning values from parallel functors. I decided to use a workaround and use my thread pool and I came out with this version:

.. code:: cpp

    template <typename T, typename Functor, typename AccFunctor>
    inline void dispatch_1d_acc(bool p, Functor&& functor, AccFunctor&& acc_functor, std::size_t first, std::size_t last){
        if(p){
            std::vector<T> futures(threads - 1);
            cpp::default_thread_pool<> pool(threads - 1);

            auto n = last - first;
            auto batch = n / threads;

            auto sub_functor = [&futures, &functor](std::size_t t, std::size_t first, std::size_t last){
                futures[t] = functor(first, last);
            };

            for(std::size_t t = 0; t < threads - 1; ++t){
                pool.do_task(sub_functor, t, first + t * batch, first + (t+1) * batch);
            }

            acc_functor(functor(first + (threads - 1) * batch, last));

            pool.wait();

            for(auto fut : futures){
                acc_functor(fut);
            }
        } else {
            acc_functor(functor(first, last));
        }
    }

I simply preallocate space for all the threads and create a new functor calling the input functor and saving its result inside the vector. It is less nice, but it works well. And it compiles MUCH faster. This **reduced the compilation time** of my biggest test case **by a factor of 8** (from 344 seconds to 44 seconds). This is really crazy. It also fixed the problem where splitting the test case was faster than big file (it is now twice faster to compile the big files than compiling all the small files separately). **This reduced the total compilation time of dll by about 400%**.

As of now, I still have no idea why this makes such a big difference. I have looked at the std::async code, but I haven't found a valid reason for this slowdown. If someone has any idea, I'd be very glad to discuss in the comments below.

Improving the template instantiation tree
#########################################

I recently discovered the templight tool that is a profiler for templates (pretty cool). After some time, I was able to build it and use it on ETL. For now, I haven't been able to reduce compile time a lot, but I have been able to reduce the template instantiation tree a lot seeing that some instantiations were completely useless and I optimized the code to remove them.

I won't be go into much details here because I plan to write a post on this subject in the coming days.

Conclusion
##########

In conclusion, I would say that it is pretty hard to improve the compile time of complex C++ programs once you have gone through all the standard methods. However, I was very happy to found that **two optimizations in the source code reduced the overall compilation of DLL by almost 500%**. I will continue working on this, but for now, the compilation time is much more reasonable.

I hope the two main facts in this article were interesting. If you have similar experience, comments or ideas for further improvements, I'd be glad to discuss them with you in the comments :)
