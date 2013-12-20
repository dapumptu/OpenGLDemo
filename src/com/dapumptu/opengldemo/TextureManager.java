package com.dapumptu.opengldemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.InputStream;

public class TextureManager {

    private static final String TAG = "TextureManager";
    private static TextureManager sInstance;

    private Context mContext;
    private int mDiffuseTextureId;
    private int mCubeMapTextureId;

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public int getDiffuseTextureId() {
        return mDiffuseTextureId;
    }

    public int getCubeMapTextureId() {
        return mCubeMapTextureId;
    }

    public static TextureManager getInstance() {
        if (sInstance == null) {
            sInstance = new TextureManager();
        }
        return sInstance;
    }

    private TextureManager() {

    }

    public void createDiffuseTexture() {

        if (mContext == null) {
            Log.e(TAG, "Context has not been initialized!");
            return;
        }

        /** Create the texture, setup its properties and upload it to OpenGL ES */
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        mDiffuseTextureId = textureIds[0];

        /** Bind the texture and Setup its properties */
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mDiffuseTextureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        /** Load the resource as a bitmap */
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.bubbles);
        Bitmap bitmapTexture = BitmapFactory.decodeStream(inputStream);

        /** Upload the texture to OpenGL ES */
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTexture, 0);

        /** Mark the bitmap for deletion */
        bitmapTexture.recycle();
    }

    public void createCubeMapTexture( )
    {
        if (mContext == null) {
            Log.e(TAG, "Context has not been initialized!");
            return;
        }

        int[] textureIds = new int[1];

        GLES20.glGenTextures (1, textureIds, 0);
        GLES20.glBindTexture (GLES20.GL_TEXTURE_CUBE_MAP, textureIds[0]);

        mCubeMapTextureId = textureIds[0];

        /** Load the resource as a bitmap */
        InputStream inputStream;
        Bitmap bitmapTexture;

        inputStream = mContext.getResources().openRawResource(R.raw.bubbles);
        bitmapTexture = BitmapFactory.decodeStream(inputStream);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, bitmapTexture, 0);
        bitmapTexture.recycle();

        inputStream = mContext.getResources().openRawResource(R.raw.bubbles);
        bitmapTexture = BitmapFactory.decodeStream(inputStream);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, bitmapTexture, 0);
        bitmapTexture.recycle();

        inputStream = mContext.getResources().openRawResource(R.raw.bubbles);
        bitmapTexture = BitmapFactory.decodeStream(inputStream);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, bitmapTexture, 0);
        bitmapTexture.recycle();

        inputStream = mContext.getResources().openRawResource(R.raw.bubbles);
        bitmapTexture = BitmapFactory.decodeStream(inputStream);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, bitmapTexture, 0);
        bitmapTexture.recycle();

        inputStream = mContext.getResources().openRawResource(R.raw.bubbles);
        bitmapTexture = BitmapFactory.decodeStream(inputStream);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, bitmapTexture, 0);
        bitmapTexture.recycle();

        inputStream = mContext.getResources().openRawResource(R.raw.bubbles);
        bitmapTexture = BitmapFactory.decodeStream(inputStream);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, bitmapTexture, 0);
        bitmapTexture.recycle();


        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

    }
}
