package sokoban;

import game.Game;
import game.audio.AudioPlayer;
import game.entities.Player;
import game.level.Level;
import sokoban.cells.*;

import java.util.Arrays;
import java.util.List;

public class Sokoban {
    Game game;
    Level level;
    private static int index = 0;

    public static final String LEVEL = "/levels/sokoban_test.png";

    static String q =
            "#######\n"+
                    "#*  # #\n"+
                    "# *   #\n"+
                    "# .$@ #\n"+
                    "# *   #\n"+
                    "#  *  #\n"+
                    "#######";
    static String w =
            "#######\n"+
                    "#.  # #\n"+
                    "#$* $ #\n"+
                    "#  $@ #\n"+
                    "# ..  #\n"+
                    "#  *  #\n"+
                    "#######";

    static String p =
            "           ###\n"+
                    "          #   #\n"+
                    "         #  *  #\n"+
                    "        #  *$*  #\n"+
                    "        # * * * #\n"+
                    "        #  *$*  #\n"+
                    "         #  *  #\n"+
                    "          #   #\n"+
                    "   ###     # #     ###\n"+
                    "  #   #   #   #   #   #\n"+
                    " #  *  # #  .  # #  *  #\n"+
                    "#  * *  #  . .  #  * *  #\n"+
                    "# *$*$*   .$+ .   *$*$* #\n"+
                    "#  * *  #  . .  #  * *  #\n"+
                    " #  *  # #  .  # #  *  #\n"+
                    "  #   #   #   #   #   #\n"+
                    "   ###     # #     ###\n"+
                    "          #   #\n"+
                    "         #  *  #\n"+
                    "        #  *$*  #\n"+
                    "        # * * * #\n"+
                    "        #  *$*  #\n"+
                    "         #  *  #\n"+
                    "          #   #\n"+
                    "           ###\n";

    private static List<String> boards = Arrays.asList(q, w, p);


    public Sokoban() {
        new Game();
    }


    public static Level init(String sokoban) {
        Level level = new Level(Sokoban.LEVEL);

        // bg musikk til sokoban
        level.addAudio(AudioPlayer.BG_MUSIC, false);


        String[] sokobanSplit = sokoban.split("\\||\\n");

        int sokoban_width = sokobanSplit[0].length();
        int sokoban_height = sokobanSplit.length;

        int x_level_centrum = (level.width << 3)/2;
        int y_level_centrum = (level.height << 3)/2;

//        System.out.printf("%s %s", x_level_centrum, y_level_centrum);

//        if (sokoban_height > 1) {
//            new LightPoint(level, x_level_centrum, y_level_centrum, sokoban_height << 3, 0x33);
//        }

        int xOrigo = (x_level_centrum-(sokoban_width << 3));
        int yOrigo = (y_level_centrum-(sokoban_height << 3));


        int x = xOrigo;
        int y = yOrigo;

        for (char cell : sokoban.toCharArray()) {
            switch (cell) {
                case '\n':
                case '|':
                    x = xOrigo;
                    y += 16;
                    continue;
                case ' ':
                    break;
                case '#':
                    new Wall(level, x, y);
                    break;
                case '.':
                    new Goal(level, x, y);
                    break;
                case '@':
                    new Player(level, x, y);
                    new Lantern(level, x+2, y+5);
                    break;
                case '$':
                    new Box(level, x, y);
                    break;
                case '*':
                    new Box(level, x, y);
                    new Goal(level, x, y);
                    break;
                case '+':
                    new Player(level, x, y);
                    new Goal(level, x, y);
                    new Lantern(level, x+2, y+5, 0);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("%s not allowed", cell));
            }
            x += 16;
        }
        return level;
    }

    public static void main(String[] args) {
        new Sokoban();
    }

    public static void deiterateIndex() {
        if (index == 0) {
            index = boards.size();
        }
        index--;
    }

    public static String nextBoard() {
        return boards.get((index++)%boards.size());
    }
}
