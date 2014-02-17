MMF-ImageJ-Plugin
Copyright 2013,2014 by Marc Gershow and Natalie Bernat

MMF-ImageJ-Plugin is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 3 of the License, 
or (at your option) any later version.
 
MMF-ImageJ-Plugin is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License along with MMF-ImageJ-Plugin.  If not, see http://www.gnu.org/licenses/.

---
Installation instructions: copy mmf_reader-0.0.0-SNAPSHOT.jar to your plugins directory and restart ImageJ
Plugins>MMF>Import MMF will open a file dialog to select an mmf file. File will be opened as a virtual stack.

This code is still under development. Please let us know of any problems.

---
 
 
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


