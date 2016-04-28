package game.entities.enemies;

//import java.util.*;

import game.entities.Entity;
import game.entities.Player;
import game.entities.projectiles.Projectile;
import game.entities.projectiles.Shooter;
import game.gfx.Screen;
import game.level.Level;
import sokoban.cells.Actable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Walker extends Enemy implements Actable{

    private boolean isSwimming;
    private int xa;
    private int ya;
    private int tickCount;
    private int xp;
    private int yp;
    private static int speed = 1;
    private int life = 1000;
    private boolean killed;

    public Walker(Level level, int x, int y) {
        super(level, x, y, speed);
        solid = true;
        dimentions = new int[]{12, 0, 12, 0};
        damage = 20;
        level.screen.putColorMap(this, new HashMap<Integer, Integer>(){{
            put(0xffd2a8, 0x33aa00);
        }});
    }

    @Override
    public void tick() {


        if (sleep > 0) {
            sleep--;
            return;
        }

        xp = level.getPlayer().x;
        yp = level.getPlayer().y;

        double distanceSqrd = (xp-x)*(xp-x)+(yp-y)*(yp-y);


        if (Math.abs(distanceSqrd) < 40000 && tickCount%5 < 2) {
            if (Math.abs(xp-x) != 0) {
                xa = (xp-x)/Math.abs(xp-x);
            } else xa = 0;

            if (Math.abs(yp-y) != 0) {
                ya = (yp-y)/Math.abs(yp-y);
            } else ya = 0;
        } else {
            xa = 0;
            ya = 0;
        }


        if (xa != 0 || ya != 0) {
            move(xa, ya);
            isMoving = true;
        } else isMoving = false;

        if (level.getTile(this.x >> 3, this.y >> 3).getId() == 3) {
            isSwimming = true;
        }
        if (isSwimming && level.getTile(this.x >> 3, this.y >> 3).getId() != 3) {
            isSwimming = false;
        }

        if (life < 1500) {
            life++;
        }

        if (tickCount % 10 == 0) {
            act();
        }

        tickCount++;
    }

    @Override
    public void render(Screen screen) {
        int xTile = 0;
        int yTile = screen.sheet.playerLine;

        int walkingSpeed = 3;
        int flipTop = (numSteps >> walkingSpeed) & 1;
        int flipBottom;

        flipBottom = (numSteps >> walkingSpeed) & 1;

        if (moveingDir == 1) {
            xTile += 2;
        } else if (moveingDir > 1) {
            xTile += 4+((numSteps >> walkingSpeed) & 1)*2;

            flipTop = (moveingDir-1)%2;

        }

        int modifier = 8*scale;
        int xOffset = x-modifier/2;
        int yOffset = y-modifier/2-4;

        if (isSwimming) {
            List<Integer> waterColor = new ArrayList<>(Arrays.asList(0xfa05f0, 0x4444ff, 0x0000ff, 0x8888ff));

            yOffset += 4;
            if ((tickCount%60)/15 == 0) {
                waterColor.remove(1);
            } else if ((tickCount%60)/15 == 1) {
                yOffset--;
                waterColor.remove(2);
            } else if ((tickCount%60)/15 == 2) {
                waterColor.remove(3);
            } else {
                yOffset--;
                waterColor.remove(1);
                waterColor.remove(2);
            }

            screen.render(xOffset, yOffset+3, 6, 0, 1, 8, waterColor, null);
            screen.render(xOffset+8, yOffset+3, 6, 1, 1, 8, waterColor, null);

        }


        screen.render(xOffset+(modifier*flipTop), yOffset, xTile+yTile*(screen.sheet.width >> 3), flipTop, scale, this);

        screen.render(xOffset+modifier-(modifier*flipTop), yOffset, (xTile+1)+yTile*(screen.sheet.width >> 3), flipTop, scale, this);

        if (!isSwimming) {
            screen.render(xOffset+(modifier*flipBottom), yOffset+modifier, xTile+(yTile+1)*(screen.sheet.width >> 3), flipBottom, scale, this);

            screen.render(xOffset+modifier-(modifier*flipBottom), yOffset+modifier, (xTile+1)+(yTile+1)*(screen.sheet.width >> 3), flipBottom, scale, this);
        }
    }

    @Override
    public void isShot(Shooter shooter, Projectile projectile) {
        life -= projectile.damage;

        shooter.shotHit(this);

        if (life <= 0) {
            if (!killed) {
                shooter.shotKilled(this);
                killed = true;
            }
            level.removeEntity(this);
        }
    }


    @Override
    public void act() {
        List<Integer> dir = getDirection();

        int xp;
        int yp;


        for (int i = -5;i < 15;i++) {
            int xa = dir.get(0);
            int ya = dir.get(1);

            if (xa != 0) {
                xp = xa*i;
                yp = (int) (i*Math.random()-(i>>1));
            } else {
                xp = (int) (i*Math.random() - (i>>1));
                yp = ya*i;
            }


            List<Entity> entities = level.getAllEntities(this.x+3+xp, this.y+yp);

            for (Entity e : entities) {
                if (e instanceof Player) {
                    //  || e instanceof Lantern
                    ((Actable) e).actedOn(this);
                    sleep = 20;


                    return;
                }
            }


//                    getEntity(this.x+3+xp, this.y+yp);

//            if (entities != null && !(entities instanceof Walker) && entities instanceof Actable && entities instanceof InputObject) {
//                System.out.println(entities.getClass());
//                ((Actable) entities).actedOn(this);
//                return;
//            }
        }
    }

    @Override
    public void actedOn(Actable acter) {

    }
}
