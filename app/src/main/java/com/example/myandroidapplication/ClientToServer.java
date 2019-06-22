package com.example.myandroidapplication;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

//a class to send commands to the server.
public class ClientToServer {
    private Socket socket;
    private Writer writer;
    private float aileron;
    private float elevator;
    private float changeTreshold;

    public ClientToServer(final String ip, final int port) {
        changeTreshold = (float) 0.1;
        aileron = 0;
        elevator = 0;
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {

                    //here you must put your computer's IP address.
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    //create a socket to make the connection with the server
                    socket = new Socket(serverAddr, port);

                    try {
                        //create a new writer

                        writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
                        //handle exceptions.
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }

            }
        });
        //start the thread.
        t.start();
        /// wait for the connection thread
        try {
            t.join();
        } catch (Exception e) {
            System.out.println(e.toString());
        }


    }

    //check if there was a difference between the former and current values.
    private boolean diffrent(float a, float b) {
        return Math.abs(a - b) > changeTreshold;
    }

    //set the aileron value.
    public void setAileron(float val) {
        if (diffrent(val, aileron)) {
            aileron = val;
            String msg = "set /controls/flight/aileron " + val + "\r\n";
            send(msg);
        }
    }

    //set the elevator value.
    public void setElevator(float val) {
        if (diffrent(val, elevator)) {
            elevator = val;
            String msg = "set /controls/flight/elevator " + val + "\r\n";
            send(msg);
        }

    }

    //a function to send a command to the server.
    public void send(final String str) {
        //send the command in a new thread.
        Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    writer.write(str);
                    writer.flush();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });
        t.start();
        //wait for the thread to finish.
        try {
            t.join();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }


}

/*import android.util.Log;

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
}*/
