/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dylan.library.opengl;

import android.opengl.GLES20;


/**
 * 不同的纹理要用不同的着色器，不然渲染不出来
 */
public class Texture2dDrawer extends TextureDrawer {

    // Simple vertex shader, used for all programs.
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uTexMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = uMVPMatrix * aPosition;\n" +
                    "    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    // Simple fragment shader for use with "normal" 2D textures.
    private static final String FRAGMENT_SHADER_2D =
            "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform sampler2D sTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";


    //一段高斯模糊的着色器代码
    private static final String FRAGMENT_SHADER_2D_BLUR="precision lowp float;\n" +
            "precision lowp int;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D sTexture;\n" +
            "\n" +
            "varying vec2 blurCoordinates[5];\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 original = texture2D(sTexture, vTextureCoord);\n" +
            "    lowp vec4 sum = vec4(0.0);\n" +
            "    sum += texture2D(sTexture, blurCoordinates[0]) * 0.204164;\n" +
            "    sum += texture2D(sTexture, blurCoordinates[1]) * 0.304005;\n" +
            "    sum += texture2D(sTexture, blurCoordinates[2]) * 0.304005;\n" +
            "    sum += texture2D(sTexture, blurCoordinates[3]) * 0.093913;\n" +
            "    sum += texture2D(sTexture, blurCoordinates[4]) * 0.093913;\n" +
            "    gl_FragColor = vec4(sum.xyz,  1.0);\n" +
            "    gl_FragColor=vec4(mix(gl_FragColor.rgb, vec3(0.0), 0.02), gl_FragColor.a);\n" +
            "}";


    private int muMVPMatrixLoc;
    private int muTexMatrixLoc;
    private int maPositionLoc;
    private int maTextureCoordLoc;

    public Texture2dDrawer() {
        super(VERTEX_SHADER, FRAGMENT_SHADER_2D);
    }

    @Override
    protected Drawable2d getDrawable2d() {
        return new Drawable2dFull();
    }

    @Override
    protected void getLocations() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        GlUtils.checkLocation(maPositionLoc, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
        GlUtils.checkLocation(maTextureCoordLoc, "aTextureCoord");
        muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        GlUtils.checkLocation(muMVPMatrixLoc, "uMVPMatrix");
        muTexMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexMatrix");
        GlUtils.checkLocation(muTexMatrixLoc, "uTexMatrix");
    }

    @Override
    public void drawFrame(int textureId, float[] texMatrix, float[] mvpMatrix) {
        GlUtils.checkGlError("draw start");

        // Select the program.
        GLES20.glUseProgram(mProgramHandle);
        GlUtils.checkGlError("glUseProgram");

        // Set the texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GlUtils.checkGlError("glUniformMatrix4fv");

        // Copy the texture transformation matrix over.
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
        GlUtils.checkGlError("glUniformMatrix4fv");

        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GlUtils.checkGlError("glEnableVertexAttribArray");

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(maPositionLoc, Drawable2d.COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, Drawable2d.VERTEXTURE_STRIDE, mDrawable2d.vertexArray());
        GlUtils.checkGlError("glVertexAttribPointer");

        // Enable the "aTextureCoord" vertex attribute.
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GlUtils.checkGlError("glEnableVertexAttribArray");

        // Connect texBuffer to "aTextureCoord".
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, Drawable2d.TEXTURE_COORD_STRIDE, mDrawable2d.texCoordArray());
        GlUtils.checkGlError("glVertexAttribPointer");

        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mDrawable2d.vertexCount());
        GlUtils.checkGlError("glDrawArrays");

        // Done -- disable vertex array, texture, and program.
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUseProgram(0);
    }

}
