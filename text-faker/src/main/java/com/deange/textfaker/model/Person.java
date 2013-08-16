package com.deange.textfaker.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.ByteArrayOutputStream;

@DatabaseTable(tableName = Person.TABLE)
public class Person extends BaseModel {

	public static final String TABLE = "person";

	public static final String NAME = "name";
	public static final String AVATAR = "avatar";
	public static final String PHONENUMBER = "phone_number";

	@DatabaseField(columnName = NAME)
	private String mName;

	@DatabaseField(columnName = AVATAR, dataType = DataType.BYTE_ARRAY)
	private byte[] mAvatarBytes;

	@DatabaseField(columnName = PHONENUMBER)
	private String mPhoneNumber;

	public static Person createInstance(final String name, final String phoneNumber) {
		return new Person(name, phoneNumber, null);
	}

	private Person(final String name, final String phoneNumber, final byte[] avatarBytes) {
		mName = name;
		mPhoneNumber = phoneNumber;
		mAvatarBytes = avatarBytes;
	}

	public Person() {
		// Needed by OrmLite
	}

	public String getName() {
		return mName;
	}

	public void setName(final String name) {
		mName = name;
	}

	public Bitmap getAvatar() {
		if (mAvatarBytes != null) {
			return BitmapFactory.decodeByteArray(mAvatarBytes, 0, mAvatarBytes.length);
		} else {
			return null;
		}
	}

	public void setAvatar(final Bitmap avatar) {
		if (avatar == null) {
			mAvatarBytes = null;

		} else {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			avatar.compress(Bitmap.CompressFormat.PNG, 100, stream);
			mAvatarBytes = stream.toByteArray();
		}
	}
}
