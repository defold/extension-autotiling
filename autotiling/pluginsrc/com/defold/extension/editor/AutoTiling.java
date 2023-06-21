package com.defold.extension.editor;

import java.util.List;
import java.util.ArrayList;

import com.dynamo.gamesys.proto.Tile.TileCell;
import com.defold.extension.editor.TilemapPlugins.TilemapLayer;
import com.defold.extension.editor.ITilemapPlugin;

// https://gamedevelopment.tutsplus.com/tutorials/how-to-use-tile-bitmasking-to-auto-tile-your-level-layouts--cms-25673
public class AutoTiling implements ITilemapPlugin {

    private static final int NORTH_4BIT = 1;
    private static final int WEST_4BIT  = 2;
    private static final int EAST_4BIT  = 4;
    private static final int SOUTH_4BIT = 8;

    private static final int NORTHWEST_8BIT = 1;
    private static final int NORTH_8BIT     = 2;
    private static final int NORTHEAST_8BIT = 4;
    private static final int WEST_8BIT      = 8;
    private static final int EAST_8BIT      = 16;
    private static final int SOUTHWEST_8BIT = 32;
    private static final int SOUTH_8BIT     = 64;
    private static final int SOUTHEAST_8BIT = 128;

    private TileCell createTileCell(int x, int y, int tile) {
        TileCell.Builder b = TileCell.newBuilder();
        b.setX(x).setY(y).setTile(tile);
        b.setHFlip(0).setVFlip(0).setRotate90(0);
        return b.build();
    }

    private int setMaskBit4(int tile, int bit) {
        tile &= ~bit;
        tile |= bit;
        return tile;
    }
    private int clearMaskBit4(int tile, int bit) {
        tile &= ~bit;
        return tile;
    }
    private int setMaskBit8(int tileIndex, int bit) {
        int tileMask = tileToMaskBit8(tileIndex);
        tileMask &= ~bit;
        tileMask |= bit;
        return maskBit8ToTile(tileMask);
    }

    private int updateMaskBit8(int tileIndex, int set, int clear) {
        int tileMask = tileToMaskBit8(tileIndex);
        tileMask &= ~clear;
        tileMask &= ~set;
        tileMask |= set;
        return maskBit8ToTile(tileMask);
    }

    private int clearMaskBit8(int tileIndex, int bit) {
        int tileMask = tileToMaskBit8(tileIndex);
        tileMask &= ~bit;
        return maskBit8ToTile(tileMask);
    }

    private int maskBit8ToTile(int tileMask) {
        switch (tileMask) {
            case 2: return 1;
            case 8: return 2;
            case 10: return 3;
            case 11: return 4;
            case 16: return 5;
            case 18: return 6;
            case 22: return 7;
            case 24: return 8;
            case 26: return 9;
            case 27: return 10;
            case 30: return 11;
            case 31: return 12;
            case 64: return 13;
            case 66: return 14;
            case 72: return 15;
            case 74: return 16;
            case 75: return 17;
            case 80: return 18;
            case 82: return 19;
            case 86: return 20;
            case 88: return 21;
            case 90: return 22;
            case 91: return 23;
            case 94: return 24;
            case 95: return 25;
            case 104: return 26;
            case 106: return 27;
            case 107: return 28;
            case 120: return 29;
            case 122: return 30;
            case 123: return 31;
            case 126: return 32;
            case 127: return 33;
            case 208: return 34;
            case 210: return 35;
            case 214: return 36;
            case 216: return 37;
            case 218: return 38;
            case 219: return 39;
            case 222: return 40;
            case 223: return 41;
            case 248: return 42;
            case 250: return 43;
            case 251: return 44;
            case 254: return 45;
            case 255: return 46;
            case 0: return 47;
            default:
                System.out.println("maskBit8ToTile unknown tile mask " + tileMask);
                return 0;
        }
    }

    private int tileToMaskBit8(int tileIndex) {
        switch (tileIndex) {
            case 1: return 2;
            case 2: return 8;
            case 3: return 10;
            case 4: return 11;
            case 5: return 16;
            case 6: return 18;
            case 7: return 22;
            case 8: return 24;
            case 9: return 26;
            case 10: return 27;
            case 11: return 30;
            case 12: return 31;
            case 13: return 64;
            case 14: return 66;
            case 15: return 72;
            case 16: return 74;
            case 17: return 75;
            case 18: return 80;
            case 19: return 82;
            case 20: return 86;
            case 21: return 88;
            case 22: return 90;
            case 23: return 91;
            case 24: return 94;
            case 25: return 95;
            case 26: return 104;
            case 27: return 106;
            case 28: return 107;
            case 29: return 120;
            case 30: return 122;
            case 31: return 123;
            case 32: return 126;
            case 33: return 127;
            case 34: return 208;
            case 35: return 210;
            case 36: return 214;
            case 37: return 216;
            case 38: return 218;
            case 39: return 219;
            case 40: return 222;
            case 41: return 223;
            case 42: return 248;
            case 43: return 250;
            case 44: return 251;
            case 45: return 254;
            case 46: return 255;
            case 47: return 0;
            default:
                System.out.println("tileToMaskBit8 unknown tile index " + tileIndex);
                return 0;
        }
    }
    private List<TileCell> update4bit(int x, int y, TilemapLayer layer) {
        List<TileCell> changes = new ArrayList<>();

        Integer north  = layer.get(x, y + 1);
        Integer south  = layer.get(x, y - 1);
        Integer west   = layer.get(x - 1, y);
        Integer east   = layer.get(x + 1, y);
        Integer center = layer.get(x, y);

        // we painted a tile
        if (center != null) {
            int mask = 0;
            if (north != null) {
                mask += NORTH_4BIT;
                changes.add(createTileCell(x, y + 1, setMaskBit4(north, SOUTH_4BIT)));
            }
            if (west != null) {
                mask += WEST_4BIT;
                changes.add(createTileCell(x - 1, y, setMaskBit4(west, EAST_4BIT)));
            }
            if (east != null) {
                mask += EAST_4BIT;
                changes.add(createTileCell(x + 1, y, setMaskBit4(east, WEST_4BIT)));
            }
            if (south != null) {
                mask += SOUTH_4BIT;
                changes.add(createTileCell(x, y - 1, setMaskBit4(south, NORTH_4BIT)));
            }
            changes.add(createTileCell(x, y, mask));
        }
        // cleared a tile
        else {
            if (north != null) {
                changes.add(createTileCell(x, y + 1, clearMaskBit4(north, SOUTH_4BIT)));
            }
            if (west != null) {
                changes.add(createTileCell(x - 1, y, clearMaskBit4(west, EAST_4BIT)));
            }
            if (east != null) {
                changes.add(createTileCell(x + 1, y, clearMaskBit4(east, WEST_4BIT)));
            }
            if (south != null) {
                changes.add(createTileCell(x, y - 1, clearMaskBit4(south, NORTH_4BIT)));
            }
        }
        return changes;
    }

    private List<TileCell> update8bit(int x, int y, TilemapLayer layer) {
        List<TileCell> changes = new ArrayList<>();

        Integer northwest = layer.get(x - 1, y + 1);
        Integer north     = layer.get(x    , y + 1);
        Integer northeast = layer.get(x + 1, y + 1);
        Integer southwest = layer.get(x - 1, y - 1);
        Integer south     = layer.get(x    , y - 1);
        Integer southeast = layer.get(x + 1, y - 1);
        Integer west      = layer.get(x - 1, y);
        Integer east      = layer.get(x + 1, y);
        Integer center    = layer.get(x    , y);

        // we painted a tile
        if (center != null) {
            int mask = 0;
            if (northwest != null && north != null && west != null) {
                mask += NORTHWEST_8BIT;
                // .O#  O - the checked tile
                // .#X  X - the painted tile
                // ...
                if ((west != null) && (north != null)) {
                    int bits = SOUTHEAST_8BIT + SOUTH_8BIT + EAST_8BIT;
                    changes.add(createTileCell(x - 1, y + 1, setMaskBit8(northwest, bits)));
                }
            }
            if (north != null) {
                mask += NORTH_8BIT;
                // #O#
                // #X#
                // ...
                int bits = SOUTH_8BIT;
                if ((west != null) && (northwest != null)) bits += (SOUTHWEST_8BIT + WEST_8BIT);
                if ((east != null) && (northeast != null)) bits += (SOUTHEAST_8BIT + EAST_8BIT);
                changes.add(createTileCell(x, y + 1, setMaskBit8(north, bits)));
            }
            if (northeast != null && north != null && east != null) {
                mask += NORTHEAST_8BIT;
                // .#O
                // .X#
                // ...
                if ((east != null) && (north != null)) {
                    int bits = SOUTHWEST_8BIT + SOUTH_8BIT + WEST_8BIT;
                    changes.add(createTileCell(x + 1, y + 1, setMaskBit8(northeast, bits)));
                }
            }
            if (west != null) {
                mask += WEST_8BIT;
                // .##
                // .OX
                // .##
                int bits = EAST_8BIT;
                if ((north != null) && (northwest != null)) bits += (NORTHEAST_8BIT + NORTH_8BIT);
                if ((south != null) && (southwest != null)) bits += (SOUTHEAST_8BIT + SOUTH_8BIT);
                changes.add(createTileCell(x - 1, y, setMaskBit8(west, bits)));
            }
            if (east != null) {
                mask += EAST_8BIT;
                // .##
                // .XO
                // .##
                int bits = WEST_8BIT;
                if ((north != null) && (northeast != null)) bits += (NORTHWEST_8BIT + NORTH_8BIT);
                if ((south != null) && (southeast != null)) bits += (SOUTHWEST_8BIT + SOUTH_8BIT);
                changes.add(createTileCell(x + 1, y, setMaskBit8(east, bits)));
            }

            if (southwest != null && south != null && west != null) {
                mask += SOUTHWEST_8BIT;
                // .#X
                // .O#
                // ...
                if ((west != null) && (south != null)) {
                    int bits = NORTHEAST_8BIT + NORTH_8BIT + EAST_8BIT;
                    changes.add(createTileCell(x - 1, y - 1, setMaskBit8(southwest, bits)));
                }
            }
            if (south != null) {
                mask += SOUTH_8BIT;
                // #X#
                // #O#
                // ...
                int bits = NORTH_8BIT;
                if ((west != null) && (southwest != null)) bits += (NORTHWEST_8BIT + WEST_8BIT);
                if ((east != null) && (southeast != null)) bits += (NORTHEAST_8BIT + EAST_8BIT);
                changes.add(createTileCell(x, y - 1, setMaskBit8(south, bits)));
            }
            if (southeast != null && south != null && east != null) {
                mask += SOUTHEAST_8BIT;
                // .X#
                // .#O
                // ...
                if ((east != null) && (south != null)) {
                    int bits = NORTHWEST_8BIT + NORTH_8BIT + WEST_8BIT;
                    changes.add(createTileCell(x + 1, y - 1, setMaskBit8(southeast, bits)));
                }
            }
            changes.add(createTileCell(x, y, maskBit8ToTile(mask)));
        }
        // cleared a tile
        else {

            if (northwest != null) {
                // .O#  O - the checked tile
                // .#X  X - the cleared tile
                // ...
                int set = 0;
                if (north != null) set += EAST_8BIT;
                if (west != null) set += SOUTH_8BIT;
                int clear = SOUTHEAST_8BIT + SOUTH_8BIT + EAST_8BIT;
                changes.add(createTileCell(x - 1, y + 1, updateMaskBit8(northwest, set, clear)));
            }
            if (north != null) {
                // #O#  O - the checked tile
                // #X#  X - the cleared tile
                // ...
                int set = 0;
                if (northwest != null) set += WEST_8BIT;
                if (northeast != null ) set += EAST_8BIT;
                int clear = SOUTH_8BIT + WEST_8BIT + SOUTHWEST_8BIT + EAST_8BIT + SOUTHEAST_8BIT;
                changes.add(createTileCell(x, y + 1, updateMaskBit8(north, set, clear)));
            }
            if (northeast != null) {
                // .#O  O - the checked tile
                // .X#  X - the cleared tile
                // ...
                int set = 0;
                if (north != null) set += WEST_8BIT;
                if (east != null) set += SOUTH_8BIT;
                int clear = SOUTHWEST_8BIT + SOUTH_8BIT + WEST_8BIT;
                changes.add(createTileCell(x + 1, y + 1, updateMaskBit8(northeast, set, clear)));
            }

            if (southwest != null) {
                // .#X  O - the checked tile
                // .O#  X - the cleared tile
                // ...
                int set = 0;
                if (south != null) set += EAST_8BIT;
                if (west != null) set += NORTH_8BIT;
                int clear = NORTHEAST_8BIT + NORTH_8BIT + EAST_8BIT;
                changes.add(createTileCell(x - 1, y - 1, updateMaskBit8(southwest, set, clear)));
            }
            if (south != null) {
                // #X#  O - the checked tile
                // #O#  X - the cleared tile
                // ...
                int set = 0;
                if (southwest != null) set += WEST_8BIT;
                if (southeast != null) set += EAST_8BIT;
                int clear = NORTH_8BIT + WEST_8BIT + NORTHWEST_8BIT + EAST_8BIT + NORTHEAST_8BIT;
                changes.add(createTileCell(x, y - 1, updateMaskBit8(south, set, clear)));
            }
            if (southeast != null) {
                // .X#  O - the checked tile
                // .#O  X - the cleared tile
                // ...
                int set = 0;
                if (south != null) set += WEST_8BIT;
                if (east != null) set += NORTH_8BIT;
                int clear = NORTHWEST_8BIT + NORTH_8BIT + WEST_8BIT;
                changes.add(createTileCell(x + 1, y - 1, updateMaskBit8(southeast, set, clear)));
            }

            if (west != null) {
                // ##.  O - the checked tile
                // OX.  X - the cleared tile
                // ##.
                int set = 0;
                if (northwest != null) set += NORTH_8BIT;
                if (southwest != null) set += SOUTH_8BIT;
                int clear = EAST_8BIT + NORTH_8BIT + NORTHEAST_8BIT + SOUTH_8BIT + SOUTHEAST_8BIT;
                changes.add(createTileCell(x - 1, y, updateMaskBit8(west, set, clear)));
            }

            if (east != null) {
                // ##.  O - the checked tile
                // XO.  X - the cleared tile
                // ##.
                int set = 0;
                if (northeast != null) set += NORTH_8BIT;
                if (southeast != null) set += SOUTH_8BIT;
                int clear = WEST_8BIT + NORTH_8BIT + NORTHWEST_8BIT + SOUTH_8BIT + SOUTHWEST_8BIT;
                changes.add(createTileCell(x + 1, y, updateMaskBit8(east, set, clear)));
            }
        }
        return changes;
    }

    private final List<TileCell> EMPTYLIST = new ArrayList<>();

    private List<TileCell> update(int x, int y, TilemapLayer layer) {
        String layerId = layer.getLayerId();
        if (layerId.endsWith("-auto4bit")) {
            return update4bit(x, y, layer);
        }
        else if (layerId.endsWith("-auto8bit")) {
            return update8bit(x, y, layer);
        }
        return EMPTYLIST;
    }

    public List<TileCell> onPaintTile(int x, int y, TilemapLayer layer) {
        return update(x, y, layer);
    }

    public List<TileCell> onClearTile(int x, int y, TilemapLayer layer) {
        return update(x, y, layer);
    }
}
