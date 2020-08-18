package com.example.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicplayer.activities.SongDetailsActivity;
import com.example.musicplayer.models.MP3Metadata;

public class MusicNotification {

	public static final String CHANNEL_ID = "MUSIC CHANNEL";

	public static final String ACTION_SHUFFLE = "ACTION_SHUFFLE";
	public static final String ACTION_PREV = "ACTION_PREV";
	public static final String ACTION_PLAY = "ACTION_PLAY";
	public static final String ACTION_NEXT = "ACTION_NEXT";
	public static final String ACTION_FAVORITE = "ACTION_FAVORITE";

	public static Notification notification;

	public static void createNotification(Context context, MP3Metadata song, int icPlay, int icShuffle, int icFav) {
		NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
		MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

		Intent intentShuffle = new Intent(context, NotificationActionService.class)
				.setAction(ACTION_SHUFFLE);
		PendingIntent pendingIntentShuffle = PendingIntent.getBroadcast(context, 0,
				intentShuffle, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent intentPrev = new Intent(context, NotificationActionService.class)
				.setAction(ACTION_PREV);
		PendingIntent pendingIntentPrev = PendingIntent.getBroadcast(context, 0,
				intentPrev, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent intentPlay = new Intent(context, NotificationActionService.class)
				.setAction(ACTION_PLAY);
		PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
				intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent intentNext = new Intent(context, NotificationActionService.class)
				.setAction(ACTION_NEXT);
		PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 0,
				intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent intentFav = new Intent(context, NotificationActionService.class)
				.setAction(ACTION_FAVORITE);
		PendingIntent pendingIntentFav = PendingIntent.getBroadcast(context, 0,
				intentFav, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent songDetailsActivity = new Intent(context, SongDetailsActivity.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addNextIntentWithParentStack(songDetailsActivity);
		PendingIntent pendingIntentSongDetail = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_playlist)
				.setContentTitle(song.title)
				.setContentText(String.format("%s | %s", song.artistName, song.albumName))
				.setLargeIcon(song.image)
				.setContentIntent(pendingIntentSongDetail)
				.setOnlyAlertOnce(true)
				.setShowWhen(false)
				.addAction(icShuffle, "Shuffle", pendingIntentShuffle)
				.addAction(R.drawable.ic_previous, "Previous", pendingIntentPrev)
				.addAction(icPlay, "Play", pendingIntentPlay)
				.addAction(R.drawable.ic_next, "Next", pendingIntentNext)
				.addAction(icFav, "Favorite", pendingIntentFav)
				.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
						.setShowActionsInCompactView(0, 1, 2, 3, 4)
						.setMediaSession(mediaSessionCompat.getSessionToken()))
				.setPriority(NotificationCompat.PRIORITY_LOW)
				.build();

		notificationManagerCompat.notify(1, notification);
	}
}
