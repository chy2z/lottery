package cn.lottery.app.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import cn.lottery.R;


/**
 * 引导页
 */
@SuppressLint("ValidFragment")
public class FirstImageFragment_1  extends Fragment {

	private View mView;
	private ImageLoader imageload;
	private String imgurl;
	private int index;
    private int[] imgs=new int[]{R.drawable.loading,R.drawable.loading,R.drawable.loading};

	public FirstImageFragment_1(){

	}

	public FirstImageFragment_1(ImageLoader _imageload,String _imgurl,String i)
	{
		imageload=_imageload;
		imgurl=_imgurl;
		index=Integer.parseInt(i);
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
		
			
		imageload.displayImage(imgurl, image, new DisplayImageOptions.Builder()
		        .showImageOnLoading(imgs[index])
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
                image.setScaleType(ScaleType.FIT_XY);	 
            }  
              
        });
		
		
	}
}
