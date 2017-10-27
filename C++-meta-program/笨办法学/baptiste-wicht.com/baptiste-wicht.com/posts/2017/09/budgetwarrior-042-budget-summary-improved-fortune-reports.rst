Almost three years ago, `I published the version 0.4.1 of budgetwarrior <https://baptiste-wicht.com/posts/2014/09/budgetwarrior-041-expense-templates-and-year-projection.html>`_. Since then, I've been using this tool almost every day to manage my personal budget. This is the only tool I use to keep track of my expenses and earnings and it makes a great tool for me. I recently felt that it was missing a few features and added them and polished a few things as well and release a new version with all the new stuff. This new version is probably nothing fancy, but a nice upgrade of the tool.

Don't pay too much attention to the values in the images since I've randomized
all the data for the purpose of this post (new feature, by the way :P).

New summary view
----------------

I've added a new report with :code:`budget summary`:

.. image:: /images/budgetwarrior_042_summary.png

This view gives concise information about the current state of your accounts. It
also gives information about your yearly and monthly objectives. Finally, it
also gives information about the last two fortune values that you've set.
I think this make a great kind of dashboard to view most of the information. If
your terminal is large enough, the three parts will be shown side by side.

Improved fortune report
-----------------------

I've made a few improvements to the :code:`budget fortune` view:

.. image:: /images/budgetwarrior_042_fortune.png

It now display the time between the different fortune values and it compute the
average savings (or avg losses) per day in each interval and in average from the
beginning of the first value.

Various changes
---------------

The balance does not propagate over the years anymore. This should mainly change
the behaviour of :code:`budget overview`. I don't think it was very
smart to propagate it all the time. The balance now starts at zero for each
year. If you want the old system, you can use the multi_year_balance=true option
in the .budgetrc configuration file.

The recurring expenses do not use an internal configuration value. This does not
change anything for the behaviour, but means that if you sync between different
machines, it will avoid a lot of possible conflicts :)

Fixed a few bugs with inconsistency between the different views and reports.
Another bug that was fixed is that :code:`budget report` was not always displaying the
first month of the year correctly, this is now fixed.

The graphs display in :code:`budget report` are now automatically adapted to width of
your terminal. Finally, the :code:`budget overview` command also displays more
information about the comparison with the previous month.

Installation
------------

If you are on Gentoo, you can install it using layman::

    layman -a wichtounet
    emerge -a budgetwarrior

If you are on Arch Linux, you can use this `AUR repository
<https://github.com/StreakyCobra/aur-budgetwarrior>`_.

For other systems, you'll have to install from sources::

    git clone --recursive git://github.com/wichtounet/budgetwarrior.git
    cd budgetwarrior
    make
    sudo make install

Conclusion
----------

A brief tutorial is available on Github: `Starting guide <https://github.com/wichtounet/budgetwarrior/wiki/Start-tutorial>`_.

If you are interested by the sources, you can download them on Github:
`budgetwarrior <https://github.com/wichtounet/budgetwarrior>`_.

If you have any suggestion for a new feature or an improvement to the tool or
you found a bug, please post an issue on Github, I'd be glad to help you. You
can post a comment directly on this post :)

If you have any other comment, don't hesitate to contact me, either by letting a
comment on this post or by email.

I hope that this application can be useful to some of you command-line adepts :)
