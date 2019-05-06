package com.liu.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {
    private final  TriggerRender mRender;
    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        mRender = new TriggerRender();
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
