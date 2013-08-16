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

package com.deange.textfaker.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deange.textfaker.R;
import com.deange.textfaker.model.ConversationMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MessageListAdapter extends CursorAdapter {

	private final DateFormat mTimeFormatter;
	private final DateFormat mDateFormatter;
	private final DateFormat mYearFormatter;

	public MessageListAdapter(final Context context) {
		super(context, null, false);

		mTimeFormatter = android.text.format.DateFormat.getTimeFormat(context);
		mYearFormatter = android.text.format.DateFormat.getMediumDateFormat(context);
		mDateFormatter = new SimpleDateFormat("MMM yy");

	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {

		View view = null;
		if (cursor.getInt(cursor.getColumnIndex(ConversationMessage.ISOUTGOING)) > 0) {
			// Outgoing message
			view = LayoutInflater.from(context).inflate(R.layout.message_list_item_send, null);

		} else {
			// Incoming message
			view = LayoutInflater.from(context).inflate(R.layout.message_list_item_recv, null);
		}

		return view;
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {

		//final ConversationMessage message = ConversationMessage.createInstance(cursor);



	}
}
