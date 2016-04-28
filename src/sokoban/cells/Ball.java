package sokoban.cells;

import game.entities.Mob;
import game.entities.Player;
import game.gfx.Colors;
import game.gfx.Light;
import game.gfx.Screen;
import game.gfx.SpriteSheet;
import game.level.Level;

import java.util.ArrayList;
import java.util.List;

public class Ball extends Mob {
    private SpriteSheet sheet = new SpriteSheet("/box16x16.png");
    private int color = Colors.get(555, 334, 445, -1);
    Player player;
    List<Integer> xPos = new ArrayList<>();
    List<Integer> yPos = new ArrayList<>();
    private boolean isHeld;


    public Ball(Level level, int x, int y) {
        super(level, x, y, level.getPlayer().speed);
        this.player = level.getPlayer();
        solid = true;

        dimentions = new int[]{16, 0, 16, 0};

    }


//    @Override
//    protected boolean hasColided(int xa, int ya) {
//        return xa == player.x || ya == player.y;
//    }

    int lastMove = 0;
    int lag = 50;

    @Override
    public void tick() {
        this.speed = player.speed;

        if (player.isMoving) {
            xPos.add(player.x);
            yPos.add(player.y);
            lastMove = 0;
            lag = 50;
        } else {
            if (lastMove < 30) {
                lag--;
            }

            lastMove++;
        }

        if (xPos.size() > lag && yPos.size() > lag) {

            int xTemp = xPos.get(xPos.size()-lag);
            int yTemp = yPos.get(yPos.size()-lag);
            x = xTemp;
            y = yTemp;

//            if (!hasColided(xTemp, yTemp)) {
//            }

            xPos = xPos.subList(xPos.size()-lag, xPos.size());
            yPos = yPos.subList(yPos.size()-lag, yPos.size());
        }


    }




    @Override
    public void render(Screen screen) {

//        screen.render(x,y,10 + 10 * 16,color,0,1);
        level.lighting.renderRoundLight(x, y, 60, 0x01, 6, 6, Light.SOFT, this);
        screen.render(x, y, sheet, 0, 0, 1, 16, this);


    }

    @Override
    public void isPushed(int x, int y) {
        return;
    }

}
