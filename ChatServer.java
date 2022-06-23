package backend;

import nu.pattern.OpenCV;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

/**
 *
 * ChatServer is a class that works as a server. The class listens the assigned port
 * and insert connected clients' ports and IP addresses into arraylists
 *
 */
public class ChatServer {


    public final static int PORT = 2020;
    private final static int BUFFER = 92024;
    private DatagramPacket packet;

    public ArrayList<InetAddress> getClient_addresses() {
        return client_addresses;
    }

    public ArrayList<Integer> getClient_ports() {
        return client_ports;
    }

    public HashSet<String> getExisting_clients() {
        return existing_clients;
    }

    private ArrayList<InetAddress> client_addresses;
    private ArrayList<Integer> client_ports;
    private HashSet<String> existing_clients;

    private DatagramSocket datagramSocket;

    private String message;

    static ChatServer instance;
    public void setClient_addresses(ArrayList<InetAddress> client_addresses) {
        this.client_addresses = client_addresses;
    }

    public void setClient_ports(ArrayList<Integer> client_ports) {
        this.client_ports = client_ports;
    }

    public void setExisting_clients(HashSet<String> existing_clients) {
        this.existing_clients = existing_clients;
    }

    /**
     * @param datagramSocket UDP's Datagram Socket
     */
    public ChatServer(DatagramSocket datagramSocket){
        this.datagramSocket = datagramSocket;
        System.out.println("Server is running and is listening on port " + datagramSocket.getLocalPort());
        this.client_addresses = new ArrayList();
        this.client_ports = new ArrayList();
        this.existing_clients = new HashSet();
    }

    /**
     *
     * This method listens assigned port and fills packet if a client makes a call
     *
     */
    public void getInput(){
        byte[] buffer = new byte[BUFFER];
        while (true) {
            try {
                //receive filled packet from socket
                packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet);

                //convert packet data into string
                message = new String(packet.getData(), 0, packet.getLength());

                //assign existing client's port and address
                InetAddress clientAddress = packet.getAddress();
                int client_port = packet.getPort();

                //fill arrays with client information
                String id = clientAddress.toString() + "|" + client_port;
                if (!existing_clients.contains(id)) {
                    existing_clients.add(id);
                    client_ports.add(client_port);
                    client_addresses.add(clientAddress);
                }
                //check the input from client
                if(message.equals("New client connected - welcome!")){
                    System.out.println("Client " + clientAddress + "has joined the chat!!");
                }

                if(existing_clients != null){
                    startThreads();
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    /**
     * This method creates Video Streaming object and starts the thread
     */
    public void startThreads(){
        try{
            VideoStreaming videoStreaming = new VideoStreaming(datagramSocket, instance);
            Thread streamerThread = new Thread(videoStreaming);
            streamerThread.start();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws SocketException {

        OpenCV.loadLocally();
        try{
            DatagramSocket socket = new DatagramSocket(PORT);
            instance = new ChatServer(socket);
            instance.getInput();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


}



