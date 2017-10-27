Back at school, I worked on the COJAC project to detect numeric overflow in
Java programs automatically. Since then, this project has evolved a lot and has
now more features:

 * It can detect integer overflows
 * Detect smearing and cancellation with float and double types
 * Detect NaN and Infinite results from computations
 * Detect offending type casting

Moreover, all these features are available without any recompilation of your
program. You simply add an argument to the invocation of the Java virtual
machine and all these errors will be detected for you automatically!

Frédéric Bapst, the person in charge of the project has recently published two
videos about the project, don't hesitate to check them out:

The first video presents the automatic analysis features of the tool:

.. youtube:: DqAFQfbWZOU

And the second presents the numeric wrapper features of the tool for even more
features:

.. youtube:: 4x9mJEFjcGc

If you have any question related to the project, you can add a comment to this
page or contact me directly be email.

If you want more information on the project you can also check out its
repository on Github: https://github.com/Cojac/Cojac
