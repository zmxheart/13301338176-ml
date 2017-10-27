Already more than three years ago, I've written a `benchmark of some of the STL containers <https://baptiste-wicht.com/posts/2012/12/cpp-benchmark-vector-list-deque.html>`_,
namely the vector, the list and the deque. Since this article was very popular,
I decided to improve the benchmarks and collect again all the results. There are
now more benchmarks and some problems have been fixed in the benchmark code.
Moreover, I have also added a new container, the plf::colony. Therefore, there
are four containers tested:

* The std::vector: This is a dynamically-resized array of elements. All the
  elements are contiguous in memory. If an element is inserted or removed it at
  a position other than the end, the following elements will be moved to fill
  the gap or to open a gap. Elements can be accessed at random position in
  constant time. The array is resized so that it can several more elements, not
  resized at each insert operation. This means that insertion at the end of the
  container is done in amortized constant time.
* The std::deque: The deque is a container that offer constant time insertion
  both at the front and at the back of the collection. In current c++ libraries,
  it is implementation as a collection of dynamically allocated fixed-size
  array. Not all elements are contiguous, but depending on the size of the data
  type, this still has good data locality. Access to a random element is also
  done in constant time, but with more overhead than the vector. For insertions
  and removal at random positions, the elements are shifted either to the front
  or to the back meaning that it is generally faster than the vector, by twice
  in average.
* The std::list: This is a doubly-linked list. It supports constant time
  insertions at any position of the collection. However, it does not support
  constant time random access. The elements are obviously not contiguous, since
  they are all allocated in nodes. For small elements, this collection has
  a very big memory overhead.
* The plf::colony: This container is a non-standard container which is
  unordered, it means that the insertion order will not necessarily be
  preserved. It provides strong iterators guarantee, pointers to non-erased
  element are not invalidated by insertion or erasure. It is especially tailored
  for high-insertion/erasure workloads. Moreover, it is also specially optimized
  for non-scalar types, namely structs and classes with relatively large data
  size (greater than 128 bits on the official documentation). Its implementation
  is more complicated than the other containers. It is also implemented as
  a list of memory blocks, but they are of increasingly large sizes. When
  elements are erased, there position is not removed, but marked as erased so
  that it can be reused for fast insertion later on. This container uses the
  same conventions as the standard containers and was proposed for inclusion to
  the standard library, which is the main reason why it's included in this
  benchmark. If you want more information, you can consult the
  `official website <http://plflib.org/colony.htm>`_.

In the text and results, the namespaces will be omitted. Note that I have only
included sequence containers in my test. These are the most common containers in
practices and these also the containers I'm the most familiar with. I could have
included multiset in this benchmark, but the interface and purpose being
different, I didn't want the benchmark to be confusing.

All the examples are compiled with g++-4.9.4 (-std=c++11 -march=native -O2) and
run on a Gentoo Linux machine with an Intel Core i7-4770 at 3.4GHz.

For each graph, the vertical axis represent the amount of time necessary to
perform the operations, so the lower values are the better. The horizontal axis
is always the number of elements of the collection. For some graph, the
logarithmic scale could be clearer, a button is available after each graph to
change the vertical scale to a logarithmic scale.

The tests are done with several different data types. The trivial data types are
varying in size, they hold an array of longs and the size of the array varies to
change the size of the data type. The non-trivial data type is composed of
a string (just long enough to avoid SSO (Small String Optimization) (even though
I'm using GCC)). The non-trivial data types comes in a second version with
noexcept move operations.  Not all results are presented for each data types if
there are not significant differences between in order to keep this article
relatively short (it's already probably too long :P).

.. TEASER_END

Here are direct links to all results:

* `Fill`_ Benchmark
* `Emplace`_ Benchmark
* `Fill Front`_ Benchmark
* `Linear Search`_ Benchmark
* `Iterate and modify`_ Benchmark
* `Random Insert`_ Benchmark
* `Random Remove`_ Benchmark
* `Iterate and Erase`_ Benchmark
* `Number Crunching`_ Benchmark
* `Sort`_ Benchmark
* `Destruction`_ Benchmark

Fill
****

Let's start with the most obvious benchmark, insertion. A series of elements
will be inserted at the back of the container. Vector and colony both have
a reserve function that is able to preallocate memory for the necessary
elements. For both containers, both the standard version without reserve and the
version with reserve are benchmarked.

Since colony is unordered, this test is using insert in place of push_back.

.. raw:: html

    <div id="graph_fill_back___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_fill_back___Trivial_8_" type="button" value="Logarithmic scale">

For a very small data type, the results are very clear, the vector with reserve
is the winner, directly followed by the deque. After this, almost three times
slower is the colony and vector without pre-allocation. Interestingly, reserve
on the colony container does not help at all. And finally, the list is the
slowest container. The fact that the deque is faster than the vector (without
reserve) is logical since the deque will not have to move all its elements after
a reallocation.

With a type of 32bytes, the results are almost the same with less margin, let's
see with something bigger:

.. raw:: html

    <div id="graph_fill_back___Trivial_128_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_fill_back___Trivial_128_" type="button" value="Logarithmic scale">

This time, the vector without reserve() is clearly the slowest and we can two
plateaus which are corresponding to the levels of cache. The fastest is now the
deque almost on par with the vector preallocated. The colony and the list are
now at the same speed.

.. raw:: html

    <div id="graph_fill_back___Trivial_4096_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_fill_back___Trivial_4096_" type="button" value="Logarithmic scale">

With a large data type (4096 bytes), the list becomes the fastest, followed by
the deque and a bit slower the colony versions and vector_reserve.

Let's see with a non-trivial type that is costly to copy but fast to move:

.. raw:: html

    <div id="graph_fill_back___NonTrivialStringMovable" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_fill_back___NonTrivialStringMovable" type="button" value="Logarithmic scale">

The vector and the deque are able to take good advantage of the move and are the
fastest here. Even the vector without pre allocations is faring quite well here.

Let's see if there is a difference with noexcept on the move operation:

.. raw:: html

    <div id="graph_fill_back___NonTrivialStringMovableNoExcept" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_fill_back___NonTrivialStringMovableNoExcept" type="button" value="Logarithmic scale">

There is in fact a significant difference for the vector without pre allocation,
that is 20% faster than with the previous version. Indeed, since it knows that
the operation cannot throw it can use a faster path for reallocation and still
guarantees its exception safety.

Overall, for insertions, the vector and deque are the fastest for small types
and the list is the fastest for the very large types. colony offers a medium
performance on this benchmark but is quite stable for different data types. When
you know the size of the collection, you should always use reserve() on vectors.
Moreover, if you can use noexcept operations, you should always do it since it
can significantly speedup the vector performance.

Emplace
*******

The next operation is very similar to the first except that we use emplace
insertions instead of pushes.

.. raw:: html

    <div id="graph_emplace_back___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_emplace_back___Trivial_8_" type="button" value="Logarithmic scale">

As expected, there is no difference between push_back and emplace_back for
trivial types. The preallocated vector is the fastest container, followed by the
deque and then significantly slower are the colony and vector without
preallocation.

.. raw:: html

    <div id="graph_emplace_back___NonTrivialStringMovable" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_emplace_back___NonTrivialStringMovable" type="button" value="Logarithmic scale">

However, here is a very large difference between the push version and the
emplace version. Indeed, it is much slower. This may seem hard to believe that
emplace is slower than normal insert since it should be at least as fast, and
generally faster. This in fact due to the version of GCC that is still using
Copy-On-Write for string. Therefore, the previous version was much faster
because the copies were not done since the string was not modified and this
saved a lot of time in that case. However, this is an artificial case since
a collection filled of all the same string is not highly likely in practice.
Generally, I think it's better to use Small-String-Optimization than
Copy-On-Write and now COW is not allowed by the standard anymore in C++11.

Overall, for the tested types, emplace should have exactly the same performance
as normal push_back. Except for the special case of COW for GCC that should not
happen anymore if you use a recent compiler and C++11.

Fill Front
**********

The next benchmark is again a fill benchmark but elements are inserted at the
front of the container. Since colony is unordered, it's removed from the
benchmark. This benchmark is mostly used because this is the worst case for
vector.

.. raw:: html

    <div id="graph_fill_front___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_fill_front___Trivial_8_" type="button" value="Logarithmic scale">

As expected, the vector is terribly slower than the deque and list containers,
by almost three orders of magnitude. As before, the deque is much faster than
the list.

If you really need a collection that offers performance for front and back
insertions, you should definitely prefer the deque over the vector. The list
should only be preferred for very large data types.

Linear Search
*************

The next operation is that is tested is the search. The container is filled with
all the numbers in [0, N] and shuffled. Then, each number in [0,N] is searched
in the container with std::find that performs a simple linear search.  In
theory, all the data structures should perform the same if we only consider
their complexity.

.. raw:: html

    <div id="graph_linear_search___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_linear_search___Trivial_8_" type="button" value="Logarithmic scale">

The differences between the different data structures are very important. The
list is about 8 times slower than the vector container. The deque is slightly
slower than the vector. Interestingly, the colony container is even 50% slower
than the list container.

This is simply due to the much better data locality of the vector and deque
compared to the list. The list is in fact a terrible data structure for data
locality. Every time the next element needs to be accessed, it is necessary to
jump from memory positions to memory positions. Since the processor will always
load a full cache line, several elements of the vector will be loaded in memory
at once and the next few loads will directly reading from the cache rather than
from the main memory. In the case of the list, this does not help since elements
are not contiguous. It means that all the time will be wasted with cache misses.
The vector is even faster than the deque because all elements are contiguous
whereas in a deque, only packs of elements are contiguous.

.. raw:: html

    <div id="graph_linear_search___Trivial_128_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_linear_search___Trivial_128_" type="button" value="Logarithmic scale">

For a bigger data type (128 bytes), the differences are smaller. The list is
only three times slower than the vector and deque which are about at the same
speed. The colony is now significantly faster than the list, but still much
slower than vector and deque.

.. raw:: html

    <div id="graph_linear_search___Trivial_4096_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_linear_search___Trivial_4096_" type="button" value="Logarithmic scale">

For a large data type (4KB), the list is still twice slower than the other
containers. For such big data, the colony is now the fastest data type, followed
by the deque and vector.

Overall, the speed of linear searching through a data structure is mostly
limited by the data locality of the container. For this reason, the deque and
vector containers are significantly faster than the list. Interestingly, the
colony container becomes faster than the other containers for large data types.
Overall, a list should never be used if the container is to be searched a lot.

Iterate and modify
******************

The next test iterates over the entire collection and increment each number
contained inside it. It uses the begin and end iterators for each container. The
time should be mostly dominated by the iteration time.

.. raw:: html

    <div id="graph_write___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_write___Trivial_8_" type="button" value="Logarithmic scale">

As expected, the list is the slowest of the container for that sort of
operations and the vector is the fastest. The deque is slightly slower than the
vector and the colony slightly faster than the list. There is a 6 times
difference between the best result, which is pretty significant.

.. raw:: html

    <div id="graph_write___Trivial_32_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_write___Trivial_32_" type="button" value="Logarithmic scale">

As the data type size augments, the deque starts to gets slightly better than
the vector and the colony starts to get much better than the list, but still not
on par with the other containers.

.. raw:: html

    <div id="graph_write___Trivial_128_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_write___Trivial_128_" type="button" value="Logarithmic scale">

Once increased to 128B, the colony really starts to get interesting being the
fastest with the deque depending on the number of elements.

.. raw:: html

    <div id="graph_write___Trivial_4096_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_write___Trivial_4096_" type="button" value="Logarithmic scale">

Interestingly, with very large data types (4KB), the vector takes the lead
again, very close to the colony and the deque a bit behind.

Overall, one thing is clear, the list is really bad for iteration. The colony
container starts to shine when the size of the data type is around 128B, but
does not provide a very significant speedup. The vector is generally the fastest
for this kind of workload.

Random Insert
*************

The following test is performing insertions at random places.  In the case of
random insert, in theory, the list should be much faster, its insert operation
being in O(1) versus O(n) for a vector or a deque.  The container is filled with
all the numbers in [0, N] and shuffled. Then, 1000 random values are inserted at
a random position in the container. The random position is found by linear
search. In both cases, the complexity of the search is O(n), the only difference
comes from the insert that follow the search. We saw before that the performance
of the list were poor for searching, so we'll see if the fast insertion can
compensate the slow search. In my opinion, this is something that happens more
in practice than simply inserting at a random position. To go to this random
position, you need to iteration to this point (the list is not random, then to
find an iterator position, you have no other choice). Again, since it's not
possible to insert at a random position in a colony, it is removed from the
benchmark.

Let's see how the containers are doing on this workload:

.. raw:: html

    <div id="graph_random_insert___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_random_insert___Trivial_8_" type="button" value="Logarithmic scale">

With a trivial number of 8 bytes, the vector and deque are clearly faster than
the list. Even though the insertion itself is much faster on the list than on
the other data structures, the search for the random positions is much slower.
However, the list is *only* three times slower than the other containers.

.. raw:: html

    <div id="graph_random_insert___Trivial_32_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_random_insert___Trivial_32_" type="button" value="Logarithmic scale">

At 32 bytes, we can see interesting things. The deque is now faster than the
vector. This is logical since the deque can decide to move either to the front
or the back and therefore should perform in average twice less move operations
than the vector. Moreover, the overhead of the list is now getting lower, with
only twice longer times.

.. raw:: html

    <div id="graph_random_insert___Trivial_128_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_random_insert___Trivial_128_" type="button" value="Logarithmic scale">

At 128 bytes element, the vector is not the fastest container anymore. It is now
becoming costly to move the elements and the iteration is becoming to be less
important. Still, the deque is the fastest on this benchmark.

.. raw:: html

    <div id="graph_random_insert___Trivial_1024_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_random_insert___Trivial_1024_" type="button" value="Logarithmic scale">

With 1024 bytes, the list is already significantly faster than the other
containers. The deque is now almost twice faster than the vector. This is
showing that the cost of iterating is now almost insignificant compared to the
cost of insertion. The list is now 4 times faster than the deque and almost
7 times faster than the vector.

If we go to 4096 bytes, the list is 10 times faster than the vector.

.. raw:: html

    <div id="graph_random_insert___NonTrivialStringMovable" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_random_insert___NonTrivialStringMovable" type="button" value="Logarithmic scale">

If we use a non-trivial data types, it becomes interesting again, even if not
that big data type. The list is already 5 times faster than the vector and
almost three times faster than the deque. This is logical, since this time it is
not enough to perform a memory copy, each object must be moved or copied from
position to position. In that case, the list only performs one of such
operations, but has the overhead of finding the position again.

.. raw:: html

    <div id="graph_random_insert___NonTrivialStringMovableNoExcept" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_random_insert___NonTrivialStringMovableNoExcept" type="button" value="Logarithmic scale">

If we add noexcept to the move operations, the vector and deque are getting
about 50% faster than before. Although they are still slower than the list for
big number of elements. This is simply because of the thread safety guarantee
of these collections that is very costly on non-noexcept move operations.

Overall, the vector and deque are clearly the winner for small trivial
data types. However, for large data types or non-trivial, the list is becoming
very interesting because it will perform O(1) insertion and the cost of
insertion of the other containers is now bigger than the cost of iterating
through the list.

Random Remove
*************

The following benchmark is quite similar than the previous except that instead of
inserting an element at a random position, a random element is removed. However,
this is done a bit differently. The collection is filled with the numbers of
[0,N] and then shuffled. Then, the numbers in [0, 1000] are removed from the
collection. This means that a linear search is done to find the position of the
element to remove.

For each collection, two versions are performed. The first version is simply
doing 1000 times a erase of the result of a find_if. The second version is using
the erase remove idiom, but only once. Of course, the remove versions (named
X_rem) are expected to be much faster than the naive versions.

(I advice to use the logarithmic scale to compare the results)

.. raw:: html

    <div id="graph_random_remove___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_random_remove___Trivial_8_" type="button" value="Logarithmic scale">

If we use a trivial small data types, the vector and the deque versions are the
fastest. The list is three times slower than them and the colony is four times
slower. As expected, the versions using one erase remove instead of several
erase is significantly faster. In fact, it is around three orders of magnitude
faster. This should not come as a surprise to anyone. What this will do is move
the elements to be removed to the end of the collection and then erase in one go
all the elements that have been moved. When using remove, the vector is twice
faster than the deque and four times faster than the list and colony.

.. raw:: html

    <div id="graph_random_remove___Trivial_32_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_random_remove___Trivial_32_" type="button" value="Logarithmic scale">

With a bigger data type, for naive versions, the deque is now getting faster
than the vector because of more efficient removal, again because it can choose
the direction in which the collection is moved. The colony is getting much
faster too, but still 20% slower than the vector. The list is still
significantly slower, but only 2.5 times slower than the deque now. For the
remove versions, the vector and deque versions are now at the same speed and the
colony is only slightly slower than them. The list is still about 2.5 times
slower than the vector and deque.

.. raw:: html

    <div id="graph_random_remove___Trivial_128_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_random_remove___Trivial_128_" type="button" value="Logarithmic scale">

With elements of 128 bytes, we see what was already observed in the Random
Insert benchmark, the deque is twice faster than the vector and even the list is
significantly faster than the vector. What is more interesting is that the colony
is now the fastest collection for the naive version. This shows excellent
removal and iteration performance for this kind of data. For the remove
versions, only the list is about 20% slower than the other versions which are
running at the same speed.

.. raw:: html

    <div id="graph_random_remove___Trivial_4096_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_random_remove___Trivial_4096_" type="button" value="Logarithmic scale">

For the 4KB data type, the difference between the naive versions are very
significant. The colony is significantly faster than all the other collections.
It is 4 times faster than the list, 26 times faster than the deque and 46 times
faster than the vector. The remove versions are almost all the same with the
list being 20% slower again the other containers. h

.. raw:: html

    <div id="graph_random_remove___NonTrivialStringMovable" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_random_remove___NonTrivialStringMovable" type="button" value="Logarithmic scale">

For a non-trivial type, we can see the same behaviour as random insertions. The
colony and the list are the fastest type, followed by the deque and finally by
the vector. The remove versions are much faster, the list being the slowest.

Overall, on this benchmark we can see that again the vector and deque are the
fastest containers. When the data type starts to be big, the colony starts to
shine. The version performing a single erase with remove_if is much faster than
the naive versions. Interestingly, with the remove version, all the collections
are performing at almost the same speed, with the list being the slowest. In
practice, if you know that you have to erase a series of elements, you should do
a erase with remove_if instead of several erase.

Iterate and Erase
*****************

The next benchmark is about erasure again, but is quite different than the
previous one. This benchmark iterates through the entire collection and at each
point, randomly decides if the element is to be removed or not. This may seem
almost the same as the naive version of the Random Erase benchmark but this is
quite difference since each erasure does not an iteration, only one iteration
though the whole container is done. This is probably something a bit more
realistic than the other workloads. Moreover, this benchmark is done with
several different erasure percentage.

Let's start with 1% probability of erasing an element.

.. raw:: html

    <div id="graph_erase1___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_erase1___Trivial_8_" type="button" value="Logarithmic scale">

Actually, I was quite surprised by these results. I would not have thought the
difference to be this huge already for 1% removal. Indeed, the list and colony
are already faster than the vector by one order of magnitude! The deque is
slightly faster than the vector.

.. raw:: html

    <div id="graph_erase1___Trivial_128_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_erase1___Trivial_128_" type="button" value="Logarithmic scale">

With 128B elements, the colony is starting to get faster than the list by about
50%. The deque is also much faster here than the vector. The colony is about
two orders of magnitude faster than the vector and almost as much faster than the deque.

.. raw:: html

    <div id="graph_erase1___Trivial_1024_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_erase1___Trivial_1024_" type="button" value="Logarithmic scale">

With 1024B elements, the colony is now 3 times faster than the list! It is now
three orders of magnitude faster than the vector and deque.

.. raw:: html

    <div id="graph_erase1___Trivial_4096_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_erase1___Trivial_4096_" type="button" value="Logarithmic scale">

If we go even higher, with 4096B elements, the colony is now 4 times faster than the list! It is now
about four orders of magnitude faster than the vector and deque.

Let's see what happens with a 10% erase rate:

.. raw:: html

    <div id="graph_erase10___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_erase10___Trivial_8_" type="button" value="Logarithmic scale">

In the previous case, the list and colony were faster than the vector by about
a factor 10 and now it's almost a factor 100!.

.. raw:: html

    <div id="graph_erase10___Trivial_4096_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_erase10___Trivial_4096_" type="button" value="Logarithmic scale">

If we jump directly to the biggest data type, the colony is again about four
times faster than the list and it's now 5 orders of magnitude faster than the
vector and deque!

Just for the sake of it, let's see what happens if we erase with a probability of 50%:

.. raw:: html

    <div id="graph_erase50___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_erase50___Trivial_8_" type="button" value="Logarithmic scale">

This time the colony and list are faster by more than 3 orders of magnitude than
the vector.

.. raw:: html

    <div id="graph_erase50___Trivial_4096_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_erase50___Trivial_4096_" type="button" value="Logarithmic scale">

For the last configuration of this test, the colony is about 3 times faster than
the list and about 5 orders than the vector. Just to put a number on it, the
colony in this case is about 361'143 times faster than the vector!

It should be clear from this result that it's a terribly bad workload for the
vector and deque which both have very bad erasure time. The results would be
about the same if were were to insert instead of erase. In each test the vecotr
and deque are several orders of magnitude slower than the other contenders. The
colony is always as fast and the list and several times faster than the list
with larger data types.

Number Crunching
****************

The next test is about numbers. Random numbers are inserted into the container
so that it is kept sorted. That means that a linear search will be performed to
find the insertion point. Since colony is unordered, it is excluded from this
benchmark. In practice, vector and deque could use binary search contrary to the
list.

Let's see the result with a number of 8 bytes:

.. raw:: html

    <div id="graph_number_crunching___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_number_crunching___Trivial_8_" type="button" value="Logarithmic scale">

The results are quite clear. The list is more than 20 times slower and than the
vector and the deque. This is because this benchmark is driven more by iterations than
by modifications of the structure and therefore the vector and deque are much
faster at this. Vector is still faster than the deque for its slightly better
locality.

If we take elements of 32 bytes:

.. raw:: html

    <div id="graph_number_crunching___Trivial_32_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_number_crunching___Trivial_32_" type="button" value="Logarithmic scale">

The list is *only* 8 times slower than the vector and deque. There is no doubt
that the difference would be even slower as the size of the elements grows.
Nevertheless, since we are talking about number crunching, this is rarely with
bigger numbers.

Overall, for such a load, the vector and deque structures are shining because of
their excellent iteration performance compared to the poor performance of the
list. Moreover, keep into account that in practice this would be done using
binary search for the vector and deque, probably meaning faster time for them
(even though binary search is not cache-efficient).

Sort
****

Let's see how the different collections are efficient at being sorted. For
list and colony, the sort member function is used while the other are using
std::sort directly. Moreover, since the colony offers two different sort
methods, both where tested. The first is using std::sort internally and the
second is using a timsort algorithm.

Let's start with a very small type:

.. raw:: html

    <div id="graph_sort___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_sort___Trivial_8_" type="button" value="Logarithmic scale">

The vector is the fastest container here, closely followed by the deque. colony
is about twice slower, with the timsort being slightly slowest. The list is
about 8 times slower than the vector.

.. raw:: html

    <div id="graph_sort___Trivial_128_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_sort___Trivial_128_" type="button" value="Logarithmic scale">

For a bigger data type, the differences are smaller between the containers. The
vector is still the fastest, but only 2.3 times faster than the list.
Interestingly, the timsort is now faster than the standard sort algorithm.

.. raw:: html

    <div id="graph_sort___Trivial_4096_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_sort___Trivial_4096_" type="button" value="Logarithmic scale">

For a really big data type, the list becomes the fastest container. The vector
and deque containers now the slowest containers. The colony is significantly
faster than the vector on this data type but still twice slower than the list.

Overall, sorting a vector and deque is generally faster than a list unless the
data type gets too big (>1KB). Again, the colony container is in a sort of
middle ground with very stable performance for both large and small data types
but is never the fastest on this benchmark.

Destruction
***********

The last test that is done is used to measure the time necessary to delete
a container. The containers are dynamically allocated, filled with n numbers,
and then their destruction time (via delete) is computed. This is probably never
a bottleneck in practice, but this is still interesting to benchmark in my
opinion.

.. raw:: html

    <div id="graph_destruction___Trivial_8_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_destruction___Trivial_8_" type="button" value="Logarithmic scale">

As you can see, the differences between the benchmarks are very significant. The
list is 10'000 times slower than the vector, the colony is 2000 slower than it
and even the deque is 200 times slower than the vector. The deallocation of
a vector, for trivial type, is simply a memory deallocation so its speed purely
depends on the speed on deallocating memory which is very fast on modern
systems.  The other containers need to deallocate all the small pieces they have
allocated. Not only does that mean more deallocations but especially means
walking through most of the elements.

.. raw:: html

    <div id="graph_destruction___Trivial_128_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_destruction___Trivial_128_" type="button" value="Logarithmic scale">

For a larger data type, the results are changing significantly. The overhead of
the deque is going up very quickly. This is actually normal each of the blocks
of the deque are actually very few elements and therefore it becomes very close
to a list in terms of deallocation and memory walks. What is very interesting
here is that colony actually is going on par with the vector and sometimes
slower than it. This shows that very deallocations are not necessary slower than
several smaller deallocations. Moreover, this also shows that colony is
especially good when the data type starts to become important.

.. raw:: html

    <div id="graph_destruction___Trivial_4096_" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_destruction___Trivial_4096_" type="button" value="Logarithmic scale">

For a very large data type, the vector and the colony are the fastest
collection, followed by the deque and list, only 1.8 times slower. This shows
that at this point, the deque makes as much allocations than the list.

.. raw:: html

    <div id="graph_destruction___NonTrivialStringMovable" style="width: 700px; height: 400px;"></div>
    <input id="graph_button_destruction___NonTrivialStringMovable" type="button" value="Logarithmic scale">

For a non-trivial type, every collection has to go through each element and
calls the necessary destructor. Therefore, the time is mostly related to the
iteration time. This puts the list on the bottom and the three other containers
at almost the same time.

Overall, the destruction of a vector for trivial types is significantly faster
than the other collections, unless the data type becomes very big. Colony has
a large overhead for small types but becomes interesting for large data types.
The list is always a poor contender since it needs to walk through all elements
in order to deallocate each node. Interestingly, the deque has more and more
overhead as the data type grows since each block will be able to hold less and
elements and therefore resembles a list. When types are non-trivial, the time
for destruction is generally tied to the time necessary to walk through the
entire collection and calls each of the destructor.

Conclusion
**********

With all these results in mind, it's now time to try to get some conclusions
about the different containers.

vector:

* The best iteration performance
* The best back insertion performance (with reserve)
* Excellent number crunching performance
* Very good for sorting small data types, bad for big types
* Very fast destruction
* Slow insertion/removal at random positions

deque:

* Very good iteration performance
* Very good insertion at the back (better with vector without reserve)
* The best front insertion performance
* Excellent number crunching performance
* Very good for sorting small data types, bad for big types
* Slow destruction for large data types
* Better insertion/removal at random position than vector, but still slow for
  many modifications

list:

* The slowest iteration performance in general
* Slow destruction
* Very fast insertion/removal from a known position

colony:

* Bad iteration performance for small types
* Good iteration performance starting from medium data types (and excellent for
  large ones)
* No possibility of random insertions
* The fastest insertion/removal from a known position
* Good sort performance for large data types, bad performance for small data
  types

Overall, each container has some advantages and disadvantages. It highly depends
on your workload:

* If you have purely iteration oriented workload, the vector is your best option, followed by
  the deque and finally the colony is quite good too if you have large elements to
  store. The list is terrible at iteration, whatever the element type.
* If you need to search a position and insert at this position, you should choose
  a deque or a vector for small trivial data types and a list or large data types
  or non-trivial ones.
* If you have to search a position and remove at this position, it depends if
  you can use erase-remove_if instead of several erase. If you can use
  erase-remove, you should use a vector or a deque or maybe a colony (only for
  large data types). If you cannot, use a deque or a vector for small data
  types and a colony (or list if you can't use colony) for larger types.
* If you iterate and do several removal and/or insertions during the same
  iteration, you should NOT use a vector or a deque and should use a colony (or
  list, if you can't) for all data types.
* If you have a sort-driven workload, you should use a vector or a deque for
  small types, a colony for medium types and a list for the big data types.
* If you have another workload, just benchmark it ;)

And maybe a few tips that can be extracted from the results:

* If you need to decide on which container to use for your task, the best way to
  decide is still to benchmark for your specific workload.
* Don't base your decisions only on Complexity Analysis, it does not take memory
  locality into account.
* If you can, try to make noexcept move operations, this will significantly
  speedup your vector and deque operations (when insert or remove occurs in the
  middle).
* If you need to erase several elements of a collection, you should always use
  one erase remove_if instead of several.
* If you need to insert several elements in a vector, always reserve the memory
  first to save some time on reallocations.

I hope this overly long post was interesting to you and that you learned
something. If you have any questions or comments, don't hesitate to post in the
comments section below.

The `code of the benchmark <https://github.com/wichtounet/articles/blob/master/src/vector_list_update_1/bench.cpp>`_ is available on Github. If you happen to run it, you should probably comment the eraseX benchmarks that takes an awful long time to run.

Another time, I hope to be able to run these benchmarks on several compilers,
but I'll have to trim down a bit some of the benchmarks (mainly the Iterate and
Erase benchmarks that takes a very long time). I'll also need to find a way to
present the result for multiple compilers in a nice way.

.. raw:: html

    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">google.load('visualization', '1.0', {'packages':['corechart']});</script>

    <script type="text/javascript">
    function draw_destruction___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['100000', 240, 22, 988, 0],
    ['200000', 485, 45, 1963, 0],
    ['300000', 707, 96, 2996, 0],
    ['400000', 962, 100, 4004, 0],
    ['500000', 1197, 114, 4989, 0],
    ['600000', 1425, 138, 5986, 0],
    ['700000', 1683, 214, 6967, 0],
    ['800000', 1902, 189, 7985, 0],
    ['900000', 2138, 263, 9532, 0],
    ['1000000', 2409, 244, 10874, 0],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_destruction___Trivial_8_'));
    var options = {curveType: "function",title: "destruction - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_destruction___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_destruction___Trivial_128_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['100000', 251, 798, 2504, 0],
    ['200000', 499, 1952, 5396, 0],
    ['300000', 740, 3088, 8595, 1361],
    ['400000', 1003, 4120, 12363, 1850],
    ['500000', 1240, 5138, 15873, 2151],
    ['600000', 1513, 6857, 19100, 2798],
    ['700000', 4926, 7974, 22711, 4032],
    ['800000', 5581, 9172, 28014, 4692],
    ['900000', 6750, 10813, 32839, 5626],
    ['1000000', 7523, 16168, 38987, 6070],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_destruction___Trivial_128_'));
    var options = {curveType: "function",title: "destruction - Trivial<128>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_destruction___Trivial_128_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_destruction___Trivial_4096_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['100000', 23042, 27594, 26377, 20120],
    ['200000', 41537, 56241, 55568, 40759],
    ['300000', 56177, 82809, 79245, 53583],
    ['400000', 70974, 101075, 102419, 68183],
    ['500000', 85402, 125446, 124540, 83074],
    ['600000', 105598, 149797, 153420, 98360],
    ['700000', 114786, 169089, 171255, 111156],
    ['800000', 125951, 189993, 193192, 125769],
    ['900000', 134245, 211323, 212633, 137547],
    ['1000000', 146530, 227040, 228934, 145069],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_destruction___Trivial_4096_'));
    var options = {curveType: "function",title: "destruction - Trivial<4096>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_destruction___Trivial_4096_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_destruction___NonTrivialStringMovable(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['100000', 251, 230, 1129, 247],
    ['200000', 503, 452, 2682, 453],
    ['300000', 755, 684, 3699, 685],
    ['400000', 1018, 930, 5477, 910],
    ['500000', 1281, 1208, 6278, 1157],
    ['600000', 1551, 1502, 7664, 1411],
    ['700000', 1813, 1789, 8733, 1799],
    ['800000', 2089, 2126, 9732, 1821],
    ['900000', 2347, 2442, 11301, 2053],
    ['1000000', 2672, 2786, 12209, 2369],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_destruction___NonTrivialStringMovable'));
    var options = {curveType: "function",title: "destruction - NonTrivialStringMovable",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_destruction___NonTrivialStringMovable');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_fill_back___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_reserve', 'vector_reserve', 'colony', 'deque', 'list', 'vector'],
    ['100000', 535, 201, 538, 984, 1649, 576],
    ['200000', 1088, 403, 1070, 478, 3160, 1163],
    ['300000', 1636, 603, 1607, 723, 4683, 2136],
    ['400000', 2188, 805, 2146, 956, 6220, 2397],
    ['500000', 2729, 1005, 2705, 1204, 7713, 2879],
    ['600000', 3226, 1207, 3219, 1440, 9317, 4423],
    ['700000', 3773, 1410, 3755, 1685, 10740, 4689],
    ['800000', 4316, 1615, 4310, 1925, 12251, 4941],
    ['900000', 4875, 1990, 4944, 2169, 13778, 5277],
    ['1000000', 5375, 2038, 5448, 2399, 15261, 5709],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_fill_back___Trivial_8_'));
    var options = {curveType: "function",title: "fill_back - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_fill_back___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_fill_back___Trivial_128_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_reserve', 'vector_reserve', 'colony', 'deque', 'list', 'vector'],
    ['100000', 2041, 1527, 2081, 1860, 2884, 3210],
    ['200000', 4047, 3027, 4264, 3981, 5731, 10844],
    ['300000', 6199, 8223, 6373, 6101, 8796, 25019],
    ['400000', 8535, 10836, 8330, 8189, 11999, 27840],
    ['500000', 10407, 13427, 10295, 10384, 14704, 30540],
    ['600000', 22272, 16100, 22151, 12553, 17410, 56066],
    ['700000', 27628, 18803, 27632, 14555, 20499, 58845],
    ['800000', 32652, 21560, 31797, 16561, 31093, 61622],
    ['900000', 36216, 24302, 35956, 18872, 37265, 64702],
    ['1000000', 40885, 27145, 40172, 27008, 42010, 67174],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_fill_back___Trivial_128_'));
    var options = {curveType: "function",title: "fill_back - Trivial<128>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_fill_back___Trivial_128_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_fill_back___Trivial_4096_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_reserve', 'vector_reserve', 'colony', 'deque', 'list', 'vector'],
    ['100000', 99291, 99165, 93745, 78083, 71280, 275150],
    ['200000', 189371, 195877, 191438, 161087, 151216, 555444],
    ['300000', 295924, 292906, 289069, 244678, 230150, 1015704],
    ['400000', 383938, 390134, 386876, 326803, 309189, 1114123],
    ['500000', 491567, 487224, 484430, 411020, 388064, 1211597],
    ['600000', 582054, 584361, 583918, 494356, 467153, 2029733],
    ['700000', 691463, 680920, 680137, 577437, 546012, 2128923],
    ['800000', 779796, 779300, 779129, 660831, 625425, 2225308],
    ['900000', 885591, 875187, 877538, 745724, 704473, 2323835],
    ['1000000', 980793, 974700, 976187, 829563, 783075, 2421730],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_fill_back___Trivial_4096_'));
    var options = {curveType: "function",title: "fill_back - Trivial<4096>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_fill_back___Trivial_4096_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_fill_back___NonTrivialStringMovable(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_reserve', 'vector_reserve', 'colony', 'deque', 'list', 'vector'],
    ['100000', 792, 389, 793, 1385, 1882, 1101],
    ['200000', 1593, 778, 1578, 1008, 3726, 2209],
    ['300000', 2376, 1169, 2378, 1514, 5552, 4153],
    ['400000', 3235, 1561, 3174, 2018, 7423, 4577],
    ['500000', 4001, 1957, 3983, 2541, 9248, 4981],
    ['600000', 4783, 2343, 5100, 3059, 11121, 9013],
    ['700000', 5594, 2735, 5589, 3575, 12977, 9101],
    ['800000', 6413, 3130, 6589, 4103, 15169, 9422],
    ['900000', 7218, 3520, 7258, 4654, 16637, 9815],
    ['1000000', 8007, 3920, 8027, 5175, 18506, 10240],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_fill_back___NonTrivialStringMovable'));
    var options = {curveType: "function",title: "fill_back - NonTrivialStringMovable",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_fill_back___NonTrivialStringMovable');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_fill_back___NonTrivialStringMovableNoExcept(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_reserve', 'vector_reserve', 'colony', 'deque', 'list', 'vector'],
    ['100000', 809, 388, 795, 1388, 1879, 798],
    ['200000', 1596, 781, 1601, 1021, 3733, 1633],
    ['300000', 2439, 1169, 2412, 1535, 5547, 3454],
    ['400000', 3185, 1556, 3248, 2065, 7394, 3612],
    ['500000', 4005, 1959, 4423, 2569, 9256, 4066],
    ['600000', 4784, 2342, 4825, 3109, 11113, 7052],
    ['700000', 5604, 2737, 5630, 3664, 13293, 7477],
    ['800000', 6373, 3133, 6434, 4176, 14889, 7910],
    ['900000', 7185, 3519, 7236, 4736, 16730, 8259],
    ['1000000', 7972, 3921, 8027, 5294, 18622, 8633],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_fill_back___NonTrivialStringMovableNoExcept'));
    var options = {curveType: "function",title: "fill_back - NonTrivialStringMovableNoExcept",animation: {duration:1200, easing:"in"},width: 600, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_fill_back___NonTrivialStringMovableNoExcept');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_emplace_back___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_reserve', 'vector_reserve', 'colony', 'deque', 'list', 'vector'],
    ['100000', 536, 178, 540, 971, 1670, 552],
    ['200000', 1070, 355, 1137, 456, 3213, 1115],
    ['300000', 1604, 533, 1610, 687, 4821, 1992],
    ['400000', 2137, 714, 2141, 912, 6341, 2272],
    ['500000', 2679, 890, 2723, 1147, 7819, 2595],
    ['600000', 3211, 1068, 3275, 1379, 9335, 4353],
    ['700000', 3750, 1246, 3757, 1602, 10897, 4636],
    ['800000', 4311, 1434, 4295, 1830, 12424, 4889],
    ['900000', 4837, 1790, 4843, 2068, 13971, 5280],
    ['1000000', 5374, 1809, 5379, 2287, 15502, 5512],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_emplace_back___Trivial_8_'));
    var options = {curveType: "function",title: "emplace_back - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_emplace_back___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_emplace_back___NonTrivialStringMovable(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_reserve', 'vector_reserve', 'colony', 'deque', 'list', 'vector'],
    ['100000', 4159, 3570, 4086, 10340, 4814, 5123],
    ['200000', 8186, 7225, 8178, 7738, 9608, 11510],
    ['300000', 12298, 10813, 12332, 11211, 14397, 21432],
    ['400000', 16407, 14445, 16422, 14961, 19540, 25450],
    ['500000', 20514, 18773, 20546, 18721, 24529, 28862],
    ['600000', 25404, 21683, 24669, 24438, 29634, 47174],
    ['700000', 28747, 25332, 28905, 26207, 34165, 49175],
    ['800000', 32832, 30607, 32917, 29952, 39691, 52576],
    ['900000', 36932, 32551, 38905, 33771, 43919, 57070],
    ['1000000', 41068, 36322, 41128, 37491, 48958, 69685],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_emplace_back___NonTrivialStringMovable'));
    var options = {curveType: "function",title: "emplace_back - NonTrivialStringMovable",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_emplace_back___NonTrivialStringMovable');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_sort___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_timsort', 'colony', 'deque', 'list', 'vector'],
    ['100000', 11, 8, 6, 15, 5],
    ['200000', 24, 17, 13, 39, 11],
    ['300000', 38, 28, 21, 81, 18],
    ['400000', 52, 38, 28, 129, 24],
    ['500000', 67, 50, 36, 175, 32],
    ['600000', 83, 62, 43, 252, 37],
    ['700000', 102, 78, 51, 302, 45],
    ['800000', 120, 95, 60, 367, 52],
    ['900000', 139, 114, 68, 418, 59],
    ['1000000', 159, 132, 76, 473, 67],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_sort___Trivial_8_'));
    var options = {curveType: "function",title: "sort - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"ms"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_sort___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_sort___Trivial_128_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_timsort', 'colony', 'deque', 'list', 'vector'],
    ['100000', 21, 20, 17, 21, 14],
    ['200000', 48, 48, 38, 58, 31],
    ['300000', 77, 80, 61, 108, 50],
    ['400000', 108, 112, 84, 150, 69],
    ['500000', 146, 153, 107, 183, 88],
    ['600000', 181, 197, 137, 262, 114],
    ['700000', 215, 231, 161, 306, 134],
    ['800000', 248, 269, 183, 364, 153],
    ['900000', 283, 309, 212, 398, 177],
    ['1000000', 320, 347, 235, 447, 195],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_sort___Trivial_128_'));
    var options = {curveType: "function",title: "sort - Trivial<128>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"ms"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_sort___Trivial_128_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_sort___Trivial_4096_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_timsort', 'colony', 'deque', 'list', 'vector'],
    ['100000', 172, 167, 380, 37, 368],
    ['200000', 345, 342, 812, 94, 792],
    ['300000', 519, 518, 1269, 172, 1237],
    ['400000', 697, 695, 1749, 238, 1696],
    ['500000', 879, 883, 2221, 297, 2175],
    ['600000', 1058, 1099, 2680, 419, 2616],
    ['700000', 1239, 1287, 3182, 489, 3128],
    ['800000', 1424, 1485, 3687, 569, 3610],
    ['900000', 1611, 1706, 4182, 637, 4089],
    ['1000000', 1795, 1834, 4712, 718, 4616],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_sort___Trivial_4096_'));
    var options = {curveType: "function",title: "sort - Trivial<4096>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"ms"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_sort___Trivial_4096_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_fill_front___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'deque', 'list', 'vector'],
    ['10000', 103, 164, 10808],
    ['20000', 45, 317, 47462],
    ['30000', 68, 468, 112331],
    ['40000', 91, 671, 208569],
    ['50000', 113, 864, 337059],
    ['60000', 138, 1056, 505252],
    ['70000', 161, 1067, 705592],
    ['80000', 184, 1370, 930767],
    ['90000', 207, 1374, 1192495],
    ['100000', 230, 1531, 1482277],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_fill_front___Trivial_8_'));
    var options = {curveType: "function",title: "fill_front - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_fill_front___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_linear_search___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['1000', 1225, 194, 594, 115],
    ['2000', 4837, 808, 2963, 417],
    ['3000', 10802, 1706, 7022, 923],
    ['4000', 19162, 3009, 12493, 1641],
    ['5000', 29970, 4812, 19916, 2681],
    ['6000', 42972, 6694, 28622, 4024],
    ['7000', 58487, 9108, 38993, 5602],
    ['8000', 76267, 11886, 51254, 7461],
    ['9000', 96459, 15073, 64857, 9535],
    ['10000', 118988, 18544, 80483, 11880],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_linear_search___Trivial_8_'));
    var options = {curveType: "function",title: "linear_search - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_linear_search___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_linear_search___Trivial_128_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['1000', 1290, 314, 1699, 316],
    ['2000', 5223, 1578, 7973, 1640],
    ['3000', 11864, 4445, 19918, 5136],
    ['4000', 21161, 9900, 37173, 10354],
    ['5000', 33118, 17371, 59245, 17496],
    ['6000', 47625, 26720, 85888, 26406],
    ['7000', 64734, 37500, 118073, 36900],
    ['8000', 84493, 50223, 153380, 49153],
    ['9000', 106670, 64512, 197568, 62828],
    ['10000', 131667, 80432, 244541, 78244],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_linear_search___Trivial_128_'));
    var options = {curveType: "function",title: "linear_search - Trivial<128>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_linear_search___Trivial_128_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_linear_search___Trivial_4096_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['1000', 1388, 486, 4104, 773],
    ['2000', 11302, 9886, 18768, 10841],
    ['3000', 28771, 27352, 50864, 29040],
    ['4000', 53444, 52677, 103377, 55008],
    ['5000', 86680, 86373, 211490, 88483],
    ['6000', 126078, 128374, 266281, 130910],
    ['7000', 174497, 178375, 374968, 182652],
    ['8000', 235061, 236228, 570192, 244255],
    ['9000', 288774, 300379, 642628, 313213],
    ['10000', 358575, 373799, 801212, 389737],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_linear_search___Trivial_4096_'));
    var options = {curveType: "function",title: "linear_search - Trivial<4096>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_linear_search___Trivial_4096_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_write___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 23, 5, 24, 2],
    ['20000', 47, 11, 49, 5],
    ['30000', 71, 16, 73, 8],
    ['40000', 94, 22, 99, 13],
    ['50000', 118, 27, 123, 14],
    ['60000', 146, 33, 148, 17],
    ['70000', 165, 39, 173, 23],
    ['80000', 189, 45, 197, 23],
    ['90000', 214, 78, 223, 27],
    ['100000', 236, 56, 247, 38],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_write___Trivial_8_'));
    var options = {curveType: "function",title: "write - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_write___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_write___Trivial_32_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 24, 12, 38, 11],
    ['20000', 49, 25, 76, 23],
    ['30000', 74, 37, 113, 35],
    ['40000', 99, 51, 153, 57],
    ['50000', 124, 64, 191, 65],
    ['60000', 148, 76, 234, 75],
    ['70000', 173, 89, 271, 92],
    ['80000', 198, 102, 320, 104],
    ['90000', 223, 119, 384, 125],
    ['100000', 252, 136, 432, 146],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_write___Trivial_32_'));
    var options = {curveType: "function",title: "write - Trivial<32>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_write___Trivial_32_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_write___Trivial_128_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 28, 34, 58, 26],
    ['20000', 57, 59, 122, 59],
    ['30000', 100, 93, 240, 123],
    ['40000', 199, 170, 536, 329],
    ['50000', 314, 250, 797, 414],
    ['60000', 450, 319, 858, 509],
    ['70000', 585, 533, 1076, 565],
    ['80000', 577, 567, 1294, 664],
    ['90000', 668, 615, 1539, 781],
    ['100000', 780, 893, 1666, 971],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_write___Trivial_128_'));
    var options = {curveType: "function",title: "write - Trivial<128>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_write___Trivial_128_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_write___Trivial_4096_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 173, 197, 788, 163],
    ['20000', 345, 401, 1579, 338],
    ['30000', 523, 615, 2355, 506],
    ['40000', 705, 828, 3091, 686],
    ['50000', 876, 1026, 3967, 851],
    ['60000', 1054, 1218, 4764, 1027],
    ['70000', 1234, 1432, 5446, 1174],
    ['80000', 1407, 1623, 6260, 1395],
    ['90000', 1573, 1830, 7028, 1487],
    ['100000', 1761, 2041, 7763, 1660],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_write___Trivial_4096_'));
    var options = {curveType: "function",title: "write - Trivial<4096>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_write___Trivial_4096_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_number_crunching___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'deque', 'list', 'vector'],
    ['10000', 14, 179, 10],
    ['20000', 58, 1021, 43],
    ['30000', 135, 2593, 102],
    ['40000', 251, 4897, 194],
    ['50000', 402, 7917, 330],
    ['60000', 606, 11595, 472],
    ['70000', 823, 15840, 655],
    ['80000', 1094, 21019, 873],
    ['90000', 1447, 26789, 1107],
    ['100000', 1763, 33428, 1379],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_number_crunching___Trivial_8_'));
    var options = {curveType: "function",title: "number_crunching - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"ms"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_number_crunching___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_number_crunching___Trivial_32_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'deque', 'list', 'vector'],
    ['10000', 37, 255, 36],
    ['20000', 170, 1262, 178],
    ['30000', 405, 2945, 425],
    ['40000', 736, 5383, 792],
    ['50000', 1159, 8546, 1236],
    ['60000', 1695, 12676, 1778],
    ['70000', 2319, 17857, 2417],
    ['80000', 3043, 23655, 3157],
    ['90000', 3872, 30960, 4013],
    ['100000', 4865, 40274, 5022],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_number_crunching___Trivial_32_'));
    var options = {curveType: "function",title: "number_crunching - Trivial<32>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"ms"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_number_crunching___Trivial_32_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_random_insert___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'deque', 'list', 'vector'],
    ['10000', 2, 15, 2],
    ['20000', 5, 25, 4],
    ['30000', 8, 36, 7],
    ['40000', 11, 43, 10],
    ['50000', 14, 54, 13],
    ['60000', 17, 59, 17],
    ['70000', 20, 70, 19],
    ['80000', 23, 76, 22],
    ['90000', 26, 86, 25],
    ['100000', 29, 93, 28],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_random_insert___Trivial_8_'));
    var options = {curveType: "function",title: "random_insert - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"ms"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_random_insert___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_random_insert___Trivial_32_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'deque', 'list', 'vector'],
    ['10000', 8, 30, 8],
    ['20000', 16, 49, 20],
    ['30000', 24, 66, 30],
    ['40000', 31, 84, 40],
    ['50000', 40, 101, 49],
    ['60000', 48, 117, 60],
    ['70000', 57, 136, 69],
    ['80000', 64, 159, 79],
    ['90000', 73, 173, 89],
    ['100000', 80, 192, 99],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_random_insert___Trivial_32_'));
    var options = {curveType: "function",title: "random_insert - Trivial<32>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"ms"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_random_insert___Trivial_32_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_random_insert___Trivial_128_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'deque', 'list', 'vector'],
    ['10000', 29, 28, 33],
    ['20000', 57, 52, 67],
    ['30000', 79, 77, 101],
    ['40000', 102, 108, 218],
    ['50000', 137, 161, 336],
    ['60000', 195, 212, 443],
    ['70000', 257, 291, 571],
    ['80000', 323, 384, 676],
    ['90000', 394, 497, 771],
    ['100000', 495, 583, 907],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_random_insert___Trivial_128_'));
    var options = {curveType: "function",title: "random_insert - Trivial<128>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"ms"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_random_insert___Trivial_128_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_random_insert___Trivial_1024_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'deque', 'list', 'vector'],
    ['10000', 180, 51, 559],
    ['20000', 587, 98, 1209],
    ['30000', 1026, 148, 1855],
    ['40000', 1391, 202, 2524],
    ['50000', 1787, 269, 3074],
    ['60000', 2197, 346, 3715],
    ['70000', 2632, 467, 4321],
    ['80000', 3033, 599, 5019],
    ['90000', 3421, 789, 5502],
    ['100000', 3858, 940, 6190],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_random_insert___Trivial_1024_'));
    var options = {curveType: "function",title: "random_insert - Trivial<1024>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"ms"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_random_insert___Trivial_1024_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_random_insert___NonTrivialStringMovable(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'deque', 'list', 'vector'],
    ['10000', 19, 22, 29],
    ['20000', 40, 36, 60],
    ['30000', 60, 47, 90],
    ['40000', 75, 58, 129],
    ['50000', 102, 70, 184],
    ['60000', 141, 77, 228],
    ['70000', 176, 89, 293],
    ['80000', 213, 99, 370],
    ['90000', 270, 114, 479],
    ['100000', 332, 124, 598],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_random_insert___NonTrivialStringMovable'));
    var options = {curveType: "function",title: "random_insert - NonTrivialStringMovable",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"ms"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_random_insert___NonTrivialStringMovable');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_random_insert___NonTrivialStringMovableNoExcept(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'deque', 'list', 'vector'],
    ['10000', 11, 21, 17],
    ['20000', 22, 35, 33],
    ['30000', 33, 47, 51],
    ['40000', 47, 57, 73],
    ['50000', 64, 69, 104],
    ['60000', 86, 78, 141],
    ['70000', 115, 89, 196],
    ['80000', 146, 98, 266],
    ['90000', 178, 111, 318],
    ['100000', 219, 121, 392],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_random_insert___NonTrivialStringMovableNoExcept'));
    var options = {curveType: "function",title: "random_insert - NonTrivialStringMovableNoExcept",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"ms"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_random_insert___NonTrivialStringMovableNoExcept');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_random_remove___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_rem', 'list_rem', 'deque_rem', 'colony', 'vector_rem', 'deque', 'list', 'vector'],
    ['10000', 34, 36, 17, 11733, 12, 2740, 17316, 2152],
    ['20000', 59, 61, 29, 23191, 18, 5527, 29399, 4826],
    ['30000', 83, 87, 40, 34410, 24, 8351, 39070, 7713],
    ['40000', 107, 113, 51, 46078, 30, 11295, 47834, 10470],
    ['50000', 132, 140, 61, 58636, 36, 14922, 57215, 14151],
    ['60000', 156, 167, 72, 69224, 41, 17191, 64306, 17141],
    ['70000', 180, 193, 83, 82030, 47, 20255, 73274, 20251],
    ['80000', 204, 219, 93, 92481, 53, 23242, 80413, 23431],
    ['90000', 227, 245, 103, 106583, 58, 26270, 90603, 25991],
    ['100000', 251, 272, 114, 117651, 64, 29039, 97792, 28923],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_random_remove___Trivial_8_'));
    var options = {curveType: "function",title: "random_remove - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_random_remove___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_random_remove___Trivial_32_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_rem', 'list_rem', 'deque_rem', 'colony', 'vector_rem', 'deque', 'list', 'vector'],
    ['10000', 38, 57, 26, 12316, 20, 7800, 27510, 7934],
    ['20000', 72, 112, 46, 24308, 44, 17292, 47475, 19668],
    ['30000', 94, 149, 64, 36290, 67, 24228, 64534, 29795],
    ['40000', 125, 203, 102, 48919, 89, 30826, 82993, 39752],
    ['50000', 151, 256, 101, 61504, 94, 38952, 101693, 49550],
    ['60000', 178, 308, 126, 72668, 135, 47984, 118332, 59610],
    ['70000', 207, 355, 169, 87787, 134, 58937, 136520, 69002],
    ['80000', 257, 420, 159, 97676, 166, 63107, 152509, 79116],
    ['90000', 265, 487, 203, 112046, 173, 71256, 176673, 88847],
    ['100000', 298, 581, 233, 124141, 222, 82505, 201676, 101310],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_random_remove___Trivial_32_'));
    var options = {curveType: "function",title: "random_remove - Trivial<32>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_random_remove___Trivial_32_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_random_remove___Trivial_128_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_rem', 'list_rem', 'deque_rem', 'colony', 'vector_rem', 'deque', 'list', 'vector'],
    ['10000', 101, 124, 109, 13005, 83, 23752, 26464, 31765],
    ['20000', 204, 207, 229, 25694, 154, 50027, 50994, 64593],
    ['30000', 293, 344, 313, 38088, 297, 77235, 75295, 99556],
    ['40000', 439, 536, 487, 51925, 522, 108448, 106607, 202596],
    ['50000', 649, 732, 650, 69192, 679, 145997, 151774, 314132],
    ['60000', 794, 913, 838, 89891, 800, 193581, 207641, 430058],
    ['70000', 960, 1131, 949, 115960, 972, 264964, 284104, 556339],
    ['80000', 1112, 1359, 1173, 145300, 1102, 301027, 372561, 646867],
    ['90000', 1283, 1630, 1434, 182342, 1266, 379881, 483162, 739455],
    ['100000', 1446, 1837, 1491, 235660, 1400, 458457, 563565, 888703],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_random_remove___Trivial_128_'));
    var options = {curveType: "function",title: "random_remove - Trivial<128>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_random_remove___Trivial_128_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_random_remove___Trivial_4096_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_rem', 'list_rem', 'deque_rem', 'colony', 'vector_rem', 'deque', 'list', 'vector'],
    ['10000', 4736, 4698, 4617, 39532, 4767, 1091578, 85723, 2115621],
    ['20000', 10085, 9576, 9848, 88240, 10355, 2517933, 176353, 4517966],
    ['30000', 15397, 14489, 14589, 128582, 15712, 3824783, 268166, 6948301],
    ['40000', 21104, 19699, 19654, 181041, 20595, 5098600, 375474, 9294166],
    ['50000', 26187, 24422, 24954, 236803, 25795, 6450727, 549223, 11537719],
    ['60000', 31189, 29491, 29697, 283510, 31277, 7958946, 819166, 14031055],
    ['70000', 36098, 34180, 34939, 348877, 36326, 9276083, 1133577, 16203758],
    ['80000', 41606, 39731, 39996, 395975, 41662, 10625249, 1632991, 18866131],
    ['90000', 47053, 44751, 45391, 465851, 46463, 11725416, 2105929, 20747906],
    ['100000', 52335, 49366, 50371, 504753, 51501, 13140273, 2263264, 23326904],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_random_remove___Trivial_4096_'));
    var options = {curveType: "function",title: "random_remove - Trivial<4096>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_random_remove___Trivial_4096_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_random_remove___NonTrivialStringMovable(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony_rem', 'list_rem', 'deque_rem', 'colony', 'vector_rem', 'deque', 'list', 'vector'],
    ['10000', 77, 82, 69, 12235, 61, 15935, 22466, 31062],
    ['20000', 153, 215, 139, 24210, 124, 35413, 37713, 64059],
    ['30000', 233, 269, 213, 36021, 195, 52850, 49766, 91089],
    ['40000', 337, 444, 303, 48252, 261, 78478, 60970, 126172],
    ['50000', 473, 611, 415, 61323, 384, 97581, 70700, 168568],
    ['60000', 716, 902, 646, 72462, 584, 128577, 80692, 225575],
    ['70000', 952, 1522, 990, 85763, 885, 164754, 92389, 283541],
    ['80000', 1195, 1929, 1153, 96792, 1262, 203742, 102470, 366730],
    ['90000', 1577, 2355, 1519, 111623, 1589, 269549, 118415, 454944],
    ['100000', 1955, 2724, 1904, 123026, 1976, 318905, 126505, 587929],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_random_remove___NonTrivialStringMovable'));
    var options = {curveType: "function",title: "random_remove - NonTrivialStringMovable",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_random_remove___NonTrivialStringMovable');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase1___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 217, 307, 211, 323],
    ['20000', 434, 864, 437, 900],
    ['30000', 650, 1565, 677, 1713],
    ['40000', 867, 2435, 892, 2899],
    ['50000', 1083, 3502, 1163, 4429],
    ['60000', 1299, 4796, 1376, 6630],
    ['70000', 1516, 6438, 1562, 8969],
    ['80000', 1732, 8209, 1818, 11570],
    ['90000', 1949, 10067, 2082, 14200],
    ['100000', 2166, 12084, 2184, 17514],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase1___Trivial_8_'));
    var options = {curveType: "function",title: "erase1 - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase1___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase1___Trivial_128_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 226, 2000, 269, 2718],
    ['20000', 454, 7836, 464, 10457],
    ['30000', 681, 16403, 738, 22992],
    ['40000', 911, 29577, 1018, 65448],
    ['50000', 1138, 45043, 1319, 127474],
    ['60000', 1366, 71735, 1691, 207840],
    ['70000', 1593, 84423, 2041, 294932],
    ['80000', 1822, 115501, 2416, 403334],
    ['90000', 2051, 134966, 2982, 505069],
    ['100000', 2280, 175910, 3146, 658349],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase1___Trivial_128_'));
    var options = {curveType: "function",title: "erase1 - Trivial<128>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase1___Trivial_128_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase1___Trivial_1024_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 231, 13437, 410, 52741],
    ['20000', 461, 65216, 1110, 227142],
    ['30000', 692, 171147, 1894, 513750],
    ['40000', 920, 367514, 2622, 918117],
    ['50000', 1149, 621053, 3155, 1408123],
    ['60000', 1375, 960229, 3993, 2049752],
    ['70000', 1722, 1403867, 4690, 2784979],
    ['80000', 1937, 1838891, 5388, 3668648],
    ['90000', 2058, 2317158, 5908, 4570680],
    ['100000', 2286, 2994859, 6793, 5661989],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase1___Trivial_1024_'));
    var options = {curveType: "function",title: "erase1 - Trivial<1024>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase1___Trivial_1024_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase1___Trivial_4096_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 238, 93726, 814, 232766],
    ['20000', 469, 483549, 1709, 930971],
    ['30000', 704, 1070792, 2597, 2067731],
    ['40000', 988, 1925628, 3487, 3663263],
    ['50000', 1155, 3021550, 4345, 5617430],
    ['60000', 1384, 4409863, 5235, 8246515],
    ['70000', 1613, 6158920, 6290, 11193952],
    ['80000', 1836, 7821032, 7167, 14671644],
    ['90000', 2068, 9628539, 8129, 18109340],
    ['100000', 2291, 12340880, 8816, 22662773],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase1___Trivial_4096_'));
    var options = {curveType: "function",title: "erase1 - Trivial<4096>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase1___Trivial_4096_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase1___NonTrivialStringMovable(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 229, 1563, 211, 2950],
    ['20000', 457, 6424, 426, 11674],
    ['30000', 687, 13865, 635, 28474],
    ['40000', 917, 25940, 850, 48898],
    ['50000', 1147, 41776, 1063, 80001],
    ['60000', 1395, 65660, 1283, 125958],
    ['70000', 1635, 92020, 1501, 182406],
    ['80000', 1875, 120655, 1723, 259933],
    ['90000', 2118, 152769, 1947, 353842],
    ['100000', 2362, 200519, 2166, 505506],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase1___NonTrivialStringMovable'));
    var options = {curveType: "function",title: "erase1 - NonTrivialStringMovable",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase1___NonTrivialStringMovable');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase1___NonTrivialStringMovableNoExcept(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 227, 1014, 218, 1652],
    ['20000', 455, 3864, 512, 6262],
    ['30000', 682, 8163, 709, 14219],
    ['40000', 911, 15328, 946, 26290],
    ['50000', 1140, 25620, 1184, 46128],
    ['60000', 1371, 42191, 1311, 76570],
    ['70000', 1633, 63024, 1498, 121352],
    ['80000', 1863, 83527, 1780, 181246],
    ['90000', 2101, 112944, 1943, 254902],
    ['100000', 2346, 152146, 2167, 359363],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase1___NonTrivialStringMovableNoExcept'));
    var options = {curveType: "function",title: "erase1 - NonTrivialStringMovableNoExcept",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase1___NonTrivialStringMovableNoExcept');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase1___NonTrivialArray_32_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 227, 641, 213, 734],
    ['20000', 453, 2278, 427, 2808],
    ['30000', 680, 4668, 641, 6076],
    ['40000', 906, 8135, 855, 10681],
    ['50000', 1133, 12194, 1070, 16465],
    ['60000', 1359, 17675, 1285, 23793],
    ['70000', 1586, 24189, 1502, 31455],
    ['80000', 1812, 30792, 1719, 41218],
    ['90000', 2039, 37693, 1937, 50610],
    ['100000', 2265, 47763, 2160, 62814],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase1___NonTrivialArray_32_'));
    var options = {curveType: "function",title: "erase1 - NonTrivialArray<32>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase1___NonTrivialArray_32_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase10___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 234, 1034, 232, 1325],
    ['20000', 469, 3895, 465, 5097],
    ['30000', 703, 8473, 697, 11513],
    ['40000', 938, 14949, 932, 21295],
    ['50000', 1171, 22967, 1164, 34842],
    ['60000', 1406, 33073, 1400, 52010],
    ['70000', 1641, 45191, 1630, 71826],
    ['80000', 1965, 59202, 1862, 95030],
    ['90000', 2109, 74614, 2094, 120732],
    ['100000', 2342, 92226, 2329, 150796],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase10___Trivial_8_'));
    var options = {curveType: "function",title: "erase10 - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase10___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase10___Trivial_4096_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 250, 842907, 892, 2265105],
    ['20000', 496, 4253837, 1855, 8972316],
    ['30000', 741, 10188000, 2769, 20223705],
    ['40000', 986, 18420087, 3680, 36476230],
    ['50000', 1237, 28764550, 4587, 56964347],
    ['60000', 1475, 41827774, 5514, 81944722],
    ['70000', 1716, 57539236, 6560, 111655781],
    ['80000', 1960, 75200470, 7385, 146359452],
    ['90000', 2202, 94893672, 8301, 184694348],
    ['100000', 2446, 117279729, 9230, 228155412],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase10___Trivial_4096_'));
    var options = {curveType: "function",title: "erase10 - Trivial<4096>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase10___Trivial_4096_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase10___NonTrivialStringMovable(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 246, 12464, 239, 26678],
    ['20000', 493, 54240, 478, 108310],
    ['30000', 741, 125975, 721, 250587],
    ['40000', 993, 237758, 968, 483063],
    ['50000', 1244, 390313, 1232, 799319],
    ['60000', 1512, 615755, 1529, 1228531],
    ['70000', 1889, 865535, 1847, 1814849],
    ['80000', 2214, 1146598, 2224, 2709679],
    ['90000', 2547, 1471200, 2542, 3776329],
    ['100000', 2888, 1870840, 2889, 5474739],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase10___NonTrivialStringMovable'));
    var options = {curveType: "function",title: "erase10 - NonTrivialStringMovable",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase10___NonTrivialStringMovable');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase10___NonTrivialStringMovableNoExcept(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 246, 8953, 239, 14138],
    ['20000', 493, 35992, 513, 56234],
    ['30000', 742, 81893, 766, 134451],
    ['40000', 994, 146192, 1073, 278179],
    ['50000', 1254, 240585, 1301, 472042],
    ['60000', 1509, 381865, 1787, 731139],
    ['70000', 1786, 569531, 2851, 1138603],
    ['80000', 2204, 794615, 2388, 1733860],
    ['90000', 2537, 1052822, 2715, 2540540],
    ['100000', 2884, 1365057, 3250, 3627882],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase10___NonTrivialStringMovableNoExcept'));
    var options = {curveType: "function",title: "erase10 - NonTrivialStringMovableNoExcept",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase10___NonTrivialStringMovableNoExcept');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase10___NonTrivialArray_32_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 243, 4125, 233, 5335],
    ['20000', 485, 16954, 464, 23706],
    ['30000', 727, 39254, 699, 53967],
    ['40000', 970, 71131, 933, 98904],
    ['50000', 1212, 110244, 1167, 154330],
    ['60000', 1457, 159124, 1412, 220907],
    ['70000', 1699, 218484, 1645, 300710],
    ['80000', 1939, 287840, 1895, 392446],
    ['90000', 2181, 362448, 2152, 496353],
    ['100000', 2424, 436889, 2441, 614048],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase10___NonTrivialArray_32_'));
    var options = {curveType: "function",title: "erase10 - NonTrivialArray<32>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase10___NonTrivialArray_32_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase50___Trivial_4096_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 326, 2522085, 1045, 11165905],
    ['20000', 645, 13926322, 2153, 45382602],
    ['30000', 961, 34753740, 3207, 102522428],
    ['40000', 1276, 64249487, 4240, 181987993],
    ['50000', 1591, 102425847, 5308, 284732115],
    ['60000', 1906, 148028986, 6820, 411329466],
    ['70000', 2223, 203385378, 7698, 559182433],
    ['80000', 2535, 263735752, 8617, 732632726],
    ['90000', 2852, 335588257, 9716, 923355801],
    ['100000', 3167, 412401861, 10564, 1143742819],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase50___Trivial_4096_'));
    var options = {curveType: "function",title: "erase50 - Trivial<4096>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase50___Trivial_4096_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_erase50___Trivial_8_(){
    var data = google.visualization.arrayToDataTable([
    ['x', 'colony', 'deque', 'list', 'vector'],
    ['10000', 315, 3152, 326, 5788],
    ['20000', 625, 12797, 661, 24435],
    ['30000', 938, 29971, 985, 55462],
    ['40000', 1254, 50793, 1350, 103208],
    ['50000', 1561, 78567, 1703, 171330],
    ['60000', 1875, 113351, 1983, 254353],
    ['70000', 2185, 155573, 2300, 356568],
    ['80000', 2500, 202748, 2632, 475635],
    ['90000', 2817, 258164, 2964, 606481],
    ['100000', 3121, 321172, 3291, 752925],
    ]);
    var graph = new google.visualization.LineChart(document.getElementById('graph_erase50___Trivial_8_'));
    var options = {curveType: "function",title: "erase50 - Trivial<8>",animation: {duration:1200, easing:"in"},width: 700, height: 400,hAxis: {title:"Number of elements", slantedText:true},vAxis: {viewWindow: {min:0}, title:"us"}};
    graph.draw(data, options);
    var button = document.getElementById('graph_button_erase50___Trivial_8_');
    button.onclick = function(){
    if(options.vAxis.logScale){
    button.value="Logarithmic Scale";
    } else {
    button.value="Normal scale";
    }
    options.vAxis.logScale=!options.vAxis.logScale;
    graph.draw(data, options);
    };
    }
    function draw_all(){
    draw_fill_back___Trivial_8_();
    draw_fill_back___Trivial_128_();
    draw_fill_back___Trivial_4096_();
    draw_fill_back___NonTrivialStringMovable();
    draw_fill_back___NonTrivialStringMovableNoExcept();
    draw_emplace_back___Trivial_8_();
    draw_emplace_back___NonTrivialStringMovable();
    draw_fill_front___Trivial_8_();
    draw_linear_search___Trivial_8_();
    draw_linear_search___Trivial_128_();
    draw_linear_search___Trivial_4096_();
    draw_write___Trivial_8_();
    draw_write___Trivial_32_();
    draw_write___Trivial_128_();
    draw_write___Trivial_4096_();
    draw_random_insert___Trivial_8_();
    draw_random_insert___Trivial_32_();
    draw_random_insert___Trivial_128_();
    draw_random_insert___Trivial_1024_();
    draw_random_insert___NonTrivialStringMovable();
    draw_random_insert___NonTrivialStringMovableNoExcept();
    draw_random_remove___Trivial_8_();
    draw_random_remove___Trivial_32_();
    draw_random_remove___Trivial_128_();
    draw_random_remove___Trivial_4096_();
    draw_random_remove___NonTrivialStringMovable();
    draw_erase1___Trivial_8_();
    draw_erase1___Trivial_128_();
    draw_erase1___Trivial_1024_();
    draw_erase1___Trivial_4096_();
    draw_erase10___Trivial_8_();
    draw_erase10___Trivial_4096_();
    draw_erase50___Trivial_8_();
    draw_erase50___Trivial_4096_();
    draw_number_crunching___Trivial_8_();
    draw_number_crunching___Trivial_32_();
    draw_sort___Trivial_8_();
    draw_sort___Trivial_128_();
    draw_sort___Trivial_4096_();
    draw_destruction___Trivial_8_();
    draw_destruction___Trivial_128_();
    draw_destruction___Trivial_4096_();
    draw_destruction___NonTrivialStringMovable();
    }
    google.setOnLoadCallback(draw_all);
    </script>
