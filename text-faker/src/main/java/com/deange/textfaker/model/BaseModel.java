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

package com.deange.textfaker.model;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;

public abstract class BaseModel {

	public static final long INVALID_LOCAL_ID = -1;
	public static final String LOCAL_ID = BaseColumns._ID;

	@DatabaseField(columnName = LOCAL_ID, generatedId = true)
	long mId;

	public long getId() {
		return mId;
	}

	public void setId(final long id) {
		mId = id;
	}

}
