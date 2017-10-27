This last month, I've been reading quite a few blogs about personal finance and
I've decided to integrate more features into budgetwarrior. This post is about
three new features that I've integrated. It's not yet a new release, so if you
want to test this version, you'll have to compile it from the *master* branch on
Git.

As it was last time, the values on my screenshots have all been randomized.

If you have several assets with different distributions, I believe it is a great
value to have them all shown at the same time. Especially if you want to change
the distribution of your portfolio or if you plan big changes in it.

Track assets
++++++++++++

The first feature I've added is a feature to precisely track each of your assets
independently. And you can also track the allocation of your portfolio in terms
of stocks, bonds and cash. The tool also lets you set the desired distribution
of your assets and will compute the difference that you should make in order to
comply to your desired distribution.

First, you need to define all your asset classes (your accounts, funds, and
stocks, ...) and their distribution with :code:`budget asset add`. It also
supports to set a currency. The default currency is now CHF, but you can set it
in the configuration file, for instance :code:`default_currency=USD`. You can
see your assets using :code:`budget asset`:

.. image:: /images/budgetwarrior_assets.png
   :alt: View of your assets

You can then set the value of your assets using :code:`budget asset value add`.
The system will save all the values of your assets. For now, only the last value
is used in the application to display. In the future, I plan to add new reports
for evolution of the portfolio over time. You can see your current net worth
with the :code:`budget asset value`:

.. image:: /images/budgetwarrior_asset_values.png
   :alt: View of your portfolio

The different currencies will all be converted to the default currency.

Savings rate
++++++++++++

The second change I did is to compute the savings rate of each month and year.
The savings rate is simply the portion of your income that you are able to save
each month. The savings rate for a year is simple the average of the savings
rate of each month.

The savings rate of the month can be seen with :code:`budget overview month`:

.. image:: /images/budgetwarrior_savings_rate.png
   :alt: Savings rate of the month

The saving rates of each month can also be seen in the overview of the year with
:code:`budget overview year`:

.. image:: /images/budgetwarrior_savings_rate_year.png
   :alt: Savings rate of the year

This shows the savings rate of each month, the average of the year and the
average of the current year up to the current month.

The savings rate is a very important metric of your budget. In my case, it's
currently way too low and made me realize I really need to save more. Any
savings rate below 10% is too low. There are no rule as too much it should be,
but I'd like to augment mine to at least 20% next year.

Auto-completion
+++++++++++++++

The last feature is mostly some quality-of-life improvement. Some of the inputs
in the console can now be completed. It's not really auto-completion per se, but
you can cycle through the list of possible values using the UP and DOWN.

This makes it much easier to set some values such as asset names (in
:code:`budget asset value add` for instance), account names and objective types
and sources. I'm trying to make the input of values easier.

Conclusion
++++++++++

I don't know exactly what else will be integrated in this feature, but I may
already improve some visualization for asset values. If I learn something new
about personal finance that I may integrate in the tool, I'll do it as well.

If you are interested by the sources or want to install this version,
you can download them on Github:
`budgetwarrior <https://github.com/wichtounet/budgetwarrior>`_.

The new features are in the *master* branch.

If you have a suggestion for a new features or you found a bug, please post an
issue on Github, I'd be glad to help you.

If you have any comment, don't hesitate to contact me, either by letting a
comment on this post or by email.
