package cn.lottery.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.app.activity.login.EditPasswordActivity;
import cn.lottery.app.activity.login.LoginActivity;
import cn.lottery.app.activity.myself.MyChangePhoneActivity;
import cn.lottery.app.activity.myself.MyWalletMoneyForgetPasswordActivity;
import cn.lottery.app.activity.upgrade.UpdateDialogActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseActivity;

public class SettingActivity  extends BaseActivity implements OnClickListener {

	LinearLayout exit,update,editpwd,editPayPwd,editPhone;

	LinearLayout layoutBack;

	ImageView updateTipImg;

	boolean isCheckUpdate=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_km_setting);

		initView();

		requestChechAppVersion();
	}

	private void initView() {
		update= (LinearLayout) findViewById(R.id.update);
		update.setOnClickListener(this);

		exit= (LinearLayout) findViewById(R.id.exit);
		exit.setOnClickListener(this);

		editpwd= (LinearLayout) findViewById(R.id.editpwd);
		editpwd.setOnClickListener(this);

		editPayPwd= (LinearLayout) findViewById(R.id.editPayPwd);
		editPayPwd.setOnClickListener(this);

		editPhone= (LinearLayout) findViewById(R.id.editPhone);
		editPhone.setOnClickListener(this);

		layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(this);

		updateTipImg = (ImageView) findViewById(R.id.updateTipImg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.layoutBack:
				finish();
				break;
			case R.id.exit:
				outlogin();
				toast("退出登录成功");
				Intent it = new Intent(getBaseContext(), LoginActivity.class);
				it.putExtra("setting","setting");
				startActivity(it);
				finish();
				break;
			case R.id.update:
				isCheckUpdate=false;
				requestChechAppVersion();
				break;
			case R.id.editPayPwd:
				if (!Config.userToken.equals("")&&!Config.phone.equals("")) {
					Intent intent = new Intent(this,MyWalletMoneyForgetPasswordActivity.class);
					startActivity(intent);
				} else {
					startActivity(new Intent(this, LoginActivity.class));
				}
				break;
			case R.id.editPhone:
				if (!Config.userToken.equals("")) {
					Intent intent = new Intent(this,MyChangePhoneActivity.class);
					startActivity(intent);
				} else {
					startActivity(new Intent(this, LoginActivity.class));
				}
				break;
			case R.id.editpwd:
				if (!Config.userToken.equals("")) {
					Intent intent = new Intent(this,EditPasswordActivity.class);
					startActivity(intent);
				} else {
					startActivity(new Intent(this, LoginActivity.class));
				}
				break;
			default:
				break;
		}
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

			if(isCheckUpdate){
				if (!version.equals("") && !version.equals(Config.API_VERSION))
				{
					updateTipImg.setVisibility(View.VISIBLE);
				}
			}else {
				if (!version.equals("") && !version.equals(Config.API_VERSION)) //版本不一致
				{
					// 有新版本.
					Intent it = new Intent(getBaseContext(), UpdateDialogActivity.class);
					it.putExtra("isFocused", isFocused + "");
					it.putExtra("downAddress", downAddress);
					it.putExtra("version", version);
					it.putExtra("content", content);
					startActivity(it);
				} else //版本一致
				{
					toast("已经是最新版本");
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}