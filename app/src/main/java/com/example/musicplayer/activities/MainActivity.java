package com.example.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.musicplayer.FileSearcher;
import com.example.musicplayer.MetadataMp3;
import com.example.musicplayer.R;
import com.example.musicplayer.SongWrapper;
import com.example.musicplayer.util.MusicPlayerUtil;
import com.example.musicplayer.views.SongFileView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ViewGroup _fileViewContainer;
    private SongWrapper _songWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.btnBuscar);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.three_dot_xxhdpi);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.BLACK);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugFileSearcher();
            }
        });
        _songWrapper = new SongWrapper();

    }

    private void debugFileSearcher() {
        FileSearcher fileSearcher = new FileSearcher(".mp3", this);
        fileSearcher.findFilesOnPath(fileSearcher.getRootPath());
        File[] files = fileSearcher.getFiles();
        fileSearcher.printFileUtil(files);
        createFileView(files);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createFileView(File[] files) {
        if (_fileViewContainer == null) {
            _fileViewContainer = findViewById(R.id.songListContainer);
        }

        int length = files.length;
        for (int fileIndex = 0; fileIndex < length; fileIndex++) {
            File currentFile = files[fileIndex];

            if (currentFile == null) continue;
            if (!currentFile.canRead()) continue;

            MetadataMp3 metadataMp3 = new MetadataMp3(currentFile);
            metadataMp3.extractMetadata();

            final SongFileView view = new SongFileView(this);
            view.setNameTextSize(MusicPlayerUtil.spToPx(17, this));
            view.setArtistAlbumNameTextSize(MusicPlayerUtil.spToPx(15, this));
            view.setFileDisplayAlbumName(metadataMp3.getAlbumName());
            view.setFileDisplayArtistName(metadataMp3.getArtistName());
            view.setImage(metadataMp3.getImage());
            view.setReferenceFile(currentFile);
            String title = metadataMp3.getTitle();
            if(title.equals("Unknown Title"))
                view.setFileDisplayName(currentFile.getName());
            else
                view.setFileDisplayName(title);

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (v.getClass() == SongFileView.class) {
//                        Log.d("Movimiento", "clickeo");
//
////                        songClickListener((SongFileView) v);
//                        view.animateTouched();
////                        Intent intent = new Intent(MainActivity.this, SongOptionsActivity.class);
////                        startActivity(intent);
//                    }
//                }
//            });
//
//            view.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    Log.d("Movimiento", "Se mantiene");
//                    view.animateReleaseTouched();
//                    return true;
//                }
//            });

            view.setOnTouchListener(new View.OnTouchListener() {
                private long tInicio = 0;
                private long tFinal = 0;
                private long tDiferencia = 0;
                private boolean cancelado = false;
                private boolean moreOptions = false;
                private boolean animation = false;
                private Handler mHandler;
                private boolean presionado = true;

                @Override
                public boolean onTouch(View v, final MotionEvent event) {
                    int eventAction = event.getAction();

                    switch (eventAction) {
                        case MotionEvent.ACTION_DOWN:
                            Log.d("Contiene", "DOWN");

                            cancelado = false;

                            if(!cancelado)
                            {
                                tInicio = System.currentTimeMillis();
                                int xx = (int) event.getX();
                                int yy = (int) event.getY();
                                if (view.getRectMoreOptions().contains(xx, yy))
                                {
                                    moreOptions = true;
                                    animation = false;
                                }
                                else
                                {
                                    animation = true;
                                    moreOptions = false;
                                    view.animateHoldTouched();
                                    Log.d("Contiene", "Se anima");
                                    if (mHandler == null)
                                    {
                                        Log.d("Contiene", "Se ejecuta");
                                        Log.d("Contiene", "Cancelado: " + cancelado);
                                        presionado = true;
                                        mHandler = new Handler();
                                        mHandler.postDelayed(mAction, 505);

                                    }
                                }

                            }
                            else
                                cancelado = false;
                            break;

                        case MotionEvent.ACTION_CANCEL:
                            view.animateReleaseTouched();
                            cancelado = true;
                            break;

                        case MotionEvent.ACTION_UP:
                            tFinal = System.currentTimeMillis();
                            tDiferencia = tFinal - tInicio;
                            if(animation)
                                view.animateReleaseTouched();

                            if(!cancelado)
                            {
                                if(tDiferencia < 500)
                                {
                                    presionado = false;
                                    if (moreOptions) {
                                        Intent intent = new Intent(MainActivity.this, SongOptionsActivity.class);
                                        startActivity(intent);
                                    } else {
                                        songClickListener((SongFileView) v);
                                    }
                                }
                            }
                            if (mHandler == null) return true;
                            mHandler.removeCallbacks(mAction);
                            mHandler = null;
                            break;
                    }
                    return true;
                }

                Runnable mAction = new Runnable() {
                    @Override public void run() {
                        if(!cancelado && presionado)
                        {
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.EFFECT_TICK));
                            } else {
                                //deprecated in API 26
                                v.vibrate(30);
                            }
                            Toast.makeText(MainActivity.this, "This is my Toast message!",
                                    Toast.LENGTH_LONG).show();
                            view.animateReleaseTouched();
                            animation = false;
                        }
                    }
                };
            });

            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(_fileViewContainer.getWidth(), MusicPlayerUtil.dpToPx(70, this));
            view.setLayoutParams(layout);
            _fileViewContainer.addView(view);
        }
    }

    private void songClickListener(SongFileView songFileView) {
        if (_songWrapper == null) return;

        _songWrapper.play(songFileView.getReferenceFile());
    }


}
