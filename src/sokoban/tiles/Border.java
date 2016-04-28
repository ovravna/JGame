package sokoban.tiles;

import game.level.tiles.BasicTile;

import java.util.List;

public class Border extends BasicTile {


    public Border(int id, int x, int y, int tileColor, int levelColor, List<Integer> ignoreColors) {
        super(id, x, y, tileColor, levelColor, ignoreColors);

    }
}
