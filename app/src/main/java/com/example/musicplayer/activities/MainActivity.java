package com.example.musicplayer.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.musicplayer.FileSearcher;
import com.example.musicplayer.MP3Metadata;
import com.example.musicplayer.PlayList;
import com.example.musicplayer.SongViewAdapter;
import com.example.musicplayer.TouchGestureSolver;
import com.example.musicplayer.util.MetaDataWrapperUtil;
import com.example.musicplayer.MusicApplication;
import com.example.musicplayer.R;
import com.example.musicplayer.SongWrapper;
import com.example.musicplayer.util.MusicPlayerUtil;
import com.example.musicplayer.views.SongFileView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SongViewAdapter.OnSongListener {

	public static final String TAG = "MainActivity";
    private SongWrapper _songWrapper;
	private Intent _songDetailsIntent;
	private Intent _songOptionsIntent;
	private PlayList _playList;
	private ArrayList<File> _files;

	private RecyclerView recyclerView;
	private SongViewAdapter mAdapter;
	private RecyclerView.LayoutManager layoutManager;

    @SuppressLint("HandlerLeak")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Hola", "inicia");

        if (Build.VERSION.SDK_INT >= 23)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

		if(_songDetailsIntent == null)
			_songDetailsIntent = new Intent(this, SongDetailsActivity.class);
		if(_songOptionsIntent == null)
			_songOptionsIntent = new Intent(this, SongOptionsActivity.class);

        _songWrapper = ((MusicApplication) getApplication()).getSongWrapper();
        _playList = ((MusicApplication) getApplication()).getPlayList();
		_songWrapper.addOnSongChangeActionListener(new SongWrapper.OnSongChangeActionListener() {
			@Override
			public void onSongChangeAction(SongWrapper sw, File file) {
				onPlayListSongChangeAction(sw, file);
			}
		});

		recyclerView = (RecyclerView) findViewById(R.id.songListContainer);

		recyclerView.setHasFixedSize(true);

		layoutManager = new LinearLayoutManager(this);
		_files = executeFileSearcher();
		_playList.setSongList(_files);



		MP3Metadata[] metadataSongs = new MP3Metadata[_files.size()];

		for(int i = 0; i < metadataSongs.length; i++) {
			metadataSongs[i] = MetaDataWrapperUtil.MP3FromFile(_files.get(i));
		}

		recyclerView.setLayoutManager(layoutManager);

		mAdapter = new SongViewAdapter(metadataSongs, this);
		recyclerView.setAdapter(mAdapter);
    }

	@Override
	protected void onStart() {

		super.onStart();
	}

	private ArrayList<File> executeFileSearcher() {
        FileSearcher fileSearcher = new FileSearcher(".mp3", this);
        fileSearcher.findFilesOnPath(fileSearcher.getRootPath());
        ArrayList<File> files = fileSearcher.getFiles();
        fileSearcher.printFileUtil(files);
        return files;
    }

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onClickSong(int position) {
    	onPlaySong(_files.get(position));
	}

	@Override
	public void onLongClickSong(int position) {
		Toast.makeText(this, "This is a message", Toast.LENGTH_LONG).show();
    }

	@Override
	public void onClickSongOptions(int position) {
    	startActivity(_songOptionsIntent);
	}
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	private void onPlaySong(File file) {
		if (_songWrapper == null) return;
		_songWrapper.play(file);
		_playList.setIndexFromSong(file);
		MP3Metadata metadata = MetaDataWrapperUtil.MP3FromFile(file);


		Bundle options = new Bundle();
		options.putParcelable(SongDetailsActivity.BUNDLE_SONG_METADATA, metadata);
		startActivity(_songDetailsIntent, options);
	}

	public void onPlayListSongChangeAction(SongWrapper sw, File file) {
		_playList.increaseSongIndex();
		file = _playList.getCurrentSong();

		_songWrapper.play(file);
	}
}
