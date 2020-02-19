package com.example.musicplayer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.musicplayer.R;

public class SongFileView extends View {

    private float _fileDisplayTextSize = 0f;
    private String _fileDisplayName;

    private Paint _painter;
    private Rect _drawingRect;

    public SongFileView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SongFileView, 0, 0);

        try {
            _fileDisplayName = typedArray.getString(R.styleable.SongFileView_fileDisplayName);
            _fileDisplayTextSize = typedArray.getFloat(R.styleable.SongFileView_fileDisplayTextSize, 0f);
        } finally {
            typedArray.recycle();
        }
    }

    private void init() {
        // Inicializo las variables
        _painter = new Paint(Paint.ANTI_ALIAS_FLAG);
        _drawingRect = new Rect();
    }

    private void resolveTextSize()
    {
        if(_fileDisplayTextSize == 0){
            _fileDisplayTextSize = _painter.getTextSize();
        }else{
            _painter.setTextSize(_fileDisplayTextSize);
        }
    }

    private void setFileDisplayName(String displayName) {
        _fileDisplayName = displayName;
    }

    private String getFileDisplayName() {
        return _fileDisplayName;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int color = getResources().getColor(R.color.colorAccent);
        _painter.setColor(color);

        float width = getMeasuredWidth();
        float height = getMeasuredHeight();

        getDrawingRect(_drawingRect);
        canvas.drawRect(_drawingRect, _painter);

        resolveTextSize();

        _painter.setColor(Color.WHITE);
        _painter.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(_fileDisplayName, width /2,height/2, _painter);

        Log.d("OnDraw", "called!");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
