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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.deange.textfaker.R;

public class ConversationPersonDialog extends PatchedDialogFragment<ConversationPersonDialog.Callback> implements
		DialogInterface.OnClickListener {

	public static final String TAG = ConversationPersonDialog.class.getSimpleName();
	private Callback mCallback = Fallback.INSTANCE;
	private EditText mToPerson;
	private EditText mToPhoneNumber;
	private String mPersonName;
	private String mPhoneNumber;

	public ConversationPersonDialog(final String personName, final String phoneNumber) {
		mPersonName = personName;
		mPhoneNumber = phoneNumber;
	}

	public static void show(ConversationPersonDialog dialog, final Callback callback, final FragmentManager manager,
	                        final String personName, final String phoneNumber) {
		Log.v(TAG, "show()");

		final FragmentTransaction transaction = manager.beginTransaction();
		if (dialog != null) {
			transaction.remove(dialog);
		}

		dialog = ConversationPersonDialog.createInstance(personName, phoneNumber);
		dialog.setCallback(callback);
		dialog.show(transaction, ConversationPersonDialog.TAG);
	}

	public static ConversationPersonDialog createInstance(final String personName, final String phoneNumber) {
		Log.v(TAG, "createInstance()");

		final ConversationPersonDialog dialog = new ConversationPersonDialog(personName, phoneNumber);
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

		final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_compose_new, null);

		mToPerson = (EditText) view.findViewById(R.id.dialog_compose_new_to);
		mToPhoneNumber = (EditText) view.findViewById(R.id.dialog_compose_new_phone);

		mToPerson.setText(mPersonName);
		mToPhoneNumber.setText(mPhoneNumber);

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.compose_message)
				.setView(view)
				.setPositiveButton(android.R.string.ok, this)
				.setNegativeButton(android.R.string.cancel, this)
				.create();

	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		mPersonName = mToPerson.getText().toString();
		mPhoneNumber = mToPhoneNumber.getText().toString();
	}

	@Override
	public void onClick(final DialogInterface dialog, final int which) {
		Log.v(TAG, "onClick");

		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				final String toPerson = mToPerson.getText().toString().trim();
				final String toPhoneNumber = mToPhoneNumber.getText().toString().trim();
				mCallback.onConversationPersonEditAsked(toPerson, toPhoneNumber);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dismiss();
				break;
		}

	}


	public interface Callback {
		public void onConversationPersonEditAsked(final String toPerson, final String toPhoneNumber);
	}

	private static final class Fallback implements Callback {
		private static final Callback INSTANCE = new Fallback();

		@Override
		public void onConversationPersonEditAsked(final String toPerson, final String toPhoneNumber) {
			Log.w(TAG, "Fallback: onConversationPersonEditAsked()");
		}

	}
}
