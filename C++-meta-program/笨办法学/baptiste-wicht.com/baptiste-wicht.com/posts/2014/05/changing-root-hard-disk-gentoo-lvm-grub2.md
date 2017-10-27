I recently changed my hard disk on one of my servers and I didn't found a simple
and up to date tutorial to do that, so I decided to post here how I did it. 

I do not claim that is the best method, or the easiest or the safest method, but
that is the one I used and it worked quite well. You could do it online with LVM, 
but when changing the boot hard disk, I'd rather be safe. Especially because you
need to reinstall Grub on the new hard disk and probably alter its
configuration. But, doing it online directly with LVM should work. 

My server is using LVM and Grub2. You will need a Gentoo Live CD (or DVD, or
USB, or any other Linux live installation with the necessary tools). If you do
not use LVM, this guide is not for you. Even if I wrote this guide for Gentoo,
this is also gonna work with another system as long as you use LVM and Grub2,
just use your distribution live cd. 

Here I will assume /dev/sda is your old disk and /dev/sdb is your new disk.
I will also assume that you previous VG was vg0. If it is, you'll have to
replace them in the commands. 

1\. Obviously, shutdown your computer. 

2\. Install the new hard disk in it. 

3\. Reboot on the Live CD:

  * Activate LVM: vgchange -a y
  * Create the LVM partition on the new disk (/dev/sdb) with fdisk. 
  * Create a new PV: pvcreate /dev/sdb1
  * Create a new VG: vgcreate vg1 /dev/sdb1
  * Recreate all your LV with lvcreate. In my case, I have /dev/vg0/root and
/dev/vg0/data 

Now, you'll need to copy your data. Here is an example with my root: 

```bash
mkdir old
mkdir new
mount /dev/vg0/root/ old
mount /dev/vg1/root/ new
cp -ax old/* new
umount old
umount new
```

You have to do that with for LV you have. 

4\. Shutdown the computer

5\. Remove the old hard disk and use the connector of the old hard disk to
connect the new one. 

6\. Reboot on the Live CD: 

  * Activate LVM: vchange -a y
  * Rename the VG: vgrename vg0 
  * Chroot in the new hard disk
  * Install grub on the hard disk: grub2-install /dev/sda
  * Regenerate the Grub config: grub2-mkconfig -o /boot/grub/grub.cfg
  * Exit from chroot

7\. Reboot on Gentoo

8\. You now have switched hard disk. 

At any point, if something goes wrong, you still have the previous hard disk
completely ready at hand. 

I hope this would useful to some Linux users. 