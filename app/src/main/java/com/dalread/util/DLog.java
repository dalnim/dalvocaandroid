package com.dalread.util;

import android.util.Log;

/**
 * Override Log of android, not show when DEBUG mode off
 * 
 * @author rakuten
 * 
 */
public class DLog {
	/**
	 * 
	 * @param Tag
	 * @param log
	 */
	public static void v(String Tag, String log) {
		if (Constant.DEBUG)
			Log.v(Tag, log);
	}

	/**
	 *
	 * @param Tag
	 * @param log
	 * @param e
	 */
	public static void v(String Tag, String log, Throwable e) {
		if (Constant.DEBUG)
			Log.v(Tag, log, e);
	}

	/**
	 *
	 * @param Tag
	 * @param log
	 */
	public static void d(String Tag, String log) {
		if (Constant.DEBUG)
			Log.d(Tag, log);
	}

	/**
	 *
	 * @param Tag
	 * @param log
	 * @param e
	 */
	public static void d(String Tag, String log, Throwable e) {
		if (Constant.DEBUG)
			Log.d(Tag, log, e);
	}

	/**
	 *
	 * @param Tag
	 * @param log
	 */
	public static void e(String Tag, String log) {
		if (Constant.DEBUG)
			Log.e(Tag, log);
	}

	/**
	 *
	 * @param Tag
	 * @param log
	 * @param e
	 */
	public static void e(String Tag, String log, Throwable e) {
		if (Constant.DEBUG)
			Log.e(Tag, log, e);
	}

	/**
	 *
	 * @param Tag
	 * @param log
	 */
	public static void i(String Tag, String log) {
		if (Constant.DEBUG)
			Log.i(Tag, log);
	}

	/**
	 *
	 * @param Tag
	 * @param log
	 * @param e
	 */
	public static void i(String Tag, String log, Throwable e) {
		if (Constant.DEBUG)
			Log.i(Tag, log, e);
	}

	/**
	 *
	 * @param Tag
	 * @param log
	 */
	public static void w(String Tag, String log) {
		if (Constant.DEBUG)
			Log.w(Tag, log);
	}

	/**
	 *
	 * @param Tag
	 * @param log
	 * @param e
	 */
	public static void w(String Tag, String log, Throwable e) {
		if (Constant.DEBUG)
			Log.w(Tag, log, e);
	}

}
