package com.dylan.library.utils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Author: Dylan
 * Date: 2021/3/19
 * Desc:
 */

class MapUtils {


    public static void sortMapByASCII(Map<String, ?> paraMap){
        List<Map.Entry<String, ?>> infoIds = new ArrayList<Map.Entry<String, ?>>(paraMap.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        Collections.sort(infoIds, new Comparator<Map.Entry<String, ?>>() {
            @Override
            public int compare(Map.Entry<String, ?> o1, Map.Entry<String, ?> o2) {
                return (o1.getKey()).compareTo(o2.getKey());
            }
        });
    }


    public static String sortAndJointMapValue(Map<String,String> map){
        return sortAndJointMapValue(map,false);
    }

    public static String sortAndJointMapValue(Map<String, String> paraMap, boolean urlEncode) {
        String buff = "";
        try {
            sortMapByASCII(paraMap);
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : paraMap.entrySet()) {
                if (EmptyUtils.isNotBlank(item.getKey())) {
                    String val = item.getValue();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                        val = val.replace("+", "%20");
                        val=val.replace("%3A", ":");
                    }
                    buf.append(val).append("&");
                }

            }
            buff = buf.toString();
            if (!buff.isEmpty()) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }





}
