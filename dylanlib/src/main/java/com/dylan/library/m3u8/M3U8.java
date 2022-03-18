package com.dylan.library.m3u8;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2022/3/19
 * Desc:
 */

public class M3U8 {
    private double totalDuration;
    private List<M3U8Ts> tsList = new ArrayList<M3U8Ts>();


    public double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public List<M3U8Ts> getTsList() {
        return tsList;
    }

    public void setTsList(List<M3U8Ts> tsList) {
        this.tsList = tsList;
    }

    public void addTs(M3U8Ts ts) {
        this.tsList.add(ts);
    }



    public static class M3U8Ts {
        private String tsUrl;
        private double seconds;

        public M3U8Ts(String tsUrl, double seconds) {
            this.tsUrl = tsUrl;
            this.seconds = seconds;
        }

        public String getTsUrl() {
            return tsUrl;
        }

        public void setTsUrl(String tsUrl) {
            this.tsUrl = tsUrl;
        }

        public double getSeconds() {
            return seconds;
        }

        public void setSeconds(double seconds) {
            this.seconds = seconds;
        }

        @Override
        public String toString() {
            return tsUrl + " (" + seconds + "sec)";
        }
    }
}
