package com.example.mynewproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class firstActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5000;

    Animation topAnim, bottomAnim;
    ImageView image;
    TextView slogan;
    LinearLayout mbutton;
    Button mlogin, mRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim =  AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        image = findViewById(R.id.imageslogan);
        slogan = findViewById(R.id.welcome);
        mbutton = findViewById(R.id.mbutton);
        mlogin = findViewById(R.id.mlogin);
        mRegister = (Button) findViewById(R.id.register);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(firstActivity.this,Register.class);
                startActivity(intent);
                finish();
            }
        });

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(firstActivity.this,MainActivity.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View,String>(image,"logo_image");
                pairs[1] = new Pair<View,String>(slogan,"logo_text");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(firstActivity.this,pairs);
                startActivity(intent, options.toBundle());
                finish();
            }
        });

        image.setAnimation(topAnim);
        mbutton.setAnimation(bottomAnim);



    }
}
