package com.example.musicplayer.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;

import static androidx.activity.result.contract.ActivityResultContracts.*;

public class MainActivity extends AppCompatActivity implements SongViewAdapter.OnSongListener {

	public static final String TAG = "MainActivity";
    private SongWrapper _songWrapper;
	private Intent _songDetailsIntent;
	private Intent _songOptionsIntent;
	private PlayList _playList;
	private ArrayList<File> _files;

	private static final int EXTERNAL_STORAGE = 0;

	private RecyclerView recyclerView;
	private SongViewAdapter mAdapter;
	private RecyclerView.LayoutManager layoutManager;

    @SuppressLint("HandlerLeak")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

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

		if(Build.VERSION.SDK_INT >= 23) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
					PackageManager.PERMISSION_GRANTED) {
				loadSongs();
			} else {
				requestPermissions();
			}
		}
		else {
			loadSongs();
		}
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
	private void requestPermissions() {
		if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
			String msg = "Se necesita el permiso de almacenamiento para poder buscar los archivos de musica";
			new AlertDialog.Builder(this)
					.setTitle("¡Se necesitan permisos!")
					.setMessage(msg)
					.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE);
						}
					})
					.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.show();
		}
		else {
			ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE);
		}
	}

	private void loadSongs() {
		recyclerView = (RecyclerView) findViewById(R.id.songListContainer);
		recyclerView.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(this);

		if(_files == null)
			_files = executeFileSearcher();

		if(_files != null){
			_playList.setSongList(_files);

			MP3Metadata[] metadataSongs = new MP3Metadata[_files.size()];

			for(int i = 0; i < metadataSongs.length; i++) {
				metadataSongs[i] = MetaDataWrapperUtil.MP3FromFile(_files.get(i));
			}

			recyclerView.setLayoutManager(layoutManager);

			mAdapter = new SongViewAdapter(metadataSongs, this);
			recyclerView.setAdapter(mAdapter);
		}
	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	private ArrayList<File> executeFileSearcher() {
    	//TODO: Cambiar el sistema de busqueda de archivos para hacerlo compatible con android 11
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

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case EXTERNAL_STORAGE:
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 &&
						grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					loadSongs();
				}  else {
					requestPermissions();
				}
				return;
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.toolbar_widgets, menu);
		getSupportActionBar().setTitle("");
		return true;
	}
}


