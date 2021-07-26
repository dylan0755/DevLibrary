package com.dylan.library.media;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Build;
import android.support.annotation.RequiresApi;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2021/07/26
 * Desc: 媒体文件转换工具
 */
public class MediaConverter {




    /**
     *
     * @param pcmInPath
     * @param sampleRate  16000
     * @param channel AudioFormat.CHANNEL_IN_MONO;
     * @param encoding AudioFormat.ENCODING_PCM_16BIT;
     * @param wavOutPath
     * @throws Exception
     */
    public static void pcmToWav(String pcmInPath, int sampleRate, int channel, int encoding, String wavOutPath) throws Exception {
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channel, encoding);
        FileInputStream in;
        FileOutputStream out;
        long totalAudioLen;
        long totalDataLen;
        long longSampleRate = sampleRate;
        int channels = channel == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
        long byteRate = 16 * sampleRate * channels / 8;
        byte[] data = new byte[bufferSize];
        in = new FileInputStream(pcmInPath);
        out = new FileOutputStream(wavOutPath);
        totalAudioLen = in.getChannel().size();
        totalDataLen = totalAudioLen + 36;

        writeWaveFileHeader(out, totalAudioLen, totalDataLen,
                longSampleRate, channels, byteRate);
        while (in.read(data) != -1) {
            out.write(data);
        }
        in.close();
        out.close();
    }


    /**
     * 加入wav文件头
     */
    private static void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        // RIFF/WAVE header
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        //WAVE
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        // 'fmt ' chunk
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        // 4 bytes: size of 'fmt ' chunk
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // format = 1
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // block align
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0;
        // bits per sample
        header[34] = 16;
        header[35] = 0;
        //data
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }



    /**
     * meger多个wav
     * @param wavFiles  多个wav
     * @param output  要生成的wav
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void mergeWav(List<File> wavFiles, String output) throws IOException {
        if (wavFiles.size() < 1) {
            return;
        }
        try(FileInputStream fis = new FileInputStream(wavFiles.get(0));
            FileOutputStream fos = new FileOutputStream(new File(output))){
            byte[] buffer = new byte[1024 * 4];
            int total = 0;
            int count;
            while ((count = fis.read(buffer)) > -1) {
                fos.write(buffer, 0, count);
                total += count;
            }
            fis.close();
            for (int i = 1; i < wavFiles.size(); i++) {
                File file = wavFiles.get(i);
                try(FileInputStream fisH = new FileInputStream(file)){
                    Header header = resolveHeader(fisH);
                    FileInputStream dataInputStream = header.dataInputStream;
                    while ((count = dataInputStream.read(buffer)) > -1) {
                        fos.write(buffer, 0, count);
                        total += count;
                    }
                }
            }
            fos.flush();
            fos.close();

            FileInputStream fisHo = new FileInputStream(new File(output));
            Header outputHeader = resolveHeader(fisHo);
            outputHeader.dataInputStream.close();
            try(RandomAccessFile res = new RandomAccessFile(output, "rw")){
                res.seek(4);
                byte[] fileLen = intToByteArray(total + outputHeader.dataOffset - 8);
                res.write(fileLen, 0, 4);
                res.seek(outputHeader.dataSizeOffset);
                byte[] dataLen = intToByteArray(total);
                res.write(dataLen, 0, 4);
            }
        }
    }

    private static Header resolveHeader(FileInputStream fis) throws IOException {
        byte[] byte4 = new byte[4];
        byte[] buffer = new byte[2048];
        int readCount = 0;
        Header header = new Header();
        fis.read(byte4);// RIFF
        fis.read(byte4);
        readCount += 8;
        header.fileSizeOffset = 4;
        header.fileSize = byteArrayToInt(byte4);
        fis.read(byte4);// WAVE
        fis.read(byte4);// fmt
        fis.read(byte4);
        readCount += 12;
        int fmtLen = byteArrayToInt(byte4);
        fis.read(buffer, 0, fmtLen);
        readCount += fmtLen;
        fis.read(byte4);// data or fact
        readCount += 4;
        if (isFmt(byte4, 0)) {// 包含fmt段
            fis.read(byte4);
            int factLen = byteArrayToInt(byte4);
            fis.read(buffer, 0, factLen);
            fis.read(byte4);// data
            readCount += 8 + factLen;
        }
        fis.read(byte4);// data size
        int dataLen = byteArrayToInt(byte4);
        header.dataSize = dataLen;
        header.dataSizeOffset = readCount;
        readCount += 4;
        header.dataOffset = readCount;
        header.dataInputStream = fis;
        return header;
    }

    /**
     * 将int转化为byte[]
     */
    private static byte[] intToByteArray(int data) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array();
    }

    private static boolean isFmt(byte[] bytes, int start) {
        if (bytes[start + 0] == 'f' && bytes[start + 1] == 'm' && bytes[start + 2] == 't' && bytes[start + 3] == ' ') {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将byte[]转化为int
     */
    private static int byteArrayToInt(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /**
     * 头部部分信息
     */
    static class Header {
        public int fileSize;
        public int fileSizeOffset;
        public int dataSize;
        public int dataSizeOffset;
        public int dataOffset;
        public FileInputStream dataInputStream;
    }

}
