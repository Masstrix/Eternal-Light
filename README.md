<div align="center">
    <img height="150px" src="https://i.imgur.com/0zAVXVj.png">
    <h2>Eternal Light</h2>
    <p>Visually see light levels on your minecraft server!</p>
</div>
<p align="center">
    <img src="https://img.shields.io/spiget/tested-versions/50961?style=for-the-badge">
    <a href="https://github.com/Masstrix/Eternal-Nature/blob/master/LICENSE">
        <img src="https://img.shields.io/github/license/Masstrix/Eternal-Nature?style=for-the-badge"/>
    </a>
    <br><br>
    <a>
        <img src="https://img.shields.io/spiget/downloads/50961?style=for-the-badge">
    </a>
    <a>
        <img src="https://img.shields.io/bstats/servers/6575?style=for-the-badge">
    </a>
    <a>
        <img src="https://img.shields.io/bstats/players/6575?style=for-the-badge">
    </a>
</p>

# Quick Links
* [Discord Support](https://discord.gg/Uk3M9Y6)
* [Spigot Page](https://www.spigotmc.org/resources/50961/)
* [Wiki](https://github.com/Masstrix/Eternal-Light/wiki)
* [Report an Issue](https://github.com/Masstrix/Eternal-Light/issues/new)

# Permissions
To set the permissions, open the file `permissions.yml` in the spigot 
directory (the one that contains spigot.yml and bukkit.yml) and paste the following:
```
eternallight.admin: 
  default: true
eternallight.use:
  default: true
eternallight.mode: 
  default: true
eternallight.target: 
  default: true
```
* `eternallight.admin` - Gives permission to use /eternallight.
* `eternallight.use` - Gives permission to toggle and use the overlay.
* `eternallight.mode` - Allows players to change the mode with /ll mode.
* `eternallight.target` - Allows players to change the target with /ll target.

You can see the available options in the documentation to [the permissions.yml file](https://bukkit.gamepedia.com/Permissions.yml). 
If you are using a permission plugin, refer to its documentation instead.

# Commands
* `/eternallight reload` Reloads the plugins config file.  
* `/eternallight version` Checks the plugins version.  
* `/eternallight renderdistance <distance>` Changes how far out it renders light points. This will cause expetentually more load the larger it is.  
* `/lightlevels or /ll` Toggles the light level display.  
* `/lightlevels mode [mode]` Toggles between the active modes for the light level display.
* `/lightlevels target [target]` Sets a target entity. When a target is set the display modes SPAWNABLE and ALL will be tailored to that specific entity. If the entity only spawns in a specific dimension then it will always show as safe in other dimensions.

# Display Methods
* `All` Renders yellow where hostile mobs can spawn at night, red where they can spawn at any time, and green where they cannot spawn.
* `Lightlevels` Renders a smooth light level indicator. (looks pretty)
* `Spawnable` Renders red and yellow particles to show where hotile entities can spawn. Does not show any particles where hostile mobs cannot spawn.

<br><br>
<div align="center">
    <a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CFWPD6QYNTRLC&source=url">
        <img src="https://img.shields.io/badge/PayPal-Donate-blue?style=for-the-badge">
    </a>
</div>

<br><br>
