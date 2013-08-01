package com.deange.textfaker.utils;

import com.deange.textfaker.TFBuildConfig;

public class Formatter {

	/*
		Generate a package prefix for an extra variable in an intent
	 */
	public static String makeExtra(final String name) {
		return TFBuildConfig.PACKAGE_PREFIX + "." + name;
	}

}
