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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.deange.textfaker.R;
import com.deange.textfaker.content.ContentHelper;
import com.deange.textfaker.content.ormlite.OrmLiteLoader;
import com.deange.textfaker.model.Conversation;
import com.deange.textfaker.model.ConversationMessage;
import com.deange.textfaker.utils.Formatter;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public class MessageActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = MessageActivity.class.getSimpleName();

	private static final String EXTRA_CONVERSATION_ID = Formatter.makeExtra(TAG, "extra_conversation_id");
	private static final int LOADER_CONVERSATION_MESSAGES_ID = 0xdeadbeef;

	public static Intent createIntent(final Context context, final Conversation conversation) {
		Log.v(TAG, "createIntent()");

		final Intent intent = new Intent(context, MessageActivity.class);
		intent.putExtra(EXTRA_CONVERSATION_ID, conversation.getId());

		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_list);

		setupActionBar();

		getSupportLoaderManager().initLoader(LOADER_CONVERSATION_MESSAGES_ID, null, this);
	}

	private void setupActionBar() {
		final ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptionsMenu()");

		getMenuInflater().inflate(R.menu.message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected()");

		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle bundle) {
		Log.v(TAG, "onCreateLoader()");

		OrmLiteLoader<ConversationMessage> loader = null;
		final long conversationId = getIntent().getLongExtra(EXTRA_CONVERSATION_ID, Conversation.INVALID_LOCAL_ID);

		try {
			final QueryBuilder<ConversationMessage, Long> queryBuilder = ContentHelper.getInstance(this).getDao
					(ConversationMessage.class).queryBuilder();
			queryBuilder.where().eq(ConversationMessage.CONVERSATION_ID, conversationId);
			queryBuilder.orderBy(ConversationMessage.TIME_SENT, false);

			loader = new OrmLiteLoader<ConversationMessage>(this, ConversationMessage.class, queryBuilder);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return loader;
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> cursorLoader, final Cursor cursor) {
		Log.v(TAG, "onLoadFinished()");


	}

	@Override
	public void onLoaderReset(final Loader<Cursor> cursorLoader) {

	}
}
