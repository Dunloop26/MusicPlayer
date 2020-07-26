package com.example.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.views.SongFileView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ViewGroup _fileViewContainer;
    private SongWrapper _songWrapper;
    private Intent _songDetailsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.btnBuscar);

        if(_songDetailsIntent == null)
            _songDetailsIntent = new Intent(this, MainActivity.class);


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
        FileSearcher.printFileUtil(files);

        //songWrapper.play("/storage/emulated/0/Music/Deezloader Music/Imagine Dragons - Believer.mp3");
//        FileSearcher fileSearcher = new FileSearcher(".mp3", this);
//        fileSearcher.findFilesOnPath(fileSearcher.getRootPath());
//        File[] files = fileSearcher.getFiles();
//        fileSearcher.printFileUtil(files);
//
        createFileView(files);
    }

    private void createFileView(File[] files) {
        if (_fileViewContainer == null) {
            _fileViewContainer = findViewById(R.id.songListContainer);
        }

        _fileViewContainer.removeAllViews();

//        int length = 1;
        for (File currentFile : files) {
            if (currentFile == null) continue;
            if (!currentFile.canRead()) continue;

            SongFileView view = new SongFileView(this);
            setupViewData(this, currentFile, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getClass() == SongFileView.class) {
                        songClickListener((SongFileView) v);
                    }
                }
            });

            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(_fileViewContainer.getWidth(), dpToPx(70, this));
            view.setLayoutParams(layout);
            _fileViewContainer.addView(view);
        }
    }

    private void setupViewData(Context context, File songFile, SongFileView view)
    {
        MP3Metadata metadata = MetaDataWrapperUtil.MP3FromFile(songFile);

        view.setNameTextSize(spToPx(17, context));
        view.setArtistAlbumNameTextSize(spToPx(15, context));

        view.setFileDisplayAlbumName(metadata.albumName);
        view.setFileDisplayArtistName(metadata.artistName);
        view.setImage(metadata.image);
        view.setReferenceFile(songFile);

        String title = metadata.title;
        view.setFileDisplayName(
                title.equals(MetaDataWrapperUtil.UNKNOWN_TITLE)
                        ? songFile.getName()
                        : title);

    }

    private void songClickListener(SongFileView songFileView) {
        if (_songWrapper == null) return;

//        _songWrapper.play(songFileView.getReferenceFile());
//        startActivity(_songDetailsIntent, );
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
