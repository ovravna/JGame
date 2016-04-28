package game.net;

//import java.util.*;

import game.Game;
import game.net.packets.Packet;
import game.net.packets.Packet00Login;

import java.io.IOException;
import java.net.*;

public class GameClient extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;

    private Game game;


    public GameClient(Game game, String ipAddress) {

        this.game = game;

        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
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


            System.out.println("SERVER -> "+new String(packet.getData()));

        }
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, GameServer.PORT);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
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
                System.out.println("Joined the game!");

//                PlayerMP player = new PlayerMP(levelManager.testLevel, 0, 0, ((Packet00Login) packet).getUsername(), address, port, false);

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

}
