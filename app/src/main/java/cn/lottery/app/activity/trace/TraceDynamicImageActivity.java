package cn.lottery.app.activity.trace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import java.util.ArrayList;

import cn.lottery.R;
import cn.lottery.app.adapter.ViewPageFragmentAdapter;
import cn.lottery.app.fragment.ImageLoadFragment;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.widget.viewpagerindicator.CirclePageIndicator;

/**
 * 动态图片显示
 * Created by admin on 2017/7/15.
 */
public class TraceDynamicImageActivity extends BaseActivity {

    ViewPager mViewPager;

    ViewPageFragmentAdapter vpmAdapter;

    CirclePageIndicator indicator;

    String[] url = null;

    String imgIndex = "";

    ArrayList<Fragment> fragmentsaArrayList;

    String[] CONTENT = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_trace_dynamic_image);

        Intent it = getIntent();

        url = it.getStringExtra("url").split(",");

        imgIndex = it.getStringExtra("imgIndex");

        initView();

        initData();
    }

    private void initView() {
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
    }

    private void initData() {
        if (url.length > 0) {

            CONTENT = new String[url.length];

            fragmentsaArrayList = new ArrayList<Fragment>();

            for (int i = 0; i < url.length; i++) {
                CONTENT[i] = url[i];
                fragmentsaArrayList.add(new ImageLoadFragment(app.getImageLoader(), url[i], "", i, false, ImageView.ScaleType.FIT_CENTER,true));
            }

            vpmAdapter = new ViewPageFragmentAdapter(getSupportFragmentManager(),
                    fragmentsaArrayList, CONTENT);

            mViewPager.setAdapter(vpmAdapter);

            indicator.setViewPager(mViewPager);

            if (imgIndex != null) {
                mViewPager.setCurrentItem(Integer.parseInt(imgIndex));
                indicator.setCurrentItem(Integer.parseInt(imgIndex));
            }

        }
    }
}
