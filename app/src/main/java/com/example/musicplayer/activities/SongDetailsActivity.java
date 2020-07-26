package com.example.musicplayer.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.MP3Metadata;
import com.example.musicplayer.MetaDataWrapperUtil;
import com.example.musicplayer.MusicApplication;
import com.example.musicplayer.R;
import com.example.musicplayer.SongWrapper;

import java.io.File;
import java.util.concurrent.TimeUnit;

import kotlin.Metadata;

public class SongDetailsActivity extends AppCompatActivity
{
    public final static String BUNDLE_SONG_METADATA = "song_metadata";
    private SongWrapper _songWrapper;

    private TextView _songNameTextView;
    private TextView _songLengthTextView;
    private ImageView _songCoverImageView;

    private MP3Metadata _metadata;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_details_activity);

        _songCoverImageView = findViewById(R.id.songDetails_imageSongCover);
        _songNameTextView = findViewById(R.id.songDetails_textViewSongName);
        _songLengthTextView = findViewById(R.id.songDetails_textViewSongDuration);
        _songWrapper = ((MusicApplication)getApplication()).getSongWrapper();

        if(savedInstanceState != null)
            _metadata = savedInstanceState.getParcelable(BUNDLE_SONG_METADATA);
        else
            _metadata = MetaDataWrapperUtil.MP3FromFile(_songWrapper.getCurrentSongFile());

        updateInformation(_metadata);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateInformation(MP3Metadata metadata)
    {
        if(_songWrapper == null || metadata == null) return;

        if(_songNameTextView != null)
            _songNameTextView.setText(metadata.title);

        if(_songCoverImageView != null)
            _songCoverImageView.setImageBitmap(metadata.image);

        int songDuration = _songWrapper.getMediaDuration();
        if(_songLengthTextView != null && songDuration != -1)
        {
            double minutes =(double) TimeUnit.MILLISECONDS.toMinutes(songDuration);
            int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(songDuration) - (int)(minutes * 60);

            _songLengthTextView.setText(String.format("%d:%02d",(int) Math.floor(minutes), seconds));
        }
    }
}
