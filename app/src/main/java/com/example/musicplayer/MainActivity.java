package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.musicplayer.view.SongFileView;

import java.io.File;

public class MainActivity extends AppCompatActivity
{

    private ViewGroup _fileViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.btnBuscar);


        if(Build.VERSION.SDK_INT >= 23)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugFileSearcher();
            }
        });

    }

    private void debugFileSearcher()
    {
        FileSearcher fileSearcher = new FileSearcher(".mp3", this);
        fileSearcher.findFilesOnPath(fileSearcher.getRootPath());
        File[] files = fileSearcher.getFiles();
        fileSearcher.printFileUtil(files);

        createFileView(files);
    }

    private void createFileView(File[] files)
    {
        if(_fileViewContainer == null){
            _fileViewContainer = findViewById(R.id.songListContainer);
        }

        int length = files.length;
        for (int fileIndex = 0; fileIndex < length; fileIndex++) {
            File currentFile = files[fileIndex];

            if(currentFile == null) continue;
            if(!currentFile.canRead()) continue;

            Button view = new Button(this);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            view.setText(currentFile.getName());

            _fileViewContainer.addView(view);
        }
    }

    private void songClickListener(View v)
    {

    }
}
