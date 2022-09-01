package com.example.androidsensorshare;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.text.format.Formatter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDP_Broadcast {
    Context context;
    int port;
    String target_ip = new String();
    boolean autoip;
    public UDP_Broadcast(Context con, int dstport){
        context = con;
        port = dstport;
    }
    void setip(String ip){
        target_ip = ip;
    }
    void setautoip(Boolean b){
        autoip = b;
    }
    void send(String str){
        new udpBroadCast(str).start();
    }

    private  class udpBroadCast extends Thread {
        MulticastSocket sender = null;
        DatagramPacket dj = null;
        InetAddress group = null;
        DatagramSocket socket = null;

        byte[] data = new byte[1024];
        public udpBroadCast(String dataString) {
            data = dataString.getBytes();
        }
        @Override
        public void run() {
            super.run();
            Looper.prepare();
            try {
                socket = new DatagramSocket();
                socket.setBroadcast(true);
                boolean a,b,c;
                a = !autoip;
                b = !target_ip.isEmpty();
                c = target_ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$");
                if(a && b && c)
                //if(!autoip && !target_ip.isEmpty() && target_ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$"))
                    group = InetAddress.getByName(target_ip);
                else {
                    if(!autoip) {
                        Toast.makeText(context, "Invalid IP " + target_ip, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    group = getBroadcastAddress();
                }
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, group, port);
                socket.send(sendPacket);
                socket.close();

                Looper.loop();
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
        if(ip == "0.0.0.0"){
            ip = "255.255.255.255";
        }
        str = ip.replaceFirst("\\.[0-9]{1,3}$", ".255");
        return InetAddress.getByName(str);
    }
}
