package com.dylan.library.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;


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


    public static SpannableString tintText(String fullText, int start,int end,int colorValue) {
        SpannableString spannableString = new SpannableString(fullText);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorValue);
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    public static SpannableString tintText(String fullText, String innerText, int colorValue) {
        SpannableString spannableString = new SpannableString(fullText);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorValue);
        int start = fullText.indexOf(innerText);
        int end = start + innerText.length();
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }


    public static SpannableString convertBlodStyle(String text, int start, int end) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }
    public static SpannableString convertBlodStyle(SpannableString spannableString, int start, int end) {
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    public static SpannableString setAbsoluteSizeSpan(String text, int dip, int start, int end) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new AbsoluteSizeSpan(dip, true), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    public static SpannableString setAbsoluteSizeSpan(SpannableString spannableString, int dip, int start, int end) {
        spannableString.setSpan(new AbsoluteSizeSpan(dip, true), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }


}
