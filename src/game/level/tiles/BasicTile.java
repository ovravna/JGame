package game.level.tiles;

import game.gfx.Screen;
import game.level.Level;

import java.util.List;

import static java.util.Arrays.asList;

public class BasicTile extends Tile {

    protected int tileId;
    protected int tileColor;
    protected int block;


    public BasicTile(int id, int x, int y, int levelColor) {
        this(id, x, y, levelColor, asList(0x000000));
    }

    public BasicTile(int id, int x, int y, int levelColor, List<Integer> ignoreColors) {
        this(id, x, y,  levelColor, 8, ignoreColors);
    }

    public BasicTile(int id, int x, int y, int levelColor, int block, List<Integer> ignoreColors) {
        super(id, false, false, levelColor, ignoreColors);
        this.tileId = x+y*(128/block);
//        this.tileColor = tileColor;
        this.block = block;
    }


    @Override
    public void tick() {

    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        screen.render(x, y, tileId, 0, 1, block, ignoreColors, null);
    }
}
