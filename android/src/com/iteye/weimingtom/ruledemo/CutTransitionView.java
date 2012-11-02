package com.iteye.weimingtom.ruledemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class CutTransitionView extends View {
	private static final int DELAY = 5;
	private static final boolean SHOW_THREADHOLD = true;
	private static final int INCREMENT = 1;
	private static final int TEXT_SIZE = 16;
	private static final String THREADHOLD_TEXT = "Threadhold:";
	private static final float BLUR_RADIUS = 10f;
	private static final boolean USE_BLUR = true;
	
	private Paint paint;
	private long lastTick = 0;
	private int threadhold = 0;
	private Bitmap bg1, bg2;
	private int bgWidth, bgHeight;
	
	private Rect srcRect;
	private Rect dstRect;
	BlurMaskFilter maskfilter;
	
	public CutTransitionView(Context context) {
		super(context);
		init(context);
	}	
	
	public CutTransitionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		setFocusable(true);
		requestFocus();
		bg1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg1);
		bg2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg2);
		bgWidth = bg1.getWidth();
		bgHeight = bg1.getHeight();
		paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setTextSize(TEXT_SIZE);
        paint.setColor(Color.RED);
        
    	srcRect = new Rect();
    	dstRect = new Rect();
    	if (USE_BLUR) {
    		maskfilter = new BlurMaskFilter(BLUR_RADIUS, BlurMaskFilter.Blur.OUTER);
    	}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
        final long ct = System.currentTimeMillis();
        final long time = ct - lastTick;
        if (time >= DELAY) {
        	lastTick = System.currentTimeMillis();
        	threadhold += INCREMENT;
        	if (threadhold > 255) {
        		threadhold = 0;
        	}
        }
        canvas.drawColor(Color.WHITE);
        draw1(canvas);
        if (SHOW_THREADHOLD) {
        	canvas.drawText(THREADHOLD_TEXT + this.threadhold, 0, -paint.ascent(), paint);
        }
        final long drawTime = System.currentTimeMillis() - ct;
        final long realDelay = DELAY > drawTime ? DELAY - drawTime : 0;
        postInvalidateDelayed(realDelay);
	}
	
	private void draw1(Canvas canvas) {
		srcRect.set(0, 0, (int)(bgWidth / 255.0 * threadhold), bgHeight);
		dstRect.set(srcRect);
		canvas.drawBitmap(bg1, 0, 0, paint);
		if (USE_BLUR) { 
			paint.setMaskFilter(maskfilter);
		}
		canvas.drawBitmap(bg2, srcRect, dstRect, paint);
		if (USE_BLUR) { 
			paint.setMaskFilter(null);
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
}
