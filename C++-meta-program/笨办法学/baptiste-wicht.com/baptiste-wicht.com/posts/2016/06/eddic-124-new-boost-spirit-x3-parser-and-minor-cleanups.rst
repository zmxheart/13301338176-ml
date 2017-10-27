After almost 2 years, the new version of eddic (the compiler of the EDDI
programming language) is out! eddic 1.2.4

I haven't worked a lot on this project in the last years, I have been busy with
my Ph.D. related projects (ETL and DLL), my operating systems, cpm, ... I've
mostly worked on the parser to test the new version of Boost Spirit: X3. This
will be described on the next section, with the other changes in the later
section.

New Boost Spirit X3
###################

Boost Spirit X3 is a completely revamped version of Boost Spirit X3. It's aimed
at performance, both at compile-time and at runtime and uses recent features of
modern C++. It's not compatible with Boost Spirit Qi, so you'll most likely
have to rewrite a lot of stuff, in the parser, in the Abstract Syntax Tree
(AST) and in the AST passes as well.

For reference, I'm using the Boost 1.59 version.

Pros
****

Let's start with the pros.

First, the runtime performance is definitely better. Parsing all my eddi test
cases and samples, *takes 42% less time than with the previous parser*. It is
important to know that the old parser was very optimized, with moves instead of
copies and with a static lexer. You can take a look at `this post
<http://baptiste-wicht.com/posts/2013/06/improving-eddic-boost-spirit-parser-performances.html>`_
to see what was necessary to optimize the old Qi. I think it's a good result
since the new grammar does not use a lexer (x3 does not support it) and does
not need these optimizations. This improvement really was my objective. I'll
try to push it farther in the future.

Compile-time performance is also much better. It takes 3 times less time to
compile the new parser (1 minute to around 20 seconds). Moreover, the new
parser is now in only one file, rather than being it necessary to split it all
over the place for compile-time performance. Even though it's not really
important for me, it's still good to have :)

Especially due to the performance point, I've been able to remove some code,
the lexer, the generated static lexer and the special pointers optimizations of
the AST.

Cons
****

Unfortunately, there are some disadvantages of using the new Spirit X3.

First, the AST needs to be changed. For good parsing performance, you need to
use x3::variant and x3::forward_ast. This is a major pain in the ass since
x3::variant is much less practical to use than boost::variant. Almost
everything is explicit, meaning uglier code than before, in my opinion.
Moreover, you need to work around x3::forward_ast for boost::get, whereas
boost::recursive_wrapper was working better in that matter. I've had to create
my own wrapper around boost::get in order to be able to use the new tree. In my
opinion, this is clearly a regression.

Secondly, although X3 was also meant to remove the need to use some hacks in
the grammar, I ended up having more hacks than before. For instance, many AST
node have a fake field in order to make X3 happy. I've still had to use the
horrible eps hack at one place. I've had to create a few more rules in order to
fix type deduction that is working differently than before (worse for me). And
for some reasons, I had to replace some expectations from the grammar to make it
parse correctly. This is a really important regression in my opinion, since it
may make the parsing slower and will make the error message less nice.

The previous error handling system allowed me to track the file from which an
AST node was parsed from. Although the new error handler is a lot nicer than
the old system, it does not have this feature, so I had to work around this by
using new annotation nodes and a new global handler. Overall, it's probably a
bit worse than before, but makes for lighter AST nodes.

Finally, for some reason, I haven't been able to use the debug option of the
library (lots of compile time errors). That complicated a bit the debugging of
the parser.

Spirit X3 or Spirit Qi ?
************************

Overall, I have to say I'm a bit disappointed by Spirit X3. Even though it's
faster at runtime and faster to compile, I was really expecting less issues
with it. What I really did not like was all the changes I had to make because
of x3::variant and x3::forward_ast. Overall, I really don't think it was worth
the trouble porting my parser to Spirit X3.

If you have a new project, I would still consider using Boost Spirit X3.

If you have an existing parser, I would probably not advice porting it to X3.
Unless you really have issues with parsing performances (and especially if you
have not already optimized QI parser), it's probably not worth the trouble and
all the time necessary for all the changes.


Other changes
#############

The other changes are much more minor. First of all, I've gotten rid of CMake.
This project has really made me hate CMake. I have actually gotten rid of it on
all my projects. I'm now using plain Makefiles and having a much better time
with them. I've also replaced boost Program Options with cxxopts. It's a much
more modern approach for program options parsing. Moreover, it's much more
lightweight and it's header only. Only advantages. There also have been lots of
changes to code (still not very good quality though).

Future
######

eddic was my first real project in C++ and this can be seen in the code and the
organization. The quality of the code is really bad now that I read it again.
Some things are actually terrible :P It's probably normal since I was a
beginner in C++ at the time.

For the future version of the compiler, I want to clean the code a lot more and
focus on the EDDI language adding new features. Moreover, I'll also get rid of
Boost Test Framework by using Catch (or doctest if it is ready).

As for now, I'm not sure on which project I'm going to focus. Either I'll
continue working on the compiler or I'll start working again on my operating
system (thor-os) in which I was working on process concurrency (without too
much success :P). I'll probably post next updates on this post in the coming
months.

Download
########

You can find the EDDI Compiler sources on the `Github repository <https://github.com/wichtounet/eddic>`_

The version is available in the *v1.2.4* tag available in the GitHub
repository, in the releases pages or directly in the <em>master</em> branch.
