package cn.lottery.app.activity.myself;

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

/**
 * 忘记或者修改支付密码
 * Created by admin on 2017/5/29.
 */
public class MyWalletMoneyForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    EditText phone,code;

    LinearLayout layoutBack;

    Button getCode,next;

    private int recLen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_my_wallet_forget_password);

        initView();
    }

    private void initView(){
        phone = (EditText) findViewById(R.id.phone);
        code = (EditText) findViewById(R.id.code);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        getCode = (Button) findViewById(R.id.getCode);
        getCode.setOnClickListener(this);

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);

        String p=Config.phone;
        phone.setText((p.replace(p.substring(3, p.length() - 3), "*****")));
        phone.setEnabled(false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.getCode:
                if (recLen == 0) {
                    recLen = 1;
                    getCode.setEnabled(false);
                    requestGetPhoneCode();
                }
                break;
            case R.id.next:
                if(check()) {
                   requestCheckCode();
                }
                break;
        }
    }


    private boolean check(){
        if(code.getText().toString().trim().equals("")){
            toast("验证码不能为空");
            return false;
        }
        return true;
    }


    //--------------------请求入口--------------------------

    /**
     * 获取验证码接口
     */
    private void  requestGetPhoneCode(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", Config.phone);
        params.put("type", "3");
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.getPhoneCode, params, null,"requestGetPhoneCode");
    }


    /**
     * 验证手机和短信
     */
    private void  requestCheckCode(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", Config.phone);
        params.put("key", code.getText().toString());
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.checkPayPwdCode, params, null,"requestCheckCode");
    }

    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestGetPhoneCode")) {
            closeDialog();
            processGetPhoneCode(obj);
        }
        else if (tag.equals("requestCheckCode")) {
            closeDialog();
            processCheckCode(obj);
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

    /**
     * 获取验证码业务逻辑
     * @param json
     */
    private void processGetPhoneCode(JSONObject json){
        try {
            JSONObject msg = json.getJSONObject("msg");
            toast(msg.getString("info"));
            new Thread(new MyReadNumbersThread()).start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证校验证逻辑
     * @param json
     */
    private  void processCheckCode(JSONObject json){
        try {
            JSONObject msg = json.getJSONObject("msg");
            if(json.getBoolean("validateResult")) {
                Intent it = new Intent(MyWalletMoneyForgetPasswordActivity.this, MyWalletMoneyForgetPasswordInputActivity.class);
                it.putExtra("phone", Config.phone);
                it.putExtra("code", code.getText().toString().trim());
                startActivity(it);
                finish();
            }
            else toast(msg.getString("info"));

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
