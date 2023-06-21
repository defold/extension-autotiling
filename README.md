# extension-autotiling

This is an extension for applying 4- or 8-bit autotiling while painting on a Defold tilemap. The extension uses the solution described in this blog post:

https://gamedevelopment.tutsplus.com/tutorials/how-to-use-tile-bitmasking-to-auto-tile-your-level-layouts--cms-25673

4-bit autotiling tilesource:

![](main/assets/images/grass-4bit.png)

8-bit autotiling tilesource:

![](main/assets/images/grass-8bit.png)


## Example
Open this extension as a Defold project and run it. Select the tilemap and start painting with any tile from the palette. The autotiling algorithm will automatically pick the correct tiles to paint, based on surrounding tiles.

