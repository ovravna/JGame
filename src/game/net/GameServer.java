package game.net;

//import java.util.*;

import game.Game;
import game.entities.PlayerMP;
import game.level.LevelManager;
import game.net.packets.Packet;
import game.net.packets.Packet00Login;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class GameServer extends Thread {

    public static final int PORT = 1332;
    private DatagramSocket socket;

    private Game game;
    private LevelManager levelManager;

    private List<PlayerMP> connectedPlayers = new ArrayList<>();


    public GameServer(Game game) {

        this.game = game;

        try {
            this.socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    public void run() {
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);

            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());




//            String message = new String(packet.getData());
//            System.out.println("CLIENT -> "+message);
//
//            if (message.trim().equalsIgnoreCase("ping")) {
//                sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
//            }


        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port) {
        String msg = new String(data).trim();
        Packet.PacketTypes type = Packet.lookupPacket(msg.substring(0, 2));
        Packet packet;

        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                packet = new Packet00Login(data);
                System.out.println(((Packet00Login) packet).getUsername()+" logged in!");

                PlayerMP player = new PlayerMP(levelManager.testLevel, 50, 30, ((Packet00Login) packet).getUsername(), address, port, false);

                this.addConnection(player, ((Packet00Login) packet));


//
//                if (address.getHostAddress().equalsIgnoreCase("127.0.0.1")) {
//                    player = new PlayerMP(levelManager.testLevel, 0, 0, packet.getUsername(), address, port, true);
//                }
//
//                if (player != null) {
//                    connectedPlayers.add(player);
//
//                }



                break;
            case DISCONNECT:
                break;

        }

    }

    public void addConnection(PlayerMP player, Packet00Login packet) {
        boolean alreadyConnected = false;
        for (PlayerMP p : connectedPlayers) {
            if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
                if (p.ipaddress == null) {
                    p.ipaddress = player.ipaddress;
                }

                if (p.port == -1) {
                    p.port = player.port;
                }

                alreadyConnected = true;
            } else {
                sendData(packet.getData(), p.ipaddress, p.port);
            }


//            else {
//                Packet00Login loginPacket = new Packet00Login(player.getUsername());
//                sendData(loginPacket.getData(), p.ipaddress, p.port);
//            }
            if (alreadyConnected) {
                connectedPlayers.add(player);
            }

        }


    }

    public void sendData(byte[] data, InetAddress ipAddress, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendDataToAllClients(byte[] data) {
        for (PlayerMP p : connectedPlayers) {
            sendData(data, p.ipaddress, p.port);
        }


    }
}
