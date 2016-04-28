import game.Game;

import java.applet.Applet;
import java.awt.*;
public class Launcher extends Applet {


    private static Game game = new Game();

    @Override
    public void init() {
        game.setVisible(true);
        setLayout(new BorderLayout());
        add(game, BorderLayout.CENTER);
        setMaximumSize(Game.DIMENSIONS);
        setMinimumSize(Game.DIMENSIONS);
        setPreferredSize(Game.DIMENSIONS);

        Game.isApplet = true;
    }

    @Override
    public void start() {
        game.start();
    }

    @Override
    public void stop() {
        game.stop();
    }

    public static void main(String[] args) {
        game.init();
    }
}
