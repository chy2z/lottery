package cn.lottery.app.activity.myself;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseActivity;

/**
 * 现金提现
 * Created by admin on 2017/6/23.
 */
public class MyWalletMoneyCashPwdActivity extends BaseActivity implements View.OnClickListener {

    ImageView imageViewClose;

    TextView forgotPassword,moneyCash;

    EditText moneyCash1,moneyCash2,moneyCash3,moneyCash4,moneyCash5,moneyCash6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_my_wallet_money_cash_pwd);

        initView();

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(check()) {
            requestMoneyCash();
        }
    }

    private void initView() {
        imageViewClose = (ImageView) findViewById(R.id.imageViewClose);
        imageViewClose.setOnClickListener(this);

        moneyCash= (TextView) findViewById(R.id.moneyCash);

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

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
                    if(check()) {
                        requestMoneyCash();
                    }
                    else toast("密码不正确");
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

    private  void initData(){
        Intent it = getIntent();
        moneyCash.setText(it.getStringExtra("moneyCash"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewClose:
                finish();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, MyWalletMoneyForgetPasswordActivity.class));
                break;
        }
    }

    private String getPayPwd(){
        return moneyCash1.getText().toString()+moneyCash2.getText().toString()+moneyCash3.getText().toString()+moneyCash4.getText().toString()+moneyCash5.getText().toString()+moneyCash6.getText().toString();
    }

    private Boolean check(){
        return  getPayPwd().length()==6;
    }

    /**
     *  请求提现
     */
    private void requestMoneyCash() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("applyMoney",moneyCash.getText().toString());
        params.put("payPwd",getPayPwd());
        params.put("userId",Config.customerID);
        params.put("user_token",Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.getMoneyCash, params, null,	"requestMoneyCash");
    }

    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
             if(tag.equals("requestMoneyCash")){
                 closeDialog();
                 processMoneyCash(obj);
             }
    }

    /**
     * 提现业务处理
     * @param json
     */
    private  void processMoneyCash(JSONObject json) {
        try {
            JSONObject msg=json.getJSONObject("msg");
            Boolean validateResult=json.getBoolean("validateResult");
            if(validateResult){
                toast(msg.getString("info"));
                finish();
            }
            else{
                toast(msg.getString("info"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
