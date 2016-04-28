package game.entities.projectiles;

//import java.util.*;

import game.entities.Entity;
import game.entities.Mob;

public interface Shooter {

    void shotHit(Entity victim);

    void shotKilled(Mob victim);
}
