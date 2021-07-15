package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerSetting extends AppCompatActivity {


    private TextView mNameField, mPhoneField, mPseudoField;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private  String userId;
    private String mName, mPseudo;
    private String mPhone;
    private String mProfileImageUrl;
    private ImageView mProfileImage;
    private ImageView mBack;
    private Uri resultUri;
    private RelativeLayout relativepseudo,relativeemail, relativephone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_setting);

        mNameField = (TextView) findViewById(R.id.name);
        mPhoneField = (TextView) findViewById(R.id.phone);
        mPseudoField = (TextView) findViewById(R.id.pseudo);
        mBack = (ImageView) findViewById(R.id.back);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        relativepseudo = (RelativeLayout) findViewById(R.id.relativepseudo);
        relativeemail = (RelativeLayout) findViewById(R.id.relativename);
        relativephone = (RelativeLayout) findViewById(R.id.relativephone);

        relativeemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmailDialog();
            }
        });
        relativephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhoneDialog();
            }
        });
        relativepseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPseudoDialog();
            }
        });



        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);

            }
        });



        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }

    public void showPseudoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerSetting.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(CustomerSetting.this).inflate(R.layout.layout_pseudo_dialog,(LinearLayout) findViewById(R.id.layoutDialogPseudo));
        builder.setView(view);
        builder.setCancelable(false);
        final TextInputEditText newpseudo = ((TextInputEditText) view.findViewById(R.id.dialog_pseudo));
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.confirmPseudo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPseudo = newpseudo.getText().toString();
                if(mPseudo.isEmpty()){
                    newpseudo.setError("Field can not be empty");
                    return;
                }
                else{
                    newpseudo.setError(null);
                    saveUserInformation();
                }
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.cancelPseudo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    public void showPhoneDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerSetting.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(CustomerSetting.this).inflate(R.layout.layout_phone_dialog,(LinearLayout) findViewById(R.id.layoutDialogPhone));
        builder.setView(view);
        builder.setCancelable(false);
        final TextInputEditText newphone = ((TextInputEditText) view.findViewById(R.id.dialog_phone));
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.confirmPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhone = newphone.getText().toString();

                if(mPhone.isEmpty()){
                    newphone.setError("Field can not be empty");
                    return;
                }
                else{
                    newphone.setError(null);
                    saveUserInformation();
                }

                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.cancelPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    public void showEmailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerSetting.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(CustomerSetting.this).inflate(R.layout.layout_email_dialog,(LinearLayout) findViewById(R.id.layoutDialogEmail));
        builder.setView(view);
        builder.setCancelable(false);
        final TextInputEditText newemail = ((TextInputEditText) view.findViewById(R.id.dialog_email));
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.confirmEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mName = newemail.getText().toString();
                if(!mName.equals("")){
                    saveUserInformation();
                }
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.cancelEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }



    private void getUserInfo(){
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    if(map.get("name") != null){
                        mName = map.get("name").toString();
                        mNameField.setText(mName);

                    }

                    if(map.get("phone") != null){
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }

                    if(map.get("pseudo") != null){
                        mPseudo = map.get("pseudo").toString();
                        mPseudoField.setText(mPseudo);
                    }

                    if(map.get("profileImageUrl") != null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void saveUserInformation(){

        Map userInfo = new HashMap();
        userInfo.put("name",mName);
        userInfo.put("phone",mPhone);
        userInfo.put(("pseudo"),mPseudo);
        mCustomerDatabase.updateChildren(userInfo);

        if(resultUri != null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_image").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

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
                            mCustomerDatabase.updateChildren(newImage);

                            finish();
                            return;
                        }
                    });
                }
            });


        }else{
            finish();
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
}
