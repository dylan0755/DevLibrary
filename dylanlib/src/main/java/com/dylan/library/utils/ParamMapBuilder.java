package com.dylan.library.utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Dylan
 * Date: 2020/10/29
 * Desc:
 */
public class ParamMapBuilder {
    private static Builder builder;

    public static Builder put(String key, Object value) {
        if (builder == null) {
            builder = new Builder();
        }
        builder.put(key, value);
        return builder;
    }


    public static Map<String, Object> buildDefaultParm() {
        if (builder == null) {
            builder = new Builder();
        }
        return builder.build();
    }

    public static class Builder {
        private Map<String, Object> paramMap;

        public Builder() {
            paramMap = new HashMap<>();
        }

        public Builder put(String key, Object value) {
            if (key==null||key.isEmpty()) return this;
            paramMap.put(key, value);
            return this;
        }

        public Map<String, Object> build() {
            Map<String,Object> map=new HashMap<>();
            map.putAll(paramMap);
            paramMap.clear();
            return map;
        }


        public String buildJSONString(){
            JSONObject jsonObject=new JSONObject(paramMap);
            paramMap.clear();
            return jsonObject.toString();
        }
        
        
    }
}
