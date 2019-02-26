package com.example.avjindersinghsekhon.minimaltodo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by jauth on 2018-02-05.
 */

public class ActivityLogin extends AppCompatActivity {

    SignInButton btn;
    AppCompatButton offlineBtn;
    AppCompatButton loginBtn;
    AppCompatButton createBtn;
    EditText emailIn;
    EditText passIn;
    FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 999;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mDatabase = mRootRef.child("users");

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        // ------ Email Sign In Button Listener ------
        loginBtn = (AppCompatButton) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailSignIn();
            }
        });

        // ----- Create Account Button Listener -----
        createBtn = (AppCompatButton) findViewById(R.id.btn_create);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEmailAccount();
            }
        });

        // ----- Offline Mode Button Listener -----
        offlineBtn = (AppCompatButton) findViewById(R.id.btn_offline);
        offlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity ma = new MainActivity();
                ma.offline = true;
                startActivity(new Intent(ActivityLogin.this, MainActivity.class));
            }
        });

        // ----- Google Sign In Button Listener -----
        btn = (SignInButton) findViewById(R.id.google_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                }
            }
        };

        // ------ Configure Google Sign In ------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(ActivityLogin.this,"something went wrong",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    // ----- Email -----
    private void emailSignIn(){
        emailIn = (EditText) findViewById(R.id.input_email);
        String email = emailIn.getText().toString();

        passIn = (EditText) findViewById(R.id.input_password);
        String password = passIn.getText().toString();

        if (email.matches("") ||  password.matches("")){
            //email and/or password fields are empty
            Toast.makeText(ActivityLogin.this, "Email and Password are required.",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(ActivityLogin.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void createEmailAccount() {
        emailIn = (EditText) findViewById(R.id.input_email);
        String email = emailIn.getText().toString();

        passIn = (EditText) findViewById(R.id.input_password);
        String password = passIn.getText().toString();
        if (email.matches("") || password.matches("")) {
            //email and/or password fields are empty
            Toast.makeText(ActivityLogin.this, "Email and Password are required.",
                    Toast.LENGTH_SHORT).show();
        } else if (password.length() > 8) {
            //password too short
            Toast.makeText(ActivityLogin.this, "password must be at least 8 characters.",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                userDatabaseSetUp(FirebaseAuth.getInstance().getCurrentUser());
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(ActivityLogin.this, "Account Creation failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // ----- Google -----

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result;
            result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(ActivityLogin.this,"auth went wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            // if the user has never logged in before
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNew == true){
                                userDatabaseSetUp(FirebaseAuth.getInstance().getCurrentUser());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(ActivityLogin.this,"Authentication failed, try again",Toast.LENGTH_SHORT).show();                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    // ----- Database Setup -----

    private void userDatabaseSetUp(FirebaseUser user) {
        String email = user.getEmail();
        String username = user.getDisplayName();
        String uid = user.getUid();
        writeNewUser(username, email, uid);
    }

    private void writeNewUser(String username, String email, String uid) {
        ArrayList<ToDoItem>  items = new ArrayList<>();
        ArrayList<String> lists =  new ArrayList<>();
        lists.add(0, "Show All Lists");
        User user = new User(username, email);
        mDatabase.child(uid).child("userInfo").setValue(user);
        mDatabase.child(uid).child("toDoItems").setValue(items);
        mDatabase.child(uid).child("lists").setValue(lists);
    }
}
