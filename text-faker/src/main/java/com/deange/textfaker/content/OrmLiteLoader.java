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

package com.deange.textfaker.content;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.deange.textfaker.model.BaseModel;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public class OrmLiteLoader<T extends BaseModel> extends AsyncTaskLoader<Cursor> {

	final QueryBuilder<T, Long> mQueryBuilder;
	final Class<T> mClazz;

	public OrmLiteLoader(final Context context, final Class<T> clazz, final QueryBuilder<T, Long> queryBuilder) {
		super(context);
		mClazz = clazz;
		mQueryBuilder = queryBuilder;
	}

	@Override
	protected void onStartLoading() {
		forceLoad();
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	public Cursor loadInBackground() {

		Cursor cursor = null;
		CloseableIterator<T> iterator = null;

		if ((mQueryBuilder != null) && (mClazz != null)) {
			try {
				final Dao<T, Long> dao = ContentHelper.getInstance(getContext()).getDao(mClazz);

				iterator = dao.iterator(mQueryBuilder.prepare());
				final AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
				cursor = results.getRawCursor();

			} catch (final SQLException e) {

			}
		}

		return cursor;
	}

}
