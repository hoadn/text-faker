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

package com.deange.textfaker.model;

import android.database.Cursor;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Conversation.TABLE)
public class Conversation extends BaseModel {

	public static final String TABLE = "conversation";
	public static final String PERSON = "person";
	public static final String UPDATED = "updated";

	@DatabaseField(columnName = PERSON)
	private long mPersonId;

	@DatabaseField(columnName = UPDATED)
	private long mLastUpdated;

	private Conversation(final long personId, final long lastUpdated) {
		mPersonId = personId;
		mLastUpdated = lastUpdated;
	}

	public Conversation() {
		// Needed by OrmLite
	}

	public static Conversation createInstance(final long personId, final long lastUpdated) {
		return new Conversation(personId, lastUpdated);
	}

	public static Conversation createInstance(final Cursor cursor) {
		final long id = cursor.getLong(cursor.getColumnIndex(BaseModel.LOCAL_ID));
		final long personId = cursor.getLong(cursor.getColumnIndex(PERSON));
		final long lastUpdated = cursor.getLong(cursor.getColumnIndex(UPDATED));

		final Conversation conversation = new Conversation(personId, lastUpdated);
		conversation.setId(id);

		return conversation;
	}

	public long getPersonId() {
		return mPersonId;
	}

	public void setPersonId(final long personId) {
		mPersonId = personId;
	}

	public long getLastUpdated() {
		return mLastUpdated;
	}

	public void setLastUpdated(final long lastUpdated) {
		mLastUpdated = lastUpdated;
	}

}
