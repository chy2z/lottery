package cn.lottery.app.activity.myself;

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
import cn.lottery.framework.util.Valid;

/**
 * 绑定支付宝
 * Created by admin on 2017/5/29.
 */
public class BindAlipayOneActivity extends BaseActivity implements View.OnClickListener {

    EditText phone,code,alipy;

    LinearLayout layoutBack;

    Button getCode,next;

    private int recLen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_bing_alipay_one);

        initView();

        initData();
    }

    private void initView(){
        phone = (EditText) findViewById(R.id.phone);
        code = (EditText) findViewById(R.id.code);
        alipy = (EditText) findViewById(R.id.alipy);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        getCode = (Button) findViewById(R.id.getCode);
        getCode.setOnClickListener(this);

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
    }

    private void initData(){
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
                if (!Valid.isMobileNO(Config.phone)) {
                    toast("输入的手机号码不正确");
                } else {
                    if (recLen == 0) {
                        recLen = 1;
                        getCode.setEnabled(false);
                        requestGetPhoneCode();
                    }
                }
                break;
            case R.id.next:
                if(check()) {
                    requestBindAlipay();
                }
                break;
        }
    }


    private boolean check(){
        if(code.getText().toString().trim().equals("")){
            toast("验证码不能为空");
            return false;
        }
        if(alipy.getText().toString().trim().equals("")){
            toast("支付宝账户不能为空");
            return false;
        }
        if(alipy.getText().toString().trim().length()<Config.alipayLength){
            toast("支付宝账户不正确");
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
        params.put("type", "4");
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.getPhoneCode, params, null,"requestGetPhoneCode");
    }

    /**
     * 绑定支付宝账户
     */
    private void  requestBindAlipay(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", Config.phone);
        params.put("alipayAccount", alipy.getText().toString());
        params.put("key", code.getText().toString());
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.bindAlipay, params, null,"requestBindAlipay");
    }

    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestGetPhoneCode")) {
            closeDialog();
            processrequestGetPhoneCode(obj);
        }
        else if (tag.equals("requestBindAlipay")) {
            closeDialog();
            processrequestBindAlipay(obj);
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
    private void processrequestGetPhoneCode(JSONObject json){
        try {
            JSONObject msg = json.getJSONObject("msg");
            toast(msg.getString("info"));
            new Thread(new MyReadNumbersThread()).start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定支付宝业务
     * @param json
     */
    private void processrequestBindAlipay(JSONObject json){
        try {
            JSONObject msg = json.getJSONObject("msg");
            toast(msg.getString("info"));
            finish();
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
