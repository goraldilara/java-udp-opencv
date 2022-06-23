package ui;

import org.opencv.imgcodecs.Imgcodecs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class ClientWindow extends JFrame {

    String host_name;
    JTextPane message_field;
    JTextPane room_field;

    //opencv integration attributes
    public JLabel screen;
    private JButton btnCapture;
    private BufferedImage img;
    ImageIcon ic = new ImageIcon();

    String message = "";
    boolean message_is_ready = false;

    private int counter = 1;

    public JLabel getScreen() {
        return screen;
    }

    public void setScreen(JLabel screen) {
        this.screen = screen;
    }

    public ClientWindow() {

        setLayout(null);

        screen = new JLabel();
        screen.setBounds(0, 0, 640, 480);
        add(screen);

        btnCapture = new JButton("capture");
        btnCapture.setBounds(280, 480, 80, 40);
        add(btnCapture);

        JTextArea textArea = new JTextArea();
        textArea.setBounds(40, 519, 640, 155);
        add(textArea);

        btnCapture.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                message = "Screenshot has been captured!!";
                message_is_ready = true;
            }
        });

        setSize(new Dimension(640, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void displayMessage(String receivedMessage) {
        StyledDocument doc = room_field.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), receivedMessage + "\n", null);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    /**
     *
     * This method gets the buffered image object and puts it into JLabel object to be displayed
     *
     * @param in Input Stream object
     * @throws IOException
     */
    public void getImg(InputStream in) throws IOException {
        BufferedImage img = ImageIO.read(in);
        //covert buffered image to icon for java swing
        ic = new ImageIcon(img);
        screen.setIcon(ic);
    }

    public boolean isMessageReady() {
        return message_is_ready;
    }

    public void setMessageReady(boolean messageReady) {
        this.message_is_ready = messageReady;
    }

    public String getMessage() {
        return message;
    }

    public String getHostName() {
        return host_name;
    }

}
