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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.deange.textfaker.ui.dialog.PatchedDialogFragment;

public class FragmentUtils {

	private static final String TAG = FragmentUtils.class.getSimpleName();

	private FragmentUtils() {
	}

	/**
	 * Finds a PatchedDialogFragment with a given callback, and sets it if non-null.
	 * Should be used on any orientation changes to recapture references to dialog fragments
	 *
	 * @param outDialog the patcheddialogfragment to find
	 * @param manager   the support fragment manager of the activity
	 * @param callback  the patcheddialogfragment's callback object
	 * @param dialogTag the tag associated with the dialog
	 */
	@SuppressWarnings("unchecked")
	public static void findDialogFragment(PatchedDialogFragment outDialog, final FragmentManager manager,
	                                      final Object callback, final String dialogTag) {
		if (outDialog == null) {
			try {
				outDialog = (PatchedDialogFragment) manager.findFragmentByTag(dialogTag);
				if (outDialog != null) {
					outDialog.setCallback(callback);
				}
			} catch (final ClassCastException e) {
				Log.e(TAG, "Error attempting to find DialogFragment with tag: \"" + dialogTag + "\": " + e.toString());
			}
		}
	}

	/**
	 * Find a Fragment within a given activity, and recaptures the reference
	 *
	 * @param outFragment the fragment to find
	 * @param manager     the support fragment manager of the activity
	 * @param fragmentTag the tag associated with the fragment
	 */
	public static void findFragment(Fragment outFragment, final FragmentManager manager, final String fragmentTag) {
		if (outFragment == null) {
			try {
				outFragment = manager.findFragmentByTag(fragmentTag);
			} catch (final ClassCastException e) {
				Log.e(TAG, "Error attempting to find Fragment with tag: \"" + fragmentTag + "\": " + e.toString());
			}
		}
	}

}
