No, I'm not dead ;) I've been very busy with my Ph.D (and playing Path of Exile,
let's be honest...) and haven't had time to write something here in a long time.

Until now, there was too way to use my
`Deep Learning Library (DLL) <https://github.com/wichtounet/dll/>`_ project:

1. Write a C++ program that uses the library
2. Install DLL and write a configuration file to define your network and the problem to solve

The first version gives you all the features of the tool and allows you to build
exactly what you need. The second version is a bit more limited, but does not
require any C++ knowledge. However, it still does require a recent C++ compiler
and build system.

Due to the high C++ requirements that are not met by Visual Studio and the fact
that I don't work on Windows, this platform is not supported by the tool. Until
now!

I've added a third option to use DLL in the form of a Docker image to make the
second option even easier and allow the use of DLL on Windows. All you need is
Docker, which is available on Linux, Mac and Windows. This is still limited to
the second option in that you need to write a configuration describing the
network, but you need to build DLL and don't need to install all its
dependencies.

Usage
#####

To install the image, you can simply use `docker pull`:

.. code:: bash

    docker pull wichtounet/docker-dll

Then, to run it, you have to create a folder containing a `dll.conf` file and
mount in the container at `/dll/data/`. There are some examples in the
`image repository <https://github.com/wichtounet/docker-dll/>`_.  For instance,
on Linux from the cloned repository:

.. code:: bash

    docker run -v ${pwd}/rbm_mnist/:/dll/data/ wichtounet/docker-dll

or on Windows:

.. code:: bash

    docker run -v /c/Users/Baptiste/rbm_mnist/:/dll/data wichtounet/docker-dll

This will automatically run the actions specified in the configuration file and
train your network.

Conclusion
##########

I would really have thought this would be harder, but it turned out that Docker
is a very good solution to deploy multiplatform demo tools :)

As of now, there is only support for mnist data format in the tool in this
form, but I plan to add basic CSV support as well in the near future.

I hope that this will help people willing to try the library with a simpler
usage.
