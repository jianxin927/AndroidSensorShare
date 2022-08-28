package com.example.androidsensorshare;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDP_Broadcast {


    void send(String str){
        new udpBroadCast(str).start();
    }

    private  class udpBroadCast extends Thread {
        MulticastSocket sender = null;
        DatagramPacket dj = null;
        InetAddress group = null;

        byte[] data = new byte[1024];

        public udpBroadCast(String dataString) {
            data = dataString.getBytes();
        }

        @Override
        public void run() {
            try {
                sender = new MulticastSocket();
                //group = InetAddress.getByName("224.0.0.1");
                //group = InetAddress.getByName("255.255.255.255");
                group = InetAddress.getByName("192.168.43.140");
                dj = new DatagramPacket(data,data.length,group,51996);
                sender.send(dj);
                sender.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
