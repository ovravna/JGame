package game.entities.projectiles;

//import java.util.*;

import game.gfx.Screen;
import game.gfx.SpriteSheet;
import game.level.Level;

public class Bullet extends Projectile {

    private SpriteSheet sheet;

    private int aliveTime = 50;
    private int lenght = 10;
    private double rad = 0;

    public Bullet(Level level, int x, int y, int xv, int yv, Shooter shooter) {
        super(level, x, y, xv, yv, 4, shooter);
        this.damage = 100;
        this.aliveTime = 50;
        sheet = new SpriteSheet("/box16x16.png");
        solid = true;
        dimentions = new int[]{8, 4, 8, 4};
    }

    @Override
    public void tick() {

        if (!(solid && hasColided(xv, yv))) {
            x += xv*speed;
            y += yv*speed;
        } else if (!(getEntity(x, y) instanceof Projectile)) {
            rad += 0.8;
            aliveTime -= 8;
        }

        if (aliveTime <= 0) {
            level.lighting.removeLightSource(this);
            level.removeEntity(this);
        }

        aliveTime--;
    }

    @Override
    public void render(Screen screen) {

        // TODO: 26.04.2016 something wrong with render
        screen.render(((int) (x+xv*lenght))-3, ((int) (y+yv*lenght))-8, sheet, 2, 0, 1, 16, this);

        level.lighting.renderRoundLight(((int) (x+xv*lenght)), ((int) (y+yv*lenght)), ((int) rad), -0xaa, this);

    }

    @Override
    public void isPushed(int x, int y) {

    }
}
