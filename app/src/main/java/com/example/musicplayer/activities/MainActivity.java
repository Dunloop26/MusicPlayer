package com.example.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.example.musicplayer.FileSearcher;
import com.example.musicplayer.MusicNotification;
import com.example.musicplayer.models.MP3Metadata;
import com.example.musicplayer.PlayList;
import com.example.musicplayer.adapters.SongViewAdapter;
import com.example.musicplayer.util.MetaDataWrapperUtil;
import com.example.musicplayer.MusicApplication;
import com.example.musicplayer.R;
import com.example.musicplayer.SongWrapper;
import com.example.musicplayer.viewmodels.MainActivityViewModel;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SongViewAdapter.OnSongListener {

	public static final String TAG = "MainActivity";
    private SongWrapper mSongWrapper;
	private Intent mSongDetailsIntent;
	private Intent mSongOptionsIntent;
	private PlayList mPlayList;

	private NotificationManager mNotificationManager;

	private MainActivityViewModel mMainActivityViewModel;

	private static final int EXTERNAL_STORAGE = 0;

	private RecyclerView mRecyclerView;
	private SongViewAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;

    @SuppressLint("HandlerLeak")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if(mSongDetailsIntent == null)
			mSongDetailsIntent = new Intent(this, SongDetailsActivity.class);
		if(mSongOptionsIntent == null)
			mSongOptionsIntent = new Intent(this, SongOptionsActivity.class);

        mSongWrapper = ((MusicApplication) getApplication()).getSongWrapper();
        mPlayList = ((MusicApplication) getApplication()).getPlayList();
		mSongWrapper.addOnSongChangeActionListener(new SongWrapper.OnSongChangeActionListener() {
			@Override
			public void onSongChangeAction(SongWrapper sw, File file) {
				onPlayListSongChangeAction(sw, file);
			}
		});

		mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
		mMainActivityViewModel.init();

		mMainActivityViewModel.getMetadataSongs().observe(this, new Observer<List<MP3Metadata>>() {
			@Override
			public void onChanged(List<MP3Metadata> mp3Metadata) {
				mAdapter.notifyDataSetChanged();
			}
		});

		if(Build.VERSION.SDK_INT >= 23) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
					PackageManager.PERMISSION_GRANTED)
				initRecyclerView();
			else
				requestPermissions();
		}
		else
			initRecyclerView();

		mPlayList.setSongList(mMainActivityViewModel.getFiles());
		if(Build.VERSION.SDK_INT >= 26) {
			createNotificationChannel();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public void createNotificationChannel() {
		NotificationChannel channel = new NotificationChannel(MusicNotification.CHANNEL_ID,
				"Music", NotificationManager.IMPORTANCE_LOW);

		mNotificationManager = getSystemService(NotificationManager.class);
		if(mNotificationManager != null) {
			mNotificationManager.createNotificationChannel(channel);
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

	private void initRecyclerView() {
		mRecyclerView = (RecyclerView) findViewById(R.id.songListContainer);
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(this);

		mRecyclerView.setLayoutManager(mLayoutManager);

		mAdapter = new SongViewAdapter(mMainActivityViewModel.getMetadataSongs().getValue(), this);
		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onClickSong(int position) {
    	mPlayList.setIndexCurrentSong(position);
    	onPlaySong(mPlayList.getCurrentSong());
	}

	@Override
	public void onLongClickSong(int position) {
		Toast.makeText(this, "This is a message", Toast.LENGTH_LONG).show();
    }

	@Override
	public void onClickSongOptions(int position) {
    	startActivity(mSongOptionsIntent);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	private void onPlaySong(File file) {
		if (mSongWrapper == null) return;
//		mSongWrapper.initMediaPlayer(this);
		mSongWrapper.play(file);
		mPlayList.setIndexFromSong(file);
		MP3Metadata metadata = MetaDataWrapperUtil.MP3FromFile(file);

		MusicNotification.createNotification(this, metadata,
				R.drawable.ic_pause, R.drawable.ic_shuffle, R.drawable.ic_favorite);


		Bundle options = new Bundle();
		options.putParcelable(SongDetailsActivity.BUNDLE_SONG_METADATA, metadata);
		startActivity(mSongDetailsIntent, options);
	}

	public void onPlayListSongChangeAction(SongWrapper sw, File file) {
		mPlayList.increaseSongIndex();
		file = mPlayList.getCurrentSong();
		mSongWrapper.play(file);
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case EXTERNAL_STORAGE:
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 &&
						grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					initRecyclerView();
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mNotificationManager.cancelAll();
	}
}


