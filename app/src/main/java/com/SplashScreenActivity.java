package com;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ar.docscanner.R;
import com.ar.docscanner.activities.DashboardActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_SCREEN = 5000;

    Animation topAnim, bottomAnim;
    TextView AppName;
    ImageView LogoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        hooks();

        Animations();

        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class));
                        finish();
                    }
                }, SPLASH_SCREEN);
    }

    private void Animations() {
        AppName.setAnimation(bottomAnim);
        LogoImage.setAnimation(topAnim);
    }

    private void hooks() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        LogoImage = findViewById(R.id.LogoImage);
        AppName = findViewById(R.id.AppName);
    }
}