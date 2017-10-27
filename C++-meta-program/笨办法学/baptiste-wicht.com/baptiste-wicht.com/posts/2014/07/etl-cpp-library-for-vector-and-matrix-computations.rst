When working on Machine Learning algorithms, I was in need of a simple library
to ease working with vectors and matrix. This is the reason why I started
developing ETL (Expression Template Library). 

ETL is a small header only library for C++ that provides vector and matrix
classes with support for Expression Templates to perform very efficient
operations on them.

The library supports statically sized and dynamically sized vector and matrix
structures with efficient element-wise operations. All the operations are
implemented lazily with Expression Templates, they are only implemented once the
expression is assigned to a concrete structure. 

Data structures
***************

Several structures are available: 

* :code:`fast_vector<T, Rows>`: A vector of size Rows with elements of type T. This must
  be used when you know the size of the vector at compile-time.
* :code:`dyn_vector<T>`: A vector with element of type T. The size of the vector can be
  set at runtime.
* :code:`fast_matrix<T, Rows,Columns>`: A matrix of size Rows x Columns with elements of
  type T. This must be used when you know the size of the matrix at
  compile-time.
* :code:`dyn_matrix<T>`: A matrix with element of type T. The size of the matrix can be
  set at runtime.

All the structures are size-invariant, once set they cannot be grown or
shrinked. 

In every operations that involves fast version of the structures, all the sizes
are known at compile-time, this gives the compiler a lot of opportunities for
optimization. 

Element-wise operations
***********************

Classic element-wise operations can be done on vector and matrix as if it was
done on scalars. Matrices and vectors can also be added, subtracted, divided,
...  by scalars. 

Here is an example of what can be done: 

.. code:: cpp

   etl::dyn_vector<double> a({1.0,2.0,3.0});
   etl::dyn_vector<double> b({3.0,2.0,1.0});

   etl::dyn_vector<double> c(1.4 * (a + b) / b + b + a / 1.2);

All the operations are only executed once the expression is evaluated to
construct the dyn_vector. No temporaries are involved. This is as efficient as
if a single for loop was used and each element was computed directly. 

You can easily assign the same value to a structure by using the operator = on
it. 

Unary operators
***************

Several unary operators are available. Each operation is performed on every
element of the vector or the matrix. 

Available operators:

* :code:`log`
* :code:`abs`
* :code:`sign`
* :code:`max/min`
* :code:`sigmoid`
* :code:`noise`: Add standard normal noise to each element
* :code:`logistic_noise`: Add normal noise of mean zero and variance sigmoid(x) to each
  element
* :code:`exp`
* :code:`softplus`
* :code:`bernoulli`

Several transformations are also available:

* :code:`hflip`: Flip the vector or the matrix horizontally
* :code:`vflip`: Flip the vector or the matrix vertically
* :code:`fflip`: Flip the vector or the matrix horizontally and vertically. It is the
  equivalent of :code:`hflip(vflip(x))`
* :code:`dim/row/col`: Return a vector representing a sub part of a matrix (a row or a
  col)
* :code:`reshape`: Interpret a vector as a matrix

Again, all these operations are performed lazily, they are only executed when the
expression is assigned to something. 

Lazy evaluation
***************

All binary and unary operations are applied lazily, only when they are assigned
to a concrete vector or matrix class. 

The expression can be evaluated using the :code:`s(x)` function that returns a
concrete class (fast_vector,fast_matrix,dyn_vector,dyn_matrix) based on the
expression. 

Reduction
*********

Several reduction functions are available:

* sum: Return the sum of a vector or matrix
* mean: Return the sum of a vector or matrix
* dot: Return the dot product of two vector or matrices

Functions
*********

The header *convolution.hpp* provides several convolution operations both in 1D
(vector) and 2D (matrix). All the convolution are available in valid, full and
same versions. 

The header *mutiplication.hpp* provides the matrix multiplication operation
(:code:`mmult`). For now on, only the naive algorithm is available. I'll
probably add support for Strassen algorithm in the near future. 

It is possible to pass an expression rather than an data structure to functions.
You have to keep in mind that expression are lazy, therefore if you pass a + b
to a matrix multiplication, an addition will be run each time an element is
accessed (n^3 times), therefore, it is rarely efficient. 

Examples
********

Here are some examples of these operators (taken from my Machine Learning
Library):

.. code:: cpp

        h_a = sigmoid(b + mmul(reshape<1, num_visible>(v_a), w, t));
        h_s = bernoulli(h_a);

.. code:: cpp
        
        h_a = min(max(b + mmul(reshape<1, num_visible>(v_a), w, t), 0.0), 6.0);
        h_s = ranged_noise(h_a, 6.0);
        
.. code:: cpp

        weight exp_sum = sum(exp(b + mmul(reshape<1, num_visible>(v_a), w, t)));

        h_a = exp(b + mmul(reshape<1, num_visible>(v_a), w, t)) / exp_sum;

        auto max = std::max_element(h_a.begin(), h_a.end());

        h_s = 0.0;
        h_s(std::distance(h_a.begin(), max)) = 1.0;

Conclusion
**********

This library is available on Github: `etl <https://github.com/wichtounet/etl>`_. 
It is licensed under MIT license. 

It is header-only, therefore you don't have to build it. However, it uses some
recent C++14 stuff, you'll need a recent version of Clang or G++ to be able to
use it. 

If you find an issue or have an idea to improve it, just post it on Github or
as a comment here and I'll do my best to work on that. If you have any question
on the usage of the library, I'd be glad to answer them. 
