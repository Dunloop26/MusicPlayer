package com.example.musicplayer.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.MP3Metadata;
import com.example.musicplayer.util.MetaDataWrapperUtil;
import com.example.musicplayer.MusicApplication;
import com.example.musicplayer.R;
import com.example.musicplayer.SongWrapper;
import com.example.musicplayer.util.MusicPlayerUtil;

import java.util.concurrent.TimeUnit;

public class SongDetailsActivity extends AppCompatActivity
{
    public final static String BUNDLE_SONG_METADATA = "song_metadata";
    private SongWrapper _songWrapper;

    private TextView _songNameTextView;
    private TextView _songLengthTextView;
    private TextView _songCurrentTIme;
    private ImageView _songCoverImageView;
    private SeekBar _reproductionBar;

    private Button _btnPlay;

    private MP3Metadata _metadata;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_details_activity);

        _songCoverImageView = findViewById(R.id.songDetails_imageSongCover);
        _songNameTextView = findViewById(R.id.songDetails_textViewSongName);
        _songLengthTextView = findViewById(R.id.songDetails_textViewSongDuration);
        _songCurrentTIme = findViewById(R.id.songDetails_textViewSongCurrentTime);
        _reproductionBar = findViewById(R.id.songDetails_reproductionBar);
        _songWrapper = ((MusicApplication)getApplication()).getSongWrapper();
        _btnPlay = findViewById(R.id.songDetails_buttonPlay);

        _btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _songWrapper.continuePlaying();
            }
        });

        _reproductionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                _songWrapper.play(_songWrapper.getCurrentSongFile(), _reproductionBar.getProgress());
            }
        });


        if(savedInstanceState != null)
            _metadata = savedInstanceState.getParcelable(BUNDLE_SONG_METADATA);
        else
        {
            _metadata = MetaDataWrapperUtil.MP3FromFile(_songWrapper.getCurrentSongFile());
            if(_metadata.title.equals(MetaDataWrapperUtil.UNKNOWN_TITLE))
                _metadata.title = _songWrapper.getCurrentSongFile().getName();
        }

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
        {
            if(metadata.image == null)
                if(Build.VERSION.SDK_INT >= 16)
                    _songCoverImageView.setBackground(getResources().getDrawable(R.drawable.logo1));
                else
                    _songCoverImageView.setBackgroundResource(R.drawable.logo1);
            else
                _songCoverImageView.setImageBitmap(metadata.image);
        }

        int songDuration = _songWrapper.getMediaDuration();
        _reproductionBar.setMax(songDuration);
        if(_songLengthTextView != null && songDuration != -1)
        {
            _songLengthTextView.setText(MusicPlayerUtil.getFormatTimeFromMilliseconds(songDuration));
        }
    }
}

