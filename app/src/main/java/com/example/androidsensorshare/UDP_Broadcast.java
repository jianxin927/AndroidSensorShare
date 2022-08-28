package com.example.androidsensorshare;

import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    Context context;
    public UDP_Broadcast(Context con){ context = con;}
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
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, getBroadcastAddress(), 51996);
                socket.send(sendPacket);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @NonNull
    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifiManager=(WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip= Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        String str = new String();
        str = ip.replaceFirst("\\.[0-9]{1,3}$", ".255");
        return InetAddress.getByName(str);
    }
}
