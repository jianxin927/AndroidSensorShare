package com.example.androidsensorshare;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class UDP_Broadcast {
    int port;
    String ip_assign;
    String ip_wlan;
    String ip_global;
    String ip_info;
    String ip_final_dest;
    boolean autoip;
    private InetAddress group;
    String TAG = "#####";
    public UDP_Broadcast( int dstport){
        retrieveipList();
        assignip("255.255.255.255");
        port = dstport;
        autoip = false;
    }
    void assignip(String ip){
        ip_assign = ip;
        updateTarget();
    }
    void setautoip(Boolean b){
        autoip = b;
    }
    void send(String str){
        new udpBroadCast(str).start();
    }
    void updateTarget() {
        try {
            if(autoip){
                group = InetAddress.getByName(ip_wlan.replaceFirst("\\.[0-9]{1,3}$", ".255"));
            }else {
                group = InetAddress.getByName(ip_assign);
            }
            ip_final_dest = group.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private  class udpBroadCast extends Thread {
        MulticastSocket sender = null;
        DatagramPacket dj = null;
        DatagramSocket socket = null;

        byte[] data = new byte[1024];
        public udpBroadCast(String dataString) {
            data = dataString.getBytes();
        }
        @Override
        public void run() {
            try {
                super.run();
                Looper.prepare();
                if(!isValidIP(ip_final_dest)) {
                    Toast.makeText(globalAppClass.globalContext, "Invalid ip: " + "<"+ip_final_dest+">", Toast.LENGTH_SHORT).show();
                }else {
                    socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    DatagramPacket sendPacket = new DatagramPacket(data, data.length, group, port);
                    socket.send(sendPacket);
                    socket.close();
                }
                Looper.loop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @NonNull
    private String getBroadcastAddress() {
        WifiManager wifiManager = (WifiManager) globalAppClass.globalContext.getSystemService(WIFI_SERVICE);
        String ip= Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        String str = new String("0.0.0.0");
        if(ip.equals(str)){
            ip = "255.255.255.255";
        }
        str = ip.replaceFirst("\\.[0-9]{1,3}$", ".255");
        return str;
    }
    public void retrieveipList(){
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            ip_info = "";
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        //Log.d(TAG,"IPV6 skip: "+ ia.toString());
                        continue;// skip ipv6
                    }
                    String interfaceInfo = ni.getName() + ":" + ia.getHostAddress();
                    //Log.d(TAG,interfaceInfo);
                    //TextView mainView = findViewById(R.id.iplist);
                    //mainView.setText(mainView.getText()+interfaceInfo+"\r\n");
                    ip_info += interfaceInfo+"\r\n";

                    if(ni.getName().contains("wlan")){
                        ip_wlan = ia.getHostAddress();
                    }else if(ni.getName().contains("lo")){

                    }else{//rmnet,
                        ip_global = ia.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.d(TAG,e.toString());
            e.printStackTrace();
        }
    }

    public static boolean isValidIP(String ip){
        return ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$");
    }
}
