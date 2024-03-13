package com.example.unixplore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

//import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {
    TextView uniXplore, university;
private static int Splash_timeout=5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        uniXplore=findViewById(R.id.textview1);
        university=findViewById(R.id.textview2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashintent=new Intent(SplashScreen.this,MainActivity.class);
                startActivity(splashintent);
                finish();
            }
        },Splash_timeout);

        Animation myanimation= AnimationUtils.loadAnimation(SplashScreen.this,R.anim.animation2);
        uniXplore.startAnimation(myanimation);

        Animation myanimation2= AnimationUtils.loadAnimation(SplashScreen.this,R.anim.animation1);
        university.startAnimation(myanimation2);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
}