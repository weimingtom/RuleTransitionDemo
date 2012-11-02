package com.jacp.tone.view;  

import java.util.ArrayList;  
  
import android.content.Context;  
import android.graphics.Bitmap;  
import android.graphics.Canvas;  
import android.graphics.ColorMatrix;  
import android.graphics.ColorMatrixColorFilter;  
import android.graphics.Paint;  
import android.view.Gravity;  
import android.view.View;  
import android.widget.LinearLayout;  
import android.widget.SeekBar;  
import android.widget.SeekBar.OnSeekBarChangeListener;  
import android.widget.TextView;  

/** 
 * @author maylian7700@126.com 
 */  
public class ToneLayer {  
    public static final int FLAG_SATURATION = 0x0;
    public static final int FLAG_LUM = 0x1; 
    public static final int FLAG_HUE = 0x2;
    
    private TextView mSaturation;  
    private SeekBar mSaturationBar;
    private TextView mHue;  
    private SeekBar mHueBar;
    private TextView mLum;  
    private SeekBar mLumBar;  
  
    private float mDensity;  
    private static final int TEXT_WIDTH = 50;  
  
    private LinearLayout mParent;  
  
    private ColorMatrix mLightnessMatrix;  
    private ColorMatrix mSaturationMatrix;  
    private ColorMatrix mHueMatrix;  
    private ColorMatrix mAllMatrix;  

    private float mLumValue = 1F;  
    private float mSaturationValue = 0F;  
    private float mHueValue = 0F;  
    
    private static final int MIDDLE_VALUE = 127;  
    private static final int MAX_VALUE = 255;
    private ArrayList<SeekBar> mSeekBars = new ArrayList<SeekBar>();  
  
    public ToneLayer(Context context) {  
        init(context);  
    }
    
    private void init(Context context) {  
        mDensity = context.getResources().getDisplayMetrics().density;  
        mSaturation = new TextView(context);  
        mSaturation.setText("saturation:");  
        mHue = new TextView(context);
        mHue.setText("hue:");  
        mLum = new TextView(context);  
        mLum.setText("lum:");  
        
        mSaturationBar = new SeekBar(context);  
        mHueBar = new SeekBar(context);  
        mLumBar = new SeekBar(context);  
          
        mSeekBars.add(mSaturationBar);  
        mSeekBars.add(mHueBar);  
        mSeekBars.add(mLumBar);  
          
        for (int i = 0, size = mSeekBars.size(); i < size; i++) {  
            SeekBar seekBar = mSeekBars.get(i);  
            seekBar.setMax(MAX_VALUE);  
            seekBar.setProgress(MIDDLE_VALUE);  
            seekBar.setTag(i);  
        }  
  
        LinearLayout saturation = new LinearLayout(context);  
        saturation.setOrientation(LinearLayout.HORIZONTAL);  
        saturation.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));  
  
        LinearLayout.LayoutParams txtLayoutparams = new LinearLayout.LayoutParams((int) (TEXT_WIDTH * mDensity), LinearLayout.LayoutParams.MATCH_PARENT);  
        mSaturation.setGravity(Gravity.CENTER);  
        saturation.addView(mSaturation, txtLayoutparams);  
  
        LinearLayout.LayoutParams seekLayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
        saturation.addView(mSaturationBar, seekLayoutparams);  
  
        LinearLayout hue = new LinearLayout(context);  
        hue.setOrientation(LinearLayout.HORIZONTAL);  
        hue.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));  
  
        mHue.setGravity(Gravity.CENTER);  
        hue.addView(mHue, txtLayoutparams);  
        hue.addView(mHueBar, seekLayoutparams);  
  
        LinearLayout lum = new LinearLayout(context);  
        lum.setOrientation(LinearLayout.HORIZONTAL);  
        lum.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));  
  
        mLum.setGravity(Gravity.CENTER);  
        lum.addView(mLum, txtLayoutparams);  
        lum.addView(mLumBar, seekLayoutparams);  
  
        mParent = new LinearLayout(context);  
        mParent.setOrientation(LinearLayout.VERTICAL);  
        mParent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));  
        mParent.addView(saturation);  
        mParent.addView(hue);  
        mParent.addView(lum);  
    }  
  
    public View getParentView() {  
        return mParent;  
    }
    
    public void setSaturation(int saturation) {  
        mSaturationValue = saturation * 1.0F / MIDDLE_VALUE;  
    }  
    
    public void setHue(int hue) {  
        mHueValue = hue * 1.0F / MIDDLE_VALUE;  
    }  
    
    public void setLum(int lum) {  
        mLumValue = (lum - MIDDLE_VALUE) * 1.0F / MIDDLE_VALUE * 180;  
    }  
    
    public ArrayList<SeekBar> getSeekBars() {  
        return mSeekBars;  
    }  
    
    public Bitmap handleImage(Bitmap bm, int flag) {  
        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),  
                Bitmap.Config.ARGB_8888);  
        Canvas canvas = new Canvas(bmp);  
        Paint paint = new Paint();  
        paint.setAntiAlias(true);  
        if (null == mAllMatrix) {  
            mAllMatrix = new ColorMatrix();  
        }
        if (null == mLightnessMatrix) {  
            mLightnessMatrix = new ColorMatrix();
        }
        if (null == mSaturationMatrix) {  
            mSaturationMatrix = new ColorMatrix();  
        }
        if (null == mHueMatrix) {  
            mHueMatrix = new ColorMatrix();  
        }
        switch (flag) {  
        case FLAG_HUE:
            mHueMatrix.reset();  
            mHueMatrix.setScale(mHueValue, mHueValue, mHueValue, 1);
            break;
            
        case FLAG_SATURATION:
            mSaturationMatrix.reset();  
            mSaturationMatrix.setSaturation(mSaturationValue);  
            break;  
        
        case FLAG_LUM:  
            mLightnessMatrix.reset();
            mLightnessMatrix.setRotate(0, mLumValue);  
            mLightnessMatrix.setRotate(1, mLumValue);  
            mLightnessMatrix.setRotate(2, mLumValue);
            break;  
        }
        mAllMatrix.reset();
        mAllMatrix.postConcat(mHueMatrix);  
        mAllMatrix.postConcat(mSaturationMatrix);
        mAllMatrix.postConcat(mLightnessMatrix);
        paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));  
        canvas.drawBitmap(bm, 0, 0, paint);
        return bmp;  
    }
}
