package com.example.spacedefender;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {


    private EditText newPassword;
    private Button btnChangepass;
    private Button btnSignOut;
    private Button btnMainMenu;
    private Button btnUpdatePass;
    private Button btnCancel;
    private ProgressBar progressBar;
    private TextView welcomeTxt, scoreTxt;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private CountDownTimer timer;
    private boolean session_out = false;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthLis;
    private String userId;
    private User myUser = new User();
    private ImageView userImg;
    private int score;
    // FB user profile
    private URL imgValue = null;
    private String facebookUser;
    private AccessToken token;
    // Sharing with FB
    private Button shareButton;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    // user profile
    private Uri image_uri;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_user_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("userProfileImg");

        welcomeTxt = findViewById(R.id.welcomeTxt);
        scoreTxt = findViewById(R.id.scoreTxt);
        newPassword = findViewById(R.id.newPassword);
        btnChangepass = findViewById(R.id.btnChangepass);
        btnUpdatePass = findViewById(R.id.btnUpdatePass);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnMainMenu = findViewById(R.id.btnMainMenu);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.GONE);
        progressBar = new ProgressBar(this);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        shareButton = findViewById(R.id.share_btn);
        userImg = findViewById(R.id.userImg);

        btnChangepass.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnUpdatePass.setOnClickListener(this);
        btnMainMenu.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        mAuthLis = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    // user sign in
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

    public void detectUser() {
        if (currentUser != null) {
            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {

                if (user.getProviderId().equals("facebook.com")) {
                    //For linked facebook account
                    btnChangepass.setVisibility(View.GONE);
                    shareButton.setVisibility(View.VISIBLE);
                    userImg.setOnClickListener(null);
                    // FB user profile pi
                    token = AccessToken.getCurrentAccessToken();
                    facebookUser = AccessToken.getCurrentAccessToken().getUserId();
                    fbUserProfilePic(facebookUser);
                    Log.d("xx_xx_provider_info", "User is signed in with Facebook");

                } else {
                    //For linked Google account
                    shareButton.setVisibility(View.GONE);
                    if (user.getPhotoUrl() != null) {
                        Picasso.with(getApplicationContext()).load(String.valueOf(user.getPhotoUrl())).into(userImg);
                    }
                    userImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SelectProfilePic();
                            //userProfilePic();
                        }
                    });
                    Log.d("xx_xx_provider_info", "User is signed in with Google");
                }

            }
        }
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

                    //myUser.setUsername(username);
                    welcomeTxt.setText("Welcome: " + "\n" + username + "\n" + "Your email:" + "\n" + email);
                    scoreTxt.setText("Your score: " + score);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        }
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
       firebaseAuth.addAuthStateListener(mAuthLis);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnChangepass) {
            newPassword.setVisibility(View.VISIBLE);
            btnUpdatePass.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnMainMenu.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.GONE);
            btnChangepass.setVisibility(View.GONE);

        } else if (v.getId() == R.id.btnSignOut) {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            Toast.makeText(UserProfile.this, "Logged out!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            startActivity(new Intent(UserProfile.this, MainActivity.class));
        } else if (v.getId() == R.id.btnUpdatePass) {
            ChangePasswordRequest(newPassword.getText().toString());

        } else if (v.getId() == R.id.btnMainMenu) {
            startActivity(new Intent(UserProfile.this, MainMenu.class));
        } else if (v.getId() == R.id.share_btn) {
            // here should pass the score for the user
            share (score);
        }else if (v.getId() == R.id.btnCancel){
            btnMainMenu.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnChangepass.setVisibility(View.VISIBLE);
            newPassword.setVisibility(View.GONE);
            btnUpdatePass.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Start timer on inactivity
        Log.i("Main", "Timer started!");
        timer.start();
        firebaseAuth.addAuthStateListener(mAuthLis);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //if user do some activity then cancel timer
        if (timer != null) {
            Log.i("Main", "Timer stopped!");
            timer.cancel();
            firebaseAuth.addAuthStateListener(mAuthLis);
        }
        //if user comes back after session time out then redirect to login page
        if (session_out == true) {
            firebaseAuth.signOut();
            Toast.makeText(UserProfile.this, "Session Timed Out!!.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserProfile.this, MainActivity.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(mAuthLis);
    }



    /*------------ Below Code is for changing password process -----------*/

    private void ChangePasswordRequest(final String new_password) {
        if (new_password.equals("")) {
            Toast.makeText(UserProfile.this, "Enter Password!! ", Toast.LENGTH_LONG).show();
        } else {
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            assert user != null;
            user.updatePassword(new_password)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfile.this, "User Password Updated.", Toast.LENGTH_LONG).show();
                                newPassword.setVisibility(View.GONE);
                                btnUpdatePass.setVisibility(View.GONE);
                                btnMainMenu.setVisibility(View.VISIBLE);
                                btnSignOut.setVisibility(View.VISIBLE);
                                btnChangepass.setVisibility(View.VISIBLE);
                                btnCancel.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(UserProfile.this, "For Securtiy resons you have to re-login first.\nThen try to update password.", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        }
    }

    /*-------- Below Code is for sharing score and get user profile pic from FB -----------*/

    public void share (int score){
        shareDialog = new ShareDialog(this);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setQuote("Hi Guys I have got score:  "+score+" in this game")
                    //.setImageUrl(Uri.parse("https://lh3.googleusercontent.com/proxy/C-LFK66JUDy7Q8WPFNMYJrOYFhCOAuDPppp6zUJkH2YhfW-JwXpT_zX6biJunN4HPy9uRWmuaL_C3L5SnElfMzgeIiYnWJZE2Dmtl07g0sgQLmB9KaN_kGL4FAsNOQ"))
                    .setContentUrl(Uri.parse("https://lh3.googleusercontent.com/proxy/C-LFK66JUDy7Q8WPFNMYJrOYFhCOAuDPppp6zUJkH2YhfW-JwXpT_zX6biJunN4HPy9uRWmuaL_C3L5SnElfMzgeIiYnWJZE2Dmtl07g0sgQLmB9KaN_kGL4FAsNOQ"))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    public void fbUserProfilePic(String id) {
       try {
            imgValue = new URL("https://graph.facebook.com/"+id+"/picture?type=large");
            //Bitmap mIcon1 = BitmapFactory.decodeStream(imgValue.openConnection().getInputStream());
            //userImg.setImageBitmap(mIcon1);
           Picasso.with(getApplicationContext()).load(String.valueOf(imgValue)).into(userImg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    /*-------- Below Code is for selecting image from galary or camera -----------*/

    private void SelectProfilePic() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")){
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            String [] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission,1000);
                        }
                        else {
                            openCamera();
                        }
                    }
                    else {
                        openCamera();
                    }
                }
                else if (options[item].equals("Choose from Gallery")){

                    Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);


                }
                else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }
            }
        });
        builder.show();

    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //Camera intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(takePictureIntent, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else {
                    //permisiion from pop up was denied.
                    Toast.makeText(UserProfile.this,"Permission Denied...",Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    userImg.setImageURI(image_uri);
                    userProfilePic();
                    break;
                case 2:
                    //data.getData returns the content URI for the selected Image
                    image_uri = data.getData();
                    userImg.setImageURI(image_uri);
                    userProfilePic();
                    break;
            }
        }
    }

    //*---------- uploading and downloading user profile pic from firebase storage

    public void userProfilePic(){
        if(image_uri != null)
        {
           progressBar.setVisibility(View.VISIBLE);


            final StorageReference ref = storageReference.child("images/"+currentUser.getUid());
            ref.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            userImg.setImageURI(image_uri);
                            downloadUserImg(ref);
                            Toast.makeText(UserProfile.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(UserProfile.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());

                        }
                    });
        }
    }

    public void downloadUserImg(StorageReference reference){

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setUserProfileUrl(uri);
            }
        });
    }

    public void setUserProfileUrl(Uri uri){
        FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        myUser.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void  updateUI(FirebaseUser account){
        if(account != null){
            displayUserInfo();
            detectUser();
        }else {
            startActivity(new Intent(this,MainActivity.class));
        }
    }
}
