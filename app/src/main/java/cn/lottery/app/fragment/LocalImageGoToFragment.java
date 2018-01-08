package cn.lottery.app.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import cn.lottery.R;
import cn.lottery.app.activity.HomePageActivity;
import cn.lottery.framework.Config;

@SuppressLint("ValidFragment")
public class LocalImageGoToFragment extends Fragment {

	private View mView;
	private int resid;

	public LocalImageGoToFragment(int _resid)
	{
		resid=_resid;
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.item_test_main_viewpage_goto, container, false);
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final ImageView image = (ImageView) getView().findViewById(R.id.tm_Image);

		final Button butApp = (Button) getView().findViewById(R.id.butApp);

		butApp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mView.getContext(), HomePageActivity.class);
				intent.putExtra("from", "loading");
				startActivity(intent);
				Config.spActity.finish();
				//Toast.makeText(getContext(), "点击我", Toast.LENGTH_SHORT).show();
			}
		});

		image.setBackgroundResource(resid);

		image.setScaleType(ScaleType.FIT_CENTER);
	}
	
}
