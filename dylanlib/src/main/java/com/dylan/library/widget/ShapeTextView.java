package com.dylan.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dylan.library.R;

/**
 * Author: Dylan
 * Date: 2020/3/26
 * Desc:
 */
public class ShapeTextView extends TextView {
    private boolean openSelector;
    //自定背景边框Drawable
    private GradientDrawable gradientDrawable;
    //按下时的Drawable
    private GradientDrawable selectorDrawable;
    //填充色
    private int solidColor = 0;
    //边框色
    private int strokeColor = 0;
    //按下填充色
    private int solidTouchColor = 0;
    //按下边框色
    private int strokeTouchColor = 0;
    //边框宽度
    private int strokeWidth = 0;
    //四个角的弧度
    private float radius;
    private float topLeftRadius;
    private float topRightRadius;
    private float bottomLeftRadius;
    private float bottomRightRadius;
    //边框虚线的宽度
    float dashWidth = 0;
    //边框虚线的间隙
    float dashGap = 0;
    //字体色
    private int textColor = 0;


    public ShapeTextView(Context context) {
        this(context, null);
    }

    public ShapeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        //默认背景
        gradientDrawable = getNeedDrawable(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius,
                        bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius},
                solidColor, strokeWidth, strokeColor, dashWidth, dashGap);
        //如果设置了选中时的背景
        if (openSelector) {
            selectorDrawable = getNeedDrawable(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius,
                            bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius},
                    solidTouchColor, strokeWidth, strokeTouchColor, dashWidth, dashGap);

            //动态生成Selector
            StateListDrawable stateListDrawable = new StateListDrawable();
            //是否按下
            int pressed = android.R.attr.state_pressed;

            stateListDrawable.addState(new int[]{pressed}, selectorDrawable);
            stateListDrawable.addState(new int[]{}, gradientDrawable);
            setBackgroundDrawable(stateListDrawable);
        } else {
            setBackgroundDrawable(gradientDrawable);
        }
    }

    /**
     * 初始化参数
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DLShapeTextView, 0, 0);

        openSelector = ta.getBoolean(R.styleable.DLShapeTextView_openSelector, false);

        solidColor = ta.getInteger(R.styleable.DLShapeTextView_solidColor, 0x00000000);
        strokeColor = ta.getInteger(R.styleable.DLShapeTextView_strokeColor, 0x00000000);

        solidTouchColor = ta.getInteger(R.styleable.DLShapeTextView_solidTouchColor, 0x00000000);
        strokeTouchColor = ta.getInteger(R.styleable.DLShapeTextView_strokeTouchColor, 0x00000000);
        textColor = getCurrentTextColor();
        strokeWidth = (int) ta.getDimension(R.styleable.DLShapeTextView_strokeWidth, 0);

        //四个角单独设置会覆盖radius设置
        radius = ta.getDimension(R.styleable.DLShapeTextView_radius, 0);
        topLeftRadius = ta.getDimension(R.styleable.DLShapeTextView_topLeftRadius, radius);
        topRightRadius = ta.getDimension(R.styleable.DLShapeTextView_topRightRadius, radius);
        bottomLeftRadius = ta.getDimension(R.styleable.DLShapeTextView_bottomLeftRadius, radius);
        bottomRightRadius = ta.getDimension(R.styleable.DLShapeTextView_bottomRightRadius, radius);

        dashGap = ta.getDimension(R.styleable.DLShapeTextView_dashGap, 0);
        dashWidth = ta.getDimension(R.styleable.DLShapeTextView_dashWidth, 0);

        ta.recycle();
    }

    /**
     * @param radius      四个角的半径
     * @param colors      渐变的颜色
     * @param strokeWidth 边框宽度
     * @param strokeColor 边框颜色
     * @return
     */
    public static GradientDrawable getNeedDrawable(float[] radius, int[] colors, int strokeWidth, int strokeColor) {
        //TODO:判断版本是否大于16  项目中默认的都是Linear散射 都是从左到右 都是只有开始颜色和结束颜色
        GradientDrawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            drawable = new GradientDrawable();
            drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            drawable.setColors(colors);
        } else {
            drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        }

        drawable.setCornerRadii(radius);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        return drawable;
    }

    /**
     * @param radius      四个角的半径
     * @param bgColor     背景颜色
     * @param strokeWidth 边框宽度
     * @param strokeColor 边框颜色
     * @return
     */
    public static GradientDrawable getNeedDrawable(float[] radius, int bgColor, int strokeWidth, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadii(radius);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setColor(bgColor);
        return drawable;
    }

    /**
     * @param radius      四个角的半径
     * @param bgColor     背景颜色
     * @param strokeWidth 边框宽度
     * @param strokeColor 边框颜色
     * @param dashWidth   虚线边框宽度
     * @param dashGap     虚线边框间隙
     * @return
     */
    public static GradientDrawable getNeedDrawable(float[] radius, int bgColor, int strokeWidth, int strokeColor, float dashWidth, float dashGap) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadii(radius);
        drawable.setStroke(strokeWidth, strokeColor, dashWidth, dashGap);
        drawable.setColor(bgColor);
        return drawable;
    }
}