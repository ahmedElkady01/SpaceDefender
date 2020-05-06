package com.example.spacedefender;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import com.Company.SpaceDefender.UnityPlayerActivity;


public class StartGameTransition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, UnityPlayerActivity.class));

    }

}
