package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.musicplayer.activities.SongOptionsActivity;
import com.example.musicplayer.models.MP3Metadata;
import com.example.musicplayer.util.MetaDataWrapperUtil;
import com.example.musicplayer.util.MusicPlayerUtil;
import com.example.musicplayer.views.SongFileView;

import java.io.File;

class SongFileViewManager implements TouchGestureSolver.OnGestureListener {

	private File mFile;
	private SongFileView mSongFileView;
	private Context mContext;
	private int mWidth;
	private TouchGestureSolver.OnGestureListener mOnGestureListener;

	public SongFileViewManager(Context context, File file, int width){
		mWidth = width;
		mContext = context;
		mFile = file;
	}

	@SuppressLint("ClickableViewAccessibility")
	public void createView() {
		mSongFileView = new SongFileView(mContext);
		setupViewData(mContext, mFile, mSongFileView);
		mSongFileView.setOnTouchListener(new TouchGestureSolver() {
			private boolean moreOptions = false;
			private boolean animation = false;

			@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				SongFileView sv = (SongFileView) v;
				int eventAction = event.getAction();

				switch (eventAction) {
					case MotionEvent.ACTION_DOWN:
						int x = (int) event.getX();
						int y = (int) event.getY();
						if (sv.getRectMoreOptions().contains(x, y))
						{
							moreOptions = true;
							animation = false;
						}
						else
						{
							animation = true;
							moreOptions = false;
							sv.animateHoldTouched();
						}
						break;

					case MotionEvent.ACTION_MOVE:

						break;

					case MotionEvent.ACTION_CANCEL:
						if(animation)
						{
							sv.animateReleaseTouched();
						}
						animation = false;
						break;

					case MotionEvent.ACTION_UP:
						if(animation)
							sv.animateReleaseTouched();
						break;
				}

				return super.onTouch(v, event);
			}

			@Override
			public void onLongClick(View v) {
				SongFileView sv = (SongFileView) v;
				Log.d("Listenerprueba", "En el main");
				Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
				// Vibrate for 30 milliseconds
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.EFFECT_TICK));
				} else {
					//deprecated in API 26 or higher
					vibrator.vibrate(30);
				}
				Toast.makeText(mContext, "This is my Toast message!",
						Toast.LENGTH_LONG).show();
				if(animation)
					sv.animateReleaseTouched();
				animation = false;
			}

			@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onSingleClick(View v) {
				onSingleClick(v);
				if (moreOptions) {
					Intent intent = new Intent(mContext, SongOptionsActivity.class);
					mContext.startActivity(intent);
				} else {
					onSingleClick((SongFileView) v);
				}
			}

		});

		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(mWidth, MusicPlayerUtil.dpToPx(70, mContext));
		mSongFileView.setLayoutParams(layout);
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


	public void onSingleClick(View view)
	{
		if(mOnGestureListener != null)
			mOnGestureListener.onSingleClick(view);
	}

	public void onLongClick(View view){
		Log.d("Listenerprueba", "en el solver");
		if(mOnGestureListener != null)
			mOnGestureListener.onLongClick(view);
	}

	public SongFileView getSongFileView() {
		return mSongFileView;
	}
}
