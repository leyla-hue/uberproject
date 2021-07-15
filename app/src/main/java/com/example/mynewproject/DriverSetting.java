package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverSetting extends AppCompatActivity {


    private TextView mNameField, mPhoneField,mPseudoField, mCarField, mCarNumField, mServiceField;
    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;
    private  String userId;
    private String mName, mPhone, mProfileImageUrl,mCar,mCarNumber,mPseudo, mService;
    private ImageView mBack;
    private ImageView mProfileImage;
    private Uri resultUri;
    private TextView navName;
    private RelativeLayout relativeName, relativePseudo, relativePhone, relativeCarNumber, relativeCar, relativeService;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_setting);

        mNameField = (TextView) findViewById(R.id.name);
        mPseudoField = (TextView) findViewById(R.id.pseudo);
        mCarNumField = (TextView) findViewById(R.id.matricule);
        mServiceField = (TextView) findViewById(R.id.service);
        mPhoneField = (TextView) findViewById(R.id.phone);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mCarField = (TextView) findViewById(R.id.car);
        navName =  (TextView) findViewById(R.id.nav_name);
        mBack = (ImageView) findViewById(R.id.back);


        relativeName = (RelativeLayout) findViewById(R.id.relativename);
        relativePseudo = (RelativeLayout) findViewById(R.id.relativepseudo);
        relativePhone = (RelativeLayout) findViewById(R.id.relativephone);
        relativeCarNumber = (RelativeLayout) findViewById(R.id.relativematricule);
        relativeCar = (RelativeLayout) findViewById(R.id.relativecar);
        relativeService=(RelativeLayout) findViewById(R.id.relativeservice);

        relativeService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showServiceDialog();
            }
        });

        relativeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNameDialog();
            }
        });
        relativePseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPseudoDialog();
            }
        });
        relativePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhoneDialog();
            }
        });
        relativeCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCarDialog();
            }
        });
        relativeCarNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCarNumberDialog();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId);


        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        getUserInfo();


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });

    }

    private void showServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverSetting.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DriverSetting.this).inflate(R.layout.layout_service_dialog,(LinearLayout) findViewById(R.id.layoutDialogService));
        builder.setView(view);
        builder.setCancelable(false);
        final RadioButton xservice = ((RadioButton) view.findViewById(R.id.uberx));
        final RadioButton xlservice = ((RadioButton) view.findViewById(R.id.uberxl));
        final RadioButton blackservice = ((RadioButton) view.findViewById(R.id.uberblack));

        final TextView textcarx = ((TextView) view.findViewById(R.id.textcarx));
        final TextView textcarxl = ((TextView) view.findViewById(R.id.textcarxl));
        final TextView textcarblack = ((TextView) view.findViewById(R.id.textcarblack));

        final Button serviceMode = ((Button)view.findViewById(R.id.servicemode));

        xservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xservice.setChecked(true);
                xlservice.setChecked(false);
                blackservice.setChecked(false);

                textcarx.setVisibility(View.VISIBLE);
                textcarxl.setVisibility(View.GONE);
                textcarblack.setVisibility(View.GONE);

                mService = "CarX";
                serviceMode.setText("Economic");
            }
        });
        xlservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xservice.setChecked(false);
                xlservice.setChecked(true);
                blackservice.setChecked(false);

                textcarx.setVisibility(View.GONE);
                textcarxl.setVisibility(View.VISIBLE);
                textcarblack.setVisibility(View.GONE);

                mService = "CarXL";
                serviceMode.setText("Family");
            }
        });
        blackservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xservice.setChecked(false);
                xlservice.setChecked(false);
                blackservice.setChecked(true);

                textcarx.setVisibility(View.GONE);
                textcarxl.setVisibility(View.GONE);
                textcarblack.setVisibility(View.VISIBLE);

                mService = "CarBlack";
                serviceMode.setText("Business");
            }
        });

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.confirmService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mService.equals("")){
                    saveUserInformation();
                }
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.cancelService).setOnClickListener(new View.OnClickListener() {
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
    private void showCarNumberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverSetting.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DriverSetting.this).inflate(R.layout.layout_carnumber_dialog,(LinearLayout) findViewById(R.id.layoutDialogNumberCar));
        builder.setView(view);
        builder.setCancelable(false);
        final TextInputEditText newnumbercar = ((TextInputEditText) view.findViewById(R.id.dialog_numberCar));
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.confirmNumberCar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCarNumber = newnumbercar.getText().toString();
                if(mCarNumber.isEmpty()){
                    newnumbercar.setError("Field can not be empty");
                    return;
                }
                else{
                    newnumbercar.setError(null);
                    saveUserInformation();
                }
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.cancelNumberCar).setOnClickListener(new View.OnClickListener() {
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
    private void showCarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverSetting.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DriverSetting.this).inflate(R.layout.layout_car_dialog,(LinearLayout) findViewById(R.id.layoutDialogCar));
        builder.setView(view);
        builder.setCancelable(false);
        final TextInputEditText newcar = ((TextInputEditText) view.findViewById(R.id.dialog_car));
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.confirmCar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCar = newcar.getText().toString();
                if(mCar.isEmpty()){
                    newcar.setError("Field can not be empty");
                    return;
                }
                else{
                    newcar.setError(null);
                    saveUserInformation();
                }
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.cancelCar).setOnClickListener(new View.OnClickListener() {
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
    private void showPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverSetting.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DriverSetting.this).inflate(R.layout.layout_phone_dialog,(LinearLayout) findViewById(R.id.layoutDialogPhone));
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
    private void showPseudoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverSetting.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DriverSetting.this).inflate(R.layout.layout_pseudo_dialog,(LinearLayout) findViewById(R.id.layoutDialogPseudo));
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
    private void showNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverSetting.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DriverSetting.this).inflate(R.layout.layout_email_dialog,(LinearLayout) findViewById(R.id.layoutDialogEmail));
        builder.setView(view);
        builder.setCancelable(false);
        final TextInputEditText newemail = ((TextInputEditText) view.findViewById(R.id.dialog_email));
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.confirmEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mName = newemail.getText().toString();
                if(mName.isEmpty()){
                    newemail.setError("Field can not be empty");
                    return;
                }
                else{
                    newemail.setError(null);
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
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    if(map.get("name") != null){
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                    }
                    if(map.get("pseudo") != null){
                        mPseudo = map.get("pseudo").toString();
                        mPseudoField.setText(mPseudo);
                    }

                    if(map.get("phone") != null){
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }

                    if(map.get("car") != null){
                        mCar = map.get("car").toString();
                        mCarField.setText(mCar);
                    }

                    if(map.get("carNumber") != null){
                        mCarNumber = map.get("carNumber").toString();
                        mCarNumField.setText(mCarNumber);
                    }
                    if(map.get("service") != null){
                        mService = map.get("service").toString();
                        mServiceField.setText(mService);
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
        userInfo.put("car",mCar);
        userInfo.put("pseudo",mPseudo);
        userInfo.put("carNumber",mCarNumber);
        userInfo.put("service",mService);

        mDriverDatabase.updateChildren(userInfo);

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
                            mDriverDatabase.updateChildren(newImage);

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

