package com.dylan.library.opengl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

/**
 * Author: Dylan
 * Date: 2021/1/8
 * Desc:
 */

public class GlUtils {
    public static final String TAG = GlUtils.class.getSimpleName();
    private static final int SIZEOF_FLOAT = 4;

    public static final float[] IDENTITY_MATRIX;

    static {
        IDENTITY_MATRIX = new float[16];
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
    }


    //通过传入图片宽高和预览宽高，计算变换矩阵，得到的变换矩阵是预览类似ImageView的centerCrop效果
    public static float[] getShowMatrix(int imgWidth,int imgHeight,int viewWidth,int viewHeight){
        float[] projection=new float[16];
        float[] camera=new float[16];
        float[] matrix=new float[16];

        float sWhView=(float)viewWidth/viewHeight;
        float sWhImg=(float)imgWidth/imgHeight;
        if(sWhImg>sWhView){
            Matrix.orthoM(projection,0,-sWhView/sWhImg,sWhView/sWhImg,-1,1,1,3);
        }else{
            Matrix.orthoM(projection,0,-1,1,-sWhImg/sWhView,sWhImg/sWhView,1,3);
        }
        Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
        Matrix.multiplyMM(matrix,0,projection,0,camera,0);
        return matrix;
    }

    //旋转
    public static float[] rotate(float[] m,float angle){
        Matrix.rotateM(m,0,angle,0,0,1);
        return m;
    }

    //镜像   Gl2Utils.flip(matrix,true,false);
    public static float[] flip(float[] m,boolean x,boolean y){
        if(x||y){
            Matrix.scaleM(m,0,x?-1:1,y?-1:1,1);
        }
        return m;
    }

    public static float[] frontCameraMirror(float[] m){
        Matrix.scaleM(m,0,-1,1,1);
        return m;
    }


    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (program == 0) {
            Log.e(TAG, "Could not create program");
        }
        GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(program, pixelShader);
        checkGlError("glAttachShader");
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    /**
     * Checks to see if a GLES error has been raised.
     */
    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
        }
    }


    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        checkGlError("glCreateShader type=" + shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + shaderType + ":");
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    /**
     * Checks to see if the location we obtained is valid.  GLES returns -1 if a label
     * could not be found, but does not set the GL error.
     * <p>
     * Throws a RuntimeException if the location is invalid.
     */
    public static void checkLocation(int location, String label) {
        if (location < 0) {
            Log.e(TAG, "Unable to locate '" + label + "' in program");
        }
    }

    //图片数据转纹理
    public static int createImageTexture(ByteBuffer data, int width, int height, int format) {
        int[] textureHandles = new int[1];
        int textureHandle;

        GLES20.glGenTextures(1, textureHandles, 0);
        textureHandle = textureHandles[0];
        checkGlError("glGenTextures");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        checkGlError("loadImageTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, /*level*/ 0, format,
                width, height, /*border*/ 0, format, GLES20.GL_UNSIGNED_BYTE, data);
        checkGlError("loadImageTexture");
        return textureHandle;
    }

    //图片转纹理
    public static int createImageTexture(Bitmap bmp) {
        int[] textureHandles = new int[1];
        int textureHandle;

        GLES20.glGenTextures(1, textureHandles, 0);
        textureHandle = textureHandles[0];
        checkGlError("glGenTextures");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        checkGlError("loadImageTexture");
        android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, /*level*/ 0, bmp, 0);
        checkGlError("loadImageTexture");
        return textureHandle;
    }

    //图片纹理转Bitmap
    public void createBitmapFromTexture(int texId, float[] texMatrix, float[] mvpMatrix, final int texWidth, final int texHeight, final OnReadBitmapListener listener){
        createBitmapFromTexture(texId,texMatrix,mvpMatrix,texWidth,texHeight,listener,false);
    }

    //图片纹理转Bitmap
    public void createBitmapFromTexture(int texId, float[] texMatrix, float[] mvpMatrix, final int texWidth, final int texHeight, final OnReadBitmapListener listener, boolean isOes){
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texWidth, texHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        int[] frameBuffers = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffers, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textures[0], 0);
        int[] viewport = new int[4];
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0);
        GLES20.glViewport(0, 0, texWidth, texHeight);
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Program program;
        if (isOes) {
            program = new ProgramTextureOES();
        } else {
            program = new ProgramTexture2dWithAlpha();
        }
        program.drawFrame(texId, texMatrix, mvpMatrix);
        program.release();

        final ByteBuffer buffer = ByteBuffer.allocateDirect(texWidth * texHeight * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        GLES20.glFinish();
        GLES20.glReadPixels(0, 0, texWidth, texHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buffer);
        checkGlError("glReadPixels");
        buffer.rewind();
        GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_NONE);
        GLES20.glDeleteTextures(1, textures, 0);
        GLES20.glDeleteFramebuffers(1, frameBuffers, 0);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = Bitmap.createBitmap(texWidth, texHeight, Bitmap.Config.ARGB_8888);
                bmp.copyPixelsFromBuffer(buffer);
                android.graphics.Matrix matrix = new android.graphics.Matrix();
                matrix.preScale(1f, -1f);
                Bitmap finalBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
                bmp.recycle();
                if (listener != null) {
                    listener.onReadBitmapListener(finalBmp);
                }
            }
        });

    }

    /**
     * Allocates a direct float buffer, and populates it with the float array data.
     */
    public static FloatBuffer createFloatBuffer(float[] coords) {
        // Allocate a direct ByteBuffer, using 4 bytes per float, and copy coords into it.
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * SIZEOF_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(coords);
        fb.position(0);
        return fb;
    }

    /**
     * Writes GL version info to the log.
     */
    public static void logVersionInfo() {
        Log.i(TAG, "vendor  : " + GLES20.glGetString(GLES20.GL_VENDOR));
        Log.i(TAG, "renderer: " + GLES20.glGetString(GLES20.GL_RENDERER));
        Log.i(TAG, "version : " + GLES20.glGetString(GLES20.GL_VERSION));

        int[] values = new int[1];
        GLES30.glGetIntegerv(GLES30.GL_MAJOR_VERSION, values, 0);
        int majorVersion = values[0];
        GLES30.glGetIntegerv(GLES30.GL_MINOR_VERSION, values, 0);
        int minorVersion = values[0];
        if (GLES30.glGetError() == GLES30.GL_NO_ERROR) {
            Log.i(TAG, "glVersion: " + majorVersion + "." + minorVersion);
        }
    }

    /**
     * 获取 OpengGL 主版本号，在 GL 线程调用
     *
     * @return
     */
    public static int getGlMajorVersion() {
        int[] values = new int[1];
        GLES30.glGetIntegerv(GLES30.GL_MAJOR_VERSION, values, 0);
        int majorVersion = values[0];
        return majorVersion;
    }

    /**
     * Creates a texture object suitable for use with this program.
     * <p>
     * On exit, the texture will be bound.
     */
    public static int createTextureObject(int textureTarget) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        checkGlError("glGenTextures");

        int texId = textures[0];
        GLES20.glBindTexture(textureTarget, texId);
       checkGlError("glBindTexture " + texId);

        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        checkGlError("glTexParameter");

        return texId;
    }

    public static void deleteTextures(int[] textureId) {
        if (textureId != null && textureId.length > 0) {
            GLES20.glDeleteTextures(textureId.length, textureId, 0);
        }
    }

    public static void createFrameBuffers(int[] fboTex, int[] fboId, int width, int height) {
//generate fbo id
        GLES20.glGenFramebuffers(fboId.length, fboId, 0);
//generate texture
        GLES20.glGenTextures(fboTex.length, fboTex, 0);
//Bind Frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);
//Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex[0]);
//Define texture parameters
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//Attach texture FBO color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTex[0], 0);
// check status
        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "createFrameBuffers: glCheckFramebufferStatus status != GL_FRAMEBUFFER_COMPLETE");
        }
//we are done, reset
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_NONE);
    }

    public static void deleteFrameBuffers(int[] fboId) {
        if (fboId != null && fboId.length > 0) {
            GLES20.glDeleteFramebuffers(fboId.length, fboId, 0);
        }
    }

    public static float[] changeMvpMatrixCrop(float[] srcMatrix,float viewWidth, float viewHeight, float textureWidth, float textureHeight) {
        float scale = viewWidth * textureHeight / viewHeight / textureWidth;
        float[] mvp = Arrays.copyOf(srcMatrix, srcMatrix.length);
        Matrix.scaleM(mvp, 0, scale > 1 ? 1F : (1F / scale), scale > 1 ? scale : 1F, 1F);
        return mvp;
    }

    public static float[] changeMvpMatrixInside(float[] srcMatrix,float viewWidth, float viewHeight, float textureWidth, float textureHeight) {
        float scale = viewWidth * textureHeight / viewHeight / textureWidth;
        float[] mvp = Arrays.copyOf(srcMatrix, srcMatrix.length);
        Matrix.scaleM(mvp, 0, scale > 1 ? (1F / scale) : 1F, scale > 1 ? 1F : scale, 1F);
        return mvp;
    }

    /**
     * Prefer OpenGL ES 3.0, otherwise 2.0
     *
     * @param context
     * @return
     */
    public static int getSupportGlVersion(Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        int version = configurationInfo.reqGlEsVersion >= 0x30000 ? 3 : 2;
        String glEsVersion = configurationInfo.getGlEsVersion();
        Log.d(TAG, "reqGlEsVersion: " + Integer.toHexString(configurationInfo.reqGlEsVersion)
                + ", glEsVersion: " + glEsVersion + ", return: " + version);
        return version;
    }


    public interface OnReadBitmapListener {
        void onReadBitmapListener(Bitmap var1);
    }
}
