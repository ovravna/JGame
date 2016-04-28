package game.entities.enemies;

//import java.util.*;

import game.entities.Mob;
import game.level.Level;

import java.util.Arrays;
import java.util.List;

public abstract class Enemy extends Mob {
    public int damage;
    protected int sleep;


    public Enemy(Level level,  int x, int y, int speed) {
        super(level, x, y, speed);
    }


    public List<Integer> getDirection() {
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
}
