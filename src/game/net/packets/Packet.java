package game.net.packets;

//import java.util.*;

import game.net.GameClient;
import game.net.GameServer;

public abstract class Packet {

    public static enum PacketTypes {
        INVALID(-1), LOGIN(00), DISCONNECT(01);


        private int packetId;

        PacketTypes(int packetId) {
            this.packetId = packetId;
        }

        public int getId() {
            return packetId;
        }
    }

    private byte packetId;

    public Packet(int packetId) {
        this.packetId = (byte) packetId;
    }

    public abstract void writeData(GameClient client);

    public abstract void writeData(GameServer server);

    public abstract byte[] getData();

    public static PacketTypes lookupPacket(String id) {
        try {
            return lookupPacket(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return PacketTypes.INVALID;
        }
    }


        public static PacketTypes lookupPacket(int id) {
        for (PacketTypes p : PacketTypes.values()) {
            if (p.getId() == id) {
                return p;
            }
        }

        return PacketTypes.INVALID;

    }

    public String readData(byte[] data) {
        String msg = new String(data).trim();

        return msg.substring(2);
    }


}
