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

package com.deange.textfaker.ui.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.deange.textfaker.R;
import com.deange.textfaker.content.ContentHelper;
import com.deange.textfaker.content.ormlite.OrmDeleteTask;
import com.deange.textfaker.content.ormlite.OrmInsertTask;
import com.deange.textfaker.content.ormlite.OrmLiteLoader;
import com.deange.textfaker.content.ormlite.OrmUpdateTask;
import com.deange.textfaker.model.BaseModel;
import com.deange.textfaker.model.Conversation;
import com.deange.textfaker.model.ConversationMessage;
import com.deange.textfaker.ui.adapter.MessageListAdapter;
import com.deange.textfaker.ui.dialog.ConfirmDeleteDialog;
import com.deange.textfaker.ui.dialog.ConversationPersonDialog;
import com.deange.textfaker.ui.dialog.EditMessageDialog;
import com.deange.textfaker.ui.dialog.MessageSenderDialog;
import com.deange.textfaker.utils.Formatter;
import com.deange.textfaker.utils.FragmentUtils;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MessageActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher,
		ConfirmDeleteDialog.Callback, View.OnClickListener, View.OnLongClickListener,
		MessageSenderDialog.Callback, OrmInsertTask.Callback,
		ConversationPersonDialog.Callback, OrmUpdateTask.Callback, OrmDeleteTask.Callback, EditMessageDialog.Callback {

	private static final String TAG = MessageActivity.class.getSimpleName();
	private static final String EXTRA_CONVERSATION_ID = Formatter.makeExtra(TAG, "extra_conversation_id");
	private static final int LOADER_CONVERSATION_MESSAGES_ID = 0xdeadbeef;
	private static final int LOADER_CONVERSATION_ID = 0xb00bcafe;
	private EditText mMessageEditView;
	private ImageButton mSendSmsButton;
	private ListView mListView;
	private MessageListAdapter mAdapter;
	private Conversation mConversation;
	private ConversationMessage mLongPressedMessage;
	private EditMessageDialog mEditMessageDialog;
	private ConversationPersonDialog mPersonDialog;
	private MessageSenderDialog mMessageDialog;
	private ConfirmDeleteDialog mDeleteDialog;

	public static Intent createIntent(final Context context, final Conversation conversation) {
		Log.v(TAG, "createIntent()");

		final Intent intent = new Intent(context, MessageActivity.class);
		intent.putExtra(EXTRA_CONVERSATION_ID, conversation.getId());

		return intent;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.v(TAG, "onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_list);

		setupActionBar();
		findFragments();
		mAdapter = new MessageListAdapter(this, this);

		mListView = (ListView) findViewById(R.id.history);
		mListView.setAdapter(mAdapter);

		mSendSmsButton = (ImageButton) findViewById(R.id.send_button_sms);
		mSendSmsButton.setOnClickListener(this);
		updateSmsButtonState("");

		mMessageEditView = (EditText) findViewById(R.id.embedded_text_editor);
		mMessageEditView.addTextChangedListener(this);

		getSupportLoaderManager().initLoader(LOADER_CONVERSATION_MESSAGES_ID, null, this);
		getSupportLoaderManager().initLoader(LOADER_CONVERSATION_ID, null, this);
	}

	private void setupActionBar() {
		final ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);

			if (mConversation != null) {
				actionBar.setTitle(mConversation.getName());
				actionBar.setSubtitle(mConversation.getNumber());
			}
		}
	}

	private void findFragments() {
		FragmentUtils.findDialogFragment(mEditMessageDialog, getSupportFragmentManager(), this, EditMessageDialog.TAG);
		FragmentUtils.findDialogFragment(mMessageDialog, getSupportFragmentManager(), this, MessageSenderDialog.TAG);
		FragmentUtils.findDialogFragment(mDeleteDialog, getSupportFragmentManager(), this,
				ConfirmDeleteDialog.TAG);
		FragmentUtils.findDialogFragment(mPersonDialog, getSupportFragmentManager(), this,
				ConversationPersonDialog.TAG);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		Log.v(TAG, "onCreateOptionsMenu()");

		getMenuInflater().inflate(R.menu.message_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected()");

		switch(item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_delete:
				showDeleteDialog(Conversation.class, mConversation.getId(),
						R.string.dialog_delete_conversation_message);
				break;
			case R.id.action_edit_conversation:
				showChangePersonDialog();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle bundle) {
		Log.v(TAG, "onCreateLoader()");

		OrmLiteLoader loader = null;
		final long conversationId = getIntent().getLongExtra(EXTRA_CONVERSATION_ID, Conversation.INVALID_LOCAL_ID);

		try {
			if (loaderId == LOADER_CONVERSATION_MESSAGES_ID) {
				final QueryBuilder<ConversationMessage, Long> queryBuilder = ContentHelper.getInstance(this).getDao
						(ConversationMessage.class).queryBuilder();
				queryBuilder.where().eq(ConversationMessage.CONVERSATION_ID, conversationId);
				queryBuilder.orderBy(ConversationMessage.TIME_SENT, true);

				loader = new OrmLiteLoader<ConversationMessage>(this, ConversationMessage.class, queryBuilder);

			} else if (loaderId == LOADER_CONVERSATION_ID) {
				final QueryBuilder<Conversation, Long> queryBuilder = ContentHelper.getInstance(this).getDao
						(Conversation.class).queryBuilder();
				queryBuilder.where().eq(Conversation.LOCAL_ID, conversationId);

				loader = new OrmLiteLoader<Conversation>(this, Conversation.class, queryBuilder);
			}

		} catch (SQLException ignored) {

		}

		return loader;
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> cursorLoader, final Cursor cursor) {
		Log.v(TAG, "onLoadFinished()");

		final int loaderId = cursorLoader.getId();
		if (loaderId == LOADER_CONVERSATION_MESSAGES_ID) {
			mAdapter.swapCursor(cursor);

		} else if (loaderId == LOADER_CONVERSATION_ID) {
			mConversation = new Conversation(cursor);
			setupActionBar();
		}
	}

	@Override
	public void onLoaderReset(final Loader<Cursor> cursorLoader) {
		Log.v(TAG, "onLoaderReset()");
		mAdapter.swapCursor(null);
	}

	void refresh() {
		Log.v(TAG, "refresh()");
		getSupportLoaderManager().restartLoader(LOADER_CONVERSATION_MESSAGES_ID, null, this);
	}

	private void showDeleteDialog(final Class<?> clazz, final long itemId, final int textResId) {
		ConfirmDeleteDialog.show(mDeleteDialog, this, getSupportFragmentManager(), clazz, itemId,
				getString(textResId));
	}

	private void showChooseSenderDialog() {
		MessageSenderDialog.show(mMessageDialog, this, getSupportFragmentManager(), mConversation.getName());
	}

	private void showChangePersonDialog() {
		ConversationPersonDialog.show(mPersonDialog, this, getSupportFragmentManager(), mConversation.getName(),
				mConversation.getNumber());
	}

	private void showEditMessageDialog() {
		EditMessageDialog.show(mEditMessageDialog, this, getSupportFragmentManager(), mLongPressedMessage);
	}

	private void setConversationLastUpdatedToNow() {
		new UpdateConversationTask(this, mConversation).execute();
	}

	void updateSmsButtonState(final String text) {
		Log.v(TAG, "updateSmsButtonState()");

		final boolean enabled = (text.trim().length() != 0);
		final float alpha = (enabled) ? 1f : 0.5f;

		mSendSmsButton.setAlpha(alpha);
		mSendSmsButton.setEnabled(enabled);
		mSendSmsButton.setFocusable(enabled);
	}

	@Override
	public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
		updateSmsButtonState(s.toString());
	}

	@Override
	public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
	}

	@Override
	public void afterTextChanged(final Editable s) {
	}

	@Override
	public void onClick(final View v) {
		Log.v(TAG, "onClick()");

		switch(v.getId()) {
			case R.id.send_button_sms:
				showChooseSenderDialog();
				break;
		}
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {

		final int itemIndex = mListView.getPositionForView(v);

		if (itemIndex != AdapterView.INVALID_POSITION) {
			mLongPressedMessage = new ConversationMessage((Cursor) mAdapter.getItem(itemIndex));
			getMenuInflater().inflate(R.menu.message_context_menu, menu);
			menu.setHeaderTitle(R.string.menu_header_title);
		}

	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {

		switch(item.getItemId()) {
			case R.id.action_copy_text:
				Formatter.copyToClipboard(this, mLongPressedMessage.getText());
				break;
			case R.id.action_edit_message:
				showEditMessageDialog();
				break;
			case R.id.action_delete:
				showDeleteDialog(ConversationMessage.class, mLongPressedMessage.getId(),
						R.string.dialog_delete_message_message);
				break;
		}

		return true;
	}

	@Override
	public boolean onLongClick(final View v) {
		return v.showContextMenu();
	}

	@Override
	public void onDeleteItemAsked(final Class clazz, final long itemId) {
		Log.v(TAG, "onDeleteItemAsked()");

		if (clazz == Conversation.class) {
			ConfirmDeleteDialog.deleteConversation(this, itemId);

		} else if (clazz == ConversationMessage.class) {
			new OrmDeleteTask<ConversationMessage>(this, this, ConversationMessage.class).execute(mLongPressedMessage);
		}
	}

	@Override
	public void onConversationPersonEditAsked(final String toPerson, final String toPhoneNumber) {
		Log.v(TAG, "onConversationPersonEditAsked()");

		mConversation.setName(toPerson);
		mConversation.setNumber(toPhoneNumber);

		new OrmUpdateTask<Conversation>(this, this, Conversation.class).execute(mConversation);
	}

	@Override
	public void onMessageSenderChosen(final MessageSenderDialog.Sender messageSender) {
		Log.v(TAG, "onMessageSenderChosen()");

		final boolean isOutgoing = (messageSender == MessageSenderDialog.Sender.YOURSELF);
		final String messageText = mMessageEditView.getText().toString().trim();

		final ConversationMessage message = new ConversationMessage(mConversation.getId(), isOutgoing, messageText);

		new OrmInsertTask<ConversationMessage>(this, this, ConversationMessage.class).execute(message);
	}

	@Override
	public void onMessageUpdateAsked(final Date newDate, final String newMessage) {

		mLongPressedMessage.setTime(newDate.getTime());
		mLongPressedMessage.setText(newMessage);

		new OrmUpdateTask<ConversationMessage>(this, this, ConversationMessage.class).execute(mLongPressedMessage);
	}

	@Override
	public void onInsertCompleted(final BaseModel model) {

		setConversationLastUpdatedToNow();

		mMessageEditView.setText("");
		refresh();
	}

	@Override
	public void onUpdateCompleted(final Class clazz, final long itemId) {
		if (clazz == Conversation.class) {
			setupActionBar();

		} else if (clazz == ConversationMessage.class) {
			setConversationLastUpdatedToNow();
			refresh();
		}
	}

	@Override
	public void onDeleteCompleted(final int rowsDeleted) {
		refresh();
	}

	private class UpdateConversationTask extends AsyncTask<Void, Void, Void> {

		private Context mContext;
		private Conversation mConversation;

		public UpdateConversationTask(final Context context, final Conversation conversation) {
			mContext = context;
			mConversation = conversation;
		}

		@Override
		protected Void doInBackground(final Void... voids) {

			final ContentHelper content = ContentHelper.getInstance(mContext);

			try {
				final QueryBuilder<ConversationMessage, Long> query = content.getDao(ConversationMessage.class)
						.queryBuilder();
				query.where().eq(ConversationMessage.CONVERSATION_ID, mConversation.getId());
				query.orderBy(ConversationMessage.TIME_SENT, false);

				final List<ConversationMessage> messages = query.query();
				if (messages != null) {
					final long lastMessageTime = messages.get(0).getTime();
					mConversation.setLastUpdated(lastMessageTime);
					content.getDao(Conversation.class).update(mConversation);
				}

			} catch (final SQLException ignored) {
			}

			return null;
		}
	}
}
