package cn.lottery.app.activity;

import android.os.Bundle;
import android.os.Handler;
import cn.lottery.R;
import cn.lottery.framework.activity.BaseActivity;

/**
 * app遮挡加载页
 * 
 */
public class MaskingLayerActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_masking_layer);						
		
	    new Handler().postDelayed(new Runnable() {
	      @Override
	      public void run() {
	    	  finish();
	      }
	    }, 3500);
		
	}		
   
}
