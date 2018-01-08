package cn.lottery.app.activity.upgrade;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.lottery.R;
import cn.lottery.app.activity.LoadingActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.service.AppUpdateService;
import cn.lottery.framework.util.L;

public class UpdateDialogActivity extends BaseActivity implements OnClickListener {
	private Button mUpdate;
	private TextView mUpdateVersion;
	private LinearLayout mUpdateContent;
	private Button mCancle;
	private TextView tv;

	private String isFocused;
	private String downAddress;
	private String version;
	private String content;		
	private Intent it;
	public static UpdateDialogActivity currentActivity;
	RelativeLayout imgclose;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_dialog_box);
		currentActivity = this;
		Intent it = getIntent();
		//如果类型不对，取到的值为null
		isFocused = it.getStringExtra("isFocused");
		downAddress = it.getStringExtra("downAddress");
		version = it.getStringExtra("version");
		content = it.getStringExtra("content");	
		initView();
		initListener();
	}

	private void initView() {
		mUpdate = (Button) findViewById(R.id.update_sure);
		
		mUpdateVersion = (TextView) findViewById(R.id.dialog_version);
		
		mUpdateContent = (LinearLayout) findViewById(R.id.dialog_content);		
		
		mCancle = (Button) findViewById(R.id.update_cancle);
		
		imgclose = (RelativeLayout) findViewById(R.id.layoutClose);	
		
		
		mUpdateVersion.setText("咖忙" + version + "版本更新");
		
		if (isFocused.equals("1")) {
			mCancle.setText("退出应用");
		} else {
			mCancle.setText("下次再说");
		}		

	
		String str1[] = content.split("[;]");
		for (int i = 0; i < str1.length; i++) {
			L.d("str1", str1[i]);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( 
					 LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); 
			tv = (TextView) LayoutInflater.from(currentActivity).inflate(
					R.layout.item_dialog_content, null);
			tv.setText(str1[i]);
			tv.setLayoutParams(lp);
			mUpdateContent.addView(tv);
		}		
	}

	private void initListener() {
		mUpdate.setOnClickListener(this);
		mCancle.setOnClickListener(this);
		imgclose.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_sure:
			downApp(downAddress);
			finish();
			LoadingActivity.currentActivity.finish();
			break;
		case R.id.update_cancle:
			if (isFocused.equals("1")) { //强制更新
				finish();
				LoadingActivity.currentActivity.finish();
			} else {
				isGoToGuidePage();
				LoadingActivity.currentActivity.finish();
			}
			break;
		case R.id.layoutClose:
			if (isFocused.equals("1")) { //强制更新
				finish();
				LoadingActivity.currentActivity.finish();
			} else {
				isGoToGuidePage();
			}
			break;
		default:
			break;
		}
	}

	private final void downApp(String downUrl) {
		Intent updateIntent = new Intent(this, AppUpdateService.class);
		updateIntent.putExtra("titleId", "kamang");
		updateIntent.putExtra("appname", Config.appName);
		updateIntent.putExtra("downurl", downUrl);
		startService(updateIntent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
