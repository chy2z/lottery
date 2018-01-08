package cn.lottery.app.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ScrollPageFragmentAdapter extends FragmentPagerAdapter {
	
	private ArrayList<Fragment> fragmentsaArrayList=null;
	
	private String[] CONTENT=null;
	
	public ScrollPageFragmentAdapter(FragmentManager fm, ArrayList<Fragment> _fragmentsaArrayList, String[] _CONTENT) {
		super(fm);
		fragmentsaArrayList=_fragmentsaArrayList;
		CONTENT=_CONTENT;
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentsaArrayList.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return CONTENT[position % CONTENT.length].toUpperCase();
	}

	@Override
	public int getCount() {
		return CONTENT.length;
	}
}
