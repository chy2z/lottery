package cn.lottery.app.activity.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import java.util.Map;
import java.util.Set;

import cn.lottery.R;
import cn.lottery.app.activity.login.LoginActivity;
import cn.lottery.app.activity.openim.OpenimLoginActivity;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.util.L;

public class TestMainActivity extends AppCompatActivity implements View.OnClickListener {

    UMShareAPI mShareAPI = null;

    SHARE_MEDIA platform=null;

    String useToken;

    Button butwx,butqq,butusertoken,butsina,butOpeIM,butlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        useToken = SApplication.getInstance().getPushAgent().getRegistrationId();

        toast(useToken);

        L.d("注册信息",useToken);

        butwx = (Button) findViewById(R.id.butwx);
        butwx.setOnClickListener(this);

        butqq = (Button) findViewById(R.id.butqq);
        butqq.setOnClickListener(this);

        butsina = (Button) findViewById(R.id.butsina);
        butsina.setOnClickListener(this);

        butusertoken = (Button) findViewById(R.id.butusertoken);
        butusertoken.setOnClickListener(this);

        butOpeIM = (Button) findViewById(R.id.butOpeIM);
        butOpeIM.setOnClickListener(this);

        butlogin = (Button) findViewById(R.id.butlogin);
        butlogin.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 友盟社会化分享回调
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 友盟授权
     */
    private void umengDoOauthVerify(SHARE_MEDIA type) {

        mShareAPI = UMShareAPI.get(this);

        //platform = SHARE_MEDIA.WEIXIN;

        platform=type;

        if (SHARE_MEDIA.SINA == type) {

            /*

            UMShareConfig config = new UMShareConfig();
            config.setSinaAuthType(UMShareConfig.AUTH_TYPE_WEBVIEW);
            UMShareAPI.get(InfoDetailActivity.this).setShareConfig(config);
            */


            /*

            UMShareConfig config = new UMShareConfig();
            config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO);
            UMShareAPI.get(InfoDetailActivity.this).setShareConfig(config);


           UMShareConfig config = new UMShareConfig();
            config.isNeedAuthOnGetUserInfo(true);
            UMShareAPI.get(InfoDetailActivity.this).setShareConfig(config);
            * */

            /*
            UMAuthListener umDelAuthListener = new UMAuthListener() {
                @Override
                public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                    Toast.makeText( getApplicationContext(), "deleteOauth succeed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                    Toast.makeText( getApplicationContext(), "deleteOauth fail", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel(SHARE_MEDIA platform, int action) {
                    Toast.makeText( getApplicationContext(), "deleteOauth cancel", Toast.LENGTH_SHORT).show();
                }
            };

            mShareAPI.deleteOauth(this, platform,umDelAuthListener);

            */

            mShareAPI.isAuthorize(this,type);

            toast("配置回调地址");

            com.umeng.socialize.Config.REDIRECT_URL="http://sns.whalecloud.com/sina2/callback";
        }

        UMAuthListener umAuthListener = new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action,Map<String, String> data) {
                //获取用户信息
                umengGetPlatformInfo();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText(getApplicationContext(), "授权失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText(getApplicationContext(), "授权取消",Toast.LENGTH_SHORT).show();
            }
        };

        mShareAPI.doOauthVerify(this, platform, umAuthListener);
    }


    /**
     * 获取用户信息
     */
    private void umengGetPlatformInfo() {

        UMAuthListener umGetPlatformInfoListener = new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action,Map<String, String> data) {

                StringBuilder sb = new StringBuilder();

                Set<String> keys = data.keySet();
                for(String key : keys){
                   sb.append(key+"="+data.get(key).toString()+"\r\n");
                }

                L.d("getPlatformInfoData",sb.toString());

                Toast.makeText(getApplicationContext(),sb.toString(),Toast.LENGTH_SHORT).show();

                //mShareAPI.getPlatformInfo(DialogLoginSelectActivity.this, platform, umAuthListener);

                //Config.wx_unionid=data.get("unionid").toString();
                //Config.wx_nicknames=data.get("nickname").toString();
                //Config.wx_headimgurls=data.get("headimgurl").toString();

                Toast.makeText(getApplicationContext(), "获取用户信息 succeed",Toast.LENGTH_SHORT).show();

                //Toast.makeText(getApplicationContext(), Config.wx_nicknames, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {

                L.d("获取用户信息失败",t.getMessage());

                Toast.makeText(getApplicationContext(), "获取用户信息 fail",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText(getApplicationContext(), "获取用户信息 cancel",
                        Toast.LENGTH_SHORT).show();
            }
        };

        mShareAPI.getPlatformInfo(this, platform, umGetPlatformInfoListener);
    }


    /**
     * 消息提示
     *
     * @param msg
     */
    public void toast(String msg) {
        Toast.makeText(TestMainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case  R.id.butwx:
                 toast("butwx");
                 umengDoOauthVerify(SHARE_MEDIA.WEIXIN);
                 break;
             case R.id.butqq:
                 toast("butqq");
                 umengDoOauthVerify(SHARE_MEDIA.QQ);
                 break;
             case R.id.butsina:
                 toast("butsina");
                 umengDoOauthVerify(SHARE_MEDIA.SINA);
                 break;
             case R.id.butusertoken:
                 toast(SApplication.getInstance().getPushAgent().getRegistrationId());
                 break;
             case R.id.butOpeIM:
                 //Intent intent = new Intent(this,TraceActivity.class);
                 startActivity(new Intent(this,OpenimLoginActivity.class));
                 break;
             case R.id.butlogin:
                 startActivity(new Intent(this,LoginActivity.class));
                 break;
         }
    }
}
