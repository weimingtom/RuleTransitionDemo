package com.iteye.weimingtom.ruledemo;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class RuleTransitionView extends View {
	private static final int DELAY = 5;
	private static final AvoidXfermode mode1 = new AvoidXfermode(Color.WHITE, 0, AvoidXfermode.Mode.TARGET);
	private static final PorterDuffXfermode mode2 = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

	private Paint paint;
	private long lastTick = 0;
	private int threadhold = 0;
	private Bitmap bg1, bg2, rule, mask;
	private int bgWidth, bgHeight;
	private float textHeight;
	private String notifyText = "threadhold : ";
	
	public RuleTransitionView(Context context) {
		super(context);
		init(context);
	}	
	
	public RuleTransitionView(Context context, AttributeSet attrs) {
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
		rule = BitmapFactory.decodeResource(context.getResources(), R.drawable.rule);
		mask = makeMask(bgWidth, bgHeight);
        
		paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setTextSize(32);
        paint.setColor(Color.RED);
        
        Rect bounds = new Rect();
        paint.getTextBounds(notifyText, 0, notifyText.length(), bounds);
        textHeight = bounds.height();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
        long time = System.currentTimeMillis() - lastTick;
        if (time >= DELAY) {
        	lastTick = System.currentTimeMillis();
        	threadhold++;
        	if (threadhold > 255) {
        		threadhold = 0;
        	}
        }
        
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bg2, 0, 0, paint);
        int sc = canvas.saveLayer(0, 0, bgWidth, bgHeight, null, Canvas.ALL_SAVE_FLAG);
        
        int add = (threadhold << 16) | (threadhold << 8) | (threadhold);
        paint.setColorFilter(new LightingColorFilter(0xFFFFFFFF, add));
        canvas.drawBitmap(rule, 0, 0, paint);
        paint.setColorFilter(null);
        
        paint.setXfermode(mode1);
        canvas.drawBitmap(mask, 0, 0, paint);
        
        paint.setXfermode(mode2);
        canvas.drawBitmap(bg1, 0, 0, paint);
        
        paint.setXfermode(null);
        
        canvas.drawText(notifyText + this.threadhold, 0, textHeight, paint);
        canvas.restoreToCount(sc);   
        
        invalidate();
	}
    
    private static Bitmap makeMask(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        c.drawColor(Color.TRANSPARENT);
        return bm;
    }
}
