package com.dylan.library.widget.shape;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.dylan.library.R;

/**
 * Author: Administrator
 * Date: 2020/7/29
 * Desc:
 */
public class ShapeViewHelper {
    private boolean openSelector;
    //自定背景边框Drawable
    private GradientDrawable gradientDrawable;
    //按下时的Drawable
    private GradientDrawable selectorDrawable;
    //填充色
    private int solidColor = 0;
    //边框色
    private int strokeColor = 0;
    //渐变开始颜色
    private int startColor=0;
    //渐变结束颜色
    private int endColor=0;
    private float angle=0;
    //按下填充色
    private int solidTouchColor = 0;
    //按下边框色
    private int strokeTouchColor = 0;
    //边框宽度
    private int strokeWidth = 0;
    //背景透明度
    private float shapeAlpha=1.0f;
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
    private int[] colors;


    /**
     * 初始化参数
     *
     * @param context
     * @param attrs
     */
    public void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DLShapeView, 0, 0);

        openSelector = ta.getBoolean(R.styleable.DLShapeView_openSelector, false);

        solidColor = ta.getInteger(R.styleable.DLShapeView_solidColor, 0x00000000);
        strokeColor = ta.getInteger(R.styleable.DLShapeView_strokeColor, 0x00000000);
        startColor=ta.getInteger(R.styleable.DLShapeView_startColor,-1);
        endColor=ta.getInteger(R.styleable.DLShapeView_endColor,-1);
        angle=ta.getFloat(R.styleable.DLShapeView_angle,0);

        if (startColor!=-1&&endColor!=-1){
            colors=new int[]{startColor,endColor};
        }else{
            colors=new int[]{solidColor};
        }

        solidTouchColor = ta.getInteger(R.styleable.DLShapeView_solidTouchColor, 0x00000000);
        strokeTouchColor = ta.getInteger(R.styleable.DLShapeView_strokeTouchColor, 0x00000000);
        strokeWidth = (int) ta.getDimension(R.styleable.DLShapeView_strokeWidth, 0);

        //四个角单独设置会覆盖radius设置
        radius = ta.getDimension(R.styleable.DLShapeView_radius, 0);
        topLeftRadius = ta.getDimension(R.styleable.DLShapeView_topLeftRadius, radius);
        topRightRadius = ta.getDimension(R.styleable.DLShapeView_topRightRadius, radius);
        bottomLeftRadius = ta.getDimension(R.styleable.DLShapeView_bottomLeftRadius, radius);
        bottomRightRadius = ta.getDimension(R.styleable.DLShapeView_bottomRightRadius, radius);

        dashGap = ta.getDimension(R.styleable.DLShapeView_dashGap, 0);
        dashWidth = ta.getDimension(R.styleable.DLShapeView_dashWidth, 0);
        shapeAlpha=ta.getFloat(R.styleable.DLShapeView_shapeAlpha,1.0f);




        ta.recycle();
    }



    public void setCustomBackground(View targetView) {
        //默认背景
        gradientDrawable = getNeedDrawable(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius,
                        bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius},
                colors, angle,strokeWidth, strokeColor, dashWidth, dashGap);
        //如果设置了选中时的背景
        if (openSelector) {
            selectorDrawable = getNeedDrawable(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius,
                            bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius},
                    new int[]{startColor,solidTouchColor,endColor}, angle, strokeWidth, strokeTouchColor, dashWidth, dashGap);

            //动态生成Selector
            StateListDrawable stateListDrawable = new StateListDrawable();
            //是否按下
            int pressed = android.R.attr.state_pressed;

            stateListDrawable.addState(new int[]{pressed}, selectorDrawable);
            stateListDrawable.addState(new int[]{}, gradientDrawable);
            stateListDrawable.setAlpha((int) (shapeAlpha*255.0f));
            targetView.setBackgroundDrawable(stateListDrawable);
        } else {
            gradientDrawable.setAlpha((int) (shapeAlpha*255.0f));
            targetView.setBackgroundDrawable(gradientDrawable);
        }
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
    public static GradientDrawable getNeedDrawable(float[] radius, int[]bgColor, float angle,int strokeWidth, int strokeColor, float dashWidth, float dashGap) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadii(radius);
        drawable.setUseLevel(false);
        drawable.setStroke(strokeWidth, strokeColor, dashWidth, dashGap);
        if (bgColor.length>1){
            drawable.setColors(bgColor);
            drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);//线性渐变
            if (angle==0||angle==360){
                drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            }else if (angle==90){
                drawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
            }else if (angle==180){
                drawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
            }else if (angle==270){//上到下
                drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            }
        }else{
            drawable.setColor(bgColor[0]);
        }
        return drawable;
    }



}
