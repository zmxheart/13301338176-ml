In this post, I'll describe a technique that can be used to configure a class at compile-time when there are multiple, optional parameters, with default values to this class. I used this technique in my dll project to configure each instance of Restricted Boltzmann Machine. 

The technique presented here will only work with C++11 because of the need for variadic template. This could be emulated without them by fixing a maximum number of parameters, but I won't go into this in this post. 

The problem
###########

For this post, we'll take the case of a single class, let's call it configurable. This class has several parameters: 

* A of type int
* B of type char
* C of an enum type
* D of type bool
* E is a type
* F is a template type

This class could simply be written as such: 

.. code:: c++

    enum class type {
        AAA,
        BBB,
        CCC
    };

    template<int T_A = 1, char T_B = 'b', type T_C = type::BBB, bool T_D = false, typename T_E = watcher_1, template<typename> class T_F = trainer_1>
    struct configurable_v1 {
        static constexpr const int A = T_A;
        static constexpr const char B = T_B;
        static constexpr const type C = T_C;
        static constexpr const bool D = T_D;

        using E = T_E;

        template<typename C>
        using F = T_F<C>;

        //Something useful
    };

and used simply as well:

.. code:: c++

    using configurable_v1_t = configurable_v1<100, 'z', type::CCC, true, watcher_2, trainer_2>;

This works well and nothing is wrong with this code. However, if you want all default values but the last one, you have to specify each and every one of the previous template parameters as well. The first disadvantage is that it is verbose and tedious. Secondly, instead of using directly the default values implicitly, you have specified them. This means that if the default values are changed by the library authors or even by you in the configurable_v1 class, either all the usages will be out of sync or you'll have to update them. And again, this is not practical. Moreover, if the author of the configurable_v1 template adds new template parameters before the last, you'll have to update all the instantiation points as well. 

Moreover, here we only have 6 parameters, if you have more, the problem becomes even worse. 

The solution
############

What can we do to improve over these problems ? We are going to use variadic template parameters in the configurable class and use simple classes for each possible parameters. This will be done in the configurable_v2 class. At the end you could use the class as such: 

.. code:: c++

    using configurable_v2_t1 = configurable_v2<a<100>, b<'z'>, c<type::CCC>, d, e<watcher_2>, f<trainer_2>>;
    using configurable_v2_t2 = configurable_v2<f<trainer_2>>;

You can note, that on the second line, we only specified the value for the last parameter without specifiyng any other value :) This is also much more flexible since the order of the parameters has absolutely no impact. Here, for the sake of the example, the parameters are badly named, so it is not very clear what this do, but in practice, you can give better names to the parameters and make the types more clear. Here is an example from my dll library: 

.. code:: c++

    using rbm_t = dll::rbm_desc<
        28 * 28, 200,
       dll::batch_size<25>,
       dll::momentum,
       dll::weight_decay<>,
       dll::visible<dll::unit_type::GAUSSIAN>,
       dll::shuffle,
       dll::weight_type<float>
    >::rbm_t;

rbm_desc is class that is configurable with this technique, expect that the first two parameters are mandatory and not named. I personally thinks that this is quite clear, but of course I may be biased ;)

So let's code!

The class declaration is quite simple: 

.. code:: c++

    template<typename... Args>
    struct configurable_v2 {
        //Coming
    }

We will now have to exact values and types from Args in order to get the 4 values, the type and the template type out of Args. 

Extracting integral values
##########################

We will start with the parameter *a* that holds a value of type int with a default value of 1. Here is one way of writing it: 

.. code:: c++

    struct a_id;

    template<int value>
    struct a : std::integral_constant<int, value> {
        using type_id = a_id;
    };

So, *a* is simply an integral constant with another typedef *type_id*. Why do we need this id ? Because a is a type template, we cannot use std::is_same to compare it with other types, since its value is part of the type. If we had only int values, we could easily write a traits that indicates if the type is a specialization of a, but since will have several types, this would be a real pain to do and we would need such a traits for each possible type. Here the simple way to go is to add inner identifiers to each types.

We can now write a struct to extract the int value for a from *Args*. *Args* is a list of types in the form parameter_name<parameter_value>... . We have to find a specialization of a inside this list. If such a specialization is present, we'll take its integral constant value as the value for a, otherwise, we'll take the default values. Here is what we want to do: 

.. code:: c++

    template<typename... Args>
    struct configurable_v2 {
        static constexpr const int A = get_value_int<a<1>, Args...>::value;

        //Coming
    }

We specify directly into the class the default values (1) for a and we use the class *get_value_int* to get its value from the variadic type list. Here is the implementation: 

.. code:: c++

    template<typename D, typename... Args>
    struct get_value_int;

    template<typename D>
    struct get_value_int<D> : std::integral_constant<int, D::value> {};

    template<typename D, typename T2, typename... Args>
    struct get_value_int<D, T2, Args...> {
        template<typename D2, typename T22, typename Enable = void>
        struct impl 
            : std::integral_constant<int, get_value_int<D, Args...>::value> {};

        template<typename D2, typename T22>
        struct impl <D2, T22, std::enable_if_t<std::is_same<typename D2::type_id, typename T22::type_id>::value>> 
            : std::integral_constant<int, T22::value> {};

        static constexpr const int value = impl<D, T2>::value;
    };

If you are not really familiar with Template Metaprogramming (TMP), this may seems very unfamiliar or even barbaric, but I'll try to explain into details what is going on here :)

*get_value_int* is a template that takes a type D, representing the parameter we want to extract and its default, and the list of args. It has a first partial specialization for the case when Args is empty. In which case, its value is simply the value inside D (the default value). The second partial specialization handles the case when there are at least one type (T2) inside the list of args. This separation in two partial specialization is the standard way to works with variadic template parameters. This specialization is more complicated than the first one since it uses an inner class to get the value out of the list. The inner class (impl) takes the parameter type (D2), the type that is present in the list (T22) and a special parameter (Enable) that is used for SFINAE. If you're not familiar with SFINAE (you're probably not reading this article...), it is, put simply, a mean to activate or deactivate a template class or function based on its template parameters. Here, the partial specialization of *impl* is enabled if *T22* and *D2* have the same *type_id*, in which case, the value of *T22* is taken as the return of *impl*. In the basic case, template recursion is used to continue iterating over the list of types. The fact that this has to be done into two template classes is because we cannot add a new template parameter to a partial template specialization even without a name. We cannot either add a simple *Enable* parameter to get_value_int, we cannot put before Args since then it would be necessary to give it a value in the code that uses it which is not practical neither a good practice.

We can now do the same for b that is of type char. Here is the parameter definition for b: 

.. code:: c++

    struct a_id;

    template<int value>
    struct a : std::integral_constant<int, value> {
        using type_id = a_id;
    };

This code is highly similar to the code for a, so we can generalize a bit this with a base class: 

.. code:: c++

    struct a_id;
    struct b_id;

    template<typename ID, typename T, T value>
    struct value_conf_t : std::integral_constant<T, value> {
        using type_id = ID;
    };

    template<int value>
    struct a : value_conf_t<a_id, int, value> {};

    template<char value>
    struct b : value_conf_t<b_id, char, value> {};

This make the next  parameters easier to describe and avoids small mistakes. 

Making *get_value_char* could be achieved by replacing each *int* by *char* but this would create a lot of duplicated code. So instead of writing *get_value_char*, we will replace *get_value_int* with a generic *get_value* that is able to extract any integral value type: 

.. code:: c++

    template<typename D, typename... Args>
    struct get_value;

    template<typename D, typename T2, typename... Args>
    struct get_value<D, T2, Args...> {
        template<typename D2, typename T22, typename Enable = void>
        struct impl 
            : std::integral_constant<decltype(D::value), get_value<D, Args...>::value> {};

        template<typename D2, typename T22>
        struct impl <D2, T22, std::enable_if_t<std::is_same<typename D2::type_id, typename T22::type_id>::value>> 
            : std::integral_constant<decltype(D::value), T22::value> {};

        static constexpr const auto value = impl<D, T2>::value;
    };

    template<typename D>
    struct get_value<D> : std::integral_constant<decltype(D::value), D::value> {};

This code is almost the same as get_value_int except that the return type is deduced from the value of the parameters. I used *decltype* and *auto* to automatically gets the correct types for the values. This is the only thing that changed.

With that we are ready the parameter c as well: 

.. code:: c++

    template<typename... Args>
    struct configurable_v2 {
        static constexpr const auto A = get_value<a<1>, Args...>::value;
        static constexpr const auto B = get_value<b<'b'>, Args...>::value;
        static constexpr const auto C = get_value<c<type::BBB>, Args...>::value;

        //Coming
    };

Extracting boolean flags
########################

The parameter d is a bit different since it is a boolean flag that puts directly the value to true. We could simply make a integral boolean value (and this would work), but here I needed a boolean flag for activating a feature deactivated by default. 

Defining the parameter is easy: 

.. code:: c++

    template<typename ID>
    struct basic_conf_t {
        using type_id = ID;
    };

    struct d_id;
    struct d : basic_conf_t<d_id> {};

It is similar to the other parameters, except that it has no value. You'll see later in this article why type_id is necessary here. 

To check if the flag is present, we'll write the is_present template: 

.. code:: c++

    template<typename T1, typename... Args>
    struct is_present;

    template<typename T1, typename T2, typename... Args>
    struct is_present<T1, T2, Args...> : std::integral_constant<bool, std::is_same<T1, T2>::value || is_present<T1, Args...>::value> {};

    template<typename T1>
    struct is_present<T1> : std::false_type {};

This time, the template is much easier. We simply need to iterate through all the types from the variadic template parameter and test if the type is present somewhere. Again, you can see that we used two partial template specialization to handle the different cases. 

With this we can now get the value for D: 

.. code:: c++

    template<typename... Args>
    struct configurable_v2 {
        static constexpr const auto A = get_value<a<1>, Args...>::value;
        static constexpr const auto B = get_value<b<'b'>, Args...>::value;
        static constexpr const auto C = get_value<c<type::BBB>, Args...>::value;
        static constexpr const auto D = is_present<d, Args...>::value;

        //Coming
    };

Extracting types
################

The next parameter does not hold a value, but a type. It won't be an integral constant, but it will define a typedef value with the configured type: 

.. code:: c++

    template<typename ID, typename T>
    struct type_conf_t {
        using type_id = ID;
        using value = T;
    };

    template<typename T>
    struct e : type_conf_t<e_id, T> {};

You may think that the extracting will be very different, but in fact it very similar. And here it is: 

.. code:: c++

    template<typename D, typename... Args>
    struct get_type;

    template<typename D, typename T2, typename... Args>
    struct get_type<D, T2, Args...> {
        template<typename D2, typename T22, typename Enable = void>
        struct impl {
            using value = typename get_type<D, Args...>::value;
        };

        template<typename D2, typename T22>
        struct impl <D2, T22, std::enable_if_t<std::is_same<typename D2::type_id, typename T22::type_id>::value>> {
            using value = typename T22::value;
        };

        using value = typename impl<D, T2>::value;
    };

    template<typename D>
    struct get_type<D> {
        using value = typename D::value;
    };

Every integral constant has been replaced with alias declaration (with *using*) and we need to use the *typename* disambiguator in from of X::value, but that's it :) We could probably have created an integral_type struct to simplify it a bit further, but I don't think that would change a lot. The code of the class follows the same changes: 

.. code:: c++

    template<typename... Args>
    struct configurable_v2 {
        static constexpr const auto A = get_value<a<1>, Args...>::value;
        static constexpr const auto B = get_value<b<'b'>, Args...>::value;
        static constexpr const auto C = get_value<c<type::BBB>, Args...>::value;
        static constexpr const auto D = is_present<d, Args...>::value;

        using E = typename get_type<e<watcher_1>, Args...>::value;

        //Coming
    };

Extracting template types
#########################

The last parameter is not a type but a template, so there are some slight changes necessary to extract them. First, let's take a look at the parameter definition: 

.. code:: c++

    template<typename ID, template<typename> class T>
    struct template_type_conf_t {
        using type_id = ID;
    
        template<typename C>
        using value = T<C>;
    };

    template<template<typename> class T>
    struct f : template_type_conf_t<f_id, T> {};

Here instead of taking a simple type, we take a type template with one template parameter. This design has a great limitations. It won't be possible to use it for template that takes more than one template parameter. You have to create an extract template for each possible combination that you want to handle. In my case, I only had the case of a template with one template parameter, but if you have several combination, you'll have to write more code. It is quite simple code, since the adaptations are minor, but it is still tedious. Here is the *get_template_type* template: 

.. code:: c++

    template<typename D, typename... Args>
    struct get_template_type;

    template<typename D, typename T2, typename... Args>
    struct get_template_type<D, T2, Args...> {
        template<typename D2, typename T22, typename Enable = void>
        struct impl {
            template<typename C>
            using value = typename get_template_type<D, Args...>::template value<C>;
        };

        template<typename D2, typename T22>
        struct impl <D2, T22, std::enable_if_t<std::is_same<typename D2::type_id, typename T22::type_id>::value>> {
            template<typename C>
            using value = typename T22::template value<C>;
        };

        template<typename C>
        using value = typename impl<D, T2>::template value<C>;
    };

    template<typename D>
    struct get_template_type<D> {
        template<typename C>
        using value = typename D::template value<C>;
    };

Again, there are only few changes. Every previous alias declaration is now a template alias declaration and we have to use *template* disambiguator in front of value. We now have the final piece to write the configurable_v2 class: 

.. code:: c++

    template<typename... Args>
    struct configurable_v2 {
        static constexpr const auto A = get_value<a<1>, Args...>::value;
        static constexpr const auto B = get_value<b<'b'>, Args...>::value;
        static constexpr const auto C = get_value<c<type::BBB>, Args...>::value;
        static constexpr const auto D = is_present<d, Args...>::value;

        using E = typename get_type<e<watcher_1>, Args...>::value;

        template<typename C>
        using F = typename get_template_type<f<trainer_1>, Args...>::template value<C>;
    };

Validating parameter rules
##########################

If you have more parameters and several classes that are configured in this manner, the user may use a wrong parameter in the list. In that case, nothing will happen, the parameter will simply be ignored. Sometimes, this behavior is acceptable, but sometimes it is better to make the code invalid. That's what we are going to do here by specifying a list of valid parameters and using static_assert to ensure this condition. 

Here is the assertion: 

.. code:: c++

    template<typename... Args>
    struct configurable_v2 {
        static constexpr const auto A = get_value<a<1>, Args...>::value;
        static constexpr const auto B = get_value<b<'b'>, Args...>::value;
        static constexpr const auto C = get_value<c<type::BBB>, Args...>::value;
        static constexpr const auto D = is_present<d, Args...>::value;

        using E = typename get_type<e<watcher_1>, Args...>::value;

        template<typename C>
        using F = typename get_template_type<f<trainer_1>, Args...>::template value<C>;

        static_assert(
            is_valid<tmp_list<a_id, b_id, c_id, d_id, e_id, f_id>, Args...>::value,
            "Invalid parameters type");

        //Something useful
    };

Since the is_valid traits needs two variadic list of parameters, we have to encapsulate list of valid types in another structure (*tmp_list*) to separate the two sets. Here is the implementation of the validation: 

.. code:: c++

    template<typename... Valid>
    struct tmp_list {
        template<typename T>
        struct contains : std::integral_constant<bool, is_present<typename T::type_id, Valid...>::value> {};
    };

    template<typename L, typename... Args>
    struct is_valid;

    template<typename L, typename T1, typename... Args>
    struct is_valid <L, T1, Args...> : std::integral_constant<bool, L::template contains<T1>::value && is_valid<L, Args...>::value> {};

    template<typename L>
    struct is_valid <L> : std::true_type {};

The struct tmp_list has a single inner class (*contains*) that test if a given type is present in the list. For this, we reuse the is_present template that we created when extracting boolean flag. The *is_valid* template simply test that each parameter is present in the *tmp_list*. 

Validation could also be made so that no parameters could be present twice, but I will put that aside for now. 

Conclusion
##########

Here it is :)

We now have a set of template that allow us to configure a class at compile-time with named, optional, template parameters, with default and in any order. I personally thinks that this is a great way to configure a class at compile-time and it is also another proof of the power of C++. If you think that the code is complicated, don't forget that this is only the library code, the client code on contrary is at least as clear as the original version and even has several advantages. 

I hope that this article interested you and that you learned something. 

The code of this article is available on Github: https://github.com/wichtounet/articles/blob/master/src/named_template_par/configurable.cpp 
It has been tested on Clang 3.5 and GCC 4.9.1. 
