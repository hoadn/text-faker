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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.deange.datetimepicker.date.DatePickerDialog;
import com.deange.datetimepicker.time.RadialPickerLayout;
import com.deange.datetimepicker.time.TimePickerDialog;
import com.deange.textfaker.R;
import com.deange.textfaker.model.ConversationMessage;
import com.deange.textfaker.utils.Formatter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EditMessageDialog extends PatchedDialogFragment<EditMessageDialog.Callback>
		implements DialogInterface.OnClickListener, View.OnClickListener, DatePickerDialog.OnDateSetListener,
		TimePickerDialog.OnTimeSetListener, TextWatcher {

	public static final String TAG = EditMessageDialog.class.getSimpleName();
	private static final String DATE_DIALOG_TAG = TAG + ".date";
	private static final String TIME_DIALOG_TAG = TAG + ".time";
	private Callback mCallback = Fallback.INSTANCE;
	private ConversationMessage mMessage;
	private EditText mMessageEdit;
	private Button mDateButton;
	private Button mTimeButton;
	private Button mDoneButton;
	private Date mNewDate;
	private Date mNewTime;

	public EditMessageDialog(final ConversationMessage message, final Callback callback) {
		mMessage = message;
		setRetainInstance(true);
		setCallback(callback);
	}

	public static void show(EditMessageDialog dialog, final Callback callback, final FragmentManager manager,
			final ConversationMessage message) {
		Log.v(TAG, "show()");

		final FragmentTransaction transaction = manager.beginTransaction();
		if (dialog != null) {
			transaction.remove(dialog);
		}

		dialog = new EditMessageDialog(message, callback);
		dialog.show(transaction, EditMessageDialog.TAG);
	}

	public void setCallback(final Callback callback) {
		Log.v(TAG, "setCallback()");

		mCallback = (callback == null) ? Fallback.INSTANCE : callback;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		Log.v(TAG, "onCreateDialog()");

		final View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_message, null);

		mMessageEdit = (EditText) rootView.findViewById(R.id.dialog_message_text);
		mDateButton = (Button) rootView.findViewById(R.id.dialog_message_date);
		mTimeButton = (Button) rootView.findViewById(R.id.dialog_message_time);

		mNewDate = new Date(mMessage.getTime());
		mNewTime = new Date(mMessage.getTime());

		mMessageEdit.addTextChangedListener(this);
		mMessageEdit.setText(mMessage.getText());
		mDateButton.setOnClickListener(this);
		mTimeButton.setOnClickListener(this);
		mDateButton.setText(Formatter.formatLongDate(mNewDate));
		mTimeButton.setText(Formatter.formatTime(mNewTime));

		final AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setView(rootView)
				.setTitle(R.string.dialog_edit_message_title)
				.setPositiveButton(android.R.string.ok, this)
				.setNegativeButton(android.R.string.cancel, this)
				.create();

		return dialog;
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart()");

		super.onStart();
		mDoneButton = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
	}

	@Override
	public void onClick(final DialogInterface dialog, final int which) {
		Log.v(TAG, "onClick()");

		switch(which) {
			case DialogInterface.BUTTON_POSITIVE:
				final String newMessage = mMessageEdit.getText().toString().trim();
				mCallback.onMessageUpdateAsked(getFinalDateValues(), newMessage);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dismiss();
				break;
		}
	}

	@Override
	public void onClick(final View v) {
		Log.v(TAG, "onClick()");

		switch(v.getId()) {
			case R.id.dialog_message_date:
				showDateDialog();
				break;
			case R.id.dialog_message_time:
				showTimeDialog();
				break;
		}
	}

	private void showDateDialog() {
		Log.v(TAG, "showDateDialog()");

		final Calendar cal = Calendar.getInstance();
		cal.setTime(mNewDate);
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH);
		final int day = cal.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog.newInstance(this, year, month, day).show(getActivity().getFragmentManager(), DATE_DIALOG_TAG);
	}

	private void showTimeDialog() {
		Log.v(TAG, "showTimeDialog()");

		final Calendar cal = Calendar.getInstance();
		cal.setTime(mNewTime);
		final int hour = cal.get(Calendar.HOUR_OF_DAY);
		final int minute = cal.get(Calendar.MINUTE);

		TimePickerDialog.newInstance(this, hour, minute, false).show(getActivity().getFragmentManager(),
				TIME_DIALOG_TAG);
	}

	private Date getFinalDateValues() {
		Log.v(TAG, "getFinalDateValues()");

		final Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(mNewDate);
		final Calendar timeCal = Calendar.getInstance();
		timeCal.setTime(mNewTime);

		dateCal.set(Calendar.HOUR_OF_DAY, (timeCal.get(Calendar.HOUR_OF_DAY)));
		dateCal.set(Calendar.MINUTE, (timeCal.get(Calendar.MINUTE)));

		return dateCal.getTime();
	}

	@Override
	public void onDateSet(final DatePickerDialog dialog, final int year, final int monthOfYear, final int dayOfMonth) {
		Log.v(TAG, "onDateSet()");

		mNewDate = (new GregorianCalendar(year, monthOfYear, dayOfMonth)).getTime();
		mDateButton.setText(Formatter.formatLongDate(mNewDate));
	}

	@Override
	public void onTimeSet(final RadialPickerLayout view, final int hourOfDay, final int minute) {
		Log.v(TAG, "onTimeSet()");

		final Calendar cal = Calendar.getInstance();
		cal.setTime(mNewDate);
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);

		mNewTime = cal.getTime();
		mTimeButton.setText(Formatter.formatTime(mNewTime));
	}

	@Override
	public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
	}

	@Override
	public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
		if (mDoneButton != null) {
			mDoneButton.setEnabled(s.toString().trim().length() > 0);
		}
	}

	@Override
	public void afterTextChanged(final Editable s) {
	}


	public interface Callback {

		public void onMessageUpdateAsked(final Date newDate, final String newMessage);
	}

	private static final class Fallback implements Callback {

		private static final Callback INSTANCE = new Fallback();

		@Override
		public void onMessageUpdateAsked(final Date newDate, final String newMessage) {
			Log.w(TAG, "Fallback: onMessageUpdateAsked()");
		}

	}
}
