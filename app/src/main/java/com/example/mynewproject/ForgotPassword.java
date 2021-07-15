package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private TextInputEditText email;
    private Button send;
    private ImageView back;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = (TextInputEditText) findViewById(R.id.email_forgetpassword);
        send = (Button) findViewById(R.id.send_forgetpassword);
        back = (ImageView) findViewById(R.id.back_forgetpassword);

        mAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString();

                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ForgotPassword.this,"Please enter your email",Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPassword.this,"Please check your email",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgotPassword.this,MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                String message = task.getException().getMessage();
                                Toast.makeText(ForgotPassword.this,"Error" +message,Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });
    }
}
