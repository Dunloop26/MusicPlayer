package com.example.musicplayer.util;

import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;

import java.util.concurrent.TimeUnit;

public class MusicPlayerUtil
{
	public static int dpToPx(float dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}

	public static int spToPx(float sp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
	}

	public static double getMinutesFromMilliseconds(int milliseconds)
	{
		return (double) TimeUnit.MILLISECONDS.toMinutes(milliseconds);
	}
	public static int getSecondsFromMilliseconds(int milliseconds)
	{
		double minutes = getMinutesFromMilliseconds(milliseconds);
		return (int) TimeUnit.MILLISECONDS.toSeconds(milliseconds) - (int)(minutes * 60);
	}

	public static String getFormatTimeFromMilliseconds(int milliseconds)
	{
		double minutes = getMinutesFromMilliseconds(milliseconds);
		int seconds = getSecondsFromMilliseconds(milliseconds);
		return String.format("%d:%02d",(int) Math.floor(minutes), seconds);
	}
}
