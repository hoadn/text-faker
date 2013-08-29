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
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.deange.textfaker.R;
import com.deange.textfaker.content.ContentHelper;
import com.deange.textfaker.model.Conversation;
import com.deange.textfaker.model.ConversationMessage;
import com.deange.textfaker.utils.Formatter;
import com.j256.ormlite.stmt.QueryBuilder;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationListAdapter extends CursorAdapter {

	public ConversationListAdapter(final Context context) {
		super(context, null, false);
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
		return LayoutInflater.from(context).inflate(R.layout.conversation_list_item, null);
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {

		final Conversation conversation = new Conversation(cursor);

		final TextView toPersonView = (TextView) view.findViewById(R.id.from);
		final TextView updatedView = (TextView) view.findViewById(R.id.date);
		final TextView messageView = (TextView) view.findViewById(R.id.last_message);
		final TextView countView = (TextView) view.findViewById(R.id.count);
		final QuickContactBadge avatarView = (QuickContactBadge) view.findViewById(R.id.avatar);

		new ConversationMessagePopulater(messageView, countView).execute(conversation);

		final Date lastUpdated = new Date(conversation.getLastUpdated());
		final String formattedDate = Formatter.formatMessageDate(lastUpdated);

		updatedView.setText(formattedDate);
		toPersonView.setText(conversation.getName());
		avatarView.setImageToDefault();

	}

	private class ConversationMessagePopulater extends AsyncTask<Conversation, Void, List<ConversationMessage>> {

		WeakReference<TextView> mMessageReference;
		WeakReference<TextView> mCountReference;

		public ConversationMessagePopulater(final TextView messageView, final TextView countView) {
			mMessageReference = new WeakReference<TextView>(messageView);
			mCountReference = new WeakReference<TextView>(countView);
		}

		@Override
		protected List<ConversationMessage> doInBackground(final Conversation... conversations) {

			final long conversationId = conversations[0].getId();
			List<ConversationMessage> messages = new ArrayList<ConversationMessage>();

			try {
				final QueryBuilder<ConversationMessage, Long> queryBuilder = ContentHelper.getInstance(mContext).getDao
						(ConversationMessage.class).queryBuilder();
				queryBuilder.where().eq(ConversationMessage.CONVERSATION_ID, conversationId);
				queryBuilder.orderBy(ConversationMessage.TIME_SENT, false);
				messages = queryBuilder.query();

			} catch (SQLException ignored) {

			}

			return messages;
		}

		@Override
		protected void onPostExecute(final List<ConversationMessage> conversationMessages) {

			final long messageCount = conversationMessages.size();

			if (mCountReference.get() != null) {
				mCountReference.get().setText(String.valueOf(messageCount));
			}

			if (mMessageReference.get() != null) {
				if (!conversationMessages.isEmpty()) {
					final String lastMessageText = conversationMessages.get(0).getText();
					mMessageReference.get().setText(lastMessageText);

				} else {
					mMessageReference.get().setText(R.string.conversation_no_messages);
				}
			}

		}
	}

}