package com.dylan.library.utils;


/**
 * Author: Dylan
 * Date: 2021/11/26
 * Desc:
 */
public class DataTypeConversionUtils {


    public static byte[] shortsToBytes(short[] data) {
        byte[] buffer = new byte[data.length * 2];
        int shortIndex, byteIndex;
        shortIndex = byteIndex = 0;
        for (; shortIndex != data.length; ) {
            buffer[byteIndex] = (byte) (data[shortIndex] & 0x00FF);
            buffer[byteIndex + 1] = (byte) ((data[shortIndex] & 0xFF00) >> 8);
            ++shortIndex;
            byteIndex += 2;
        }
        return buffer;
    }




    public static short[] bytesToShorts(byte[] data){
        if (data == null) return new short[]{};
        int newDataLength = data.length / 2;
        if (data.length % 2 == 1) {
            newDataLength += 1;
        }
        //此处是将字节数据转换为short数据
        short[] newData = new short[newDataLength];
        for (int i = 0; i < newDataLength; i++) {
            byte low = 0;
            byte high = 0;
            if (2 * i < data.length) {
                low = data[2 * i];
            }
            if ((2 * i + 1) < data.length) {
                high = data[2 * i + 1];
            }
            newData[i] = (short) (((high << 8) & 0xff00) | (low & 0x00ff));
        }
        return newData;
    }
}
