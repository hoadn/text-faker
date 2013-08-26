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

public class BooleanConverter {

	private BooleanConverter() {
	}

	/**
	 * Converts an int to a boolean
	 *
	 * @param value an integer
	 * @return the corresponding boolean
	 */
	public static boolean convert(final int value) {
		return (value != 0);
	}

	/**
	 * Converts a boolean to an int
	 * @param value a boolean
	 * @return the corresponding int
	 */
	public static int convert(final boolean value) {
		return (value ? 1 : 0);
	}

}
