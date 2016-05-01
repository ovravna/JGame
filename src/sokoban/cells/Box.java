package sokoban.cells;

import game.entities.Entity;
import game.entities.Mob;
import game.gfx.Light;
import game.gfx.Screen;
import game.gfx.SpriteSheet;
import game.level.Level;

public class Box extends Mob {

    private SpriteSheet sheet = new SpriteSheet("/wall16x16.png");
    private boolean renderLight;

    public Box(Level level, int x, int y) {
        super(level, x, y, 1);
        solid = true;
        pushable = true;
        dimentions = new int[]{14, 1, 14, 1};


    }


    @Override
    public void tick() {
        if (level.getPlayer() == null) return;
        Entity entity = getEntity(x+8, y+8);

        if (entity instanceof Goal) {
            renderLight = true;
            ((Goal) entity).setFilled();

        } else renderLight = false;


    }

    @Override
    public void render(Screen screen) {
        screen.render(x, y, 5 + 5*8, 0, 1, 16, this);

        if (renderLight) {
            level.lighting.renderRoundLight(x + 4, y + 7 , 6, -0x88, Light.SOFT, this);
        } else level.lighting.removeLightSource(this);

    }

    @Override
    public void isPushed(int x, int y) {

        move(x, y);
    }

}
