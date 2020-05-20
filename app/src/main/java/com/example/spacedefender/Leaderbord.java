package com.example.spacedefender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Leaderbord extends AppCompatActivity implements View.OnClickListener{

    private Button btnBackToMenu;
    private String username;
    private int score;
    private ArrayList<String> usernameList = new ArrayList<String>();
    private ArrayList<Integer> scoreList = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderbord);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnBackToMenu.setOnClickListener(this);
        displayData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBackToMenu){
            startActivity(new Intent(Leaderbord.this,MainMenu.class));
        }
    }

    public void displayData(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersdRef = rootRef.child("users");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    username = ds.child("username").getValue(String.class);
                    if(username != null){  //avoid crash due to null user
                        score = ds.child("score").getValue(Integer.class);
                        System.out.println(username+"---------------------------------------");
                        usernameList.add(username);
                        scoreList.add(score);
                    }
                }

                init();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);

    }

    public void init() {
        TableLayout stk =  findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText("         No        ");
        tv0.setTextColor(Color.RED);
        tv0.setTextSize(18);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText("      Users      ");
        tv1.setTextColor(Color.RED);
        tv1.setTextSize(18);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText("      Level      ");
        tv2.setTextColor(Color.RED);
        tv2.setTextSize(18);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText("      Scores      ");
        tv3.setTextColor(Color.RED);
        tv3.setTextSize(18);
        tbrow0.addView(tv3);
        stk.addView(tbrow0);
        for (int i = 0; i < usernameList.size();  i++) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText("  " + i);
            t1v.setTextColor(Color.WHITE);
            t1v.setTextSize(18);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText("   " + usernameList.get(i));
            t2v.setTextColor(Color.WHITE);
            t2v.setTextSize(18);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText("     Level   1");
            t3v.setTextColor(Color.WHITE);
            t3v.setTextSize(18);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText("   " + scoreList.get(i));
            t4v.setTextColor(Color.WHITE);
            t4v.setTextSize(18);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);
            stk.addView(tbrow);
        }

    }
}
