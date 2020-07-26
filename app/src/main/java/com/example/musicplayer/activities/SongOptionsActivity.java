package com.example.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.example.musicplayer.R;

public class SongOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_options);



    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        View view = getWindow().getDecorView();
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = width;
        layoutParams.height = (int) (height * 0.55f);
        getWindowManager().updateViewLayout(view, layoutParams);
    }
}