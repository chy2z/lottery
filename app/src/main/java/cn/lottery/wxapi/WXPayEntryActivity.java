package cn.lottery.wxapi;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.lottery.framework.Config;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.util.wxpay.Constants;
import cn.lottery.framework.util.SPUtils;
import cn.lottery.framework.util.T;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
	private static final String TAG = WXPayEntryActivity.class.getName();
	private static String URL_PERSONAL_CHARGE = Config.URL_PREFIX + "/Account/personalCharge.htm";
	private static String URL_FAILURE_PAY = Config.URL_PREFIX+ "/Account/changeAlipayRecordCode.htm";
	private static String URL_CHARGE_PAY = Config.URL_PREFIX + "/stake/payStakeChargeOrder.htm";
	private static String URL_PAY = Config.URL_PREFIX + "/Account/payCharge.htm";
	private IWXAPI api;
	private String mToDateStr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.handleIntent(getIntent(), this);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		mToDateStr = sf.format(c.getTime());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	/**
	 * 保证金充值失败
	 */
	public void personalFailureCharge(String code, String codeDesc) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("customerId", (String) SPUtils.get(this, "userId", ""));
		param.put("outTradeNo", (String) SPUtils.get(this, "out_trade_no", ""));
		param.put("code", code);
		param.put("codeDesc", codeDesc);
		param.put("chargeType", "2");
		//post(URL_FAILURE_PAY, param, (String) SPUtils.get(this, "userToken", ""),"personalFailureCharge");
	}

	private void requestUrl() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("customerId", (String) SPUtils.get(this, "userId", ""));
		map.put("orderId", SPUtils.get(this, "orderId", "").toString());
		//post(Config.URL_IS_OPEN, map, null, "rentShare");
	}

	@Override
	protected void handSuccess(JSONObject obj, Object tag) {
		super.handSuccess(obj, tag);
		/*
		if (tag.equals("rentShare")) {
			try {
				Intent it = new Intent(WXPayEntryActivity.this, ShareDialogActivity.class);
				it.putExtra("content", obj.getString("content"));
				it.putExtra("imgUrl", obj.getString("imgUrl"));
				it.putExtra("title", obj.getString("title"));
				it.putExtra("urlSina", obj.getString("urlSina"));
				it.putExtra("urlWX", obj.getString("urlWX"));
				it.putExtra("AnimationTag", "1");
				startActivity(it);
				finish();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			finish();
		}
        */
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (resp.errStr != null) {
			Log.d(TAG, resp.errStr);
		}
		if (resp.openId != null) {
			Log.d(TAG, resp.openId);
		}
		if (resp.transaction != null) {
			Log.d(TAG, resp.transaction);
		}
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

			if (String.valueOf(resp.errCode).equals("0")) {
				T.showShort(WXPayEntryActivity.this, "支付成功");
				if (SPUtils.get(WXPayEntryActivity.this, "chooseType", "").equals("yikaibi")) {
					givieEakayCoin();
					SPUtils.put(WXPayEntryActivity.this, "isWXPayReturn", "true");
				}
				if (SPUtils.get(WXPayEntryActivity.this, "chooseType", "").equals("baozhengjin")) {
					BigDecimal totalDecimal = new BigDecimal(SPUtils.get(WXPayEntryActivity.this,
							"total", "0").toString());
					if (totalDecimal.compareTo(new BigDecimal("1500")) == 0) {
						SPUtils.put(WXPayEntryActivity.this, "EakayCoinGivie", 50 + "");
					}
					SPUtils.put(WXPayEntryActivity.this, "isWXPayReturn", "true");
				}
				if (SPUtils.get(WXPayEntryActivity.this, "chooseType", "").equals("dingdan")) {
					requestUrl();
				} else {
					finish();
				}
			} else if (String.valueOf(resp.errCode).equals("-1")) {
				T.showShort(WXPayEntryActivity.this, "支付失败");
				personalFailureCharge(4000 + "", "订单支付失败");
			} else if (String.valueOf(resp.errCode).equals("-2")) {
				T.showShort(WXPayEntryActivity.this, "支付中途取消");
				personalFailureCharge(6001 + "", "用户中途取消");
			}
		}
	}

	public void givieEakayCoin() {
		// 比较的钱
		BigDecimal oneBigDecimal = new BigDecimal("500");
		BigDecimal twoBigDecimal = new BigDecimal("1000");
		BigDecimal threeBigDecimal = new BigDecimal("2000");

		// 送的钱
		BigDecimal firstBigDecimal = new BigDecimal("50");
		BigDecimal secondBigDecimal = new BigDecimal("100");
		BigDecimal thirdBigDecimal = new BigDecimal("200");

		String gift = 0 + "";
		BigDecimal totalDecimal = new BigDecimal(SPUtils.get(WXPayEntryActivity.this, "total", "0")
				.toString());
		// BigDecimal totalDecimal = new BigDecimal("500");
		if (totalDecimal.compareTo(oneBigDecimal) >= 0 && totalDecimal.compareTo(twoBigDecimal) < 0) {
			gift = 50 + "";
		}

		if (totalDecimal.compareTo(twoBigDecimal) >= 0
				&& totalDecimal.compareTo(threeBigDecimal) < 0) {
			gift = 100 + "";
		}

		if (totalDecimal.compareTo(twoBigDecimal) >= 0
				&& totalDecimal.compareTo(threeBigDecimal) < 0) {
			gift = 200 + "";
		}
		SPUtils.put(WXPayEntryActivity.this, "EakayCoinGivie", gift + "");
	}
}