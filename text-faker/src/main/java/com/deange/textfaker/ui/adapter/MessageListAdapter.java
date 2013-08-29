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

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deange.textfaker.R;
import com.deange.textfaker.model.ConversationMessage;
import com.deange.textfaker.ui.layout.QuickContactDivot;
import com.deange.textfaker.utils.BooleanConverter;
import com.deange.textfaker.utils.Formatter;

import java.util.Date;

public class MessageListAdapter extends CursorAdapter {

	private static final int TOTAL_VIEW_COUNT = 2;

	private View.OnLongClickListener mLongPressListener;

	public MessageListAdapter(final Activity context, final View.OnLongClickListener longPressListnener) {
		super(context, null, false);
		mLongPressListener = longPressListnener;
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {

		final ConversationMessage message = new ConversationMessage(cursor);
		final boolean isOutgoing = message.isOutgoing();

		View view;
		if (isOutgoing) {
			// Outgoing message
			view = LayoutInflater.from(context).inflate(R.layout.message_list_item_send, null);

		} else {
			// Incoming message
			view = LayoutInflater.from(context).inflate(R.layout.message_list_item_recv, null);
		}

		return view;
	}

	@Override
	public int getItemViewType(final int position) {
		final ConversationMessage message = new ConversationMessage((Cursor) getItem(position));
		return BooleanConverter.convert(message.isOutgoing());
	}

	@Override
	public int getViewTypeCount() {
		return TOTAL_VIEW_COUNT;
	}

	@Override
	public void bindView(View view, final Context context, final Cursor cursor) {

		view.setOnLongClickListener(mLongPressListener);
		((Activity) mContext).registerForContextMenu(view);

		final ConversationMessage message = new ConversationMessage(cursor);
		final String formattedDate = Formatter.formatMessageDate(new Date(message.getTime()));

		((TextView) view.findViewById(R.id.text_view)).setText(message.getText());
		((TextView) view.findViewById(R.id.date_view)).setText(formattedDate);

		((QuickContactDivot) view.findViewById(R.id.avatar)).setImageToDefault();
	}
}
