package com.example.app1;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by mithileshhinge on 07/01/18.
 */
public class Listen extends Thread {
    public byte[] buffer;
    public static DatagramSocket listenSocket;
    private int ListenPort = 6673;
    //private static int msgPort = 6676;
    private int sampleRate = 44100;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize;

    final byte BYTE_START_LISTEN = 5, BYTE_STOP_LISTEN = 6;

    AudioTrack audioTrack;
    public boolean listen_status = true;


    public Listen(){

    }

    public void run(){
        try{
            System.out.println(".......TRY CATCH CHYA AAT.........");

            while (!LivefeedFragment.sendMsg(BYTE_START_LISTEN)){}

            minBufSize = 4096;
            buffer = new byte[minBufSize];

            DatagramPacket packet = new DatagramPacket(buffer,buffer.length);

            System.out.println("DataGramSocket BANAVLA!!!!!");
            System.out.println("Server: " + LivefeedFragment.servername);

            //ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate,AudioFormat.CHANNEL_OUT_MONO,audioFormat,minBufSize,AudioTrack.MODE_STREAM);
            audioTrack.play();

            while(true){

                System.out.println(".......blah blah.......");
                listenSocket = new DatagramSocket(ListenPort);

                listenSocket.receive(packet);
                System.out.println("PACKETS RECEIVED DATA:" + String.valueOf(buffer));
                /*((Activity) MainActivity.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                            if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {

                            }
                        }
                    }
                });*/

                audioTrack.write(buffer, 0, minBufSize);
                audioTrack.play();

                listenSocket.close();
                if (!listen_status){
                    if(audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                        if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                            try {
                                audioTrack.stop();
                                System.out.println("AUDIO TRACK STOP!!!");
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }

                        audioTrack.release();
                        System.out.println("!!! AUDIO TRACK RELEASED");
                    }

                    while (!LivefeedFragment.sendMsg(BYTE_STOP_LISTEN)){}

                    System.out.println("LISTEN CLIENT BANDA JHALA");
                    listen_status = true;
                    return;
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
