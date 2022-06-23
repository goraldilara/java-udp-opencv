package backend;

import ui.ClientWindow;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * MessageSender is a thread class that gets the messages from client and passes it with UDP packet.
 *
 */
class MessageSender implements Runnable {
    public final static int PORT = 2020;
    private DatagramSocket socket;
    private InetAddress hostName;
    private ClientWindow window;

    /**
     *
     * @param sock UDP's Datagram Socket
     * @param host IP Address of the connected client
     * @param win Java Swing UI class ClientWindow's instance
     */
    MessageSender(DatagramSocket sock, InetAddress host, ClientWindow win) {
        socket = sock;
        hostName = host;
        window = win;
    }

    /**
     *
     * This method fills the packet with the string that the client sends
     *
     * @param s The string that the client sends
     * @throws Exception
     */

    private void sendMessage(String s) throws Exception {
        byte[] buffer = s.getBytes();
        InetAddress address = hostName;
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        socket.send(packet);
    }

    /**
     * run method of the thread class for sending message
     */
    public void run() {
        boolean connected = false;
        do {
            try {
                sendMessage("New client connected - welcome!");
                connected = true;
            } catch (Exception e) {
                window.displayMessage(e.getMessage());
            }
        } while (!connected);
        while (true) {
            try {
                while (!window.isMessageReady()) {
                    Thread.sleep(100);
                }
                sendMessage(window.getMessage());
                window.setMessageReady(false);
            } catch (Exception e) {
                window.displayMessage(e.getMessage());
            }
        }
    }
}

/**
 * MessageReceiver is a thread class that gets sent messages from other clients over the server.
 * It passes messages to the Java Swing UI.
 */
class MessageReceiver implements Runnable {
    DatagramSocket socket;
    byte[] buffer;
    ClientWindow window;
    InputStream inputStream;

    /**
     *
     * @param sock UDP's Datagram Socket
     * @param win Java Swing UI class ClientWindow's instance
     */
    MessageReceiver(DatagramSocket sock, ClientWindow win) {
        socket = sock;
        buffer = new byte[92024];
        window = win;
    }


    /**
     * run method of the thread class for receiving message
     */
    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                //get data to the buffer
                byte[] buff = packet.getData();

                //conversion of byte array data to buffered image
                inputStream = new ByteArrayInputStream(buff);
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        // TODO Auto-generated method stub
                        new Thread(new Runnable() {

                            public void run() {
                                // TODO Auto-generated method stub
                                try {
                                    window.getImg(inputStream);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }).start();
                    }
                });
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}

/**
 * Main class of the java file. It triggers the threads of the other classes to send and receive operations
 */
public class ChatClient {

    public static void main(String[] args) throws Exception {

        ClientWindow window = new ClientWindow();
        InetAddress host = InetAddress.getByName("localhost");
        //InetAddress host = InetAddress.getByName("172.17.1.183");
        window.setTitle("UDP CHAT  Server: " + host);
        DatagramSocket socket = new DatagramSocket();
        MessageReceiver receiver = new MessageReceiver(socket, window);
        MessageSender sender = new MessageSender(socket, host, window);
        Thread receiverThread = new Thread(receiver);
        Thread senderThread = new Thread(sender);
        receiverThread.start();
        senderThread.start();
    }


}

