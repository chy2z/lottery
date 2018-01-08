package cn.lottery.app.activity.openim.contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWAccount;
import com.alibaba.mobileim.channel.constant.YWProfileSettingsConstants;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactManager;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.fundamental.widget.WxAlertDialog;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import cn.lottery.R;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.widget.RoundedBitmapDisplayer.MyRoundedBitmapDisplayer;

/**
 * 单聊界面的聊天设置
 */
public class OpenimContactSettingActivity extends Activity {

    private ImageView contactHead;
    private TextView contactShowName;
    private ImageView msgRemindSwitch,msgBlackSwitch;
    private RelativeLayout clearMsgRecordLayout;
    private String appKey;
    private String userId;
    private int msgRecFlag = YWProfileSettingsConstants.RECEIVE_PEER_MSG;
    private YWAccount account;

    private YWContactManager contactManager;
    private IYWContact contact;

    private IYWConversationService conversationService;
    private YWConversation conversation;
    private Handler uiHandler;

    boolean isBlocked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null) {
            appKey = intent.getStringExtra("AppKey");
            userId = intent.getStringExtra("UserId");
        }
        uiHandler = new Handler(Looper.getMainLooper());
        account = OpenIMLoginHelper.getInstance().getIMKit().getIMCore();
        contactManager = (YWContactManager) OpenIMLoginHelper.getInstance().getIMKit().getContactService();
        contact = contactManager.getContactProfileInfo(userId, appKey);
        conversationService = OpenIMLoginHelper.getInstance().getIMKit().getConversationService();
        conversation = conversationService.getConversationByUserId(userId);
        if(contactManager != null) {
            if(!contactManager.isBlackListEnable()) {
                contactManager.enableBlackList();
            }
            msgRecFlag = contactManager.getMsgRecFlagForContact(userId, appKey);
            isBlocked= contactManager.isBlackContact(contact.getUserId(), contact.getAppKey());
        }
        setContentView(R.layout.openim_activity_contact_setting);
        initViews();
    }

    public static Intent getContactSettingActivityIntent(Context context, String appKey, String userId) {
        Intent intent = new Intent(context, OpenimContactSettingActivity.class);
        intent.putExtra("AppKey", appKey);
        intent.putExtra("UserId", userId);
        return intent;
    }

    private void initViews() {
        contactHead = (ImageView) findViewById(R.id.head);

        contactShowName = (TextView) findViewById(R.id.contact_show_name);

        if (contact != null) {
            contactShowName.setText(contact.getShowName());
        } else {
            contactShowName.setText(userId);
        }

        SApplication.getInstance().getImageLoader().displayImage(contact.getAvatarPath(), contactHead, new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.login_head)
                .showImageForEmptyUri(R.drawable.login_head)
                .showImageOnFail(R.drawable.login_head)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new MyRoundedBitmapDisplayer(0))
                .build());


        msgBlackSwitch = (ImageView) findViewById(R.id.receive_msg_black_switch);

        msgRemindSwitch = (ImageView) findViewById(R.id.receive_msg_remind_switch);

        clearMsgRecordLayout = (RelativeLayout) findViewById(R.id.clear_msg_record);


        if (msgRecFlag != YWProfileSettingsConstants.RECEIVE_PEER_MSG_NOT_REMIND) {
            msgRemindSwitch.setImageResource(R.drawable.on_switch);
        } else {
            msgRemindSwitch.setImageResource(R.drawable.off_switch);
        }

        if (isBlocked) {
            msgBlackSwitch.setImageResource(R.drawable.on_switch);
        } else {
            msgBlackSwitch.setImageResource(R.drawable.off_switch);
        }

        msgBlackSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBlackContact();
            }
        });

        msgRemindSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMsgRecType();
            }
        });

        clearMsgRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMsgRecord();
            }
        });

        initTitle();
    }

    private void initTitle() {
        View titleView = findViewById(R.id.title_bar);
        titleView.setVisibility(View.VISIBLE);
        titleView.setBackgroundColor(getResources().getColor(R.color.title_bg));

        TextView leftButton = (TextView) findViewById(R.id.left_button);
        leftButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.openim_common_back_btn_black, 0, 0, 0);
        leftButton.setText("返回");
        leftButton.setTextColor(Color.BLACK);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView) findViewById(R.id.title_self_title);
        title.setText("聊天设置");
        title.setTextColor(Color.BLACK);
    }

    private void setBlackContact(){
       if(isBlocked) {  //移除黑名单
           contactManager.removeBlackContact(contact.getUserId(), contact.getAppKey(), new IWxCallback() {
               @Override
               public void onSuccess(Object... result) {
                   IYWContact iywContact = (IYWContact) result[0];
                   //YWLog.i(TAG, "移除黑名单成功,  id = " + iywContact.getUserId() + ", appkey = " + iywContact.getAppKey());
                   //IMNotificationUtils.getInstance().showToast(getBaseContext(), "移除黑名单成功,  id = " + iywContact.getUserId() + ", appkey = " + iywContact.getAppKey());
                   //IMNotificationUtils.getInstance().showToast(getBaseContext(), "移除黑名单成功");
                   msgBlackSwitch.setImageResource(R.drawable.off_switch);
                   isBlocked=false;
               }

               @Override
               public void onError(int code, String info) {
                   //YWLog.i(TAG, "移除黑名单失败，code = " + code + ", info = " + info);
                   IMNotificationUtils.getInstance().showToast(getBaseContext(), "移除黑名单失败，code = " + code + ", info = " + info);
               }

               @Override
               public void onProgress(int progress) {

               }
           });
       }else{
           contactManager.addBlackContact(contact.getUserId(), contact.getAppKey(), new IWxCallback() {
               @Override
               public void onSuccess(Object... result) {
                   IYWContact iywContact = (IYWContact) result[0];
                   //YWLog.i(TAG, "加入黑名单成功, id = " + iywContact.getUserId() + ", appkey = " + iywContact.getAppKey());
                   //IMNotificationUtils.getInstance().showToast(getBaseContext(), "加入黑名单成功, id = " + iywContact.getUserId() + ", appkey = " + iywContact.getAppKey());
                   //IMNotificationUtils.getInstance().showToast(getBaseContext(), "加入黑名单成功");
                   msgBlackSwitch.setImageResource(R.drawable.on_switch);
                   isBlocked=true;
               }

               @Override
               public void onError(int code, String info) {
                   //YWLog.i(TAG, "加入黑名单失败，code = " + code + ", info = " + info);
                   IMNotificationUtils.getInstance().showToast(getBaseContext(), "加入黑名单失败，code = " + code + ", info = " + info);
               }

               @Override
               public void onProgress(int progress) {

               }
           });
       }
    }

    private void setMsgRecType() {
        if (contact == null){
            return;
        }
        if(msgRecFlag != YWProfileSettingsConstants.RECEIVE_PEER_MSG_NOT_REMIND) {
            contactManager.setContactMsgRecType(contact, YWProfileSettingsConstants.RECEIVE_PEER_MSG_NOT_REMIND, 10, new SettingsCallback());
        } else {
            contactManager.setContactMsgRecType(contact, YWProfileSettingsConstants.RECEIVE_PEER_MSG, 10, new SettingsCallback());
        }
    }

    class SettingsCallback implements IWxCallback {

        @Override
        public void onError(int code, String info) {
            IMNotificationUtils.getInstance().showToast("onError:"+ " code: " + code + "info:" + info, OpenimContactSettingActivity.this);
        }

        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onSuccess(Object... result) {
            if(contact != null) {
                msgRecFlag = contactManager.getMsgRecFlagForContact(contact);
            } else {
                msgRecFlag = contactManager.getMsgRecFlagForContact(userId, appKey);
            }
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(msgRecFlag != YWProfileSettingsConstants.RECEIVE_PEER_MSG_NOT_REMIND) {
                        msgRemindSwitch.setImageResource(R.drawable.on_switch);
                    } else {
                        msgRemindSwitch.setImageResource(R.drawable.off_switch);
                    }
                    //IMNotificationUtils.getInstance().showToast("onSuccess:" + msgRecFlag,OpenimContactSettingActivity.this);
                }
            });
        }
    }

    protected void clearMsgRecord() {
        String message = "清空的消息再次漫游时不会出现。你确定要清空聊天消息吗？";
//        if (mConversation.getConversationType() == ConversationType.WxConversationType.Room) {
//            message = getResources().getString(
//                    R.string.clear_roomchatting_msg_confirm);
//        }
        AlertDialog.Builder builder = new WxAlertDialog.Builder(OpenimContactSettingActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                conversation.getMessageLoader().deleteAllMessage();
                                IMNotificationUtils.getInstance().showToast("记录已清空",
                                        OpenimContactSettingActivity.this);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
    }

}
