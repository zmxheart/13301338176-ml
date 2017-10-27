I just started growing again my RAID6 array from 12 to 13 disks and
I encountered a new issue. The reshape started, but with a speed of 0K/s. After
some searching, I found a very simple solution:

.. code:: bash

    echo max > /sys/block/md0/md/sync_max

And the reshape started directly at 50M/s :)

The solution is the same if you are growing any type of RAID level with parity
(RAID5, RAID6, ...).

Normally, the issues I have are related to speed not very good. I've written
a post in the post about
`how to speed up RAID5 / RAID6 growing with mdadm <https://baptiste-wicht.com/posts/2015/03/how-to-speed-up-raid-5-6-growing-with-mdadm.html>`_.
Although RAID5 / RAID6 growing, or another reshape operation, will never be very
fast, you can still speed up the process a lot from a few days to a few hours.
Currently, my reshape is working at 48M/s and I'm looking at around 16 hours of
reshape, but I have 13 disks of 3To, so it's not so bad.

I hope this very simple tip can be helpful to some of you :)
