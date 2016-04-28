package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();
    public Key act = new Key();
    public Key jump = new Key();
    public Key enter = new Key();
    public Key restart = new Key();
    public Key meta_data = new Key();
    public Key light = new Key();
    public Key span = new Key();


    public Key projectile = new Key();
    private Game game;

    public InputHandler(Game game) {
        this.game = game;
        game.addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        toggleKey(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        toggleKey(e.getKeyCode(), false);

    }


    public void toggleKey(int keyCode, boolean isPressed) {
        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
            up.toggle(isPressed);
        }
        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
            down.toggle(isPressed);
        }
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            left.toggle(isPressed);
        }
        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            right.toggle(isPressed);
        }
        if (keyCode == KeyEvent.VK_E) {
            act.toggle(isPressed);
        }

        if (keyCode == KeyEvent.VK_SPACE) {
            jump.toggle(isPressed);
        }

        if (keyCode == KeyEvent.VK_ENTER) {
            enter.toggle(isPressed);
        }

        if (keyCode == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }

        if (keyCode == KeyEvent.VK_F3) {
            Game.META_DATA = true;
        }

        if (keyCode == KeyEvent.VK_F4) {
            Game.META_DATA = false;
        }

        if (keyCode == KeyEvent.VK_R) {
            restart.toggle(isPressed);
        }

        if (keyCode == KeyEvent.VK_F2 || keyCode == KeyEvent.VK_M) {
            meta_data.toggle(isPressed);
        }

        if (keyCode == KeyEvent.VK_L) {
            light.toggle(isPressed);
        }

        if (keyCode == KeyEvent.VK_Q || keyCode == KeyEvent.VK_PERIOD) {
            projectile.toggle(isPressed);
        }

        if (keyCode == KeyEvent.VK_Z) {
            span.toggle(isPressed);
        }


    }

    public class Key {
        private boolean pressed;
        private boolean lastPressed;
        private int numTimesPressed = 0;

        public boolean isPressed() {
            return pressed;
        }

        public boolean isReleased() {
            if (lastPressed && pressed != lastPressed) {
                lastPressed = pressed;
                return true;
            }

            lastPressed = pressed;

            return false;
        }

        public boolean isToggled() {
            if (pressed && pressed != lastPressed) {
                lastPressed = pressed;
                return true;
            }

            lastPressed = pressed;

            return false;
        }

        public int getNumTimesPressed() {
            return numTimesPressed;
        }

        public void toggle(boolean isPressed) {
            pressed = isPressed;
            if (isPressed) numTimesPressed++;
        }
    }


}
