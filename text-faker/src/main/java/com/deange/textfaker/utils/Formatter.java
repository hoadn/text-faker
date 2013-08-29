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

package com.deange.textfaker.utils;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.telephony.PhoneNumberUtils;

import com.deange.textfaker.TFBuildConfig;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Formatter {

	private static DateFormat mTimeFormatter;
	private static DateFormat mDateFormatter;
	private static DateFormat mYearFormatter;

	private Formatter() {
	}

	/**
	 * Initialize the date formatters
	 * @param context a context to retrieve the user-preferred date/time formats
	 */
	public static void createInstance(final Context context) {
		mTimeFormatter = android.text.format.DateFormat.getTimeFormat(context);
		mYearFormatter = android.text.format.DateFormat.getMediumDateFormat(context);
		mDateFormatter = new SimpleDateFormat("MMM dd");
	}

	/**
	 * Formats a date into its time value
	 * @param date a date to be formatted
	 * @return the formatted date as a time
	 */
	public static String formatTime(final Date date) {
		return mTimeFormatter.format(date);
	}

	/**
	 * Formats a date into its day and month value
	 * @param date a date to be formatted
	 * @return the formatted date as a date and month
	 */
	public static String formatMediumDate(final Date date) {
		return mDateFormatter.format(date);
	}

	/**
	 * Formats a date into its day, month, and year value
	 * @param date a date to be formatted
	 * @return the formatted date as a date, month, and year
	 */
	public static String formatLongDate(final Date date) {
		return mYearFormatter.format(date);
	}

	/**
	 * Formats a date appropriately
	 * @param date a date to be formatted
	 * @return the formatted dat as either a time, date and month, or date, month, and year
	 */
	public static String formatMessageDate(final Date date) {
		final Calendar now = Calendar.getInstance();
		final Calendar then = Calendar.getInstance();
		then.setTimeInMillis(date.getTime());

		String formattedDate;
		if (now.get(Calendar.YEAR) != then.get(Calendar.YEAR)) {
			// Different year
			formattedDate = Formatter.formatLongDate(then.getTime());

		} else if (now.get(Calendar.DAY_OF_YEAR) != then.get(Calendar.DAY_OF_YEAR)) {
			// Different day
			formattedDate = Formatter.formatMediumDate(then.getTime());

		} else {
			// Today
			formattedDate = Formatter.formatTime(then.getTime());
		}

		return formattedDate;
	}

	/**
	 * Formats a phone number into its correct country-specific format
	 * @param phoneNumber the input phone number string
	 * @return the formatted phone number string
	 */
	public static String formatPhoneNumber(final String phoneNumber) {
		return PhoneNumberUtils.formatNumber(phoneNumber);
	}

	/**
	 * Copies a snippet of text to the device's clipboard
	 * @param context a context to retrieve the system clipboard
	 * @param text    the text to be copied
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void copyToClipboard(final Context context, final String text) {

		final Object clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			((android.text.ClipboardManager) clipboard).setText(text);

		} else {
			((android.content.ClipboardManager) clipboard).setPrimaryClip(ClipData.newPlainText(null, text));
		}
	}

	/**
	 * Convenience to prepend a package prefix to intent extra names
	 * @param clazzTag the name of the class which the extra is for
	 * @param name     the intent extra name being created
	 * @return the formatted intent extra name with the package prefix
	 */
	public static String makeExtra(final String clazzTag, final String name) {
		return TFBuildConfig.PACKAGE_PREFIX + "." + clazzTag + "." + name;
	}

	/**
	 * Convenience to prepend a package prefix to SharedPreferences keys
	 * @param prefName the name of the preference
	 * @return the formatted preference key name
	 */
	public static String makePrefs(final String prefName) {
		return TFBuildConfig.PACKAGE_PREFIX + "." + prefName;
	}

}
