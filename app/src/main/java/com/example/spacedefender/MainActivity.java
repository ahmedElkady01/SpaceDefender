package com.example.spacedefender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText loginEmail;
    private EditText loginPassword;
    private Button btnSignIn;
    private LoginButton btnFb;
    private TextView forgot_password;
    private TextView createAccount;
    private CheckBox rememberMe;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth.AuthStateListener mAuthLis;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private FirebaseUser currnetUser;
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        FacebookSdk.sdkInitialize(getApplicationContext());

        progressBar = new ProgressBar(this);
        loginEmail = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnFb = findViewById(R.id.btnFb);
        forgot_password = findViewById(R.id.forgot_password);
        createAccount = findViewById(R.id.createAccount);
        rememberMe  = findViewById(R.id.rememberMe);
        progressBar = findViewById(R.id.progressBar);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        progressBar.setVisibility(View.GONE);

        btnSignIn.setOnClickListener(this);
        forgot_password.setOnClickListener(this);
        createAccount.setOnClickListener(this);
        btnFb.setOnClickListener(this);
        btnFb.setPermissions(Arrays.asList("email", "public_profile"));


        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            loginEmail.setText(loginPreferences.getString("email", ""));
            loginPassword.setText(loginPreferences.getString("password", ""));
            rememberMe.setChecked(true);
        }

        mAuthLis = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currnetUser = firebaseAuth.getCurrentUser();
                if (currnetUser!=null){
                    updateUI(currnetUser);
                }
                else {
                    updateUI(null);
                }
            }
        };

        // to komw the hash key
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.d("AppLog", "key:" + FacebookSdk.getApplicationSignature(this)+"=");
    }



    public void onClick(View v) {
        if (v.getId() == R.id.btnSignIn) {
            checkValidation();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(loginEmail.getWindowToken(), 0);
            String email = loginEmail.getText().toString();
            String password = loginPassword.getText().toString();
            if (rememberMe.isChecked()){
                loginPrefsEditor.putBoolean("saveLogin", true);
                loginPrefsEditor.putString("email", email);
                loginPrefsEditor.putString("password", password);
                loginPrefsEditor.commit();
            }
            else {
                loginPrefsEditor.clear();
                loginPrefsEditor.commit();
            }
            currnetUser = firebaseAuth.getCurrentUser();
            //doSomethingElse();
            updateUI(currnetUser);
        }
        else if (v.getId() == R.id.forgot_password){
            Intent intent = new Intent(MainActivity.this,ForgotPassword.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.createAccount){
            Intent intent = new Intent(MainActivity.this,Register.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.btnFb){
            signFb();
        }

    }

    public void doSomethingElse() {
        startActivity(new Intent(MainActivity.this, MainMenu.class));
        //MainActivity.this.finish();
    }

    public void signFb(){
        btnFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loadUserProfile(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthLis);
        if (currnetUser != null) {
            updateUI(currnetUser);
        }
        else {
            updateUI(null);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(mAuthLis);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currnetUser = firebaseAuth.getCurrentUser();
        if (currnetUser!=null){
            updateUI(currnetUser);
        }else {
            updateUI(null);
        }

    }

    public void checkValidation(){
         String email = loginEmail.getText().toString().trim();
         String password = loginPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password )) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        } if (password.length() <6) {
            Toast.makeText(this, "Minimum length of password should be 6", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    // create new Intent to go to the menue
                    currnetUser = firebaseAuth.getCurrentUser();
                    updateUI(currnetUser);

                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    updateUI(null);

                }
            }
        });
    }

    protected void loadUserProfile(final AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    String id = object.getString("id");
                     String username = object.getString("name");
                     String email = object.getString("email");


                   handleFacebookAccessToken(accessToken, username,email);
                    //System.out.println(username)


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","id,name, email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void handleFacebookAccessToken(AccessToken token, final String username, final String email) {
        progressBar.setVisibility(View.VISIBLE);
        btnFb.setVisibility(View.GONE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                btnFb.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {
                    String password = "null";
                    int score = 0;
                    String status = "online";
                    User newUser = new User(username, email, password,score,status);
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            currnetUser = firebaseAuth.getCurrentUser();
                            updateUI(currnetUser);


                        }
                    });
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(MainActivity.this, "Auth Failed, you are already registered ", Toast.LENGTH_SHORT).show();
                        updateUI(null);

                    }else{
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        updateUI(null);

                    }
                }

            }
        });
    }

    public void  updateUI(FirebaseUser account){
        if(account != null){
           // Toast.makeText(this,"U Signed In successfully",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,MainMenu.class));
        }else {
            // Toast.makeText(this,"U Didnt signed in",Toast.LENGTH_LONG).show();
        }
    }


}
