package com.dylan.library.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dylan on 2016/12/22.
 */

public class LoginRegUtil {



    public static boolean phonePassCheck(String phone, String pass){
          if (!checkPhone(phone)){
              return false;
          }

        if (!StringUtils.isValid(pass)) {
            ToastUtil.toToast("请输入密码");
            return false;
        }

        return true;
    }

    private static boolean checkPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            ToastUtil.toToast("请输入手机号");
            return false;
        }
        String reg = "^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(phone);
        boolean flag = matcher.matches();
        if (flag) {//验证通过
            return true;
        } else {
            ToastUtil.toToast("无效手机号");
            return false;
        }
    }





}
