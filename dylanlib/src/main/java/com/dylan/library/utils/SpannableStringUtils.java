package com.dylan.library.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;


/**
 * Author: Dylan
 * Date: 2019/8/10
 * Desc:
 */
public class SpannableStringUtils {

      public static SpannableString firstLineMarginLeft(int leftMargin, String string){
          SpannableString spannableString=new SpannableString(string);
          LeadingMarginSpan.Standard marginSpan=new LeadingMarginSpan.Standard(leftMargin,0);
          spannableString.setSpan(marginSpan,0,spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
          return spannableString;
      }





}
