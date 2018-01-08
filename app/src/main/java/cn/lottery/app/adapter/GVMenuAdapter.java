package cn.lottery.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;

import cn.lottery.R;

public class GVMenuAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<HashMap<String, Object>> data;
	private ImageLoader imageload;

	public GVMenuAdapter(Context context, ArrayList<HashMap<String, Object>> data, ImageLoader _imageload) {
		super();
		this.inflater = LayoutInflater.from(context);
		this.data = data;
		this.imageload=_imageload;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_menu_gridview, null);
			holder.mImage=(ImageView) convertView.findViewById(R.id.image);
			holder.mText=(TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (data.size()>0) {		
			holder.mText.setText(data.get(position).get("Name").toString());				
			imageload.displayImage(data.get(position).get("imgURL").toString(),holder.mImage, new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.bg_no_colcor)
			.showImageOnFail(R.drawable.bg_no_colcor)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)		
			.displayer(new SimpleBitmapDisplayer())
			.build());		
		}
		return convertView;
	}

	class ViewHolder {
		ImageView mImage;
		TextView mText;
	}
}
