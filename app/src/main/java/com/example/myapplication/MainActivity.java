package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    public void onImageClick(View view) {
        if (view.getId() == R.id.play) {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.score) {
            Toast.makeText(this, "Score in progress !", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.settings) {
        Toast.makeText(this, "Setting in progress !", Toast.LENGTH_SHORT).show();
        }
    }
}