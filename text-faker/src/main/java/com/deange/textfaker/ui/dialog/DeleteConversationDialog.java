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
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.deange.textfaker.R;
import com.deange.textfaker.model.Conversation;

public class DeleteConversationDialog extends PatchedDialogFragment implements DialogInterface
		.OnClickListener {

	public static final String TAG = DeleteConversationDialog.class.getSimpleName();
	private Callback mCallback = Fallback.INSTANCE;
	private Conversation mConversation;

	public DeleteConversationDialog(final Conversation conversation) {
		mConversation = conversation;
	}

	public static DeleteConversationDialog createInstance(final Conversation conversation) {
		Log.v(TAG, "createInstance()");

		final DeleteConversationDialog dialog = new DeleteConversationDialog(conversation);
		dialog.setRetainInstance(true);
		return dialog;
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
				.setTitle(R.string.dialog_delete_conversation_title)
				.setMessage(R.string.dialog_delete_conversation_message)
				.setPositiveButton(android.R.string.ok, this)
				.setNegativeButton(android.R.string.cancel, this)
				.create();

	}

	@Override
	public void onClick(final DialogInterface dialog, final int which) {
		Log.v(TAG, "onClick");

		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				mCallback.deleteConversationAsked(mConversation);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dismiss();
				break;
		}

	}

	public interface Callback {
		public void deleteConversationAsked(final Conversation conversation);
	}

	private static final class Fallback implements Callback {
		private static final Callback INSTANCE = new Fallback();

		@Override
		public void deleteConversationAsked(final Conversation conversation) {
			Log.w(TAG, "Fallback: deleteConversationAsked()");
		}

	}
}
