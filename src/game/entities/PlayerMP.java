package game.entities;

//import java.util.*;

import game.level.Level;

import java.net.InetAddress;

public class PlayerMP extends Player {


    private final String username;
    public InetAddress ipaddress;
    public int port;

    public PlayerMP(Level level, int x, int y, String username, InetAddress ipaddress, int port, boolean isLocal) {
        super(level, x, y, isLocal);
        this.username = username;
        this.ipaddress = ipaddress;
        this.port = port;
    }

    @Override
    public void tick() {
        super.tick();

    }

    public String getUsername() {
        return username;
    }
}
