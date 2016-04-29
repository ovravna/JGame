package game.net;

//import java.util.*;

import game.Game;
import game.entities.PlayerMP;
import game.level.LevelManager;
import game.net.packets.Packet;
import game.net.packets.Packet00Login;

import java.io.IOException;
import java.net.*;


public class GameClient extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private Game game;
    private LevelManager levelManager;

    public GameClient(Game game, String ipAddress) {
        this.game = game;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                System.err.println("START RUN IN CLIENT");
                socket.receive(packet);
                System.err.println("CONTINUE RUN IN CLIENT");
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim();
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
        Packet packet = null;
        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                System.err.println("Login!");
                packet = new Packet00Login(data);
                handleLogin((Packet00Login) packet, address, port);
                break;
            case DISCONNECT:
//                packet = new Packet01Disconnect(data);
//                System.out.println("["+address.getHostAddress()+":"+port+"] "
//                        +((Packet01Disconnect) packet).getUsername()+" has left the world...");
//                game.level.removePlayerMP(((Packet01Disconnect) packet).getUsername());
//                break;
            }
    }

    public void sendData(byte[] data) {
        if (!game.isApplet) {
            DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, GameServer.PORT);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleLogin(Packet00Login packet, InetAddress address, int port) {
        System.out.println("["+address.getHostAddress()+":"+port+"] "+packet.getUsername()
                +" has joined the game...");
        PlayerMP player = new PlayerMP(levelManager.testLevel, packet.getX(), packet.getY(), packet.getUsername(), address, port, true);
        player.addToLevel();
    }

//    private void handleMove(Packet02Move packet) {
//        this.game.level.movePlayer(packet.getUsername(), packet.getX(), packet.getY(), packet.getNumSteps(),
//                packet.isMoving(), packet.getMovingDir());
//    }

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

}
