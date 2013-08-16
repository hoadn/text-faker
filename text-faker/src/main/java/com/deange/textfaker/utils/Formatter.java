package com.deange.textfaker.utils;

import android.content.Context;

import com.deange.textfaker.TFBuildConfig;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Formatter {

	private static DateFormat mTimeFormatter;
	private static DateFormat mDateFormatter;
	private static DateFormat mYearFormatter;

	/**
	 * Initialize the date formatters
	 *
	 * @param context a context to retrieve the user-preferred date/time formats
	 */
	public static void createInstance(final Context context) {
		mTimeFormatter = android.text.format.DateFormat.getTimeFormat(context);
		mYearFormatter = android.text.format.DateFormat.getMediumDateFormat(context);
		mDateFormatter = new SimpleDateFormat("MMM dd");
	}

	/**
	 * Formats a date into its time value
	 *
	 * @param date a date to be formatted
	 * @return the formatted date as a time
	 */
	public static String formatTime(final Date date) {
		return mTimeFormatter.format(date);
	}

	/**
	 * Formats a date into its day and month value
	 *
	 * @param date a date to be formatted
	 * @return the formatted date as a date and month
	 */
	public static String formatMediumDate(final Date date) {
		return mDateFormatter.format(date);
	}

	/**
	 * Formats a date into its day, month, and year value
	 *
	 * @param date a date to be formatted
	 * @return the formatted date as a date, month, and year
	 */
	public static String formatLongDate(final Date date) {
		return mYearFormatter.format(date);
	}

	/**
	 * Convenience to prepend a package prefix to intent extra names
	 *
	 * @param clazz the name of the class which the intent is being passed to
	 * @param name  the intent extra name being created
	 * @return the formatted intent extra name with the package prefix
	 */
	public static String makeExtra(final String clazz, final String name) {
		return TFBuildConfig.PACKAGE_PREFIX + "." + clazz + "." + name;
	}

	/**
	 * Convenience to prepend a package prefix to SharedPreferences keys
	 *
	 * @param prefName the name of the preference
	 * @return the formatted preference key name
	 */
	public static String makePrefs(final String prefName) {
		return TFBuildConfig.PACKAGE_PREFIX + "." + prefName;
	}

}
