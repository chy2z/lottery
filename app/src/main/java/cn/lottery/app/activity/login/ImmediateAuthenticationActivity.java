package cn.lottery.app.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lottery.R;
import cn.lottery.framework.activity.BaseActivity;

/**
 * 立即认证
 * Created by admin on 2017/5/28.
 */
public class ImmediateAuthenticationActivity  extends BaseActivity implements View.OnClickListener {

    LinearLayout layoutBack;
    Button authentication;
    TextView title;
    ImageView img;
    String type="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_immediate_authentication);

        initView();
    }



    private  void initView(){
        authentication = (Button) findViewById(R.id.authentication);
        authentication.setOnClickListener(this);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        title=(TextView) findViewById(R.id.title);
        img=(ImageView) findViewById(R.id.img);

        Intent it = getIntent();
        //如果类型不对，取到的值为null
        ////1：注册用户 2：认证中 3：认证用户 4：认证失败
        type = it.getStringExtra("type");
        if(type.equals("1")){
            title.setText("你还没实名认证");
            img.setImageResource(R.drawable.renzheng);
        }
        else  if(type.equals("2")){
            title.setText("实名认证中,请等待审核通过");
            authentication.setVisibility(View.GONE);
            img.setImageResource(R.drawable.renzheng2);
        }
        else  if(type.equals("4")){
            title.setText("认证失败,请重新认证");
            img.setImageResource(R.drawable.renzheng);
        }
        else  if(type.equals("3")){
            title.setText("你已完成实名认证");
            authentication.setVisibility(View.GONE);
            img.setImageResource(R.drawable.renzheng3);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.authentication:
                startActivity(new Intent(this, CertificationActivity.class));
                finish();
                break;
        }
    }
}
