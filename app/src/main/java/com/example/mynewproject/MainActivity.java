package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private Button mlogin, mRegister, forgot;
    private TextView welcome,sign;
    private ImageButton mReturn;
    private ProgressBar mProgress;
    private String currentUserId;
    private CheckBox rememberMe;



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN );


        mEmail = (TextInputEditText) findViewById(R.id.activity_main_name_input);
        mPassword = (TextInputEditText) findViewById(R.id.activity_main_name_input2);
        mReturn = (ImageButton) findViewById(R.id.ret);
        mlogin = (Button) findViewById(R.id.activity_main_valider);
        mRegister = (Button) findViewById(R.id.register);
        welcome = (TextView) findViewById(R.id.welcome);
        sign = (TextView) findViewById(R.id.sign);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        forgot = (Button) findViewById(R.id.forgot);
        rememberMe = (CheckBox) findViewById(R.id.remember_me);

        //Session
        SessionManager sessionManager = new SessionManager(MainActivity.this,SessionManager.SESSION_REMEMBERME);
        if(sessionManager.checkRememberMe()){
            HashMap<String,String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            mEmail.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONFULLNAME));
            mPassword.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD));
        }


        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ForgotPassword.class);
                startActivity(intent);
            }
        });

        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, firstActivity.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.this, "right-to-left");
                finish();
            }
        });

        startService(new Intent(MainActivity.this, enAppKilled.class));

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Register.class);

                CustomIntent.customType(MainActivity.this, "left-to-right");

                Pair[] pairs = new Pair[5];
                pairs[0]  = new Pair<View,String>(mEmail,"name_trans");
                pairs[1]  = new Pair<View,String>(mPassword,"pass_trans");
                pairs[2]  = new Pair<View,String>(mlogin,"login_trans");
                pairs[3]  = new Pair<View,String>(welcome,"logo_text");
                pairs[4]  = new Pair<View,String>(sign,"sign_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                startActivity(intent, options.toBundle());
                finish();
                return;
            }
        });


        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    getUserInformation();

                }
            }
        };


        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();


                if(rememberMe.isChecked()){
                    //Session
                    SessionManager sessionManager = new SessionManager(MainActivity.this,SessionManager.SESSION_REMEMBERME);
                    sessionManager.createRememberMeSession(email,password);
                }

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"sign in error", Toast.LENGTH_SHORT).show();
                            mProgress.setVisibility(View.GONE);
                        }
                        else {
                            mProgress.setVisibility(View.VISIBLE);

                            //Session
                            SessionManager sessionManager = new SessionManager(MainActivity.this,SessionManager.SESSION_USERSESSION);
                            sessionManager.createLoginSession(email,password);

                            return;
                        }
                    }
                });
            }
        });

    }


    private void getUserInformation() {
        DatabaseReference checkuser = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(currentUserId);
        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Intent intent = new Intent(MainActivity.this,CustomerMap.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(MainActivity.this,DriverMap.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
