package com.example.mynewproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class driverServiceRegister extends AppCompatActivity {

    private Button carX, carxl, carblack;
    private ProgressBar mProgress;
    private TextInputEditText mCar, mMatricule;
    private String car, number;
    private String userId;
    private FirebaseAuth mAuth;
    private DatabaseReference mCurrentCar, mCurrentService, mCurrentCarNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_service_register);

        carX = (Button) findViewById(R.id.carxtype);
        carxl = (Button) findViewById(R.id.carxltype);
        carblack = (Button) findViewById(R.id.carblacktype);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mCar = (TextInputEditText) findViewById(R.id.car_driverService);
        mMatricule = (TextInputEditText) findViewById(R.id.matricule_driverService);


        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mCurrentCar = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("car");
        mCurrentService = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("service");
        mCurrentCarNumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("carNumber");



        carX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateCar() || !validateMatricule()){
                    return;
                }
                carX.setBackground(getResources().getDrawable(R.drawable.state_driverservice));
                mProgress.setVisibility(View.VISIBLE);

                car = mCar.getText().toString();
                number = mMatricule.getText().toString();

                mCurrentCarNumber.setValue(number);
                mCurrentCar.setValue(car);
                mCurrentService.setValue("CarX");

                Intent intent = new Intent(driverServiceRegister.this,DriverMap.class);
                startActivity(intent);
                finish();

            }
        });

        carxl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateCar() || !validateMatricule()){
                    return;
                }
                carxl.setBackground(getResources().getDrawable(R.drawable.state_driverservice));
                mProgress.setVisibility(View.VISIBLE);

                car = mCar.getText().toString();
                number = mMatricule.getText().toString();

                mCurrentCarNumber.setValue(number);
                mCurrentCar.setValue(car);
                mCurrentService.setValue("CarXL");

                Intent intent = new Intent(driverServiceRegister.this,DriverMap.class);
                startActivity(intent);
                finish();
            }
        });

        carblack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateCar() || !validateMatricule()){
                    return;
                }
                carblack.setBackground(getResources().getDrawable(R.drawable.state_driverservice));
                mProgress.setVisibility(View.VISIBLE);

                car = mCar.getText().toString();
                number = mMatricule.getText().toString();

                mCurrentCarNumber.setValue(number);
                mCurrentCar.setValue(car);
                mCurrentService.setValue("CarBlack");

                Intent intent = new Intent(driverServiceRegister.this,DriverMap.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //validate numberCar
    private boolean validateCar(){
        String val = mCar.getText().toString().trim();
        if((val.isEmpty())){
            mCar.setError("Field can not be empty");
            return false;
        }

        else{
            mCar.setError(null);
            return true;
        }
    }
    //validate numberCar
    private boolean validateMatricule(){
        String val = mMatricule.getText().toString().trim();
        String checkmatricule = "[0-9]+[0-9]+[0-9]+-+[0-4]+[0-9]";
        if((val.isEmpty())){
            mMatricule.setError("Field can not be empty");
            return false;
        }
        else if(!val.matches(checkmatricule)){
            mMatricule.setError("Invalid number car. Must be like: XXX-YY");
            return false;
        }
        else{
            mMatricule.setError(null);
            return true;
        }
    }
}
