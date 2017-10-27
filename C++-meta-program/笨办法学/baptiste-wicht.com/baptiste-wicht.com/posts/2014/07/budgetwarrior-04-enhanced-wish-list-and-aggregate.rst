I've just released a new version of my command-line budget manager:
budgetwarrior 0.4. 

Enhanced aggregate overview
---------------------------

The aggregate overviews have been greatly improved. First, there is now a
*budget overview month* command that groups all expenses of amonth together.
Here is a possible output:

.. image:: /images/budget_04_aggregate_month.png

It also possible to use *--full* option to also aggregate together the different
accounts: 

.. image:: /images/budget_04_aggregate_month_full.png

Another new option is *--no-group* that disables the grouping by categories: 

.. image:: /images/budget_04_aggregate_month_full_ng.png

Moreover, the separator of categories can now be configured with *--separator=*. 

All these options can also be set in the configuration with these options: 

* aggregate_full : If set to true, does the same as the --full option. 
* aggregate_no_group : If set to true, does the same as the --no-group option. 
* aggregate_separator : Sets the separator for grouping. 

Enhanced wish list
------------------

The wishes management has also been improved. 

First, each wish can now be set an Urgency and Importance level. This is now
shown in wish status as simple indicators:

.. image:: /images/budget_04_wish_status.png

Moreover, the accuracy of the estimation compared to the paid amount is shown in
*wish list*:

.. image:: /images/budget_04_wish_list.png

Various changes
---------------

Objective status now shows more information about the status of the objectives: 

.. image:: /images/budget_04_objective_status.png

The versioning module has been improved. The *versioning sync* does now perform
a commmit as well as pull/push. *versioning push*, *versioning pull* and
*versioning status* commands have been added. 

*budget version* command shows the version of budgetwarrior. 

Aliases a now available to make shorted commands: 

* budget sync -> budget versioning sync
* budget aggregate -> budget overview aggregate

Installation
------------

If you are on Gentoo, you can install it using layman::

    layman -a wichtounet
    emerge -a budgetwarrior

If you are on Arch Linux, you can use this `AUR repository
<https://github.com/StreakyCobra/aur>`_.

For other systems, you'll have to install from sources::

    git clone git://github.com/wichtounet/budgetwarrior.git
    cd budgetwarrior
    make
    sudo make install

Conclusion
----------

If you are interested by the sources, you can download them on Github:
`budgetwarrior <https://github.com/wichtounet/budgetwarrior>`_.

If you have a suggestion or you found a bug, please post an issue on Github.

If you have any comment, don't hesitate to contact me, either by letting a
comment on this post or by email.
