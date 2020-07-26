package com.example.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.musicplayer.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ImageView image = (ImageView) findViewById(R.id.imageView);
        int newColor = getResources().getColor(R.color.colorLightModePrimaryText);
        image.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
    }
}