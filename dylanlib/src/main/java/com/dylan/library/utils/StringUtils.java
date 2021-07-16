package com.dylan.library.utils;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.dylan.library.exception.ELog;
import com.dylan.library.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dylan on 2016/6/2.
 */
public class StringUtils {
    private static String TAG = "StringUtils";

    private StringUtils() {

    }







    public static String formatFileSize(long size) {
       return FileUtils.formatFileSize(size);
    }

    /**
     * 用字符串表示金额的时候处理字符串为空和小数点问题
     */
    public static String  dealNumericValue(String string){
        if (string==null||string.isEmpty())string="0";
        return AmountUtils.rvZeroAndDot(string);
    }



    public static boolean isNotEmpty(String strObj) {
        if (strObj != null && !strObj.isEmpty()) {
            return true;
        } else if (strObj == null) {
            return false;
        } else {
            return false;
        }
    }


    /**
     * 这个方法会提示变量是null还是 empty
     */
    public static boolean isNotEmpty(String strObj, String variableName) {
        if (strObj != null && !strObj.isEmpty()) {
            return true;
        } else if (strObj == null) {
            Log.e(TAG, variableName + " == null");
            return false;
        } else {
            Log.e(TAG, variableName + " is Empty");
            return false;
        }
    }


    public static boolean isEmpty(String strObj) {
        return !isNotEmpty(strObj);
    }

    public static boolean isEmpty(String strObj, String variableName) {
        return !isNotEmpty(strObj, variableName);
    }


    public static String upperFirstLetter(String str) {
        if (isEmpty(str) || !Character.isLowerCase(str.charAt(0))) return str;
        String endStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        return endStr;
    }

    public static String lowerFirstLetter(String str) {
        if (isEmpty(str) || !Character.isUpperCase(str.charAt(0))) return str;
        String endStr = str.substring(0, 1).toLowerCase() + str.substring(1);
        return endStr;
    }


    public static String reverse(String s) {
        if (isEmpty(s)) return "";
        StringBuffer buff = new StringBuffer();
        buff.append(s);
        return buff.reverse().toString();
    }

    /**
     * @param args
     * @param separator 分隔符
     * @return
     */
    public static String append(String[] args, String separator) {
        if (args == null || args.length == 0) return "";
        if (separator == null) separator = "、";
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, size = args.length; i < size; i++) {
            String str = args[i];
            if (isNotEmpty(str)) {
                if (i != size - 1) buffer.append(str).append(separator);
                else buffer.append(str);
            }
        }

        return buffer.toString();
    }


    public static boolean containsOneStr(String[] array, String str) {
        if (array == null || array.length == 0) return false;
        if (str==null)return false;
        for (int i = 0, len = array.length; i < len; i++) {
            if (array[i].equals(str)) return true;
        }
        return false;
    }


    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    public static boolean isMD5(String msg) {
        int cnt = 0;
        for (int i=0; i<msg.length(); ++i) {
            switch (msg.charAt(i)) {
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                    ++ cnt;
                    if (32 <= cnt) return true;
                    break;
                case '/':
                    if ((i + 10) < msg.length()) {// "/storage/"
                        char ch1 = msg.charAt(i+1);
                        char ch2 = msg.charAt(i+8);
                        if ('/' == ch2 && ('s' == ch1 || 'S' == ch1)) return true;
                    }
                default:
                    cnt = 0;
                    break;
            }
        }
        return false;
    }


    /**
     * 判断是否包含中文，支持 。 ，中文字符
     */
    public static boolean containsChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }


    public static CharSequence splitColor(String str, int limitStart, int limitEnd, String splitColor) {
        if (str == null) return str;
        SpannableString span = null;
        try {
            span = new SpannableString(str);
            ForegroundColorSpan fspan = new ForegroundColorSpan(Color.parseColor(splitColor));
            span.setSpan(fspan, limitStart, limitEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } catch (Exception e) {

        }
        return span;
    }

    //全角转半角
    public static String toDBC(String input) {
        if (input == null) return "";
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }


    /**
     * 判断是否为数字
     */
    public static boolean isNumber(String str) {
        String pattern = "^[\\+\\-]?[\\d]+(\\.[\\d]+)?$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        return m.matches();
    }


    //支持全角空格去除
    public static String trim(String textContent) {
        if (textContent == null) return "";
        while (textContent.startsWith("　")) {//这里判断是不是全角空格
            textContent = textContent.substring(1).trim();
        }
        while (textContent.endsWith("　")) {
            textContent = textContent.substring(0, textContent.length() - 1).trim();
        }
        return textContent.trim();
    }

    /**
     * utf-8 转unicode
     *
     * @return String
     */
    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(myBuffer[i]);
            if (ub == Character.UnicodeBlock.BASIC_LATIN) {
                //英文及数字等
                sb.append(myBuffer[i]);

            } else if (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                //全角半角字符
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);

            } else {
                //汉字
                short s = (short) myBuffer[i];

                String hexS = Integer.toHexString(s);
                //如果s为负数,转换后会带有四个f,这里去掉四个f
                if (hexS.indexOf("ffff") > -1) {
                    hexS = hexS.substring(4, hexS.length());
                }
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * @return String
     */
    public static String unicodeToUtf8(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }


    /**
     * @return String
     */

    public static String gbKToUnicode(String str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char chr1 = (char) str.charAt(i);

            if (!isNeedConvert(chr1)) {
                result.append(chr1);
                continue;
            }

            result.append("\\u" + Integer.toHexString((int) chr1));
        }

        return result.toString();
    }

    public static boolean isNeedConvert(char para) {
        return ((para & (0x00FF)) != para);
    }

    /**
     * @return String
     */

    public static String unicodeToGBK(String dataStr) {
        int index = 0;
        StringBuffer buffer = new StringBuffer();

        int li_len = dataStr.length();
        while (index < li_len) {
            if (index >= li_len - 1
                    || !"\\u".equals(dataStr.substring(index, index + 2))) {
                buffer.append(dataStr.charAt(index));

                index++;
                continue;
            }

            String charStr = "";
            charStr = dataStr.substring(index + 2, index + 6);

            char letter = (char) Integer.parseInt(charStr, 16);

            buffer.append(letter);
            index += 6;
        }

        return buffer.toString();
    }


    /**
     * 替换为星号
     */
    public static String replaceWithStarSymbol(String text, int start,int end){
        if (EmptyUtils.isEmpty(text))return text;
        int len =text.length();
        if (start>len-1)return text;
        if (end>len-1) return text;


        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c= text.charAt(i);
            if (i>=start&i<=end){
                stringBuilder.append("*");
            }else{
                stringBuilder.append(c);
            }
        }

        return stringBuilder.toString();
    }

    public static SpannableStringBuilder findSpecWordAndHighLight(String srcText,String keyWord,int highLightColor){

        return SpannableStringUtils.findSpecWordAndHighLight(srcText,keyWord,highLightColor);
    }



    public static SpannableStringBuilder findSpecWordAndHighLight(String srcText, List<String> keyWords, int highLightColor){
        return SpannableStringUtils.findSpecWordAndHighLight(srcText,keyWords,highLightColor);
    }



    public static String bundleToString(Bundle bundle){
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
        }
        return sb.toString();

    }


    public static Object decodeJsonUrlEncodeValue(Object objJson) {
        try {
            if (objJson instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) objJson;
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        decodeJsonUrlEncodeValue(jsonArray.get(i));
                    }
                }
            } else if (objJson instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) objJson;
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = jsonObject.get(key);
                    if (value instanceof JSONArray ||value instanceof JSONObject) {
                        decodeJsonUrlEncodeValue(value);
                    } else {
                        if (UrlEncoderUtils.hasUrlEncoded(value.toString())){
                            String decodeString= URLDecoder.decode(value.toString());
                            jsonObject.put(key,decodeString);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            ELog.e(e);
        }
        return objJson;
    }


    public static Object forEachJSonValue(Object objJson,OnJSonValueForEachCallBack callBack) {
        try {
            if (objJson instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) objJson;
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        forEachJSonValue(jsonArray.get(i),callBack);
                    }
                }
            } else if (objJson instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) objJson;
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = jsonObject.get(key);
                    if (value instanceof JSONArray ||value instanceof JSONObject) {
                        forEachJSonValue(value,callBack);
                    } else {
                        callBack.onEach(jsonObject,key,value);
                    }
                }
            }
        } catch (JSONException e) {
            ELog.e(e);
        }
        return objJson;
    }


    public interface OnJSonValueForEachCallBack{
          void onEach(JSONObject jsonObject,String key,Object value);
    }


}
