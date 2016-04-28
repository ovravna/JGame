package game.level;

import game.Game;
import game.InputHandler;
import game.InputManager;
import game.entities.PlayerMP;
import game.entities.enemies.Walker;
import game.gfx.Screen;
import game.net.GameClient;
import game.net.GameServer;
import game.net.packets.Packet00Login;
import sokoban.Sokoban;
import sokoban.cells.Goal;
import sokoban.cells.Lantern;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static game.level.Levels.MENU;

public class LevelManager extends InputManager {
    public  Screen screen;
    private GameServer server;
    private GameClient client;
    private List<Level> levels = new ArrayList<>();
    private Levels currentLevelType;
    private Level currentLevel;
    private int smoothRise;
    private int cycleTime = 20;
    private boolean wonFlag;
    private Graphics g;
    public Level testLevel;


    public LevelManager(Screen screen, InputHandler input) {
        this(screen, input, null, null, null);
    }

    public LevelManager(Screen screen, InputHandler input, Level level, GameServer server, GameClient client) {
        this.screen = screen;
        this.server = server;
        this.client = client;
        //kake er godt

        this.server.setLevelManager(this);
        this.client.setLevelManager(this);

        addLevel(level);
        super.setInput(input);
        currentLevelType = MENU;
        System.out.println("LevelManger initilized");
        loadLevel(currentLevelType);
        testLevel = new Level(this, Sokoban.LEVEL);
    }

    public void loadLevel(Level level) {
        currentLevel = level;

        if (currentLevel.levelManager == null) {
            currentLevel.addManager(this);
        }

//        currentLevel.loadLevel();
    }


    public void loadLevel() {
        loadLevel(currentLevelType);
    }

    public void loadLevel(Levels currentLevel) {
        loadLevel(currentLevel, true);
    }
    public void loadLevel(Levels currentLevelType, boolean unload) {
        this.currentLevelType = currentLevelType;

        if (this.currentLevel != null && unload) {
            this.currentLevel.unload();
            this.currentLevel = null;
        }

        switch (currentLevelType) {
            case MENU:
                this.currentLevel = new Menu(this, "/levels/Black.png");
                break;
            case FIRST_LEVEL:

                if (!levels.isEmpty()) {
                    this.currentLevel = levels.get(0);
                    System.out.println("GameLevel-level: "+this.currentLevel);
                    this.currentLevel.addManager(this);
                } else System.out.println("No first level");

                break;
            case TEST:
                testLevel.isRenderingLight(true);
                testLevel.setFilterColor(-0xfa);

                PlayerMP player = new PlayerMP(testLevel, 0, 0, "Kake", null, -1, true);
                System.out.println("Player LM "+player);
                Packet00Login packet = new Packet00Login(player.getUsername(), player.x, player.y);

                if (server != null) {
                    server.addConnection(player, packet);
                }

                packet.writeData(client);


                new Lantern(testLevel, 10, 10);

                new Walker(testLevel, 60, 60);

                loadLevel(testLevel);
                break;
            case NEXT:
                loadLevel(Sokoban.init(Sokoban.nextBoard()));
                break;
            default:
                throw new RuntimeException("Invalid level");
        }
        System.out.println("Level loading");
        this.currentLevel.loadLevel();
        screen.setLighting(this.currentLevel.lighting);
        System.out.println("Level loaded");
    }

    public void addLevel(Level level) {
        if (level != null && !levels.contains(level)) {
            levels.add(level);
            level.addManager(this);
        }
        System.out.println("Level: "+level);

    }

    public void addLevels(Level... levels) {
        for (Level level : levels) {
            if (!(level == null || this.levels.contains(level))) {
                this.levels.add(level);
            }
        }
    }

    public Level currentLevel() {
        return currentLevel;
    }

    public void tick() {
        currentLevel.tick();

        if (screen.g == null && this.g != null) {
            screen.g = this.g;
        }

//========= Sokoban spesific win conditions

        if (!wonFlag && currentLevel.entities.stream().anyMatch(n -> n instanceof Goal && ((Goal) n).isWon())) {
            smoothRise = 1;
            cycleTime = 1;
            wonFlag = true;
        }

        if (smoothRise != 0) {
            if (currentLevel.lighting.setFilter(cycleTime, smoothRise == 1, 0xff))
                smoothRise = 0;
        } else if (wonFlag) {
            loadLevel(Levels.NEXT, false);
            wonFlag = false;
        }
//=========================================

        if (input.restart.isToggled()) {
            Sokoban.deiterateIndex();
            loadLevel(Levels.MENU);
        }

        if (input.meta_data.isToggled()) {
            Game.META_DATA = !Game.META_DATA;
        }

        if (input.light.isToggled()) {
            currentLevel.isRenderingLight();
        }

        switch (currentLevelType) {
            case TEST:
//                if (Game.tickCount%20 == 0) {
//                    int[] pos = randomEdgePos();
//
//                    new Walker(currentLevel,pos[0],pos[1]);
//
//                }
                break;
            default:
        }
    }

    private int[] randomEdgePos() {
        int[] pos = new int[2];
        double r = Math.random();
        if (r < 0.5) {
            pos[0] = r < 0.25 ? 0:currentLevel.width*8;
            pos[1] = (int) (currentLevel.height*8*Math.random());
        } else {
            pos[0] = (int) (currentLevel.width*8*Math.random());
            pos[1] = r < 0.25 ? 0:currentLevel.width*8;
        }

        return pos;
    }


    public void draw(Graphics g) {

        currentLevel.draw(g, screen);

        if (this.g == null) {
            this.g = g;
        }

    }

}
