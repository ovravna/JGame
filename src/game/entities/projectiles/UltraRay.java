package game.entities.projectiles;

//import java.util.*;

import game.gfx.Screen;
import game.gfx.SpriteSheet;
import game.level.Level;

public class UltraRay extends Projectile {

    private final SpriteSheet sheet;

    private int lenght = 10;
    private double rad = 2;

    public UltraRay(Level level, int x, int y, int xv, int yv, Shooter shooter) {
        super(level, x, y, xv, yv,  3,  shooter);
        this.damage = 40;
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
            rad += 0.5;
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

        screen.render(((int) (x+xv*lenght)), ((int) (y+yv*lenght)), sheet, 0, 0, 0, 16, this);
        level.lighting.renderRoundLight(((int) (x+xv*lenght)), ((int) (y+yv*lenght)), (int) (rad*Math.random() + 1), -(int) (0xcccccc*Math.random()), this);

    }

    @Override
    public void isPushed(int x, int y) {

    }
}
