
package com.dapumptu.opengldemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    private MyGLSurfaceView mSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);

        RelativeLayout.LayoutParams params;
        mSurfaceView = new MyGLSurfaceView(this);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.addView(mSurfaceView, params);

        Button modelButton = new Button(this);
        modelButton.setText("Model");
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layout.addView(modelButton, params);

        modelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSurfaceView.onModelButtonClick();
            }
        });

        Button shaderButton = new Button(this);
        shaderButton.setText("Shader");
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layout.addView(shaderButton, params);

        shaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSurfaceView.onShaderButtonClick();
            }
        });

    }
    
    @Override
    public void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }

}
