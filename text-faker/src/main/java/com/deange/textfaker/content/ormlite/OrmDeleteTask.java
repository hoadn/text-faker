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
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;

public class OrmDeleteTask<T extends BaseModel> extends OrmBaseTask<Object, Integer> {

	protected static final String TAG = OrmDeleteTask.class.getSimpleName();
	final Callback mCallback;
	final Context mContext;
	final Class<T> mClazz;
	final ContentHelper mContent;

	public OrmDeleteTask(final Context context, final Callback callback, final Class<T> clazz) {
		mContext = context;
		mCallback = callback;
		mClazz = clazz;
		mContent = ContentHelper.getInstance(mContext);
	}

	protected Integer delete(final DeleteBuilder<T, Long>... items) throws SQLException {
		final DeleteBuilder<T, Long> item = items[0];
		final int rowsDeleted = mContent.getDao(mClazz).delete(item.prepare());
		return rowsDeleted;
	}

	protected Integer delete(final T... items) throws SQLException {
		final T item = items[0];
		final int rowsDeleted = mContent.getDao(mClazz).delete(item);
		return rowsDeleted;
	}

	@Override
	protected Integer doInBackground(final Object... items) {

		final Object item = items[0];
		int rowsDeleted = 0;

		try {

			if (item instanceof DeleteBuilder) {
				rowsDeleted = delete((DeleteBuilder) item);

			} else if (item instanceof BaseModel) {
				// This is of type <T extends BaseModel>
				// noinspection unchecked
				rowsDeleted = delete((T) item);
			}

		} catch (Exception e) {
			Log.e(TAG, "Fatal error occurred.");
			e.printStackTrace();
			return 0;
		}

		return rowsDeleted;
	}

	@Override
	protected void onPostExecute(final Integer rowsDeleted) {
		if (mCallback != null) {
			mCallback.onDeleteCompleted(rowsDeleted);
		}
	}

	public interface Callback {

		public void onDeleteCompleted(final int rowsDeleted);
	}

}
