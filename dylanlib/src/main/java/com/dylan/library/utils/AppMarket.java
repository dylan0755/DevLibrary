package com.dylan.library.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

/**
 * Author: Dylan
 * Date: 2021/10/30
 * Desc:
 */
public class AppMarket {

    public static final String MARKET_YINGYONGBAO="com.tencent.android.qqdownloader";//应用宝
    public static final String MARKET_HUAWEI="com.huawei.appmarket";//华为
    public static final String MARKET_XIAOMI="com.xiaomi.market";//小米
    public static final String MARKET_OPPO="com.oppo.market";//OPPO
    public static final String MARKET_OPPO_HEYTAP="com.heytap.market";//OPPO 软件商店
    public static final String MARKET_VIVO="com.bbk.appstore";//vivo
    public static final String MARKET_MEIZU="com.meizu.mstore";//魅族
    public static final String MARKET_LENOVO="com.lenovo.leos.appstore";//联想
    public static final String MARKET_ZTE="zte.com.market";//中兴
    public static final String MARKET_SAMSUNG="com.sec.android.app.samsungapps";//三星
    public static final String MARKET_COOLPAD="com.yulong.android.coolmart";//酷派
    public static final String MARKET_BAIDU="com.baidu.appsearch";//百度手机助手
    public static final String MARKET_QIHU="com.qihoo.appstore";//360手机助手
    public static final String MARKET_91="com.dragon.android.pandaspace";// 91手机助手
    public static final String MARKET_PP="com.pp.assistant";// PP手机助手
    public static final String MARKET_YINGYONGHUI="com.yingyonghui.market";//应用汇
    public static final String MARKET_SOUGOU="com.sogou.androidtool";// 搜狗应用市场
    public static final String MARKET_JINLI="com.gionee.aora.market";// 金立软件商店
    public static final String MARKET_ANDROID="com.hiapk.marketpho";//安卓市场
    public static final String MARKET_GO="cn.goapk.market";//GO 商店
    public static final String MARKET_KU="com.coolapk.market";//酷市场


    public String[] getMarketPacks(){
       return new String[]{MARKET_YINGYONGBAO,MARKET_HUAWEI,MARKET_XIAOMI,MARKET_OPPO,
               MARKET_OPPO_HEYTAP,MARKET_VIVO,MARKET_MEIZU,MARKET_LENOVO,MARKET_ZTE,
               MARKET_SAMSUNG,MARKET_COOLPAD,MARKET_BAIDU,MARKET_QIHU,MARKET_91,MARKET_PP,
               MARKET_YINGYONGHUI,MARKET_SOUGOU,MARKET_JINLI,MARKET_ANDROID,MARKET_GO,MARKET_KU};
    }




    public  Intent getMarketIntent(Context context){
            Uri uri = Uri.parse("market://details?id="+context.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            List<ResolveInfo> infos = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                infos = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
            }else{
                infos = context.getPackageManager().queryIntentActivities(intent, 0);
            }
            if (infos!=null){
                for (ResolveInfo info : infos) {
                    String packName = info.activityInfo.packageName;
                    if (StringUtils.containsOneStr(getMarketPacks(),packName)){
                        intent.setPackage(packName);
                        break;
                    }
                }
            }
            return intent;
    }


    public void startMarketActivity(Context context){
        try {
            context.startActivity(getMarketIntent(context));
        } catch (Exception e) {
            ToastUtils.show("您的手机没有安装Android应用市场");
            e.printStackTrace();
        }
    }
}
