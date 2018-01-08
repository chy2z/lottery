package cn.lottery.app.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.lottery.R;
@SuppressLint("ValidFragment")
public class LocalImageFragment extends Fragment {

	private View mView;
	private int resid;
	
	public LocalImageFragment(int _resid)
	{
		resid=_resid;
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView=inflater.inflate(R.layout.item_test_main_viewpage, container,false);
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		ImageView image = (ImageView) getView().findViewById(R.id.tm_Image);
		image.setBackgroundResource(resid);
		image.setScaleType(ScaleType.FIT_CENTER);
	}
	
}
