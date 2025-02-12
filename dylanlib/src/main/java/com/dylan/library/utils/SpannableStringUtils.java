package com.dylan.library.utils;

import android.graphics.Color;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.dylan.library.widget.ClickableLinkMovementMethod;
import com.dylan.library.widget.ClickableSpanTextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Author: Dylan
 * Date: 2019/8/10
 * Desc:
 */
public class SpannableStringUtils {

    //第一行开头留空
    public static SpannableString firstLineMarginLeft(int leftMargin, String string) {
        SpannableString spannableString = new SpannableString(string);
        LeadingMarginSpan.Standard marginSpan = new LeadingMarginSpan.Standard(leftMargin, 0);
        spannableString.setSpan(marginSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    public static SpannableString getTintSpannableString(String fullText, String tintKeyWord, int colorValue) {
        if (fullText == null) fullText = "";
        if (tintKeyWord == null) tintKeyWord = "";
        int start = fullText.indexOf(tintKeyWord);
        int end = start + tintKeyWord.length();
        SpannableString spannableString = new SpannableString(fullText);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorValue);
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }


    public static SpannableString getTintSpannableString(String fullText, int start, int end, int colorValue) {
        SpannableString spannableString = new SpannableString(fullText);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorValue);
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }


    public static SpannableString getTintSpannableString(SpannableString spannableString, int start, int end, int colorValue) {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorValue);
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    public static SpannableString getGradientTintSpannableString(String text, int startColor, int endColor, GradientSpan.Orient orient){
        SpannableString spannableString = new SpannableString(text);
        GradientSpan gradientSpan = new GradientSpan(startColor, endColor,orient);
        spannableString.setSpan(gradientSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }


    public static SpannableString getBlodStyleSpannableString(String fullText, int start, int end) {
        SpannableString spannableString = new SpannableString(fullText);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    public static SpannableString getBlodStyleSpannableString(SpannableString spannableString, int start, int end) {
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    public static SpannableString getAbsoluteSizeSpannableString(String fullText, int dip, int start, int end) {
        SpannableString spannableString = new SpannableString(fullText);
        spannableString.setSpan(new AbsoluteSizeSpan(dip, true), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    public static SpannableString getAbsoluteSizeSpannableString(SpannableString spannableString, int dip, int start, int end) {
        spannableString.setSpan(new AbsoluteSizeSpan(dip, true), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }


    public static SpannableString getTintResizeSpannableString(String fullText, int dip, int start, int end, int colorValue) {
        SpannableString spannableString = new SpannableString(fullText);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorValue);
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString = getAbsoluteSizeSpannableString(spannableString, dip, start, end);
        return spannableString;
    }
    public static SpannableString getTintResizeSpannableString(String fullText,int dp,String keyWord1,int colorValue1,String keyWord2,int colorValue2){
        SpannableString spannableString = new SpannableString(fullText);
        try {
            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(colorValue1);
            ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(colorValue2);
            int start1=fullText.indexOf(keyWord1);
            int end1=start1+keyWord1.length();
            int start2=fullText.indexOf(keyWord2);
            int end2=start2+keyWord2.length();
            spannableString.setSpan(new AbsoluteSizeSpan(dp, true), start1, end1, 18);
            spannableString.setSpan(new AbsoluteSizeSpan(dp, true), start2, end2, 18);
            spannableString.setSpan(colorSpan1, start1, end1, 18);
            spannableString.setSpan(colorSpan2, start2, end2, 18);
            spannableString.setSpan(spannableString, start2, end2, 18);
        }catch (Exception e){
            e.printStackTrace();
        }
        return spannableString;
    }

    public static SpannableString getTintResizeSpannableString(String fullText, String tintKeyWord, int dp, int tintColor) {
        if (fullText == null) fullText = "";
        if (tintKeyWord == null) tintKeyWord = "";
        int start = fullText.indexOf(tintKeyWord);
        int end = start + tintKeyWord.length();
        SpannableString spannableString = new SpannableString(fullText);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(tintColor);
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return getAbsoluteSizeSpannableString(spannableString, dp, start, end);
    }


    public static SpannableString getTintResizeSpannableString(String fullText, String tintKeyWord, int dp, String colorString) {
        if (fullText == null) fullText = "";
        if (tintKeyWord == null) tintKeyWord = "";
        int start = fullText.indexOf(tintKeyWord);
        int end = start + tintKeyWord.length();
        int tintColor = Color.parseColor(colorString);
        SpannableString spannableString = new SpannableString(fullText);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(tintColor);
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return getAbsoluteSizeSpannableString(spannableString, dp, start, end);
    }

    //着色-大小-加粗
    public static SpannableString getTintResizeBoldSpannableString(String fullText, int dip, int start, int end, int colorValue) {
        SpannableString spannableString = new SpannableString(fullText);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorValue);
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString = getAbsoluteSizeSpannableString(spannableString, dip, start, end);
        spannableString = getBlodStyleSpannableString(spannableString, start, end);
        return spannableString;
    }


    public static SpannableString getUnderLineSpannableString(String fullText) {
        if (fullText == null) fullText = "";
        SpannableString spannableString = new SpannableString(fullText.trim());
        UnderlineSpan span = new UnderlineSpan();
        spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }


    public static SpannableString getUnderLineTintSpannableString(String fullText, int start, int end, int colorValue) {
        SpannableString spannableString = getUnderLineSpannableString(fullText);
        spannableString = getTintSpannableString(spannableString, start, end, colorValue);
        return spannableString;
    }

    public static SpannableString getUnderLineTintSpannableString(String fullText, String tintKeyWord, String colorString) {
        if (fullText == null) fullText = "";
        if (tintKeyWord == null) tintKeyWord = "";
        int colorValue = Color.parseColor(colorString);
        int start = fullText.indexOf(tintKeyWord);
        int end = start + tintKeyWord.length();
        SpannableString spannableString = getUnderLineSpannableString(fullText);
        spannableString = getTintSpannableString(spannableString, start, end, colorValue);
        return spannableString;
    }


    public static SpannableString setClickTintResizeSpanStr(TextView textView, String fullText, int dp, int start, int end, final int colorValue, final View.OnClickListener clickListener) {
        SpannableString spannableString = getClickTintResizeSpanStr(fullText, dp, start, end, colorValue, clickListener);
        textView.setText(spannableString);
        if (textView instanceof ClickableSpanTextView) {
            ((ClickableSpanTextView) textView).setLocalLinkMovementMethod(ClickableLinkMovementMethod.getInstance());
        } else {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        return spannableString;
    }


    public static SpannableString setClickTintResizeSpanStr(TextView textView, String fullText, String tintKeyWord, int dp, int colorValue, final View.OnClickListener clickListener) {
        SpannableString spannableString = getClickTintResizeSpanStr(fullText, tintKeyWord, dp, colorValue, clickListener);
        textView.setText(spannableString);
        if (textView instanceof ClickableSpanTextView) {
            ((ClickableSpanTextView) textView).setLocalLinkMovementMethod(ClickableLinkMovementMethod.getInstance());
        } else {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        return spannableString;
    }

    public static SpannableString setClickTintResizeSpanStr(TextView textView, String fullText, int dp, int start1, int end1, int start2, int end2, int colorValue1, final int colorValue2, final View.OnClickListener clickListener) {
        SpannableString spannableString = getClickTintResizeSpanStr(fullText, dp, start1, end1, start2, end2, colorValue1, colorValue2, clickListener);
        textView.setText(spannableString);
        if (textView instanceof ClickableSpanTextView) {
            ((ClickableSpanTextView) textView).setLocalLinkMovementMethod(ClickableLinkMovementMethod.getInstance());
        } else {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        return spannableString;
    }

    public static SpannableString setClickTintResizeSpanStr(TextView textView, String fullText, int dp, String keyWord1, String keyWord2, final int colorValue1, final int colorValue2, final View.OnClickListener clickListener1, final View.OnClickListener clickListener2) {
        SpannableString spannableString = getClickTintResizeSpanStr(fullText, dp, keyWord1, keyWord2, colorValue1, colorValue2, clickListener1, clickListener2);
        textView.setText(spannableString);
        if (textView instanceof ClickableSpanTextView) {
            ((ClickableSpanTextView) textView).setLocalLinkMovementMethod(ClickableLinkMovementMethod.getInstance());
        } else {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        return spannableString;
    }


    private static SpannableString getClickTintResizeSpanStr(String text, int dp, int start, int end, final int colorValue, final View.OnClickListener clickListener) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            public void onClick(View v) {
                Selection.removeSelection((Spannable) ((TextView) v).getText());
                if (clickListener != null) {
                    clickListener.onClick(v);
                }

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(colorValue);
                ds.setUnderlineText(false);//去掉下划线,否则 ForegroundColorSpan 失效

            }
        };

        text = text.concat(" ");
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorValue);
        //大小
        spannableString.setSpan(new AbsoluteSizeSpan(dp, true), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //颜色
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //点击
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    private static SpannableString getClickTintResizeSpanStr(String text, String tintKeyWord, int dp, final int colorValue, final View.OnClickListener clickListener) {
        if (text == null) text = "";
        if (tintKeyWord == null) tintKeyWord = "";
        int start = text.indexOf(tintKeyWord);
        int end = start + tintKeyWord.length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            public void onClick(View v) {
                Selection.removeSelection((Spannable) ((TextView) v).getText());
                if (clickListener != null) {
                    clickListener.onClick(v);
                }

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(colorValue);
                ds.setUnderlineText(false);//去掉下划线,否则 ForegroundColorSpan 失效

            }
        };

        text = text.concat(" ");
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorValue);
        //大小
        spannableString.setSpan(new AbsoluteSizeSpan(dp, true), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //颜色
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //点击
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    private static SpannableString getClickTintResizeSpanStr(String fullText, int dp, int start1, int end1, int start2, int end2, int colorValue1, final int colorValue2, final View.OnClickListener clickListener) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            public void onClick(View v) {
                Selection.removeSelection((Spannable) ((TextView) v).getText());
                if (clickListener != null) {
                    clickListener.onClick(v);
                }

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(colorValue2);
                ds.setUnderlineText(false);//去掉下划线,否则 ForegroundColorSpan 失效

            }
        };

        fullText = fullText.concat(" ");
        SpannableString spannableString = new SpannableString(fullText);
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(colorValue1);
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(colorValue2);
        //大小
        spannableString.setSpan(new AbsoluteSizeSpan(dp, true), start1, end1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(dp, true), start2, end2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //颜色
        spannableString.setSpan(colorSpan1, start1, end1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(colorSpan2, start2, end2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //点击
        spannableString.setSpan(clickableSpan, start2, end2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    private static SpannableString getClickTintResizeSpanStr(String fullText, int dp, String keyWord1, String keyWord2, final int colorValue1, final int colorValue2, final View.OnClickListener clickListener1, final View.OnClickListener clickListener2) {

        int start1 = fullText.indexOf(keyWord1);
        int end1 = start1 + keyWord1.length();

        int start2 = fullText.indexOf(keyWord2);
        int end2 = start2 + keyWord2.length();


        ClickableSpan clickableSpan1 = new ClickableSpan() {
            public void onClick(View v) {
                Selection.removeSelection((Spannable) ((TextView) v).getText());
                if (clickListener1 != null) {
                    clickListener1.onClick(v);
                }

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(colorValue1);
                ds.setUnderlineText(false);//去掉下划线,否则 ForegroundColorSpan 失效

            }
        };


        ClickableSpan clickableSpan2 = new ClickableSpan() {
            public void onClick(View v) {
                Selection.removeSelection((Spannable) ((TextView) v).getText());
                if (clickListener2 != null) {
                    clickListener2.onClick(v);
                }

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(colorValue2);
                ds.setUnderlineText(false);//去掉下划线,否则 ForegroundColorSpan 失效

            }
        };

        fullText = fullText.concat(" ");
        SpannableString spannableString = new SpannableString(fullText);
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(colorValue1);
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(colorValue2);
        //大小
        spannableString.setSpan(new AbsoluteSizeSpan(dp, true), start1, end1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(dp, true), start2, end2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //颜色
        spannableString.setSpan(colorSpan1, start1, end1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(colorSpan2, start2, end2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //点击
        spannableString.setSpan(clickableSpan1, start1, end1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(clickableSpan2, start2, end2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }


    public static SpannableString getStrikeThruText(String text) {
        if (text==null)return null;
        return getStrikeThruText(text,0,text.length());
    }

    public static SpannableString getStrikeThruText(String text, int start, int end) {
        if (text == null) return null;
        SpannableString spanStrikethrough = new SpannableString(text);
        StrikethroughSpan stSpan = new StrikethroughSpan();
        spanStrikethrough.setSpan(stSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spanStrikethrough;
    }


    public static SpannableStringBuilder findSpecWordAndHighLight(String srcText,String keyWord,int highLightColor){
        if (EmptyUtils.isEmpty(srcText))return new SpannableStringBuilder("");
        SpannableStringBuilder spannable = new SpannableStringBuilder(srcText);
        Pattern p = Pattern.compile(Pattern.quote(keyWord));//关键字
        Matcher m = p.matcher(srcText);//匹配关键字
        while (m.find()) {
            int start = m.start();
            if (start >= 0) {
                int end = start + keyWord.length();
                spannable.setSpan(new ForegroundColorSpan(highLightColor), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
            }
        }
        return spannable;
    }



    public static SpannableStringBuilder findSpecWordAndHighLight(String srcText, List<String> keyWords, int highLightColor){
        if (EmptyUtils.isEmpty(srcText))return new SpannableStringBuilder("");
        SpannableStringBuilder spannable = new SpannableStringBuilder(srcText);
        if (EmptyUtils.isNotEmpty(keyWords)) {
            for (String sensitiveContentWord : keyWords) {
                Pattern p = Pattern.compile(Pattern.quote(sensitiveContentWord));//关键字
                Matcher m = p.matcher(srcText);//匹配关键字
                while (m.find()) {
                    int start = m.start();
                    if (start >= 0) {
                        int end = start + sensitiveContentWord.length();
                        spannable.setSpan(new ForegroundColorSpan(highLightColor), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
                    }

                }
            }
        }

        return spannable;
    }

    public static SpannableStringBuilder findSpecWordAndHighLight(String srcText, List<HighLight> highLights){
        if (EmptyUtils.isEmpty(srcText))return new SpannableStringBuilder("");
        SpannableStringBuilder spannable = new SpannableStringBuilder(srcText);
        if (EmptyUtils.isNotEmpty(highLights)) {
            for (HighLight highLight : highLights) {
                String sensitiveContentWord=highLight.getKeyWord();
                Pattern p = Pattern.compile(Pattern.quote(sensitiveContentWord));//关键字
                Matcher m = p.matcher(srcText);//匹配关键字
                while (m.find()) {
                    int start = m.start();
                    if (start >= 0) {
                        int end = start + sensitiveContentWord.length();
                        spannable.setSpan(new ForegroundColorSpan(highLight.getHighLightColor()), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
                        if (highLight.getExtraCharacterStyle()!=null)spannable.setSpan(highLight.getExtraCharacterStyle(),start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }

                }
            }
        }

        return spannable;
    }

    public static class HighLight{
        private String keyWord;
        private int highLightColor;
        private CharacterStyle extraCharacterStyle;

        public String getKeyWord() {
            return keyWord;
        }

        public void setKeyWord(String keyWord) {
            this.keyWord = keyWord;
        }

        public int getHighLightColor() {
            return highLightColor;
        }

        public void setHighLightColor(int highLightColor) {
            this.highLightColor = highLightColor;
        }

        public CharacterStyle getExtraCharacterStyle() {
            return extraCharacterStyle;
        }

        public void setExtraCharacterStyle(CharacterStyle extraCharacterStyle) {
            this.extraCharacterStyle = extraCharacterStyle;
        }
    }

}
