package com.example.musicplayer.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.musicplayer.R;
import com.example.musicplayer.activities.MainActivity;
import com.example.musicplayer.activities.SongOptionsActivity;
import com.example.musicplayer.util.MusicPlayerUtil;

import java.io.File;

public class SongFileView extends View {

	/**
	 * Contextp de la VIew
	 */
	private Context _context;

	/**
	 * Titulo del mp3
	 */
	private String _fileDisplayTitle;

	/**
	 * Tamaño del titulo
	 */
	private float _nameTextSize;

	/**
	 *  Artista del mp3
	 */
	private String _fileDisplayArtistName;

	/**
	 * Tamaño del texto para el artista y el album
	 */
	private float _artistAlbumNameTextSize;

	/**
	 * Album del mp3
	 */
	private String _fileDisplayAlbumName;

	/**
	 * Color del fondo de la view
	 */
	private int _backgroundDisplayColor;

	/**
	 * Color del texto para el titulo
	 */
	private int _fontDisplayTitleColor;

	/**
	 * Color del texto para el artista y el album
	 */
	private int _fontDisplayArtistAlbumColor;

	/**
	 * Margen de para la imagen con respecto a la altura de la view
	 */
	private float _imageMargin;

	/**
	 * tamaño por defecto del texto para el nombre
	 */
	public static float DEFAULT_NAME_TEXT_SIZE;

	/**
	 * tamaño por defecto del texto para el artista y el album
	 */
	public static float DEFAULT_ARTIST_ALBUM_TEXT_SIZE;

	/**
	 * Relacion de la margen con respecto a la altura de la view
	 */
	public static final float DEFAULT_IMAGE_MARGIN_RELATION = 0.1875f;

	/**
	 * Relacion del espacio para el texto con respecto al ancho de la view
	 */
	public static final float MEASURED_TEXT_RELATION = 0.65f;

	/**
	 * Maximo de pixeles disponibles para dibujor el texto en el canvas
	 */
	private float _maxMeasuredText;

	/**
	 * Archivo de referecia para la view
	 */
	private File _referenceFile;

	private Paint _painter;
	private Rect _drawingRect;

	/**
	 * Imagen de la portada del album
	 */
	private Bitmap _image;

	/**
	 * Imagen para el boton de mas opciones
	 */
	private Bitmap _moreOptionsImage;

	/**
	 * Tamaño de la imagen
	 */
	private int _imageSize;

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




		DEFAULT_NAME_TEXT_SIZE = MusicPlayerUtil.spToPx(17, _context);
		DEFAULT_ARTIST_ALBUM_TEXT_SIZE = MusicPlayerUtil.spToPx(15, _context);

		// Obtencion de datos
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SongFileView, 0, 0);

		try {
			_fileDisplayTitle = typedArray.getString(R.styleable.SongFileView_fileDisplayName);
			if (_fileDisplayTitle == null) _fileDisplayTitle = "Unknown title";
			_nameTextSize = typedArray.getDimension(R.styleable.SongFileView_nameTextSize, DEFAULT_NAME_TEXT_SIZE);

			_fileDisplayArtistName = typedArray.getString(R.styleable.SongFileView_fileDisplayAlbumName);
			if (_fileDisplayArtistName == null) _fileDisplayArtistName = "Unknown artist";
			_artistAlbumNameTextSize = typedArray.getDimension(R.styleable.SongFileView_artistAlbumNameTextSize, DEFAULT_ARTIST_ALBUM_TEXT_SIZE);

			_fileDisplayAlbumName = typedArray.getString(R.styleable.SongFileView_fileDisplayAlbumName);
			if (_fileDisplayAlbumName == null) _fileDisplayAlbumName = "Unknown album";

			_backgroundDisplayColor = typedArray.getColor(R.styleable.SongFileView_backgroundDisplayColor, getResources().getColor(R.color.colorLightModeBackground));
			_fontDisplayTitleColor = typedArray.getColor(R.styleable.SongFileView_fontDisplayColor, getResources().getColor(R.color.colorLightModePrimaryText));
			_fontDisplayArtistAlbumColor = typedArray.getColor(R.styleable.SongFileView_fontDisplayColor, getResources().getColor(R.color.colorLightModeSecundaryText));

			_imageMargin = typedArray.getFloat(R.styleable.SongFileView_imageMargin, 35f);

		} finally {
			typedArray.recycle();
		}


		renderImage();


	}

	/**
	 * Metodo que reescala la imagen a un ancho y un alto deseado
	 * @param bitmap imagen a reescalar
	 * @param reqWidth ancho deseado
	 * @param reqHeight alto deseado
	 * @return
	 */
	private Bitmap getResizeBitMap(Bitmap bitmap, int reqWidth, int reqHeight) {
		Matrix matrix = new Matrix();

		RectF src = new RectF(0f, 0f, bitmap.getWidth(), bitmap.getHeight());
		RectF dst = new RectF(0f, 0f, reqWidth, reqHeight);

		matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	/**
	 * Metodo que rendeiza las imagenes de la view
	 */
	public void renderImage()
	{

		if(_image == null)
			_image = BitmapFactory.decodeResource(getResources(), R.drawable.logo1);

		_moreOptionsImage = BitmapFactory.decodeResource(getResources(), R.drawable.three_dot_xxhdpi);

		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
				else
					getViewTreeObserver().removeGlobalOnLayoutListener(this);

				_maxMeasuredText = getWidth() * MEASURED_TEXT_RELATION;

				_imageMargin = ((float)getHeight()) * DEFAULT_IMAGE_MARGIN_RELATION;
				_imageSize = getHeight() - ((int) (_imageMargin * 2));
				_image = getResizeBitMap(_image, _imageSize, _imageSize);
				int moreOptHeight = (int)(getHeight() - (getHeight() * 0.3f));

				int moreOptWidth = (int) (moreOptHeight * _moreOptionsImage.getWidth() / _moreOptionsImage.getHeight());
				_moreOptionsImage = getResizeBitMap(_moreOptionsImage, moreOptHeight, moreOptWidth);

			}

		});


	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		_painter.setColor(_backgroundDisplayColor);





		getDrawingRect(_drawingRect);
		canvas.drawRect(_drawingRect, _painter);

		// Se dibuja la imagen de la caratula
		canvas.drawBitmap(_image, _imageMargin, _imageMargin, null);

		//Se dibuja la imagen de el boton para mas opciones
		canvas.drawBitmap(_moreOptionsImage,
				getWidth() - (_imageMargin * 2) - _moreOptionsImage.getWidth(),
				(getHeight() / 2) - (_moreOptionsImage.getHeight() / 2), _painter);

		_painter.setColor(_fontDisplayTitleColor);
		_painter.setTextSize(_nameTextSize);
		_painter.setTextAlign(Paint.Align.LEFT);

		drawText(_fileDisplayTitle,
				_imageSize + ((int) (_imageMargin * 2)),
				((_imageSize / 4f) + _imageMargin) + (_imageSize * 0.1f),
				canvas);

		_painter.setColor(_fontDisplayArtistAlbumColor);
		_painter.setStrokeWidth(0.7f);
		canvas.drawLine(getHeight(), 0f, getWidth() - _imageMargin, 0f, _painter);
		canvas.drawLine(getHeight(), getHeight(), getWidth() - _imageMargin, getHeight(), _painter);

		_painter.setTextSize(_artistAlbumNameTextSize);

		drawText(String.format("%s | %s", _fileDisplayArtistName,_fileDisplayAlbumName),
				_imageSize + ((int) (_imageMargin * 2)),
				(((_imageSize / 4f) + (_imageSize / 2f)) + _imageMargin) + (_imageSize * 0.1f),
				canvas);
	}

	/**
	 * Metodo que dibuja los texto en el canvas con respecto al espacio definido para el texto
	 * @param text texto a dibujar
	 * @param x coordenada x
	 * @param y coordenada y
	 * @param canvas
	 */
	private void drawText(String text, float x, float y, Canvas canvas)
	{
		// Se Obtiene el ancho en pixeles que ocupa la imagen
		// con respecto al tamaño del texto definido en el painter
		float measuredText = _painter.measureText(text);


		if(measuredText > _maxMeasuredText)
		{
			int chars = _painter.breakText(text, true, _maxMeasuredText, null);
			canvas.drawText(String.format("%s...", text.substring(0, chars)), x, y, _painter);
		}
		else
		{
			canvas.drawText(text, x, y, _painter);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	/*
	 * Getters and Setters
	 */

	public void setReferenceFile(File file) {
		_referenceFile = file;
	}

	public void animateTouched()
	{
		ValueAnimator animator = ValueAnimator.ofInt(0, 50, 0);
		animator.setDuration(300);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (int) animation.getAnimatedValue();
				_backgroundDisplayColor = Color.rgb(255 - value, 255 - value, 255 - value);
				invalidate();
			}
		});

		animator.start();
	}

	public void animateHoldTouched()
	{
		ValueAnimator animator = ValueAnimator.ofInt(0, 50);
		animator.setDuration(250);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (int) animation.getAnimatedValue();
				_backgroundDisplayColor = Color.rgb(255 - value, 255 - value, 255 - value);
				invalidate();
			}
		});

		animator.start();
	}

	public void animateReleaseTouched()
	{
		ValueAnimator animator = ValueAnimator.ofInt(50, 0);
		animator.setDuration(250);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (int) animation.getAnimatedValue();
				_backgroundDisplayColor = Color.rgb(255 - value, 255 - value, 255 - value);
				invalidate();
			}
		});

		animator.start();
	}

	public Rect getRectMoreOptions()
	{
		Rect r = new Rect();
		r.left = (int) (getWidth() - (getImageMargin() * 2) - getMoreOptionsImage().getHeight());
		r.top = 0;
		r.right = (int) (getWidth() - (getImageMargin() * 2) + getMoreOptionsImage().getHeight());
		r.bottom = getHeight();
		return r;
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

	public float getImageMargin()
	{
		return _imageMargin;
	}

	public Bitmap getMoreOptionsImage() {
		return _moreOptionsImage;
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
