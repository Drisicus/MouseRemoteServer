package com.example.mouseremote;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IpDisplayBox extends JPanel {
        public void paint(Graphics g){
            g.drawString("Works with port 8855", 10, 10);
            int y = 30;
            for(String ip : getIp()){
                g.drawString(ip, 10, 10 + y);
                y += 20;
            }
        }

        public void paintBox(){
            JFrame frame= new JFrame("MouseRemote");
            frame.getContentPane().add(new IpDisplayBox());
            frame.setSize(500, 200);
            frame.setResizable(true);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
        }

    // Code extracted from: https://stackoverflow.com/questions/40912417/java-getting-ipv4-address
    public static List<String> getIp(){
        String ip;
        List<String> allIps = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof Inet6Address) continue;

                    ip = addr.getHostAddress();
                    allIps.add(iface.getDisplayName() + " " + ip);
                    System.out.println();
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return allIps;
    }
}
