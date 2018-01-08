package cn.lottery.app.activity.openim.contact;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.ui.contact.ContactsFragment;
import com.alibaba.mobileim.ui.contact.adapter.ContactsAdapter;
import java.util.List;

import cn.lottery.R;
import cn.lottery.framework.openim.ChattingOperationCustomSample;
import cn.lottery.framework.openim.OpenIMLoginHelper;

/**
 * 选择联系人发送名片
 */
public class OpenimSelectContactToSendCardActivity extends FragmentActivity {

    private static final String TAG = "OpenimSelectContactToSendCardActivity";

    private YWIMKit mIMKit;
    private ContactsFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openim_select_contact);
        mIMKit = OpenIMLoginHelper.getInstance().getIMKit();

        initTitle();
        createFragment();
        YWLog.i(TAG, "onCreate");
    }

    private void initTitle(){
        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.title_bar);
        titleBar.setBackgroundColor(getResources().getColor(R.color.title_bg));
        titleBar.setVisibility(View.VISIBLE);

        TextView titleView = (TextView) findViewById(R.id.title_self_title);
        TextView leftButton = (TextView) findViewById(R.id.left_button);
        leftButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.openim_common_back_btn_white, 0, 0, 0);
        leftButton.setTextColor(Color.WHITE);
        leftButton.setText("取消");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleView.setTextColor(Color.WHITE);
        titleView.setText("选择联系人");


        TextView rightButton = (TextView) findViewById(R.id.right_button);
        rightButton.setText("完成");
        rightButton.setTextColor(Color.WHITE);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsAdapter adapter = mFragment.getContactsAdapter();
                List<IYWContact> list = adapter.getSelectedList();
                if (list != null && list.size() > 0){
                    ChattingOperationCustomSample.selectContactListener.onSelectCompleted(list);
                    finish();
                }
            }
        });
    }

    private void createFragment(){
        mFragment =mIMKit.getContactsFragment();
        Bundle bundle = mFragment.getArguments();
        bundle.putString(ContactsFragment.SEND_CARD, ContactsFragment.SEND_CARD);
        mFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.contact_list_container, mFragment).commit();
    }
}
