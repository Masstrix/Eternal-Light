![Banner](https://image.prntscr.com/image/vMHp1JlFSi_51KvHGOIehA.png)

# Eternal Light
> Adds the ability to show the light-levels of blocks around you.


## Modes & Commands
> Eternal Light is built to be simple and light weight while 
adding a useful feature, to keep it this way all the commands are kept basic and easy to learn and remember.

#### Commands
`/eternallight reload` Reloads the plugins config file.  
`/eternallight version` Checks the plugins version.  
`/showlight or /ll` Toggles the light level display.  
`/showlight mode` Toggles between the active modes for the light level display.

#### Display Methods
* `Normal` Displays red and yellow particles to show where hotile entities can spawn.
* `Inclusive` Displays the same as normal but with green particles for where hostile entities cannot spawn.
* `Smooth` Displays the same as inclusive but with a smooth gradient between each change.


## API Usage
> Theres not much of a point accessing any of this plugin but if you did want to you can do so using the shown ways.
```java
public LightVisual getPlayersVisual(Player player) {
    return EternalLight.getInstance().getProjector().getVisual(player);
}
```
