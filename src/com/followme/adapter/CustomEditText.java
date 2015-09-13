package com.followme.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomEditText extends EditText{

	 public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	        init();
	    }

	    public CustomEditText(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        init();
	    }

	    public CustomEditText(Context context) {
	        super(context);
	        init();
	    }

	    private void init() {
	        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
	                                               "fonts/sneakerhead.ttf");
	        setTypeface(tf);
	    }

}
