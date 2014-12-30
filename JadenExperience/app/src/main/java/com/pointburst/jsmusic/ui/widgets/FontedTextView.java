package com.pointburst.jsmusic.ui.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by kshitij on 1/8/14.
 */
public class FontedTextView extends TextView {
	public FontedTextView(Context context) {
		super(context);
		init();
	}

	public FontedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FontedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas.ttf");
		setTypeface(tf ,1);

	}

	@Override
	public boolean isInEditMode() {
		return true;
	}
}
