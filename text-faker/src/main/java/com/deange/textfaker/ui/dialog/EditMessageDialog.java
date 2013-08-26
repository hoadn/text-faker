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

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.deange.textfaker.model.ConversationMessage;

import java.util.Date;

public class EditMessageDialog extends PatchedDialogFragment<EditMessageDialog.Callback> {

	public static final String TAG = EditMessageDialog.class.getSimpleName();
	private Callback mCallback = Fallback.INSTANCE;
	private ConversationMessage mMessage;

	public EditMessageDialog(final ConversationMessage message) {
		mMessage = message;
	}

	public static void show(EditMessageDialog dialog, final Callback callback, final FragmentManager manager,
			final ConversationMessage message) {
		Log.v(TAG, "show()");

		final FragmentTransaction transaction = manager.beginTransaction();
		if (dialog != null) {
			transaction.remove(dialog);
		}

		dialog = EditMessageDialog.createInstance(message);
		dialog.setCallback(callback);
		dialog.show(transaction, EditMessageDialog.TAG);

	}

	public static EditMessageDialog createInstance(final ConversationMessage message) {
		Log.v(TAG, "createInstance()");

		EditMessageDialog dialog = new EditMessageDialog(message);
		dialog.setRetainInstance(true);
		return dialog;
	}

	@Override
	public void setCallback(final Callback callback) {
		Log.v(TAG, "setCallback()");

		mCallback = (callback == null) ? Fallback.INSTANCE : callback;
	}

	public interface Callback {

		public void onMessageUpdated(final Date newDate, final String newMessage);
	}

	private static final class Fallback implements Callback {

		private static final Callback INSTANCE = new Fallback();

		@Override
		public void onMessageUpdated(final Date newDate, final String newMessage) {
			Log.w(TAG, "Fallback: onMessageUpdated()");
		}

	}
}
