package com.lvrenyang.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.view.View.MeasureSpec;

public class UIUtils {
	public static Bitmap ViewToBitmap(View v) {
		v.setDrawingCacheEnabled(true);
		v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

		//v.layout(v.getLeft(), v.getTop(), v.getLeft() + v.getWidth(),
		//		v.getTop() + v.getHeight());
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		v.buildDrawingCache(true);

		Bitmap bitmap = v.getDrawingCache();
		if (null != bitmap) {
			bitmap = Bitmap.createBitmap(bitmap);
		}
		v.setDrawingCacheEnabled(false);
		return bitmap;
	}

}
