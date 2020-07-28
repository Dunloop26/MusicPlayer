package com.example.musicplayer.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.musicplayer.FileSearcher;
import com.example.musicplayer.MP3Metadata;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.btnBuscar);

        if (Build.VERSION.SDK_INT >= 23)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

		if(_songDetailsIntent == null)
			_songDetailsIntent = new Intent(this, SongDetailsActivity.class);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugFileSearcher();
            }
        });
        _songWrapper = ((MusicApplication) getApplication()).getSongWrapper();

    }

    private void debugFileSearcher() {
        FileSearcher fileSearcher = new FileSearcher(".mp3", this);
        fileSearcher.findFilesOnPath(fileSearcher.getRootPath());
        File[] files = fileSearcher.getFiles();
        fileSearcher.printFileUtil(files);
        createFileView(files);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void createFileView(File[] files) {
        if (_fileViewContainer == null)
            _fileViewContainer = findViewById(R.id.songListContainer);

		_fileViewContainer.removeAllViews();

//        int length = files.length;
		int length = 4;
        for (int fileIndex = 0; fileIndex < length; fileIndex++) {
            File currentFile = files[fileIndex];

            if (currentFile == null) continue;
            if (!currentFile.canRead()) continue;

            final SongFileView view = new SongFileView(this);
			setupViewData(this, currentFile, view);


			// TODO : Refactorizar el codigo de los gestos
			//		Aqui un ejemplo.
			//			TouchGestureSolver _t;
			//			_t.setOnTouchUpAction = new TouchSolverMethod(TouchGestureSolver t)
			//			{
			//				if(t.getTouchDuration() > 3000)
			//				playLiftAnimation()
			//			}
			//			_t.setOnTouchDownAction
			//			{
			//				playTouchAnimation()
			//			}
			//			view.setOnTouchListener(_t);
			//			TouchGestureSolver implements View.TouchListener
			//			{
			//				onTouch()
			//				{
			//					onTouchAction(this)
			//				}
			//			}
            view.setOnTouchListener(new View.OnTouchListener() {

                private long tInicio = 0;
                private long tFinal = 0;
                private long tDiferencia = 0;

                private boolean cancelado = false;
                private boolean moreOptions = false;
                private boolean animation = false;
				private boolean presionado = true;

				private int xIni;
				private int yIni;

				private Handler mHandler;

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
				@Override
                public boolean onTouch(View v, final MotionEvent event) {
                    int eventAction = event.getAction();
					int xAct;
					int yAct;

                    switch (eventAction) {
                        case MotionEvent.ACTION_DOWN:

                            cancelado = false;
							presionado = false;

							tInicio = System.currentTimeMillis();
							xIni = (int) event.getX();
							yIni = (int) event.getY();
							if (view.getRectMoreOptions().contains(xIni, yIni))
							{
								moreOptions = true;
								animation = false;
							}
							else
							{
								animation = true;
								moreOptions = false;
								view.animateHoldTouched();
								if (mHandler == null)
								{
									presionado = true;
									mHandler = new Handler();
									mHandler.postDelayed(mAction, 505);

								}
							}
                            break;

						case MotionEvent.ACTION_MOVE:

							xAct = (int) event.getX();
							yAct = (int) event.getY();

							if(!cancelado)
							{
								if(Math.abs(xIni - xAct) > 50 || Math.abs(yIni - yAct) > 50)
								{
									cancelado = true;
									moreOptions = false;
									presionado = false;
									if(animation)
										view.animateReleaseTouched();
									animation = false;
									if(mHandler == null) return true;
									mHandler.removeCallbacks(mAction);
									mHandler = null;
								}
							}

							break;

                        case MotionEvent.ACTION_CANCEL:
                            if(animation)
                            {
								view.animateReleaseTouched();
							}
							animation = false;
                            cancelado = true;
                            if(mHandler == null) return true;
							mHandler.removeCallbacks(mAction);
                            mHandler = null;
                            break;

                        case MotionEvent.ACTION_UP:
                            tFinal = System.currentTimeMillis();
                            tDiferencia = tFinal - tInicio;
                            if(animation)
								view.animateReleaseTouched();

                            if(!cancelado)
                            {
                                if(tDiferencia < 500)
                                {
                                    presionado = false;
                                    if (moreOptions) {
                                        Intent intent = new Intent(MainActivity.this, SongOptionsActivity.class);
                                        startActivity(intent);
                                    } else {
                                        songClickListener((SongFileView) v);
                                    }
                                }
                            }
                            if (mHandler == null) return true;
                            mHandler.removeCallbacks(mAction);
                            mHandler = null;
                            break;
                    }
                    return true;
                }

                Runnable mAction = new Runnable() {
                    @Override public void run() {
                        if(!cancelado && presionado)
                        {
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
                    }
                };
            });

            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(_fileViewContainer.getWidth(), MusicPlayerUtil.dpToPx(70, this));
            view.setLayoutParams(layout);
            _fileViewContainer.addView(view);
        }
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
		MP3Metadata metadata = MetaDataWrapperUtil.MP3FromFile(songFileView.getReferenceFile());

		Log.d("MandarArchivo", "Hola!");
		Log.d("MandarArchivo", (_songWrapper.getCurrentSongFile().getName() == null) + "");


		Bundle options = new Bundle();
		options.putParcelable(SongDetailsActivity.BUNDLE_SONG_METADATA, metadata);
		startActivity(_songDetailsIntent, options);
	}


}
