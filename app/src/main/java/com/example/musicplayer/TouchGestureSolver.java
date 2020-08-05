package com.example.musicplayer;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TouchGestureSolver implements View.OnTouchListener
{
	private long tInicio = 0;
	private long tFinal = 0;
	private long tDiferencia = 0;

	private boolean cancelado = false;
	private boolean presionado = true;

	private int xIni;
	private int yIni;

	private View mView;

	private Handler mHandler;
	private OnGestureListener _onGestureListener;

	public interface OnGestureListener
	{
		void onSingleClick(View view);

		void onLongClick(View view);
	}

	private  Runnable _mAction = new Runnable() {
		@Override public void run() {
			if(!cancelado && presionado)
			{
				Log.d("Listenerprueba", "En el runnable");
				onLongClick(mView);
			}
		}
	};



	@Override
	public boolean onTouch(View v, MotionEvent event) {
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
				if (mHandler == null)
				{
					presionado = true;
					mHandler = new Handler();
					mView = v;
					mHandler.postDelayed(_mAction, 505);

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
						presionado = false;
						if(mHandler == null) return true;
						mHandler.removeCallbacks(_mAction);
						mHandler = null;
					}
				}

				break;

			case MotionEvent.ACTION_CANCEL:
				cancelado = true;
				if(mHandler == null) return true;
				mHandler.removeCallbacks(_mAction);
				mHandler = null;
				break;

			case MotionEvent.ACTION_UP:
				tFinal = System.currentTimeMillis();
				tDiferencia = tFinal - tInicio;

				if(!cancelado)
				{
					if(tDiferencia < 500)
					{
						onSingleClick(v);
					}
				}
				if (mHandler == null) return true;
				mHandler.removeCallbacks(_mAction);
				mHandler = null;
				break;
		}
		return true;
	}

	public void setOnGestureListener(OnGestureListener onGestureListener){
		_onGestureListener = onGestureListener;
	}

	public void onSingleClick(View view)
	{
		if(_onGestureListener != null)
			_onGestureListener.onSingleClick(view);
	}

	public void onLongClick(View view){
		Log.d("Listenerprueba", "en el solver");
		if(_onGestureListener != null)
			_onGestureListener.onLongClick(view);
	}
}
