package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.iv_logo);
        TextView tvAppName = findViewById(R.id.tv_app_name);

        // Logo fade in animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(800);
        fadeIn.setFillAfter(true);

        // Name slide up animation
        TranslateAnimation slideUp = new TranslateAnimation(0, 0, 50, 0);
        slideUp.setDuration(600);
        slideUp.setStartOffset(400); // Start after logo begins to fade in
        slideUp.setFillAfter(true);

        ivLogo.startAnimation(fadeIn);
        tvAppName.startAnimation(slideUp);
        ivLogo.setVisibility(View.VISIBLE);
        tvAppName.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, 2500);
    }
}
