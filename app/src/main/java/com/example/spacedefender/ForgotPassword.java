package com.example.spacedefender;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends FragmentActivity implements View.OnClickListener {


    private EditText resetEmail;
    private Button reset_btn;
    private Button backToLogin;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        progressBar = new ProgressBar(this);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        resetEmail = findViewById(R.id.resetEmail);
        reset_btn = findViewById(R.id.sendPassBtn);
        backToLogin = findViewById(R.id.backToLoginBtn);
        reset_btn.setOnClickListener(this);
        backToLogin.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sendPassBtn){
            PasswordResetEmail(resetEmail.getText().toString());

        }
        if (v.getId() == R.id.backToLoginBtn){
            startActivity(new Intent(ForgotPassword.this,MainActivity.class));

        }

    }

    /*------------ Below Code is for reset password process user will get email on registered email-----------*/

    private void PasswordResetEmail(final String email) {
        if(email.equals("")){
            Toast.makeText(ForgotPassword.this, "Enter Email!! ", Toast.LENGTH_LONG).show();
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ForgotPassword.this, "We have sent a reset password link to email: " + email, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPassword.this,MainActivity.class));
                            } else {
                                Toast.makeText(ForgotPassword.this, "Email not found in database!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}

