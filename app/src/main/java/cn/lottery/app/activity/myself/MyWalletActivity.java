package cn.lottery.app.activity.myself;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.app.activity.login.LoginActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseActivity;

/**
 * 我的钱包
 * Created by admin on 2017/6/10.
 */
public class MyWalletActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout layoutBack,moneyDeail,moneyCash,bindAlipay;

    TextView balance,alipayStatus,alipay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_my_wallet);

        initView();

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private  void initView(){
        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        moneyDeail = (LinearLayout) findViewById(R.id.moneyDeail);
        moneyDeail.setOnClickListener(this);

        moneyCash = (LinearLayout) findViewById(R.id.moneyCash);
        moneyCash.setOnClickListener(this);

        bindAlipay= (LinearLayout) findViewById(R.id.bindAlipay);
        bindAlipay.setOnClickListener(this);

        balance= (TextView) findViewById(R.id.balance);

        alipayStatus= (TextView) findViewById(R.id.alipayStatus);

        alipay= (TextView) findViewById(R.id.alipay);
    }

    private void initData(){
        requestFindAccountInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.moneyDeail:
                startActivity(new Intent(MyWalletActivity.this,MyWalletCashDetailsActivity.class));
                break;
            case R.id.moneyCash:
                startActivity(new Intent(MyWalletActivity.this,MyWalletMoneyCashActivity.class));
                break;
            case R.id.bindAlipay:
                if(Config.phone.equals("")||Config.userToken.equals("")) {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                else{
                    startActivity(new Intent(MyWalletActivity.this, BindAlipayOneActivity.class));
                }
                break;
        }
    }


    /**
     * 请求用户信息
     */
    private void requestFindAccountInfo() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.findAccountInfo, params, null,"requestFindAccountInfo");
    }

    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestFindAccountInfo")) {
            closeDialog();
            processFindAccountInfo(obj);
        }
    }

    /**
     * 获取账户业务处理
     * @param json
     */
    private void processFindAccountInfo(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            if(msg.getBoolean("success")) {
                JSONObject user = json.getJSONObject("map");
                balance.setText(user.getString("balance"));
                if(user.getString("alipay").equals("")){

                }
                else{
                    String pay=user.getString("alipay");
                    alipay.setText(pay.replace(pay.substring(3,pay.length()-3),"******"));
                    alipayStatus.setText("更改");
                    alipayStatus.setTextColor(getResources().getColor(R.color.title_bg));
                }
            }
            else{
                toast("获取账户信息出错！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
