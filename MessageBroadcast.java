package backend;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MessageBroadcast{

    private DatagramSocket socket;
    private String message;
    ChatServer serverInstance;

    MessageBroadcast(DatagramSocket socket, ChatServer serverInstance) {
        this.socket = socket;
        this.serverInstance = serverInstance;
    }

    public void broadcastMessage(byte[] buffy){
        try {
            byte[] buffer = buffy;

            //set packet with byte array for multiple clients
            for (int i = 0; i < serverInstance.getClient_addresses().size(); i++) {
                //get client addresses from server instance
                InetAddress cl_address = serverInstance.getClient_addresses().get(i);
                int cl_port = serverInstance.getClient_ports().get(i);

                //send packet to the socket
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, cl_address, cl_port);
                socket.send(packet);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
