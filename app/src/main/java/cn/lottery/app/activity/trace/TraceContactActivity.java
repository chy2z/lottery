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
import cn.lottery.app.activity.login.LoginActivity;
import cn.lottery.app.activity.openim.OpenimTribeFragment;
import cn.lottery.framework.Config;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.openim.OpenIMLoginHelper;

/**
 * 踪迹好友列表
 */
public class TraceContactActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = TraceContactActivity.class.getSimpleName();
    private int currentIndex = 0;
    private Fragment mCurrentFrontFragment;
    private ArrayList<Fragment> fragmentArrayList;
    private Button butFriend,ButTribe;
    private TextView addContact;
    private LinearLayout layoutBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_km_trace_contact_frame_container);
        initView();
        initFragment();
    }

    private  void initView(){
        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        butFriend = (Button) findViewById(R.id.butFriend);
        butFriend.setOnClickListener(this);
        butFriend.setSelected(false);
        butFriend.setTag(0);

        ButTribe = (Button) findViewById(R.id.ButTribe);
        ButTribe.setOnClickListener(this);
        ButTribe.setSelected(false);
        ButTribe.setTag(1);

        addContact= (TextView) findViewById(R.id.addContact);
        addContact.setOnClickListener(this);
    }

    private  void initFragment(){
        fragmentArrayList = new ArrayList<Fragment>(3);
        fragmentArrayList.add(OpenIMLoginHelper.getInstance().getIMKit().getContactsFragment());
        fragmentArrayList.add(new OpenimTribeFragment());
        butFriend.setSelected(true);
        changeTab(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.layoutBack:
                finish();
                break;
            case R.id.butFriend:
                butFriend.setBackgroundResource(R.drawable.container_corners_orange_btn_left);
                butFriend.setTextColor(getResources().getColor(R.color.txt_white));
                ButTribe.setBackgroundResource(R.drawable.container_corners_white_btn_right);
                ButTribe.setTextColor(getResources().getColor(R.color.title_bg));
                changeTab((Integer) v.getTag());
                break;
            case R.id.ButTribe:
                ButTribe.setBackgroundResource(R.drawable.container_corners_orange_btn_right);
                ButTribe.setTextColor(getResources().getColor(R.color.txt_white));
                butFriend.setBackgroundResource(R.drawable.container_corners_white_btn_left);
                butFriend.setTextColor(getResources().getColor(R.color.title_bg));
                changeTab((Integer) v.getTag());
                break;
            case R.id.addContact:
                if(Config.userToken.equals("")){
                    startActivityForResult(new Intent(this, LoginActivity.class),1);
                }
                else {
                    Intent intent = new Intent().setClass(this, TraceAddContactOrTribeActivity.class);
                    intent.putExtra("type","Friend");
                    startActivity(intent);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCurrentFrontFragment != null) {
            mCurrentFrontFragment.onActivityResult(requestCode, resultCode, data);
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

}
