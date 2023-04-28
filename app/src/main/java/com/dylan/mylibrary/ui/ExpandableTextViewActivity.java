package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.dylan.library.utils.CompatUtils;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.widget.ExpandableTextView;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2021/05/29
 * Desc:
 */
public class ExpandableTextViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandabletextview);

        ExpandableTextView expandableTextView = findViewById(R.id.expanded_text);
        int viewWidth = getWindowManager().getDefaultDisplay().getWidth() - DensityUtils.dp2px(this, 20f);
        expandableTextView.initWidth(viewWidth);
        expandableTextView.setMaxLines(3);
        expandableTextView.setHasAnimation(true);
        expandableTextView.setCloseInNewLine(true);
        expandableTextView.setTextColor(Color.GRAY);
        expandableTextView.setOpenSuffixColor(Color.parseColor("#9B9B9B"));
        expandableTextView.setCloseSuffixColor(Color.parseColor("#9B9B9B"));
        //将 展开 收起 替换成图片
        expandableTextView.setOpenDrawable(CompatUtils.getDrawable(R.drawable.icon_text_expand),
                DensityUtils.dp2px(this, 54),
                DensityUtils.dp2px(this, 12));
        expandableTextView.setCloseDrawable(CompatUtils.getDrawable(R.drawable.icon_text_packup),
                DensityUtils.dp2px(this, 54),
                DensityUtils.dp2px(this, 12));
        String originText = "只需要1个APP，让你的创作效率高出120倍！\n这款APP，把创作短视频遇到的问题几乎都一站式解决了\n为什么你创作视频效率提不上去？\n使劲背台词？写剧本没头绪？不知道哪找去水印工具？视频做好了不懂变现？\n我们开发了这样一款产品，叫提词宝APP\n你只需要下载这一款，你的所有创作烦恼都被解决了！\n从提词到变现，一站式解决，不仅仅是工具更是生态！\n四大核心功能，集结提词，创作，工具，变现。\n九种提词模式，十项创作功能，九大视频工具，六大变现模块\n让拍视频，写剧本，创作视频，变现更简单\n让更多人拥抱创作自由！";
        expandableTextView.setOriginalText(originText);


        ExpandableTextView expandableTextView2 = findViewById(R.id.expanded_text2);
        expandableTextView2.initWidth(viewWidth);
        expandableTextView2.setMaxLines(3);
        expandableTextView2.setHasAnimation(true);
        expandableTextView2.setCloseInNewLine(true);
        expandableTextView2.setTextColor(Color.GRAY);
        expandableTextView2.setOpenSuffixColor(Color.parseColor("#9B9B9B"));
        expandableTextView2.setCloseSuffixColor(Color.parseColor("#9B9B9B"));
        //将 展开 收起 替换成图片
        expandableTextView2.setOpenDrawable(CompatUtils.getDrawable(R.drawable.icon_text_expand),
                DensityUtils.dp2px(this, 54),
                DensityUtils.dp2px(this, 12));
        expandableTextView2.setCloseDrawable(CompatUtils.getDrawable(R.drawable.icon_text_packup),
                DensityUtils.dp2px(this, 54),
                DensityUtils.dp2px(this, 12));

        expandableTextView2.setOriginalText("在全球，随着Flutter被越来越多的知名公司应用在自己的商业APP中，" +
                "Flutter这门新技术也逐渐进入了移动开发者的视野，尤其是当Google在2018年IO大会上发布了第一个" +
                "Preview版本后，国内刮起来一股学习Flutter的热潮。\n\n为了更好的方便帮助中国开发者了解这门新技术" +
                "，我们，Flutter中文网，前后发起了Flutter翻译计划、Flutter开源计划，前者主要的任务是翻译" +
                "Flutter官方文档，后者则主要是开发一些常用的包来丰富Flutter生态，帮助开发者提高开发效率。而时" +
                "至今日，这两件事取得的效果还都不错！"
        );


    }


}
