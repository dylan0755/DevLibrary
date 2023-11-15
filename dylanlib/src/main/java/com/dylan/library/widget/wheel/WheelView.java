//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.dylan.library.widget.wheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


import com.dylan.library.R;
import com.dylan.library.utils.DensityUtils;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WheelView extends View {
    private DividerType dividerType;
    Context context;
    Handler handler;
    private GestureDetector gestureDetector;
    OnItemSelectedListener onItemSelectedListener;
    private boolean isOptions;
    private boolean isCenterLabel;
    ScheduledExecutorService mExecutor;
    private ScheduledFuture<?> mFuture;
    Paint paintOuterText;
    Paint paintCenterText;
    Paint paintIndicator;
    WheelAdapter adapter;
    private String label;
    int textSize;
    int maxTextWidth;
    int maxTextHeight;
    float itemHeight;
    Typeface typeface;
    int textColorOut;
    int textColorCenter;
    int dividerColor;
    float lineSpacingMultiplier;
    boolean isLoop;
    float firstLineY;
    float secondLineY;
    float centerY;
    float totalScrollY;
    int initPosition;
    private int selectedItem;
    int preCurrentIndex;
    int change;
    int itemsVisible;
    int measuredHeight;
    int measuredWidth;
    int halfCircumference;
    int radius;
    private int mOffset;
    private float previousY;
    long startTime;
    private static final int VELOCITYFLING = 5;
    int widthMeasureSpec;
    private int mGravity;
    private int drawCenterContentStart;
    private int drawOutContentStart;
    private static final float SCALECONTENT = 0.8F;
    private float CENTERCONTENTOFFSET;
    private float roundRadius;

    public WheelView(Context context) {
        this(context, (AttributeSet)null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        roundRadius=dp2px(context,8);
        this.isOptions = false;
        this.isCenterLabel = true;
        this.mExecutor = Executors.newSingleThreadScheduledExecutor();
        this.typeface = Typeface.MONOSPACE;
        this.textColorOut = -5723992;
        this.textColorCenter = -14013910;
        this.dividerColor = -2763307;
        this.lineSpacingMultiplier = 1.6F;
        this.itemsVisible = 11;
        this.mOffset = 0;
        this.previousY = 0.0F;
        this.startTime = 0L;
        this.mGravity = 17;
        this.drawCenterContentStart = 0;
        this.drawOutContentStart = 0;
        this.textSize = dp2px(context,20);
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        float density = dm.density;
        if (density < 1.0F) {
            this.CENTERCONTENTOFFSET = 2.4F;
        } else if (1.0F <= density && density < 2.0F) {
            this.CENTERCONTENTOFFSET = 3.6F;
        } else if (1.0F <= density && density < 2.0F) {
            this.CENTERCONTENTOFFSET = 4.5F;
        } else if (2.0F <= density && density < 3.0F) {
            this.CENTERCONTENTOFFSET = 6.0F;
        } else if (density >= 3.0F) {
            this.CENTERCONTENTOFFSET = density * 2.5F;
        }

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.wheelPick, 0, 0);
            this.mGravity = a.getInt(R.styleable.wheelPick_wheelPick_gravity, 17);
            this.textColorOut = a.getColor(R.styleable.wheelPick_wheelPick_textColorOut, this.textColorOut);
            this.textColorCenter = a.getColor(R.styleable.wheelPick_wheelPick_textColorCenter, this.textColorCenter);
            this.dividerColor = a.getColor(R.styleable.wheelPick_wheelPick_dividerColor, this.dividerColor);
            this.textSize = a.getDimensionPixelOffset(R.styleable.wheelPick_wheelPick_textSize, this.textSize);
            this.lineSpacingMultiplier = a.getFloat(R.styleable.wheelPick_wheelPick_lineSpacingMultiplier, this.lineSpacingMultiplier);
            a.recycle();
        }

        this.judgeLineSpae();
        this.initLoopView(context);
    }

    private static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setDividerRoundRadius(int roundRadius) {
        this.roundRadius =dp2px(context,roundRadius);
    }

    private void judgeLineSpae() {
        if (this.lineSpacingMultiplier < 1.2F) {
            this.lineSpacingMultiplier = 1.2F;
        } else if (this.lineSpacingMultiplier > 2.0F) {
            this.lineSpacingMultiplier = 2.0F;
        }

    }

    private void initLoopView(Context context) {
        this.context = context;
        this.handler = new MessageHandler(this);
        this.gestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        this.gestureDetector.setIsLongpressEnabled(false);
        this.isLoop = true;
        this.totalScrollY = 0.0F;
        this.initPosition = -1;
        this.initPaints();
    }

    private void initPaints() {
        this.paintOuterText = new Paint();
        this.paintOuterText.setColor(this.textColorOut);
        this.paintOuterText.setAntiAlias(true);
        this.paintOuterText.setTypeface(this.typeface);
        this.paintOuterText.setTextSize((float)this.textSize);
        this.paintCenterText = new Paint();
        this.paintCenterText.setColor(this.textColorCenter);
        this.paintCenterText.setAntiAlias(true);
        this.paintCenterText.setTextScaleX(1.1F);
        this.paintCenterText.setTypeface(this.typeface);
        this.paintCenterText.setTextSize((float)this.textSize);
        this.paintIndicator = new Paint();
        this.paintIndicator.setColor(this.dividerColor);
        this.paintIndicator.setAntiAlias(true);
        if (VERSION.SDK_INT >= 11) {
            this.setLayerType(1, (Paint)null);
        }

    }

    private void remeasure() {
        if (this.adapter != null) {
            this.measureTextWidthHeight();
            this.halfCircumference = (int)(this.itemHeight * (float)(this.itemsVisible - 1));
            this.measuredHeight = (int)((double)(this.halfCircumference * 2) / 3.141592653589793D);
            this.radius = (int)((double)this.halfCircumference / 3.141592653589793D);
            this.measuredWidth = MeasureSpec.getSize(this.widthMeasureSpec);
            this.firstLineY = ((float)this.measuredHeight - this.itemHeight) / 2.0F;
            this.secondLineY = ((float)this.measuredHeight + this.itemHeight) / 2.0F;
            this.centerY = this.secondLineY - (this.itemHeight - (float)this.maxTextHeight) / 2.0F - this.CENTERCONTENTOFFSET;
            if (this.initPosition == -1) {
                if (this.isLoop) {
                    this.initPosition = (this.adapter.getItemsCount() + 1) / 2;
                } else {
                    this.initPosition = 0;
                }
            }

            this.preCurrentIndex = this.initPosition;
        }
    }

    private void measureTextWidthHeight() {
        Rect rect = new Rect();

        for(int i = 0; i < this.adapter.getItemsCount(); ++i) {
            String s1 = this.getContentText(this.adapter.getItem(i));
            this.paintCenterText.getTextBounds(s1, 0, s1.length(), rect);
            int textWidth = rect.width();
            if (textWidth > this.maxTextWidth) {
                this.maxTextWidth = textWidth;
            }

            this.paintCenterText.getTextBounds("星期", 0, 2, rect);
//            ScaleUtil scaleUtil = new ScaleUtil(this.getContext());
//            this.maxTextHeight = rect.height() + scaleUtil.toScaleSize(16);
            this.maxTextHeight = rect.height();
        }

        this.itemHeight = this.lineSpacingMultiplier * (float)this.maxTextHeight;
    }

    void smoothScroll(ACTION action) {
        this.cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            this.mOffset = (int)((this.totalScrollY % this.itemHeight + this.itemHeight) % this.itemHeight);
            if ((float)this.mOffset > this.itemHeight / 2.0F) {
                this.mOffset = (int)(this.itemHeight - (float)this.mOffset);
            } else {
                this.mOffset = -this.mOffset;
            }
        }

        this.mFuture = this.mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, this.mOffset), 0L, 10L, TimeUnit.MILLISECONDS);
    }

    protected final void scrollBy(float velocityY) {
        this.cancelFuture();
        this.mFuture = this.mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0L, 5L, TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (this.mFuture != null && !this.mFuture.isCancelled()) {
            this.mFuture.cancel(true);
            this.mFuture = null;
        }

    }

    public final void setCyclic(boolean cyclic) {
        this.isLoop = cyclic;
    }

    public final void setTypeface(Typeface font) {
        this.typeface = font;
        this.paintOuterText.setTypeface(this.typeface);
        this.paintCenterText.setTypeface(this.typeface);
    }

    public final void setTextSize(float size) {
        if (size > 0.0F) {
            this.textSize = (int)(this.context.getResources().getDisplayMetrics().density * size);
            this.paintOuterText.setTextSize((float)this.textSize);
            this.paintCenterText.setTextSize((float)this.textSize);
        }

    }

    public final void setCurrentItem(int currentItem) {
        this.selectedItem = currentItem;
        this.initPosition = currentItem;
        this.totalScrollY = 0.0F;
        this.invalidate();
    }

    public void setVisibleItems(int visibleItems) {
        this.itemsVisible = visibleItems;
    }

    public final void setOnItemSelectedListener(OnItemSelectedListener OnItemSelectedListener) {
        this.onItemSelectedListener = OnItemSelectedListener;
    }

    public final void setAdapter(WheelAdapter adapter) {
        this.adapter = adapter;
        this.remeasure();
        this.invalidate();
    }

    public final WheelAdapter getAdapter() {
        return this.adapter;
    }

    public final int getCurrentItem() {
        return this.selectedItem;
    }

    protected final void onItemSelected() {
        if (this.onItemSelectedListener != null) {
            this.postDelayed(new OnItemSelectedRunnable(this), 200L);
        }

    }

    protected void onDraw(Canvas canvas) {
        if (this.adapter != null) {
            if (this.initPosition < 0) {
                this.initPosition = 0;
            }

            if (this.initPosition >= this.adapter.getItemsCount()) {
                this.initPosition = this.adapter.getItemsCount() - 1;
            }

            Object[] visibles = new Object[this.itemsVisible];
            this.change = (int)(this.totalScrollY / this.itemHeight);

            try {
                this.preCurrentIndex = this.initPosition + this.change % this.adapter.getItemsCount();
            } catch (ArithmeticException var12) {
                Log.e("WheelView", "出错了！adapter.getItemsCount() == 0，联动数据不匹配");
            }

            if (!this.isLoop) {
                if (this.preCurrentIndex < 0) {
                    this.preCurrentIndex = 0;
                }

                if (this.preCurrentIndex > this.adapter.getItemsCount() - 1) {
                    this.preCurrentIndex = this.adapter.getItemsCount() - 1;
                }
            } else {
                if (this.preCurrentIndex < 0) {
                    this.preCurrentIndex += this.adapter.getItemsCount();
                }

                if (this.preCurrentIndex > this.adapter.getItemsCount() - 1) {
                    this.preCurrentIndex -= this.adapter.getItemsCount();
                }
            }

            float itemHeightOffset = this.totalScrollY % this.itemHeight;

            int counter;
            int index;
            for(counter = 0; counter < this.itemsVisible; ++counter) {
                index = this.preCurrentIndex - (this.itemsVisible / 2 - counter);
                if (this.isLoop) {
                    index = this.getLoopMappingIndex(index);
                    visibles[counter] = this.adapter.getItem(index);
                } else if (index < 0) {
                    visibles[counter] = "";
                } else if (index > this.adapter.getItemsCount() - 1) {
                    visibles[counter] = "";
                } else {
                    visibles[counter] = this.adapter.getItem(index);
                }
            }

            if (this.dividerType == DividerType.WRAP) {
                float startX;
                if (TextUtils.isEmpty(this.label)) {
                    startX = (float)((this.measuredWidth - this.maxTextWidth) / 2 - 12);
                } else {
                    startX = (float)((this.measuredWidth - this.maxTextWidth) / 4 - 12);
                }

                if (startX <= 0.0F) {
                    startX = 10.0F;
                }

                float endX = (float)this.measuredWidth - startX;
                canvas.drawLine(startX, this.firstLineY, endX, this.firstLineY, this.paintIndicator);
                canvas.drawLine(startX, this.secondLineY, endX, this.secondLineY, this.paintIndicator);
            } else if (this.dividerType == DividerType.ROUND_RECT){
                float padding=dp2px(context,7);
                float startX;
                if (TextUtils.isEmpty(this.label)) {
                    startX = (float)((this.measuredWidth - this.maxTextWidth) / 2 - padding);
                } else {
                    startX = (float)((this.measuredWidth - this.maxTextWidth) / 4 - padding);
                }

                if (startX <= 0.0F) {
                    startX = 10.0F;
                }

                float endX = (float)this.measuredWidth - padding;
                RectF rectF=new RectF(startX,firstLineY,endX,secondLineY);
                canvas.drawRoundRect(rectF,roundRadius,roundRadius, paintIndicator);
            }
            else {
                canvas.drawLine(0.0F, this.firstLineY, (float)this.measuredWidth, this.firstLineY, this.paintIndicator);
                canvas.drawLine(0.0F, this.secondLineY, (float)this.measuredWidth, this.secondLineY, this.paintIndicator);
            }

            if (!TextUtils.isEmpty(this.label) && this.isCenterLabel) {
                index = this.measuredWidth - this.getTextWidth(this.paintCenterText, this.label);
                canvas.drawText(this.label, (float)index - this.CENTERCONTENTOFFSET, this.centerY, this.paintCenterText);
            }

            for(counter = 0; counter < this.itemsVisible; ++counter) {
                canvas.save();
                double radian = (double)((this.itemHeight * (float)counter - itemHeightOffset) / (float)this.radius);
                float angle = (float)(90.0D - radian / 3.141592653589793D * 180.0D);
                if (!(angle >= 90.0F) && !(angle <= -90.0F)) {
                    String contentText;
                    if (!this.isCenterLabel && !TextUtils.isEmpty(this.label) && !TextUtils.isEmpty(this.getContentText(visibles[counter]))) {
                        contentText = this.getContentText(visibles[counter]) + this.label;
                    } else {
                        contentText = this.getContentText(visibles[counter]);
                    }

                    this.reMeasureTextSize(contentText);
                    this.measuredCenterContentStart(contentText);
                    this.measuredOutContentStart(contentText);
                    float translateY = (float)((double)this.radius - Math.cos(radian) * (double)this.radius - Math.sin(radian) * (double)this.maxTextHeight / 2.0D);
                    canvas.translate(0.0F, translateY);
                    canvas.scale(1.0F, (float)Math.sin(radian));
                    if (translateY <= this.firstLineY && (float)this.maxTextHeight + translateY >= this.firstLineY) {
                        canvas.save();
                        canvas.clipRect(0.0F, 0.0F, (float)this.measuredWidth, this.firstLineY - translateY);
                        canvas.scale(1.0F, (float)Math.sin(radian) * 0.8F);
                        canvas.drawText(contentText, (float)this.drawOutContentStart, (float)this.maxTextHeight, this.paintOuterText);
                        canvas.restore();
                        canvas.save();
                        canvas.clipRect(0.0F, this.firstLineY - translateY, (float)this.measuredWidth, (float)((int)this.itemHeight));
                        canvas.scale(1.0F, (float)Math.sin(radian) * 1.0F);
                        canvas.drawText(contentText, (float)this.drawCenterContentStart, (float)this.maxTextHeight - this.CENTERCONTENTOFFSET, this.paintCenterText);
                        canvas.restore();
                    } else if (translateY <= this.secondLineY && (float)this.maxTextHeight + translateY >= this.secondLineY) {
                        canvas.save();
                        canvas.clipRect(0.0F, 0.0F, (float)this.measuredWidth, this.secondLineY - translateY);
                        canvas.scale(1.0F, (float)Math.sin(radian) * 1.0F);
                        canvas.drawText(contentText, (float)this.drawCenterContentStart, (float)this.maxTextHeight - this.CENTERCONTENTOFFSET, this.paintCenterText);
                        canvas.restore();
                        canvas.save();
                        canvas.clipRect(0.0F, this.secondLineY - translateY, (float)this.measuredWidth, (float)((int)this.itemHeight));
                        canvas.scale(1.0F, (float)Math.sin(radian) * 0.8F);
                        canvas.drawText(contentText, (float)this.drawOutContentStart, (float)this.maxTextHeight, this.paintOuterText);
                        canvas.restore();
                    } else if (translateY >= this.firstLineY && (float)this.maxTextHeight + translateY <= this.secondLineY) {
                        float Y = (float)this.maxTextHeight - this.CENTERCONTENTOFFSET;
                        canvas.drawText(contentText, (float)this.drawCenterContentStart, Y, this.paintCenterText);
                        int preSelectedItem = this.adapter.indexOf(visibles[counter]);
                        this.selectedItem = preSelectedItem;
                    } else {
                        canvas.save();
                        canvas.clipRect(0, 0, this.measuredWidth, (int)this.itemHeight);
                        canvas.scale(1.0F, (float)Math.sin(radian) * 0.8F);
                        canvas.drawText(contentText, (float)this.drawOutContentStart, (float)this.maxTextHeight, this.paintOuterText);
                        canvas.restore();
                    }

                    canvas.restore();
                    this.paintCenterText.setTextSize((float)this.textSize);
                } else {
                    canvas.restore();
                }
            }

        }
    }

    private void reMeasureTextSize(String contentText) {
        Rect rect = new Rect();
        this.paintCenterText.getTextBounds(contentText, 0, contentText.length(), rect);
        int width = rect.width();

        int size;
        for(size = this.textSize; width > this.measuredWidth; width = rect.width()) {
            --size;
            this.paintCenterText.setTextSize((float)size);
            this.paintCenterText.getTextBounds(contentText, 0, contentText.length(), rect);
        }

        this.paintOuterText.setTextSize((float)size);
    }

    private int getLoopMappingIndex(int index) {
        if (index < 0) {
            index += this.adapter.getItemsCount();
            index = this.getLoopMappingIndex(index);
        } else if (index > this.adapter.getItemsCount() - 1) {
            index -= this.adapter.getItemsCount();
            index = this.getLoopMappingIndex(index);
        }

        return index;
    }

    private String getContentText(Object item) {
        if (item == null) {
            return "";
        } else if (item instanceof IPickerViewData) {
            return ((IPickerViewData)item).getPickerViewText();
        } else {
            return item instanceof Integer ? String.format(Locale.getDefault(), "%02d", (Integer)item) : item.toString();
        }
    }

    private void measuredCenterContentStart(String content) {
        Rect rect = new Rect();
        this.paintCenterText.getTextBounds(content, 0, content.length(), rect);
        switch(this.mGravity) {
            case 3:
                this.drawCenterContentStart = 0;
                break;
            case 5:
                this.drawCenterContentStart = this.measuredWidth - rect.width() - (int)this.CENTERCONTENTOFFSET;
                break;
            case 17:
                if (!this.isOptions && this.label != null && !this.label.equals("") && this.isCenterLabel) {
                    this.drawCenterContentStart = (int)((double)(this.measuredWidth - rect.width()) * 0.25D);
                } else {
                    this.drawCenterContentStart = (int)((double)(this.measuredWidth - rect.width()) * 0.5D);
                }
        }

    }

    private void measuredOutContentStart(String content) {
        Rect rect = new Rect();
        this.paintOuterText.getTextBounds(content, 0, content.length(), rect);
        switch(this.mGravity) {
            case 3:
                this.drawOutContentStart = 0;
                break;
            case 5:
                this.drawOutContentStart = this.measuredWidth - rect.width() - (int)this.CENTERCONTENTOFFSET;
                break;
            case 17:
                if (!this.isOptions && this.label != null && !this.label.equals("") && this.isCenterLabel) {
                    this.drawOutContentStart = (int)((double)(this.measuredWidth - rect.width()) * 0.25D);
                } else {
                    this.drawOutContentStart = (int)((double)(this.measuredWidth - rect.width()) * 0.5D);
                }
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.widthMeasureSpec = widthMeasureSpec;
        this.remeasure();
        this.setMeasuredDimension(this.measuredWidth, this.measuredHeight);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = this.gestureDetector.onTouchEvent(event);
        float top;
        switch(event.getAction()) {
            case 0:
                this.startTime = System.currentTimeMillis();
                this.cancelFuture();
                this.previousY = event.getRawY();
                break;
            case 1:
            default:
                if (!eventConsumed) {
                    top = event.getY();
                    double L = Math.acos((double)(((float)this.radius - top) / (float)this.radius)) * (double)this.radius;
                    int circlePosition = (int)((L + (double)(this.itemHeight / 2.0F)) / (double)this.itemHeight);
                    float extraOffset = (this.totalScrollY % this.itemHeight + this.itemHeight) % this.itemHeight;
                    this.mOffset = (int)((float)(circlePosition - this.itemsVisible / 2) * this.itemHeight - extraOffset);
                    if (System.currentTimeMillis() - this.startTime > 120L) {
                        this.smoothScroll(ACTION.DAGGLE);
                    } else {
                        this.smoothScroll(ACTION.CLICK);
                    }
                }
                break;
            case 2:
                float dy = this.previousY - event.getRawY();
                this.previousY = event.getRawY();
                this.totalScrollY += dy;
                if (!this.isLoop) {
                    top = (float)(-this.initPosition) * this.itemHeight;
                    float bottom = (float)(this.adapter.getItemsCount() - 1 - this.initPosition) * this.itemHeight;
                    if ((double)this.totalScrollY - (double)this.itemHeight * 0.25D < (double)top) {
                        top = this.totalScrollY - dy;
                    } else if ((double)this.totalScrollY + (double)this.itemHeight * 0.25D > (double)bottom) {
                        bottom = this.totalScrollY - dy;
                    }

                    if (this.totalScrollY < top) {
                        this.totalScrollY = (float)((int)top);
                    } else if (this.totalScrollY > bottom) {
                        this.totalScrollY = (float)((int)bottom);
                    }
                }
        }

        this.invalidate();
        return true;
    }

    public int getItemsCount() {
        return this.adapter != null ? this.adapter.getItemsCount() : 0;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void isCenterLabel(Boolean isCenterLabel) {
        this.isCenterLabel = isCenterLabel;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);

            for(int j = 0; j < len; ++j) {
                iRet += (int)Math.ceil((double)widths[j]);
            }
        }

        return iRet;
    }

    public void setIsOptions(boolean options) {
        this.isOptions = options;
    }

    public void setTextColorOut(int textColorOut) {
        if (textColorOut != 0) {
            this.textColorOut = textColorOut;
            this.paintOuterText.setColor(this.textColorOut);
        }

    }

    public void setTextColorCenter(int textColorCenter) {
        if (textColorCenter != 0) {
            this.textColorCenter = textColorCenter;
            this.paintCenterText.setColor(this.textColorCenter);
        }

    }

    public void setDividerColor(int dividerColor) {
        if (dividerColor != 0) {
            this.dividerColor = dividerColor;
            this.paintIndicator.setColor(this.dividerColor);
        }

    }

    public void setDividerType(DividerType dividerType) {
        this.dividerType = dividerType;
    }

    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        if (lineSpacingMultiplier != 0.0F) {
            this.lineSpacingMultiplier = lineSpacingMultiplier;
            this.judgeLineSpae();
        }

    }

    public static enum DividerType {
        FILL,
        WRAP,
        ROUND_RECT;

        private DividerType() {
        }
    }

    public static enum ACTION {
        CLICK,
        FLING,
        DAGGLE;

        private ACTION() {
        }
    }
}
