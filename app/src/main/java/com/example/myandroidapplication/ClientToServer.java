package com.example.myandroidapplication;

import android.util.Log;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientToServer {

    public void Connect() {
        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName("10.0.0.2");
            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, 1234);
            try {
                //sends the message to the server
                OutputStream output = socket.getOutputStream();
                FileInputStream fis = new FileInputStream(pic);
                output.write(imgbyte);
                output.flush();
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                socket.close();
            }
        } catch (
                Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }
}
