package sokoban.cells;

import game.entities.Entity;
import game.entities.Mob;
import game.gfx.Screen;
import game.gfx.SpriteSheet;
import game.level.Level;

public class Goal extends Mob {

    private final SpriteSheet goalSheet;
    private boolean isFilled;
    private boolean isWon;

    public Goal(Level level, int x, int y) {
        super(level, x, y, 0);
        goalSheet = new SpriteSheet("/box16x16.png");

        solid = false;
        pushable = false;
        dimentions = new int[]{10, 6, 10, 6};
        level.goals.add(this);

    }

    public void setFilled() {
        isFilled = true;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public boolean isWon() {
        return isWon;
    }

    @Override
    public void tick() {
//        System.out.println(goals.size());
//        System.out.println(goals.stream().allMatch(Goal::isFilled));
        if (isFilled && level.goals.stream().allMatch(Goal::isFilled) && !isWon) {
            isWon = true;

            for (Entity entity: level.entities) if (entity instanceof Wall)
                    level.removeEntity(entity);
            level.clearRemoveStack();

        }

        isFilled = false;
    }

    @Override
    public void render(Screen screen) {
        screen.render(x, y, goalSheet, 0, 0, 1, 16, this);
    }

    @Override
    public void isPushed(int x, int y) {

    }
}
