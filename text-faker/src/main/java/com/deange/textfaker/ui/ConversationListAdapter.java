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

package com.deange.textfaker.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.deange.textfaker.R;
import com.deange.textfaker.model.Conversation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConversationListAdapter extends CursorAdapter {

	private final DateFormat mTimeFormatter;
	private final DateFormat mDateFormatter;
	private final DateFormat mYearFormatter;

	public ConversationListAdapter(final Context context) {
		super(context, null, false);

		mTimeFormatter = android.text.format.DateFormat.getTimeFormat(context);
		mYearFormatter = android.text.format.DateFormat.getMediumDateFormat(context);
		mDateFormatter = new SimpleDateFormat("MMM yy");

	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
		return LayoutInflater.from(context).inflate(R.layout.conversation_list_item, null);
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {

		final Conversation conversation = Conversation.create(cursor);

		final TextView toPersonView = (TextView) view.findViewById(R.id.from);
		final TextView updatedView = (TextView) view.findViewById(R.id.date);
		final QuickContactBadge avatarView = (QuickContactBadge) view.findViewById(R.id.avatar);

		toPersonView.setText(conversation.getPerson());

		if (conversation.getAvatar() == null) {
			avatarView.setImageToDefault();
		} else {
			avatarView.setImageBitmap(conversation.getAvatar());
		}

		final Calendar now = Calendar.getInstance();
		final Calendar then = Calendar.getInstance();
		then.setTimeInMillis(conversation.getLastUpdated());

		if (now.get(Calendar.YEAR) != then.get(Calendar.YEAR)) {
			updatedView.setText(mYearFormatter.format(then.getTime()));

		} else if (now.get(Calendar.DAY_OF_YEAR) != then.get(Calendar.DAY_OF_YEAR)) {
			updatedView.setText(mDateFormatter.format(then.getTime()));

		} else {
			updatedView.setText(mTimeFormatter.format(then.getTime()));
		}

	}
}
