package cn.lottery.app.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.lottery.R;
import cn.lottery.app.activity.web.WebPageBackActivity;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

@SuppressLint("ValidFragment")
public class ImageLoadFragment  extends Fragment {

	private View mView;
	private ImageLoader imageload;
	private String imgurl;
	private String httpurl;
	private int index;
	private int[] imgs=new int[]{R.drawable.waiting};
	private boolean addEvent=false;
	private boolean clickClose=false;
	private ScaleType scaleType=ScaleType.FIT_XY;
	
	public ImageLoadFragment(ImageLoader _imageload,String _imgurl,String _httpurl,int i,boolean event,ScaleType st,boolean _clickClose)
	{
		imageload=_imageload;
		imgurl=_imgurl;
		httpurl=_httpurl;
		index=i;
		addEvent=event;
		scaleType=st;
		clickClose=_clickClose;
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
		
		final ImageView image = (ImageView) getView().findViewById(R.id.tm_Image);

		if(addEvent) {
			image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mView.getContext(), WebPageBackActivity.class);
					intent.putExtra("httpURL", httpurl);
					startActivity(intent);
				}
			});
		}

		if(clickClose){
			image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getActivity().finish();
				}
			});
		}
		
		imageload.displayImage(imgurl, image, new DisplayImageOptions.Builder()
		        .showImageOnLoading(imgs[0])
				.showImageForEmptyUri(R.drawable.bg_no_colcor)
				.showImageOnFail(R.drawable.bg_no_colcor)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)		
				.displayer(new SimpleBitmapDisplayer())				
				.build(),new SimpleImageLoadingListener(){  
			  
            @Override  
            public void onLoadingComplete(String imageUri, View view,  
                    Bitmap loadedImage) {  
                super.onLoadingComplete(imageUri, view, loadedImage);  
                image.setScaleType(scaleType);
            }  
              
        });
		
		
	}
}
