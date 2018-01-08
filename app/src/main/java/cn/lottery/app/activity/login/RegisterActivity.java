package cn.lottery.app.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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

/**
 * 注册
 * Created by admin on 2017/5/26.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    EditText nick,phone,code,pwd,pwdAgain;

    LinearLayout layoutBack;

    Button getCode,save;

    int recLen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_register);

        initView();
    }

    private void initView(){
        nick = (EditText) findViewById(R.id.nick);
        phone = (EditText) findViewById(R.id.phone);
        code = (EditText) findViewById(R.id.code);
        pwd = (EditText) findViewById(R.id.pwd);
        pwdAgain = (EditText) findViewById(R.id.pwdAgain);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        getCode = (Button) findViewById(R.id.getCode);
        getCode.setOnClickListener(this);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
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
               if(check()) requestRegister();
                break;
        }
    }


    private boolean check(){

        String code1=pwd.getText().toString().trim();
        String code2=pwdAgain.getText().toString().trim();

        if (!Valid.isMobileNO(phone.getText().toString().trim())) {
            toast("手机号码不正确");
            return false;
        }

        if(nick.getText().toString().trim().equals("")){
            toast("用户昵称不能为空");
            return false;
        }

        if(code.getText().toString().trim().equals("")){
            toast("验证码不能为空");
            return false;
        }

        if(code1.equals("")){
            toast("登录密码不能为空");
            return false;
        }
        else if(code2.equals("")){
            toast("确认密码不能为空");
            return false;
        }
        else if(code1.length()<Config.userPwdLength||code2.length()<Config.userPwdLength)
        {
            toast("密码长度最少"+Config.userPwdLength+"位");
            return false;
        }
        else if(!code1.equals(code2)){
            toast("两次密码不一样");
            return false;
        }
        else return true;
    }



    //--------------------请求入口--------------------------

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
    private void requestRegister() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nickName", nick.getText().toString());
        params.put("password", SecurityUtils.MD5(pwd.getText().toString().trim()));
        params.put("phone", phone.getText().toString());
        params.put("code", code.getText().toString());
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.register, params, null,"requestRegister");
    }



    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestRegister")) {
            closeDialog();
            processRequestRegsiter(obj);
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

    private void processRequestRegsiter(JSONObject json){

        try {
            JSONObject msg = json.getJSONObject("msg");
            toast(msg.getString("info"));
            Config.points=0;
            Config.IMUserId=json.getString("IMUserId");
            Config.IMPassword=json.getString("IMPassword");
            Config.status=json.getString("status");
            Config.userToken=json.getString("user_token");
            Config.customerID=json.getInt("userId")+"";
            Config.nickname=nick.getText().toString();
            Config.phone=phone.getText().toString();
            Config.loginType="phone";
            Config.isSwitchUserSucess=true; //标记切换用户成功

            SPUtils.put(getBaseContext(), "status", Config.status);

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
                toast(msg.getString("info"));
                new Thread(new MyReadNumbersThread()).start();
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }


    final Handler handler = new Handler(){          // handle
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
