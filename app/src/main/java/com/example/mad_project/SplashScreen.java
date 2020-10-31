package com.example.mad_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private static final int TIME_DURATION = 2000;
    SharedPreferences loginStatus;
    Animation BottomAnim;
    TextView slog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        BottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        slog = findViewById(R.id.textView2);
        slog.setAnimation(BottomAnim);

        loginStatus = getSharedPreferences("SignIn", MODE_PRIVATE);
        if(!loginStatus.contains("Email")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, SignIn.class);
                    startActivity(intent);
                    finish();
                }
            },TIME_DURATION);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            },TIME_DURATION);
        }


    }
}
