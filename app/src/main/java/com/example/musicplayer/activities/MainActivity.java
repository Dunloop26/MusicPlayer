package com.example.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.musicplayer.FileSearcher;
import com.example.musicplayer.MP3Metadata;
import com.example.musicplayer.PlayList;
import com.example.musicplayer.TouchGestureSolver;
import com.example.musicplayer.util.MetaDataWrapperUtil;
import com.example.musicplayer.MusicApplication;
import com.example.musicplayer.R;
import com.example.musicplayer.SongWrapper;
import com.example.musicplayer.util.MusicPlayerUtil;
import com.example.musicplayer.views.SongFileView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ViewGroup _fileViewContainer;
    private SongWrapper _songWrapper;
	private Intent _songDetailsIntent;
	private PlayList _playList;
	private Handler _addSongViewsHandler;

    @SuppressLint("HandlerLeak")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		_fileViewContainer = findViewById(R.id.songListContainer);

        if (Build.VERSION.SDK_INT >= 23)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

		if(_songDetailsIntent == null)
			_songDetailsIntent = new Intent(this, SongDetailsActivity.class);

        _songWrapper = ((MusicApplication) getApplication()).getSongWrapper();
        _playList = ((MusicApplication) getApplication()).getPlayList();

		_addSongViewsHandler = new Handler()
		{
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(@NonNull Message msg) {
				SongFileView[] songFileViews = (SongFileView[]) msg.obj;
				for(int indexSongView = 0; indexSongView < songFileViews.length; indexSongView++)
				{
					_fileViewContainer.addView(songFileViews[indexSongView]);
				}
			}
		};

        _songWrapper.addOnSongChangeActionListener(new SongWrapper.OnSongChangeActionListener() {
			@Override
			public void onSongChangeAction(SongWrapper sw, File file) {
				onPlayListSongChangeAction(sw, file);
			}
		});
    }

	@Override
	protected void onStart() {
		Runnable loadSongViewsRunnable = new Runnable() {
			@Override
			public void run() {
				SongFileView[] songFileViews = createFileView(executeFileSearcher());
				Message message = new Message();
				message.obj = songFileViews;
				_addSongViewsHandler.sendMessage(message);
			}
		};

		Thread mythread = new Thread(loadSongViewsRunnable);
		mythread.start();
		super.onStart();
	}

	private File[] executeFileSearcher() {
        FileSearcher fileSearcher = new FileSearcher(".mp3", this);
        fileSearcher.findFilesOnPath(fileSearcher.getRootPath());
        File[] files = fileSearcher.getFiles();
        fileSearcher.printFileUtil(files);
        return files;
    }

    int indexSong;
    @SuppressLint("ClickableViewAccessibility")
    private SongFileView[] createFileView(File[] files) {
		int length = files.length;
		SongFileView[] songFileViews = new SongFileView[length];
		for (int fileIndex = 0; fileIndex < length; fileIndex++) {

        	indexSong = fileIndex;

            File currentFile = files[fileIndex];
			_playList.addSong(currentFile);

            if (currentFile == null) continue;
            if (!currentFile.canRead()) continue;

            final SongFileView view = new SongFileView(this);
			setupViewData(this, currentFile, view);
			view.setOnTouchListener(new TouchGestureSolver() {
				private boolean moreOptions = false;
				private boolean animation = false;

				@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int eventAction = event.getAction();

					switch (eventAction) {
						case MotionEvent.ACTION_DOWN:
							int x = (int) event.getX();
							int y = (int) event.getY();
							if (view.getRectMoreOptions().contains(x, y))
							{
								moreOptions = true;
								animation = false;
							}
							else
							{
								animation = true;
								moreOptions = false;
								view.animateHoldTouched();
							}
							break;

						case MotionEvent.ACTION_MOVE:

							break;

						case MotionEvent.ACTION_CANCEL:
							if(animation)
							{
								view.animateReleaseTouched();
							}
							animation = false;
							break;

						case MotionEvent.ACTION_UP:
							if(animation)
								view.animateReleaseTouched();
							break;
					}

					return super.onTouch(v, event);
				}

				@Override
				public void onLongClick() {
					Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					// Vibrate for 30 milliseconds
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.EFFECT_TICK));
					} else {
						//deprecated in API 26 or higher
						v.vibrate(30);
					}
					Toast.makeText(MainActivity.this, "This is my Toast message!",
							Toast.LENGTH_LONG).show();
					if(animation)
						view.animateReleaseTouched();
					animation = false;
				}

				@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
				@Override
				public void onSingleClick(View v) {

					if (moreOptions) {
						Intent intent = new Intent(MainActivity.this, SongOptionsActivity.class);
						startActivity(intent);
					} else {
						songClickListener((SongFileView) v);
					}
				}

			});

            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(_fileViewContainer.getWidth(), MusicPlayerUtil.dpToPx(70, this));
            view.setLayoutParams(layout);
            songFileViews[fileIndex] = view;
        }
		return  songFileViews;
	}

	private void setupViewData(Context context, File songFile, SongFileView view)
	{
		MP3Metadata metadata = MetaDataWrapperUtil.MP3FromFile(songFile);

		view.setNameTextSize(MusicPlayerUtil.spToPx(17, context));
		view.setArtistAlbumNameTextSize(MusicPlayerUtil.spToPx(15, context));

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

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	private void songClickListener(SongFileView songFileView) {
		if (_songWrapper == null) return;
		_songWrapper.play(songFileView.getReferenceFile());
		_playList.setIndexFromSong(songFileView.getReferenceFile());
		MP3Metadata metadata = MetaDataWrapperUtil.MP3FromFile(songFileView.getReferenceFile());


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
