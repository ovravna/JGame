package game.level;

import game.Game;
import game.audio.AudioPlayer;
import game.entities.Entity;
import game.entities.Player;
import game.gfx.Drawer;
import game.gfx.Screen;
import game.level.tiles.Tile;
import sokoban.cells.Goal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Level {

    public Screen screen;
    private byte[] tiles;
    public int width;
    public int height;
    protected LevelManager levelManager;
    private String imagePath;
    private BufferedImage image;
    private Player player;
    public Lighting lighting;

    public static HashMap<Integer, Tile> tileColors = new HashMap<>();

    private Stack<Entity> removeStack = new Stack<>();
    public List<Entity> entities = new ArrayList<>();
    public List<Goal> goals = new ArrayList<>();
    public List<AudioPlayer> audios = new ArrayList<>();
    private AudioPlayer initAudio;
    private List<Drawer> drawers = new ArrayList<>();


    public Level(String imagePath) {
        this(null, imagePath);
    }

    public Level(LevelManager levelManager, String imagePath) {
        System.out.println(imagePath);
        this.levelManager = levelManager;
        
        if (levelManager != null) {
            this.screen = levelManager.screen;
        }

        this.imagePath = imagePath;

        if (levelManager != null) {
            lighting = new Lighting(levelManager.screen);
        }

        if (imagePath != null) {
            loadImage();
        } else {
            this.width = 64;
            this.height = 64;
            this.tiles = new byte[width*height];
            this.generateLevel();
        }
    }

    private void loadImage() {
        try {
            this.image = ImageIO.read(Level.class.getResource(this.imagePath));
            this.width = image.getWidth();
            this.height = image.getHeight();
            tiles = new byte[width*height];

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLevel() {
        System.err.println("Audio "+initAudio);
        if (initAudio != null && !initAudio.isRunning()) {
            initAudio.play();
            System.err.println("Audio loaded");
        } else if (initAudio != null && initAudio.isRunning()) {
            System.out.println("Previous audio not unloaded");
        }

        this.loadTiles();
    }

    private void loadTiles() {
        int[] tileColor = this.image.getRGB(0, 0, width, height, null, 0, width);

        for (int y = 0;y < height;y++) {
            for (int x = 0;x < width;x++) {
                if (tileColors.containsKey(tileColor[x+y*width])) {
                    this.tiles[x+y*width] = tileColors.get(tileColor[x+y*width]).getId();
                }
            }
        }
    }

    private void saveLevelToFile() {
        try {
            ImageIO.write(image, "png", new File(Level.class.getResource(this.imagePath).getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void alterTile(int x, int y, Tile newTile) {
        this.tiles[x+y*width] = newTile.getId();
        image.setRGB(x, y, newTile.getLevelColor());
    }

    public void generateLevel() {
        for (int y = 0;y < height;y++) {
            for (int x = 0;x < width;x++) {
                if (x * y % 10 < 7) {
                    tiles[x+y*width] = Tile.GRASS.getId();
                } else {
                    tiles[x+y*width] = Tile.STONE.getId();
                }
            }
        }
    }

    public void renderTiles(Screen screen, int xOffset, int yOffset) {
        if (lighting == null) {
            if (levelManager == null) {
                System.out.println("Level: LevelManager is null");
            }
            System.out.println("Level: Lighting is null");

            lighting = new Lighting(levelManager.screen);
//            screen.setLighting(lighting);
        }

        if (xOffset < 0) {
            xOffset = 0;
        }
        if (xOffset > (width << 3)-screen.width) {
            xOffset = (width << 3)-screen.width;
        }
        if (yOffset < 0) {
            yOffset = 0;
        }
        if (yOffset > (height << 3)-screen.height) {
            yOffset = (height << 3)-screen.height;
        }

        screen.setOffset(xOffset, yOffset);

        for (int y = (yOffset >> 3);y < (yOffset+screen.height >> 3)+1;y++) {
            for (int x = (xOffset >> 3);x < (xOffset+screen.width >> 3)+1;x++) {
                getTile(x, y).render(screen, this, x << 3, y << 3);
            }
        }
    }

    public void renderEntities(Screen screen) {
        entities.sort(null);
        try {
            entities.forEach(entity -> entity.render(screen));
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }


    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return Tile.VOID;
        return Tile.tiles[tiles[x+y*width]];
    }

    public Entity getEntity(int x, int y) {
        List<Entity> entities = getAllEntities(x, y);
        if (entities.size() > 0) {
            entities.sort(null);
            return entities.get(0);
        }
        return null;
    }

    public List<Entity> getAllEntities(int x, int y) {
        return entities.stream().filter(n -> n.entityOn(x, y)).collect(Collectors.toList());

    }

    public void setFilterColor(int filterColor) {
        lighting.setFilterColor(filterColor);
    }

    public void tick() {
        Game.entities = entities.size();

        clearRemoveStack();

        try {
            entities.forEach(Entity::tick);
        } catch (ConcurrentModificationException ignored) {
        } finally {
            if (!removeStack.isEmpty()) {
                for (Entity entity : removeStack) {
                    entities.remove(entity);
                }
            }
        }
        for (Tile t : Tile.tiles) {
            if (t == null) break;
            t.tick();
        }

    }

    public Player getPlayer() {
        return player;
    }

    public void addEntities(Entity entity) {
        if (entity instanceof Player) this.player = (Player) entity;
        this.entities.add(entity);
    }

    public void draw(Graphics g, Screen screen) {
        for (Drawer d : drawers) {
            d.draw(g, screen);
        }
    }


    public void addManager(LevelManager levelManager) {
        this.levelManager = levelManager;
        this.screen = levelManager.screen;
        System.out.println("Level: "+levelManager);

        if (lighting == null) {
            lighting = new Lighting(this.levelManager.screen);
        }
    }

    public void isRenderingLight() {
        if (lighting != null) {
            isRenderingLight(!lighting.renderLight);
        }
    }

    public void isRenderingLight(boolean renderLight) {
        if (lighting != null) {
            lighting.renderLight = renderLight;
        } else
            System.out.println("Level.isRenderingLight: lighting = null");
    }

    public void removeEntity(Entity entity) {
        lighting.removeLightSource(entity);
        removeStack.push(entity);
    }

    public void clearRemoveStack() {
        if (!removeStack.isEmpty()) {
            for (Entity entity : removeStack) {
                entities.remove(entity);
            }
        }
    }

    public void addAudio(AudioPlayer audio) {
        addAudio(audio, false);
    }

    public void addAudio(AudioPlayer audio, boolean playOnInit) {
        if (playOnInit) {
            this.initAudio = audio;
        }
        audios.add(audio);
    }

    public void unload() {
        for (AudioPlayer audioPlayer : audios) {
            audioPlayer.stop();
        }

    }

    public void addDrawer(Drawer drawer) {
        drawers.add(drawer);

    }
}


