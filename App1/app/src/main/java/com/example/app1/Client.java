package com.example.app1;

/**
 * Created by Home on 10-07-2017.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v7.preference.PreferenceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class Client extends Thread {
    private String serverName;
    private DatagramSocket udpSocket;
    private Socket socket;
    private int udpPort = 6663;
    private int port = 6666;
    private boolean livefeed = true;
    //private InputStream in;
    //private OutputStream out;
    private static SharedPreferences spref_ip;
    Client() {

    }

    public void run() {
        try {
            //serverName = MainActivity.jIP.getText().toString();
            //serverName="192.168.7.2";
            spref_ip = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
            serverName = spref_ip.getString("ip_address","");
            socket = new Socket(serverName, port);

            while (true) {
                /*socket = new Socket(serverName, port);
                in = socket.getInputStream();
                out = socket.getOutputStream();*/

                udpSocket = new DatagramSocket(udpPort);
                byte[] buf = new byte[64000];
                DatagramPacket imgPacket = new DatagramPacket(buf, buf.length);
                udpSocket.receive(imgPacket);
                byte[] imgBuf = imgPacket.getData();

                LivefeedFragment.frame = BitmapFactory.decodeByteArray(imgBuf, 0, imgBuf.length);
                //LivefeedFragment.frame = BitmapFactory.decodeStream(new FlushedInputStream(in));
                LivefeedFragment.frameChanged = true;
                //socket.close();
                udpSocket.close();
                if (!livefeed) {
                    socket.close();
                    System.out.println("CLIENT BANDA JHALA");
                    livefeed = true;
                    return;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            try {
                if(socket!=null)
                    socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void end(){

        livefeed = false;
    }
}

