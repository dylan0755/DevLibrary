package com.dylan.mylibrary.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.widget.banner.Banner;
import com.dylan.library.widget.banner.HolderCreator;
import com.dylan.library.widget.banner.ScaleInTransformer;
import com.dylan.library.widget.shape.ShapeTextView;
import com.dylan.mylibrary.R;
import com.hjq.toast.Toaster;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Author: Dylan
 * Date: 2022/03/24
 * Desc:
 */
public class PostBannerDialog extends BaseDialog {
    @BindView(R.id.poster_share_banner)
    Banner posterShareBanner;
    @BindView(R.id.tvSavePoster)
    ShapeTextView tvSavePoster;
    private int defaultPosition = 0;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_post_banner;
    }

    public PostBannerDialog(Context context) {
        super(context,true);
        findViewById(R.id.maskView).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                dismiss();
            }
        });
    }


    public void show() {
        super.show();
        getCategoryData();
    }


    private void getCategoryData() {
        List<String> imgs=new ArrayList<>();
        imgs.add("http://api.test.tcbvip.com/attach/code/user/ZZZZZZ/1.png");
        imgs.add("http://api.test.tcbvip.com/attach/code/user/ZZZZZZ/2.png");
        findViewById(R.id.tvSavePoster).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Toaster.show("敬请期待");
            }
        });
        posterShareBanner.setIndicator(null)
                .setHolderCreator(new ImageHolderCreator())
                .setPages(imgs);

        posterShareBanner.setPageMargin(DensityUtils.dp2px(getContext(), 8), DensityUtils.dp2px(getContext(), 35))//设置左右页面露出来的宽度及item与item之间的宽度
                .setPageTransformer(true, new ScaleInTransformer())//内置ScaleInTransformer，设置切换缩放动画
                .setAutoPlay(false)
                .setOuterPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        defaultPosition = position;
                        Logger.d(position + ">>>>>>2>>>>");

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

    }


    public static class ImageHolderCreator implements HolderCreator {
        @Override
        public View createView(final Context context, final int index, Object o) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(iv)
                    .load(o)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(DensityUtils.dp2px(context,8))))
                    .into(iv);
            return iv;
        }
    }

}
