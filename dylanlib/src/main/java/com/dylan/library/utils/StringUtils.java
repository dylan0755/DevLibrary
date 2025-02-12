package com.dylan.library.utils;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.dylan.library.exception.ELog;
import com.dylan.library.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dylan on 2016/6/2.
 */
public class StringUtils {
    private static String TAG = "StringUtils";

    private StringUtils() {

    }


    public static boolean containsEmoji(String source) {
        return EmojiFilter.containsEmoji(source);
    }


    public static String filterEmoji(String source, String replaceStr) {
        return EmojiFilter.filterEmoji(source,replaceStr);
    }



    public static String getFormatFileSize(long size) {
       return FileUtils.getFormatFileSize(size);
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


    public static boolean isNotBlank(String str){
        return EmptyUtils.isNotBlank(str);
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

    public static boolean startWithKeyWord(String originText, String[] keyWords) {
        return startWithKeyWord(originText,keyWords,null);
    }

    public static boolean startWithKeyWord(String originText, String[] keyWords,String excludeKeyword) {
        if (keyWords != null && keyWords.length != 0) {
            if (originText != null) {
                int i = 0;
                for (int len = keyWords.length; i < len; ++i) {
                    if (originText.startsWith(keyWords[i])) {
                        if (!keyWords[i].equals(excludeKeyword)){
                            return true;
                        }
                    }
                }
            }
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
    /**
     * 判断是否包含数字
     */
    public static boolean containsNumber(String str) {
        Pattern pattern = Pattern.compile(".*[0-9].*");
        return pattern.matcher(str).matches();
    }


    public static String extractNumber(String str) {
        String regEx = "[0-9]+([.]{1}[0-9]+){0,1}";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        String result="";
        while (m.find()) {
            result=m.group(0);
        }
        return result;
    }

    public static boolean isIntegerNumber(String str) {
        String regEx = "[0-9]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 判断是否为数字
     */
    public static boolean isNumber(String str) {
        return isNumber(str,false);
    }
    public static boolean isNumber(String str,boolean includeStarWithPlus) {
        if (EmptyUtils.isEmpty(str))return false;
        if (includeStarWithPlus){
            return str.matches("[+-]?[0-9]+(\\.[0-9]+)?");
        }else{
            return str.matches("[-]?[0-9]+(\\.[0-9]+)?");
        }

    }

    public static int chineseNumber2Int(String chineseNumber) {
        String aval = "零一二三四五六七八九";
        String bval = "十百千万亿";
        int[] bnum = {10, 100, 1000, 10000, 100000000};
        Integer num = 0;
        char[] arr = chineseNumber.toCharArray();
        int len = arr.length;
        Stack<Integer> stack = new Stack<Integer>();
        for (int i = 0; i < len; i++) {
            char s = arr[i];
            //跳过零
            if (s == '零') {
                continue;
            }
            //用下标找到对应数字
            int index = bval.indexOf(s);
            //如果不在bval中，即当前字符为数字，直接入栈
            if (index == -1) {
                stack.push(aval.indexOf(s));
            } else { //当前字符为单位。
                int tempsum = 0;
                int val = bnum[index];
                //如果栈为空则直接入栈
                if (stack.isEmpty()) {
                    stack.push(val);
                    continue;
                }
                //如果栈中有比val小的元素则出栈，累加，乘N，再入栈
                while (!stack.isEmpty() && stack.peek() < val) {
                    tempsum += stack.pop();
                }
                //判断是否经过乘法处理
                if (tempsum == 0) {
                    stack.push(val);
                } else {
                    stack.push(tempsum * val);
                }
            }
        }
        //计算最终的和
        while (!stack.isEmpty()) {
            num += stack.pop();
        }
        return num;
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

    public static String findSerialSameString(String s1, String s2) {
        if (s1.length() > s2.length()) {
            String temp = s1;
            s1 = s2;
            s2 = temp;
        }
        int n = s1.length();
        int index = 0;
        ok:
        for (; n > 0; n--) {
            for (int i = 0; i < s1.length() - n + 1; i++) {
                String s = s1.substring(i, i + n);
                if (s2.contains(s)) {
                    index = i;
                    break ok;
                }
            }
        }
        return s1.substring(index, index + n);
    }

    public static int findSameCharCount(String source, String target) {
        char[] sources = source.toCharArray();
        char[] targets = target.toCharArray();
        int sourceLen = sources.length;
        int targetLen = targets.length;
        int samesum = 0;
        int locatpre = 0;
        String sameStr = "";
        for (int i = 0; i < targetLen - 1; i++) {
            for (int j = 0; j < sourceLen - 1; j++) {
                if ((targets[i] == sources[j]) && (targets[i + 1] == sources[j + 1])) {
                    samesum++;
                    sameStr += targets[i];
                    if (j > locatpre) {
                        locatpre = j;
                    }
                    break;
                }
            }
        }
        return samesum;
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
    public static SpannableStringBuilder findSpecWordAndHighLight(String srcText, List<SpannableStringUtils.HighLight> keyWords){
        return SpannableStringUtils.findSpecWordAndHighLight(srcText,keyWords);
    }


    public static String bundleToString(Bundle bundle){
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
        }
        return sb.toString();

    }

    public static boolean isUrl(String str) {
        if (isEmpty(str))
            return false;
        str = str.trim();
        return str.matches("^(http|https)://.+");
    }

    public static String convertToDownloadSpeed(BigDecimal bigDecimal, int scale) {
        BigDecimal unit = new BigDecimal(1);
        BigDecimal kb = new BigDecimal(1 << 10);
        BigDecimal mb = new BigDecimal(1 << 10).multiply(kb);
        BigDecimal gb = new BigDecimal(1 << 10).multiply(mb);
        BigDecimal tb = new BigDecimal(1 << 10).multiply(gb);
        BigDecimal pb = new BigDecimal(1 << 10).multiply(tb);
        BigDecimal eb = new BigDecimal(1 << 10).multiply(pb);
        if (bigDecimal.divide(kb, scale, BigDecimal.ROUND_HALF_UP).compareTo(unit) < 0)
            return bigDecimal.divide(unit, scale, BigDecimal.ROUND_HALF_UP).toString() + " B";
        else if (bigDecimal.divide(mb, scale, BigDecimal.ROUND_HALF_UP).compareTo(unit) < 0)
            return bigDecimal.divide(kb, scale, BigDecimal.ROUND_HALF_UP).toString() + " KB";
        else if (bigDecimal.divide(gb, scale, BigDecimal.ROUND_HALF_UP).compareTo(unit) < 0)
            return bigDecimal.divide(mb, scale, BigDecimal.ROUND_HALF_UP).toString() + " MB";
        else if (bigDecimal.divide(tb, scale, BigDecimal.ROUND_HALF_UP).compareTo(unit) < 0)
            return bigDecimal.divide(gb, scale, BigDecimal.ROUND_HALF_UP).toString() + " GB";
        else if (bigDecimal.divide(pb, scale, BigDecimal.ROUND_HALF_UP).compareTo(unit) < 0)
            return bigDecimal.divide(tb, scale, BigDecimal.ROUND_HALF_UP).toString() + " TB";
        else if (bigDecimal.divide(eb, scale, BigDecimal.ROUND_HALF_UP).compareTo(unit) < 0)
            return bigDecimal.divide(pb, scale, BigDecimal.ROUND_HALF_UP).toString() + " PB";
        return bigDecimal.divide(eb, scale, BigDecimal.ROUND_HALF_UP).toString() + " EB";
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
