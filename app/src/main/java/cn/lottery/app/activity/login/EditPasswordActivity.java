package cn.lottery.app.activity.login;

import android.os.Bundle;
import android.view.View;
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
import cn.lottery.framework.util.SecurityUtils;

/**
 * 修改密码
 * Created by admin on 2017/6/1.
 */
public class EditPasswordActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout layoutBack;

    EditText oldPwd,newPwd,newPwdAgain;

    TextView ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_edit_password);

        initView();
    }

    private void initView(){
        oldPwd = (EditText) findViewById(R.id.oldPwd);
        newPwd = (EditText) findViewById(R.id.newPwd);
        newPwdAgain = (EditText) findViewById(R.id.newPwdAgain);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        ok = (TextView) findViewById(R.id.ok);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.ok:
                if(check()) {
                    requestEditPassword();
                }
                break;
            default:
                break;
        }
    }


    private boolean check(){
        String code=oldPwd.getText().toString().trim();
        String code1=newPwd.getText().toString().trim();
        String code2=newPwdAgain.getText().toString().trim();

        if(code.equals("")){
            toast("旧的密码不能为空");
            return false;
        }

        if(code.equals(code1)){
            toast("新的密码不能和旧的密码一样");
            return false;
        }

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
     * 请求修改密码
     */
    private  void requestEditPassword(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("oldPwd", SecurityUtils.MD5(oldPwd.getText().toString().trim()));
        params.put("newPwd", SecurityUtils.MD5(newPwd.getText().toString().trim()));
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.editPassword, params, null,"requestEditPassword");
    }

    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestEditPassword")) {
            closeDialog();
            processRequestEditPassword(obj);
        }
    }

    /**
     * 请求出错
     */
    @Override
    protected void handError(Object tag, String errorInfo) {

    }

    /**
     * 修改业务
     * @param json
     */
    private  void processRequestEditPassword(JSONObject json){
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
