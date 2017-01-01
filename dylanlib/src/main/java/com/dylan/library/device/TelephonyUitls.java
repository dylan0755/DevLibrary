package com.dylan.library.device;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dylan on 2017/1/2.
 */

public class TelephonyUitls {

    private TelephonyUitls() {

    }

    /**
     * 获取当前设置的电话号码
     */
    public static PhoneInfo getPhoneInfo(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //手机号码未必获取得到，因为运营商不会把手机号码写入sim卡
        String phonenumber = telephonyManager.getLine1Number();
        String provideName = getProvidersName(telephonyManager);
        PhoneInfo phoneInfo = new PhoneInfo();
        phoneInfo.setPhoneNumber(phonenumber);
        phoneInfo.setProviderName(provideName);
        return phoneInfo;
    }

    /**
     * 需要加入权限<uses-permission
     * android:name="android.permission.READ_PHONE_STATE"/> <BR>
     * <p>
     * IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
     */
    public static String getProvidersName(TelephonyManager telephonyManager) {
        String ProvidersName = null;
        // 返回唯一的用户ID;就是这张卡的编号神马的
        String IMSI = telephonyManager.getSubscriberId();

        System.out.println(IMSI);
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            ProvidersName = "中国移动";
        } else if (IMSI.startsWith("46001")) {
            ProvidersName = "中国联通";
        } else if (IMSI.startsWith("46003")) {
            ProvidersName = "中国电信";
        }
        return ProvidersName;
    }

    public static String getProviderByPhoneNumber(String phoneNumber){
        return getProviderByPhoneNumber(phoneNumber,false);
    }
    public static String getProviderByPhoneNumber(String phoneNumber,boolean showErro) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return "";
        if (!checkPhone(phoneNumber)){
            if (showErro){
                return "该号码不是一个有效的手机号码";
            }
            return "";
        }
        /**
         * 中国移动：China Mobile
         * 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
         */
        String cm_model = "^1(3[4-9]|4[7]|5[0-27-9]|7[08]|8[2-478])\\d{8}$";
        /**
         * 中国联通：China Unicom
         * 130,131,132,145,155,156,170,171,175,176,185,186
         */
        String cu_model = "^1(3[0-2]|4[5]|5[56]|7[0156]|8[56])\\d{8}$";
        /**
         * 中国电信：China Telecom
         * 133,149,153,170,173,177,180,181,189
         */
        String telecom_model = "^1(3[3]|4[9]|53|7[037]|8[019])\\d{8}$";

        Pattern pattern_cm = Pattern.compile(cm_model);
        Pattern pattern_cu = Pattern.compile(cu_model);
        Pattern pattern_tele = Pattern.compile(telecom_model);
        boolean flag1 = pattern_cm.matcher(phoneNumber).matches();
        boolean flag2 = pattern_cu.matcher(phoneNumber).matches();
        boolean flag3 = pattern_tele.matcher(phoneNumber).matches();
        if (flag1) return "中国移动";
        if (flag2) return "中国联通";
        if (flag3) return "中国电信";

        if (showErro)return "无法识别该手机号";
        else return "";

    }


    public static boolean checkPhone(String phone){
        String reg = "^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(phone);
        boolean flag = matcher.matches();
        if (flag) {//验证通过
            return true;
        } else {
            return false;
        }
    }
}
