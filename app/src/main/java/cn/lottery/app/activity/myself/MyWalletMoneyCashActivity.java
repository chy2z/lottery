package cn.lottery.app.activity.myself;

import android.content.Intent;
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

import java.math.BigDecimal;
import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.util.MathUtil;

/**
 * 现金提现
 * Created by admin on 2017/6/23.
 */
public class MyWalletMoneyCashActivity  extends BaseActivity implements View.OnClickListener {

    LinearLayout layoutBack;

    TextView alipay,balance,allMoneyCash;

    EditText moneyCash;

    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_my_wallet_money_cash);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);

        alipay = (TextView) findViewById(R.id.alipay);

        balance = (TextView) findViewById(R.id.balance);

        allMoneyCash = (TextView) findViewById(R.id.allMoneyCash);

        allMoneyCash.setOnClickListener(this);

        moneyCash = (EditText) findViewById(R.id.moneyCash);


        moneyCash.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    next.setEnabled(false);
                    next.setBackgroundResource(R.drawable.openim_btn_disable);
                }
                else{
                    next.setEnabled(true);
                    next.setBackgroundResource(R.drawable.btn_yellow);
                }

                judgeNumber(moneyCash.getText());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private  void initData(){
        requestFindAccountInfo();
    }

    /**
     * 金额输入框中的内容限制（最大：小数点前五位，小数点后2位）
     * @param edt
     */
    public void judgeNumber(Editable edt){
        String temp = edt.toString();
        int posDot = temp.indexOf(".");//返回指定字符在此字符串中第一次出现处的索引
        if (posDot <= 0) {//不包含小数点
            if (temp.length() <= 5) {
                return;//小于五位数直接返回
            } else {
                edt.delete(5, 6);//大于五位数就删掉第六位（只会保留五位）
                return;
            }
        }
        if (temp.length() - posDot - 1 > 2)//如果包含小数点
        {
            edt.delete(posDot + 3, posDot + 4);//删除小数点后的第三位
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.allMoneyCash:
                moneyCash.setText(balance.getText().toString());
                break;
            case R.id.next:
                BigDecimal balanced=new BigDecimal(balance.getText().toString());
                BigDecimal cash=new BigDecimal(moneyCash.getText().toString());
                if(cash.compareTo(balanced)<1) {
                    if (balanced.doubleValue() > 0) {
                        Intent it = new Intent(MyWalletMoneyCashActivity.this, MyWalletMoneyCashPwdActivity.class);
                        it.putExtra("moneyCash", MathUtil.format2point(cash.doubleValue()));
                        startActivity(it);
                    } else {
                        toast("提现金额要大于0");
                    }
                }
                else{
                    toast("提现金额不能大于余额");
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
                if(!user.getString("alipay").equals("")) {
                    if(user.getBoolean("payPwd")) {
                        balance.setText(user.getString("balance"));
                        String pay = user.getString("alipay");
                        alipay.setText(pay.replace(pay.substring(3, pay.length() - 3), "******"));
                        Config.balance = user.getString("balance");
                    }
                    else{
                        toast("请绑定支付密码");
                        finish();
                        Intent intent = new Intent(this,MyWalletMoneyForgetPasswordActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    toast("请绑定支付宝帐号");
                    finish();
                    startActivity(new Intent(this,BindAlipayOneActivity.class));
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
