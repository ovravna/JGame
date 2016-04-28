package game.audio;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class Test {

    public static void main(String[] args) {
        new Test().play();
    }

    public void play() {
        URL thing = getClass().getResource("/SFX/menuoption.mp3");
        Media audioFile = new Media(thing.toString());
        try {
            MediaPlayer player = new MediaPlayer(audioFile);
            player.play();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

}
