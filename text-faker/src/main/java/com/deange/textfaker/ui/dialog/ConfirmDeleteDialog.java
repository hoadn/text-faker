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

package com.deange.textfaker.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.deange.textfaker.R;
import com.deange.textfaker.content.ContentHelper;
import com.deange.textfaker.content.ormlite.OrmDeleteTask;
import com.deange.textfaker.model.Conversation;
import com.deange.textfaker.model.ConversationMessage;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;

public class ConfirmDeleteDialog extends PatchedDialogFragment<ConfirmDeleteDialog.Callback> implements
		DialogInterface.OnClickListener {

	public static final String TAG = ConfirmDeleteDialog.class.getSimpleName();
	private final long mDeletableItemId;
	private final String mMessage;
	private final Class<?> mClazz;
	private Callback mCallback = Fallback.INSTANCE;

	public ConfirmDeleteDialog(final Class<?> clazz, final long itemId, final String message) {
		mClazz = clazz;
		mDeletableItemId = itemId;
		mMessage = message;
	}

	public static void show(ConfirmDeleteDialog dialog, final Callback callback, final FragmentManager manager,
			final Class<?> clazz, final long itemId, final String message) {
		Log.v(TAG, "show()");

		final FragmentTransaction transaction = manager.beginTransaction();
		if (dialog != null) {
			transaction.remove(dialog);
		}

		dialog = ConfirmDeleteDialog.createInstance(clazz, itemId, message);
		dialog.setCallback(callback);
		dialog.show(transaction, ConfirmDeleteDialog.TAG);

	}

	public static ConfirmDeleteDialog createInstance(final Class<?> clazz, final long itemId, final String message) {
		Log.v(TAG, "createInstance()");

		final ConfirmDeleteDialog dialog = new ConfirmDeleteDialog(clazz, itemId, message);
		dialog.setRetainInstance(true);
		return dialog;
	}

	@SuppressWarnings("unchecked")
	public static void deleteConversation(final Context context, final long conversationId) {
		Log.v(TAG, "deleteConversation()");

		try {
			final DeleteBuilder<ConversationMessage, Long> deleteMessages = ContentHelper.getInstance(context).getDao
					(ConversationMessage.class).deleteBuilder();
			deleteMessages.where().eq(ConversationMessage.CONVERSATION_ID, conversationId);
			new OrmDeleteTask<ConversationMessage>(context, (OrmDeleteTask.Callback) context,
					ConversationMessage.class).execute(deleteMessages);

			final DeleteBuilder<Conversation, Long> deleteConversation = ContentHelper.getInstance(context).getDao
					(Conversation.class).deleteBuilder();
			deleteConversation.where().idEq(conversationId);
			new OrmDeleteTask<Conversation>(context, (OrmDeleteTask.Callback) context, Conversation.class).execute
					(deleteConversation);

		} catch (SQLException e) {
			Toast.makeText(context, R.string.delete_conversation_failed, Toast.LENGTH_SHORT).show();
		}

	}

	@SuppressWarnings("unchecked")
	public static void deleteAllConversations(final Context context, final OrmDeleteTask.Callback callback) {
		Log.v(TAG, "deleteAllConversations()");

		final DeleteBuilder<ConversationMessage, Long> deleteMessages = ContentHelper.getInstance(context).getDao
				(ConversationMessage.class).deleteBuilder();
		new OrmDeleteTask<ConversationMessage>(context, callback, ConversationMessage.class).execute(deleteMessages);

		final DeleteBuilder<Conversation, Long> deleteConversation = ContentHelper.getInstance(context).getDao
				(Conversation.class).deleteBuilder();
		new OrmDeleteTask<Conversation>(context, callback, Conversation.class).execute(deleteConversation);
	}

	@SuppressWarnings("unchecked")
	public static void deleteMessage(final Context context, final OrmDeleteTask.Callback callback,
			final long messageId) {
		try {
			final DeleteBuilder<ConversationMessage, Long> deleteConversation = ContentHelper.getInstance(context)
					.getDao(ConversationMessage.class).deleteBuilder();
			deleteConversation.where().idEq(messageId);

			new OrmDeleteTask<ConversationMessage>(context, callback, ConversationMessage.class).execute
					(deleteConversation);
		} catch (SQLException e) {
			Toast.makeText(context, R.string.delete_message_failed, Toast.LENGTH_SHORT).show();
		}
	}

	public void setCallback(final Callback callback) {
		Log.v(TAG, "setCallback()");

		mCallback = (callback == null) ? Fallback.INSTANCE : callback;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		Log.v(TAG, "onCreateDialog()");

		return new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.ic_action_alerts_and_states_warning)
				.setTitle(R.string.dialog_delete_item_title)
				.setMessage(mMessage)
				.setPositiveButton(android.R.string.ok, this)
				.setNegativeButton(android.R.string.cancel, this)
				.create();

	}

	@Override
	public void onClick(final DialogInterface dialog, final int which) {
		Log.v(TAG, "onClick");

		switch(which) {
			case DialogInterface.BUTTON_POSITIVE:
				mCallback.onDeleteItemAsked(mClazz, mDeletableItemId);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dismiss();
				break;
		}

	}

	public interface Callback {

		public void onDeleteItemAsked(final Class clazz, final long itemId);
	}

	private static final class Fallback implements Callback {

		private static final Callback INSTANCE = new Fallback();

		@Override
		public void onDeleteItemAsked(final Class clazz, final long itemId) {
			Log.w(TAG, "Fallback: onDeleteItemAsked()");
		}

	}
}
