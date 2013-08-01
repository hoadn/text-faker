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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = ConversationMessage.TABLE)
public class ConversationMessage extends BaseModel {

	public static final String TABLE = "conversation_message";
	public static final String CONVERSATION_ID = "conversation_id";

	public static final String TIME_SENT = "time_sent";
	public static final String TEXT = "text";
	public static final String SENDER = "sender";
	public static final String RECEIVER = "receiver";


	@DatabaseField(columnName = CONVERSATION_ID)
	private long mConversationId;

	@DatabaseField(columnName = TIME_SENT)
	private long mTime;

	@DatabaseField(columnName = TEXT)
	private String mText;

	@DatabaseField(columnName = SENDER)
	private long mSenderId;

	@DatabaseField(columnName = RECEIVER)
	private long mReceiverId;


	public long getConversationId() {
		return mConversationId;
	}

	public void setConversationId(final long conversationId) {
		mConversationId = conversationId;
	}

	public long getReceiverId() {
		return mReceiverId;
	}

	public void setReceiverId(final long receiverId) {
		mReceiverId = receiverId;
	}

	public long getSenderId() {
		return mSenderId;
	}

	public void setSenderId(final long senderId) {
		mSenderId = senderId;
	}

	public String getText() {
		return mText;
	}

	public void setText(final String text) {
		mText = text;
	}

	public long getTime() {
		return mTime;
	}

	public void setTime(final long time) {
		mTime = time;
	}
}
