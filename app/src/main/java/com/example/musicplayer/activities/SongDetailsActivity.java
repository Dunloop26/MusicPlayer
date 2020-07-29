package com.example.musicplayer.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.MP3Metadata;
import com.example.musicplayer.util.MetaDataWrapperUtil;
import com.example.musicplayer.MusicApplication;
import com.example.musicplayer.R;
import com.example.musicplayer.SongWrapper;
import com.example.musicplayer.util.MusicPlayerUtil;

import java.io.File;

public class SongDetailsActivity extends AppCompatActivity implements SongWrapper.OnSongChangeActionListener
{
    public final static String BUNDLE_SONG_METADATA = "song_metadata";
    private SongWrapper _songWrapper;

    private TextView _songNameTextView;
    private TextView _songLengthTextView;
    private TextView _songCurrentTimeTextView;

    private ImageView _songCoverImageView;
    private ImageView _playImageView;
    private ImageView _prevImageView;
    private ImageView _nextImageView;

    private SeekBar _reproductionBar;

    private MP3Metadata _metadata;

    private Handler _progressBarHandler;
    private Handler _changeSongHandler;

    private Runnable _changeSongRunnable;

    private AnimationDrawable _animationFromPauseToPlay;
    private AnimationDrawable _animationFromPlayToPause;

    private int _songDuration;
    private boolean _fromPauseToPlay;
    private boolean _draggedFromUser;
    private boolean _changeFromUser;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_details_activity);


        _songCurrentTimeTextView = findViewById(R.id.songDetails_textViewSongCurrentTime);
        _songLengthTextView = findViewById(R.id.songDetails_textViewSongDuration);
        _songCoverImageView = findViewById(R.id.songDetails_imageSongCover);
        _songNameTextView = findViewById(R.id.songDetails_textViewSongName);
        _reproductionBar = findViewById(R.id.songDetails_reproductionBar);
        _playImageView = findViewById(R.id.songDetails_buttonPlay);
        _prevImageView = findViewById(R.id.songDetails_buttonPrev);
        _nextImageView = findViewById(R.id.songDetails_buttonNext);

        _playImageView.setBackgroundResource(R.drawable.reproduction_animation_from_pause_to_play);
        _prevImageView.setBackgroundResource(R.drawable.prev);
        _nextImageView.setBackgroundResource(R.drawable.next);

        _draggedFromUser = false;
        _changeFromUser = false;
        _fromPauseToPlay = true;


        _songWrapper = ((MusicApplication)getApplication()).getSongWrapper();
        _songWrapper.addOnSongChangeActionListener(this);


        _animationFromPauseToPlay = (AnimationDrawable) _playImageView.getBackground();



        _progressBarHandler = new Handler()
        {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                _songCurrentTimeTextView.setText(MusicPlayerUtil.getFormatTimeFromMilliseconds(msg.what));
                _reproductionBar.setProgress(msg.what);
            }
        };

        _changeSongRunnable = new Runnable() {
            @Override
            public void run() {
                _metadata = MetaDataWrapperUtil.MP3FromFile(_songWrapper.getCurrentSongFile());
                if (_metadata.title.equals(MetaDataWrapperUtil.UNKNOWN_TITLE))
                    _metadata.title = _songWrapper.getCurrentSongFile().getName();
                updateInformation(_metadata);

            }
        };

        _playImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_fromPauseToPlay)
                {
                    if(_songWrapper.isPlaying())
                    {
                        _playImageView.setBackgroundResource(R.drawable.reproduction_animation_from_pause_to_play);
                        _animationFromPauseToPlay = (AnimationDrawable) _playImageView.getBackground();
                        _animationFromPauseToPlay.setVisible(false, true);
                        _animationFromPauseToPlay.start();
                        _fromPauseToPlay = false;
                    }
                }
                else
                {
                    if(!_songWrapper.isPlaying())
                    {
                        _playImageView.setBackgroundResource(R.drawable.reproduction_animation_from_play_to_pause);
                        _animationFromPlayToPause = (AnimationDrawable) _playImageView.getBackground();
                        _animationFromPlayToPause.setVisible(false, true);
                        _animationFromPlayToPause.start();
                        _fromPauseToPlay = true;
                    }
                }
                _songWrapper.continuePlaying();
            }
        });

        _reproductionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    _songCurrentTimeTextView.setText(MusicPlayerUtil.getFormatTimeFromMilliseconds(_reproductionBar.getProgress()));
                    _draggedFromUser = true;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                _draggedFromUser = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                _draggedFromUser = false;
                _songWrapper.play(_songWrapper.getCurrentSongFile(), _reproductionBar.getProgress());
                _changeFromUser = true;
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
        startReproductionBarUpdateThread();
    }

    public void startReproductionBarUpdateThread()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                while(_songWrapper != null)
                {
                    if(_songWrapper.isPlaying() && !_draggedFromUser)
                    {
                        try {
                            Message message = new Message();
                            message.what = _songWrapper.getMediaCurrentPosition();
                            _progressBarHandler.sendMessage(message);
                            if(_changeFromUser)
                            {
                                if(_songWrapper.getMediaDuration() < 5000)
                                    Thread.sleep(100);
                                else
                                    Thread.sleep(1000);
                                _changeFromUser = false;
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

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
                _songCoverImageView.setImageDrawable(getResources().getDrawable(R.drawable.logo1));
            else
                _songCoverImageView.setImageBitmap(metadata.image);
        }
        _songDuration = _songWrapper.getMediaDuration();
        _reproductionBar.setMax(_songDuration);
        if(_songLengthTextView != null && _songDuration != -1)
        {
            _songLengthTextView.setText(MusicPlayerUtil.getFormatTimeFromMilliseconds(_songDuration));
        }
    }

    @Override
    public void onSongChangeAction(SongWrapper sw, File file) {

        if (_changeSongHandler == null)
            _changeSongHandler = new Handler();
        else
            _changeSongHandler.removeCallbacks(_changeSongRunnable);
        _changeSongHandler.postDelayed(_changeSongRunnable, 100);
    }
}

