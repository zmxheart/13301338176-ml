Recently, I wanted to know how many lines of code I had on my Sonar server with all my C++ projects. Sonarsource proposes a commercial plugins (Views) that allows to do that (and much more...), but I didn't wanted to pay thousands of dollars simply to get a total of my lines of code, therefore I wrote a very  simple Sonar plugin to compute some global metrics.

This plugin is very simple, it only provides a global widgets that aggregates some stats over all your projects. For instance, here is the results on my Sonar server:

.. image:: /images/aggregator_widget.png
   :align: center



The plugin is freely available on Github: https://github.com/wichtounet/aggregator-plugin . However, it has only be tested on my Sonar server (4.5.2) and it is my first Sonar plugin, so it may not work everywhere. If you experience issues, don't hesitate to open an issue on Github or to propose a Pull Request.

You can install the plugin by putting the .jar file (from the Github Releases page) into your sonar/extensions/plugins directory and restart Sonar. You should then have access to a new global widget that you can add to a dashboard.

I hope this plugin helps some of you.
