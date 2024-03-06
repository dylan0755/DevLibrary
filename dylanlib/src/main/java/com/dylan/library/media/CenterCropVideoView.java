package com.dylan.library.media;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;

import com.dylan.library.opengl.GlUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.media.MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;


/**
 * Author: Dylan
 * Date: 2022/4/15
 * Desc:
 */

public class CenterCropVideoView extends GLSurfaceView {
    /**
     * MediaPlayer
     */
    private MediaPlayer mediaPlayer;
    /**
     * 视频路径
     */
    private String videoPath;
    /**
     * OnPreparedListener
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener;

    /**
     * 构造函数
     */
    public CenterCropVideoView(Context context) {
        this(context, null);
    }

    /**
     * 构造函数
     */
    public CenterCropVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        Logger.d("ClipVideoView init");
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        setRenderer(new VideoRenderer(getContext()));
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    /**
     * 设置视频播放监听器
     */
    public void setPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        mOnPreparedListener = onPreparedListener;
    }

    /**
     * 播放视频
     */
    public void start(String path) {
        if (mediaPlayer == null) {
            Logger.d("start video mediaPlayer is null,path:" + path);
            videoPath = path;
            return;
        }
        try {
            videoPath = path;
            Logger.d("start video path:" + path);
            mediaPlayer.reset();
            mediaPlayer.setLooping(true);
            mediaPlayer.setDataSource(path);
            mediaPlayer.setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(null);
            }
        } catch (Exception e) {
            Logger.e("play video error:" + e + "  video path:" + path);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaPlayer != null && !"".equals(videoPath)) {
            Logger.d("video restart");
            mediaPlayer.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            Logger.d("video pause");
            mediaPlayer.pause();
        }
    }

    /**
     * 销毁
     */
    public void release() {
        if (mediaPlayer != null) {
            Logger.d("video release");
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private class VideoRenderer implements Renderer {
        /**
         * TAG
         */
        private static final String TAG = "VideoRenderer";
        /**
         * context
         */
        private Context context;

        /**
         * 纹理id
         */
        int textureId = -1;
        /**
         * 坐标点
         */
        private final int COORDS_PER_VERTEX = 3;
        /**
         * 坐标点
         */
        private final int TEXCOORDS_PER_VERTEX = 2;
        /**
         * float size
         */
        private static final int FLOAT_SIZE = 4;
        /**
         * * 定点坐标
         */
        private float[] QUAD_COORDS;
        /**
         * 纹理坐标
         */
        private float[] quadTexCoords;

        public void resetVerticesAndTextCoords() {
            QUAD_COORDS = new float[]{
                    1.0f, 1.0f, 0,   // 3 top right
                    -1.0f, 1.0f, 0,   // 2 top left
                    -1.0f, -1.0f, 0,   // 0 bottom left
                    1.0f, -1.0f, 0,    // 1 bottom right


            };


            quadTexCoords = new float[]{
                    1f, 0f,  //bottom right
                    0.0f, 0.0f, //bottom left
                    0f, 1f,//top left
                    1f, 1f, //Top right

            };
        }

        /**
         * index
         */
        private final short[] index = {0, 1, 2, 0, 2, 3};
        /**
         * 顶点
         */
        private FloatBuffer quadVertices;
        /**
         * 纹理
         */
        private FloatBuffer quadTexCoord;
        /**
         * 索引
         */
        private ShortBuffer shortBuffer;
        /**
         * program
         */
        private int quadProgram = -1;
        /**
         * 顶点参数索引
         */
        private int quadPositionParam = -1;
        /**
         * 纹理参数索引
         */
        private int quadTexCoordParam = -1;
        /**
         * oes
         */
        private int uTextureSamplerLocation = -1;
        /**
         * 是否有新的一针视频
         */
        private boolean updateSurface = false;
        /**
         * SurfaceTexture
         */
        private SurfaceTexture surfaceTexture;
        /**
         * 锁
         */
        private Object lock = new Object();

        /**
         * 构造函数
         */
        public VideoRenderer(Context context) {
            this.context = context;
            resetVerticesAndTextCoords();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Logger.d("onSurfaceCreated");
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            textureId = textures[0];
            int textureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
            GLES20.glBindTexture(textureTarget, textureId);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);


            String base_vertex = "attribute vec4 a_Position;\n" +
                    "attribute vec2 a_TexCoordinate;\n" +
                    "\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "    v_TexCoord = a_TexCoordinate;\n" +
                    "    gl_Position = a_Position;\n" +
                    "}\n";
            int vertexShader =
                    loadGLShader(TAG, context, GLES20.GL_VERTEX_SHADER, base_vertex);


            String base_fragment_oes = "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "\n" +
                    "uniform samplerExternalOES u_Texture;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "    gl_FragColor = texture2D(u_Texture, v_TexCoord);\n" +
                    "}\n";

            int fragmentShader =
                    loadGLShader(
                            TAG, context, GLES20.GL_FRAGMENT_SHADER, base_fragment_oes);

            quadProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(quadProgram, vertexShader);
            GLES20.glAttachShader(quadProgram, fragmentShader);
            GLES20.glLinkProgram(quadProgram);
            GLES20.glUseProgram(quadProgram);

            checkGLError(TAG, "Program creation");

            quadPositionParam = GLES20.glGetAttribLocation(quadProgram, "a_Position");
            quadTexCoordParam = GLES20.glGetAttribLocation(quadProgram, "a_TexCoordinate");
            uTextureSamplerLocation = GLES20.glGetUniformLocation(quadProgram, "u_Texture");
            checkGLError(TAG, "Program parameters");

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(true);
            surfaceTexture = new SurfaceTexture(textureId);
            Surface surface = new Surface(surfaceTexture);
            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    synchronized (lock) {
                        updateSurface = true;
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    resetVerticesAndTextCoords();
                }
            });
            mediaPlayer.setSurface(surface);
            if (!"".equals(videoPath) && !mediaPlayer.isPlaying()) {
                start(videoPath);
            }
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Logger.d("onSurfaceChanged");
            GLES20.glViewport(0, 0, width, height);
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);


            updateFloatData(width, height);


        }

        private void updateFloatData(float width, float height) {


            int degrees = 0;
            int videoWidth = 0, videoHeight = 0;
            if (EmptyUtils.isNotEmpty(videoPath)) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoPath);
                degrees = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                if (degrees == 90) {
                    int temp = videoHeight;
                    videoHeight = videoWidth;
                    videoWidth = temp;
                }
                Log.e(TAG, "degrees: " + degrees + " videoWidth=" + videoWidth + "  videoHeight=" + videoHeight);
                retriever.release();
            }


            quadVertices = ByteBuffer.allocateDirect(QUAD_COORDS.length * FLOAT_SIZE)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            quadVertices.put(QUAD_COORDS);
            quadVertices.position(0);


            if (degrees == 90) {
                quadTexCoords = rotate90(quadTexCoords);
                //纹理裁剪
                //quadTexCoords = centerCropTextCoords(quadTexCoords, width, height, videoWidth, videoHeight);
            }else if (degrees==180){
                quadTexCoords = rotate180(quadTexCoords);
            } else if (degrees == 270) {
                quadTexCoords = rotate270(quadTexCoords);
            } else {
                //纹理裁剪
                quadTexCoords = centerCropTextCoords(quadTexCoords, width, height, videoWidth, videoHeight);
            }

            quadTexCoord = ByteBuffer.allocateDirect(quadTexCoords.length * FLOAT_SIZE)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            quadTexCoord.put(quadTexCoords);
            quadTexCoord.position(0);


            shortBuffer = ByteBuffer.allocateDirect(index.length * 2)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer();
            shortBuffer.put(index);
            shortBuffer.position(0);
            resetVerticesAndTextCoords();


        }

        public float[] centerCropTextCoords(float[] texCoords, float viewWidth, float viewHeight, float videoWidth, float videoHeight) {
            float videoRatio = videoWidth / videoHeight;
            float viewRatio = viewWidth / viewHeight;

            if (viewRatio < videoRatio) {
                float s = (1 - (videoHeight / videoWidth * viewRatio)) / 2.0F;
                return new float[]{
                        texCoords[0] - s, 0f,
                        texCoords[2] + s, 0f,
                        texCoords[4] + s, 1f,
                        texCoords[6] - s, 1f};


            } else if (viewRatio > videoRatio) {
                float s = (1 - (videoWidth / (videoHeight * viewRatio))) / 2.0F;
                return new float[]{
                        1f, texCoords[1] + s,
                        0f, texCoords[3] + s,
                        0f, texCoords[5] - s,
                        1f, texCoords[7] - s};
            }

            return texCoords;
        }


        @Override
        public void onDrawFrame(GL10 gl) {
            synchronized (lock) {
                if (updateSurface) {
                    surfaceTexture.updateTexImage();
                    updateSurface = false;
                }
            }
            GLES20.glUseProgram(quadProgram);
            // Set the vertex positions.
            quadVertices.position(0);
            GLES20.glEnableVertexAttribArray(quadPositionParam);
            GLES20.glVertexAttribPointer(
                    quadPositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, quadVertices);
            // Set the texture coordinates.
            quadTexCoord.position(0);
            GLES20.glEnableVertexAttribArray(quadTexCoordParam);
            GLES20.glVertexAttribPointer(
                    quadTexCoordParam, TEXCOORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, quadTexCoord);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glUniform1i(uTextureSamplerLocation, 0);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, shortBuffer);
            // Disable vertex arrays
            GLES20.glDisableVertexAttribArray(quadPositionParam);
            GLES20.glDisableVertexAttribArray(quadTexCoordParam);
        }
    }

    private float[] rotate90(float[] quadTexCoords) {
        quadTexCoords = new float[]{
                quadTexCoords[2], quadTexCoords[3], //bottom left
                quadTexCoords[4], quadTexCoords[5],//top left
                quadTexCoords[6], quadTexCoords[7], //Top right
                quadTexCoords[0], quadTexCoords[1],  //bottom right
        };
        return quadTexCoords;

    }

    private float[] rotate180(float[] quadTexCoords) {
        quadTexCoords = new float[]{
                quadTexCoords[4], quadTexCoords[5],//top left
                quadTexCoords[6], quadTexCoords[7], //Top right
                quadTexCoords[0], quadTexCoords[1],  //bottom right
                quadTexCoords[2], quadTexCoords[3], //bottom left
        };
        return quadTexCoords;

    }


    private float[] rotate270(float[] quadTexCoords) {
        quadTexCoords = new float[]{
                quadTexCoords[6], quadTexCoords[7], //Top right
                quadTexCoords[0], quadTexCoords[1],  //bottom right
                quadTexCoords[2], quadTexCoords[3], //bottom left
                quadTexCoords[4], quadTexCoords[5],//top left

        };
        return quadTexCoords;
    }

    public static int loadGLShader(String tag, Context context, int type, String shaderStr) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderStr);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(tag, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     *
     * @param label Label to report in case of error.
     * @throws RuntimeException If an OpenGL error is detected.
     */
    public static void checkGLError(String tag, String label) {
        int lastError = GLES20.GL_NO_ERROR;
        // Drain the queue of all errors.
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(tag, label + ": glError " + error);
            lastError = error;
        }
        if (lastError != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(label + ": glError " + lastError);
        }
    }

}
