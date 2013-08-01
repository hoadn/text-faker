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
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.deange.textfaker.TFBuildConfig;
import com.deange.textfaker.model.BaseModel;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static final String TAG = DatabaseHelper.class.getSimpleName();
	private static DatabaseHelper sInstance = null;
	private static WeakReference<Callback> mCallbackRef = new WeakReference<Callback>(
			Fallback.INSTANCE);
	private final Class<? extends BaseModel>[] mBaseModels;
	private final Map<Class<? extends BaseModel>, Dao<? extends BaseModel, Long>> mDaos =
			new HashMap<Class<? extends BaseModel>, Dao<? extends BaseModel, Long>>();

	@SuppressWarnings("unchecked")
	private DatabaseHelper(final Context context, final String name, final int version) {
		super(context, name, null, version);
		mBaseModels = ContentType.MODELS;
	}

	public static void setDatabaseCallback(final Callback databaseCallback) {
		mCallbackRef = new WeakReference<Callback>(databaseCallback);
	}

	public static synchronized DatabaseHelper getInstance(final Context context) {
		if (sInstance == null) {
			sInstance = new DatabaseHelper(context, TFBuildConfig.DATABASE_NAME,
					TFBuildConfig.DATABASE_VERSION);
		}
		return sInstance;
	}

	@Override
	public void onCreate(final SQLiteDatabase db, final ConnectionSource connection) {
		createTables(db, connection);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
	                      int oldVersion, int newVersion) {
		Log.v(TAG, "onUpgrade()");

		final Callback databaseCallback = mCallbackRef.get();
		if (databaseCallback != null) {
			databaseCallback.onUpgrade(database, connectionSource, oldVersion, newVersion);
		}
	}

	@Override
	public void close() {
		mDaos.clear();

		sInstance = null;

		for (final Class<? extends BaseModel> clazz : mBaseModels) {
			try {
				TableUtils.clearTable(connectionSource, clazz);
			} catch (java.sql.SQLException e) {
				throw new SQLException(e.getMessage());
			}
		}

	}

	@SuppressWarnings("unchecked")
	public <T extends BaseModel> Dao<T, Long> getDaoEx(final Class<T> clazz) {
		Dao<T, Long> result = null;
		if (mDaos.containsKey(clazz)) {
			result = (Dao<T, Long>) mDaos.get(clazz);

		} else {
			try {
				result = getDao(clazz);
				mDaos.put(clazz, result);

			} catch (final java.sql.SQLException e) {
				throw new SQLException(e.getMessage());
			}
		}
		return result;
	}

	public void createTables(final SQLiteDatabase db, final ConnectionSource cs) {
		createTables(cs);
	}

	public void deleteTables(final SQLiteDatabase db, final ConnectionSource cs) {
		deleteTables(cs);
	}

	public void createTables(final ConnectionSource cs) {
		for (final Class<? extends BaseModel> clazz : mBaseModels) {
			createTable(clazz, cs);
		}
	}

	public void deleteTables(final ConnectionSource cs) {
		for (final Class<? extends BaseModel> clazz : mBaseModels) {
			dropTable(clazz, cs);
		}
	}

	public void createTable(final Class<? extends BaseModel> clazz, final ConnectionSource cs) {
		try {
			TableUtils.createTable(cs, clazz);
		} catch (final java.sql.SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}

	public void dropTable(final Class<? extends BaseModel> clazz, final ConnectionSource cs) {
		try {
			TableUtils.dropTable(cs, clazz, false);
		} catch (final java.sql.SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}

	public interface Callback {
		public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
		                      int oldVersion, int newVersion);
	}

	private final static class Fallback implements Callback {
		public static final Callback INSTANCE = new Fallback();

		@Override
		public void onUpgrade(SQLiteDatabase db, ConnectionSource connection, int oldVersion,
		                      int newVersion) {
			Log.w(TAG, "Fallback: onUpgrade()");
		}
	}
}
