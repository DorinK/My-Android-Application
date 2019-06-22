package com.example.myandroidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class JoystickActivity extends AppCompatActivity implements JoystickView.JoystickListener {

    ClientToServer client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);

        // Get the Intent that started this activity and extract the message
        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");
        int port = intent.getIntExtra("port", 5402);

        client = new ClientToServer(ip, port);
    }

    //set the aileron and the elevator when the joystick is moved.
    @Override
    public void onJoystickMoved(float x, float y) {
        client.setAileron(x);
        client.setElevator(y != 0 ? -y : 0);
    }
}
