package com.deange.textfaker.utils;

public final class BooleanConverter {

	private BooleanConverter() {
	}

	public static boolean convert(final int value) {
		return (value != 0);
	}

	public static int convert(final boolean value) {
		return (value ? 1 : 0);
	}

}
