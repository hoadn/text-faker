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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.deange.textfaker.R;

public class MessageSenderDialog extends PatchedDialogFragment<MessageSenderDialog.Callback> implements
		DialogInterface.OnClickListener {

	public static final String TAG = MessageSenderDialog.class.getSimpleName();
	private Callback mCallback = Fallback.INSTANCE;
	private String mPersonName;

	private MessageSenderDialog(final String personName) {
		mPersonName = personName;
	}

	public static void show(MessageSenderDialog dialog, final Callback callback, final FragmentManager manager,
	                        final String personName) {

		final FragmentTransaction transaction = manager.beginTransaction();
		if (dialog != null) {
			transaction.remove(dialog);
		}

		// Create and show the dialog.
		dialog = MessageSenderDialog.createInstance(personName);
		dialog.setCallback(callback);
		dialog.show(transaction, MessageSenderDialog.TAG);

	}

	public static MessageSenderDialog createInstance(final String personName) {
		Log.v(TAG, "createInstance()");

		final MessageSenderDialog dialog = new MessageSenderDialog(personName);
		dialog.setRetainInstance(true);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		Log.v(TAG, "onCreateDialog()");

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.dialog_sender_title)
				.setPositiveButton(R.string.dialog_sender_yourself, this)
				.setNegativeButton(mPersonName, this)
				.create();
	}

	public void setCallback(final Callback callback) {
		Log.v(TAG, "setCallback()");

		mCallback = (callback == null) ? Fallback.INSTANCE : callback;
	}

	@Override
	public void onClick(final DialogInterface dialog, final int which) {
		Log.v(TAG, "onClick()");

		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				mCallback.onMessageSenderChosen(Sender.YOURSELF);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				mCallback.onMessageSenderChosen(Sender.OTHERPERSON);
				break;
		}
	}

	public enum Sender {YOURSELF, OTHERPERSON}

	public interface Callback {
		public void onMessageSenderChosen(final Sender messageSender);
	}

	private static final class Fallback implements Callback {
		private static final Callback INSTANCE = new Fallback();

		@Override
		public void onMessageSenderChosen(final Sender messageSender) {
			Log.w(TAG, "Fallback: onMessageSenderChosen()");
		}

	}
}
