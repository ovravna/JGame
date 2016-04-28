package game.level.tiles;

//import java.util.*;

import game.gfx.Screen;

import java.util.List;

public class BasicSolidTile extends BasicTile {

    public BasicSolidTile(int id, int x, int y,  int levelColor) {
        this(id, x, y, levelColor, Screen.defaultIgnoreColors);
    }

    public BasicSolidTile(int id, int x, int y, int levelColor, List<Integer> ignoreColors) {
        this(id, x, y,  levelColor, 8, ignoreColors);
    }

    public BasicSolidTile(int id, int x, int y, int levelColor, int block, List<Integer> ignoreColors) {
        super(id, x, y, levelColor, block, ignoreColors);
        this.solid = true;
    }
}
