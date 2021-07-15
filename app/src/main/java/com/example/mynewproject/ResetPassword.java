package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResetPassword extends AppCompatActivity {

    private TextInputEditText oldpassword, newpassword, confirmnewpassword;
    private Button mConfirm;
    private String reelOldPassword, setTextOldPassword, setTextNewPassword,setTextConfirmNewPassword, customerOrDriver;
    private ImageView mBack;
    private FirebaseAuth mAuth;
    private DatabaseReference mPasswordInfo;
    private String userId;
    private FirebaseUser user;
    String textDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        customerOrDriver = getIntent().getExtras().getString("customerOrDriver");
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        user = mAuth.getCurrentUser();

        oldpassword = (TextInputEditText) findViewById(R.id.oldPassword);
        newpassword = (TextInputEditText) findViewById(R.id.newpassword);
        confirmnewpassword = (TextInputEditText) findViewById(R.id.newpasswordVerification);
        mConfirm = (Button) findViewById(R.id.confirm);
        mBack = (ImageView) findViewById(R.id.backpasswordreset);

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });

        getPasswordInfo();
    }

    private void getPasswordInfo() {
        mPasswordInfo = FirebaseDatabase.getInstance().getReference().child("Users").child(customerOrDriver).child(userId);
        mPasswordInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("password") != null) {
                        reelOldPassword = map.get("password").toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInfo(){
        setTextOldPassword = oldpassword.getText().toString();
        setTextNewPassword = newpassword.getText().toString();
        setTextConfirmNewPassword = confirmnewpassword.getText().toString();

        if(reelOldPassword.equals(setTextOldPassword)){
                if(setTextNewPassword.equals(setTextConfirmNewPassword)){
                    Map passwordinfo = new HashMap();
                    passwordinfo.put("password",setTextNewPassword);
                    mPasswordInfo.updateChildren(passwordinfo);
                    user.updatePassword(setTextNewPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ResetPassword.this,"Password reset successfully",Toast.LENGTH_LONG).show();
                        }
                    });
                    finish();
                }
                else{
                    textDialog="Reconfirm your new password please";
                    showEmailDialog();
                }
            }
            else{
                textDialog="Reconfirm your old password please";
                showEmailDialog();
            }
        }

    public void showEmailDialog(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ResetPassword.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ResetPassword.this).inflate(R.layout.resetpassword_dialog,(LinearLayout) findViewById(R.id.layoutDialogResetPassword));
        builder.setView(view);
        final TextView newtext = ((TextView) view.findViewById(R.id.textPassword));
        newtext.setText(textDialog);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.confirmPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.cancelPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                finish();
            }
        });

        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


}
