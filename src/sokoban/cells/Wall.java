package sokoban.cells;

import game.entities.Mob;
import game.gfx.Screen;
import game.gfx.SpriteSheet;
import game.level.Level;

public class Wall extends Mob {

    private SpriteSheet sheet;

    public Wall(Level level, int x, int y) {
        super(level, x, y, 0);

        solid = true;
        pushable = false;
        dimentions = new int[] {16, 0, 16, 0};
        sheet = new SpriteSheet("/wall16x16.png");

    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Screen screen) {
        screen.render(x, y, sheet , 0, 0, 1, 16, this);

    }

    @Override
    public void isPushed(int x, int y) {

    }
}
