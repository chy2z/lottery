package cn.lottery.app.activity.trace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import cn.lottery.R;
import cn.lottery.framework.activity.BaseActivity;

/**
 * 动态信息
 * Created by admin on 2017/6/27.
 */
public class TraceDynamicActivity extends BaseActivity implements View.OnClickListener  {

    private static final String TAG = "TraceDynamicActivity";

    private LinearLayout layoutBack;

    private Button butMyDy,butNearDy,butFriendDy,butAttentionDy;

    private TextView  writeDynamic;

    private Fragment mCurrentFrontFragment;

    private int currentIndex = 0;

    private ArrayList<Fragment> fragmentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_km_trace_dynamic);
        initView();
        initFragment();
    }


    private void initView(){

        butMyDy = (Button) findViewById(R.id.butMyDy);
        butMyDy.setOnClickListener(this);
        butMyDy.setSelected(false);
        butMyDy.setTag(0);

        butNearDy = (Button) findViewById(R.id.butNearDy);
        butNearDy.setOnClickListener(this);
        butNearDy.setSelected(false);
        butNearDy.setTag(1);

        butFriendDy = (Button) findViewById(R.id.butFriendDy);
        butFriendDy.setOnClickListener(this);
        butFriendDy.setSelected(false);
        butFriendDy.setTag(2);

        butAttentionDy = (Button) findViewById(R.id.butAttentionDy);
        butAttentionDy.setOnClickListener(this);
        butAttentionDy.setSelected(false);
        butAttentionDy.setTag(3);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        writeDynamic = (TextView) findViewById(R.id.writeDynamic);
        writeDynamic.setOnClickListener(this);
    }

    private  void initFragment(){
        fragmentArrayList = new ArrayList<Fragment>(4);
        fragmentArrayList.add(new TraceMyDynamicFragment());
        fragmentArrayList.add(new TraceNearDynamicFragment());
        fragmentArrayList.add(new TraceFriendDynamicFragment());
        fragmentArrayList.add(new TraceWatchDynamicFragment());
        butMyDy.setSelected(true);
        changeTab(0);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCurrentFrontFragment != null) {
            mCurrentFrontFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.butMyDy:
                butMyDy.setBackgroundResource(R.drawable.container_corners_orange_btn_left);
                butMyDy.setTextColor(getResources().getColor(R.color.txt_white));
                butNearDy.setBackgroundResource(R.drawable.container_corners_white_btn_center);
                butNearDy.setTextColor(getResources().getColor(R.color.title_bg));
                butFriendDy.setBackgroundResource(R.drawable.container_corners_white_btn_center);
                butFriendDy.setTextColor(getResources().getColor(R.color.title_bg));
                butAttentionDy.setBackgroundResource(R.drawable.container_corners_white_btn_right);
                butAttentionDy.setTextColor(getResources().getColor(R.color.title_bg));
                changeTab(0);
                break;
            case R.id.butNearDy:
                butMyDy.setBackgroundResource(R.drawable.container_corners_white_btn_left);
                butMyDy.setTextColor(getResources().getColor(R.color.title_bg));

                butNearDy.setBackgroundResource(R.drawable.container_corners_orange_btn_center);
                butNearDy.setTextColor(getResources().getColor(R.color.txt_white));

                butFriendDy.setBackgroundResource(R.drawable.container_corners_white_btn_center);
                butFriendDy.setTextColor(getResources().getColor(R.color.title_bg));

                butAttentionDy.setBackgroundResource(R.drawable.container_corners_white_btn_right);
                butAttentionDy.setTextColor(getResources().getColor(R.color.title_bg));
                changeTab(1);
                break;
            case R.id.butFriendDy:
                butMyDy.setBackgroundResource(R.drawable.container_corners_white_btn_left);
                butMyDy.setTextColor(getResources().getColor(R.color.title_bg));

                butNearDy.setBackgroundResource(R.drawable.container_corners_white_btn_center);
                butNearDy.setTextColor(getResources().getColor(R.color.title_bg));

                butFriendDy.setBackgroundResource(R.drawable.container_corners_orange_btn_center);
                butFriendDy.setTextColor(getResources().getColor(R.color.txt_white));


                butAttentionDy.setBackgroundResource(R.drawable.container_corners_white_btn_right);
                butAttentionDy.setTextColor(getResources().getColor(R.color.title_bg));
                changeTab(2);
                break;
            case R.id.butAttentionDy:
                butMyDy.setBackgroundResource(R.drawable.container_corners_white_btn_left);
                butMyDy.setTextColor(getResources().getColor(R.color.title_bg));
                butFriendDy.setBackgroundResource(R.drawable.container_corners_white_btn_center);
                butFriendDy.setTextColor(getResources().getColor(R.color.title_bg));
                butNearDy.setBackgroundResource(R.drawable.container_corners_white_btn_center);
                butNearDy.setTextColor(getResources().getColor(R.color.title_bg));

                butAttentionDy.setBackgroundResource(R.drawable.container_corners_orange_btn_right);
                butAttentionDy.setTextColor(getResources().getColor(R.color.txt_white));
                changeTab(3);
                break;
            case R.id.writeDynamic:
                startActivityForResult(new Intent(this, TracePublishingDynamicsActivity.class),1);
                break;
        }
    }


    private void changeTab(int index) {

        currentIndex = index;

        butMyDy.setSelected(index == 0);

        butNearDy.setSelected(index == 1);

        butFriendDy.setSelected(index == 2);

        butAttentionDy.setSelected(index == 3);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        //判断当前的Fragment是否为空，不为空则隐藏
        if (null != mCurrentFrontFragment) {
            ft.hide(mCurrentFrontFragment);
        }

        //先根据Tag从FragmentTransaction事物获取之前添加的Fragment
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentArrayList.get(currentIndex).getClass().getName());

        if (null == fragment) {
            //如fragment为空，则之前未添加此Fragment。便从集合中取出
            fragment = fragmentArrayList.get(index);
        }

        mCurrentFrontFragment = fragment;

        //判断此Fragment是否已经添加到FragmentTransaction事物中
        if (!fragment.isAdded()) {
            ft.add(R.id.wx_container, fragment, fragment.getClass().getName());
        } else {
            ft.show(fragment);
        }

        ft.commit();
    }


}