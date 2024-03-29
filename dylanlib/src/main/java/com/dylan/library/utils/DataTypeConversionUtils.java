package com.dylan.library.utils;


import java.nio.ByteBuffer;

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


    public static short[] bytesToShorts(byte[] data) {
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

    public static byte[] toByteArray(double[] doubleArray) {
        int times = Double.SIZE / Byte.SIZE;
        byte[] bytes = new byte[doubleArray.length * times];
        for (int i = 0; i < doubleArray.length; i++) {
            ByteBuffer.wrap(bytes, i * times, times).putDouble(doubleArray[i]);
        }
        return bytes;
    }

    public static double[] toDoubleArray(byte[] byteArray) {
        int times = Double.SIZE / Byte.SIZE;
        double[] doubles = new double[byteArray.length / times];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = ByteBuffer.wrap(byteArray, i * times, times).getDouble();
        }
        return doubles;
    }

    public static byte[] toByteArray(int[] intArray) {
        int times = Integer.SIZE / Byte.SIZE;
        byte[] bytes = new byte[intArray.length * times];
        for (int i = 0; i < intArray.length; i++) {
            ByteBuffer.wrap(bytes, i * times, times).putInt(intArray[i]);
        }
        return bytes;
    }

    public static int[] toIntArray(byte[] byteArray) {
        int times = Integer.SIZE / Byte.SIZE;
        int[] ints = new int[byteArray.length / times];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = ByteBuffer.wrap(byteArray, i * times, times).getInt();
        }
        return ints;
    }

    public static float[] doubleArrayToFloatArray(double[] doubleArray){
        float[] floatArray = new float[doubleArray.length];

        for (int i = 0; i < doubleArray.length; i++) {
            floatArray[i] = (float) doubleArray[i];
        }

        return floatArray;
    }
}

