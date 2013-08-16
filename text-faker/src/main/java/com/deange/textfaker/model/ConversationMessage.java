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

import com.deange.textfaker.utils.BooleanConverter;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = ConversationMessage.TABLE)
public class ConversationMessage extends BaseModel {

	public static final String TABLE = "conversation_message";
	public static final String CONVERSATION_ID = "conversation_id";

	public static final String TIME_SENT = "time_sent";
	public static final String TEXT = "text";
	public static final String SENDER = "sender";
	public static final String ISOUTGOING = "is_outgoing";


	@DatabaseField(columnName = CONVERSATION_ID)
	private long mConversationId;

	@DatabaseField(columnName = TIME_SENT)
	private long mTime;

	@DatabaseField(columnName = TEXT)
	private String mText;

	@DatabaseField(columnName = ISOUTGOING)
	private boolean mIsOutgoing;

	public static ConversationMessage createInstance(final Cursor cursor) {

		final boolean isOutgoing = BooleanConverter.convert(cursor.getInt(cursor.getColumnIndex(ISOUTGOING)));
		final long conversationId = cursor.getLong(cursor.getColumnIndex(CONVERSATION_ID));
		final long time = cursor.getLong(cursor.getColumnIndex(TIME_SENT));
		final String text = cursor.getString(cursor.getColumnIndex(TEXT));

		final ConversationMessage message = new ConversationMessage(conversationId, isOutgoing, text);
		message.setTime(time);

		return message;
	}

	public static ConversationMessage createInstance(final long conversationId, final boolean isOutgoing, final String text) {
		return new ConversationMessage(conversationId, isOutgoing, text);
	}

	private ConversationMessage(final long conversationId, final boolean isOutgoing, final String text) {
		mConversationId = conversationId;
		mText = text;
		mIsOutgoing = isOutgoing;
		mTime = System.currentTimeMillis();
	}

	public ConversationMessage() {
		// Needed by OrmLite
	}

	public long getConversationId() {
		return mConversationId;
	}

	public boolean isOutgoing() {
		return mIsOutgoing;
	}

	public String getText() {
		return mText;
	}

	public long getTime() {
		return mTime;
	}

	public void setTime(final long time) {
		mTime = time;
	}

	public void setIsOutgoing(final boolean isOutgoing) {
		mIsOutgoing = isOutgoing;
	}
}
