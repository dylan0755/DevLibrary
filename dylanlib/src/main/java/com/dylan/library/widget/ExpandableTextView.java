package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.HtmlUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.widget.OverLinkMovementMethod;

import java.lang.reflect.Field;

/**
 * Author: Dylan
 * Date: 2021/05/29
 * Desc: 布局里面不要设置padding， 用margin 代替，否则展开 不会在文本末尾
 */
public class ExpandableTextView extends AppCompatTextView {
    private static final String TAG = ExpandableTextView.class.getSimpleName();

    public static final String ELLIPSIS_STRING = new String(new char[]{'\u2026'});
    private static final int DEFAULT_MAX_LINE = 3;
    private static final String DEFAULT_OPEN_SUFFIX = "展开";
    private static final String DEFAULT_CLOSE_SUFFIX = "收起";
    volatile boolean animating = false;
    boolean isClosed = false;
    private int mMaxLines = DEFAULT_MAX_LINE;
    /** TextView可展示宽度，包含paddingLeft和paddingRight */
    private int initWidth = 0;
    /** 原始文本 */
    private SpannableString mStyleSpan;

    private SpannableStringBuilder mOpenSpannableStr, mCloseSpannableStr;

    private boolean hasAnimation = false;
    private Animation mOpenAnim, mCloseAnim;
    private int mOpenHeight, mCLoseHeight;
    private boolean mExpandable;
    private boolean mCloseInNewLine;
    private SpannableString mOpenSuffixSpan, mCloseSuffixSpan;
    private String mOpenSuffixStr = DEFAULT_OPEN_SUFFIX;
    private String mCloseSuffixStr = DEFAULT_CLOSE_SUFFIX;
    private int mOpenSuffixColor, mCloseSuffixColor;
    private Drawable openDrawable,closeDrawable;
    private int openDrawableWidth,openDrawableHeight;
    private int closeDrawableWidth,closeDrawableHeight;
    private boolean isSuffixBold;

    private OnClickListener mOnClickListener;

    private CharSequenceToSpannableHandler mCharSequenceToSpannableHandler;

    public ExpandableTextView(Context context) {
        super(context);
        initialize();
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    /** 初始化 */
    private void initialize() {
        setHighlightColor(Color.TRANSPARENT);
        mOpenSuffixColor = mCloseSuffixColor = Color.parseColor("#F23030");
        setMovementMethod(OverLinkMovementMethod.getInstance());
        setIncludeFontPadding(false);
        updateOpenSuffixSpan();
        updateCloseSuffixSpan();
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }



    public void setOriginalText(SpannableString styleSpan){
        this.mStyleSpan=styleSpan;
        mExpandable = false;
        mCloseSpannableStr = new SpannableStringBuilder();
        final int maxLines = mMaxLines;
        SpannableStringBuilder tempText = charSequenceToSpannable(mStyleSpan);
        mOpenSpannableStr = charSequenceToSpannable(styleSpan);


        if (maxLines != -1) {
            Layout layout = createStaticLayout(tempText);
            mExpandable = layout.getLineCount() > maxLines;
            if (mExpandable) {
                //拼接展开内容
                if (mCloseInNewLine) {
                    mOpenSpannableStr.append("\n");
                }
                if (mCloseSuffixSpan != null) {
                    mOpenSpannableStr.append(mCloseSuffixSpan);
                }
                //计算原文截取位置
                int endPos = layout.getLineEnd(maxLines - 1);
                if (mStyleSpan.length() <= endPos) {
                    mCloseSpannableStr = charSequenceToSpannable(mStyleSpan);
                } else {
                    mCloseSpannableStr = charSequenceToSpannable(mStyleSpan.subSequence(0, endPos));
                }
                SpannableStringBuilder tempText2 = charSequenceToSpannable(mCloseSpannableStr).append(ELLIPSIS_STRING);
                if (mOpenSuffixSpan != null) {
                    tempText2.append(mOpenSuffixSpan);
                }
                Logger.e(tempText2);
                //循环判断，收起内容添加展开后缀后的内容
                Layout tempLayout = createStaticLayout(tempText2);
                while (tempLayout.getLineCount() > maxLines) {
                    int lastSpace = mCloseSpannableStr.length() - 1;
                    if (lastSpace == -1) {
                        break;
                    }
                    if (mStyleSpan.length() <= lastSpace) {
                        mCloseSpannableStr = charSequenceToSpannable(styleSpan);
                    } else {
                        int end=lastSpace;
                        if (styleSpan.length()-1<end)end=styleSpan.length()-1;
                        mCloseSpannableStr = charSequenceToSpannable(styleSpan.subSequence(0, end));
                    }
                    tempText2 = charSequenceToSpannable(mCloseSpannableStr).append(ELLIPSIS_STRING);
                    if (mOpenSuffixSpan != null) {
                        tempText2.append(mOpenSuffixSpan);
                    }
                    tempLayout = createStaticLayout(tempText2);
                }

                //添加省略号
                int ellipsisEnd=mCloseSpannableStr.length();
                mCloseSpannableStr.append(ELLIPSIS_STRING);

                //折叠的最后一行未满一行，展开按钮不能放在最右，所以填充空格
                boolean hasFillSpace=false;//是否有填充空格
                int spaceCount=0;
                if (tempLayout.getLineCount()==maxLines
                        &&mOpenSuffixSpan!=null){
                    SpannableStringBuilder temSpan=new SpannableStringBuilder(mCloseSpannableStr);
                    Layout tempLayout333 = createStaticLayout(temSpan);
                    while(tempLayout333.getLineCount()==maxLines){
                        SpannableStringBuilder spanInner=new SpannableStringBuilder(mCloseSpannableStr);
                        spanInner.append(" ").append(ELLIPSIS_STRING).append(mOpenSuffixSpan);
                        tempLayout333=createStaticLayout(spanInner);
                        if (tempLayout333.getLineCount()==maxLines){
                            hasFillSpace=true;
                            mCloseSpannableStr.append(" ");
                            spaceCount++;
                        }else{
                            break;
                        }
                    }
                }
                mCloseSpannableStr.append(mOpenSuffixSpan);
                //计算收起的文本高度
                mCLoseHeight = tempLayout.getHeight() + getPaddingTop() + getPaddingBottom();
            }
        }
        isClosed = mExpandable;
        if (mExpandable) {
            setText(mCloseSpannableStr);
        } else {
            setText(mOpenSpannableStr);
        }
    }

    public void setOriginalText(String srcText) {
        if (EmptyUtils.isEmpty(srcText))return;
        SpannableString spannableString = new SpannableString(autoSplitText(srcText));
        setOriginalText(spannableString);

    }

    private int hasEnCharCount(CharSequence str){
        int count = 0;
        if(!TextUtils.isEmpty(str)){
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if(c >= ' ' && c <= '~'){
                    count++;
                }
            }
        }
        return count;
    }

    private void switchOpenClose() {
        if (mExpandable) {
            isClosed = !isClosed;
            if (isClosed) {
                close();
            } else {
                open();
            }
        }
    }

    /**
     * 设置是否有动画
     *
     * @param hasAnimation
     */
    public void setHasAnimation(boolean hasAnimation) {
        this.hasAnimation = hasAnimation;
    }

    /** 展开 */
    private void open() {
        if (hasAnimation) {
            Layout layout = createStaticLayout(mOpenSpannableStr);
            mOpenHeight = layout.getHeight() + getPaddingTop() + getPaddingBottom();
            executeOpenAnim();
        } else {
            ExpandableTextView.super.setMaxLines(Integer.MAX_VALUE);
            setText(mOpenSpannableStr);
            if (mOpenCloseCallback != null){
                mOpenCloseCallback.onOpen();
            }
        }
    }

    /** 收起 */
    private void close() {
        if (hasAnimation) {
            executeCloseAnim();
        } else {
            ExpandableTextView.super.setMaxLines(mMaxLines);
            setText(mCloseSpannableStr);
            if (mOpenCloseCallback != null){
                mOpenCloseCallback.onClose();
            }
        }
    }

    /** 执行展开动画 */
    private void executeOpenAnim() {
        //创建展开动画
        if (mOpenAnim == null) {
            mOpenAnim = new ExpandCollapseAnimation(this, mCLoseHeight, mOpenHeight);
            mOpenAnim.setFillAfter(true);
            mOpenAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ExpandableTextView.super.setMaxLines(Integer.MAX_VALUE);
                    setText(mOpenSpannableStr);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //  动画结束后textview设置展开的状态
                    getLayoutParams().height = mOpenHeight;
                    requestLayout();
                    animating = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        if (animating) {
            return;
        }
        animating = true;
        clearAnimation();
        //  执行动画
        startAnimation(mOpenAnim);
    }

    /** 执行收起动画 */
    private void executeCloseAnim() {
        //创建收起动画
        if (mCloseAnim == null) {
            mCloseAnim = new ExpandCollapseAnimation(this, mOpenHeight, mCLoseHeight);
            mCloseAnim.setFillAfter(true);
            mCloseAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animating = false;
                    ExpandableTextView.super.setMaxLines(mMaxLines);
                    setText(mCloseSpannableStr);
                    getLayoutParams().height = mCLoseHeight;
                    requestLayout();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        if (animating) {
            return;
        }
        animating = true;
        clearAnimation();
        //  执行动画
        startAnimation(mCloseAnim);
    }

    /**
     * @param spannable
     *
     * @return
     */
    private Layout createStaticLayout(SpannableStringBuilder spannable) {
        // int contentWidth = initWidth - getPaddingLeft() - getPaddingRight();
        int contentWidth = initWidth;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            StaticLayout.Builder builder = StaticLayout.Builder.obtain(spannable, 0, spannable.length(), getPaint(), contentWidth);
            builder.setAlignment(Layout.Alignment.ALIGN_OPPOSITE);
            builder.setIncludePad(getIncludeFontPadding());
            builder.setLineSpacing(getLineSpacingExtra(), getLineSpacingMultiplier());
            return builder.build();
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new StaticLayout(spannable, getPaint(), contentWidth, Layout.Alignment.ALIGN_NORMAL,
                    getLineSpacingMultiplier(), getLineSpacingExtra(), getIncludeFontPadding());
        }else{
            return new StaticLayout(spannable, getPaint(), contentWidth, Layout.Alignment.ALIGN_NORMAL,
                    getFloatField("mSpacingMult",1f), getFloatField("mSpacingAdd",0f), getIncludeFontPadding());
        }
    }

    private float getFloatField(String fieldName,float defaultValue){
        float value = defaultValue;
        if(TextUtils.isEmpty(fieldName)){
            return value;
        }
        try {
            // 获取该类的所有属性值域
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field:fields) {
                if(TextUtils.equals(fieldName,field.getName())){
                    value = field.getFloat(this);
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }


    /**
     * @param charSequence
     *
     * @return
     */
    private SpannableStringBuilder charSequenceToSpannable(@NonNull CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = null;
        if (mCharSequenceToSpannableHandler != null) {
            spannableStringBuilder = mCharSequenceToSpannableHandler.charSequenceToSpannable(charSequence);
        }
        if (spannableStringBuilder == null) {
            spannableStringBuilder = new SpannableStringBuilder(charSequence);
        }
        return spannableStringBuilder;
    }

    /**
     * 初始化TextView的可展示宽度
     *
     * @param width
     */
    public void initWidth(int width) {
        initWidth = width;
    }

    @Override
    public void setMaxLines(int maxLines) {
        this.mMaxLines = maxLines;
        super.setMaxLines(maxLines);
    }

    /**
     * 设置展开后缀text
     *
     * @param openSuffix
     */
    public void setOpenSuffix(String openSuffix) {
        mOpenSuffixStr = openSuffix;
        updateOpenSuffixSpan();
    }

    /**
     * 设置展开后缀文本颜色
     *
     * @param openSuffixColor
     */
    public void setOpenSuffixColor(@ColorInt int openSuffixColor) {
        mOpenSuffixColor = openSuffixColor;
        updateOpenSuffixSpan();
    }

    /**
     * 设置收起后缀text
     *
     * @param closeSuffix
     */
    public void setCloseSuffix(String closeSuffix) {
        mCloseSuffixStr = closeSuffix;
        updateCloseSuffixSpan();
    }

    /**
     * 设置收起后缀文本颜色
     *
     * @param closeSuffixColor
     */
    public void setCloseSuffixColor(@ColorInt int closeSuffixColor) {
        mCloseSuffixColor = closeSuffixColor;
        updateCloseSuffixSpan();
    }

    /**
     * 收起后缀是否另起一行
     *
     * @param closeInNewLine
     */
    public void setCloseInNewLine(boolean closeInNewLine) {
        mCloseInNewLine = closeInNewLine;
        updateCloseSuffixSpan();
    }

    public void setSuffixBold(boolean suffixBold) {
        isSuffixBold = suffixBold;
    }

    public void setOpenDrawable(Drawable drawable, int width, int height){
        openDrawable=drawable;
        openDrawableWidth=width;
        openDrawableHeight=height;
        updateOpenSuffixSpan();
    }


    public void setCloseDrawable(Drawable drawable,int width,int height){
        closeDrawable=drawable;
        closeDrawableWidth=width;
        closeDrawableHeight=height;
        updateCloseSuffixSpan();
    }

    /** 更新展开后缀Spannable */
    private void updateOpenSuffixSpan() {
        if (TextUtils.isEmpty(mOpenSuffixStr)) {
            mOpenSuffixSpan = null;
            return;
        }
        mOpenSuffixSpan = new SpannableString(mOpenSuffixStr);
        //把"展开"替换成图片
        if (openDrawable!=null){
            openDrawable.setBounds(0, 0, openDrawableWidth, openDrawableHeight);  //确定图标所在的矩形位置，这里应该是要以text文本的下边距为基准的
            ImageSpan imageSpan = new ImageSpan(openDrawable);
            mOpenSuffixSpan.setSpan(imageSpan,mOpenSuffixStr.length()-2,mOpenSuffixStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }else{
            if (isSuffixBold)mOpenSuffixSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, mOpenSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        mOpenSuffixSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                switchOpenClose();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(mOpenSuffixColor);
                ds.setUnderlineText(false);
                ds.bgColor=Color.TRANSPARENT;
                ds.linkColor=Color.TRANSPARENT;
            }
        },0, mOpenSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    /** 更新收起后缀Spannable */
    private void updateCloseSuffixSpan() {
        if (TextUtils.isEmpty(mCloseSuffixStr)) {
            mCloseSuffixSpan = null;
            return;
        }
        mCloseSuffixSpan = new SpannableString(mCloseSuffixStr);
        if (closeDrawable!=null){
            closeDrawable.setBounds(0, 0, closeDrawableWidth, closeDrawableHeight);  //确定图标所在的矩形位置，这里应该是要以text文本的下边距为基准的
            ImageSpan imageSpan = new ImageSpan(closeDrawable);
            mCloseSuffixSpan.setSpan(imageSpan,mOpenSuffixStr.length()-2,mOpenSuffixStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }else{
            if (isSuffixBold)mCloseSuffixSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, mCloseSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        if (mCloseInNewLine) {
            AlignmentSpan alignmentSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE);
            mCloseSuffixSpan.setSpan(alignmentSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mCloseSuffixSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                switchOpenClose();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(mCloseSuffixColor);
                ds.setUnderlineText(false);
                ds.bgColor=Color.TRANSPARENT;
                ds.linkColor=Color.TRANSPARENT;
            }
        },1, mCloseSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public OpenAndCloseCallback mOpenCloseCallback;
    public void setOpenAndCloseCallback(OpenAndCloseCallback callback){
        this.mOpenCloseCallback = callback;
    }

    public interface OpenAndCloseCallback{
        void onOpen();
        void onClose();
    }
    /**
     * 设置文本内容处理
     *
     * @param handler
     */
    public void setCharSequenceToSpannableHandler(CharSequenceToSpannableHandler handler) {
        mCharSequenceToSpannableHandler = handler;
    }

    public interface CharSequenceToSpannableHandler {
        @NonNull
        SpannableStringBuilder charSequenceToSpannable(CharSequence charSequence);
    }

    class ExpandCollapseAnimation extends Animation {
        private final View mTargetView;//动画执行view
        private final int mStartHeight;//动画执行的开始高度
        private final int mEndHeight;//动画结束后的高度

        ExpandCollapseAnimation(View target, int startHeight, int endHeight) {
            mTargetView = target;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            setDuration(400);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            mTargetView.setScrollY(0);
            //计算出每次应该显示的高度,改变执行view的高度，实现动画
            mTargetView.getLayoutParams().height = (int) ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight);
            mTargetView.requestLayout();
        }
    }


    private String autoSplitText(CharSequence originalText) {
        final String rawText = originalText.toString(); //原始文本
        final Paint tvPaint = getPaint(); //paint，包含字体等信息
        final float tvWidth = initWidth; //控件可用宽度

        //将原始文本按行拆分
        String[] rawTextLines = rawText.replaceAll("\r", "").split("\n");
        StringBuilder sbNewText = new StringBuilder();
        for (String rawTextLine : rawTextLines) {
            if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                //如果整行宽度在控件可用宽度之内，就不处理了
                sbNewText.append(rawTextLine);
            } else {
                //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                float lineWidth = 0;
                for (int cnt = 0; cnt != rawTextLine.length(); ++cnt) {
                    char ch = rawTextLine.charAt(cnt);
                    lineWidth += tvPaint.measureText(String.valueOf(ch));
                    if (lineWidth <= tvWidth) {
                        sbNewText.append(ch);
                    } else {
                        sbNewText.append("\n");
                        lineWidth = 0;
                        --cnt;
                    }
                }
            }
            sbNewText.append("\n");
        }

        //把结尾多余的\n去掉
        if (!rawText.endsWith("\n")) {
            sbNewText.deleteCharAt(sbNewText.length() - 1);
        }

        return sbNewText.toString();
    }
}