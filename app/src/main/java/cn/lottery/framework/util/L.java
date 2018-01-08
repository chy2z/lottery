package cn.lottery.framework.util;

import android.util.Log;
import cn.lottery.framework.Config;

/**
 * Log统一管理类
 * 
 * @author specter
 * 
 */
public class L {

	private L() {
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	// 下面四个是默认tag的函数
	public static void i(String msg) {
		if (Config.IS_DEBUG)
			Log.i(Config.DEBUG_TAG, msg);
	}

	public static void d(String msg) {
		if (Config.IS_DEBUG)
			Log.d(Config.DEBUG_TAG, msg);
	}

	public static void e(String msg) {
		if (Config.IS_DEBUG)
			Log.e(Config.DEBUG_TAG, msg);
	}

	public static void v(String msg) {
		if (Config.IS_DEBUG)
			Log.v(Config.DEBUG_TAG, msg);
	}

	// 下面是传入自定义tag的函数
	public static void i(String tag, String msg) {
		if (Config.IS_DEBUG)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (Config.IS_DEBUG)
			Log.i(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (Config.IS_DEBUG)
			Log.i(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (Config.IS_DEBUG)
			Log.i(tag, msg);
	}
}