package com.example.todolist_java;

import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;



public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
        },3000);
        changeStatusColor();
    }
    void changeStatusColor(){
        // Change status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color)); // Change to your desired color resource
    }
}
