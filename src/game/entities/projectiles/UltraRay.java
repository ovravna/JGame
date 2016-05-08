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
        super(level, x, y, xv, yv, 4,  shooter);
        this.damage = 80;
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
        } else {
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

//        screen.render(((int) (x+xv*lenght))-3, ((int) (y+yv*lenght)) -8, sheet, 0, 0, 1, 16, this);
        level.lighting.renderRoundLight(((int) (x+xv*lenght)), ((int) (y+yv*lenght)), (int) (rad*Math.random() + 1), -(int) (0xcccccc*Math.random()), this);

//        level.lighting.renderRoundLight(((int) (x+xv*lenght)), ((int) (y+yv*lenght)), 1, 0xbb, Light.SQUARE , this);

    }

    @Override
    public void isPushed(int x, int y) {

    }
}
