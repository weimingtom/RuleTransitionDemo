package com.iteye.weimingtom.ruledemo;

import com.jacp.tone.ImageToneActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class RuleTransitionDemoActivity extends Activity {
	private int[] viewIds = {
		R.id.rule,
		R.id.cut,	
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitle();
        setContentView(R.layout.main);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        this.findViewById(R.id.buttonRule).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showView(R.id.rule);
			}
        });
        this.findViewById(R.id.buttonCut).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showView(R.id.cut);
			}
        });
        this.findViewById(R.id.buttonTone).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RuleTransitionDemoActivity.this, 
						ImageToneActivity.class));
			}
        });
        this.findViewById(R.id.buttonText1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RuleTransitionDemoActivity.this, 
						TextViewTest.class));
			}
        });        
    }
    
    public void hideTitle() {
    	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    
    public void showView(int id) {
    	for (int i = 0; i < viewIds.length; i++) {
    		if (viewIds[i] != id) {
    			this.findViewById(viewIds[i]).setVisibility(View.INVISIBLE);
    		} else {
    			this.findViewById(viewIds[i]).setVisibility(View.VISIBLE);
    		}
    	}
    }
}
