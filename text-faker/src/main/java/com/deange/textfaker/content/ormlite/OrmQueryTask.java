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
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class OrmQueryTask<Result extends BaseModel> extends OrmBaseTask<QueryBuilder<Result,
		Long>, List<Result>> {

	private static final String TAG = OrmQueryTask.class.getSimpleName();

	final Callback<Result> mCallback;
	final Context mContext;
	final Class<Result> mClazz;
	final ContentHelper mContent;

	public interface Callback<Result> {
		public void onQueryCompleted(final List<Result> itemCollection);
	}

	public OrmQueryTask(final Context context, final Callback<Result> callback,
	                    final Class<Result> clazz) {
		mContext = context;
		mClazz = clazz;
		mCallback = callback;
		mContent = ContentHelper.getInstance(mContext);
	}

	@Override
	protected List<Result> doInBackground(final QueryBuilder<Result, Long>... queryBuilder) {

		try {
			final QueryBuilder<Result, Long> query = queryBuilder[0];
			final List<Result> listItems = mContent.getDao(mClazz).query(query.prepare());
			return listItems;

		} catch (SQLException e) {
			Log.e(TAG, "Fatal error occurred.");
			return null;
		}

	}

	@Override
	protected void onPostExecute(List<Result> itemCollection) {
		if (mCallback != null) {
			mCallback.onQueryCompleted(itemCollection);
		}
	}

}
