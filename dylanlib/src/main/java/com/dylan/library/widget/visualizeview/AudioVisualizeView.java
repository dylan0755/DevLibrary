package com.dylan.library.widget.visualizeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dylan.library.R;
import com.dylan.library.utils.DataTypeConversionUtils;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.Logger;

/**
 * Author: Dylan
 * Date: 2023/12/3
 * Desc:
 */

public  class AudioVisualizeView extends View implements VisualizeCallback {
    private int orientation;
    private float heightRate;
    protected int mSpectrumCount;
    protected int mWaveThreshold;
    protected float mItemMargin;
    protected float mSpectrumRatio;
    protected float mStrokeWidth;
    protected int mColor;
    protected boolean isVisualizationEnabled;
    protected float[] mRawAudioBytes;
    protected Paint mPaint;
    protected Path mPath;
    protected float centerX;
    protected float centerY;
    protected VisualizerHelper visualizerHelper;

    public AudioVisualizeView(Context context) {
        this(context, (AttributeSet)null);
    }

    public AudioVisualizeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioVisualizeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isVisualizationEnabled = true;
        this.handleStyleable(context, attrs, defStyleAttr);
        this.init();
    }

    private void handleStyleable(Context context, AttributeSet attrs, int defStyle) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AudioVisualizeView, defStyle, 0);

        try {
            this.mColor = ta.getColor(R.styleable.AudioVisualizeView_visualize_color, -1);
            this.mSpectrumCount = ta.getInteger(R.styleable.AudioVisualizeView_visualize_count, 60) + 1;
            this.mWaveThreshold = ta.getInteger(R.styleable.AudioVisualizeView_visualize_threshold, 0);
            this.mSpectrumRatio = ta.getFloat(R.styleable.AudioVisualizeView_visualize_ratio, 1.0F);
            this.mItemMargin = ta.getDimension(R.styleable.AudioVisualizeView_visualize_item_margin, 12.0F);
            this.orientation = ta.getInteger(R.styleable.AudioVisualizeView_visualize_orientation, 2);
            this.heightRate = ta.getFloat(R.styleable.AudioVisualizeView_visualize_height_rate, 1.0F);
            int strokeSize=ta.getInteger(R.styleable.AudioVisualizeView_visualize_stroke_size, 2);
            mStrokeWidth=DensityUtils.dp2px(context,strokeSize);
        } catch (Exception var9) {
            var9.printStackTrace();
        } finally {
            ta.recycle();
        }

    }

    protected void init() {
        this.mPaint = new Paint();
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
        this.mPaint.setColor(this.mColor);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);//圆角
        this.mPaint.setAntiAlias(true);
        this.mPath = new Path();
        this.visualizerHelper = new VisualizerHelper();
        this.visualizerHelper.setVisualizeCallback(this);
        this.visualizerHelper.setVisualCount(this.mSpectrumCount);
    }

    public void bindAudioSessionId(int audioSessionId) {
        try {
            this.visualizerHelper.setAudioSessionId(audioSessionId);
        } catch (Exception var3) {
            Logger.e(var3.getMessage());
        }

    }

    public void receiveAudioData(byte[] fft) {

        float[] model = new float[fft.length / 2 + 1];
        model[0] = (byte) Math.abs(fft[1]);
        int j = 1;

        for (int i = 2; i < mSpectrumCount * 2; ) {
            model[j] = (float) Math.hypot(fft[i], fft[i + 1]);
            i += 2;
            j++;
            model[j] = (float) Math.abs(fft[j]);
        }

        onFftDataCapture(model);

    }

    public void onFftDataCapture(float[] parseData) {
        if (this.isVisualizationEnabled) {
            this.mRawAudioBytes = parseData;
            this.postInvalidate();
        }
    }



    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.centerX = getMeasuredWidth() / 2.0F;
        this.centerY = getMeasuredHeight() / 2.0F;
    }

    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (this.mRawAudioBytes != null) {
            for (int i = 1; i < this.mSpectrumCount; ++i) {
                int wave = (int) this.mRawAudioBytes[i];
                Log.e("onDraw: ",""+wave );
                wave = wave > this.mWaveThreshold ? wave : 0;
                wave = (int) ((float) wave *heightRate);

                if (orientation==2){
                    int startY = (int) (getHeight() * 0.5);
                    canvas.drawLine(getWidth() * (float)i / (float)this.mSpectrumCount, startY + (float)wave, getWidth() * (float)i / (float)this.mSpectrumCount, startY - (float)wave, this.mPaint);
                }else {
                    int startY = (int) (getHeight() * 0.8);
                    int endY = startY - wave;
                    if (endY < getHeight() * 0.25) endY = (int) (getHeight() * 0.25);
                    canvas.drawLine(getWidth()* (float) i / (float) this.mSpectrumCount, startY, getWidth() * (float) i / (float) this.mSpectrumCount, endY, this.mPaint);
                }
            }
        }
    }



    public void release() {
        if (this.visualizerHelper != null) {
            this.visualizerHelper.release();
        }

    }
}