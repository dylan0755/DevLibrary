package com.dylan.mylibrary.ui;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2019/8/3
 * Desc:
 */
public class CircleAnimationActivity extends AppCompatActivity {
    LinearLayout animLayout;
    private Button btstart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_circleanimation);
        animLayout =findViewById(R.id.rootLayout);
        btstart=findViewById(R.id.btStart);
        btstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup viewgroup= (ViewGroup) v.getParent();
                int width=viewgroup.getMeasuredWidth();
                int height=viewgroup.getMeasuredHeight();
                int widhtSpec=View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
                int heightSpec= View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
                animLayout.measure(widhtSpec,heightSpec);
                startAnimation(v);
            }
        });

    }


    public void startAnimation(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] viewLocation=new int[2];
            view.getLocationInWindow(viewLocation);
            int cenX=viewLocation[0]+view.getMeasuredWidth()/2;
            int cenY=view.getMeasuredHeight()/2;
            float radius = animLayout.getMeasuredHeight();

            Animator animator;
            final boolean isVisible=(animLayout.getVisibility()==View.VISIBLE);
            if (isVisible){
                animator= ViewAnimationUtils.createCircularReveal(animLayout,cenX,cenY,radius,0);
            }else{//openAnimation
                animLayout.setVisibility(View.VISIBLE);
                animator=ViewAnimationUtils.createCircularReveal(animLayout,cenX,cenY,0,radius);
            }
            animator.setDuration(350);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isVisible){
                        animLayout.setVisibility(View.GONE);
                        btstart.setText("开始");
                    }else{
                        btstart.setText("结束");
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();

        }
    }
}
