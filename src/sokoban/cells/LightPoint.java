package sokoban.cells;

import game.entities.Mob;
import game.gfx.Light;
import game.gfx.Screen;
import game.level.Level;

public class LightPoint extends Mob {
    private int radius;
    private final Light light;
    private int filter;

    public boolean renderLight = true;

    public LightPoint(Level level, int x, int y, int filter, int radius) {
        this(level, x, y, filter, radius, Light.SOFT);
    }

    public LightPoint(Level level, int x, int y, int filter, int radius, Light light) {
        super(level, x, y, 0);

        this.filter = filter;
        this.radius = radius;
        this.light = light;
    }



    @Override
    public void tick() {

    }

    @Override
    public void render(Screen screen) {
        if (renderLight) {
            System.out.println("rendering light");
            level.lighting.renderRoundLight(x, y, radius, filter, light, this);
        }

    }

    @Override
    public void isPushed(int x, int y) {

    }
}
