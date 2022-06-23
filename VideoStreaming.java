package backend;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 *
 * VideoStreaming is a thread class design to broadcast video
 * with computer camera using OpenCV library
 */
public class VideoStreaming implements Runnable{

    private DatagramSocket socket;
    private VideoCapture capture = new VideoCapture(0);
    private Mat image = new Mat();
    ChatServer serverInstance;
    MessageBroadcast messageInstance;

    /**
     * @param sock UDP's Datagram Socket
     * @param serverInstance ChatServer class instance to get client IP Addresses
     */
    VideoStreaming(DatagramSocket sock, ChatServer serverInstance) {
        this.socket = sock;
        this.serverInstance = serverInstance;
        messageInstance = new MessageBroadcast(sock, serverInstance);
    }

    /**
     * run method of the thread class for video streaming
     */
    public void run() {
        try {
            while (true) {
                //capture mat frame with video capture
                capture.read(image);
                try {
                    //convert mat frame to the buffered image
                    BufferedImage buffImg = Mat2BufferedImage(image);

                    //write image data to byte array stream
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(buffImg, "jpg", baos);
                    baos.flush();

                    //fill byte array with image data
                    byte[] buffy = baos.toByteArray();

                    messageInstance.broadcastMessage(buffy);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * This method converts image matrix into buffered image
     *
     * @param matrix captured image matrix by video capture object
     * @return  buffered image
     * @throws Exception
     */
    static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
        MatOfByte mob = new MatOfByte();
        //usage of opencv imgcodecs function for jpg conversion
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }
}
