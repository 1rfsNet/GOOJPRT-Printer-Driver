package com.lvrenyang.labelview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

// 标签页布局，继承自RelativeLayout，重写onDraw
public class LabelLayout extends RelativeLayout {

	Paint paint = new Paint();

	public LabelLayout(Context context) {
		super(context);
	}

	public LabelLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public LabelLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int w = 5;

		// 给当前正在操作的组件，添加一个草绿色边框
		View v = this.findFocus();
		if ((null != v) && (this.getId() != v.getId())) {

			paint.setColor(0xFF99CC33);
			canvas.drawRect(v.getLeft() - w, v.getTop() - w, v.getRight() + w,
					v.getBottom() + w, paint);
		}

		super.onDraw(canvas);

	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

}
