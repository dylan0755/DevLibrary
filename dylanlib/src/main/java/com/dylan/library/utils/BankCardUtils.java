package com.dylan.library.utils;

import android.content.Context;

import com.dylan.library.exception.ELog;
import com.dylan.library.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 *  校验银行卡号是否正确
 */

public class BankCardUtils {


    private static final String VALUE = "value";
    private static final String TEXT = "text";
    private JSONArray bankJsonArray;

    public BankCardUtils() {

    }




    public static boolean checkBankCardCode(String cardNo) {
        String nonCheckCodeCardId=cardNo.substring(0, cardNo.length() - 1);
        if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")||nonCheckCodeCardId.trim().length()<15
                ||nonCheckCodeCardId.trim().length()>18) {
            return false;
        }

        char bit = getBankCardCheckCode(nonCheckCodeCardId);
        return cardNo.charAt(cardNo.length() - 1) == bit;
    }




    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     * 该校验的过程：
     * 1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
     * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，则将其减去9），再求和。
     * 3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
     */
    private static char getBankCardCheckCode(String nonCheckCodeCardId){
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        // 执行luh算法
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if(j % 2 == 0) {  //偶数位处理
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
    }



    public static String getCardTypeText(String cardType) {
        if ("DC".equals(cardType)){
            return  "储蓄卡";
        }else if ("CC".equals(cardType)){
            return "信用卡";
        }else if ("SCC".equals(cardType)){
            return "准贷记卡";
        }else if ("PC".equals(cardType)){
            return "预付费卡";
        }else{
            return "";
        }
    }

    public static String getBankNameByValue(Context context, String bankValue) {
        if (bankValue == null || bankValue.isEmpty()) return bankValue;
        try {
            JSONArray bankJsonArray = new JSONArray(getBankSortListJsonText(context));
            int len = bankJsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject bankObject = bankJsonArray.optJSONObject(i);
                if (bankObject != null) {
                    if (bankValue.equals(bankObject.optString(VALUE))) {
                        return bankObject.optString(TEXT,"");
                    }
                }
            }
        } catch (JSONException e) {
            ELog.e(e);
        }
        return "";
    }


    public static void getCardInfo(final String cardNo, final ReponseCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardBinCheck=true&cardNo=" + cardNo);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(6000);
                    connection.setReadTimeout(6000);
                    connection.setRequestMethod("GET");
                    int reponseCode = connection.getResponseCode();
                    if (reponseCode == 200) {
                        String json = FileUtils.getStringFromInputStream(connection.getInputStream());
                        if (callBack != null) callBack.onSuccess(json);
                    } else {
                        String error = FileUtils.getStringFromInputStream(connection.getErrorStream());
                        if (callBack != null)
                            callBack.onError("reponseCode=" + reponseCode + " error=" + error);
                    }

                } catch (Exception e) {
                    if (callBack != null)
                        callBack.onError(e.getMessage());
                    ELog.e(e);
                }

            }
        }).start();
    }


    public interface ReponseCallBack {
        void onSuccess(Object object);

        void onError(String errorMsg);
    }


    public static String getBankSortListJsonText(Context context) {
        try {
            String jsonText = FileUtils.getStringFromInputStream(context.getAssets().open("bankSort.json"));
            return jsonText;
        } catch (Exception e) {
            e.printStackTrace();
            ELog.e(e);
        }
        return "";
    }





}
