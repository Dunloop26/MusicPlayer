package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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

            SongFileView view = new SongFileView(this);
            view.setFileDisplayName(currentFile.getName());
            view.setFileDisplayTextSize(getWindow().getWindowManager().getDefaultDisplay().getWidth() / 25f);
            view.setReferenceFile(currentFile);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getClass() == SongFileView.class) {
                        songClickListener((SongFileView) v);
                    }
                }
            });

            _fileViewContainer.addView(view);
        }
    }

    private void songClickListener(SongFileView songFileView) {
        if (_songWrapper == null) return;

        _songWrapper.Play(songFileView.getReferenceFile());
    }
}
