package cn.lottery.app.activity.openim;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.mobileim.YWChannel;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.alibaba.mobileim.fundamental.widget.YWAlertDialog;
import com.alibaba.mobileim.login.YWLoginCode;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.alibaba.mobileim.utility.IMPrefsTools;
import cn.lottery.R;
import cn.lottery.app.activity.trace.TraceActivity;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.openim.NotificationInitSampleHelper;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.openim.UserProfileSampleHelper;
import cn.lottery.framework.openim.common.Constant;

/**
 * Created by admin on 2017/4/25.
 */
public class OpenimLoginActivity extends BaseActivity implements View.OnClickListener {

    Button butLogin;
    EditText txtuid;
    EditText txtpwd;
    EditText txtappkey;
    private ProgressBar progressBar;
    private static final String TAG = OpenimLoginActivity.class.getSimpleName();
    private static final int GUEST = 1;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_openim_login);

        initView();

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toast("onResume");
        //进入登录界面先把上一次登录推出
        OpenIMLoginHelper.getInstance().loginOut_Sample();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private  void initView(){

        progressBar=(ProgressBar)findViewById(R.id.login_progress);

        txtuid= (EditText) findViewById(R.id.txtuid);

        txtpwd= (EditText) findViewById(R.id.txtpwd);

        txtappkey= (EditText) findViewById(R.id.txtappkey);

        butLogin= (Button) findViewById(R.id.butLogin);

        butLogin.setOnClickListener(this);
    }

    private  void initData(){
        txtuid.setText("chy2z");
        txtpwd.setText("123456");
        txtappkey.setText(OpenIMLoginHelper.APP_KEY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.butLogin:

                //判断当前网络状态，若当前无网络则提示用户无网络
                if (YWChannel.getInstance().getNetWorkState().isNetWorkNull()) {
                    Toast.makeText(OpenimLoginActivity.this, "网络已断开，请稍后再试哦~", Toast.LENGTH_SHORT).show();
                    return;
                }

                butLogin.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);

                final String userId=txtuid.getText().toString();
                final String password=txtpwd.getText().toString();
                final String appKey=txtappkey.getText().toString();

                //初始化imkit
                OpenIMLoginHelper.getInstance().initIMKit(userId, appKey);

                //自定义头像和昵称回调初始化(如果不需要自定义头像和昵称，则可以省去)
                UserProfileSampleHelper.initProfileCallback();

                //通知栏相关的初始化
                NotificationInitSampleHelper.init();

                OpenIMLoginHelper.getInstance().login_Sample(userId.toString(), password.toString(), appKey.toString(), new IWxCallback() {

                    @Override
                    public void onSuccess(Object... arg0) {

                        saveLoginInfoToLocal(userId.toString(), password.toString(), appKey.toString());

                        butLogin.setClickable(true);

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(OpenimLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                        YWLog.i(TAG, "login success!");

                        //Intent intent = new Intent(OpenimLoginActivity.this, OpenimFragmentTabs.class);

                        Intent intent = new Intent(OpenimLoginActivity.this, TraceActivity.class);
                        intent.putExtra(Constant.LOGIN_SUCCESS, "loginSuccess");
                        OpenimLoginActivity.this.startActivity(intent);
                        OpenimLoginActivity.this.finish();

                        //客服
                        //YWIMKit mKit = LoginSampleHelper.getInstance().getIMKit();
                        //EServiceContact contact = new EServiceContact("账号001:1");
                        //LoginActivity.this.startActivity(mKit.getChattingActivityIntent(contact));

                        mockConversationForDemo();
                    }

                    @Override
                    public void onProgress(int arg0) {

                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        progressBar.setVisibility(View.GONE);
                        if (errorCode == YWLoginCode.LOGON_FAIL_INVALIDUSER) { //若用户不存在，则提示使用游客方式登陆
                            showDialog(GUEST);
                        } else {
                            butLogin.setClickable(true);
                            YWLog.w(TAG, "登录失败，错误码：" + errorCode + "  错误信息：" + errorMessage);
                            IMNotificationUtils.getInstance().showToast(OpenimLoginActivity.this, errorMessage);
                        }
                    }
                });

                break;
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case GUEST: {
                AlertDialog.Builder builder = new YWAlertDialog.Builder(this);
                builder.setMessage("账号不存在，是否以游客方式登录？")
                        .setCancelable(false)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.dismiss();
                                        //guest_login();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.dismiss();
                                        butLogin.setClickable(true);
                                    }
                                });
                AlertDialog dialog = builder.create();
                return dialog;

            }
        }
        return super.onCreateDialog(id);
    }

    /**
     * 保存登录的用户名密码到本地
     *
     * @param userId
     * @param password
     */
    private void saveLoginInfoToLocal(String userId, String password, String appkey) {
        IMPrefsTools.setStringPrefs(OpenimLoginActivity.this, "USER_ID", userId);
        IMPrefsTools.setStringPrefs(OpenimLoginActivity.this, "PASSWORD", password);
        IMPrefsTools.setStringPrefs(OpenimLoginActivity.this, "APPKEY", appkey);
    }

    /**
     * 模拟两条聊天数据，仅用于演示
     */
    private void mockConversationForDemo() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                YWIMKit imKit = OpenIMLoginHelper.getInstance().getIMKit();
                String targetId1 = "chy2z";
                String targetId2 = "tq";

                IYWConversationService conversationService = imKit.getConversationService();
                YWConversation conversation = conversationService.getConversationCreater().createConversationIfNotExist(targetId1);
                YWMessage msg = YWMessageChannel.createTextMessage("hello");
                if (conversation.getLastestMessage() == null) {
                    conversation.getMessageSender().sendMessage(msg, 120, null);
                }

                YWConversation conversation2 = conversationService.getConversationCreater().createConversationIfNotExist(targetId2);
                YWMessage msg2 = YWMessageChannel.createTextMessage("hi");
                if (conversation2.getLastestMessage() == null) {
                    conversation2.getMessageSender().sendMessage(msg2, 120, null);
                }
            }
        });
    }
}
