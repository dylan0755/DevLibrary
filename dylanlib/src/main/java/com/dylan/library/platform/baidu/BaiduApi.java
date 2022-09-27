package com.dylan.library.platform.baidu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;

import java.io.File;
import java.net.URLEncoder;

/**
 * Author: Dylan
 * Date: 2022/9/27
 * Desc:
 */
public class BaiduApi {


    public static String ocrGeneral(String access_token, Bitmap bitmap) throws Exception {
        String bitmapBase64Str = BitmapHelper.bitmapToBase64(bitmap);
        if (EmptyUtils.isEmpty(bitmapBase64Str)) {
            return "bitmap str base64 empty";
        }
        String param = "image=" + URLEncoder.encode(bitmapBase64Str);
        String ocrUrl = "https://aip.baidubce.com/rest/2.0/ocr/v1/general";
        return BaiduHttpUtils.post(ocrUrl,access_token, param);
    }

    public static String ocrGeneralBasic(String access_token, Bitmap bitmap) throws Exception {
        String bitmapBase64Str = BitmapHelper.bitmapToBase64(bitmap);
        if (EmptyUtils.isEmpty(bitmapBase64Str)) {
            return "bitmap str base64 empty";
        }
        String param = "image=" + URLEncoder.encode(bitmapBase64Str);
        String ocrUrl = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        return BaiduHttpUtils.post(ocrUrl,access_token, param);
    }

    //人像抠图
    public static String bodySeg(String accessToken, File imgFile) throws Exception {
        if (EmptyUtils.isEmpty(accessToken)) {
            Logger.e("ACCESS_TOKEN EMPTY");
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        if (EmptyUtils.isEmpty(bitmap)){
            Logger.e("bitmap==null");
            return null;
        }
        String bitmapBase64Str = BitmapHelper.bitmapToBase64(bitmap);
        if (EmptyUtils.isEmpty(bitmapBase64Str)) {
            Logger.e("bitmap str base64 empty");
            return null;
        }
        Logger.e("上传图片....");
        String imageStr = URLEncoder.encode(bitmapBase64Str);
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_seg";
        String param = "image=" + imageStr;
        return BaiduHttpUtils.post(url, accessToken, param);
    }
}