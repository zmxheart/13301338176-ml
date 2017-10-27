In the previous installment in the Mutt series, `I've talked about my Mutt
configuration 
<http://baptiste-wicht.com/posts/2014/07/a-mutt-journey-my-mutt-configuration.html>`_.
In this post, I'll talk about notmuch and how to use it to search through mails.

By default, you can search mails in Mutt by using the / key. By doing that, you
can only search in the current folder. This is very fast, but this is not always
what you want. When you don't know in which folder the mail you are looking for
is, you don't want to test each folder. By default, there are no feature to
achieve global searching in Mutt.

That is where notmuch comes to the rescue. notmuch is a very simple tool that
allows you to search through your mail. As its name indicates, it does not do
much. It doesn't download your mails, you have to have them locally, which is
perfect `if you use offlineimap
<http://baptiste-wicht.com/posts/2014/07/a-mutt-journey-download-mails-with-offlineimap.html>`_.
It does not provide a user interface, but you can query it from the command line
and it can be used from other tools. It should be available in most of the
distributions.

Configuration
*************

The configuration of notmuch is fairly simple. You can write your
:code:`.notmuch-config` directly or run :code:`notmuch setup` that will
interactively help you to fill the configuration.

Here is my configuration:

.. code:: 

    [database]
    path=/data/oi/Gmail/

    [user]
    name=Baptiste Wicht
    primary_email=baptiste.wicht@gmail.com

    [new]
    tags=inbox
    ignore=

    [search]
    exclude_tags=deleted;

    [maildir]
    synchronize_flags=true

It needs of cours the place where your mails are stored. Then, some information
about you. The next section is to specify which tags you want to add to new
mails. Here, I specified that each new mail must be tagged with `inbox`. You can
add several tags to new mails. In the `[search]` section, the excluded tags are
specified.

Usage
*****

Once you have configured notmuch, you can run `notmuch new` to process all
existing mails. The first run may take some time (in minutes, it is still quite
fast), but the subsequent runs will be very fast. You should run notmuch after
each offlineimap run. I personally run it in a shell script that is run by cron.
You could also use one of the hooks of offlineimap to run notmuch.

Once indexing has been done, you can start searching your mails. The first
option to search mail is simply to use `notmuch search <query>` from the command
line. This will directly displays the results. Search is instant on my mails.

If you use mutt-kz like me, notmuch support is directly integrated. You can type
X, and then type your query like :code:`notmuch://?query=X` and the results will
be displayed as a normal Mutt folder. You can open mails directly from here and
you can also edit the mails as if you were in their source folders. This is
really practical.

If you use mutt, you can have the same experience, by using the `notmuch-mutt`
patch (`here <http://notmuchmail.org/notmuch-mutt/>`). In several distributions,
there is an option to build it with this support or another package to add the
feature.

Another feature of notmuch is its ability to tag mails. It automatically tags
new mails and deleted mails. But you can also explicitely tag messages by using
:code:`notmuch tag`. For instance, to tag all messages from the notmuch mailing
list:

.. code::

    notmuch tag +notmuch -- tag:new and to:notmuch@notmuchmail.org

I personally don't use this feature since I use imapfilter and IMAP folders to
sort my mail, but it can be very useful. You can run these commands in the
cronjob and always have you tags up to date. Tags can then be used in notmuch to
search or to create virtual folder in Mutt.

Conclusion
**********

That is already more or less everything that there is to know about notmuch. It
does not do a lot of thing, but it does them really well.

That concludes the series of posts on Mutt. If you have any question on my Mutt
configuration, I'd be glad to extend on the comments.
