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

package com.deange.textfaker.utils;

import android.view.View;

public final class ViewUtils {

	private ViewUtils() {
	}

	/**
	 * Convenience method to set visibility using boolean
	 *
	 * @param view    the view to be changed
	 * @param visible the state of the view's visibility
	 */
	public static void setVisibility(final View view, final boolean visible) {
		view.setVisibility((visible ? View.VISIBLE : View.GONE));
	}

}
