package cn.lottery.app.activity.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.util.SPUtils;
import cn.lottery.framework.util.SecurityUtils;
import cn.lottery.framework.util.Valid;
import cn.lottery.framework.widget.RoundedBitmapDisplayer.MyRoundedBitmapDisplayer;

/**
 * 第三方账户绑定手机
 * Created by admin on 2017/6/20.
 */
public class BindPhoneActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout layoutBack,linearLayoutpwd,linearLayoutpwdagain;

    ImageView imgHead;

    TextView nickName,txtpwd,txtpwdagain;

    EditText phone,code,pwd,pwdAgain;

    Button getCode,save;

    int recLen = 0;

    boolean excistPhone=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_bind_phone);

        initView();

        initData();
    }

    private void initView() {

        linearLayoutpwd = (LinearLayout) findViewById(R.id.linearLayoutpwd);
        linearLayoutpwdagain = (LinearLayout) findViewById(R.id.linearLayoutpwdagain);
        txtpwd = (TextView) findViewById(R.id.txtpwd);
        txtpwdagain = (TextView) findViewById(R.id.txtpwdagain);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        getCode = (Button) findViewById(R.id.getCode);
        getCode.setOnClickListener(this);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);

        imgHead = (ImageView) findViewById(R.id.imgHead);
        nickName = (TextView) findViewById(R.id.nickName);
        phone = (EditText) findViewById(R.id.phone);
        code = (EditText) findViewById(R.id.code);
        pwd = (EditText) findViewById(R.id.pwd);
        pwdAgain = (EditText) findViewById(R.id.pwdAgain);


        linearLayoutpwd.setVisibility(View.INVISIBLE);
        linearLayoutpwdagain.setVisibility(View.INVISIBLE);
        txtpwd.setVisibility(View.INVISIBLE);
        txtpwdagain.setVisibility(View.INVISIBLE);
        pwd.setVisibility(View.INVISIBLE);
        pwdAgain.setVisibility(View.INVISIBLE);
    }

    private void  initData(){

        nickName.setText(Config.wx_nicknames);

        DisplayImageOptions option = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.login_head)
                .showImageOnFail(R.drawable.login_head).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new MyRoundedBitmapDisplayer(0))
                .build();

        app.getImageLoader().displayImage(Config.wx_headimgurls, imgHead, option);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.getCode:
                if (!Valid.isMobileNO(phone.getText().toString().trim())) {
                    toast("输入的手机号码不正确");
                } else {
                    if (recLen == 0) {
                        recLen = 1;
                        getCode.setEnabled(false);
                        Config.phone = phone.getText().toString().trim();
                        requestGetPhoneCode();
                    }
                }
                break;
            case R.id.save:
                if(check()) requestBindRegister();
                break;
        }
    }


    private boolean check() {

        String code1 = pwd.getText().toString().trim();
        String code2 = pwdAgain.getText().toString().trim();

        if (!Valid.isMobileNO(phone.getText().toString().trim())) {
            toast("手机号码不正确");
            return false;
        }

        if (code.getText().toString().trim().equals("")) {
            toast("验证码不能为空");
            return false;
        }

        if(!excistPhone) {
            if (code1.equals("")) {
                toast("登录密码不能为空");
                return false;
            } else if (code2.equals("")) {
                toast("确认密码不能为空");
                return false;
            } else if (code1.length() < Config.userPwdLength || code2.length() < Config.userPwdLength) {
                toast("密码长度最少" + Config.userPwdLength + "位");
                return false;
            } else if (!code1.equals(code2)) {
                toast("两次密码不一样");
                return false;
            } else return true;
        }

        return true;
    }

    /**
     * 获取验证码接口
     */
    private void  requestGetPhoneCode(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", phone.getText().toString());
        params.put("type", "1");
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.getPhoneCode, params, null,"requestGetPhoneCode");
    }

    /**
     * 用户注册
     */
    private void requestBindRegister() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", phone.getText().toString());
        params.put("code", code.getText().toString());
        params.put("thirdNickName", nickName.getText().toString());
        params.put("thirdImg", Config.wx_headimgurls);
        if(!excistPhone)
            params.put("loginPwd", SecurityUtils.MD5(pwd.getText().toString().trim()));
        params.put("accountName", Config.wx_unionid);
        params.put("type", Config.wx_type);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.bindRegister, params, null,"requestBindRegister");
    }

    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestBindRegister")) {
            closeDialog();
            processRequestBindRegsiter(obj);
        }
        else if (tag.equals("requestGetPhoneCode")) {
            closeDialog();
            processrequestGetPhoneCode(obj);
        }
    }


    /**
     * 请求出错
     */
    @Override
    protected void handError(Object tag, String errorInfo) {
        if (tag.equals("requestGetPhoneCode")) {
            recLen = 0;
            getCode.setEnabled(true);
        }
    }

    //-----------------------请求结果业务逻辑-----------------------

    private void processRequestBindRegsiter(JSONObject json){

        try {
            JSONObject msg = json.getJSONObject("msg");
            JSONObject user = json.getJSONObject("user");
            toast(msg.getString("info"));
            Config.IMUserId=user.getString("accountId");
            Config.IMPassword=user.getString("accountPwd");
            Config.status=user.getString("status");
            Config.userToken=user.getString("user_token");
            Config.customerID=user.getInt("id")+"";
            Config.nickname=nickName.getText().toString();
            Config.phone=phone.getText().toString();
            Config.icon=user.getString("img");
            Config.loginType="phone";
            Config.isSwitchUserSucess=true; //标记切换用户成功

            SPUtils.put(getBaseContext(), "IMUserId", Config.IMUserId);
            SPUtils.put(getBaseContext(), "IMPassword", Config.IMPassword);
            SPUtils.put(getBaseContext(), "status", Config.status);
            SPUtils.put(getBaseContext(), "icon", Config.icon);
            SPUtils.put(getBaseContext(), "nickname", Config.nickname);
            SPUtils.put(getBaseContext(), "userToken", Config.userToken);
            SPUtils.put(getBaseContext(), "loginType", Config.loginType);
            SPUtils.put(getBaseContext(), "points", Config.points);
            SPUtils.put(getBaseContext(), "customerID", Config.customerID);

            if(Config.status.equals("1")){
                if((Boolean) SPUtils.get(getBaseContext(), "isFirstAuthentication","").equals("")) {
                    //注册用户，进入实名认证
                    Intent it=new Intent(this, ImmediateAuthenticationActivity.class);
                    it.putExtra("type","1");
                    startActivity(it);
                }
            }

            this.finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processrequestGetPhoneCode(JSONObject json){
        try {
            JSONObject msg = json.getJSONObject("msg");
            excistPhone=json.getBoolean("existUser");
            if(msg.getBoolean("success")){
                 if(!excistPhone){
                     linearLayoutpwd.setVisibility(View.VISIBLE);
                     linearLayoutpwdagain.setVisibility(View.VISIBLE);
                     txtpwd.setVisibility(View.VISIBLE);
                     txtpwdagain.setVisibility(View.VISIBLE);
                     pwd.setVisibility(View.VISIBLE);
                     pwdAgain.setVisibility(View.VISIBLE);
                 }
            }
            toast(msg.getString("info"));
            new Thread(new MyReadNumbersThread()).start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //============================================= handle

    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1:
                    getCode.setText("" + (Config.waitValidCodeTime-recLen)+"s");
                    recLen++;
                    break;
                case 0:
                    recLen=0;
                    getCode.setText("获取");
                    getCode.setEnabled(true);
                    break;
            }

            super.handleMessage(msg);
        }
    };

    public class MyReadNumbersThread implements Runnable { // thread
        @Override
        public void run() {
            while (true) {
                try {
                    if (recLen <= Config.waitValidCodeTime){
                        Thread.sleep(1000); // sleep 1000ms
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                    else
                    {
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                        break;
                    }
                } catch (Exception e) {
                }
            }
        }
    }
}
