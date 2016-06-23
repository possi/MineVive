Vivecraft Spigot Plugin (MineVive)
===========================================

This Plugin allows you to setup a Spigot 1.7 Minecraft-Server to use with [Minecraft-Vive](https://github.com/jrbudda/minecrift).

#### Server-Setup:
1. Create an empty folder to setup the server (e.g. /opt/minecraft/vive/)
1. Download [Spigot 1.7.10](https://www.google.com/search?q=Spigot+1.7.10) (Tested with Build spigot-1.7.10-R0.1-1649.jar) and save it to `spigot.jar` in your Folder
1. Create a sub-folder called `plugins`
1. Download [ProtocolLib 3.7-SNAPSHOT for Spigot 1.7.x](http://ci.dmulloy2.net/job/ProtocolLib/232/) to the **plugins**-Directory (has to be named `ProtocolLib.jar`)
1. Download [this Plugin](https://github.com/possi/MineVive/releases) and save it as `MineVive.jar` within the **plugins**-Directory
1. Start the server from your server directory with this command-line:
  `java -jar spigot.jar`
1. The server will exit after 10 seconds and there will be new files in the server-directory.
1. Open `eula.txt` with your favorite text-editor and change line 3 to `eula=true` (don't forget to save the change)
1. Open `spigot.yml` and change two values:
   1. `moved-wrongly-threshold: 10`
   1. `moved-too-quickly-threshold: 1000.0`
1. Now start the server again (cmd from `6.`)
   You're done. Minecrive-Vive can connect to your server and use teleporting-locomotion.


### How does it work?

The whole credit goes to Automat's impressive work of implementing Vive-Support into Minecraft!

There are only 2 small obstacles to use this on usual Minecraft-Servers:

1. Minecraft usual limits the distance a player can move in a *blink*. Thanks to Spigot, this threshold can be modified ([md_5](https://github.com/md-5) is awesome!)
   The Limits 10 and 1000.0 seems to be enough for now. I have no idea if they are way to huge. You may play with the limits to prevent Player from cheating.
2. minecrift-vive/vivecraft asks the Server if teleport-motion is allowed, or otherwise falls back to *normal*-movement.
   This Plugin just response with "go for it", so minecrift-vive keeps teleporting-motion activated. That's all.

### Benefit using Spigot:
There are thousands of free Plugins which work with Bukkit (Spigot) 1.7 so you can create fantastic servers with lot more features!
Here you go: http://dev.bukkit.org/categories/

By the way: Non-Vivers can join the Server too. They just need a usual 1.7 Client, which official can be downloaded via the Launcher!