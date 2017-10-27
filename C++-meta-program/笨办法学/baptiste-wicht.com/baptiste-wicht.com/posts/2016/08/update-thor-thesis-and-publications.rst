Since it's been a real while since the last post I've written here, I wanted to
write a short status update.

I had to serve one month in the army, which does not help at all for
productivity :P Since the update to Boost Spirit X3, I haven't worked on my
eddic compiler again, but I've switched back to my operating system project:
thor. I'm having a lot of fun with it again and it's in much better state than
before.

We also have been very productive on the publication side, with four new
publications this year in various conferences. I'll update the blog when the
proceedings are published. I'll be going to ICANN 2016 and ANNPR 2016 next week
and probably to ICFHR in October. And of course, I'll go back to Meeting C++ in
November :) As for my thesis, it's finally going great, I've started writing
regularly and it's taking form!

Thor
====

My project Thor Operating System now has much more features than before:

* 64bit operating system
* Preemptive Multiprocessing
* Keyboard / Mouse driver
* Full ACPI support with ACPICA
* Read/Write ATA driver
* FAT32 file system support
* HPET/RTC/PIT drivers
* Basic PCI support
* Multi stage booting with FAT32

Since last time, I've fixed tons of bug in the system. Although there are still
some culprit, it's much more stable than before. They were a lot of bugs in the
scheduler with loads of race conditions. I hope I've working through most of
them now.

I'm currently working on the network stack. I'm able to receive and send packets
using the Realtek 8139 card. I have working support for Ethernet, IP and ARP.
I'm currently working on adding ICMP support. I've come to realize that the
hardest part is not to develop the code here but to find a way to test it.
Network in Qemu is a huge pain in the ass to configure. And then, you need tools
to generate some packets or at least answer to packets send by the virtual
machine, and it's really bad... Nevertheless, it's pretty fun overall :)

Aside from this, I'm also working on a window manager. I'll try to post an
update on this.

You can take a look at the `thor sources <https://github.com/wichtounet/thor-os>`_ if you're interested.

Future
======

For the time being, I'll focus my effort on the thor project. I also have some
development to do on my home automation system: `asgard-server <https://github.com/wichtounet/asgard-server>`_ that I plan to finalize and deploy in a useful way this weekend in my apartment. You can also expect some updates on my deep learning library where I've started work to make it more user-friendly (kind of). I'm also still waiting on the first stable version of doctest for a new comparison with Catch.

I really want to try to publish again some more posts on the blog. I'll
especially try to publish some more updates about Thor.
