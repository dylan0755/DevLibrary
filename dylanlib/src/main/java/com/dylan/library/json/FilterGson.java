package com.dylan.library.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Dylan on 2018/6/30.
 */

public class FilterGson {

    public static Gson buildGson() {
          return   new GsonBuilder()
                    .registerTypeAdapter(Integer.class, new IntegerFilterAdapter())
                    .registerTypeAdapter(int.class, new IntegerFilterAdapter())
                    .registerTypeAdapter(Double.class, new DoubleFilterAdapter())
                    .registerTypeAdapter(double.class, new DoubleFilterAdapter())
                    .registerTypeAdapter(Long.class, new LongFilterAdapter())
                    .registerTypeAdapter(long.class, new LongFilterAdapter())
                    .create();
    }


}
