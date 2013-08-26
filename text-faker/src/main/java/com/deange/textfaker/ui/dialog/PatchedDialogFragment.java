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

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public abstract class PatchedDialogFragment<Callback> extends DialogFragment implements Callable<Callback> {

	@Override
	public void onDestroyView() {
		// WORKAROUND: http://code.google.com/p/android/issues/detail?id=17423
		if ((getDialog() != null) && getRetainInstance()) {
			getDialog().setDismissMessage(null);
		}
		super.onDestroyView();
	}

	// WORKAROUND: https://code.google.com/p/android/issues/detail?id=23761
	@Override
	public void show(final FragmentManager manager, final String tag) {
		show(manager.beginTransaction(), tag, false);
	}

	@Override
	public int show(final FragmentTransaction transaction, final String tag) {
		return show(transaction, tag, false);
	}

	public int show(final FragmentTransaction transaction, final String tag,
	                final boolean allowStateLoss) {
		try {
			transaction.add(this, tag);
			return allowStateLoss ? transaction.commitAllowingStateLoss() : transaction.commit();

		} catch (final IllegalStateException e) {
			return 0;
		}
	}

}