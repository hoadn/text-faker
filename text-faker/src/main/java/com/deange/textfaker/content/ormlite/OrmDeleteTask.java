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

public class OrmDeleteTask<T extends BaseModel> extends OrmBaseTask<DeleteBuilder<T, Long>,
		Integer> {

	protected static final String TAG = OrmDeleteTask.class.getSimpleName();

	final Callback mCallback;
	final Context mContext;
	final Class<T> mClazz;
	final ContentHelper mContent;

	public interface Callback {
		public void onDeleteCompleted(final int rowsDeleted);
	}

	public OrmDeleteTask(final Context context, final Callback callback, final Class<T> clazz) {
		mContext = context;
		mCallback = callback;
		mClazz = clazz;
		mContent = ContentHelper.getInstance(mContext);
	}

	@Override
	protected Integer doInBackground(final DeleteBuilder<T, Long>... deleteBuilder) {

		try {
			final DeleteBuilder<T, Long> delete = deleteBuilder[0];
			final int rowsDeleted = mContent.getDao(mClazz).delete(delete.prepare());
			return rowsDeleted;

		} catch (SQLException e) {
			Log.e(TAG, "Fatal error occurred.");
			return 0;
		}

	}

	@Override
	protected void onPostExecute(final Integer rowsDeleted) {
		if (mCallback != null) {
			mCallback.onDeleteCompleted(rowsDeleted);
		}
	}

}
