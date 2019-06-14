![Banner](https://image.prntscr.com/image/vMHp1JlFSi_51KvHGOIehA.png)
Adds the ability to show the light-levels of blocks around you.


## Modes & Commands
> Eternal Light is built to be simple and light weight while 
adding a useful feature, to keep it this way all the commands are kept basic and easy to learn and remember.

#### Commands
`/eternallight reload` Reloads the plugins config file.  
`/eternallight version` Checks the plugins version.  
`/eternallight renderdistance <distance>` Changes how far out it renders light points. This will cause expetentually more load the larger it is.  
`/lightlevel or /ll` Toggles the light level display.  
`/lightlevel mode [mode]` Toggles between the active modes for the light level display.

#### Display Methods
* `All` Renders yellow where hostile mobs can spawn at night, red where they can spawn at any time, and green where they cannot spawn.
* `Lightlevels` Renders a smooth light level indicator. (looks pretty)
* `Spawnable` Renders red and yellow particles to show where hotile entities can spawn. Does not show any particles where hostile mobs cannot spawn.
