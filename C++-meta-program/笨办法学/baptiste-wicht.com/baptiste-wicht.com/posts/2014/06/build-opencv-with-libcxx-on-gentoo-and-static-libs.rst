

When you build C++ projects with CLang, you have the choice between using the
stdlibc++ that is provided along G++ and the new libc++ that is provided by
CLang. 

libc++ is another implementation of the C++ Standard Library. This
implementation is dual-licensed under the MIT license and UIUC license. It is
especially targeting C++11 and has already 100% support for C++14. This
last point is the reason that I use libc++ on several of my projects. Moreover,
it is also the default on Mac OS X. 

The problem with linking with another library is that you can only works with
libraries that have been compiled with libc++ support. For instance, if you want
to use Boost dynamic libraries, you'll have to compile Boost from sources with
libc++. 

For one of my project, I'm using OpenCV and libc++. To simplify the installation
of OpenCV, I created a new ebuild with a *libcxx* use flag to selectively build the
library with libc++. This requires LLVM/CLang on the build machine. Moreover, by
default, the Gentoo ebuild does not have support for building the static
libraries. The reason for that is that OpenCV build is not able to build dynamic
and static libraries. I added a *static-libs* use flag that build the static
libraries by building OpenCV a second time after the first. That will likely
double the compile time (unless ccache is used). Anyhow, it is simple easier
than to build that by hand on several machine. 

The ebuild is available on `my overlay 
<https://github.com/wichtounet/wichtounet-overlay>`_. You can add the overlay to
your machine by modifying */etc/layman/layman.cfg*:: 

 overlays: http://www.gentoo.org/proj/en/overlays/repositories.xml
           http://github.com/wichtounet/wichtounet-overlay/raw/master/repository.xml

Then, you can add it to layman::
 
 layman -S
 layman -a wichtounet

For now, I have created an ebuild for *opencv-2.4.8-r1*. If someone is
interested in other versions, I'd be glad to create new ebuilds. 

I hope that this ebuild will be helpful. 
