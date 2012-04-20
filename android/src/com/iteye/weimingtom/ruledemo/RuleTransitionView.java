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
import android.util.AttributeSet;
import android.view.View;

public class RuleTransitionView extends View {
	private static final int DELAY = 5;
	private static final boolean SHOW_THREADHOLD = true;
	private static final int INCREMENT = 1;
	private static final int TEXT_SIZE = 16;
	private static final String THREADHOLD_TEXT = "Threadhold:";
	private static final int RULE_ID = R.drawable.rule;
	
	private static final int RULE = 0;
	private static final int RULE_TEMP_BITMAP = 1;
	private int transitionType = RULE;

	private AvoidXfermode mode1;
	private PorterDuffXfermode mode2;
	private LightingColorFilter[] filters;	
	private Paint paint;
	private long lastTick = 0;
	private int threadhold = 0;
	private Bitmap bg1, bg2, rule, mask, bgTemp;
	private Canvas bgTempCanvas;
	private int bgWidth, bgHeight;
	
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
		paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setTextSize(TEXT_SIZE);
        paint.setColor(Color.RED);
        
		if (transitionType == RULE_TEMP_BITMAP) {
	        bgTemp = makeMask(bgWidth, bgHeight);
	        bgTempCanvas = new Canvas(bgTemp);
		}
		rule = BitmapFactory.decodeResource(context.getResources(), RULE_ID);
		mask = makeMask(bgWidth, bgHeight);
        mode1 = new AvoidXfermode(Color.WHITE, 0, AvoidXfermode.Mode.TARGET);
        mode2 = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        filters = new LightingColorFilter[256];
        for (int i = 0; i < 256; i++) {
        	filters[i] = new LightingColorFilter(0xFFFFFFFF, (i << 16) | (i << 8) | (i));
        }
    }
	
	@Override
	public void onDraw(Canvas canvas) {
        long time = System.currentTimeMillis() - lastTick;
        if (time >= DELAY) {
        	lastTick = System.currentTimeMillis();
        	threadhold += INCREMENT;
        	if (threadhold > 255) {
        		threadhold = 0;
        	}
        }
        
        canvas.drawColor(Color.WHITE);
        
        if (transitionType == RULE) {
        	draw1(canvas);
        } else if (transitionType == RULE_TEMP_BITMAP) {
        	draw2(canvas);
        }
        
        if (SHOW_THREADHOLD) {
        	canvas.drawText(THREADHOLD_TEXT + this.threadhold, 0, -paint.ascent(), paint);
        }
        postInvalidateDelayed(DELAY);
	}
    
	private void draw1(Canvas canvas) {
        canvas.drawBitmap(bg2, 0, 0, paint);
        int sc = canvas.saveLayer(0, 0, bgWidth, bgHeight, null, Canvas.ALL_SAVE_FLAG);
        
        paint.setColorFilter(filters[threadhold]);
        canvas.drawBitmap(rule, 0, 0, paint);
        paint.setColorFilter(null);
        
        paint.setXfermode(mode1);
        canvas.drawBitmap(mask, 0, 0, paint);
        
        paint.setXfermode(mode2);
        canvas.drawBitmap(bg1, 0, 0, paint);
        
        paint.setXfermode(null);
        canvas.restoreToCount(sc);
	}

	private void draw2(Canvas canvas) {
		bgTempCanvas.drawColor(Color.TRANSPARENT);
        paint.setColorFilter(filters[threadhold]);
        bgTempCanvas.drawBitmap(rule, 0, 0, paint);
        paint.setColorFilter(null);
        
        paint.setXfermode(mode1);
        bgTempCanvas.drawBitmap(mask, 0, 0, paint);
        
        paint.setXfermode(mode2);
        bgTempCanvas.drawBitmap(bg1, 0, 0, paint);
        
        paint.setXfermode(null);
        canvas.drawBitmap(bg2, 0, 0, paint);
        canvas.drawBitmap(bgTemp, 0, 0, paint);
	}
	
    private static Bitmap makeMask(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        c.drawColor(Color.TRANSPARENT);
        return bm;
    }

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		bg1.recycle();
		bg2.recycle();
		rule.recycle();
		mask.recycle();
		if (transitionType == RULE_TEMP_BITMAP) {
			bgTemp.recycle();
		}
	}
}
