Recently, I started using systemd-networkd to manage my network. It works really
good for static address configuration, but I experienced some problem with DHCP.
There is DHCP client support integrated into systemd, so I wanted to use this
instead of using another DHCP client.

(If you are not familiar with systemd-networkd, you can have a look at the last
section of this article)

The problem with that is that services are not waiting for DHCP leases to be
obtained. Most services (sshd for instance), are waiting for network.target,
however, network.target does not wait for the DHCP lease to be obtained from the
server. If you configured ssh on a specific IP and this IP is obtained with
DHCP, it will fail at startup. The same is true for NFS mounts for instance.

Force services to wait for the network to be configured
#######################################################

The solution is to make services like sshd waits for network-online.target
instead of network.target. There is a simple way in systemd to override default
service files. For a X.service, systemd will also parse all the
/etc/systemd/X.service.d/\*.conf files.

For instance, to make sshd be started only after DHCP is finished

/etc/systemd/system/sshd.service.d/network.conf::

    [Unit]
    Wants=network-online.target
    After=network-online.target

However, by default, network-online.target does not wait for anything. You'll
have to enable another service to make it work::

    systemctl enable systemd-networkd-wait-online

And another note, at least on Gentoo, I had to use systemd-216 for it to work::

    emerge -a "=sys-apps/systemd-216"

And after this, it worked like a charm at startup.

Force NFS mounts to wait for the network
########################################

There is no service file for nfs mounts, but there is a target remote-fs.target
that groups the remote file systems mounts. You can override its configuration
in the same as a service:

/etc/systemd/system/remote-fs.target.d/network.conf::

    [Unit]
    Wants=network-online.target
    After=network-online.target

Conclusion
##########

Here we are, I hope this tip will be useful to some of you ;)

Appendix. Configure interface with DHCP with systemd
####################################################

To configure an interface with DHCP, you have to create a .network file in
/etc/systemd/network/. For instance, here is my
/etc/systemd/network/local.network file:

.. code::

   [Match]
   Name=enp3s0

   [Network]
   DHCP=v4

and you have to enable systemd-networkd::

   systemctl enable systemd-networkd
