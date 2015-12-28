package com.hammer.notes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.hammer.notes.utils.ToolsUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;

public class NoteRenderer implements GLSurfaceView.Renderer{

	//coordinate
	private FloatBuffer mFaceVertices;
	private FloatBuffer mFaceTextureCoordinates;
	//private FloatBuffer mFaceColors;
	
	//constant values
	private static final int TEXTURE_COORDINATE_DATA_SIZE = 2;
	//private static final int COLOR_DATA_SIZE = 4;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int FREQUENCY = 20;
    private static final int POSITION_OFFSET = 0;
    private static final int POSITION_DATA_SIZE = 3;
    private static final int STRIDE = POSITION_DATA_SIZE * BYTES_PER_FLOAT;
    private static final float UNIT = 2.20f;
    private static final int ROW = 7;
    private static final int COLUMN = 2;
    private static final int FACES = 7;
    private static final int COUNT = 3 * FACES * COLUMN;
    
    private float mX = 0f;
    private float mY = 0f;
    private float mZ = 0f;
    private float mWidth;
    private float mHeight;
    private float MAX_X;
    private float MAX_Y;
    private float MAX_Z;
    private float UNIT_X;
    private float UNIT_Y;
    private float UNIT_Z;
    private float mLeftX;
    private float mRightX;
    private float mUpY;
    private float mLeftS;
    private float mRightS;
    private float mUpUnit;
    private float mUpTranslate;
    
    //matrix
    private float[] mMVPMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    
    //handles
    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mProgramHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private int mTextureDataHandle;
    //private int mColorHandle;
    
    private MyPoint[][] mPoints;
    private Handler mHandler;
    private Context mContext;
    private boolean mLeft = true;
    private float[] mColor = {0f, 0f, 0f, 0f};
    
    public NoteRenderer(Context context, Handler handler)
    {
    	mContext = context;
   	 	mHandler = handler;
   	 	mPoints = new MyPoint[ROW][COLUMN];
   	 	for(int i = 0; i < ROW; i++)
   	 	{
   	 		for(int j = 0; j < COLUMN; j++)
   	 		{
   	 			mPoints[i][j] = new MyPoint(0, 0, 0);
   	 		}
   	 	}
    }
    
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		if(0 != mTextureDataHandle) 
		{
			if(!mLeft)
	    	{
				if(mY > 0)
	    		{
	    			mX -= UNIT_X;
	    			mY -= UNIT_Y;
	    			mZ -= UNIT_Z;
	    			mUpTranslate += mUpUnit;
	    		}
	    		else
	    		{
	    			mX = 0f;
	    			mY = 0f;
	    			mZ = 0f;
	    			mUpTranslate = mMaxTranslate;
	    			mHandler.sendEmptyMessage(NotesActivity.MSG_ANIMATE_END_RIGHT);
	    		}
	    	}
	    	else
	    	{
	    		if(mY < MAX_Y)
	    		{
	    			mX += UNIT_X;
	    			mY += UNIT_Y;
	    			mZ += UNIT_Z;
	    			mUpTranslate -= mUpUnit;
	    		}
	    		else
	    		{
	    			mX = MAX_X;
	    			mY = MAX_Y;
	    			mZ = MAX_Z;
	    			mUpTranslate = 0;
	    			mHandler.sendEmptyMessage(NotesActivity.MSG_ANIMATE_END_LEFT);
	    		}
	    	}
	    	setVertices();
	        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
	        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
	        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
	        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
	        //mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
	        
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
	        GLES20.glUniform1i(mTextureUniformHandle, 0);
	         
	        Matrix.setIdentityM(mModelMatrix, 0);
	        Matrix.translateM(mModelMatrix, 0, 0f, -mUpTranslate, 0f);
	        drawFaces();
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		GLES20.glViewport(0, 0, width, height);
        //Log.e("NoteRenderer", "w:" + width + ",h:" + height);
        final float ratio = (float) width / height;
        mHeight = UNIT;
        mWidth = ratio * UNIT;
        mUpY = mHeight / 2 - Y_UP * mHeight;
        MAX_X = mWidth / 8;
        MAX_Y = mHeight * (1 - Y_UP)  / (ROW -1);
        MAX_Z = MAX_Y;
        UNIT_X = MAX_X / FREQUENCY;
        UNIT_Y = MAX_Y / FREQUENCY;
        UNIT_Z = UNIT_Y;
        mRightX = mWidth / 2;
        mLeftX = - mRightX;
        mLeftS = mLeftX + MAX_X;
        mRightS = mRightX - MAX_X;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		GLES20.glClearColor(0f, 0f, 0f, 0f);
        // Position the eye behind the origin.
		// No culling of back faces
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		// No depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		//GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);	
		
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.1f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        
        final String vertexShader =
                "uniform mat4 u_MVPMatrix;      \n"        // A constant representing the combined model/view/projection matrix.
                
              + "attribute vec4 a_Position;     \n"        // Per-vertex position information we will pass in.
              + "attribute vec2 a_TexCoordinate;\n"     // Per-vertex texture coordinate information we will pass in. 
              //+ "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.
              
              + "varying vec2 v_TexCoordinate;  \n"     // This will be passed into the fragment shader.  
              //+ "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.
              
              + "void main()                    \n"        // The entry point for our vertex shader.
              + "{                              \n"
             // + "   v_Color = a_Color;          \n"		// Pass the color through to the fragment shader. 
              + "   v_TexCoordinate = a_TexCoordinate;\n"// Pass through the texture coordinate.
              + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
              + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in                                                                      
              + "}                              \n";    // normalized screen coordinates.
            
        final String fragmentShader =
                "precision mediump float;       \n"        // Set the default precision to medium. We don't need as high of a 
                                                        // precision in the fragment shader. 
       	   + "uniform sampler2D u_Texture;   \n"     // The input texture.
       	   + "varying vec2 v_TexCoordinate;  \n"     // Interpolated texture coordinate per fragment.
       	   //+ "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the 
       	   
              + "void main()                    \n"        // The entry point for our fragment shader.
              + "{                              \n"
              + "   gl_FragColor =  " /*v_Color **/
              +"texture2D(u_Texture, v_TexCoordinate);     \n"        // Pass the color directly through the pipeline. 
              + "}                              \n";    
            
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if(vertexShaderHandle != 0)
        {
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);
            GLES20.glCompileShader(vertexShaderHandle);
            
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            
            if(compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }
        
        if(vertexShaderHandle == 0)
        {
            throw new RuntimeException("failed to creating vertex shader");
        }
        
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if(fragmentShaderHandle != 0)
        {
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);
            GLES20.glCompileShader(fragmentShaderHandle);
            
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            
            if(compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
            
        }
        
        if(fragmentShaderHandle == 0)
        {
            throw new RuntimeException("failed to create fragment shader");
        }
        
        mProgramHandle = GLES20.glCreateProgram();
        if(mProgramHandle != 0)
        {
            GLES20.glAttachShader(mProgramHandle, vertexShaderHandle);
            GLES20.glAttachShader(mProgramHandle, fragmentShaderHandle);
            
            GLES20.glBindAttribLocation(mProgramHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(mProgramHandle, 1, "a_TexCoordinate");
            //GLES20.glBindAttribLocation(mProgramHandle, 2, "a_Color");
            
            GLES20.glLinkProgram(mProgramHandle);
            
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
            
            if(linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(mProgramHandle);
                mProgramHandle = 0;
            }
        }
        
        if(mProgramHandle == 0)
        {
            throw new RuntimeException("failed to create program");
        }
        GLES20.glUseProgram(mProgramHandle);
	}
	
	private class MyPoint{
	   	public MyPoint(float a, float b, float c)
	   	{
	   		set(a, b, c);
	   	}
	   	public float x;
	   	public float y;
	   	public float z;
	   	public void set(float a, float b, float c)
	   	{
	   		x = a;
	   		y = b;
	   		z = c;
	   	}
    }
	
	// s----t
	// |    |
	// a----c
    // |    |  //face1
    // b----d
    // |    |  //face2
    // e----f
    // |    |  //face3
    // g----h
    // |    |  //face4
    // i----j
    // |    |  //face5
    // k----l
    // |    |  //face6
    // m----n
	
	private static final float Y_UP = 0.128f;
    private static final float Y_UNIT = (1f - Y_UP) / (ROW - 1);
    private static final float[] faceTextureCoordinate =
    {      
    		//face title
	    	0.0f, 0.0f,                 
	        0.0f, Y_UP, 
	        1.0f, 0.0f,
	        0.0f, Y_UP,
	        1.0f, Y_UP,
	        1.0f, 0.0f,
    	
            //face6
            0.0f, Y_UP,                 
            0.0f, Y_UP + Y_UNIT, 
            1.0f, Y_UP,
            0.0f, Y_UP + Y_UNIT,
            1.0f, Y_UP + Y_UNIT,
            1.0f, Y_UP,
           
          //face5
            0.0f, Y_UP + Y_UNIT,                 
            0.0f, Y_UP + 2 * Y_UNIT,
            1.0f, Y_UP + Y_UNIT,
            0.0f, Y_UP + 2 * Y_UNIT,
            1.0f, Y_UP + 2 * Y_UNIT,
            1.0f, Y_UP + Y_UNIT,
          
          //face4
            0.0f, Y_UP + 2 * Y_UNIT,                 
            0.0f, Y_UP + 3 * Y_UNIT,
            1.0f, Y_UP + 2 * Y_UNIT,
            0.0f, Y_UP + 3 * Y_UNIT,
            1.0f, Y_UP + 3 * Y_UNIT,
            1.0f, Y_UP + 2 * Y_UNIT,
            
          //face3
            0.0f, Y_UP + 3 * Y_UNIT,                 
            0.0f, Y_UP + 4 * Y_UNIT,
            1.0f, Y_UP + 3 * Y_UNIT,
            0.0f, Y_UP + 4 * Y_UNIT,
            1.0f, Y_UP + 4 * Y_UNIT,
            1.0f, Y_UP + 3 * Y_UNIT,
             
          //face2
            0.0f, Y_UP + 4 * Y_UNIT,                 
            0.0f, Y_UP + 5 * Y_UNIT,
            1.0f, Y_UP + 4 * Y_UNIT,
            0.0f, Y_UP + 5 * Y_UNIT,
            1.0f, Y_UP + 5 * Y_UNIT,
            1.0f, Y_UP + 4 * Y_UNIT,
           
          //face1
            
            0.0f, Y_UP + 5 * Y_UNIT,                 
            0.0f, 1.0f,
            1.0f, Y_UP + 5 * Y_UNIT,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, Y_UP + 5 * Y_UNIT,
          
    };
	
	private void setVertices()
    {
		for(int i = 0; i < ROW; i++)
   	 	{
			mPoints[i][0].x = mLeftX;
   		 	mPoints[i][1].x = mRightX;
   		 	
   		 	mPoints[i][0].y = mUpY - i * mY;
		 	mPoints[i][1].y = mPoints[i][0].y;
   		 	
		 	mPoints[i][0].z = 0f;
   		 	mPoints[i][1].z = 0f;
   		 	if(0 != i % 2)
   		 	{
   		 		//mPoints[i][0].z = mZ - MAX_Z;
   		 		//mPoints[i][1].z = mZ - MAX_Z;
   		 		
   		 		mPoints[i][0].x = mLeftS - mX;
   		 		mPoints[i][1].x = mRightS + mX;
   		 		
   		 	}
   	 	}
		
   	 	final float[] verticesData = {
   	 		
   	 			//face title
   	 			mLeftX, mHeight/2, 0f,	
   	 			mLeftX, mUpY, 0f,
   	 			mRightX, mHeight/2, 0f,
   	 			
   	 			mLeftX, mUpY, 0f,
   	 			mRightX, mUpY, 0f,
   	 			mRightX, mHeight/2, 0f,
   	 		
   	 			//face1
    		 	mPoints[0][0].x, mPoints[0][0].y, mPoints[0][0].z,//a
    		 	mPoints[1][0].x, mPoints[1][0].y, mPoints[1][0].z,//b
    		 	mPoints[0][1].x, mPoints[0][1].y, mPoints[0][1].z,//c
    		 	
    		 	mPoints[1][0].x, mPoints[1][0].y, mPoints[1][0].z,//b
    		 	mPoints[1][1].x, mPoints[1][1].y, mPoints[1][1].z,//d
    		 	mPoints[0][1].x, mPoints[0][1].y, mPoints[0][1].z,//c
    		 	
    		    //face2
    		 	mPoints[1][0].x, mPoints[1][0].y, mPoints[1][0].z,//b
    		 	mPoints[2][0].x, mPoints[2][0].y, mPoints[2][0].z,//e
    		 	mPoints[1][1].x, mPoints[1][1].y, mPoints[1][1].z,//d
    		 	
    		 	mPoints[2][0].x, mPoints[2][0].y, mPoints[2][0].z,//e
    		 	mPoints[2][1].x, mPoints[2][1].y, mPoints[2][1].z,//f
    		 	mPoints[1][1].x, mPoints[1][1].y, mPoints[1][1].z,//d
    		 	
    		 	//face3
    		 	mPoints[2][0].x, mPoints[2][0].y, mPoints[2][0].z,//e
    		 	mPoints[3][0].x, mPoints[3][0].y, mPoints[3][0].z,//g
    		 	mPoints[2][1].x, mPoints[2][1].y, mPoints[2][1].z,//f
    		 	
    		 	mPoints[3][0].x, mPoints[3][0].y, mPoints[3][0].z,//g
    		 	mPoints[3][1].x, mPoints[3][1].y, mPoints[3][1].z,//h
    		 	mPoints[2][1].x, mPoints[2][1].y, mPoints[2][1].z,//f
    		 	
    		 	//face4
    		 	mPoints[3][0].x, mPoints[3][0].y, mPoints[3][0].z,//g
    		 	mPoints[4][0].x, mPoints[4][0].y, mPoints[4][0].z,//i
    		 	mPoints[3][1].x, mPoints[3][1].y, mPoints[3][1].z,//h
    		 	
    		 	mPoints[4][0].x, mPoints[4][0].y, mPoints[4][0].z,//i
    		 	mPoints[4][1].x, mPoints[4][1].y, mPoints[4][1].z,//j
    		 	mPoints[3][1].x, mPoints[3][1].y, mPoints[3][1].z,//h
    		 	
    		 	//face5
    		 	mPoints[4][0].x, mPoints[4][0].y, mPoints[4][0].z,//i
    		 	mPoints[5][0].x, mPoints[5][0].y, mPoints[5][0].z,//k
    		 	mPoints[4][1].x, mPoints[4][1].y, mPoints[4][1].z,//j
    		 	
    		 	mPoints[5][0].x, mPoints[5][0].y, mPoints[5][0].z,//k
    		 	mPoints[5][1].x, mPoints[5][1].y, mPoints[5][1].z,//l
    		 	mPoints[4][1].x, mPoints[4][1].y, mPoints[4][1].z,//j
    		 	
    		 	//face6
    		 	mPoints[5][0].x, mPoints[5][0].y, mPoints[5][0].z,//k
    		 	mPoints[6][0].x, mPoints[6][0].y, mPoints[6][0].z,//m
    		 	mPoints[5][1].x, mPoints[5][1].y, mPoints[5][1].z,//l
    		 	
    		 	mPoints[6][0].x, mPoints[6][0].y, mPoints[6][0].z,//m
    		 	mPoints[6][1].x, mPoints[6][1].y, mPoints[6][1].z,//n
    		 	mPoints[5][1].x, mPoints[5][1].y, mPoints[5][1].z,//l
    		 	
   	 	};
   	 	mFaceVertices = ByteBuffer.allocateDirect(verticesData.length * BYTES_PER_FLOAT)
             .order(ByteOrder.nativeOrder()).asFloatBuffer();
   	 	mFaceVertices.put(verticesData).position(0);
   	 
   	 	mFaceTextureCoordinates = ByteBuffer.allocateDirect(faceTextureCoordinate.length * BYTES_PER_FLOAT)
   	 		 .order(ByteOrder.nativeOrder()).asFloatBuffer();
   	 	mFaceTextureCoordinates.put(faceTextureCoordinate).position(0);
   	 	
   	 	final float[] verticesColor = {
	 			//face1
   	 		mColor[0], mColor[1], mColor[2], mColor[3], //a
   	 		mColor[0], mColor[1], mColor[2], mColor[3], //b
   			mColor[0], mColor[1], mColor[2], mColor[3], //c
 		 	
   			mColor[0], mColor[1], mColor[2], mColor[3], //b
   			mColor[0], mColor[1], mColor[2], mColor[3], //d
   			mColor[0], mColor[1], mColor[2], mColor[3], //c
 		 	
 		    //face2
   			mColor[0], mColor[1], mColor[2], mColor[3], //b
   			mColor[0], mColor[1], mColor[2], mColor[3], //e
   			mColor[0], mColor[1], mColor[2], mColor[3], //d
 		 	
   			mColor[0], mColor[1], mColor[2], mColor[3], //e
   			mColor[0], mColor[1], mColor[2], mColor[3], //f
   			mColor[0], mColor[1], mColor[2], mColor[3], //d
 		 	
 		 	//face3
   			mColor[0], mColor[1], mColor[2], mColor[3], //e
   			mColor[0], mColor[1], mColor[2], mColor[3], //g
   			mColor[0], mColor[1], mColor[2], mColor[3], //f
 		 	
   			mColor[0], mColor[1], mColor[2], mColor[3], //g
   			mColor[0], mColor[1], mColor[2], mColor[3], //h
   			mColor[0], mColor[1], mColor[2], mColor[3], //f
 		 	
 		 	//face4
   			mColor[0], mColor[1], mColor[2], mColor[3], //g
   			mColor[0], mColor[1], mColor[2], mColor[3], //i
   			mColor[0], mColor[1], mColor[2], mColor[3], //h
 		 	
   			mColor[0], mColor[1], mColor[2], mColor[3], //i
   			mColor[0], mColor[1], mColor[2], mColor[3], //j
   			mColor[0], mColor[1], mColor[2], mColor[3], //h
 		 	
 		 	//face5
   			mColor[0], mColor[1], mColor[2], mColor[3], //i
   			mColor[0], mColor[1], mColor[2], mColor[3], //k
   			mColor[0], mColor[1], mColor[2], mColor[3], //j
 		 	
   			mColor[0], mColor[1], mColor[2], mColor[3], //k
   			mColor[0], mColor[1], mColor[2], mColor[3], //l
   			mColor[0], mColor[1], mColor[2], mColor[3], //j
 		 	
 		 	//face6
   			mColor[0], mColor[1], mColor[2], mColor[3], //k
   			mColor[0], mColor[1], mColor[2], mColor[3], //m
   			mColor[0], mColor[1], mColor[2], mColor[3], //l
 		 	
   			mColor[0], mColor[1], mColor[2], mColor[3], //m
   			mColor[0], mColor[1], mColor[2], mColor[3], //n
   			mColor[0], mColor[1], mColor[2], mColor[3]  //l
	 	};
   	 	//mFaceColors = ByteBuffer.allocateDirect(verticesColor.length * BYTES_PER_FLOAT)
       //      .order(ByteOrder.nativeOrder()).asFloatBuffer();
   	 	//mFaceColors.put(verticesColor).position(0);
    }
	
	private void drawFaces()
    {
		mFaceVertices.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE, mFaceVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        
        //mFaceColors.position(0);
        //GLES20.glVertexAttribPointer(mColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, 0, mFaceColors);
        //GLES20.glEnableVertexAttribArray(mColorHandle);
        
        mFaceTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE, 
                GLES20.GL_FLOAT, false, 0, mFaceTextureCoordinates);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, COUNT);
    }
	
	public void loadTexture(Bitmap bitmap)
    {
		if(0 == mTextureDataHandle)
		{
			mTextureDataHandle = ToolsUtil.loadTexture(mContext, bitmap);
   	 	}
    }
    
	public void releaseTexture()
	{
		mTextureDataHandle = 0;
		//mX = 0f;
		//mY = 0f;
		//mZ = 0f;
		//mUpTranslate = 0;
	}
	
    public void switchMode()
    {
    	mLeft = !mLeft;
    }

    private float mMaxTranslate = 0f;
    
	public void setTranslatePercent(float mTranslatePercent) {
		// TODO Auto-generated method stub
		mLeft = true;
		mMaxTranslate = mTranslatePercent * mHeight;
		mUpTranslate = mMaxTranslate;
		mUpUnit = mUpTranslate / FREQUENCY;
	}
	
}