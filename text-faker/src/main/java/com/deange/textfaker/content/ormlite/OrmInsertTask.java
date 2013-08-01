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

package com.deange.textfaker.content.ormlite;

import android.content.Context;
import android.util.Log;

import com.deange.textfaker.content.ContentHelper;
import com.deange.textfaker.model.BaseModel;

import java.sql.SQLException;


public class OrmInsertTask<T extends BaseModel> extends OrmBaseTask<T, Integer> {

	protected static final String TAG = OrmInsertTask.class.getSimpleName();

	final Callback mCallback;
	final Context mContext;
	final Class<T> mClazz;
	final ContentHelper mContent;

	public interface Callback {
		public void onInsertCompleted(final int rowsInserted);
	}

	public OrmInsertTask(final Context context, final Callback callback, final Class<T> clazz) {
		mContext = context;
		mCallback = callback;
		mClazz = clazz;
		mContent = ContentHelper.getInstance(mContext);
	}

	@Override
	protected Integer doInBackground(final T... items) {

		try {
			int rowsInserted = 0;
			for (final T item : items) {
				rowsInserted += mContent.getDao(mClazz).create(item);
			}

			return rowsInserted;

		} catch (SQLException e) {
			Log.e(TAG, "Fatal error occurred.");
			return 0;
		}

	}

	@Override
	protected void onPostExecute(final Integer rowsInserted) {
		if (mCallback != null) {
			mCallback.onInsertCompleted(rowsInserted);
		}
	}

}
