package game.entities;

import game.entities.projectiles.Projectile;
import game.entities.projectiles.Shooter;
import game.level.Level;
import game.level.tiles.Tile;

public abstract class Mob extends Entity {

    public int speed;
    protected int numSteps = 0;
    public boolean isMoving;
    protected int moveingDir = 1;
    protected int scale = 1;
    protected boolean pushable;

    public Mob(Level level, int x, int y, int speed) {
        super(level);
        this.speed = speed;
        this.x = x;
        this.y = y;
    }

    public void move(int xa, int ya) {
        if (xa != 0 && ya != 0) {
            move(xa, 0);
            move(0, ya);
            numSteps--;
            return;
        }

        numSteps++;
        if (!(this.solid && hasColided(xa, ya))) {
            if (ya < 0) moveingDir = 0;
            if (ya > 0) moveingDir = 1;
            if (xa < 0) moveingDir = 2;
            if (xa > 0) moveingDir = 3;

            x += xa*speed;
            y += ya*speed;
        }
    }

    protected boolean isSolidTile(int xa, int ya, int x, int y) {
        if (level == null)
            return false;
        Tile lastTile = level.getTile((this.x+x) >> 3, (this.y+y) >> 3);
        Tile newTile = level.getTile((this.x+x+xa) >> 3, (this.y+y+ya) >> 3);
        if (!lastTile.equals(newTile) && newTile.isSolid())
            return true;

        return false;
    }

    @Override
    public void isPushed(int x, int y) {
        if (pushable && solid) {
            move(x, y);
        }
    }

    public boolean hasColided(double xa, double ya) {
        return hasColided((int) xa, (int) ya);
    }

    public boolean hasColided(int xa, int ya) {
        int[] xPos = new int[]{dimentions[0], dimentions[1]};
        int[] yPos = new int[]{dimentions[2], dimentions[3]};

        for (int i = 0;i < yPos.length;i++) {
            for (int j = 0;j < xPos.length;j++) {
                if (isSolidTile(xa, ya, xPos[j], yPos[i]) || isSolidEntity(xa, ya, xPos[j], yPos[i])) {
                    return true;
                }
            }
        }
        return false;
    }


    public void isShot(Shooter shooter, Projectile projectile) {

    }
}















