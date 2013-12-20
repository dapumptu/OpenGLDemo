
package com.dapumptu.opengldemo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MyGLSurfaceView extends GLSurfaceView {

    private static final String TAG = "MyGLSurfaceView";
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    private ScaleGestureDetector mScaleDetector;
    private final GLESRenderer mRenderer;
    private float mPreviousX;
    private float mPreviousY;
    //private float mScaleFactor;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = mRenderer.getScale() * detector.getScaleFactor();
            mRenderer.setScale(scale);

            Log.d(TAG, "onScale: scale factor is " + scale);

            //invalidate();
            return true;
        }
    }

    public MyGLSurfaceView(Context context) {
        super(context);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        setEGLConfigChooser(5, 6, 5, 0, 16, 0);
        setEGLContextClientVersion(2);

        mRenderer = new GLESRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if (e.getPointerCount() > 1 &&
                mScaleDetector.onTouchEvent(e)) {
            requestRender();
        } else {

            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;

                    float rotateX = mRenderer.getRotateX() + dy * -1.0f * 180f / getHeight();
                    mRenderer.setRotateX(rotateX);

                    float rotateY = mRenderer.getRotateY() + dx * 180f / getWidth();
                    mRenderer.setRotateY(rotateY);

                    requestRender();
                    break;
            }
            mPreviousX = x;
            mPreviousY = y;
        }

        return true;
    }

    public void onModelButtonClick() {
        mRenderer.changeObject();
        requestRender();
    }

    public void onShaderButtonClick() {
        mRenderer.changeShader();
        requestRender();
    }
}
