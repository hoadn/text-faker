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
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.deange.datetimepicker.date.DatePickerDialog;
import com.deange.textfaker.R;
import com.deange.textfaker.content.ContentHelper;
import com.deange.textfaker.content.ormlite.OrmDeleteTask;
import com.deange.textfaker.content.ormlite.OrmInsertTask;
import com.deange.textfaker.content.ormlite.OrmLiteLoader;
import com.deange.textfaker.model.BaseModel;
import com.deange.textfaker.model.Conversation;
import com.deange.textfaker.ui.adapter.ConversationListAdapter;
import com.deange.textfaker.ui.dialog.ConfirmDeleteDialog;
import com.deange.textfaker.ui.dialog.ConversationPersonDialog;
import com.deange.textfaker.utils.FragmentUtils;
import com.deange.textfaker.utils.ViewUtils;
import com.j256.ormlite.stmt.QueryBuilder;


public class ConversationActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,
		ConversationPersonDialog.Callback, OrmInsertTask.Callback, AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener, ConfirmDeleteDialog.Callback,
		OrmDeleteTask.Callback {

	private ConversationListAdapter mAdapter;
	private ListView mListView;
	private ConversationPersonDialog mPersonDialog;
	private ConfirmDeleteDialog mDeleteDialog;
	private int LOADER_CONVERSATION_ID = 0xfaceb00c;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversations);

		setupActionBar();
		findFragments();

		mAdapter = new ConversationListAdapter(this);
		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);

		getSupportLoaderManager().initLoader(LOADER_CONVERSATION_ID, null, this);

		DatePickerDialog.newInstance(null, 2013, 8, 25).show(getFragmentManager(), "lol");
	}

	private void setupActionBar() {

		final ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setTitle(R.string.action_bar_title);
		}

	}

	private void findFragments() {
		FragmentUtils.findDialogFragment(mPersonDialog, getSupportFragmentManager(), this,
				ConversationPersonDialog.TAG);
		FragmentUtils.findDialogFragment(mDeleteDialog, getSupportFragmentManager(), this,
				ConfirmDeleteDialog.TAG);
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle args) {

		final QueryBuilder<Conversation, Long> queryBuilder = ContentHelper.getInstance(this).getDao(
				Conversation.class).queryBuilder();
		// Select all, so no need to set any where() clauses
		// Just need to sort by most recent to oldest conversations
		queryBuilder.orderBy(Conversation.UPDATED, false);

		final OrmLiteLoader<Conversation> loader = new OrmLiteLoader<Conversation>(this,
				Conversation.class, queryBuilder);

		return loader;
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> cursorLoader, final Cursor cursor) {
		if ((cursor != null) && (!cursor.isClosed())) {
			mAdapter.swapCursor(cursor);

			ViewUtils.setVisibility(findViewById(android.R.id.empty), cursor.getCount() == 0);
			ViewUtils.setVisibility(findViewById(android.R.id.list), cursor.getCount() != 0);
		}
	}

	@Override
	public void onLoaderReset(final Loader<Cursor> cursorLoader) {
		mAdapter.swapCursor(null);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.conversation_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {

		switch(item.getItemId()) {
			case R.id.action_compose_new:
				showComposeDialog();
				break;
			case R.id.action_delete_all:
				showDeleteDialog(BaseModel.INVALID_LOCAL_ID, R.string.dialog_delete_all_conversations);
		}

		return super.onOptionsItemSelected(item);
	}

	private void showComposeDialog() {
		ConversationPersonDialog.show(mPersonDialog, this, getSupportFragmentManager(), null, null);
	}

	private void showDeleteDialog(final long conversationId, final int messageResId) {

		final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (mDeleteDialog != null) {
			transaction.remove(mDeleteDialog);
		}

		// Create and show the dialog.
		mDeleteDialog = ConfirmDeleteDialog.createInstance(Conversation.class, conversationId,
				getString(messageResId));
		mDeleteDialog.setCallback(this);
		mDeleteDialog.show(transaction, ConfirmDeleteDialog.TAG);
	}

	private void refresh() {
		getSupportLoaderManager().restartLoader(LOADER_CONVERSATION_ID, null, this);
	}

	@Override
	public void onConversationPersonEditAsked(final String toPerson, final String toPhoneNumber) {
		final long now = System.currentTimeMillis();
		final Conversation newConversation = Conversation.createInstance(toPerson, toPhoneNumber, now);
		new OrmInsertTask<Conversation>(this, this, Conversation.class).execute(newConversation);
	}

	@Override
	public void onDeleteItemAsked(final Class clazz, final long itemId) {

		if (itemId == BaseModel.INVALID_LOCAL_ID) {
			ConfirmDeleteDialog.deleteAllConversations(this, this);

		} else {
			ConfirmDeleteDialog.deleteConversation(this, itemId);
		}
	}

	@Override
	public void onInsertCompleted(final BaseModel model) {
		refresh();
	}

	@Override
	public void onDeleteCompleted(final int rowsDeleted) {
		refresh();
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position,
			final long id) {

		final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		final Conversation conversation = Conversation.createInstance(cursor);

		final Intent intent = MessageActivity.createIntent(this, conversation);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(final AdapterView<?> parent, final View view,
			final int position, final long id) {

		final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		final Conversation conversation = Conversation.createInstance(cursor);

		showDeleteDialog(conversation.getId(), R.string.dialog_delete_conversation_message);

		return true;
	}

}
