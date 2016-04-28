package game.entities;


import game.InputObject;
import game.entities.enemies.Walker;
import game.entities.projectiles.Projectile;
import game.entities.projectiles.UltraRay;
import game.gfx.Screen;
import game.level.Level;
import sokoban.cells.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Entity implements Comparable{

    public int x, y;
    protected Level level;
    protected boolean solid;
    private static List<? super Entity> renderOrder =
            Arrays.asList(
                    Player.class,
                    Walker.class,
                    Lantern.class,
                    UltraRay.class,
                    Ball.class,
                    Box.class,
                    Goal.class,
                    Wall.class
            );

    /**
     * xMax, xMin, yMax, yMin
     */
    protected int[] dimentions = new int[4];
    private List<Class> notInOrder = new ArrayList<>();
    public Integer fillColor = null;


    public Entity(Level level) {
        init(level);
        if (!(this instanceof PlayerMP)) {
            level.addEntity(this);
        }
    }

    private final void init(Level level) {
        this.level = level;
    }

    public abstract void tick();

    public abstract void render(Screen screen);




    public boolean isSolid() {
        return solid;
    }



    protected boolean isSolidEntity(int x, int y) {
        return isSolidEntity(0, 0, x, y);
    }

    protected boolean isSolidEntity(int xa, int ya, int x, int y) {
        if (level == null) return false;

        x += this.x+xa;
        y += this.y+ya;

        for (Entity e : level.getAllEntities(x, y)) {
                if (e.isSolid() && !e.equals(this)) {

        //============ Rules for interaction ======

                    if (this instanceof Player && e instanceof Projectile) {
                        continue;
                    }

                    if (this instanceof Projectile && e instanceof Projectile) {
                        continue;
                    }

                    if (this instanceof InputObject && e instanceof Mob) {
                        e.isPushed(xa, ya);
                    }

                    if (this instanceof Projectile && e instanceof Player) {
                        return false;
                    }

                    if (this instanceof Projectile && e instanceof Mob) {
                        ((Mob) e).isShot(((Projectile) this).getShooter(), ((Projectile) this));
                    }


        //=========================================

                    return true;
                } else return false;
        }
        return false;
    }




    public boolean entityOn(int x, int y) {
        int dx = x-this.x;
        int dy = y-this.y;

        if (dx < dimentions[0] && dx >= dimentions[1] && dy < dimentions[2] && dy >= dimentions[3]) {
            return true;
        }
        return false;

    }

    public Entity getEntity(int x, int y) {
        List<Entity> entities = new ArrayList<>();


        for (int i = 0;i < level.entities.size();i++) {
            Entity e = level.entities.get(i);
            if (e.entityOn(x, y)) {
//                if (e instanceof Player) {
//                    return e;
//                }

                entities.add(e);
            }
        }

        if (entities.size() > 0) {
            if (entities.get(0).equals(this)) {
                for (Entity entity : entities) {
                    if (!entity.equals(this)) {
                        return entity;
                    }
                }
            }
            entities.sort(null);
            return entities.get(0);
        }
        return null;
    }

    public void removeEntity(Entity entity) {
        level.entities.remove(entity);
    }

    public void removeEntity(int x, int y) {
        Entity entity = getEntity(x, y);
        level.entities.remove(entity);

//        level.entities.remove(getEntity(x, y));
    }

    public void changePosition() {
        changePosition(0, -300);
    }

    public void changePosition(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public abstract void isPushed(int x, int y);

    @Override
    public int compareTo(Object o) {
        if (o instanceof Entity) {
//            if (!renderOrder.contains(o.getClass()) && !notInOrder.contains(o.getClass())) {
//                System.out.println("Add "+o.getClass().getSimpleName()+" to renderOrder");
//                notInOrder.add(o.getClass());
//            }
            return renderOrder.indexOf(o.getClass())-renderOrder.indexOf(this.getClass());
        } else return 0;
    }

}
