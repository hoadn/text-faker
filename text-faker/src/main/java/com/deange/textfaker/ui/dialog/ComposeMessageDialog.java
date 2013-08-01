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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.deange.textfaker.R;

public class ComposeMessageDialog extends PatchedDialogFragment implements DialogInterface
		.OnClickListener {

	public static final String TAG = ComposeMessageDialog.class.getSimpleName();

	private Callback mCallback = Fallback.INSTANCE;
	private EditText mToPerson;

	public static ComposeMessageDialog createInstance() {

		final ComposeMessageDialog dialog = new ComposeMessageDialog();

		return dialog;

	}

	public ComposeMessageDialog() {
		//Needed by Android
	}

	public interface Callback {
		public void composeNewConversationAsked(final String toPerson);
	}

	public void setCallback(final Callback callback) {
		Log.v(TAG, "setCallback()");

		mCallback = (callback == null) ? Fallback.INSTANCE : callback;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		Log.v(TAG, "onCreateDialog()");

		final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_compose_new,
				null);

		mToPerson = (EditText) view.findViewById(R.id.dialog_compose_new_to);

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.compose_message)
				.setView(view)
				.setPositiveButton(android.R.string.ok, this)
				.setNegativeButton(android.R.string.cancel, this)
				.create();

	}


	@Override
	public void onClick(final DialogInterface dialog, final int which) {
		Log.v(TAG, "onClick");

		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				final String toPerson = mToPerson.getText().toString().trim();
				mCallback.composeNewConversationAsked(toPerson);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dismiss();
				break;
		}

	}

	private static final class Fallback implements Callback {
		private static final Callback INSTANCE = new Fallback();

		@Override
		public void composeNewConversationAsked(final String toPerson) {
			Log.w(TAG, "Fallback: composeNewConversationAsked()");
		}

	}
}
