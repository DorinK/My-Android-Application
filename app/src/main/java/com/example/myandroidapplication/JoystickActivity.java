package com.example.myandroidapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/***
 * Defining the JoystickActivity.
 */
public class JoystickActivity extends AppCompatActivity implements JoystickView.JoystickListener {

    // Constants.
    private static final float UPPER_APPROX = 0.999f;
    private static final float LOWER_APPROX = -0.999f;
    private static final float UPPER_BOUND = 1;
    private static final float LOWER_BOUND = -1;

    // Connection to the server.
    private ClientToServer client;

    /***
     * Creating the activity and the connection to the server.
     *
     * @param savedInstanceState    the savedInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoystickView joystickView = new JoystickView(this);

        // The joystickView object defines Joystick's activity view.
        setContentView(joystickView);

        new Thread(new Runnable() {
            public void run() {
                try {

                    // Get the Intent that started this activity and extract the params.
                    Intent intent = getIntent();
                    String ip = intent.getStringExtra("ip");
                    int port = intent.getIntExtra("port", -1);

                    // Connect to the server with the given ip and port.
                    client = new ClientToServer(ip, port);

                } catch (Exception e) {

                    // If an exception is thrown than the app is shutting down.
                    onDestroy();
                }
            }
        }).start();
    }

    /***
     * Defines actions when a joystick's movement is encountered.
     *
     * @param xCoordinate   value of joystick's placement in the x axis.
     * @param yCoordinate   value of joystick's placement in the y axis.
     */
    @Override
    public void onJoystickMoved(final float xCoordinate, final float yCoordinate) {

        // Normalizing the new coordinates of the Joystick and sending to the server.
        new Thread(new Runnable() {
            public void run() {
                try {
                    float x = xCoordinate;
                    float y = yCoordinate * -1;

                    if (xCoordinate > UPPER_APPROX) x = UPPER_BOUND;
                    else if (xCoordinate < LOWER_APPROX) x = LOWER_BOUND;

                    if (yCoordinate > UPPER_APPROX) y = LOWER_BOUND;
                    else if (yCoordinate < LOWER_APPROX) y = UPPER_BOUND;

                    // Updating the server.
                    client.setAileron(x);
                    client.setElevator(yCoordinate == 0 ? yCoordinate : y);

                } catch (Exception e) {
                }
            }
        }).start();
    }

    /***
     * Defining the destroying behaviour of the activity - Disconnect from the server.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Closing the socket.
        client.close();
    }
}