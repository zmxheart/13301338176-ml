In the last month, I've developped a very simple tool in Python: *pm*. This tool
allows to check the status of all the Git repositories inside a repository. I've
just released the first version of this tool: pm-0.1.1

Those who are following this blog will perhaps wonder why Python and not C++ :)
The reason is quite simple, I wanted to improve my skills in Python. And what is
better than to develop a project from scratch.

Features
--------

The main feature of this application is to show the status of every projects in
a directory. The status of your projects can be queried by using :code:`pm
status`. On my computer this gives something like that:

.. image:: /images/pm_status.png

The state of each branch of each project is shown. There different possible
status (they are cumulative): 
* Behind remote: Commits are available on the remote repository
* Ahead of remote: Some local commits are no pushed
* Diverged: Behind and Ahead
* Uncomitted changes: Some changes are not committed
* Clean: Indicates that everything is committed, pushed and pull.

By default, the directory is ~/dev/ but you can change it by passing the
repository to the command, if you pass a relative directory, it will be relative
to home. For instance, here is the status of my doc repositories:

.. image:: /images/pm_status_2.png

Another feature that can be useful is that it is able to check the status of
submodules with the -s option:

.. image:: /images/pm_status_sm.png

As you can see it supports recursive submodules. For each submodule it will
indicate if there are new commits available or not. 

pm is not only able to show status of the projects, it can also fetch the status 
of branches from remote by using :code:`pm fetch`. All the remote branches are
fetched from remote. It can also automatically update the projects that are
behind remote (equivalent of git pull) with :code:`pm update`. Only projects
that can be fast-forwarded are updated. 

Installation
------------

Thanks to *pip*, installation of pm is quite simple::

    pip install pm

If you don't want to use pip, you can install it by hand::

    wget https://github.com/wichtounet/pm/archive/0.1.1.tar.gz
    tar xf 0.1.1.tar.gz
    cd 0.1.1
    python setup.py install

For those interested, source code is available `on Github
<https://github.com/wichtounet/pm>`_. 

If you have any suggestion for the tool or on the source code, post a comment to
this post ;)
