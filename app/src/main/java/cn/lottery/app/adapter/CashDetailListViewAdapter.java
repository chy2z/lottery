package cn.lottery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

import cn.lottery.R;

/**
 *
 */
public class CashDetailListViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<HashMap<String, Object>> data;
	private Context mcontext;

    
	public CashDetailListViewAdapter(Context context, ArrayList<HashMap<String, Object>> data) {
		super();
		this.inflater = LayoutInflater.from(context);
		this.data = data;
		this.mcontext=context;
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

			convertView = inflater.inflate(R.layout.item_cashdetail_listview, null);

			holder.layoutGroup=(LinearLayout) convertView.findViewById(R.id.layoutGroup);

			holder.layoutBody=(LinearLayout) convertView.findViewById(R.id.layoutBody);

			holder. year=(TextView) convertView.findViewById(R.id. year);

			holder.addBalance=(TextView) convertView.findViewById(R.id.addBalance);

			holder.createTime=(TextView) convertView.findViewById(R.id.createTime);

			holder.type=(TextView) convertView.findViewById(R.id.type);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (data.size()>0) {

			if(data.get(position).get("group").toString().equals("0")) {
				holder.layoutGroup.setVisibility(View.VISIBLE);
			}

			holder.year.setText(data.get(position).get("year").toString());
			holder.type.setText(data.get(position).get("type").toString());
			holder.createTime.setText(data.get(position).get("createTime").toString());
			holder.addBalance.setText(data.get(position).get("addBalance").toString());
		}

		return convertView;
	}

	class ViewHolder {
		TextView type;
		TextView createTime;
		TextView addBalance;
		TextView  year;
		LinearLayout layoutGroup;
		LinearLayout layoutBody;
	}
}
