package com.jayce.eopengljni;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;

public class OpenGLJniRenderer implements GLSurfaceView.Renderer
{
	
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		OpenGLJniLib.step();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		OpenGLJniLib.init(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		OpenGLJniLib.create();
	}
}