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
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.deange.textfaker.R;
import com.deange.textfaker.content.ContentHelper;
import com.deange.textfaker.content.OrmLiteLoader;
import com.deange.textfaker.content.ormlite.OrmDeleteTask;
import com.deange.textfaker.content.ormlite.OrmInsertTask;
import com.deange.textfaker.model.Conversation;
import com.deange.textfaker.model.ConversationMessage;
import com.deange.textfaker.ui.ConversationListAdapter;
import com.deange.textfaker.ui.dialog.ComposeMessageDialog;
import com.deange.textfaker.ui.dialog.DeleteConversationDialog;
import com.deange.textfaker.utils.ViewUtils;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;


public class ConversationActivity extends FragmentActivity implements LoaderCallbacks<Cursor>,
		ComposeMessageDialog.Callback, OrmInsertTask.Callback, AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener, DeleteConversationDialog.Callback,
		OrmDeleteTask.Callback {

	private ConversationListAdapter mAdapter;
	private ListView mListView;
	private ComposeMessageDialog mComposeDialog;
	private DeleteConversationDialog mDeleteDialog;

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
	}

	private void setupActionBar() {

		final ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setTitle(R.string.action_bar_title);
		}

	}

	private void findFragments() {
		if (mComposeDialog == null) {
			mComposeDialog = (ComposeMessageDialog) getSupportFragmentManager().findFragmentByTag
					(ComposeMessageDialog.TAG);
			if (mComposeDialog != null) {
				mComposeDialog.setCallback(this);
			}
		}

		if (mDeleteDialog == null) {
			mDeleteDialog = (DeleteConversationDialog) getSupportFragmentManager().findFragmentByTag
					(DeleteConversationDialog.TAG);
			if (mDeleteDialog != null) {
				mDeleteDialog.setCallback(this);
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle args) {

		final QueryBuilder<Conversation, Long> queryBuilder = ContentHelper.getInstance(this)
				.getDao(Conversation.class).queryBuilder();
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
			ViewUtils.setVisibility(findViewById(android.R.id.list),  cursor.getCount() != 0);
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
		if (item.getItemId() == R.id.action_compose_new) {
			showComposeDialog();
		}

		return super.onOptionsItemSelected(item);
	}

	private void showComposeDialog() {

		final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (mComposeDialog != null) {
			transaction.remove(mComposeDialog);
		}

		// Create and show the dialog.
		mComposeDialog = ComposeMessageDialog.createInstance();
		mComposeDialog.setCallback(this);
		mComposeDialog.show(transaction, ComposeMessageDialog.TAG);
	}

	private void showDeleteDialog(final Conversation conversation) {

		final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (mDeleteDialog != null) {
			transaction.remove(mDeleteDialog);
		}

		// Create and show the dialog.
		mDeleteDialog = DeleteConversationDialog.createInstance(conversation);
		mDeleteDialog.setCallback(this);
		mDeleteDialog.show(transaction, DeleteConversationDialog.TAG);
	}

	private void refresh() {
		getSupportLoaderManager().restartLoader(LOADER_CONVERSATION_ID, null, this);
	}

	@Override
	public void composeNewConversationAsked(final String toPerson) {
		final long now = System.currentTimeMillis();
		final Conversation newConversation = Conversation.create(toPerson, now, null);
		new OrmInsertTask<Conversation>(this, this, Conversation.class).execute
				(newConversation);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteConversationAsked(final Conversation conversation) {

		try {
			final DeleteBuilder<ConversationMessage, Long> deleteMessages = ContentHelper
					.getInstance(this)
					.getDao(ConversationMessage.class).deleteBuilder();
			deleteMessages.where().eq(ConversationMessage.CONVERSATION_ID, conversation.getId());
			new OrmDeleteTask<ConversationMessage>(this, this, ConversationMessage.class).execute
					(deleteMessages);

			final DeleteBuilder<Conversation, Long> deleteConversation = ContentHelper.getInstance
					(this)
					.getDao(Conversation.class).deleteBuilder();
			deleteConversation.where().idEq(conversation.getId());
			new OrmDeleteTask<Conversation>(this, this, Conversation.class).execute
					(deleteConversation);

		} catch (SQLException e) {
			Toast.makeText(this, R.string.delete_conversation_failed, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onInsertCompleted(final int rowsInserted) {
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
		final Conversation conversation = Conversation.create(cursor);

		final Intent intent = MessageActivity.createIntent(this, conversation);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(final AdapterView<?> parent, final View view,
	                               final int position, final long id) {

		final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		final Conversation conversation = Conversation.create(cursor);

		showDeleteDialog(conversation);

		return true;
	}

}
