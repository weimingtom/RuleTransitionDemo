package com.iteye.weimingtom.ruledemo;

import android.app.Activity;
import android.graphics.BlurMaskFilter;
import android.graphics.EmbossMaskFilter;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.MaskFilterSpan;
import android.text.style.ScaleXSpan;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

/**
 * @see http://d.hatena.ne.jp/androidprogram/20100530/1275168217
 * @author Administrator
 *
 */
public class TextViewTest extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView tv = new TextView(this);
        tv.setTextSize(40.0f);
        
        BlurMaskFilter filter1 = new BlurMaskFilter(3.0f, BlurMaskFilter.Blur.INNER);
        BlurMaskFilter filter2 = new BlurMaskFilter(3.0f, BlurMaskFilter.Blur.NORMAL);
        BlurMaskFilter filter3 = new BlurMaskFilter(3.0f, BlurMaskFilter.Blur.OUTER);
        BlurMaskFilter filter4 = new BlurMaskFilter(3.0f, BlurMaskFilter.Blur.SOLID);
        
        MaskFilterSpan span1 = new MaskFilterSpan(filter1);
        MaskFilterSpan span2 = new MaskFilterSpan(filter2);
        MaskFilterSpan span3 = new MaskFilterSpan(filter3);
        MaskFilterSpan span4 = new MaskFilterSpan(filter4);
        
        SpannableString spannable1 = new SpannableString("INNER");
        SpannableString spannable2 = new SpannableString("NORMAL");
        SpannableString spannable3 = new SpannableString("OUTER");
        SpannableString spannable4 = new SpannableString("SOLID");
        
        spannable1.setSpan(span1, 0, spannable1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable2.setSpan(span2, 0, spannable2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable3.setSpan(span3, 0, spannable3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable4.setSpan(span4, 0, spannable4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        SpannableStringBuilder spannable = new SpannableStringBuilder();
        
        spannable.append(spannable1);
        spannable.append("\n");
        spannable.append(spannable2);
        spannable.append("\n");
        spannable.append(spannable3);
        spannable.append("\n");
        spannable.append(spannable4);
        
        //
        float ambient = 0.5f;
        float specular = 9.0f;
        float blurRadius = 3.0f;
        float[] direction = { 2.0f, 2.0f, 2.0f };
        EmbossMaskFilter filter = new EmbossMaskFilter(direction, ambient, specular, blurRadius);
        MaskFilterSpan span = new MaskFilterSpan(filter);
        SpannableString spannable5 = new SpannableString("Emboss");
        spannable5.setSpan(span, 0, spannable5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.append("\n");
        spannable.append(spannable5);
        
        //
        
        ScaleXSpan span6 = new ScaleXSpan(2.0f);
        SpannableString spannable6 = new SpannableString("Éì¤Ð¤·¤¿¤ê");
        spannable6.setSpan(span6, 0, spannable1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ScaleXSpan span7 = new ScaleXSpan(0.5f);
        SpannableString spannable7 = new SpannableString("¿s¤á¤¿¤ê");
        spannable7.setSpan(span7, 0, spannable7.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.append("ÎÄ×Ö¤ò");
        spannable.append(spannable6);
        spannable.append(spannable7);
        spannable.append("¤Ç¤­¤Þ¤¹¡£");
        
        tv.setText(spannable);
        setContentView(tv, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }
}
