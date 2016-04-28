package game.gfx;

//import java.util.*;

public class Font {

    private static String chars = "0123456789:;<=>? ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]"+
            "^_'abcdefghijklmnopqrstuvwxyz{|}~  !#$%&'()*+,-./";

    public static void render(String msg, Screen screen, int x, int y, int color, int scale) {

        for (int i = 0;i < msg.length();i++) {
            int charIndex = chars.indexOf(msg.charAt(i));
            if (charIndex >= 0) {
                screen.render(x+(i*8), y, charIndex + screen.sheet.fontLine*(screen.sheet.width >> 3), 0x00, scale, null);
            }
        }
    }
}


// screen.sheet.fontLine*(screen.width >> 3)