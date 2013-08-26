/*
 * Copyright 2013 Christian De Angelis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deange.textfaker.ui.layout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.QuickContactBadge;

import com.deange.textfaker.R;

public class QuickContactDivot extends QuickContactBadge {

	// Distance, in dips, from the corner of the image to the start of the divot.
	// Used for non-middle positions.  For middle positions this distance is basically
	// to the middle of edge.
	static final float CORNER_OFFSET = 12F;
	static final float WIDTH = 6F;
	static final float HEIGHT = 16F;

	// Where to draw the divot.  LEFT_UPPER, for example, means the upper edge but to the
	// left.  TOP_RIGHT means the right edge but to the top.
	public static final int LEFT_UPPER = 1;
	public static final int LEFT_MIDDLE = 2;
	public static final int LEFT_LOWER = 3;

	public static final int RIGHT_UPPER = 4;
	public static final int RIGHT_MIDDLE = 5;
	public static final int RIGHT_LOWER = 6;

	public static final int TOP_LEFT = 7;
	public static final int TOP_MIDDLE = 8;
	public static final int TOP_RIGHT = 9;

	public static final int BOTTOM_LEFT = 10;
	public static final int BOTTOM_MIDDLE = 11;
	public static final int BOTTOM_RIGHT = 12;

	static final String [] sPositionChoices = new String [] {
			"",
			"left_upper",
			"left_middle",
			"left_lower",

			"right_upper",
			"right_middle",
			"right_lower",

			"top_left",
			"top_middle",
			"top_right",

			"bottom_left",
			"bottom_middle",
			"bottom_right",
	};

	private Drawable mDrawable;
	private int mDrawableIntrinsicWidth;
	private int mDrawableIntrinsicHeight;
	private int mPosition;

	// The screen density.  Multiply this by dips to get pixels.
	private float mDensity;

	public QuickContactDivot(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(attrs);
	}

	public QuickContactDivot(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(attrs);
	}

	public QuickContactDivot(Context context) {
		super(context);
		initialize(null);
	}

	private void initialize(AttributeSet attrs) {
		if (attrs != null) {
			mPosition = attrs.getAttributeListValue(null, "position", sPositionChoices, -1);
		}

		Resources r = getContext().getResources();
		mDensity = r.getDisplayMetrics().density;

		setDrawable();
	}

	private void setDrawable() {
		Resources r = getContext().getResources();

		switch (mPosition) {
			case LEFT_UPPER:
			case LEFT_MIDDLE:
			case LEFT_LOWER:
				mDrawable = r.getDrawable(R.drawable.msg_bubble_right);
				break;

			case RIGHT_UPPER:
			case RIGHT_MIDDLE:
			case RIGHT_LOWER:
				mDrawable = r.getDrawable(R.drawable.msg_bubble_left);
				break;
		}
		mDrawableIntrinsicWidth = mDrawable.getIntrinsicWidth();
		mDrawableIntrinsicHeight = mDrawable.getIntrinsicHeight();
	}

	@Override
	public void onDraw(Canvas c) {
		super.onDraw(c);
		c.save();
		computeBounds(c);
		mDrawable.draw(c);
		c.restore();
	}

	public void setPosition(int position) {
		mPosition = position;
		setDrawable();
		invalidate();
	}

	public int getPosition() {
		return mPosition;
	}

	public float getCloseOffset() {
		return CORNER_OFFSET * mDensity;  // multiply by density to get pixels
	}

	public ImageView asImageView() {
		return this;
	}

	public void assignContactFromEmail(String emailAddress) {
		assignContactFromEmail(emailAddress, true);
	}

	public float getFarOffset() {
		return getCloseOffset() + mDrawableIntrinsicHeight;
	}

	private void computeBounds(Canvas c) {
		final int left = 0;
		final int top = 0;
		final int right = getWidth();
		final int middle = right / 2;
		final int bottom = getHeight();

		final int cornerOffset = (int) getCloseOffset();

		switch (mPosition) {
			case RIGHT_UPPER:
				mDrawable.setBounds(
						right - mDrawableIntrinsicWidth,
						cornerOffset,
						right,
						top + cornerOffset + mDrawableIntrinsicHeight);
				break;

			case LEFT_UPPER:
				mDrawable.setBounds(
						left,
						cornerOffset,
						mDrawableIntrinsicWidth,
						top + cornerOffset + mDrawableIntrinsicHeight);
				break;

			case BOTTOM_MIDDLE:
				int halfWidth = mDrawableIntrinsicWidth / 2;
				mDrawable.setBounds(
						(middle - halfWidth),
						(bottom - mDrawableIntrinsicHeight),
						(middle + halfWidth),
						(bottom));

				break;
		}
	}

}