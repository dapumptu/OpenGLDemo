package com.dapumptu.opengldemo;

import android.opengl.GLES20;

public class Shader {

    private int mProgramId;
    private int mPositionHandle;
    private int mNormalHandle;
    private int mTextureUvHandle;
    private int mMvpMatrixHandle;
    private int mMvMatrixHandle;

    private String mVertexShaderSource;
    private String mFragmentShaderSource;

    public Shader(String vsSource, String fsSource) {

        // TODO: Handle null shader source
        mVertexShaderSource = vsSource;
        mFragmentShaderSource = fsSource;

        createShaderProgram();
    }

    public int getProgramId() {
        return mProgramId;
    }

    public int getPositionHandle() {
        return mPositionHandle;
    }

    public int getNormalHandle() {
        return mNormalHandle;
    }

    public int getTextureUvHandle() {
        return mTextureUvHandle;
    }

    public int getMvpMatrixHandle() {
        return mMvpMatrixHandle;
    }

    public int getMvMatrixHandle() {
        return mMvMatrixHandle;
    }

    private void createShaderProgram() {

        int vertexShader = GLESUtils.loadShader(GLES20.GL_VERTEX_SHADER,
                mVertexShaderSource);
        int fragmentShader = GLESUtils.loadShader(GLES20.GL_FRAGMENT_SHADER,
                mFragmentShaderSource);

        mProgramId = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramId, vertexShader);
        GLES20.glAttachShader(mProgramId, fragmentShader);
        GLES20.glLinkProgram(mProgramId);

        // Warning: if attribute is not used, it will be remove by
        // the shader compiler and you will get error when using
        // GLES20.glGetAttribLocation
        mPositionHandle = GLES20.glGetAttribLocation(mProgramId, "aPosition");
        if (mPositionHandle == -1) {
            throw new RuntimeException("aPosition attribute location invalid");
        }

        mNormalHandle = GLES20.glGetAttribLocation(mProgramId, "aNormal");
        if (mNormalHandle == -1) {
            throw new RuntimeException("aNormal attribute location invalid");
        }

        mTextureUvHandle = GLES20.glGetAttribLocation(mProgramId, "aTextureCoordinate");
        if (mTextureUvHandle == -1) {
            throw new RuntimeException("aTextureCoordinate attribute location invalid");
        }

        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgramId, "uMVPMatrix");
        if (mMvpMatrixHandle == -1) {
            throw new RuntimeException("uMVPMatrix location invalid");
        }

        mMvMatrixHandle = GLES20.glGetUniformLocation(mProgramId, "uMVMatrix");
        if (mMvMatrixHandle == -1) {
            throw new RuntimeException("uMVMatrix location invalid");
        }

    }

}
