package com.example.musicplayer.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.example.musicplayer.R;

import java.io.File;

public class SongFileView extends View {

	private Context _context;

	private String _fileDisplayTitle;
	private float _nameTextSize;
	private String _fileDisplayArtistName;
	private float _artistAlbumNameTextSize;
	private String _fileDisplayAlbumName;
	private int _backgroundDisplayColor;
	private int _fontDisplayTitleColor;
	private int _fontDisplayArtistAlbumColor;

	private float _defaultNameTextSize = 42f;
	private float _defaultArtistAlbumTextSize = 35f;
	private float _imageMargin = 25f;

	private File _referenceFile;

	private Paint _painter;
	private Rect _drawingRect;

	private Bitmap _image;

	public SongFileView(Context context) {
		super(context);
		_context = context;
		init(null);
	}

	public SongFileView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		init(attrs);
	}

	private void init(@Nullable AttributeSet attrs) {
		// Inicializo las variables
		_painter = new Paint(Paint.ANTI_ALIAS_FLAG);
		_drawingRect = new Rect();

		TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SongFileView, 0, 0);

		try {
			_fileDisplayTitle = typedArray.getString(R.styleable.SongFileView_fileDisplayName);
			if (_fileDisplayTitle == null) _fileDisplayTitle = "Unknown title";
			_nameTextSize = typedArray.getDimension(R.styleable.SongFileView_nameTextSize, _defaultNameTextSize);

			_fileDisplayArtistName = typedArray.getString(R.styleable.SongFileView_fileDisplayAlbumName);
			if (_fileDisplayArtistName == null) _fileDisplayArtistName = "Unknown artist";
			_artistAlbumNameTextSize = typedArray.getDimension(R.styleable.SongFileView_artistAlbumNameTextSize, _defaultArtistAlbumTextSize);

			_fileDisplayAlbumName = typedArray.getString(R.styleable.SongFileView_fileDisplayAlbumName);
			if (_fileDisplayAlbumName == null) _fileDisplayAlbumName = "Unknown album";

			_backgroundDisplayColor = typedArray.getColor(R.styleable.SongFileView_backgroundDisplayColor, getResources().getColor(R.color.colorLightModeBackground));
			_fontDisplayTitleColor = typedArray.getColor(R.styleable.SongFileView_fontDisplayColor, getResources().getColor(R.color.colorLightModePrimaryText));
			_fontDisplayArtistAlbumColor = typedArray.getColor(R.styleable.SongFileView_fontDisplayColor, getResources().getColor(R.color.colorLightModeSecundaryText));

			_imageMargin = typedArray.getFloat(R.styleable.SongFileView_imageMargin, 25f);

		} finally {
			typedArray.recycle();
		}

		renderImage();
	}

	private Bitmap getResizeBitMap(Bitmap bitmap, int reqWidth, int reqHeight) {
		Matrix matrix = new Matrix();

		RectF src = new RectF(0f, 0f, bitmap.getWidth(), bitmap.getHeight());
		RectF dst = new RectF(0f, 0f, reqWidth, reqHeight);

		matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	public void renderImage()
	{

		if(_image == null)
			_image = BitmapFactory.decodeResource(getResources(), R.drawable.logo1);

		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
				else
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
				int imageSize = getMeasuredHeight() - ((int) (_imageMargin * 2));
				_image = getResizeBitMap(_image, imageSize, imageSize);
			}

		});
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		_painter.setColor(_backgroundDisplayColor);
		int height = _image.getHeight();

		getDrawingRect(_drawingRect);
		canvas.drawRect(_drawingRect, _painter);

		_painter.setColor(_fontDisplayTitleColor);
		_painter.setTextAlign(Paint.Align.LEFT);
		_painter.setTextSize(_nameTextSize);
		canvas.drawBitmap(_image, _imageMargin, _imageMargin, null);

		Log.d("NullReference", _fileDisplayTitle+"");
		canvas.drawText(_fileDisplayTitle, _image.getWidth() + ((int) (_imageMargin * 2)),
				((height / 4f) + _imageMargin) + (height * 0.1f),
				_painter);


		_painter.setColor(_fontDisplayArtistAlbumColor);
		_painter.setTextSize(_artistAlbumNameTextSize);
		canvas.drawText(String.format("%s | %s", _fileDisplayArtistName,_fileDisplayAlbumName),
				_image.getWidth() + ((int) (_imageMargin * 2)),
				(((height / 4f) + (height / 2f)) + _imageMargin) + (height * 0.1f),
				_painter);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	public void setReferenceFile(File file) {
		_referenceFile = file;
	}

	public File getReferenceFile() {
		return _referenceFile;
	}

	public void setFileDisplayName(String displayName) {_fileDisplayTitle = displayName; }

	public void setFileDisplayArtistName(String artistName){ _fileDisplayArtistName = artistName;}

	public void setFileDisplayAlbumName(String albumName){_fileDisplayAlbumName = albumName;}

	public void setImage(Bitmap image) {_image = image; renderImage();}

	public void setArtistAlbumNameTextSize(float artistAlbumNameTextSize) {
		_artistAlbumNameTextSize = artistAlbumNameTextSize;
	}

	public String getFileDisplayTitle() {
		return _fileDisplayTitle;
	}

	public void setNameTextSize(float size) {
		_nameTextSize = size < 0 ? 0 : size;
	}

	public float getNameTextSize() {
		return _nameTextSize;
	}


}
