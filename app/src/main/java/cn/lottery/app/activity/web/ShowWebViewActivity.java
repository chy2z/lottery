package cn.lottery.app.activity.web;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;
import cn.lottery.R;
import cn.lottery.app.activity.LoadingActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.activity.BaseActivity;

public class ShowWebViewActivity extends BaseActivity implements
		OnClickListener {

	WebView webview;
	ImageView leftBut,rightBut;
	String httpURL,notify,notificationID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_webview);		
		initview();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(webview.canGoBack())
			{						
				webview.goBack();  
				return true;
			}
			else
			{
				end();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void initview() {
		Intent intent = getIntent();
		httpURL = intent.getStringExtra("httpURL");		
		notify = intent.getStringExtra("notify");	
		notificationID = intent.getStringExtra("notificationID");	
		
		
		leftBut = (ImageView) findViewById(R.id.imageViewleft);
		leftBut.setOnClickListener(this);
		leftBut.setImageResource(R.drawable.slidingmenu);
		
		rightBut = (ImageView) findViewById(R.id.imageViewright);		
		rightBut.setVisibility(View.INVISIBLE);

		webview = (WebView) findViewById(R.id.webViewPage);
		webview.requestFocus();

		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webview.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false; //android WebView 加载重定向页面无法后退解决方案
			}
		
			@Override
			public void onPageFinished(WebView view, String url){				
							
			}				
			
		});	

		webview.setWebChromeClient(new MyWebChromeClient());

		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webSettings.setDefaultTextEncodingName("utf-8");		

		webview.loadUrl(httpURL);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageViewleft:				
			end();			
			break;
		default:
			break;
		}
	}

	private void end() {
		
		finish();
		
		/**
		 * android:launchMode="singleTask"
		 */
		if (Config.isActive <= 0) {
			Intent localIntent = new Intent(getApplicationContext(),LoadingActivity.class);
			startActivity(localIntent);
		}
	}

	/**
	 * Provides a hook for calling "alert" from javascript. Useful for debugging
	 * your javascript.
	 */
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			result.confirm();
			Toast.makeText(ShowWebViewActivity.this, message,
					Toast.LENGTH_SHORT).show();
			return true;
		}
	}

   

	//---------------------------------网络请求

	/**
	 * 请求成功返回
	 */
	@Override
	protected void handSuccess(JSONObject obj, Object tag) {

	}
	
	
    /**
     * 请求出错
     */
	@Override
	protected void handError(Object tag, String errorInfo) {

	}

}
