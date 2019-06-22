package com.example.myandroidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    // Called when user taps the Send button
    public void openJoystick(View view) {
        EditText editIP = (EditText) findViewById(R.id.Edit_IP);
        EditText editPort = (EditText) findViewById(R.id.Edit_Port);
        Intent intent = new Intent(this, JoystickActivity.class);
        intent.putExtra("ip", editIP.getText().toString());
        intent.putExtra("port", Integer.parseInt(editPort.getText().toString()));
        startActivity(intent);
    }
}