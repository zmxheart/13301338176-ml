In the series of posts about Mutt, I recently presented how I was `filtering my email
</posts/2014/07/a-mutt-journey-filter-mails-with-imapfilter.html>`_. In this
post, I'll show how I download my emails locally using offlineimap. This is the
perfect companion for Mutt.

With Mutt, you can easily directly query an IMAP server and keep the views up to
date with it. There are a few problem with this approach:

* First, you wont' be able to read your mails when you'are offline. It is
  rarely an issue in these days, but it can be useful.
* Opening an IMAP folder with a large number of mails (>1000) can be quite
  slow. I've several large folders and it was a pain opening them.
* When Mutt synchronizes with the state of the IMAP server, you'll encounter a
  freeze. If you want to synchronize often, it is quite boring.

Having your mails offline on your computers solves all these problems. Moreover,
it is also a good way to have a backup of your mails. I'm gonna talk here about
the usage for Mutt, but you can use offlineimap just for backup or for migration
reasons. The downside is that you have to store it locally. My mails takes
around 5GB on my computer.

offlineimap is a very simple tool to synchronize emails from IMAP servers. It
only supports IMAP, but in those days it is not a limitation. The
synchronization is made both ways, it will upload your local changes to the IMAP
server. It is very powerful when paired with a MUA such as Mutt.

To use offlineimap, you have to put your configuration in the ~/.offlineimaprc.
You can synchronize several accounts at once, in this post, we'll focus on one,
but the process is the same for several accounts. I'll focus on Gmail too, but
again it is the same with a bit different parameters for other mail accounts.

Configuration
=============

First, we have to declare the account:

.. code::

    [general]
    accounts = Gmail

    [Account Gmail]
    localrepository = Gmail-Local
    remoterepository = Gmail-Remote

**accounts** is the list of accounts that we have, here only one. Then, in
account, repositories are just names of the repositories we'll declare now. 

The local repository has to be configured: 

.. code::

    [Repository Gmail-Local]
    type = Maildir
    localfolders = /data/oi/Gmail/
    sep = /

The first important point is **localfolders** that sets where the mail will be
put on your computer. **sep** defines the separator used for nested IMAP
folders. I recommend / since Mutt will nest them automatically if / is used as
separator. 

Then, the remote repository has to be configured: 

.. code::

    [Repository Gmail-Remote]
    type = Gmail
    remoteuser = USER
    remotepass = PASSWORD
    realdelete = no
    folderfilter = lambda folder: folder not in ['[Gmail]/All Mail',
                                                 '[Gmail]/Important',
                                                 '[Gmail]/Starred',
                                                 ]
    sslcacertfile = /etc/ssl/certs/ca-certificates.crt

**remotepass** and **remoteuser** are your user names and password. You can also
use **remotepassfile** to read the password from a file. **realdelete=no**
indicates that we only want to remove all the labels of deleted mails. For
Gmail, it means that the mail will still be in the All Mail folder. The last
line (sslcacertfile) is mandatory for recent versions of offlineimap. The
**folderfilter** is a function that filters some folders. In my case, I do not
want to get the "All Mail", "Important" and "Starred" of my Gmail account
because it is only a duplicata of the mails in other labels. What is pretty cool
with offlineimap is that you can write Python directly in it for some of the
configuration options. Here is rule for filter is plain Python, so you can
complicated filtering if you want. 

Last, but not least, offlineimap can generates a list of mailboxes (one for each
folder in every account). It is pretty useful since Mutt can then read this file
and you'll find your mailboxes directly configured in Mutt :)

This code will generate a file ~/.mutt/mailboxes that you can source in your
Mutt configuration and get the complete list of available mailboxes. This will
be kept up to date if you add new IMAP folders on the server for instance. 

.. code::

    [mbnames]
    enabled = yes
    filename = ~/.mutt/mailboxes
    header = "mailboxes "
    peritem = "+%(accountname)s/%(foldername)s"
    sep = " "
    footer = "\n"


Translate names
===============

You may have seen in the previous section some weird folder name like
"[Gmail]/All mail", this is how Gmail names folders that are not labels. This is
quite ugly and will create odd looking folders on your computer. You can
configure offlineimap to rename these names to better ones. For that, you'll
need to rule (in Python ;) ), one to translate from remote to local and one to
do the reverse. 

Here is what I did:

.. code::

    [Repository Gmail-Local]
    nametrans = lambda folder: {'drafts':   '[Gmail]/Drafts',
                                'sent':     '[Gmail]/Sent Mail',
                                'important':'[Gmail]/Important',
                                'spam':     '[Gmail]/Spam',
                                'starred':  '[Gmail]/Starred',
                                'trash':    '[Gmail]/Trash',
                                'archive':  '[Gmail]/All Mail',
                                }.get(folder, folder)

    [Repository Gmail-Remote]
    nametrans = lambda folder: {'[Gmail]/Drafts':    'drafts',
                                '[Gmail]/Sent Mail': 'sent',
                                '[Gmail]/Starred':   'flagged',
                                '[Gmail]/Important':   'important',
                                '[Gmail]/Spam':   'spam',
                                '[Gmail]/Trash':     'trash',
                                '[Gmail]/All Mail':  'archive',
                                }.get(folder, folder)

I simply renamed all "[Gmail]" folders into something more readable and that
makes more sense to me. It is not limited to special Gmail folders of course,
this can also be applied to rename a folder X into a folder Y in the same. As it
is Python, you can do sophisticated stuff if necessary.

Speed up things
===============

If you happen to sync your mails often, you may want to speed things up. There
are several ways to do that.

The first thing you can do is use several connections to the server. You can set
maxconnections to a number higher than 1 in the remote repository configuration.
I tested several values and for Gmail 2 was the fastest choice. You can try some
values with your server to see what value is good.

Instead of plain old text files for the status of the mails, offlineimap can use
a sqlite backend. This is much faster since the complete file is not rewritten
for each update of the flags. For that behaviour, you have to set
**status_backend = sqlite** in the Account configuration.

Another thing you can do is reduce the I/O involved during sync by setting
general.fsync to false. With that, offlineimap won't have to wait for disk
operation completion after each operation.

You can run offlineimap in quick mode with -q option. With this option, change
in flags of remote messages will not be updated locally. Changes on the local
side will be uploaded corectly. It is generally a good idea is to run
offlineimap in quick mode often (every X minutes) and run it in normal mode once
or twice a day.

You can also specify which folder to sync with the -f option. Sometimes it is
enough to sync INBOX for instance. It may be much faster.

Conclusion
==========

Now that you have fully configured offlineimap, you can make it run by hand or
in a cron job. I personally run it every 5 minutes, you can choose your
favourite frequency according to your workflow. I think I'll reduce the
frequency further, it is more comfortable to get mails only by batch and not
too much of them.

If you're interested, you can take a look at `my .offlineimaprc configuration
<https://github.com/wichtounet/dotfiles/blob/master/.offlineimaprc>`_.

If you want more information about this awesome tool, you can take a look at the
`reference documentation <http://docs.offlineimap.org/en/latest/>`_.

This is it for this part of this series. In the next post, I'll present my Mutt
configuration and how I use it.
