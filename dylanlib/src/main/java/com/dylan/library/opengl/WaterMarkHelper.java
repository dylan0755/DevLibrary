package com.dylan.library.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Author: Dylan
 * Date: 2021/1/24
 * Desc:
 */

public class WaterMarkHelper {
    public static final String TAG = WaterMarkHelper.class.getSimpleName();
    private HashMap<Bitmap, Integer> textIdMap;
    private HashMap<String, Bitmap> textBitmapMap;
    private WaterMarkTextureDrawer waterDrawer;

    private SimpleDateFormat dateFormat;

    public WaterMarkHelper() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void setSimpleDateFormat(SimpleDateFormat simpleDateFormat) {
        dateFormat = simpleDateFormat;
    }

    public void initConfig() {
        waterDrawer = new WaterMarkTextureDrawer();
        textIdMap = new HashMap<>();
        textBitmapMap = new HashMap<>();
    }

    public void drawFrame(WaterBean waterBean) {
        if (waterDrawer == null) {
            Log.e(TAG, "please call initConfig method before drawFrame");
            return;
        }
        GLES20.glViewport(waterBean.getX(), waterBean.getY(), waterBean.getBitmapWidth(), waterBean.getBitmapHeight());
        waterDrawer.drawFrame(waterBean.getTextId());
    }


    //绘制静态水印
    public void drawFrame(int x, int y, int width, int height, int textId) {
        if (waterDrawer == null) {
            Log.e(TAG, "please call initConfig method before drawFrame");
            return;
        }
        GLES20.glViewport(x, y, width, height);
        waterDrawer.drawFrame(textId);
    }

    public void drawDateTimeText(WaterDateBean dateBean) {
        drawDateTimeText(dateBean.getX(), dateBean.getY(), dateBean.getColor(), dateBean.getTextSize());
    }

    public void drawDateTimeText(int x, int y, int color, int textSize) {
        if (waterDrawer == null || textBitmapMap == null || textIdMap == null) {
            Log.e(TAG, "please call initConfig method before drawFrame");
            return;
        }
        String time = dateFormat.format(new Date());
        int marginLeft = 0;
        for (int i = 0; i < time.length(); i++) {
            String timeChar = "" + time.charAt(i);
            Bitmap bitmap = textBitmapMap.get(timeChar);
            if (bitmap == null) {
                bitmap = BitmapHelper.getBitmapFromText(timeChar, color, textSize);
                textBitmapMap.put(timeChar, bitmap);
            }
            if (bitmap != null) {
                GLES20.glViewport(x + marginLeft, y, bitmap.getWidth(), bitmap.getHeight());
                marginLeft += bitmap.getWidth();
                int textId = createTexture(bitmap);
                if (textId != 0) {
                    waterDrawer.drawFrame(textId);
                }
            }
        }

    }


    private int createTexture(Bitmap bitmap) {
        if (textIdMap.get(bitmap) == null) {
            int[] textures = new int[1];
            //生成纹理
            GLES20.glGenTextures(1, textures, 0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            textIdMap.put(bitmap, textures[0]);
        }
        return textIdMap.get(bitmap);
    }


    public void releaseTextureId(WaterBean waterBean){
        if (waterBean!=null&&waterBean.isValid()){
            GlUtils.deleteTextures(new int[]{waterBean.getTextId()});
        }

    }


    public void releaseTextureId(int textId) {
        if (textId != 0) {
            GlUtils.deleteTextures(new int[]{textId});
        }

        //释放纹理
        if (textIdMap != null) {
            if (!textIdMap.isEmpty()) {
                for (int textureId : textIdMap.values()) {
                    GlUtils.deleteTextures(new int[]{textureId});
                }
            }
            textIdMap.clear();
            textIdMap = null;
        }
        //释放Bitmap
        if (textBitmapMap != null) {
            if (!textBitmapMap.isEmpty()) {
                for (Bitmap bitmap : textBitmapMap.values()) {
                    bitmap.recycle();
                }
            }
            textBitmapMap.clear();
            textBitmapMap = null;
        }
        if (waterDrawer != null) {
            waterDrawer.release();
            waterDrawer = null;
        }
    }


    //日期水印
    public static class WaterDateBean {
        private int x;
        private int y;
        private int color;
        private int textSize;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public int getTextSize() {
            return textSize;
        }

        public void setTextSize(int textSize) {
            this.textSize = textSize;
        }
    }

    //静态水印
    public static class WaterBean {
        private int textId;
        private int x;
        private int y;
        private Bitmap bitmap;

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            if (bitmap!=null){
                textId = GlUtils.createImageTexture(bitmap);
            }

        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getBitmapWidth() {
            return bitmap!=null?bitmap.getWidth():0;
        }


        public int getBitmapHeight() {
            return bitmap!=null?bitmap.getHeight():0;
        }


        public boolean isValid(){
           return textId!=0;
        }

        public int getTextId() {
            return textId;
        }


    }

}
