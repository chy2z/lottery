package cn.lottery.app.activity.openim.contact;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.mobileim.conversation.IYWMessageListener;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.gingko.presenter.contact.IContactProfileUpdateListener;
import com.alibaba.mobileim.lib.model.message.YWSystemMessage;
import com.alibaba.mobileim.utility.IMNotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.lottery.R;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseOpenimActivity;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.openim.common.Constant;

/**
 * 自定义联系人系统消息界面
 */
public class OpenimContactSystemMessageActivity extends BaseOpenimActivity {

    private OpenimContactProfileFragment.ContactSystemMessageAdapter mAdapter;
    private ListView mListView;
    private List<YWMessage> mList = new ArrayList<YWMessage>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private YWIMKit imKit;
    private YWConversation mConversation;
    private IContactProfileUpdateListener iContactProfileUpdateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openim_contact_activity_system_message);
        init();
    }

    private void init(){
        initTitle();
        imKit = OpenIMLoginHelper.getInstance().getIMKit();
        mListView = (ListView) findViewById(R.id.message_list);
        mConversation = imKit.getConversationService().getCustomConversationByConversationId(Constant.SYSTEM_FRIEND_REQ_CONVERSATION);
        mList = mConversation.getMessageLoader().loadMessage(20, null);
        mAdapter = new OpenimContactProfileFragment.ContactSystemMessageAdapter(imKit.getUserContext(),this, mList);
        mListView.setAdapter(mAdapter);
        imKit.getConversationService().markReaded(mConversation);

        //添加新消息到达监听,监听到有新消息到达的时候或者消息类别有变更的时候应该更新adapter
        mConversation.getMessageLoader().addMessageListener(mMessageListener);

        iContactProfileUpdateListener = new IContactProfileUpdateListener() {
            @Override
            public void onProfileUpdate() {

            }

            @Override
            public void onProfileUpdate(String userid, String appkey) {
                refreshAdapter();
            }
        };

        OpenIMLoginHelper.getInstance().getIMKit().getContactService().addProfileUpdateListener(iContactProfileUpdateListener);

    }


    IYWMessageListener mMessageListener = new IYWMessageListener() {
        @Override
        public void onItemUpdated() {  //消息列表变更，例如删除一条消息，修改消息状态，加载更多消息等等
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChangedWithAsyncLoad();
                }
            });
        }

        @Override
        public void onItemComing() { //收到新消息
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChangedWithAsyncLoad();
                    if(imKit!=null&&imKit.getConversationService()!=null)
                    imKit.getConversationService().markReaded(mConversation);

                }
            });
        }

        @Override
        public void onInputStatus(byte status) {

        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mConversation.getMessageLoader().removeMessageListener(mMessageListener);

    }



    private void initTitle() {
        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.title_bar);
        titleBar.setBackgroundColor(getResources().getColor(R.color.title_bg));
        titleBar.setVisibility(View.VISIBLE);

        TextView titleView = (TextView) findViewById(R.id.title_self_title);
        TextView leftButton = (TextView) findViewById(R.id.left_button);
        leftButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.openim_common_back_btn_black, 0, 0, 0);
        leftButton.setTextColor(Color.BLACK);
        leftButton.setText("返回");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleView.setTextColor(Color.BLACK);
        titleView.setText("联系人通知");


        TextView rightButton = (TextView) findViewById(R.id.right_button);
        rightButton.setText("清空");
        rightButton.setTextColor(Color.BLACK);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConversation.getMessageLoader().deleteAllMessage();
            }
        });
    }

    private void refreshAdapter(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.refreshData(mList);
            }
        });
    }
    private IYWContactService getContactService(){
        return OpenIMLoginHelper.getInstance().getIMKit().getContactService();
    }

    /**
     * 接收
     * @param message
     */
    public void acceptToBecomeFriend(final YWMessage message) {
        final YWSystemMessage msg = (YWSystemMessage) message;
            if (getContactService() != null) {
                getContactService().ackAddContact(message.getAuthorUserId(),message.getAuthorAppkey(),true,"",new IWxCallback() {
                    @Override
                    public void onSuccess(Object... result) {
                            msg.setSubType(YWSystemMessage.SYSMSG_TYPE_AGREE);
                            refreshAdapter();
                            getContactService().updateContactSystemMessage(msg);
                            operateContactCallBack(Config.IMUserId,message.getAuthorUserId(),"1");
                    }

                    @Override
                    public void onError(int code, String info) {

                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
            }
    }

    /**
     * 拒绝
     * @param message
     */
    public void refuseToBecomeFriend(final YWMessage message) {
        final YWSystemMessage msg = (YWSystemMessage) message;
        if (getContactService() != null) {
            getContactService().ackAddContact(message.getAuthorUserId(),message.getAuthorAppkey(),false,"",new IWxCallback() {
                @Override
                public void onSuccess(Object... result) {
                    msg.setSubType(YWSystemMessage.SYSMSG_TYPE_IGNORE);
                    refreshAdapter();
                    getContactService().updateContactSystemMessage(msg);
                    operateContactCallBack(Config.IMUserId,message.getAuthorUserId(),"2");
                }

                @Override
                public void onError(int code, String info) {

                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }

    }


    //=============================好友请求回调======================================

    /**
     * 好友请求回调
     * @param one
     * @param two
     * @param type
     */
    private void  operateContactCallBack(String one ,String two,String type){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accountIdOne", one);
        params.put("accountIdTwo", two);
        params.put("type", type);
        post(Config.URL_PREFIX + RequestUrl.addFriend, params, null,"operateContactCallBack");
    }


    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("operateContactCallBack")) {
            processOperateContactCallBack(obj);
        }
    }


    /**
     * 请求出错
     */
    @Override
    protected void handError(Object tag, String errorInfo) {

    }

    /**
     * 好友请求回调业务处理
     * @param json
     */
    private  void processOperateContactCallBack(JSONObject json){
        try{
            JSONObject msg= json.getJSONObject("msg");
            IMNotificationUtils.getInstance().showToast(OpenimContactSystemMessageActivity.this, msg.getString("info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}