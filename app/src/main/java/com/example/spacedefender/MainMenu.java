package com.example.spacedefender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainMenu extends AppCompatActivity implements View.OnClickListener  {

    private Button btnPlayGame, btnLeaderBord, btnChat, btnSignOut;
    private ImageButton imageButton;
    private TextView scoreTxt, welcomeTxt;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthLis;
    private CountDownTimer timer;
    private boolean session_out = false;
    private String userId;
    private User myUser = new User();
    private URL imgValue = null;
    private String facebookUser;
    private AccessToken token;
    private ImageView userImg;
    private FirebaseUser currentUser;
    private SharedPreferences sharedPreferences;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        sharedPreferences =  this.getSharedPreferences(this.getPackageName() + ".v2.playerprefs", Context.MODE_PRIVATE);
        welcomeTxt = findViewById(R.id.welcomeTxt);
        scoreTxt = findViewById(R.id.scoreTxt);
        btnLeaderBord = findViewById(R.id.btnLeaderBord);
        btnPlayGame = findViewById(R.id.btnPlayGame);
        btnChat = findViewById(R.id.btnChat);
        btnSignOut = findViewById(R.id.btnSignOut);
        userImg = findViewById(R.id.userImg);

        progressBar = new ProgressBar(this);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        btnLeaderBord.setOnClickListener(this);
        btnPlayGame.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        userImg.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        mAuthLis = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser !=null){
                    updateUI(currentUser);
                }
            }
        };


        /*------------ Below Code is for auto logout on user's inactivity -----------*/
        timer = new CountDownTimer(300000, 1000) { //set session timeout interval here
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                session_out = true;
            }
        };

    }




    public void displayUserInfo() {
        if (currentUser != null) {
            userId = currentUser.getUid();
            myRef = database.getReference("users").child(userId);

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    int score = dataSnapshot.child("score").getValue(Integer.class);
                    int unityScore = sharedPreferences.getInt("sumHS",score); //get high score saved in unity's high score

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    //logPrefs();  //log all preferences to verify if it works

                    int higher = getHigherScore(score,unityScore);
                    editor.putInt("sumHS",higher);  //set high score in unity
                    editor.apply();


                    Map<String, Object> updateScore = new HashMap<>();  //save score to firebase database
                    updateScore.put("score", higher);
                    myRef.updateChildren(updateScore);




                    //myUser.setUsername(username);
                    welcomeTxt.setText("Welcome: " + "\n" + username + "\n" + "Your email:" + "\n" + email);
                    scoreTxt.setText("Your score: " + higher);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnPlayGame) {
            //Starting game
            //startActivity(new Intent(MainMenu.this, UnityPlayerActivity.class));
            startActivity(new Intent(MainMenu.this, StartGameTransition.class));
        } else if (v.getId() == R.id.btnSignOut) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("sumHS",0);
            editor.apply(); //sets the shared preferences high score to 0, so that if a new player logs in on the same device, the high score isn't added to it.
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            Toast.makeText(MainMenu.this, "Logged out!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainMenu.this, MainActivity.class));
        } else if (v.getId() == R.id.btnLeaderBord) {
            // calling the leader bord
            startActivity(new Intent(MainMenu.this, Leaderbord.class));
        } else if (v.getId() == R.id.btnChat) {
            // calling the game settings
            startActivity(new Intent(MainMenu.this, ChatActivity.class));
        } else if (v.getId() == R.id.userImg) {
            startActivity(new Intent(MainMenu.this, UserProfile.class));
        }
      else if (v.getId() == R.id.btnCountTimer) {
        System.out.println("Button clicked");

        CountDownDisplayer.getInstance().startTimer();
        CountDownDisplayer.getInstance().setContext(this.getApplicationContext());
    }

    }


    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthLis);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Start timer on inactivity
        firebaseAuth.addAuthStateListener(mAuthLis);
        Log.i("Main", "on pause Timer started!");
        timer.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //if user do some activity then cancel timer
        if (timer != null ) {
            Log.i("Main", "on resume Timer stopped!");
            timer.cancel();
            firebaseAuth.addAuthStateListener(mAuthLis);
        }
        //if user comes back after session time out then redirect to login page
        if (session_out == true) {
            firebaseAuth.signOut();
            Toast.makeText(MainMenu.this, "Session Timed Out!!.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainMenu.this, MainActivity.class));
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        //on closing application signout user
        firebaseAuth.removeAuthStateListener(mAuthLis);
    }

    public void userProfilePic(String id) {
        try {
            imgValue = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
            //Bitmap mIcon1 = BitmapFactory.decodeStream(imgValue.openConnection().getInputStream());
            //userImg.setImageBitmap(mIcon1);
            Picasso.with(getApplicationContext()).load(String.valueOf(imgValue)).into(userImg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public void detectUser() {
        if (currentUser != null) {
            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("facebook.com")) {
                    //For linked facebook account
                    //FB user profile pi
                    token = AccessToken.getCurrentAccessToken();
                    facebookUser = AccessToken.getCurrentAccessToken().getUserId();
                    userProfilePic(facebookUser);
                    Log.d("xx_xx_provider_info", "User is signed in with Facebook");

                } else {
                    //For linked Google account
                    if (user.getPhotoUrl() != null) {
                        Picasso.with(getApplicationContext()).load(String.valueOf(user.getPhotoUrl())).into(userImg);
                    }
                    Log.d("xx_xx_provider_info", "User is signed in with Google");
                }

            }
        }
    }

    public void  updateUI(FirebaseUser account){
        if(account != null){
            displayUserInfo();
            detectUser();
        }else {
            startActivity(new Intent(this,MainActivity.class));
        }
    }

    private int getHigherScore(int score, int unityScore){
        int compare =  Integer.compare(score,unityScore);

        int higher;
        if (compare == 1){
            higher = score;
        } else {
            higher = unityScore;
        }
        return higher;
    }

    private void logPrefs(){
        Map<String,?> keys = sharedPreferences.getAll();
        Log.d("UnityAndroid", "All PlayerPrefs: ");
        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("UnityAndroid",entry.getKey() + ": " +
                    entry.getValue().toString());
        }
    }
}
