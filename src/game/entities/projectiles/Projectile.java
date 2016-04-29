package game.entities.projectiles;

//import java.util.*;

import game.entities.Mob;
import game.level.Level;

public abstract class Projectile extends Mob {
    protected Shooter shooter;
    public int damage;

    protected double xv;
    protected double yv;

    protected int aliveTime = 50;


    public Projectile(Level level, int x, int y, int xv, int yv, int speed,  Shooter shooter) {
        super(level, x, y, speed);
        this.xv = xv;
        this.yv = yv;
        this.shooter = shooter;
    }

    public Shooter getShooter() {
        return shooter;
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

}
