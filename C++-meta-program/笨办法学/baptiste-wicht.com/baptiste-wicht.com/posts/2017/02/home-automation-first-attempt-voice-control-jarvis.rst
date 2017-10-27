I have several devices in my home that can be controller via Domoticz, a few
power outlets, a few lights (more are coming), my Kodi home theater. And I have
a lot of sensors and information gathered by Domoticz. All of this is working
quite well, but I have only a few actuators and intelligence (motion sensor,
button and some automation via Lua script).

My next objective was to add voice control to my system. If I was living in
United States or United Kingdom I would directly an Amazon Dot or even an Amazon
Echo, but they are not available in Switzerland. I could have arranged for
delivery, but if I want my system to be useful to several people, I need to have
in French. It's the same problem with the Google Home system. So, no other way
than custom solutions.

Since I had an extra Raspberry Pi 2, I based my system on this. I bought a Trust
Mico microphone and a Trust Compact speakers and installed them on the Pi. Both
peripherals are working quite well.

You can have a closer look at my microphone:

.. image:: /images/jarvis_mic.jpg
   :align: center
   :alt: Trust Mico microphone for Jarvis Home Automation Voice Control

and the complete installation:

.. image:: /images/jarvis_full.jpg
   :align: center
   :alt: Jarvis Home Automation Voice Control around my TV

The Raspberry Pi is on the bottom, the speakers below the TV, left and right and
the microphone on the top right.

For the voice control software, I decided to go with Jarvis. It seems to me that
this is the best suited software for this kind of project. Moreover, it supports
French natively which seems good. I also tried Jasper, but this has been such
a pain to install that I gave up.

Jarvis is reasonably easy to install if you have a recent Raspbian image. It
took some time to install the dependencies, but in the end it was not difficult.
The installation process has a step-by-step wizard help so it's really easy to
configure everything.

However, even if it's easy to install, it's easy to configure correctly. The
first thing is to configure the hotword to activate commands. There are several
options, but I used snowboy which is offline and is made for hotword
recognition. This worked quite well, you just have to train a model with the
hotword to recognize the voice. After this, the problems started... You then
have to configure audio for the commands themselves. There are 6 parameters for
audio capture (noise levels to start and stop the capture, silence levels, ...)
and no help to tune them. So basically, I tried a lot of combinations until
I had something working reasonably well. When you are in debug mode, you can
listen to what the system captured. These parameters are dependent on your
environment and on your microphone and on your voice. I may be dumb but it took
several hours and a lot of tries to get a configuration working. After this, you
have to choose the engine for recognition of the commands. Unfortunately, all
the good options are online so everything you'll say as commands after the
hotword will be sent online. I first tried Bing, but I had very poor recognition
rate. I then switched to wit.ai which gave me better results. In the end, I have
about 60% recognition rate, which is not great at all, but at least some phrases
are working almost all the time while others are always failing. Another problem
I have with this is the large delay between commands and action. It takes almost
five seconds between the end of my sentence and the time where the lights in my
living room are tuned on or off by Jarvis via Domoticz.

So far, I'm a bit disappointed by the quality of the system, but maybe I was
hoping for too much. I have been able to control a few of my appliances but not
really reliably. Another thing I have realized is that when counting the
Raspberry Pi, its enclosure the Microphone and the speakers, this system is more
costly than an Amazon Dot and seem highly inferior (and is much less good
looking).

I'll try to improve the current system with better configuration and commands in
the coming days and I will maybe try another system for voice control. I still
hope Amazon Alexa systems or Google Home are made available in
France/Switzerland not too far in the future, since I believe these systems are
a better solution than custom made systems, at least for now. Moreover, next
month, I plan to integrate ZWave into my systems with a few sensors, complete
the lighting installation and add new motion sensors. This should make it more
useful. And hopefully, by this time, I should have a good voice control system,
but I'm not too hopeful.

Don't hesitate to comment or contact me if you have questions about this
installation or want to share experience about voice control in home automation.
If you want more details about this, dont' hesitate to ask as well ;)
