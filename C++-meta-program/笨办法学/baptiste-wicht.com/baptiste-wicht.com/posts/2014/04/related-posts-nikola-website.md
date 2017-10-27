The one thing I missed in Nikola was the lack of **Related Posts generation**. I solved this during [the migration from WordPress to Nikola](http://baptiste-wicht.com/posts/2014/03/migrated-from-wordpress-to-nikola.html), by using simple algorithms to generate related posts for each blog post and then display them in the form of a simple widget. 

For example, you can see the related posts of this post on the left, just under my Google+ badge. 

Here is the workflow that is used: 
 * A simple C++ tool generate a list of related posts in HTML for each posts
 * The generated HTML code is included in the MAKO template using Python

In this article, I'll show how the related posts are generated and how to include them in your template.

# Related Post Generation

It is important to note that it is necessary to cleanup the content of the files before using it: 
 * First, it is necessary to remove all HTML that may be present in the Markdown files. I remove only the HTML tags, not their content. For instance, in *&lt;strong&gt;test&lt;/strong&gt;*, test would be counted, but not strong. The only exception to that, is that the content of preformatted parts (typically some or console output) is completely removed.
 * It is also necessary to cleanup Markdown, for instance, parentheses and square brackets are removed, but not their content. Same goes for Markdown syntax for bold, italics, ...
 * Finally, I also remove punctuation. 

My related posts algorithm is very simple. 

First, I compute the Term Frequency (TF) of each word in each post. The number of times a word is present in a document is represented by *tf(w,d)*. I decided to give a bigger importance to words in the title and the tags, but that is just a matter of choice. 

After that, I compute the Inverse Document Frequency (IDF) of each word. This measure allows to filter words like: a, the, and, has, is, ... These words are not really representative of the content of a blog post. The formula for idf is very simple: *idf(w) = log(N / (1+ n(w)))*. *n(w)* is the number of posts where the word is present. It is a measure of rarity of a word on the complete posts set. 

Once we have the two values, we can easily compute the TF-IDF vectors of each blog post. The TF-IDF for a word is simply: *tf_idf(w,d) = tf(w, d) * idf(w)*. 

Finally, we can derive the matrix of Cosine similarities between the TF-IDF vectors. The idea of the algorithm is simple: each document is represented by a vector and then the distance between two vectors indicates how related two posts are. The formula for the Cosine similarity is also simple: *cs(d1, d2) = dot(d1, d2) / ||d1|| * || d2||*. *d1* and *d2* are two TF-IDF vectors. Once the cosine similarities between each document is computed, we can just take the N most related documents as the "Related Posts" for each blog post. 

With this list, the C++ program simply generates an HTML file that will be included in each post by Nikola template. This process is **very fast**. I have around 200 posts on this blog and the generation takes about 1 second. 

# Include in template

Once the HTML files are generate, they are included into the website by altering the template and adding their content directly into the web page. Here is the code I use in *base.tmpl*.

```mako
%if post and not post.source_link().startswith('/stories/'):
    <div class="left-sidebar-widget">
        <h3>Related posts</h3>
        <div class="left-sidebar-widget-content">
            <%
                import os
                related_dir = os.getcwd()
                related_path = related_dir + post.source_link() + ".related.html"

                try:
                    with open(related_path, 'r') as f:
                        related_text = f.read()
                        f.close()
                except IOError as e:
                    related_text = "Not generated"
            %>
            ${related_text}
        </div>
    </div>
%endif
```

You could also display it in *post.tmpl* as a simple list. 

There is a limitation with this code: it only works if the source file has the same name than the slug, otherwise the file is not found. If someone has a solution to get the path to the source file and not the slug version, I'd be glad to have it ;)

# Conclusion

The code for the generator is available on the [Github repository of my website](https://github.com/wichtounet/wichtounet.github.io/tree/master/src/related). 

I wrote it in C++ because I don't like Python a lot and because I'm not good at it and it would have taken me a lot more time to include it in Nikola. If I have time and I'm motivated enough, I'll try to integrate that in Nikola. 

I hope that could be useful for some people. 