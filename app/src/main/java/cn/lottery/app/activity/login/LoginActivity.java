package cn.lottery.app.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWChannel;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.login.YWLoginCode;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.alibaba.mobileim.utility.IMPrefsTools;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.lottery.R;
import cn.lottery.app.activity.HomePageActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.openim.NotificationInitSampleHelper;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.openim.UserProfileSampleHelper;
import cn.lottery.framework.util.L;
import cn.lottery.framework.util.SPUtils;
import cn.lottery.framework.util.SecurityUtils;
import cn.lottery.framework.util.Valid;

/**
 * 登录页面
 * Created by admin on 2017/5/26.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    ImageView weixinAccount,qqAccount,sinaAccount;

    LinearLayout layoutBack;

    Button login;

    TextView freeRegister,forgetPwd;

    EditText phone,pwd;

    UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_login);

        initView();

        outlogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Config.userToken.equals("")){
            toast("登录成功");
            openIMLogin();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 友盟社会化分享回调
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        weixinAccount=(ImageView)  findViewById(R.id.weixinAccount);
        weixinAccount.setOnClickListener(this);

        qqAccount=(ImageView)  findViewById(R.id.qqAccount);
        qqAccount.setOnClickListener(this);

        sinaAccount=(ImageView)  findViewById(R.id.sinaAccount);
        sinaAccount.setOnClickListener(this);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        freeRegister = (TextView) findViewById(R.id.freeRegister);
        freeRegister.setOnClickListener(this);

        forgetPwd = (TextView) findViewById(R.id.forgetPwd);
        forgetPwd.setOnClickListener(this);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        phone = (EditText) findViewById(R.id.phone);

        pwd = (EditText) findViewById(R.id.pwd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.freeRegister:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.forgetPwd:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            case R.id.login:
                if (!Valid.isMobileNO(phone.getText().toString().trim())) {
                    toast("手机号码不正确");
                }
                else if(pwd.getText().toString().trim().equals("")){
                    toast("请输入密码");
                }
                else{
                    requestPhoneLogin();
                }
                break;
            case R.id.weixinAccount:
                umengDoOauthVerify(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.qqAccount:
                umengDoOauthVerify(SHARE_MEDIA.QQ);
                break;
            case R.id.sinaAccount:
                umengDoOauthVerify(SHARE_MEDIA.SINA);
                break;
        }
    }

    //================================友盟

    /**
     * 友盟授权
     */
    private void umengDoOauthVerify(SHARE_MEDIA type) {

        mShareAPI = UMShareAPI.get(this);

        platform=type;

        if (SHARE_MEDIA.SINA == type) {

            mShareAPI.isAuthorize(this,type);

            //toast("配置回调地址");

            com.umeng.socialize.Config.REDIRECT_URL="http://sns.whalecloud.com/sina2/callback";
        }

        UMAuthListener umAuthListener = new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action,Map<String, String> data) {
                //获取用户信息
                umengGetPlatformInfo();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText(getApplicationContext(), "授权失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText(getApplicationContext(), "授权取消",Toast.LENGTH_SHORT).show();
            }
        };

        mShareAPI.doOauthVerify(this, platform, umAuthListener);
    }


    /**
     * 获取用户信息
     */
    private void umengGetPlatformInfo() {

        UMAuthListener umGetPlatformInfoListener = new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action,Map<String, String> data) {

                StringBuilder sb = new StringBuilder();

                Set<String> keys = data.keySet();

                for(String key : keys){
                    sb.append(key+"="+data.get(key).toString()+"\r\n");
                }

                //L.e("第三方登录参数",sb.toString());

                //Toast.makeText(getApplicationContext(),sb.toString(),Toast.LENGTH_SHORT).show();

                //mShareAPI.getPlatformInfo(DialogLoginSelectActivity.this, platform, umAuthListener);

                //Config.wx_unionid=data.get("unionid").toString();
                //Config.wx_nicknames=data.get("nickname").toString();
                //Config.wx_headimgurls=data.get("headimgurl").toString();

                //Toast.makeText(getApplicationContext(), "获取用户信息 succeed",Toast.LENGTH_SHORT).show();

                //Toast.makeText(getApplicationContext(), Config.wx_nicknames, Toast.LENGTH_SHORT).show();

                if (SHARE_MEDIA.SINA == platform) {
                    try {
                        JSONTokener jsonTokener = new JSONTokener(data.get("result").toString());
                        JSONObject json=(JSONObject) jsonTokener.nextValue();
                        Config.wx_unionid=json.get("id").toString();
                        Config.wx_nicknames=json.get("screen_name").toString();
                        Config.wx_type="4";
                        Config.wx_headimgurls=json.get("profile_image_url").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if (SHARE_MEDIA.QQ == platform) {
                    Config.wx_unionid=data.get("openid").toString();
                    Config.wx_nicknames=data.get("screen_name").toString();
                    Config.wx_type="3";
                    Config.wx_headimgurls=data.get("profile_image_url").toString();
                }
                else if (SHARE_MEDIA.WEIXIN == platform) {
                    Config.wx_unionid=data.get("unionid").toString();
                    Config.wx_nicknames=data.get("nickname").toString();
                    Config.wx_type="2";
                    Config.wx_headimgurls=data.get("headimgurl").toString();
                }

                requestThirdLogin();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {

                L.d("获取用户信息失败",t.getMessage());

                Toast.makeText(getApplicationContext(), "获取用户信息 fail",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText(getApplicationContext(), "获取用户信息 cancel",
                        Toast.LENGTH_SHORT).show();
            }
        };

        mShareAPI.getPlatformInfo(this, platform, umGetPlatformInfoListener);
    }


    //================================http请求

    /**
     * 登录接口
     */
    private void requestPhoneLogin()
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("password", SecurityUtils.MD5(pwd.getText().toString().trim()));
        params.put("phone", phone.getText().toString().trim());
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.phoneLogin, params, null,"requestPhoneLogin");
    }

    /**
     * 验证是否绑定第三方登录
     */
    private void requestThirdLogin(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accountName", Config.wx_unionid);
        params.put("thirdNickName", Config.wx_nicknames);
        params.put("type", Config.wx_type);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.thirdLogin, params, null,"special_requestThirdLogin");
    }

    /**
     * 更新友盟信息
     */
    private void requestEditUUID(){
        if(Config.deviceToken.equals("")){Config.deviceToken= app.getPushAgent().getRegistrationId();}
        if(Config.UUID.equals("")){ Config.UUID=getUUID(); }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", Config.customerID);
        params.put("uuid", Config.UUID);
        params.put("deviceToken", Config.deviceToken);
        params.put("type", "2");
        post(Config.URL_PREFIX + RequestUrl.editUUID, params, null,"requestEditUUID");
    }

    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestPhoneLogin")) {
            closeDialog();
            processPhoneLogin(obj);
        }
        else if(tag.equals("special_requestThirdLogin")){
            closeDialog();
            processThirdLogin(obj);
        }
        else if(tag.equals("requestEditUUID")){
            processEditUUID(obj);
        }
    }

    /**
     * 请求出错
     */
    @Override
    protected void handError(Object tag, String errorInfo) {

    }

    /**
     * 处理手机登录结果
     * @param json
     */
    private void processPhoneLogin(JSONObject json){
        try {

            JSONObject msg=json.getJSONObject("msg");

            toast(msg.getString("info"));

            JSONObject user=json.getJSONObject("user");

            Config.IMUserId=user.getString("accountId");
            Config.IMPassword=user.getString("accountPwd");
            Config.gender=user.getString("gender")+"";
            Config.balance=user.getDouble("balance")+"";
            Config.status=user.getString("status");
            Config.phone=user.getString("phone");
            Config.nickname=user.getString("nickName");
            Config.userToken=user.getString("user_token");
            Config.customerID=user.getString("id");
            Config.loginType="phone";
            Config.points=0;
            Config.icon=user.getString("img");
            Config.backgroundImg=user.getString("backgroundImg");

            SPUtils.put(getBaseContext(), "phone", Config.phone);
            SPUtils.put(getBaseContext(), "backgroundImg", Config.backgroundImg);
            SPUtils.put(getBaseContext(), "IMUserId", Config.IMUserId);
            SPUtils.put(getBaseContext(), "IMPassword", Config.IMPassword);
            SPUtils.put(getBaseContext(), "status", Config.status);
            SPUtils.put(getBaseContext(), "icon", Config.icon);
            SPUtils.put(getBaseContext(), "nickname", Config.nickname);
            SPUtils.put(getBaseContext(), "userToken", Config.userToken);
            SPUtils.put(getBaseContext(), "loginType", Config.loginType);
            SPUtils.put(getBaseContext(), "points", Config.points);
            SPUtils.put(getBaseContext(), "customerID", Config.customerID);

            requestEditUUID();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理第三方登录结果
     * @param json
     */
    private void processThirdLogin(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            if(msg.getBoolean("success")){
                JSONObject user=json.getJSONObject("user");
                Config.IMUserId=user.getString("accountId");
                Config.IMPassword=user.getString("accountPwd");
                Config.gender=user.getString("gender")+"";
                Config.balance=user.getDouble("balance")+"";
                Config.status=user.getString("status");
                Config.phone=user.getString("phone");
                Config.nickname=user.getString("nickName");
                Config.userToken=user.getString("user_token");
                Config.customerID=user.getString("id");
                Config.loginType="phone";
                Config.points=0;
                Config.icon=user.getString("img");
                Config.backgroundImg=user.getString("backgroundImg");

                SPUtils.put(getBaseContext(), "phone", Config.phone);
                SPUtils.put(getBaseContext(), "backgroundImg", Config.backgroundImg);
                SPUtils.put(getBaseContext(), "IMUserId", Config.IMUserId);
                SPUtils.put(getBaseContext(), "IMPassword", Config.IMPassword);
                SPUtils.put(getBaseContext(), "status", Config.status);
                SPUtils.put(getBaseContext(), "icon", Config.icon);
                SPUtils.put(getBaseContext(), "nickname", Config.nickname);
                SPUtils.put(getBaseContext(), "userToken", Config.userToken);
                SPUtils.put(getBaseContext(), "loginType", Config.loginType);
                SPUtils.put(getBaseContext(), "points", Config.points);
                SPUtils.put(getBaseContext(), "customerID", Config.customerID);

                requestEditUUID();
            }
            else{
                toast("请绑定手机号码");
                startActivity(new Intent(LoginActivity.this,BindPhoneActivity.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新友盟信息
     * @param json
     */
    private  void processEditUUID(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            if(msg.getBoolean("success")) {

                openIMLogin(); //开启云旺登录

            }
            else{
                toast("更新友盟信息出错！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 阿里云旺登录
     */
    private void openIMLogin(){

        //判断当前网络状态，若当前无网络则提示用户无网络
        if (YWChannel.getInstance().getNetWorkState().isNetWorkNull()) {
            toast("网络已断开，请稍后再试哦~");
            return;
        }

        final String userId=Config.IMUserId;
        final String password=Config.IMPassword;
        final String appKey= OpenIMLoginHelper.APP_KEY;

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

                //toast("云旺账户登录成功");

                Intent resultData=new Intent();
                resultData.putExtra("data","login");
                setResult(2,resultData);

                String setting = getIntent().getStringExtra("setting");

                if(setting==null) {
                    if (Config.status.equals("1")) { //未实名认证，第一次登录时弹出
                        if ((Boolean) SPUtils.get(getBaseContext(), "isFirstAuthentication", "").equals("")) {
                            SPUtils.put(getBaseContext(), "isFirstAuthentication", "true");
                            //注册用户，进入实名认证
                            Intent it = new Intent(LoginActivity.this, ImmediateAuthenticationActivity.class);
                            it.putExtra("type", "1");
                            startActivity(it);
                        }
                    }
                }
                else{
                    Intent it = new Intent(LoginActivity.this, HomePageActivity.class);
                    startActivity(it);
                }

                finish();


                //YWLog.i(TAG, "login success!");

                //Intent intent = new Intent(OpenimLoginActivity.this, OpenimFragmentTabs.class);

                //Intent intent = new Intent(OpenimLoginActivity.this, TraceActivity.class);
                //intent.putExtra(Constant.LOGIN_SUCCESS, "loginSuccess");
                //OpenimLoginActivity.this.startActivity(intent);
                //OpenimLoginActivity.this.finish();

                //客服
                //YWIMKit mKit = LoginSampleHelper.getInstance().getIMKit();
                //EServiceContact contact = new EServiceContact("账号001:1");
                //LoginActivity.this.startActivity(mKit.getChattingActivityIntent(contact));

                //mockConversationForDemo();
            }

            @Override
            public void onProgress(int arg0) {

            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                //progressBar.setVisibility(View.GONE);
                if (errorCode == YWLoginCode.LOGON_FAIL_INVALIDUSER) { //若用户不存在，则提示使用游客方式登陆
                    //showDialog(GUEST);
                    toast("云旺用户不存在");
                } else {
                    //butLogin.setClickable(true);
                    YWLog.w(TAG, "登录失败，错误码：" + errorCode + "  错误信息：" + errorMessage);
                    IMNotificationUtils.getInstance().showToast(LoginActivity.this, errorMessage);
                }
            }
        });
    }

    /**
     * 保存登录的用户名密码到本地
     *
     * @param userId
     * @param password
     */
    private void saveLoginInfoToLocal(String userId, String password, String appkey) {
        IMPrefsTools.setStringPrefs(LoginActivity.this, "USER_ID", userId);
        IMPrefsTools.setStringPrefs(LoginActivity.this, "PASSWORD", password);
        IMPrefsTools.setStringPrefs(LoginActivity.this, "APPKEY", appkey);
    }

}