package com.example.myandroidapplication;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Defines the connection to the server.
 */
public class ClientToServer {

    // Constants.
    private static final String SET_AILERON = "set /controls/flight/aileron ";
    private static final String SET_ELEVATOR = "set /controls/flight/elevator ";
    private static final String NEW_LINE = "\r\n";

    private Socket socket;
    private Writer outStream;
    private float aileron;
    private float elevator;

    /**
     * Constructor of the ClientToServer class which establish the connection to the server.
     *
     * @param ip   the given IP address.
     * @param port the given port.
     */
    public ClientToServer(final String ip, final int port) {

        // Default values.
        this.aileron = 0;
        this.elevator = 0;

        // Making the connection to the server in a new thread.
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {

                    // Extracting the IP address.
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    // Creating the socket.
                    socket = new Socket(serverAddr, port);

                    try {
                        // Initializing the outStream.
                        outStream = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }

                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });

        // Put the thread in the ready queue.
        t.start();

        // Making the thread wait to the main thread.
        try {
            t.join();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    /**
     * Updating the aileron value in the server.
     *
     * @param newVal the new value.
     */
    public void setAileron(float newVal) {
        this.aileron = newVal;
        String msg = SET_AILERON + newVal + NEW_LINE;
        send(msg);
    }

    /**
     * Updating the elevator value in the server.
     *
     * @param newVal the new value.
     */
    public void setElevator(float newVal) {
        this.elevator = newVal;
        String msg = SET_ELEVATOR + newVal + NEW_LINE;
        send(msg);
    }

    /**
     * Sending the new value to the server.
     *
     * @param str the command to send to the server.
     */
    public void send(final String str) {

        // Send the new value in a new thread.
        Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    outStream.write(str);
                    outStream.flush();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });

        // Put the thread in the ready queue.
        t.start();

        // Making the thread wait to the main thread.
        try {
            t.join();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    /**
     * Closing the socket.
     */
    public void close() {

        // Making it in a new thread.
        new Thread(new Runnable() {
            public void run() {
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }
        }).start();
    }
}