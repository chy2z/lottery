package cn.lottery.app.activity.trace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import cn.lottery.R;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.openim.OpenIMLoginHelper;

/**
 * 踪迹聊天界面
 *
 */
public class TraceChatActivity extends BaseActivity {

    private static final String TAG = TraceChatActivity.class.getSimpleName();

    private Fragment mCurrentFrontFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_km_trace_chart_frame_container);
        addFragment(OpenIMLoginHelper.getInstance().getIMKit().getConversationFragment(), false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCurrentFrontFragment != null) {
            mCurrentFrontFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    //跳转相关

    public void addFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.wx_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        //getSupportFragmentManager().executePendingTransactions();
        mCurrentFrontFragment=fragment;
    }

}
