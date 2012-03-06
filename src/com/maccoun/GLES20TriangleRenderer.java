/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.maccoun;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;


class GLES20TriangleRenderer implements GLSurfaceView.Renderer {

    private int ranPositionHandle;

    public GLES20TriangleRenderer(Context context) {
        mContext = context;
        mSquareArrayStructVertBuf = ByteBuffer.allocateDirect(mTriangleVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mSquareArrayStructVertBuf.put(mTriangleVerticesData).position(0);

        mSquareStructArrayVerticesBuffer = ByteBuffer.allocateDirect(mSquareVerticesData.length * FLOAT_SIZE_BYTES).
                order(ByteOrder.nativeOrder()).asFloatBuffer();
        mSquareStructArrayVerticesBuffer.put(mSquareVerticesData).position(0);
        textureVerticesBuffer = ByteBuffer.allocateDirect(textureVertices.length * FLOAT_SIZE_BYTES).
                order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureVerticesBuffer.put(textureVertices).position(0);

    }

    public void onDrawFrame(GL10 glUnused) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        calculateFPS();

        if(!isAlgTexShader){
            GLES20.glUseProgram(mProgram);

            Matrix.setIdentityM(mMMatrix, 0);
            Matrix.translateM(mMMatrix, 0, -0.6f, -0.7f, 0.0f);
            Matrix.rotateM(mMMatrix, 0, 90, 0.0f, 0.0f, 1.0f);

            Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

            GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            drawHand(true, true);

            Matrix.setIdentityM(mMMatrix, 0);
            Matrix.translateM(mMMatrix,  0, 0.0f, 0.5f, 0.0f);
            Matrix.rotateM(mMMatrix, 0, 90, 0.0f, 0.0f, 1.0f);
            Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

            GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);


            drawDude();
        }
        else{
            GLES20.glUseProgram(ranProgram);

            Matrix.setIdentityM(mMMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

            GLES20.glUniformMatrix4fv(ranMVPMatrixHandle, 1, false, mMVPMatrix, 0);

            Log.i("MEOW", "ranPositionHandle: " + mProgram);
//
//            int ran = GLES20.glGetUniformLocation(ranProgram, "random");
//            Log.i("RAN", "ran: " + ranPositionHandle);
//            GLES20.glUniform1f(ran, 5);
            GLES20.glVertexAttribPointer(ranPositionHandle, 3, GLES20.GL_FLOAT, false,
                    0, mSquareStructArrayVerticesBuffer);
            GLES20.glEnableVertexAttribArray(ranPositionHandle);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }



    }

    protected void drawHand(boolean isMoving, boolean isArrayStruct)
    {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);

        if(isArrayStruct){
            mSquareArrayStructVertBuf.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mSquareArrayStructVertBuf);
            checkGlError("glVertexAttribPointer maPosition");
            mSquareArrayStructVertBuf.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            checkGlError("glEnableVertexAttribArray maPositionHandle");
            GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mSquareArrayStructVertBuf);
            checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(maTextureHandle);
            checkGlError("glEnableVertexAttribArray maTextureHandle");

        }
        else{
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                    0, mSquareStructArrayVerticesBuffer);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            checkGlError("glVertexAttribPointer maPosition");
            GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                    0, textureVerticesBuffer);
            GLES20.glEnableVertexAttribArray(maTextureHandle);
        }


        if(isMoving){
            GLES20.glUniform1f(yPosHandle, 0.7f*(float)Math.sin(time/50.0f));

        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    protected void drawDude(){
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                0, mSquareStructArrayVerticesBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        checkGlError("glVertexAttribPointer maPosition");
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                0, textureVerticesBuffer);
        GLES20.glEnableVertexAttribArray(maTextureHandle);

        GLES20.glUniform1f(yPosHandle, 0.0f);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

    }

    private void calculateFPS(){
        time = SystemClock.uptimeMillis();

        frameCount = (frameCount + 1)%1000;

        if(frameCount == 1){
            long delta = time - lastTime;
            lastTime = time;
            float fps = 1000.0f/((float)delta/1000.0f);
            Log.i("FPS", "fps:  " + fps);
        }

    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.

        mProgram = createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
            maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
            checkGlError("glGetAttribLocation aTextureCoord");

        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        yPosHandle = GLES20.glGetUniformLocation(mProgram, "ypos");

        ranProgram = createProgram(new AlgVertTexShader().getVShader(), new RanTextFragShader().getFragmentShader());

        Log.i("RANPROG" , "ranProg: " + ranProgram);


        ranPositionHandle = GLES20.glGetAttribLocation(ranProgram, "aPosition");

        checkGlError("glGetAttribLocation aPosition");
        if (ranPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
         ranMVPMatrixHandle = GLES20.glGetUniformLocation(ranProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (ranMVPMatrixHandle== -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        /*
         * Create our texture. This has to be done each time the
         * surface is created.
         */

        textures = new int[2];
        GLES20.glGenTextures(2, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);


        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.raw.cool);


        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);


        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.raw.thumbsup);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);


        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);


    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);

        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
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
        }
        return program;
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    public void setIsTexShader(boolean isAlgTex){
        isAlgTexShader = isAlgTex;
    }


    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    private final float[] mSquareVerticesData = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f,  0.5f, 0.0f,
            0.5f,  0.5f, 0.0f,
    };

    private final float textureVertices[] = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f,  1.0f,
            0.0f,  0.0f,
    };


    private FloatBuffer mSquareStructArrayVerticesBuffer;
    private FloatBuffer textureVerticesBuffer;

    private final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -0.5f, -0.5f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f,  0.5f, 0.0f, 0.0f,  1.0f,
            0.5f,  0.5f, 0.0f, 0.0f,  0.0f, };


    private FloatBuffer mSquareArrayStructVertBuf;


    private String mVertexShader = new VertexShader().getVShader();


    private String mFragmentShader = new FragmentShader().getFragmentShader();

    private long time;
    private long lastTime = 0;
    private long frameCount = 0;

    private float[] mMVPMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mMMatrix = new float[16];
    private float[] mVMatrix = new float[16];

    private int mProgram;
    private int ranProgram;
    private int mTextureID;
    private int muMVPMatrixHandle;
    private int ranMVPMatrixHandle;
    private int maPositionHandle;
    private int maTextureHandle;
    private int yPosHandle;
    private boolean isAlgTexShader;
    private boolean isRandomTex = true;

    private int[] textures;

    private Context mContext;
    private static String TAG = "GLES20TriangleRenderer";
}
