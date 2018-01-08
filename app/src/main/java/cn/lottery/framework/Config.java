package cn.lottery.framework;

import android.app.Activity;

import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.app.activity.scrollpage.ScrollPageActivity;

public class Config {

	// --------------------------------------网络请求配置-------------------------------------//
	/**
	 * 网络请求线程数
	 */
	public static final int NET_REQUEST_THREAD_POOL_SIZE = 4;//暂时没用.默认是四个.
	/**
	 * 生成参数签名的密钥
	 */
	public static final String APP_SECRET = "makanggreat";
	/**
	 * 分配给用户的app_key,标识应用
	 */
	public static final String APP_KEY = "greatmakang";
	/**
	 * 标识请求的终端类型
	 */
	public static final String CLIENT_TYPE = "android";
	/**
	 * 请求的数据格式
	 */
	public static final String DATA_FORMAT = "json";
	/**
	 * 请求的api版本
	 */
	public static final String API_VERSION = "1.0.0";
	/**
	 * 参数签名编码方式
	 */
	public static final String SIGN_METHOD = "md5";
	/**
	 * 网路请求地址
	 */
	public static final String URL_PREFIX = "http://120.25.211.228:8080/KmAPI";
	public static final String H5_URL_PREFIX = "http://120.25.211.228:8080/KmAPI";

	//public static final String URL_PREFIX ="http://api.comeon.group";
	//public static final String H5_URL_PREFIX="http://api.comeon.group";

	// --------------------------------------网络缓存配置-------------------------------------//

	//public static final String CACHE_DIR = "kamang";

	// --------------------------------------系统调试配置-------------------------------------//

	public static final boolean IS_DEBUG = true;

	public static final String DEBUG_TAG = "printlog";

	//---------------------------------------SharedPreferences 配置-------------------------//

	public static final String SP_FILE_NAME = "ka_mang";

	//---------------------------------------服务器图片路径配置----------------------------------//

	/**
	 * 主页面
	 */
	public static Activity mainActivity = null;

	/**
	 * 滚动页面
	 */
	public static ScrollPageActivity spActity;

	public static String appName = "咖忙";

	public static int waitValidCodeTime = 60;

	public static int points = 0;

	public static int news = 0;

	public static int userPwdLength = 6;

	public static int alipayLength = 6;

	//用户状态
	public static String status = "";

	//云旺账户
	public static String IMPassword = "";

	//云旺账户
	public static String IMUserId = "";

	public static String loginType = "";

	public static String IMG_HOST_URL = "";

	public static String userToken = "";

	public static String UUID = "";

	public static String deviceToken = "";

	public static String customerID = "0";

	public static String phone = "";

	//余额
	public static String balance = "";

	//性别
	public static String gender = "";

	public static String wx_nicknames = "请登录";

	public static String wx_type = "";

	public static String wx_unionid = "";

	public static String wx_headimgurls = "drawable://" + R.drawable.login_default_head;

	public static String nickname = "请登录";

	public static String icon = "drawable://" + R.drawable.login_default_head;

	public static Boolean isSwitchUserSucess = false;

	public static String uploadurl = "http://139.196.36.206:8070/kamang/upload/uploadImg";

	public static String backgroundImg="drawable://" + R.drawable.headbg;

	public static int notifyid = 900;

	public static int isActive = 0;

	public static boolean isLogin = false;

	public static double gpslat = 0; //纬度 31.573565

	public static double gpslon = 0; //经度 118.497308

	public static String gpsAddr = "";

	public static String gpslocationDescribe = "";

	public static HashMap<String, Object> traceDynamDetail=null;

}