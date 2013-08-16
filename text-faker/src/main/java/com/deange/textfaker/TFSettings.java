package com.deange.textfaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.deange.textfaker.model.BaseModel;
import com.deange.textfaker.utils.Formatter;

public class TFSettings {

	private SharedPreferences mPreferences;

	private static TFSettings mInstance;
	private static Object sLock = new Object();
	private Context mContext;

	private static final String OWNER_ID = Formatter.makePrefs("ownerId");


	private TFSettings(final Context context) {
		mContext = context.getApplicationContext();

		mPreferences = mContext.getSharedPreferences(TFBuildConfig.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

	}

	public static void createInstance(final Context context) {
		if (mInstance == null) {
			mInstance = new TFSettings(context);

		} else {
			throw new IllegalStateException("TFSettings already created");
		}
	}

	public static TFSettings getInstance() {
		synchronized (sLock) {
			if (mInstance == null) {
				throw new IllegalStateException("TFSettings not instantiated");
			}
			return mInstance;
		}
	}

	public long getOwnerId() {
		synchronized (sLock) {
			return mPreferences.getLong(OWNER_ID, BaseModel.INVALID_LOCAL_ID);
		}
	}

	public void setOwnerId(final long localId) {
		synchronized (sLock) {
			applyChanges(mPreferences.edit().putLong(OWNER_ID, localId));
		}
	}


	private void applyChanges(final SharedPreferences.Editor editor) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			editor.apply();
		} else {
			editor.commit();
		}
	}


}
