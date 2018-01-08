package cn.lottery.framework.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.lottery.framework.Config;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.net.ErrorListener;
import cn.lottery.framework.net.JsonPostRequest;
import cn.lottery.framework.net.Net;
import cn.lottery.framework.net.SuccessListener;
import cn.lottery.framework.util.DateUtils;
import cn.lottery.framework.util.L;
import cn.lottery.framework.util.NetUtils;
import cn.lottery.framework.widget.NetDialog;

public class BaseOpenimActivity extends Activity implements
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
	 * 消息提示
	 * 
	 * @param msg
	 */
	public void toast(String msg) {
		Toast.makeText(BaseOpenimActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
}
