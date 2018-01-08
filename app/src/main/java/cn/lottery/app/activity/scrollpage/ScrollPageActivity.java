package cn.lottery.app.activity.scrollpage;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

import cn.lottery.R;
import cn.lottery.app.adapter.ScrollPageFragmentAdapter;
import cn.lottery.app.fragment.FirstImageFragment_1;
import cn.lottery.app.fragment.FirstImageFragment_2;
import cn.lottery.app.fragment.LocalImageFragment;
import cn.lottery.app.fragment.LocalImageGoToFragment;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.widget.viewpagerindicator.CirclePageIndicator;

/**
 * app引导页.
 * 
 */
public class ScrollPageActivity extends BaseActivity implements
		OnClickListener {

	private ViewPager mViewPager;
	private ArrayList<Fragment> fragmentsaArrayList = new ArrayList<Fragment>();
	private ScrollPageFragmentAdapter mAdapter;
	private String[] CONTENT = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_scroll_page);
		
		Config.spActity=this;
		
		initView();

		//请求本地引导图片地址
		loadLocalImages();

		//请求远程引导图片地址
		//requestLaunchimageInfo();
	}

	private void initView() {
	    mViewPager = (ViewPager) findViewById(R.id.main_viewpager);		
	}

	/**
	 * 加载本地引导图片
	 */
	private void loadLocalImages(){

		fragmentsaArrayList.add(new LocalImageFragment(R.drawable.guide1));
		fragmentsaArrayList.add(new LocalImageFragment(R.drawable.guide2));
		fragmentsaArrayList.add(new LocalImageFragment(R.drawable.guide3));
		fragmentsaArrayList.add(new LocalImageGoToFragment(R.drawable.guide4));

		CONTENT=new String[fragmentsaArrayList.size()];
		CONTENT[0]="1";
		CONTENT[1]="2";
		CONTENT[2]="3";
		CONTENT[3]="4";

		mAdapter = new ScrollPageFragmentAdapter(getSupportFragmentManager(),
				fragmentsaArrayList, CONTENT);

		mViewPager.setAdapter(mAdapter);

		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);

		indicator.setViewPager(mViewPager);

		indicator.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	
		}
	}

	/**
	 * 请求远程图片地址
	 */
	private void requestLaunchimageInfo() {
		HashMap<String, String> params = new HashMap<String, String>();
		post(Config.URL_PREFIX + RequestUrl.getFirstLoginImage, params, null,"getFirstLoginImage");
	}

	/**
	 * 请求成功返回
	 */
	@Override
	protected void handSuccess(JSONObject obj, Object tag) {
		if (tag.equals("getFirstLoginImage")) {
			closeDialog();
			processGetFirstLoginImage(obj);
		}
	}

	/**
	 * 请求出错
	 */
	@Override
	protected void handError(Object tag, String errorInfo) {

	}

	/**
	 * 加载图片
	 * @param json
     */
	private void processGetFirstLoginImage(JSONObject json) {
		try {
			
			JSONArray bannerList = json.getJSONArray("list");
			
			JSONObject jobj = null;

			CONTENT = new String[bannerList.length()];

			for (int i = 0; i < bannerList.length(); i++) {
				jobj = bannerList.getJSONObject(i);

				CONTENT[i] = jobj.getString("httpURL");

				
				if(i<2){
				fragmentsaArrayList.add(new FirstImageFragment_1(app
						.getImageLoader(), Config.IMG_HOST_URL
						+ jobj.getString("imgURL"),i+""));
				}
				else{
					fragmentsaArrayList.add(new FirstImageFragment_2(app
							.getImageLoader(), Config.IMG_HOST_URL
							+ jobj.getString("imgURL"),i+""));
				}					
			}

			mAdapter = new ScrollPageFragmentAdapter(getSupportFragmentManager(),
					fragmentsaArrayList, CONTENT);

			mViewPager.setAdapter(mAdapter);

			CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);

			indicator.setViewPager(mViewPager);
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
