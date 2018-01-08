package cn.lottery.app.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import cn.lottery.R;
import cn.lottery.app.activity.upgrade.UpdateDialogActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.util.L;
import cn.lottery.framework.util.SPUtils;
/**
 * app引导页.
 * 
 */
public class LoadingActivity extends BaseActivity {

	public static LoadingActivity currentActivity;

	String registrationId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_loading);						
		
		currentActivity = this;
		
		registrationId= app.getPushAgent().getRegistrationId();
				
		//闪屏的核心代码
		new Thread(new MyDelayThread()).start();
	}
		

	private void initData() {

		Config.phone=SPUtils.get(getBaseContext(), "phone", Config.phone).toString();
		Config.backgroundImg=SPUtils.get(getBaseContext(), "backgroundImg", Config.backgroundImg).toString();
		Config.IMUserId=SPUtils.get(getBaseContext(), "IMUserId", Config.IMUserId).toString();
		Config.IMPassword=SPUtils.get(getBaseContext(), "IMPassword", Config.IMPassword).toString();
		Config.status=SPUtils.get(getBaseContext(), "status", Config.status).toString();
		Config.icon=SPUtils.get(getBaseContext(), "icon", Config.icon).toString();
		Config.nickname=SPUtils.get(getBaseContext(), "nickname", Config.nickname).toString();
		Config.userToken=SPUtils.get(getBaseContext(), "userToken", Config.userToken).toString();
		Config.loginType=SPUtils.get(getBaseContext(), "loginType", Config.loginType).toString();
		Config.points=(Integer)SPUtils.get(getBaseContext(), "points", Config.points);
		Config.customerID=SPUtils.get(getBaseContext(), "customerID", Config.customerID).toString();

		if(registrationId.equals(""))
		{	
			registrationId= app.getPushAgent().getRegistrationId();
		}

		Config.deviceToken=registrationId;

		Config.UUID=getUUID();

		L.d("友盟token:",registrationId);

		L.i("UUID", Config.UUID);

		//获取配置信息
		requestSysInfo();
	}

	/**
	 * 请求版本检查
	 */
	private void requestChechAppVersion() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("version", Config.API_VERSION);
		post(Config.URL_PREFIX + RequestUrl.chechAppVersion, params, null,"requestChechAppVersion");
	}

	/**
	 * 请求成功返回
	 */
	@Override
	protected void handSuccess(JSONObject obj, Object tag) {
		if (tag.equals("getSysInfo")) {
			processGetSysInfo(obj);
		}
		if (tag.equals("requestChechAppVersion")) {
			processChechAppVersion(obj);
		}
	}
	
	
    /**
     * 请求出错
     */
	@Override
	protected void handError(Object tag, String errorInfo) {

	}

	private void requestSysInfo() {
		HashMap<String, String> params = new HashMap<String, String>();		
		post(Config.URL_PREFIX + RequestUrl.getSysInfo, params, null,"getSysInfo");
	}


	/**
	 * 检查版本
	 * @param json
     */
	private void processChechAppVersion(JSONObject json)
	{
		try {
			
			JSONObject versionInfo = json.getJSONObject("map");
			
			String version=versionInfo.getString("version");	
			
			Integer isFocused=versionInfo.getInt("isFocused");	
			
			String downAddress=versionInfo.getString("downAddress");	
			
			String content=versionInfo.getString("desc");
			
			//toast(version);
			
			if(!version.equals("")&&!version.equals(Config.API_VERSION)) //版本不一致
			{
				// 有新版本.
				Intent it = new Intent(LoadingActivity.this, UpdateDialogActivity.class);
				it.putExtra("isFocused", isFocused+"");
				it.putExtra("downAddress", downAddress);
				it.putExtra("version", version);
				it.putExtra("content", content);
				startActivity(it);
			}
			else //版本一致
			{
				isGoToGuidePage();
			}				
			
		} catch (JSONException e) {			
			e.printStackTrace();
		}			
	}


	/**
	 * 获取配置信息
	 * @param json
     */
	private void processGetSysInfo(JSONObject json)
	{
		try{

			Config.uploadurl= json.getString("uploadPicUrl");

			requestChechAppVersion();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

    /*
    /// 延迟线程
    /// 作者：chy
    /// 时间：2016/8/25 21:09
    /// <param name=""></param>
    */
	public class MyDelayThread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
				initData();
			} catch (Exception e) {
			}
		}
	}

}
