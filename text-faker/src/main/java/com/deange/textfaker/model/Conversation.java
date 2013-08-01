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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.ByteArrayOutputStream;

@DatabaseTable(tableName = Conversation.TABLE)
public class Conversation extends BaseModel {

	public static final String TABLE = "conversation";
	public static final String PERSON = "person";
	public static final String AVATAR = "avatar";
	public static final String UPDATED = "updated";

	public static Conversation create(final String person, final long lastUpdated,
	                                  final Bitmap avatar) {
		return new Conversation(person, lastUpdated, avatar);
	}

	public static Conversation create(final Cursor cursor) {
		final long id = cursor.getLong(cursor.getColumnIndex(BaseModel.LOCAL_ID));
		final String person = cursor.getString(cursor.getColumnIndex(PERSON));
		final long lastUpdated = cursor.getLong(cursor.getColumnIndex(UPDATED));
		final byte[] avatarBytes = cursor.getBlob(cursor.getColumnIndex(AVATAR));

		Bitmap bitmap = null;
		if (avatarBytes != null) {
			bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
		}

		final Conversation conversation = new Conversation(person, lastUpdated, bitmap);
		conversation.setId(id);

		return conversation;
	}

	private Conversation(final String person, final long lastUpdated, final Bitmap avatar) {
		mPerson = person;
		mLastUpdated = lastUpdated;
		setAvatar(avatar);
	}

	public Conversation() {
		// Needed by OrmLite
	}

	@DatabaseField(columnName = PERSON)
	private String mPerson;

	@DatabaseField(columnName = AVATAR, dataType = DataType.BYTE_ARRAY)
	private byte[] mAvatarBytes;

	@DatabaseField(columnName = UPDATED)
	private long mLastUpdated;

	public String getPerson() {
		return mPerson;
	}

	public void setPerson(final String person) {
		mPerson = person;
	}

	public Bitmap getAvatar() {
		if (mAvatarBytes != null) {
			return BitmapFactory.decodeByteArray(mAvatarBytes, 0, mAvatarBytes.length);
		} else {
			return null;
		}
	}

	public void setAvatar(final Bitmap avatar) {
		if (avatar == null) {
			mAvatarBytes = null;

		} else {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			avatar.compress(Bitmap.CompressFormat.PNG, 100, stream);
			mAvatarBytes = stream.toByteArray();
		}
	}

	public long getLastUpdated() {
		return mLastUpdated;
	}

	public void setLastUpdated(final long lastUpdated) {
		mLastUpdated = lastUpdated;
	}

}
