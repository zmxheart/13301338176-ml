About a month ago, I decided to switch to Mutt to read my emails. I kept my
GMail account, but I don't use the web interface anymore. It took me a long time
to prepare a complete enviromnent.

Currently, i'm using: 

* imapfilter to filter mails
* offlineimap to download my mails
* notmuch to quickly search all my mails

And of course Mutt. To be precise, I use mutt-kz, a fork of mutt with very good
notmuch integration. 

I'll try to explain each part of my environment in a series of articles on this
blog. The first one will be about imapfilter. 

imapfilter is a mail filtering utility. It connects to a remote server using
IMAP and is then able to move, copy or delete mails around. You can use it for
several tasks:

* Delete unwanted mail
* Move mails into folders according to rules

What is pretty cool is that the configuration is entirely made in Lua. It is
quite easy to write rules and then apply them to several mailboxes as if you
were programming.

Another advantage of imapfilter is that it works at the server level. Therefore,
even if you use your web client from time to time or check your mail on your
phone, the changes will still be viewable.

The configuration is done in the ~/.imapfilter/config.lua file. The
configuration is quite easy, you have to declare an IMAP object as the account. 

.. code:: lua

    local account = IMAP {
        server = 'imap_sever',
        username = 'username',
        password = 'password',
        ssl = 'ssl3',
    }

As the configuration is in Lua, you can easily get the password from another
file. For instance, here is my account declaration:

.. code:: lua

    local account = IMAP {
        server = 'imap.gmail.com',
        username = 'baptiste.wicht@gmail.com',
        password = get_imap_password(".password.offlineimaprc"),
        ssl = 'ssl3',
    }

    -- Utility function to get IMAP password from file
    function get_imap_password(file)
        local home = os.getenv("HOME")
        local file = home .. "/" .. file
        local str = io.open(file):read()
        return str;
    end

It gets the password by reading a file in the home directory. 

Once, you have the account, you can check the status of a folder with the
check_status() function. For instance: 

.. code:: lua

    account.INBOX:check_status()
    account['[Gmail]/Trash']:check_status()

You can run imapfilter simply by launching imapfilter on the command line. Once
imapfilter is run, it will print the status of the folder you choses::

    38 messages, 0 recent, 6 unseen, in baptiste.wicht@gmail.com@imap.gmail.com/INBOX.
    70 messages, 0 recent, 67 unseen, in baptiste.wicht@gmail.com@imap.gmail.com/[Gmail]/Trash.

Several functions are important: 

* select_all() on a folder allows you to get messages from an account to them
  perform action on them
* contain_subject('subject') on a list of mails allows you to keep only the mails
  that contains 'subject' in their subject
* contain_from('from') on a list of mails allows you to keep only the mails
  that comes from 'from'
* contain_to('to') on a list of mails allows you to keep only the mails
  that are addressed to 'to'
* delete_messages() on a collection of mails deletes all of them
* move_messages(folder) on a collection of mails moves all of them to another
  folder. 

You can also mix different IMAP accounts, you don't have to use only one. 

For instance, if you would delete all the mail coming from me, you could do: 

.. code:: lua

    mails = account.INBOX:select_all()
    filtered = mails:contains_from("baptiste.wicht@gmail.com")
    filtered:delete_messages()

Or you could move all the mails containing Urgent in the subject line to an IMAP
folder: 

.. code:: lua

    mails = account.INBOX:select_all()
    filtered = mails:contains_subject("Urgent")
    filtered:move_messages(account["urgent_mails"])

If you want some more examples, you can take a look at `my imapfilter
configuration
<https://github.com/wichtounet/dotfiles/blob/master/.imapfilter/config.lua>`_.

The best way to start using it is to look at examples, there are plenty of them
in the internet, especially in Github dotfiles repositories. 

The reference documentation is available using 'man imapfilter_config', there is
plenty more to see. 

For more information, you can also consult the `offical site
<https://github.com/lefcha/imapfilter>`_. 

That is it for this part of the mutt series. In the next post about mutt, I'll
talk about how I use offlineimap to get my mails.
