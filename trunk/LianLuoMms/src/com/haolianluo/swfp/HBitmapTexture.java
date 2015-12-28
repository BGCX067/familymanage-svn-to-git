package com.haolianluo.swfp;
/**根据顶点索引将图片画到画布上*/
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.opengles.GL10;

class HBitmapTexture
{
	/**四个顶点*/
	private final static int VERTS = 4;
	/**画布坐标*/
	private FloatBuffer mVertex;
	/**图像坐标*/
	private FloatBuffer mTexture;
	/**顶点坐标索引*/
	private ShortBuffer mVIndex;
	
	private int mFormat;
	private int mType;
	private float mSide;
	private static ByteBuffer mBuffer;
	private int mDepth;
	
	public HBitmapTexture(int w, int h, int depth)
	{
		ByteBuffer tmp = ByteBuffer.allocateDirect(VERTS * 3 * 4);
		tmp.order(ByteOrder.nativeOrder());
		mVertex = tmp.asFloatBuffer();
		float[] fvc = {    
			-1f,  1f, 0.0f,
			-1f, -1f, 0.0f, 
			 1f, -1f, 0.0f,
			 1f,  1f, 0.0f,};
		mVertex.put(fvc);
		mVertex.position(0);
		
		tmp = ByteBuffer.allocateDirect(VERTS * 2);
		tmp.order(ByteOrder.nativeOrder());
        mVIndex = tmp.asShortBuffer();
        short[] index = {0,1,3,2};
        mVIndex.put(index);
        mVIndex.position(0);
		
        adjust(w, h);
        
        if(depth == 16){
        	mFormat = GL10.GL_RGB;
        	mType = GL10.GL_UNSIGNED_SHORT_5_6_5;
        }else if(depth == 24){
        	mFormat = GL10.GL_RGB;
        	mType = GL10.GL_UNSIGNED_BYTE;
        }else if(depth == 32){
        	mFormat = GL10.GL_RGBA;
        	mType = GL10.GL_UNSIGNED_BYTE;
        }
        
        int bpl = ((((int)mSide*depth) + 31) >> 5) << 2;
        if(mBuffer == null){
        	Runtime.getRuntime().gc();
        	mBuffer = ByteBuffer.allocate(bpl * (int)mSide);
        }
		mDepth = depth;
		Runtime.getRuntime().gc();
	}

	private void adjust(int w, int h)
	{
		final int _256 = 256, _512 = 512;
		float max = w > h ? w:h;
		float x = Math.abs(_256 - max);
		float y = Math.abs(_512 - max);
		mSide = x > y ? _256:_512;
		float xAdjust = mSide / w;
		float yAdjust = mSide / h;
//		HLog.i("libswfengine", "i = " + mSide + " xAdjust = " + xAdjust + " yAdjust = " + yAdjust);
		float []t = {
				0.0f, 0.0f,
				0.0f, 1.0f, 
				1.0f, 1.0f, 
				1.0f, 0.0f
		};
		if(xAdjust < yAdjust){
			t[1] = ((mSide - xAdjust * w)/2)/mSide;
			t[3] = t[1];
			t[5] -= t[1];
			t[7] = t[5];
		}else{
			t[0] = ((mSide - yAdjust * w)/2)/mSide;
			t[2] = t[0];
			t[4] -= t[0];
			t[6] = t[4];
		}
		
		ByteBuffer tbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTexture  = tbb.asFloatBuffer();
        mTexture.put(t);
        mTexture.position(0);
	}
	
	public void draw(GL10 gl)
	{
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, mFormat, getWidth(), getHeight(), 0, mFormat, mType, mBuffer);
		/** 清除显存 */
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertex);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexture);
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_SHORT, mVIndex);
	}

	public byte[] getByteArray() {
		return mBuffer.array();
	}
	
	public int getWidth(){
		return (int)mSide;
	}
	
	public int getHeight(){
		return (int)mSide;
	}

	public int getDepth() {
		return mDepth;
	}
}
