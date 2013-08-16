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
import com.deange.textfaker.model.Person;
import com.deange.textfaker.utils.Formatter;
import com.j256.ormlite.stmt.QueryBuilder;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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

		final Conversation conversation = Conversation.createInstance(cursor);

		final TextView toPersonView = (TextView) view.findViewById(R.id.from);
		final TextView updatedView = (TextView) view.findViewById(R.id.date);
		final TextView messageView = (TextView) view.findViewById(R.id.last_message);
		final TextView countView = (TextView) view.findViewById(R.id.count);
		final QuickContactBadge avatarView = (QuickContactBadge) view.findViewById(R.id.avatar);

		avatarView.setImageToDefault();

		new ConversationResourcePopulater(avatarView, toPersonView).execute(conversation);
		new ConversationMessagePopulater(messageView, countView).execute(conversation);

		final Calendar now = Calendar.getInstance();
		final Calendar then = Calendar.getInstance();
		then.setTimeInMillis(conversation.getLastUpdated());

		if (now.get(Calendar.YEAR) != then.get(Calendar.YEAR)) {
			updatedView.setText(Formatter.formatLongDate(then.getTime()));

		} else if (now.get(Calendar.DAY_OF_YEAR) != then.get(Calendar.DAY_OF_YEAR)) {
			updatedView.setText(Formatter.formatMediumDate(then.getTime()));

		} else {
			updatedView.setText(Formatter.formatTime(then.getTime()));
		}

	}

	private class ConversationResourcePopulater extends AsyncTask<Conversation, Void, Person> {

		final WeakReference<TextView> mNameView;
		final WeakReference<QuickContactBadge> mAvatarView;

		ConversationResourcePopulater(final QuickContactBadge avatarView, final TextView nameView) {
			mNameView = new WeakReference<TextView>(nameView);
			mAvatarView = new WeakReference<QuickContactBadge>(avatarView);
		}

		@Override
		protected Person doInBackground(final Conversation... conversations) {

			final long personId = conversations[0].getPersonId();
			Person person = null;

			try {
				QueryBuilder<Person, Long> queryBuilder = ContentHelper.getInstance(mContext).getDao(Person.class)
						.queryBuilder();
				queryBuilder.where().idEq(personId);
				person = queryBuilder.queryForFirst();

			} catch (SQLException e) {

			}

			return person;
		}

		@Override
		protected void onPostExecute(final Person person) {
			if (person != null) {
				if ((mAvatarView.get() != null) && (person.getAvatar() != null)) {
					mAvatarView.get().setImageBitmap(person.getAvatar());
				}
				if (mNameView.get() != null) {
					mNameView.get().setText(person.getName());
				}
			}
		}
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
				queryBuilder.orderBy(ConversationMessage.TIME_SENT, true);
				messages = queryBuilder.query();

			} catch (SQLException e) {

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