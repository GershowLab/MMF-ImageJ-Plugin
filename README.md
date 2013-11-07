This is our attempt to develop a plugin to read MMF files into ImageJ. 
Currently it is unfinished code. When we have a stable version, we will release it under an open source license.
Until then, any redistribution is forbidden.

We began with the minimal Maven project implementing an ImageJ 1.x plugin, available at
https://github.com/imagej/minimal-ij1-plugin

--Instructions from original authors--
To open the project:

In [Eclipse](http://eclipse.org), for example, it is as simple as
_File&gt;Import...&gt;Existing Maven Project_

In [Netbeans](http://netbeans.org), it is even simpler: _File&gt;Open_
Project. The same works in [IntelliJ](http://jetbrains.net).

If [jEdit](http://jedit.org) is your preferred IDE, you will need the [Maven
Plugin](http://plugins.jedit.org/plugins/?MavenPlugin).

Die-hard command-line developers can use Maven directly by calling _mvn_
in the project root.

However you build the project, in the end you will have the ```.jar``` file
(called *artifact* in Maven speak) in the _target/_ subdirectory.

To copy the artifact into the correct place, you can call ```mvn
-Dimagej.app.directory=/path/to/Fiji.app/```. This will not only copy your
artifact, but also all the dependencies. Restart your ImageJ or call
*Help>Refresh Menus* to see your plugin in the menus.

Developing plugins in an IDE is convenient, especially for debugging. To
that end, the plugin contains a _main()_ method which sets the _plugins.dir_
system property (so that the plugin is added to the Plugins menu), starts
ImageJ, loads an image and runs the plugin. See also
[this page](fiji.sc/Debugging#Debugging_plugins_in_an_IDE_.28Netbeans.2C_IntelliJ.2C_Eclipse.2C_etc.29)
for information how Fiji makes it easier to debug in IDEs.


