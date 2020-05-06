package com.example.spacedefender;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import com.Company.SpaceDefender.UnityPlayerActivity;

//this class is necessary because when we quit out of the unity game, it also closes the activity that started it.
public class StartGameTransition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, UnityPlayerActivity.class));

    }

}
