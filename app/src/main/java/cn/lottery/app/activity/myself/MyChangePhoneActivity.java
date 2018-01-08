package cn.lottery.app.activity.myself;

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
import cn.lottery.framework.util.Valid;

/**
 * Created by admin on 2017/6/25.
 */
public class MyChangePhoneActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout layoutBack;

    Button next;

    EditText pwd,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_my_change_phone_one);

        initView();
    }

    private void initView() {
        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        next= (Button) findViewById(R.id.next);
        next.setOnClickListener(this);

        pwd= (EditText) findViewById(R.id.pwd);
        pwd.setOnClickListener(this);

        phone= (EditText) findViewById(R.id.phone);
        phone.setOnClickListener(this);
    }

    private boolean check(){
        if(phone.getText().toString().trim().equals("")){
            toast("手机号码不能为空");
            return false;
        }
        if (!Valid.isMobileNO(phone.getText().toString().trim())) {
            toast("手机号码不正确");
            return false;
        }
        if(!phone.getText().toString().trim().equals(Config.phone)){
            toast("手机号码与登录账户不一致");
            return false;
        }
        if(pwd.getText().toString().trim().equals("")){
            toast("密码不能为空");
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.next:
                if(check())
                   requestAccountValidate();
                break;
        }
    }


    /**
     * 账户验证接口
     */
    private void requestAccountValidate()
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", phone.getText().toString().trim());
        params.put("password", SecurityUtils.MD5(pwd.getText().toString().trim()));
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.accountValidate, params, null,"requestAccountValidate");
    }


    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestAccountValidate")) {
            closeDialog();
            processAccountValidate(obj);
        }
    }

    /**
     * 请求出错
     */
    @Override
    protected void handError(Object tag, String errorInfo) {

    }

    /**
     * 账户验证业务
     * @param json
     */
    private void processAccountValidate(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            toast(msg.getString("info"));
            if(json.getBoolean("validateResult")) {
                Intent it = new Intent(this, MyChangePhoneSecondActivity.class);
                startActivity(it);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}