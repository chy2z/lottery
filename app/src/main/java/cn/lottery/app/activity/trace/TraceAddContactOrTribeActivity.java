package cn.lottery.app.activity.trace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alibaba.mobileim.channel.cloud.contact.YWProfileInfo;

import java.util.ArrayList;

import cn.lottery.R;
import cn.lottery.app.activity.openim.contact.IFindContactParent;
import cn.lottery.framework.activity.BaseActivity;

/**
 * 踪迹查找增加联系人和群组
 * Created by admin on 2017/6/27.
 */
public class TraceAddContactOrTribeActivity extends BaseActivity implements View.OnClickListener , IFindContactParent {

    private static final String TAG = "TraceAddFriendTribeActivity";

    private LinearLayout layoutBack;

    private Button butFriend,ButTribe;

    private Fragment mCurrentFrontFragment;

    private int currentIndex = 0;

    private ArrayList<Fragment> fragmentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_km_trace_add_friend_and_tribe);
        //Intent it = getIntent();
        //如果类型不对，取到的值为null
        //type = it.getStringExtra("type");
        initView();
        initFragment();
    }

    private void initView(){
        butFriend = (Button) findViewById(R.id.butFriend);
        butFriend.setOnClickListener(this);
        butFriend.setSelected(false);
        butFriend.setTag(0);

        ButTribe = (Button) findViewById(R.id.ButTribe);
        ButTribe.setOnClickListener(this);
        ButTribe.setSelected(false);
        ButTribe.setTag(1);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);
    }

    private  void initFragment(){
        fragmentArrayList = new ArrayList<Fragment>(3);
        fragmentArrayList.add(new TraceFindContactFragment());
        fragmentArrayList.add(new TraceFindTribeFragment());
        butFriend.setSelected(true);
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
            case R.id.butFriend:
                butFriend.setBackgroundResource(R.drawable.container_corners_orange_btn_left);
                butFriend.setTextColor(getResources().getColor(R.color.txt_white));
                ButTribe.setBackgroundResource(R.drawable.container_corners_white_btn_right);
                ButTribe.setTextColor(getResources().getColor(R.color.title_bg));
                //addFragment(new TraceFindContactFragment(), false);
                changeTab(0);
                break;
            case R.id.ButTribe:
                ButTribe.setBackgroundResource(R.drawable.container_corners_orange_btn_right);
                ButTribe.setTextColor(getResources().getColor(R.color.txt_white));
                butFriend.setBackgroundResource(R.drawable.container_corners_white_btn_left);
                butFriend.setTextColor(getResources().getColor(R.color.title_bg));
                //addFragment(new TraceFindTribeFragment(), false);
                changeTab(1);
                break;
        }
    }


    private void changeTab(int index) {

        currentIndex = index;

        butFriend.setSelected(index == 0);

        ButTribe.setSelected(index == 1);


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

    //动态增加Fragment
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.wx_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        mCurrentFrontFragment=fragment;
    }

    //===========================增加联系人======================================

    public void finish(boolean POP_BACK_STACK_INCLUSIVE) {
        if(POP_BACK_STACK_INCLUSIVE){
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }else{
            getSupportFragmentManager().popBackStack();
        }
    }

    //传递数据用，由father来持有
    private YWProfileInfo ywProfileInfo;

    //传递数据用，由father来持有
    private boolean hasContactAlready;

    public YWProfileInfo getYWProfileInfo() {
        return ywProfileInfo;
    }

    public void setYWProfileInfo(YWProfileInfo ywProfileInfo) {
        this.ywProfileInfo = ywProfileInfo;
    }

    public boolean isHasContactAlready() {
        return hasContactAlready;
    }

    public void setHasContactAlready(boolean hasContactAlready) {
        this.hasContactAlready = hasContactAlready;
    }

    //===========================增加联系人======================================
}