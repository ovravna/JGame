package game.level.tiles;

import java.util.List;

public class BasicHalfTile extends BasicSolidTile {
    private int direction;

    public BasicHalfTile(int id, int x, int y, int levelColor, int block, List<Integer> ignoreColors, int direction) {
        super(id, x, y, levelColor, block, ignoreColors);
        this.direction = direction;
        renderBackGround();
        
    }

    private void renderBackGround() {


    }
}
