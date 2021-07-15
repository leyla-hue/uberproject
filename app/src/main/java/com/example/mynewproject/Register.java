package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

import static maes.tech.intentanim.CustomIntent.customType;

public class Register extends AppCompatActivity {
    private TextInputEditText mEmail, mPseudo;
    private TextInputEditText mPassword;
    private TextInputEditText mPhone;
    private TextInputEditText mCar;
    private Uri resultUri;
    private String mService;
    private Button mRegister, mLogin;
    private RadioButton mCustomer;
    private RadioButton mDriver;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ImageButton mReturn;
    private RadioGroup group;
    private ImageView mProfileImage;
    private LinearLayout linearDriver;
    private DatabaseReference mDriverDatabase, mCustomerDatabase;
    private TextView mWelcome, signUp;
    private ProgressBar progressBar;
    private Boolean correctFields=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = (TextInputEditText) findViewById(R.id.activity_register_name_input);
        mPseudo =  (TextInputEditText) findViewById(R.id.activity_register_pseudo_input);
        mPassword = (TextInputEditText) findViewById(R.id.activity_register_password_input);
        mPhone = (TextInputEditText) findViewById(R.id.activity_register_phone_input);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mRegister = (Button) findViewById(R.id.activity_register_val);
        mLogin = (Button) findViewById(R.id.activity_register_login);
        mCustomer = (RadioButton) findViewById(R.id.radio1);
        mDriver = (RadioButton) findViewById(R.id.radio2);
        mReturn = (ImageButton) findViewById(R.id.ret);
        group = (RadioGroup) findViewById(R.id.group);
        mWelcome = (TextView) findViewById(R.id.activity_register_txt);
        signUp = (TextView) findViewById(R.id.signup);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        mCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomer.setChecked(true);
                mDriver.setChecked(false);
            }
        });
        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomer.setChecked(false);
                mDriver.setChecked(true);
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,MainActivity.class);
                CustomIntent.customType(Register.this, "right-to-left");
                startActivity(intent);
                finish();
                return;

            }
        });


        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });


        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Register.this,firstActivity.class);
                startActivity(intent);
                finish();

            }
        });

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    if(mCustomer.isChecked()){
                        Intent intent = new Intent(Register.this, CustomerMap.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    else{
                        Intent intent = new Intent(Register.this, driverServiceRegister.class);
                        startActivity(intent);
                        finish();
                        return;

                    }
                }
            }
        };

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString();
                final String pseudo = mPseudo.getText().toString();
                final String password = mPassword.getText().toString();
                final String phone = mPhone.getText().toString();

                if(!validateName() || !validatePassword() || !validatePseudo() || !validateNumber()){
                    return;
                }

                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Register.this, "sign up error", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {

                                String user_id = mAuth.getCurrentUser().getUid();

                                mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id);
                                mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);

                                if (resultUri != null) {
                                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_image").child(user_id);
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                                    byte[] data = baos.toByteArray();
                                    final UploadTask uploadTask = filePath.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            finish();
                                            return;
                                        }
                                    });

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Map newImage = new HashMap();
                                                    newImage.put("profileImageUrl", uri.toString());
                                                    if (mCustomer.isChecked()) {
                                                        mCustomerDatabase.updateChildren(newImage);
                                                    } else {
                                                        mDriverDatabase.updateChildren(newImage);
                                                    }
                                                    finish();
                                                    return;
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    finish();
                                }
                                if (mCustomer.isChecked()) {
                                    DatabaseReference current_user_id = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id).child("name");
                                    DatabaseReference current_phone = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id).child("phone");
                                    DatabaseReference current_pseudo = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id).child("pseudo");
                                    DatabaseReference current_password = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id).child("password");
                                    current_user_id.setValue(email);
                                    current_phone.setValue(phone);
                                    current_pseudo.setValue(pseudo);
                                    current_password.setValue(password);
                                } else {
                                    DatabaseReference current_user_id = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("name");
                                    DatabaseReference current_phone = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("phone");
                                    DatabaseReference current_pseudo = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("pseudo");
                                    DatabaseReference current_password = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("password");
                                    current_user_id.setValue(email);
                                    current_phone.setValue(phone);
                                    current_pseudo.setValue(pseudo);
                                    current_password.setValue(password);

                                }
                            }
                        }
                    });

            }
        });
    }

    //validate email
    private boolean validateName(){
        String val = mEmail.getText().toString().trim();
        String checkEmail = "[a-zA-Z0-0._-]+@[a-z]+\\.+[a-z]+";
        if((val.isEmpty())){
            mEmail.setError("Field can not be empty");
            return false;
        }
        else if(!val.matches(checkEmail)){
            mEmail.setError("Email invalid");
            return false;
        }
        else{
            mEmail.setError(null);
            return true;
        }
    }
    //validate pseudo
    private boolean validatePseudo(){
        String val = mPseudo.getText().toString().trim();
        if((val.isEmpty())){
            mPseudo.setError("Field can not be empty");
            return false;
        }
        else{
            mPseudo.setError(null);
            return true;
        }
    }
    //validate pseudo
    private boolean validateNumber(){
        String val = mPhone.getText().toString().trim();
        if((val.isEmpty())){
            mPhone.setError("Field can not be empty");
            return false;
        }
        else if(val.length()!=10){
            mPhone.setError("Phone invalid");
            return false;
        }
        else{
            mPseudo.setError(null);
            return true;
        }
    }
    //validate editText
    private boolean validatePassword(){
        String val = mPassword.getText().toString().trim();
        if((val.isEmpty())){
            mPassword.setError("Field can not be empty");
            return false;
        }
        else if(val.length()<6){
            mPassword.setError("Password must have more then 6 caracters");
            return false;
        }
        else{
            mPassword.setError(null);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);

        }
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
