Yesterday, I added my 11th disk to my RAID 6 array. As the last time it took my more than 20 hours, I spent some time investigating how to speed things up and this post contains some tips on how to achieve good grow performances. With these tips, I have been able to reach a speed of about 55K in average during reshape. It did finish in about 13 hours.

First, take into account that some of these tips may depend on your configuration. In my case, this server is only used for this RAID, so I don't care if the CPU is used a lot during rebuild or if other processes are suffering from the load. This may not be the case with your configuration. Moreover, I speak only of hard disks, if you use SSD RAID, there are probably better way of tuning the rebuild (or perhaps it is fast enough). Finally, you have know that a RAID reshape is going to be slow, there is no way you'll grow a 10+ RAID array in one hour. G

In the examples, I use /dev/md0 as the raid array, you'll have to change this to your array name.

The first 3 tips can be used even after the rebuild has started and you should see the differences in real-time. But, these 3 tips will also be erased after each reboot.

Increase speed limits
#####################

The easiest thing to do is to increase the system speed limits on raid. You can see the current limits on your system by using these commands:

.. code:: bash

    sysctl dev.raid.speed_limit_min
    sysctl dev.raid.speed_limit_max

These values are set in Kibibytes per second (KiB/s).

You can put them to high values:

.. code:: bash

    sysctl -w dev.raid.speed_limit_min=100000
    sysctl -w dev.raid.speed_limit_max=500000

At least with these values, you won't be limited by the system.

Increase stripe cache size
##########################

By allowing the array to use more memory for its stripe cache, you may improve the performances. In some cases, it can improve performances by up to 6 times. By default, the size of the stripe cache is 256, in pages. By default, Linux uses 4096B pages. If you use 256 pages for the stripe cache and you have 10 disks, the cache would use 10*256*4096=10MiB of RAM. In my case, I have increased it to 4096:

.. code:: bash

   echo 4096 > /sys/block/md0/md/stripe_cache_size

The maximum value is 32768. If you have many disks, this may well take all your available memory. I don't think values higher than 4096 will improve performance, but feel free to try it ;)

Increase read-ahead
###################

If configured too low, the read-ahead of your array may make things slower.

You can see get the current read-ahead value with this command:

.. code:: bash

    blockdev --getra /dev/md0

These values are in 512B sector. You can set it to 32MB to be sure:

.. code:: bash

    blockdev --setra 65536 /dev/md0

This can improve the performances, but don't expect this to be a game-changer unless it was configured really low at the first place.

Tip: Reshape stuck at 0K/s
##########################

If reshape starts, but with a speed of 0K/s, you can try to issue this simple
command:

.. code:: bash

    echo max > /sys/block/md0/md/sync_max

And the reshape should start directly at your maximum speed.

The solution is the same if you are growing any type of RAID level with parity
(RAID5, RAID6, ...).

Bonus: Speed up standard resync with a write-intent bitmap
##########################################################

Although it won't speed up the growing of your array, this is something that you should do after the rebuild has finished. Write-intent bitmaps is a kind of map of what needs to be resynced. This is of great help in several cases:

* When the computer crash (power shutdown for instance)
* If a disk is disconnected, then reconnected.

In these case, it may totally avoid the need of a rebuild which is great in my opinion. Moreover, it does not take any space on the array since it uses space that is not usable by the array.

Here is how to enable it:

.. code:: bash

    mdadm --grow --bitmap=internal /dev/md0

However, it may cause some write performance degradation. In my case, I haven't seen any noticeable degradation, but if it is the case, you may want to disable it:

.. code:: bash

    mdadm --grow --bitmap=none /dev/md0

Bonus: Monitor rebuild process
##############################

If you want to monitor the build process, you can use the watch command:

.. code:: bash

    watch cat /proc/mdstat

With that you'll see the rebuild going in real-time.

You can also monitor the I/O statistics:

.. code:: bash

   watch iostat -k 1 2

Bonus: How to grow a RAID 5-6 array
###################################

As a sidenote, this section indicates how to grow an array. If you  want to add the disk /dev/sdl to the array /dev/md0, you'll first have to add it:

.. code:: bash

   mdadm --add /dev/md0 /dev/sdl

This will add the disk as a spare disk. If you had 5 disks before, you'll want to grow it to 6:

.. code:: bash

   mdadm --grow --backup-file=/root/grow_md0_backup_file --raid-devices=6 /dev/md0

The backup file must be on another disk of course. The backup file is optional but improves the chance of success if you have a power shutdown or another form of unexpected shutdown. If you know what you're doing, you can grow it without backup-file:

.. code:: bash

   mdadm --grow --raid-devices=6 /dev/md0

This command will return almost instantly, but the actual reshape won't likely be finished for hours (maybe days).

Once the rebuild is finished, you'll still have to extend the partitions with resize2fs. If you use LVM on top of the array, you'll have to resize the Physical Volume (PV) first:

.. code:: bash

    pvresize /dev/md0

and then extend the Logical Volume (s) (LV). For instance, if you want to add 1T to a LV named /dev/vgraid/work:

.. code:: bash

    vgextend -r -L+1T /dev/vgraid/work

The -r option will automatically resize the underlying filesystem. Otherwise, you'd still have to resize it with resize2fs.

Conclusion
##########

These are the changes I have found that speed up the reshape process. There are others that you may test in your case. For instance, in some systems disabling NCQ on each disk may help.

I hope that these tips will help you doing fast rebuilds in your RAID array :)
