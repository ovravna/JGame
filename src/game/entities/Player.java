package game.entities;

//import java.util.*;

import game.InputHandler;
import game.InputManager;
import game.InputObject;
import game.entities.enemies.Enemy;
import game.entities.enemies.Walker;
import game.entities.projectiles.*;
import game.gfx.Screen;
import game.level.Level;
import sokoban.cells.Actable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Player extends Mob implements Actable, InputObject, Shooter {

    private boolean colorFlag;
    private InputHandler input;
    private int scale = 1;
    protected boolean isSwimming = false;
    private int tickCount = 0;
    private String username;
    private int xa, ya;
    private boolean isJumping;
    private int jumpTime;
    private boolean shoot;
    private double rad;
    private Projectiles projectileType;
    private boolean hasKilled;
    private Mob victim;
    private int xOffset;
    private int yOffset;
    private int life = 1000;

    private static final int INITIAL_HITTIME = 10;
    private boolean hit;
    private List<Integer> hitDirection;
    private int hitTime = INITIAL_HITTIME;

    protected boolean isInputObject;


    public Player(Level level, int x, int y) {
        this(level, x, y, true);
    }

    public Player(Level level, int x, int y, boolean isInputObject) {
        super(level, x, y, 1);
        this.isInputObject = isInputObject;


        if (isInputObject) {
            InputManager.addInputObject(this);
        }
        solid = true;
        dimentions = new int[]{7, 0, 7, 3};

        if (level.screen != null) {
            level.screen.putColorMap(this, new HashMap<Integer, Integer>() {{
                put(0xc0a1ff, 0xff0000);
            }});
        } else colorFlag = true;

        projectileType = Projectiles.ULTRARAY;
    }

    @Override
    public void setInputHandler(InputHandler input) {
        this.input = input;
    }

    int i = 0;

    @Override
    public void tick() {
        if (input == null) return;

        if (life <= 0) {
            input.restart.toggle(true);
        }


        if (hit) {
            move(hitDirection.get(0)*2, hitDirection.get(1)*2);
            hitTime--;

            if (tickCount%3 < 1) {
                fillColor = 0xffffff;
            } else fillColor = null;

            if (hitTime <= 0) {
                hit = false;
                fillColor = null;
            }

            return;
        }

        if (!hit && hitTime != INITIAL_HITTIME) {

            if (tickCount%3 < 1) {
                fillColor = 0xffffff;
            } else fillColor = null;

            hitTime++;
        } else fillColor = null;

        shoot = false;

        xa = 0;
        ya = 0;

        if (input != null) {
            if (input.up.isPressed()) ya--;
            if (input.down.isPressed()) ya++;
            if (input.left.isPressed()) xa--;
            if (input.right.isPressed()) xa++;
            if (input.act.isToggled()) act();
            if (input.jump.isToggled()) {

                i++;
                i %= 2;

                isJumping = true;
                solid = false;
                jumpTime = tickCount;
            }
            if (input.span.isToggled()) {
                new Walker(level, (int) (100*Math.random()), (int) (100*Math.random()));
            }

            if (input.projectile.isPressed()) {
                shoot = true;
            }
        } else {
            System.err.println("Player input is null");
        }

        if (xa != 0 || ya != 0) {
            move(xa, ya);
            isMoving = true;
        } else isMoving = false;

        if (level.getTile(this.x >> 3, this.y >> 3).getId() == 3 && !isJumping) {
            isSwimming = true;
        }
        if (isSwimming && level.getTile(this.x >> 3, this.y >> 3).getId() != 3) {
            isSwimming = false;
        }

        if (shoot && tickCount % 3 != 0) {
            List<Integer> dir = getDirection();
            projectileType = Projectiles.values()[i];

            switch (projectileType) {
                case ULTRARAY:
                    new UltraRay(level, x, y, dir.get(0), dir.get(1), this);
                    break;
                case BULLET:
//                    if (tickCount % 2 == 0)
                        new Bullet(level, x, y, dir.get(0), dir.get(1), this);
                    break;
            }
        }
        if (rad > 5) {
            rad *= 0.99;
        } else if (rad > 0) {
            rad -= 0.05;
        }


        tickCount++;
    }

    private List<Integer> getDirection() {
        int x;
        int y;
        if (moveingDir/2 == 0) {
            y = moveingDir%2 == 0 ? -1:1;
            x = 0;
        } else {
            y = 0;
            x = moveingDir%2 == 0 ? -1:1;
        }

        return Arrays.asList(x, y);

    }

    @Override
    public void act() {
        List<Integer> dir = getDirection();
        int xa = dir.get(0);
        int ya = dir.get(1);


        int xp;
        int yp;

        for (int i = 1;i < 10;i++) {
            if (xa != 0) {
                xp = xa*i;
                yp = (int) (i*Math.random()-(i/2));
            } else {
                xp = (int) (i*Math.random()-(i/2));
                yp = ya*i;
            }

            List<Entity> entities = level.getAllEntities(this.x+3+xp, this.y+yp);

            for (Entity e : entities) {
                if (!e.equals(this) && e instanceof Actable) {
                    ((Actable) e).actedOn(this);
                    return;
                }
            }
        }
    }

    @Override
    public void actedOn(Actable acter) {
        if (acter instanceof Enemy) {
            life -= ((Enemy) acter).damage;
            hitDirection = ((Enemy) acter).getDirection();

            hit = true;
        }
    }

    @Override
    public void render(Screen screen) {
        int xTile = 0;
        int yTile = screen.sheet.playerLine;

        int walkingSpeed = 3;
        int flipTop = (numSteps >> walkingSpeed) & 1;
        int flipBottom;

        if (colorFlag) {
            System.out.println("Color map added in render");
            level.screen.putColorMap(this, new HashMap<Integer, Integer>() {{
                put(0xc0a1ff, 0xff0000);
            }});
            colorFlag = false;
        }


        if (!isJumping && !hit) {
            flipBottom = (numSteps >> walkingSpeed) & 1;
        } else flipBottom = 1;

        if (moveingDir == 1) {
            xTile += 2;
        } else if (moveingDir > 1) {
            xTile += 4+((numSteps >> walkingSpeed) & 1)*2;

            flipTop = (moveingDir-1)%2;

            if ((isJumping || hit) && xTile == 4) {
                xTile = 6;
            }
        }

        int modifier = 8*scale;
        xOffset = x-modifier/2;
        yOffset = y-modifier/2-4;

        if (isJumping && !isSwimming) {
            yOffset -= 6;
            if (tickCount-jumpTime > 20) {
                isJumping = false;
                solid = true;
            }
        }



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
            screen.render(xOffset+8, yOffset+3, 6, 1, 1, 8 , waterColor, null);

        }

//        if (rad != 0) {
//            level.lighting.renderRoundLight(xOffset+6 , yOffset - 14, (int) rad, -0xff, this);
//        }

        if (hasKilled) {

            if (tickCount%60 == 0) {
                hasKilled = false;
            }
        }


        screen.render(xOffset+(modifier*flipTop), yOffset, xTile+yTile*(screen.sheet.width >> 3), flipTop, scale, this);

        screen.render(xOffset+modifier-(modifier*flipTop), yOffset, (xTile+1)+yTile*(screen.sheet.width >> 3), flipTop, scale, this);

        if (!isSwimming || isJumping) {
            screen.render(xOffset+(modifier*flipBottom), yOffset+modifier, xTile+(yTile+1)*(screen.sheet.width >> 3), flipBottom, scale, this);

            screen.render(xOffset+modifier-(modifier*flipBottom), yOffset+modifier, (xTile+1)+(yTile+1)*(screen.sheet.width >> 3), flipBottom, scale, this);
        }


//        if (username != null) {
//            Font.render(username, screen, xOffset- ((username.length()-1) / 2 * 8),
//                    yOffset-10, Colors.get(-1, -1, -1, 555), 1);
//        }

    }

    @Override
    public void isPushed(int x, int y) {
    }

    @Override
    public void shotHit(Entity victim) {
//        System.out.println("Shot hit");

    }

    @Override
    public void shotKilled(Mob victim) {
        this.victim = victim;
//        System.out.println("Shot KILLED");
        rad += 3.5;

        hasKilled = true;
        new InfoShot(level, x, y-15, this);


    }

}
