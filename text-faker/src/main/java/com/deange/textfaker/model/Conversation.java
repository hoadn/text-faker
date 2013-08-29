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

import com.deange.textfaker.utils.Formatter;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Conversation.TABLE)
public class Conversation extends BaseModel {

	public static final String TABLE = "conversation";
	public static final String UPDATED = "updated";
	public static final String NAME = "name";
	public static final String PHONENUMBER = "phone_number";

	@DatabaseField(columnName = UPDATED)
	private long mLastUpdated;

	@DatabaseField(columnName = NAME)
	private String mPersonName;

	@DatabaseField(columnName = PHONENUMBER)
	private String mPhoneNumber;

	public Conversation(final Cursor cursor) {
		mId = cursor.getLong(cursor.getColumnIndex(BaseModel.LOCAL_ID));
		mPersonName = cursor.getString(cursor.getColumnIndex(NAME));
		mPhoneNumber = cursor.getString(cursor.getColumnIndex(PHONENUMBER));
		mLastUpdated = cursor.getLong(cursor.getColumnIndex(UPDATED));
	}

	public Conversation(final String personName, final String phoneNumber, final long lastUpdated) {
		mPersonName = personName;
		mPhoneNumber = Formatter.formatPhoneNumber(phoneNumber);
		mLastUpdated = lastUpdated;
	}

	public Conversation() {
		// Needed by OrmLite
	}

	public String getName() {
		return mPersonName;
	}

	public String getNumber() {
		return mPhoneNumber;
	}

	public long getLastUpdated() {
		return mLastUpdated;
	}

	public void setName(final String name) {
		mPersonName = name;
	}

	public void setNumber(final String number) {
		mPhoneNumber = Formatter.formatPhoneNumber(number);
	}

	public void setLastUpdated(final long lastUpdated) {
		mLastUpdated = lastUpdated;
	}

}
