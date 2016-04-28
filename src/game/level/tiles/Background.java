package game.level.tiles;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import static game.Game.*;

public class Background {

    private BufferedImage image;

    private double x;
    private double y;
    private double dx;
    private double dy;

    private double moveScale;

    public Background(String s, double ms) {

        try {
            image = ImageIO.read(getClass().getResourceAsStream(s));
//            image = ImageIO.read(new File(s));
            moveScale = ms;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setPosition(double x, double y) {
        this.x = (x*moveScale)%WIDTH;
        this.y = (y*moveScale)%HEIGHT;
    }

    public void setVector(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void update() {
        x += dx;
        y += dy;
    }

    public void draw(Graphics g) {

        g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE,null);

        if (x < 0) {
            g.drawImage(image, (int) x+WIDTH, (int) y, WIDTH*SCALE, HEIGHT*SCALE, null);
        }
        if (x > 0) {
            g.drawImage(image, (int) x-WIDTH, (int) y, WIDTH*SCALE, HEIGHT*SCALE, null);
        }
    }

}







