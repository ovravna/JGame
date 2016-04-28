package game.entities.projectiles;

//import java.util.*;

import game.gfx.Screen;
import game.gfx.SpriteSheet;
import game.level.Level;

public class InfoShot extends Projectile {


    private final SpriteSheet sheet;


    public InfoShot(Level level, int x, int y, Shooter shooter) {
        super(level, x, y, 0, -1, 2, shooter);
        aliveTime = 15;
        xv = 1.8*Math.random()-0.9;
        sheet = new SpriteSheet("/target8x8.png");
        this.x-=16;
    }

    @Override
    public void tick() {

        if (!(solid && hasColided(xv, yv))) {
            x += xv*speed;
            y += yv*speed;
        }

        if (aliveTime <= 0) {
            level.removeEntity(this);
        }

        aliveTime--;
    }


    @Override
    public void render(Screen screen) {
//        level.lighting.renderRoundLight(x, y, aliveTime, 0, this);
        screen.render(x, y, sheet, 0, 0, 1, 8, this);
        screen.render(x+8, y, sheet, 1, 0, 1, 8, this);
        screen.render(x+16, y, sheet, 2, 0, 1, 8, this);
        screen.render(x+24, y, sheet, 3, 0, 1, 8, this);
    }
}
