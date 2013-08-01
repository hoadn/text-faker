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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.deange.textfaker.model.BaseModel;
import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;

public final class ContentHelper {
	private static ContentHelper sInstance = null;
	private final Context mContext;
	private final ContentResolver mResolver;
	private final DatabaseHelper mHelper;

	private ContentHelper(final Context context) {
		mContext = context;
		mResolver = mContext.getContentResolver();
		mHelper = DatabaseHelper.getInstance(context);
	}

	public synchronized static ContentHelper getInstance(final Context context) {
		if (sInstance == null) {
			sInstance = new ContentHelper(context);
		}
		return sInstance;
	}

	public synchronized ContentResolver getResolver() {
		return mResolver;
	}

	public synchronized <T extends BaseModel> Dao<T, Long> getDao(final Class<T> clazz) {
		return mHelper.getDaoEx(clazz);
	}

	public synchronized <T extends BaseModel> Cursor getCursor(final Class<T> clazz,
	                                                           final PreparedQuery<T> query)
			throws SQLException {
		final DatabaseConnection connection = (DatabaseConnection) mHelper.getConnectionSource();
		final AndroidCompiledStatement stmt = (AndroidCompiledStatement) query
				.compile(connection, StatementType.SELECT);
		return stmt.getCursor();
	}

	public DatabaseHelper getHelper() {
		return DatabaseHelper.getInstance(mContext);
	}
}