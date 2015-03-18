# JMinerGuide

Helper application for EVE Online miners. In Java.

# Usage

* Make sure that you have Java 7+ JRE installed.
* Download release archive and extract somewhere
* Run JMinerGuide.jar from the root directory of the application
* Set up API keys for your miners and booster
* If the API key for your booster isn't available, use All 5 booster char, or set up a custom char - if your booster isn't that good yet.
* Set up your mining ship and booster ship.
* Enjoy your calculated stats!
* And, if you're under Windows, you can use Asteroid Monitor to watch your asteroids as you mine them!

# TBA

* ISK/hour calculation - for raw ores, compressed ores and reprocessed minerals
* Support for testing server
* Installer, launcher and all the niceties
* Asteroid Monitor for the Mac OS

# Asteroid Monitor

A tool to make mining more enjoyable. It's able to:

* Notify you of asteroid expiration before the laser cycle ends
* Alert you when your ore hold is filled
* Provide you with various timers for your needs
* All of this - per-window. Turn your mining operation with six alts into whack-an-asteroid game with nearly zero mining time loss!

To correctly use the monitor, you need to:

* Use Windows OS. There's no monitor for other OSes at this moment.

Either:

* Set up API keys for all your chars.
* Set up API key for your booster. If the key isn't available, you should set up booster skills on the custom character and use it as a booster.
* Correctly set ship equipment for all your miners and a booster
* Check, that calculated values for mining yield, mining cycle time and ore hold are correct

Or:

* Just start a Monitor
* Set up ore hold volume, number of turrets, ore yield per turret and turret cycle for every pilot without the API key.
* Check "Simple" checkbox for every API character, that you want to use simple setup for, and set them up same way
* You can see yield and cycle time in a turret mouseover, by the way.

You can mix pilots with API and full setup, pilots with API and simple setup and API-less pilots any way you see fit for yourself.

And then:

* Set up monitor settings and asteroid filter as needed
* Load survey scanner result (ctr-a, then ctrl-c in the results window, ctrl-v into the Load Scan window)
* Check, that used ore hold is same both in monitor and a game, adjust if needed
* Start mining ingame, and then in the monitor, same turret on the same asteroid. You can press interface buttons for that, or use f1-f3 buttons, same as ingame
* Respond to the alerts
* Rinse and repeat

# Known issues

* Yes, there is no Asteroid Monitor for Mac OS and Linux at this moment. Technology isn't there yet!
* Asteroid monitor can't work with more, than three turrets, so no managing mining titans (sorry, Chribba!). This is intentional, as it will overcomplicate the interface, and, well, almost never used this days.
* There is no drone support in the monitor at this moment. This is intentional, as it's harder to correctly emulate them with their need to constantly move to asteroid and back, than the generic turret, and there's more pressing issues at this moment. Maybe things will change later.
* Yes, Station Trip field in the main window does nothing at this moment. This will change soon.

# License

JMinerGuide source code is distributed under the BSD 2-clause license.

All EVE Online related materials, information and images, including application, dialog and window icons are a property of CCP hf.

ting.wav was downloaded from https://www.freesound.org/people/robni7/sounds/174027/ and is distributed under Creative Commons Attribution 3.0 license.
