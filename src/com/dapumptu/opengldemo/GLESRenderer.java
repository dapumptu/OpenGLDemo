
package com.dapumptu.opengldemo;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GLESRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "GLES20Renderer";

    private Context mContext;

    private float mScale = 1.0f;
    private float mRotateX = 0;
    private float mRotateY = 0;

    private float[] mMvpMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    //private float[] mNormalMatrix = new float[16];

    private int mCurrentJsonGeometryIndex = 0;
    private int mCurrentShaderIndex = 0;

    private List<JsonGeometry> mJsonGeometryList;
    private List<Shader> mShaderList;

    public float getScale() {
        return mScale;
    }

    public void setScale(float scale) {
        mScale = Math.max(0.005f, Math.min(scale, 1.0f));
    }

    public float getRotateX() {
        return mRotateX;
    }

    public void setRotateX(float rotateX) {
        this.mRotateX = rotateX % 360f;
    }

    public float getRotateY() {
        return mRotateY;
    }

    public void setRotateY(float rotateY) {
        this.mRotateY = rotateY % 360f;
    }

    public GLESRenderer(Context context) {
        mContext = context;
        mShaderList = new ArrayList<Shader>();
        mJsonGeometryList = new ArrayList<JsonGeometry>();
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        render();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        setProjection(width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig glConfig) {

        //TextureManager textureManager = TextureManager.getInstance();
        //textureManager.setContext(mContext);
        //textureManager.createDiffuseTexture();
        //textureManager.createCubeMapTexture();

        init();
    }

    public void render() {

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(getCurrentShader().getProgramId());

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureManager.getInstance().getDiffuseTextureId());
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, TextureManager.getInstance().getCubeMapTextureId());

        float[] rotateXMatrix = new float[16];
        float[] rotateYMatrix = new float[16];
        float[] rotateMatrix = new float[16];
        float[] scaleMatrix = new float[16];
        float[] translateMatrix = new float[16];
        float[] localModelMatrix = new float[16];
        //float[] inverseNormalMatrix = new float[16];

        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0,
                getCurrentJsonGeometry().getCenterX() * -1.0f,
                getCurrentJsonGeometry().getCenterY() * -1.0f,
                getCurrentJsonGeometry().getCenterZ() * -1.0f);

        Matrix.setRotateM(rotateXMatrix, 0, mRotateX, 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(rotateYMatrix, 0, mRotateY, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(rotateMatrix, 0, rotateXMatrix, 0, rotateYMatrix, 0);

        float scale = getCurrentJsonGeometry().getScale() * mScale;
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale, scale, scale);

        Matrix.multiplyMM(localModelMatrix, 0, scaleMatrix, 0, rotateMatrix, 0);
        Matrix.multiplyMM(localModelMatrix, 0, localModelMatrix, 0, translateMatrix, 0);
        mModelMatrix = localModelMatrix;

        Matrix.multiplyMM(mModelMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMvpMatrix, 0, mProjectionMatrix, 0, mModelMatrix, 0);
        
        GLES20.glUniformMatrix4fv(getCurrentShader().getMvMatrixHandle(), 1, false, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(getCurrentShader().getMvpMatrixHandle(), 1, false, mMvpMatrix, 0);

        getCurrentJsonGeometry().draw(
                getCurrentShader().getPositionHandle(), 
                getCurrentShader().getTextureUvHandle(), 
                getCurrentShader().getNormalHandle());

    }

    public void setProjection(int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float aspectRatio = (float) width / (float) height;
        Matrix.frustumM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 1, 10);
    }

    public void changeObject() {
        reset();
        mCurrentJsonGeometryIndex = (mCurrentJsonGeometryIndex + 1) % mJsonGeometryList.size();
    }

    public void changeShader() {
        mCurrentShaderIndex = (mCurrentShaderIndex + 1) % mShaderList.size();
    }

    public void init() {

        /** OpenGL ES initialization is done here */
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        /** Enable back face culling to avoid drawing faces that are invisible */
        //GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        Shader shaderObj;
        shaderObj = createShader(R.raw.vshader, R.raw.fshader);
        mShaderList.add(shaderObj);
        shaderObj = createShader(R.raw.vshader2, R.raw.fshader);
        mShaderList.add(shaderObj);
        shaderObj = createShader(R.raw.vshader3, R.raw.fshader);
        mShaderList.add(shaderObj);

        JsonGeometry geometry;
        geometry = createJsonGeometry(R.raw.suzanne);
        mJsonGeometryList.add(geometry);
        geometry = createJsonGeometry(R.raw.teapot);
        mJsonGeometryList.add(geometry);
//        geometry = createJsonGeometry(R.raw.icosphere);
//        mJsonGeometryList.add(geometry);

        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -2, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    private JsonGeometry createJsonGeometry(int jsonResId) {

        JsonGeometry jsonModel = new JsonGeometry();
        String jsonStr = null;
        try {
            InputStream in = mContext.getResources().openRawResource(jsonResId);
            byte[] b = new byte[in.available()];
            in.read(b);
            jsonStr = new String(b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonStr != null) {
            //Log.d(TAG, "Start parsing JSON");
            jsonModel.initFromJson(jsonStr);
        }

        //Log.d(TAG, "After parsing JSON file");

        return jsonModel;
    }

    private Shader createShader(int vsResId, int fsResId) {

        InputStream inStream = mContext.getResources().openRawResource(vsResId);
        String vertexShaderSource = null;
        try {
            vertexShaderSource = GLESUtils.inputStreamToString(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fragmentShaderSource = null;
        inStream = mContext.getResources().openRawResource(fsResId);
        try {
            fragmentShaderSource = GLESUtils.inputStreamToString(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Shader(vertexShaderSource, fragmentShaderSource);

    }

    private Shader getCurrentShader() {
        return mShaderList.get(mCurrentShaderIndex);
    }

    private JsonGeometry getCurrentJsonGeometry() {
        return mJsonGeometryList.get(mCurrentJsonGeometryIndex);
    }

    private void reset() {
        mScale = 1.0f;
        mRotateX = 0;
        mRotateY = 0;
    }
}


