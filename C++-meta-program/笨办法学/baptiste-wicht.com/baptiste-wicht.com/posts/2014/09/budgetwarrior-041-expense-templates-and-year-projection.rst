I've been able to finish the version 0.4.1 of budgetwarrior before I though :)

Expense templates
-----------------

The "most useful" new feature of this release is the ability to create template
for expenses. 

For that, you can give an extra parameter to budget expense add::

    budget expense add template name

This will works exactly the same as creating a new expense expect that it will
be saved as a template. Then, the next time you do::

    budget expense add template name

A new expense will be created with the date of the day and with the name and
amount saved into the template. You can create as many templates as you want as
long as they have different names. You can see all the templates you have by
using 'budget expense template'. A template can be deleted the exact same as an
expense with 'budget expense delete id'.

I think this is very useful for expense that are made several times a month, for
instance a coffee at your workplace. The price should not change a lot and it is
faster to just use the template name rather than entering all the information
again. 

Year prediction
---------------

You can now see what would be next year if you changed a bit your expenses. For
instance, how much would you still have at the end of the year if you increased
your house expenses by 20% and reduced your insurances by 5% ? 

The 'budget predict' can be used for that purpose. You can enter a multiplier
for each account in your budget and a new year will be "predicted" based on
the expenses of the current year multiplied by the specified multiplier:

.. image:: /images/budget_041_prediction.png

I think that this feature can be very useful if you want to estimate how your
budget will be for moving to a more expensive house or another insurance for
instance. 

Various changes
---------------

Two accounts can be merged together with the 'budget account migrate' command.
This command will move all expenses from an account to another and adapt the
amount of the target account. The source account will be deleted. This supports
migrated accounts. 

The 'budget wish list' command will now display the mean accuracy of your
predictions. 

You don't need Boost anymore for this project. The only remaining dependency is
libuuid. I will perhaps remove it in the next version since the UUID are not
used in the application for now.

The command 'budget gc' will clean the IDs of all your data in order to fill the
holes and make all the IDs contiguous. It is mostly a feature for order-freaks
like me who do not like to have holes in a sequence of identifiers ;)

There was a bug in the monthly report causing the scale to be displayed
completely moved, it is now fixed: 

.. image:: https://raw.githubusercontent.com/wichtounet/budgetwarrior/develop/screenshots/budget_report.png

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

If you have a suggestion for a new features or you found a bug, please post an
issue on Github, I'd be glad to help you.

If you have any comment, don't hesitate to contact me, either by letting a
comment on this post or by email.
