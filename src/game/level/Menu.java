package game.level;

import game.InputHandler;
import game.InputManager;
import game.InputObject;
import game.audio.AudioPlayer;
import game.gfx.Screen;
import game.level.tiles.Background;
import sokoban.Sokoban;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static game.Game.*;

public class Menu extends Level implements InputObject {
    private InputHandler input;
    private Background bg;
    private BufferedImage bgImage;


    private Color titleColor;
    private Color fontColor1;
    private Color fontColor2;


    private Font titleFont;
    private Font font;

    private String[] options = {
            "Start",
            "Test",
            "Make Board",
            "Help",
            "Quit"
    };
    private int currentChoice;
    private int fontOffset = 0;

    private boolean moveFont;
    private boolean showHelp;
    private boolean hideHelp;


    public Menu(LevelManager levelManager, String imagePath){
        super(levelManager, imagePath);
        InputManager.addInputObject(this);
        this.isRenderingLight(false);

//        bg = new Background("/backgrounds/menubg.gif", 0);

        titleColor = new Color(246, 198, 77);
        fontColor1 = new Color(245, 221, 128);
        fontColor2 = new Color(255, 136, 18);

        addAudio(AudioPlayer.MENUOPTION);
        addAudio(AudioPlayer.MENUSELECT);


        titleFont = new Font("Old English Text MT", Font.BOLD, 120);

        font = new Font("OCR A EXTENDED", Font.PLAIN, 80);

        try {
            bgImage = ImageIO.read(getClass().getResourceAsStream("/backgrounds/menubg.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bgImage.flush();
            if (bgImage == null) {
                return;
            }
        }


//        player = new Player(this, 0, 0);
    }

    private int offsetVal = 1;

    @Override
    public void tick() {
        super.tick();
        int MOVESPEED = 2;
        int INIT_OFFSET = 1;

        if (moveFont) {
            fontOffset -= (offsetVal*= MOVESPEED);
        }

        if (hideHelp) {
            if (offsetVal >= 512) {
                offsetVal = INIT_OFFSET;
            }
            fontOffset += (offsetVal *= MOVESPEED);
            if (fontOffset > 0) {
                showHelp = false;
                hideHelp = false;
                offsetVal = INIT_OFFSET;
            }
        }


        if (bg != null) {
            bg.update();
        }

        if (input.up.isToggled()) {

            AudioPlayer.MENUOPTION.play();


            if (!showHelp) {
                currentChoice--;
                if (currentChoice == -1) {
                    currentChoice = options.length-1;
                }
            }
        }

        if (input.down.isToggled()) {
            AudioPlayer.MENUOPTION.play();
            if (!showHelp) {
                currentChoice++;
                if (currentChoice == options.length) {
                    currentChoice = 0;
                }
            }
        }


        if (input.enter.isReleased()) {
            AudioPlayer.MENUSELECT.play();
            select();
        }
    }

    @Override
    public void draw(Graphics g, Screen screen) {
        if (bg != null) {
            bg.draw(g);
        }

        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, WIDTH*SCALE+30, HEIGHT*SCALE+30, null);
        }


        String title = "Sokoban";
        g.setColor(titleColor);
        g.setFont(titleFont);
        g.drawString(title, 350, 200);


        g.setFont(font);
        for (int i = 0;i < options.length;i++) {
            if (i == currentChoice) {
                g.setColor(fontColor2);
            } else {
                g.setColor(fontColor1);
            }
            g.drawString(options[i], 600-options[i].length()*20+fontOffset, 530+i*80);

            if (moveFont && 600-options[i].length()*20+fontOffset < -200) {
                moveFont = false;
            }
        }

        if (showHelp) {
            String[] help = new String[] {
                    "Push boxes",
                    "Go crazy",
                    "Win life",
                    "",
                    "Got it!" };

            for (int i = 0; i < help.length; i++) {
                if (i == help.length-1) {
                    g.setColor(fontColor2);
                    g.drawString(help[i], 1600-help[i].length()*20+fontOffset, 530+i*80);
                } else {
                    g.setColor(fontColor1);
                    g.drawString(help[i], 1300+fontOffset, 530+i*80);

                }
            }
        }
    }

    private void select() {
        if (showHelp) {
            hideHelp = true;
//            levelManager.loadLevel(Levels.MENU);
            return;
        }

        if (currentChoice == 0) {
            levelManager.loadLevel(Levels.NEXT);
        }

        if (currentChoice == 1) {
            levelManager.loadLevel(Levels.TEST);
        }

        if (currentChoice == 2) {
            // Add board
            String board = JOptionPane.showInputDialog("Enter Board");
            if (board != null) {
                levelManager.loadLevel(Sokoban.init(board));
            }
        }

        if (currentChoice == 3) {
            moveFont = true;
            showHelp = true;
        }

        if (currentChoice == 4) {
            System.exit(0);
        }
    }

    @Override
    public void setInputHandler(InputHandler input) {
        this.input = input;
    }
}
