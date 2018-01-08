package cn.lottery.app.activity.openim.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.alibaba.mobileim.channel.cloud.contact.YWProfileInfo;
import com.alibaba.mobileim.channel.util.YWLog;
import cn.lottery.R;

/**
 * 查找联系人容器
 */
public class OpenimFindContactActivity extends FragmentActivity implements IFindContactParent {

	private static final String TAG = OpenimFindContactActivity.class.getSimpleName();

	private Fragment mCurrentFrontFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openim_container_activity);
		addFragment(new OpenimFindContactFragment(), false);
		YWLog.i(TAG, "onCreate");
	}


	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (mCurrentFrontFragment != null) {
			mCurrentFrontFragment.onActivityResult(arg0, arg1, arg2);
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
        //		getSupportFragmentManager().executePendingTransactions();

		mCurrentFrontFragment=fragment;
	}

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

}
