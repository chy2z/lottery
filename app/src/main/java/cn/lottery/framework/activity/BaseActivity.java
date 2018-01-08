package cn.lottery.framework.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import cn.lottery.R;
import cn.lottery.app.activity.HomePageActivity;
import cn.lottery.app.activity.scrollpage.ScrollPageActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.net.ErrorListener;
import cn.lottery.framework.net.JsonPostRequest;
import cn.lottery.framework.net.Net;
import cn.lottery.framework.net.SuccessListener;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.util.DateUtils;
import cn.lottery.framework.util.L;
import cn.lottery.framework.util.NetUtils;
import cn.lottery.framework.util.SPUtils;
import cn.lottery.framework.util.T;
import cn.lottery.framework.widget.NetDialog;

public class BaseActivity extends AppCompatActivity implements
		SuccessListener<JSONObject>, ErrorListener {
	private static long lastTimeStamp = 0l;
	private NetDialog dialog;
	protected SApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (SApplication) getApplication();
		dialog = new NetDialog(this);
	}

	/**
	 * 打开加载框.
	 */
	protected void showLoadingDialog() {
		dialog.addContent("正在请求数据...");
		dialog.addProcessBar();
		dialog.isTouchDismiss(false);
		dialog.show();
	}

	/**
	 * 打开加载框.
	 */
	protected void showLoadingDialog(String msg) {
		dialog.addContent(msg);
		dialog.addProcessBar();
		dialog.isTouchDismiss(false);
		dialog.show();
	}

	/**
	 * 显示提示对话框.
	 */
	protected void showTipDialog(String message) {
		dialog.isTouchDismiss(false);
		dialog.addContent(message);
		dialog.addConfirmButton("确定");
		dialog.setCloseClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.close();
			}
		});
		dialog.setConfirmClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.close();
			}
		});
		dialog.show();
	}

	/**
	 * 关闭加载对话款或者提示对话框.
	 */
	protected void closeDialog() {
		dialog.delProcessBar();
		dialog.close();
	}

	/**
	 * 隐藏加载对话款或者提示对话框.
	 */
	protected void hiddenDialog() {
		dialog.delProcessBar();
		//dialog.close(); //某些手机下关闭在打开会出现卡屏幕，所以不需要关闭，直接更新view内容即可
	}

	/**
	 * 对话框是否已显示
	 * @return
     */
	protected boolean isShowing() {
		return dialog.isShowing();
	}

	/**
	 * post请求
	 * @param url
	 * @param params
	 * @param userToken
     * @param tag
     */
	protected void post(final String url, final HashMap<String, String> params,final String userToken, Object tag) {
		if (NetUtils.isConnected(this)) {
			if (Config.IS_DEBUG) {
				L.e("----------网络请求开始[ 时间:" + DateUtils.getCurrentDate()
						+ " 标识:" + tag.toString() + " ]----------");
			}

			JsonPostRequest request = new JsonPostRequest(url,
					Net.addPublicParam(url, params, userToken), this, this);
			DefaultRetryPolicy policy = new DefaultRetryPolicy(5000, 0, 1f);// 禁止重连.
			request.setRetryPolicy(policy);
			request.setTag(tag);

			SApplication.getInstance().getRequestQueue().add(request);
		} else {
			hiddenDialog();
			showTipDialog("网络故障,请检查网络后重试.");
		}
	}

	/**
	 * 取消请求
	 * @param tag
     */
	protected void cancle(Object tag) {
		if (Config.IS_DEBUG) {
			L.d("取消请求,tag:" + tag);
		}

		SApplication.getInstance().getRequestQueue().cancelAll(tag);
	}

	@Override
	public void onSuccessResponse(JSONObject response, Object tag) {
		if (Config.IS_DEBUG) {
			L.i("---4.请求成功,服务器返回数据如下---");
			L.d(response.toString());
			L.e("----------网络请求结束[ 时间:" + DateUtils.getCurrentDate() + " 标识:"
					+ tag.toString() + " ]----------");
		}
		try {

			String taged = tag.toString();
			if (taged.contains("special_"))
			{
				JSONObject msg = response.getJSONObject("msg");
				boolean result = msg.getBoolean("success");
				if (!result) {
					handSuccess(response, tag);
				} else {
					handSuccess(response, tag);
				}

			} else {
				JSONObject msg = response.getJSONObject("msg");
				boolean result = msg.getBoolean("success");
				if (!result) {
					hiddenDialog();
					showTipDialog(msg.getString("info"));
					handError(tag, msg.getString("info"));
				} else {
					handSuccess(response, tag);
				}
			}

		} catch (JSONException e) {
			hiddenDialog();
			showTipDialog("返回数据格式错误.");
			e.printStackTrace();
		}
	}

	@Override
	public void onErrorResponse(VolleyError error, Object tag) {
		if (Config.IS_DEBUG) {
			L.i("---4.请求出错,出错信息如下---");
			// L.d("错误状态码:" + error.networkResponse.statusCode);
			L.e("----------网络请求结束[ 时间:" + DateUtils.getCurrentDate() + " 标识:"
					+ tag.toString() + " ]----------");
		}
		hiddenDialog();
		showTipDialog("请求出错,请重试.");
		handError(tag, "请求出错,请重试.");
	}

	/**
	 * 请求成功业务处理
	 * @param obj
	 * @param tag
     */
	protected void handSuccess(JSONObject obj, Object tag) {
		// 子类覆写.
	}

	/**
	 * 请求失败业务处理
	 * @param tag
	 * @param errorInfo
     */
	protected void handError(Object tag, String errorInfo) {
		// 子类覆写.
	}

	/**
	 * 退出程序.
	 * 
	 * @param context
	 */
	public static void exitApplication(final Activity context,boolean isConfirm) {
		if(!isConfirm) {
			long currentTimeStamp = System.currentTimeMillis();
			if (currentTimeStamp - lastTimeStamp > 1350L) {
				T.showLong(context, "再按一次推出");
			} else {
				Config.isActive = 0;
				//outlogin();
				outOpenIMlogin();
				context.finish();
			}
			lastTimeStamp = currentTimeStamp;
		}
		else {
			final AlertDialog myDialog = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT).create();
			myDialog.show();
			myDialog.getWindow().setContentView(R.layout.activity_dialog_confirm);
			Button butselect= (Button)myDialog.getWindow().findViewById(R.id.butselect) ;
			butselect.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					myDialog.dismiss();
					Config.isActive=0;
					//outlogin();
					outOpenIMlogin();
					context.finish();
				}
			});

			Button butcacel= (Button)myDialog.getWindow().findViewById(R.id.butcancel) ;
			butcacel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					myDialog.dismiss();
				}
			});

			RelativeLayout imageViewClose= (RelativeLayout)myDialog.getWindow().findViewById(R.id.layoutClose) ;
			imageViewClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					myDialog.dismiss();
				}
			});
		}
	}
	
	/**
	 * 退出登录数据
	 */
	public static void outlogin(){
		outOpenIMlogin();
		Config.userToken="";
		Config.status="";
		Config.balance="0";
		Config.wx_nicknames="请登录";
		Config.nickname="请登录";
		Config.wx_unionid="";
		Config.backgroundImg="drawable://" + R.drawable.headbg;
		Config.wx_headimgurls="drawable://" + R.drawable.login_default_head;
		Config.icon="drawable://" + R.drawable.login_default_head;
		Config.points=0;
		Config.news=0;
		Config.customerID="0";
		Config.IMUserId="";
		Config.IMPassword="";
		Config.isLogin=false;
		Config.phone="";

		SPUtils.put(SApplication.getInstance().getBaseContext(), "backgroundImg", Config.backgroundImg);
		SPUtils.put(SApplication.getInstance().getBaseContext(), "phone", Config.phone);
		SPUtils.put(SApplication.getInstance().getBaseContext(), "IMUserId", Config.IMUserId);
		SPUtils.put(SApplication.getInstance().getBaseContext(), "IMPassword", Config.IMPassword);
		SPUtils.put(SApplication.getInstance().getBaseContext(), "status", Config.status);
		SPUtils.put(SApplication.getInstance().getBaseContext(), "icon", Config.icon);
		SPUtils.put(SApplication.getInstance().getBaseContext(), "nickname", Config.nickname);
		SPUtils.put(SApplication.getInstance().getBaseContext(), "userToken", Config.userToken);
		SPUtils.put(SApplication.getInstance().getBaseContext(), "loginType", Config.loginType);
		SPUtils.put(SApplication.getInstance().getBaseContext(), "points", Config.points);
		SPUtils.put(SApplication.getInstance().getBaseContext(), "customerID", Config.customerID);

	}

	/**
	 * 退出云旺登录
	 */
	public static void outOpenIMlogin(){
		OpenIMLoginHelper.getInstance().loginOut_Sample();
	}

	/**
	 * 消息提示
	 * 
	 * @param msg
	 */
	public void toast(String msg) {
		Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 是否进入引导页
	 */
	public void isGoToGuidePage(){
		Intent it;

		//判断是否与当前版本一样， 不一样说明首次登录
		if(!(Boolean) SPUtils.get(getBaseContext(), "isFirstLogin","0.0").equals(Config.API_VERSION))
		{
			SPUtils.put(getBaseContext(), "isFirstLogin",Config.API_VERSION);
			it = new Intent(this, ScrollPageActivity.class);
		}
		else
		{
			it = new Intent(this, HomePageActivity.class);
		}

		startActivity(it);

		this.finish();
	}

	/**
	 * 获取设备UUID
	 * 
	 * @return
	 */
	public String getUUID() {

		final TelephonyManager tm = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, tmPhone, androidId;

		tmDevice = "" + tm.getDeviceId();

		tmSerial = "" + tm.getSimSerialNumber();

		androidId = ""
				+ android.provider.Settings.Secure.getString(
						getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		java.util.UUID deviceUuid = new java.util.UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

		String uniqueId = deviceUuid.toString();

		L.d("uuid=" + uniqueId);

		return uniqueId;
	}


	/**
	 * 设置输入法,如果当前页面输入法打开则关闭
	 * @param activity
	 */
	public void hideInputMethod(Activity activity){
		View a = activity.getCurrentFocus();
		if(a != null){
			InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			try {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//如果输入法打开则关闭，如果没打开则打开
		//InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.toggleSoftInput(0, InputMethodManager.RESULT_HIDDEN);
	}


	/**
	 * 强制显示输入法
	 * @param view
	 */
	public void toggleSoftInput(View view){
		try {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
		} catch (Exception e) {

		}
	}
}
