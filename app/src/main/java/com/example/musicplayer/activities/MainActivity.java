package com.example.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getClass() == SongFileView.class) {
//                        songClickListener((SongFileView) v);
                        view.animateTouched();
                        Intent intent = new Intent(MainActivity.this, SongOptionsActivity.class);
                        startActivity(intent);
                    }
                }
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
