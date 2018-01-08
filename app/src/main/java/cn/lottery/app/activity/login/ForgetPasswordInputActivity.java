package cn.lottery.app.activity.login;

import android.content.Intent;
import android.os.Bundle;
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
import cn.lottery.framework.util.SecurityUtils;

/**
 * 忘记密码输入新的密码
 * Created by admin on 2017/5/29.
 */
public class ForgetPasswordInputActivity extends BaseActivity implements View.OnClickListener {

    EditText newPwdAgain,newPwd;

    LinearLayout layoutBack;

    Button save;

    String phone,code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_forget_password_input);

        initView();

        initData();
    }

    private void initView(){
        newPwd = (EditText) findViewById(R.id.newPwd);
        newPwdAgain = (EditText) findViewById(R.id.newPwdAgain);
        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
    }

    private void initData(){
        Intent it = getIntent();
        phone = it.getStringExtra("phone"); //如果类型不对，取到的值为null
        code = it.getStringExtra("code");
        //toast(phone+","+code);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.save:
                if(check()) {
                    requestForgetPassword();
                }
                break;
        }
    }

    private boolean check(){
        String code1=newPwd.getText().toString().trim();
        String code2=newPwdAgain.getText().toString().trim();

        if(code1.equals("")){
            toast("新的密码不能为空");
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


    /**
     * 获取验证码接口
     */
    private void  requestForgetPassword(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", phone);
        params.put("code", code);
        params.put("newPwd",  SecurityUtils.MD5(newPwd.getText().toString().trim()));
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.forgetPassword, params, null,"requestForgetPassword");
    }

    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestForgetPassword")) {
            closeDialog();
            processRequestForgetPassword(obj);
        }
    }

    /**
     * 请求出错
     */
    @Override
    protected void handError(Object tag, String errorInfo) {

    }

    /**
     * 忘记密码业务逻辑
     * @param json
     */
    private void processRequestForgetPassword(JSONObject json){
        try {
            JSONObject msg = json.getJSONObject("msg");

            toast(msg.getString("info"));

            if(msg.getBoolean("success")){
                     finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
