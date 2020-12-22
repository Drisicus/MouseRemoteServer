package com.example.mouseremote;


import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Point;
import java.awt.MouseInfo;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static final int PORT = 8855;

    public static void main(String[] args) throws AWTException, IOException, ClassNotFoundException {
        IpDisplayBox ipDisplayBox = new IpDisplayBox();
        ipDisplayBox.paintBox();

        InetAddress serverAddress = InetAddress.getLocalHost();
        String serverIP = serverAddress.getHostAddress();
        System.out.println(serverIP);
        connectWithMessage();
    }

    public static void connectWithMessage() throws IOException, ClassNotFoundException, AWTException {
        Robot robot = new Robot();

        // don't need to specify a hostname, it will be the current machine
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("ServerSocket awaiting connections...");

        boolean serverUp = true;
        while(serverUp) {
            // blocking call, this will wait until a connection is attempted on this port.
            Socket socket = serverSocket.accept();
            System.out.println("Connection from " + socket + "!");

            // get the input stream from the connected socket
            InputStream inputStream = socket.getInputStream();

            // create a DataInputStream so we can read data from it.
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            // read the list of messages from the socket
            List<Message> listOfMessages = (List<Message>) objectInputStream.readObject();
            System.out.println("Received [" + listOfMessages.size() + "] messages from: " + socket);
            // print out the text of every message
            System.out.println("All messages:");
            listOfMessages.forEach(msg -> {
                Point mousePosition = MouseInfo.getPointerInfo().getLocation();
                int posX = mousePosition.getLocation().x;
                int posY = mousePosition.getLocation().y;

                System.out.println("X: " + msg.getxCoord());
                System.out.println("Y: " + msg.getyCoord());
                System.out.println("isClick: " + msg.isClick());
                System.out.println("Space: " + msg.isSpace());
                robot.mouseMove(posX + msg.getxCoord() * 50,posY + msg.getyCoord() * 50);
                if(msg.isClick()){
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }
                if(msg.isSpace()){
                    robot.keyPress(KeyEvent.VK_SPACE);
                    robot.keyRelease(KeyEvent.VK_SPACE);
                }
            });
            System.out.println("Closing sockets.");
            socket.close();
        }
        serverSocket.close();
    }
}
