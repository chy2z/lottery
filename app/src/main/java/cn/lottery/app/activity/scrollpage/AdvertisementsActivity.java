package cn.lottery.app.activity.scrollpage;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.lottery.R;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.util.SPUtils;
import cn.lottery.app.activity.test.TestMainActivity;

/**
 * app广告页（第一次使用加载引导页，以后使用此页面替换引导页）
 * 
 */
public class AdvertisementsActivity extends BaseActivity implements
		OnClickListener {

	public static AdvertisementsActivity currentActivity;

	private Intent it;

	Button butApp;

	ImageView img;

	private int recLen = 8;

	private Boolean isShow = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_advertisements);

		currentActivity = this;

		initView();
			
		initData();
	}

	private void initView() {
		butApp = (Button) findViewById(R.id.butApp);
		butApp.setOnClickListener(this);
		img = (ImageView) findViewById(R.id.img);
		//尝试从缓存获取图片
		SApplication.getInstance().setImageFromCache(img,"appLaunchImage");
	}

	private void initData() {
		showLoadingDialog();
		requestLaunchimageInfo();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.butApp:
			isShow = true;
			it = new Intent(this, TestMainActivity.class);
			it.putExtra("from", "loading");
			startActivity(it);
			finish();
			break;
		}
	}

	/**
	 * 请求成功返回
	 */
	@Override
	protected void handSuccess(JSONObject obj, Object tag) {
		if (tag.equals("getLaunchimageInfo")) {
			closeDialog();
			processLaunchimageInfo(obj);
		}
	}

	/**
	 * 请求出错
	 */
	@Override
	protected void handError(Object tag, String errorInfo) {

	}

	private void requestLaunchimageInfo() {
		HashMap<String, String> params = new HashMap<String, String>();
		post(Config.URL_PREFIX + RequestUrl.getLaunchimageInfo, params, null,
				"getLaunchimageInfo");
	}

	private void processLaunchimageInfo(JSONObject json) {
		try {

			JSONObject appLaunchImage = json.getJSONObject("appLaunchImage");

			JSONObject lauchImage = appLaunchImage.getJSONObject("lauchImage");

			recLen = Integer.parseInt(appLaunchImage.getString("value"));
			
			SPUtils.put(getBaseContext(), "appLaunchImage", Config.IMG_HOST_URL + lauchImage.getString("imgURL"));
			
			app.getImageLoader().displayImage(
					Config.IMG_HOST_URL + lauchImage.getString("imgURL"),
					img,
					new DisplayImageOptions.Builder()					        
							.showImageForEmptyUri(R.drawable.bg_no_colcor)
							.showImageOnFail(R.drawable.bg_no_colcor)
							.cacheInMemory(true).cacheOnDisk(true)
							.considerExifParams(true)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new SimpleBitmapDisplayer()).build(),new SimpleImageLoadingListener(){  
						  
			            @Override  
			            public void onLoadingComplete(String imageUri, View view,  
			                    Bitmap loadedImage) {  
			                super.onLoadingComplete(imageUri, view, loadedImage);  
			                img.setScaleType(ScaleType.FIT_XY);   
			            }  
			              
			        });

			new Thread(new MyThread()).start();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	final Handler handler = new Handler() { // handle
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				butApp.setText("跳过(" + recLen + "秒)");
				recLen--;
				break;
			case 0:
				recLen = 0;
				if (!isShow) {
					it = new Intent(getApplicationContext(), TestMainActivity.class);
					it.putExtra("from", "loading");
					startActivity(it);
					finish();
				}
				break;
			}

			super.handleMessage(msg);
		}
	};

	public class MyThread implements Runnable { // thread
		@Override
		public void run() {
			while (true) {
				try {
					if (recLen > 0) {
						Thread.sleep(1000); // sleep 1000ms
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					} else {
						Message message = new Message();
						message.what = 0;
						handler.sendMessage(message);
						break;
					}
				} catch (Exception e) {
				}
			}
		}
	}

}
