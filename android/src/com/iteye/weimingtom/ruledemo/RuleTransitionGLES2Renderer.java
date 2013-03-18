package com.iteye.weimingtom.ruledemo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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

/**
 * @see https://code.google.com/p/opengles-book-samples/source/browse/#svn%2Ftrunk%2FAndroid%2FCh10_MultiTexture
 * @see https://developer.android.com/training/graphics/opengl/environment.html
 * @see https://github.com/mattdesl/lwjgl-basics/wiki/ShaderLesson4
 * 
 * @author weimingtom
 *
 */
public class RuleTransitionGLES2Renderer implements GLSurfaceView.Renderer {
	private final static boolean D = true;
	private final static String TAG = "RuleTransitionGLES2Renderer";
	
	private static final int INCREMENT = 1;
	private static final int DELAY = 5;
	private long lastTick = 0;
	private int threadhold = 0;
	
    private static final int FLOAT_SIZE_BYTES = Float.SIZE / 8;
    private static final int SHORT_SIZE_BYTES = Short.SIZE / 8;
    private static final int VERTICES_DATA_POS_OFFSET = 0;
    private static final int VERTICES_DATA_POS_SIZE = 3;
    private static final int VERTICES_DATA_UV_OFFSET = 3;
    private static final int VERTICES_DATA_UV_SIZE = 2;
    private static final int VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    
	private final short[] mIndicesData = { 
		0, 1, 2, 0, 2, 3 
    };

    private final float[] mVerticesData = { 
        -1.0f,  1.0f, 0.0f, 0.0f, 0.0f,
        -1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
         1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
         1.0f,  1.0f, 0.0f, 1.0f, 0.0f
    };

	private final static String vShaderStr =
            "attribute vec4 a_position;\n" +
            "attribute vec2 a_texCoord;\n" +
            "varying vec2 v_texCoord;\n" +
            "void main()\n" +
            "{\n" +
            "   gl_Position = a_position;\n" +
            "   v_texCoord = a_texCoord;\n" +
            "}\n";
    
	private final static String fShaderStr =
            "precision mediump float;\n" +
            "varying vec2 v_texCoord;\n" +
            "uniform sampler2D s_baseMap;\n" +
            "uniform sampler2D s_maskMap;\n" +
            "uniform sampler2D s_bgMap;\n" +
            "uniform float u_color;\n" +
            "void main()\n" +
            "{\n" +
            "  vec4 baseColor;\n" +
            "  vec4 maskColor;\n" +
            "  vec4 bgColor;\n" +
            "\n" +
            "  baseColor = texture2D(s_baseMap, v_texCoord);\n" +
            "  maskColor = texture2D(s_maskMap, v_texCoord);\n" +
            "  bgColor = texture2D(s_bgMap, v_texCoord);\n" +
            "  gl_FragColor = mix(bgColor, baseColor, step(u_color, 1.0 - maskColor));\n" +
            "}\n";
    
	private int mProgramObject;
    private int mPositionLoc, mTexCoordLoc;
    private int mBaseMapLoc, mMaskMapLoc, mBgMapLoc;
    private int mColorLoc;
    private int mBaseMapTexId, mMaskMapTexId, mBgMapTexId;
    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;
    private ShortBuffer mIndices;
    private Context mContext;

    private float[] mProjMatrix = new float[16];
    
    public RuleTransitionGLES2Renderer(Context context) {
        mContext = context;
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * FLOAT_SIZE_BYTES)
        	.order(ByteOrder.nativeOrder())
        	.asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        mIndices = ByteBuffer.allocateDirect(mIndicesData.length * SHORT_SIZE_BYTES)
        	.order(ByteOrder.nativeOrder())
        	.asShortBuffer();
        mIndices.put(mIndicesData).position(0);
    }
    
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgramObject = loadProgram(vShaderStr, fShaderStr);
        mPositionLoc = GLES20.glGetAttribLocation(mProgramObject, "a_position");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramObject, "a_texCoord");
        mBaseMapLoc = GLES20.glGetUniformLocation(mProgramObject, "s_baseMap");
        mMaskMapLoc = GLES20.glGetUniformLocation(mProgramObject, "s_maskMap");
        mBgMapLoc = GLES20.glGetUniformLocation(mProgramObject, "s_bgMap");
        mColorLoc = GLES20.glGetUniformLocation(mProgramObject, "u_color");
        mBaseMapTexId = loadTexture2(R.drawable.bg1);
        mMaskMapTexId = loadTexture2(R.drawable.rule_1);
        mBgMapTexId = loadTexture2(R.drawable.bg2);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
        GLES20.glViewport(0, 0, mWidth, mHeight);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjMatrix, 0, ratio, -ratio, 1, -1, 3, 7);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
        final long ct = System.currentTimeMillis();
        final long time = ct - lastTick;
        if (time >= DELAY) {
        	lastTick = System.currentTimeMillis();
        	threadhold += INCREMENT;
        	if (threadhold > 255) {
        		threadhold = 0;
        	}
        }
		
	    GLES20.glViewport(0, 0, mWidth, mHeight);
	    GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgramObject);
        checkGlError("glUseProgram");
        
        mVertices.position(VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(mPositionLoc, 
        	VERTICES_DATA_POS_SIZE, GLES20.GL_FLOAT, false, 
        	VERTICES_DATA_STRIDE_BYTES, mVertices);
        checkGlError("glVertexAttribPointer mPositionLoc");
        GLES20.glEnableVertexAttribArray(mPositionLoc);
        checkGlError("glEnableVertexAttribArray mPositionLoc");
        
        mVertices.position(VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 
        	VERTICES_DATA_UV_SIZE, GLES20.GL_FLOAT, false, 
        	VERTICES_DATA_STRIDE_BYTES, mVertices);
        checkGlError("glVertexAttribPointer mTexCoordLoc");
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        checkGlError("glEnableVertexAttribArray mTexCoordLoc");
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBaseMapTexId);
        GLES20.glUniform1i(mBaseMapLoc, 0);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mMaskMapTexId);
        GLES20.glUniform1i(mMaskMapLoc, 1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBgMapTexId);
        GLES20.glUniform1i(mBgMapLoc, 2);
        
        float color = (float)threadhold / 255;
        GLES20.glUniform1f(mColorLoc, color);
        
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mIndices);
        checkGlError("glDrawArrays");
	}
	
	public static int loadShader(int type, String shaderSrc) {
		int shader = GLES20.glCreateShader(type);
		if (shader == 0) {
			return 0;
		}
		GLES20.glShaderSource(shader, shaderSrc);
		GLES20.glCompileShader(shader);
		int[] compiled = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			if (D) {
				Log.e(TAG, "Could not compile shader " + type + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
			}
			GLES20.glDeleteShader(shader);
			shader = 0;
			return 0;
		}
		return shader;
	}

	public static int loadProgram(String vertShaderSrc, String fragShaderSrc) {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertShaderSrc);
		if (vertexShader == 0) {
			return 0;
		}
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragShaderSrc);
		if (fragmentShader == 0) {
			GLES20.glDeleteShader(vertexShader);
			return 0;
		}
		int programObject = GLES20.glCreateProgram();
		if (programObject == 0) {
			return 0;
		}
		GLES20.glAttachShader(programObject, vertexShader);
		checkGlError("glAttachShader");
		GLES20.glAttachShader(programObject, fragmentShader);
		checkGlError("glAttachShader");
		GLES20.glLinkProgram(programObject);
		int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(programObject, GLES20.GL_LINK_STATUS, linkStatus, 0);
		if (linkStatus[0] != GLES20.GL_TRUE) {
			if (D) {
				Log.e(TAG, "Error linking program:");
				Log.e(TAG, GLES20.glGetProgramInfoLog(programObject));
			}
			GLES20.glDeleteProgram(programObject);
			return 0;
		}
		GLES20.glDeleteShader(vertexShader);
		GLES20.glDeleteShader(fragmentShader);
		return programObject;
	}

    private int loadTexture(int drawableId) {
    	Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId);
        if (bitmap != null) {
	    	int[] textureId = new int[1];
	        byte[] buffer = new byte[bitmap.getWidth() * bitmap.getHeight() * 3];
	        for (int y = 0; y < bitmap.getHeight(); y++) {
	            for (int x = 0; x < bitmap.getWidth(); x++) {
	            	int pixel = bitmap.getPixel(x, y);
	                buffer[(y * bitmap.getWidth() + x) * 3 + 0] = (byte)((pixel >> 16) & 0xFF);
	                buffer[(y * bitmap.getWidth() + x) * 3 + 1] = (byte)((pixel >> 8) & 0xFF);
	                buffer[(y * bitmap.getWidth() + x) * 3 + 2] = (byte)((pixel >> 0) & 0xFF);
	            }
	        }
	        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 3);
	        byteBuffer.put(buffer).position(0);
	        GLES20.glGenTextures(1, textureId, 0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
	        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, byteBuffer);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);   
	        bitmap.recycle();
	        return textureId[0];
	    } else {
	    	return -1;
	    }
    }
    
    private int loadTexture2(int drawableId) {
    	Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId);
        if (bitmap != null) {
		    int[] textures = new int[1];
	        GLES20.glGenTextures(1, textures, 0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	        bitmap.recycle();
	    	return textures[0];
        } else {
        	return -1;
        }
    }
    
    private static void checkGlError(String op) {
        if (D) {
	    	int error;
	        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
	            Log.e(TAG, op + ": glError " + error);
	            throw new RuntimeException(op + ": glError " + error);
	        }
        }
    }
}
