package com.example.musicplayer.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.musicplayer.R;

import java.io.File;

public class SongFileView extends View {

    private Context _context;

    private float _fileDisplayTextSize = 0f;
    private String _fileDisplayName;
    private int _backgroundDisplayColor;
    private int _fontDisplayColor;

    private File _referenceFile;

    private Paint _painter;
    private Rect _drawingRect;

    public SongFileView(Context context)
    {
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
            _fileDisplayName = typedArray.getString(R.styleable.SongFileView_fileDisplayName);
            if(_fileDisplayName == null) _fileDisplayName = "FileName";
            _fileDisplayTextSize = typedArray.getFloat(R.styleable.SongFileView_fileDisplayTextSize, 0f);
            _backgroundDisplayColor = typedArray.getColor(R.styleable.SongFileView_backgroundDisplayColor, Color.WHITE);
            _fontDisplayColor = typedArray.getColor(R.styleable.SongFileView_fontDisplayColor, Color.BLACK);

        } finally {
            typedArray.recycle();
        }

        setMinimumWidth(100);
        setMinimumHeight(200);
    }

    private void resolveTextSize() {
        if (_fileDisplayTextSize == 0) {
            _fileDisplayTextSize = _painter.getTextSize();
        } else {
            _painter.setTextSize(_fileDisplayTextSize);
        }
    }

    public void setReferenceFile(File file) {_referenceFile = file;}
    public File getReferenceFile() {return _referenceFile;}

    public void setFileDisplayName(String displayName) {
        _fileDisplayName = displayName;
    }

    public String getFileDisplayName() {
        return _fileDisplayName;
    }

    public void setFileDisplayTextSize(float size) {
        _fileDisplayTextSize = size < 0 ? 0 : size;
    }

    public float getFileDisplayTextSize() {
        return _fileDisplayTextSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        _painter.setColor(_backgroundDisplayColor);

        float width = getMeasuredWidth();
        float height = getMeasuredHeight();

        getDrawingRect(_drawingRect);
        canvas.drawRect(_drawingRect, _painter);

        resolveTextSize();

        _painter.setColor(_fontDisplayColor);
        _painter.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(_fileDisplayName, width / 2, height / 2, _painter);
    }
}
