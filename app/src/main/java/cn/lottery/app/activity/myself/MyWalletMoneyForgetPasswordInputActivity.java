package cn.lottery.app.activity.myself;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseActivity;

/**
 * 输入支付密码
 * Created by admin on 2017/5/29.
 */
public class MyWalletMoneyForgetPasswordInputActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout layoutBack,layout1;

    TextView tip1,tip2;

    Button next1,next2;

    EditText moneyCash1,moneyCash2,moneyCash3,moneyCash4,moneyCash5,moneyCash6;

    String pwd="",againPwd="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_my_wallet_input_pay_password);

        initView();
    }

    private void initView() {
        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        tip1= (TextView) findViewById(R.id.tip1);
        tip2= (TextView) findViewById(R.id.tip2);

        next1 = (Button) findViewById(R.id.next1);
        next1.setOnClickListener(this);

        next2 = (Button) findViewById(R.id.next2);
        next2.setOnClickListener(this);

        moneyCash1= (EditText) findViewById(R.id.moneyCash1);
        moneyCash1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==2){
                    moneyCash1.setText(s.toString().substring(0,1));
                    moneyCash2.setText(s.toString().substring(1));
                    moneyCash2.setFocusable(true);
                    moneyCash2.setFocusableInTouchMode(true);
                    moneyCash2.requestFocus();
                    moneyCash2.setSelection(moneyCash2.getText().length());
                }
                else if(s.length()==1){
                    moneyCash1.setFocusable(true);
                    moneyCash1.setFocusableInTouchMode(true);
                    moneyCash1.requestFocus();
                    moneyCash1.setSelection(moneyCash1.getText().length());
                }
                else if(s.length()==0){
                    moneyCash1.setFocusable(true);
                    moneyCash1.setFocusableInTouchMode(true);
                    moneyCash1.requestFocus();
                    moneyCash1.setSelection(moneyCash1.getText().length());
                }
            }
        });

        moneyCash2= (EditText) findViewById(R.id.moneyCash2);
        moneyCash2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==2){
                    moneyCash2.setText(s.toString().substring(0,1));
                    moneyCash3.setText(s.toString().substring(1));
                    moneyCash3.setFocusable(true);
                    moneyCash3.setFocusableInTouchMode(true);
                    moneyCash3.requestFocus();
                    moneyCash3.setSelection(moneyCash3.getText().length());
                }
                else if(s.length()==1){
                    if(moneyCash3.getText().length()==0) {
                        moneyCash3.setFocusable(true);
                        moneyCash3.setFocusableInTouchMode(true);
                        moneyCash3.requestFocus();
                    }
                }
                else if(s.length()==0){
                    moneyCash1.setFocusable(true);
                    moneyCash1.setFocusableInTouchMode(true);
                    moneyCash1.requestFocus();
                    moneyCash1.setSelection(moneyCash1.getText().length());
                }
            }
        });

        moneyCash3= (EditText) findViewById(R.id.moneyCash3);
        moneyCash3.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==2){
                    moneyCash3.setText(s.toString().substring(0,1));
                    moneyCash4.setText(s.toString().substring(1));
                    moneyCash4.setFocusable(true);
                    moneyCash4.setFocusableInTouchMode(true);
                    moneyCash4.requestFocus();
                    moneyCash4.setSelection(moneyCash4.getText().length());
                }
                else if(s.length()==1){
                    if(moneyCash4.getText().length()==0) {
                        moneyCash4.setFocusable(true);
                        moneyCash4.setFocusableInTouchMode(true);
                        moneyCash4.requestFocus();
                    }
                }
                else if(s.length()==0){
                    moneyCash2.setFocusable(true);
                    moneyCash2.setFocusableInTouchMode(true);
                    moneyCash2.requestFocus();
                    moneyCash2.setSelection(moneyCash2.getText().length());
                }
            }
        });

        moneyCash4= (EditText) findViewById(R.id.moneyCash4);
        moneyCash4.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==2){
                    moneyCash4.setText(s.toString().substring(0,1));
                    moneyCash5.setText(s.toString().substring(1));
                    moneyCash5.setFocusable(true);
                    moneyCash5.setFocusableInTouchMode(true);
                    moneyCash5.requestFocus();
                    moneyCash5.setSelection(moneyCash5.getText().length());
                }
                else if(s.length()==1){
                    if(moneyCash5.getText().length()==0) {
                        moneyCash5.setFocusable(true);
                        moneyCash5.setFocusableInTouchMode(true);
                        moneyCash5.requestFocus();
                    }
                }
                else if(s.length()==0){
                    moneyCash3.setFocusable(true);
                    moneyCash3.setFocusableInTouchMode(true);
                    moneyCash3.requestFocus();
                    moneyCash3.setSelection(moneyCash3.getText().length());
                }
            }
        });

        moneyCash5= (EditText) findViewById(R.id.moneyCash5);
        moneyCash5.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==2){
                    moneyCash5.setText(s.toString().substring(0,1));
                    moneyCash6.setText(s.toString().substring(1));
                    moneyCash6.setFocusable(true);
                    moneyCash6.setFocusableInTouchMode(true);
                    moneyCash6.requestFocus();
                    moneyCash6.setSelection(moneyCash6.getText().length());
                }
                else if(s.length()==1){
                    if(moneyCash6.getText().length()==0) {
                        moneyCash6.setFocusable(true);
                        moneyCash6.setFocusableInTouchMode(true);
                        moneyCash6.requestFocus();
                    }
                }
                else if(s.length()==0){
                    moneyCash4.setFocusable(true);
                    moneyCash4.setFocusableInTouchMode(true);
                    moneyCash4.requestFocus();
                    moneyCash4.setSelection(moneyCash4.getText().length());
                }
            }
        });

        moneyCash6= (EditText) findViewById(R.id.moneyCash6);
        moneyCash6.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1){

                }
                else if(s.length()==0){
                    moneyCash5.setFocusable(true);
                    moneyCash5.setFocusableInTouchMode(true);
                    moneyCash5.requestFocus();
                    moneyCash5.setSelection(moneyCash5.getText().length());
                }
            }
        });
    }


    private String getPayPwd(){
        return moneyCash1.getText().toString()+moneyCash2.getText().toString()+moneyCash3.getText().toString()+moneyCash4.getText().toString()+moneyCash5.getText().toString()+moneyCash6.getText().toString();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.next1:
                pwd=getPayPwd();
                if(pwd.length()<6){
                    toast("密码长度不正确");
                }
                else{
                    if(next1.getVisibility()==View.VISIBLE) {
                        pwd=getPayPwd();
                        next1.setVisibility(View.GONE);
                        next2.setVisibility(View.VISIBLE);
                        tip1.setText("再次输入支付密码");
                        moneyCash1.setText("");
                        moneyCash2.setText("");
                        moneyCash3.setText("");
                        moneyCash4.setText("");
                        moneyCash5.setText("");
                        moneyCash6.setText("");
                        moneyCash1.setFocusable(true);
                        moneyCash1.setFocusableInTouchMode(true);
                        moneyCash1.requestFocus();
                        moneyCash1.setSelection(moneyCash1.getText().length());
                    }
                }
                break;
            case R.id.next2:
                againPwd=getPayPwd();
                if(againPwd.length()<6){
                    toast("密码长度不正确");
                }
                if(againPwd.equals(pwd)){
                    requestSetPayPassword();
                }
                else{
                    toast("两次密码不一致");
                }
                break;
        }
    }


    //--------------------请求入口--------------------------

    /**
     *  请求提现
     */
    private void requestSetPayPassword() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("pwd",againPwd);
        params.put("userId", Config.customerID);
        params.put("user_token",Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.setPayPassword, params, null,	"requestSetPayPassword");
    }


    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if(tag.equals("requestSetPayPassword")){
            closeDialog();
            processSetPayPassword(obj);
        }
    }


    /**
     * 请求出错
     */
    @Override
    protected void handError(Object tag, String errorInfo) {

    }

    /**
     * 设置支付密码业务
     */
    private  void processSetPayPassword(JSONObject json){
        try {

            JSONObject msg=json.getJSONObject("msg");

            toast(msg.getString("info"));

            if(msg.getBoolean("success")) finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
