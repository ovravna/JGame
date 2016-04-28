package game.gfx;

//import java.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteSheet {

    public String path;
    public int width;
    public int height;

    public int[] pixels;
    public BufferedImage image;

    public int fontLine;
    public int playerLine;

    public SpriteSheet(String path) {

        try {
            image = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            image.flush();
            if (image == null) {
                return;
            }
        }

        this.path = path;
        this.width = image.getWidth();
        this.height = image.getHeight();

        pixels = image.getRGB(0, 0, width, height, null, 0, width);


//        for (int i : pixels) {
//            System.out.println(Integer.toHexString(i));
//        }


        for (int i = 0;i < pixels.length;i++) {
            pixels[i] = (pixels[i] & 0xffffff);
        }
    }

    public void setFontLine(int fontLine) {
        this.fontLine = fontLine;
    }

    public void setPlayerLine(int playerLine) {
        this.playerLine = playerLine;
    }

    public static void main(String[] args) {
        new SpriteSheet("/box8x8.png");
    }
}
