package cn.lottery.framework.net;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.lottery.framework.Config;
import cn.lottery.framework.util.DateUtils;
import cn.lottery.framework.util.L;
import cn.lottery.framework.util.SecurityUtils;

public class Net {

	/**
	 * 添加公共参数,生成签名.
	 * 
	 * @param params
	 *            业务参数.没有传空map.
	 * @param userToken
	 *            没有userToken参数,传null.
	 * @return
	 */
	public static final Map<String, String> addPublicParam(String url, HashMap<String, String> params, String userToken) {
		params.put("timestamp", DateUtils.getCurrentDate());
		params.put("ver", Config.API_VERSION);
		params.put("client_type", Config.CLIENT_TYPE);
		params.put("sign_method", Config.SIGN_METHOD);
		params.put("format", Config.DATA_FORMAT);
		params.put("app_key", Config.APP_KEY);
		if (userToken != null) {
			params.put("user_token", userToken);
		}
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			builder.append(keys[i].toString() + params.get(keys[i].toString()));
		}
		String result = builder.toString() + Config.APP_SECRET;
		String sign = SecurityUtils.MD5(result);
		params.put("sign", sign);

		if (Config.IS_DEBUG) {
			L.i("---1.请求url地址---");
			L.d(url);
		}
		
		StringBuffer requestStr = new StringBuffer();
		requestStr.append(url);
		requestStr.append("?");
		Object[] ks = params.keySet().toArray();
		
		if (Config.IS_DEBUG) {
			L.i("---2.请求参数---");
		}
		
		for (int i = 0; i < ks.length; i++) {
			requestStr.append(ks[i].toString() + "=" + params.get(ks[i].toString()));
			if (i != (params.size() - 1)) {
				requestStr.append("&");
			}
		}
		
		if (Config.IS_DEBUG) {
			L.d(params.toString());
			L.i("---3.请求详情---");
			L.d(requestStr.toString());
		}
		
		return params;
	}
}
