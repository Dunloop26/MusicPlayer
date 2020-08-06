package com.example.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.R;

public class SongOptionsActivity extends AppCompatActivity {

    private static final String TAG = "SongOptionsActivity";

    private TextView txtName;
    private TextView txtPlayLater;
    private TextView txtAddTo;
    private TextView txtDelete;
    private TextView txtShare;
    private TextView txtFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_options);
        txtName = (TextView) findViewById(R.id.txtName);
        txtPlayLater = (TextView) findViewById(R.id.txtPlayLater);
        txtAddTo = (TextView) findViewById(R.id.txtAddTo);
        txtDelete = (TextView) findViewById(R.id.txtDelete);
        txtShare = (TextView) findViewById(R.id.txtShare);
        txtFavorite = (TextView) findViewById(R.id.txtFavorite);

        txtPlayLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Play later");
            }
        });

        txtAddTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add to");
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Delete");
            }
        });

        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Share");
            }
        });

        txtFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Favorite");
            }
        });
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