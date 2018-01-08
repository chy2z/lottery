package cn.lottery.app.activity.web;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.mobileim.contact.IYWDBContact;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.tribe.IYWTribeService;
import com.alibaba.mobileim.utility.IMNotificationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.lottery.R;
import cn.lottery.app.activity.SettingActivity;
import cn.lottery.app.activity.login.ImmediateAuthenticationActivity;
import cn.lottery.app.activity.login.LoginActivity;
import cn.lottery.app.activity.trace.TraceDynamicImageActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.util.ImageCompress;
import cn.lottery.framework.util.L;
import cn.lottery.framework.util.UploadUtils;
import cn.lottery.framework.util.alipay.AliPay;

/**
 * web
 * Created by admin on 2017/5/29.
 */
public class WebPageBackActivity extends BaseActivity implements View.OnClickListener,UploadUtils.OnUploadProcessListener,AliPay.onAliPay {

    Handler mHandler = new Handler(Looper.getMainLooper());

    WebView webview;

    TextView webTitle,clickFunction;

    LinearLayout layoutBack;

    String groupId="0";

    String accountId="0";

    String clickFunctionName;

    String tableName;

    String dataId;

    String imgIndex;

    String uploadUrl="";
    String uploadType="";
    String uploadMaxCount="0";
    File scaledFile;
    Uri uri;
    String picPath = null;
    File file = null;
    UploadUtils uploadUtils;
    Long ufileSize;
    int uploadProcess=0;
    Boolean isPhotosStartUpload=false;
    int countPhotos=0;
    String PhotosImageurl="";
    List<PhotoInfo> mPhotoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_webpage);

        initUpload();

        initView();

        initWebView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Config.userToken.equals("")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webview.loadUrl("javascript: notifyWeb('','')");
                }
            });
        }
        else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webview.loadUrl("javascript: notifyWeb('"+Config.userToken+"','"+Config.customerID+"')");
                }
            });
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(webview.canGoBack())
            {
                webview.goBack();
                return true;
            }
            else
            {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始上传组件
     */
    private void initUpload(){
        uploadUtils = UploadUtils.getInstance();
        uploadUtils.setOnUploadProcessListener(this);
        mPhotoList = new ArrayList<PhotoInfo>();
    }

    /**
     * 初始控件
     */
    private void initView(){
        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        webTitle = (TextView) findViewById(R.id.webTitle);

        clickFunction = (TextView) findViewById(R.id.clickFunction);

        clickFunction.setOnClickListener(this);
    }

    /**
     * 初始webview
     */
    private void initWebView(){
        Intent intent = getIntent();
        String httpURL = intent.getStringExtra("httpURL");

        webview = (WebView) findViewById(R.id.webViewPage);
        webview.requestFocus();

        /*

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        webview.setOnKeyListener(new View.OnKeyListener() {        // webview can go back
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
                    webview.goBack();
                    return true;
                }
                return false;
            }
        });

        */


        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // android WebView 加载重定向页面无法后退解决方案
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //toast(url);

                android.util.Log.d("==========web==========",url);

            }

        });

        webview.setWebChromeClient(new MyWebChromeClient());

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setDefaultTextEncodingName("utf-8");

        webview.addJavascriptInterface(getHtmlObject(), "commonAct");

        webview.loadUrl(httpURL);
    }

    /**
     * 注册javascript接口
     * @return
     */
    private Object getHtmlObject(){

        Object insertObj = new Object(){

            @JavascriptInterface
            public String HtmlcallJava(){
                return "Html call Java";
            }

            @JavascriptInterface
            public String HtmlcallJava2(final String param){
                return "Html call Java : " + param;
            }

            @JavascriptInterface
            public void JavacallHtml(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadUrl("javascript: showFromHtml()");
                        toast("clickBtn");
                    }
                });
            }

            @JavascriptInterface
            public void JavacallHtml2(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadUrl("javascript: showFromHtml2('IT-homer blog')");
                        toast("clickBtn2");
                    }
                });
            }


            /**
             * 获取当前客户端类型
             */
            @JavascriptInterface
            public void getCurrentClientType(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadUrl("javascript: receivceClientTypeFromClient('android')");
                    }
                });
            }


            /**
             * 获取用户UserToken
             */
            @JavascriptInterface
            public void  getCurrentUserToken(){
                toast("getCurrentUserToken");
            }

            /**
             * 打开登录
             */
            @JavascriptInterface
            public void  openUserLoginPage() {
                if(Config.userToken.equals("")) {
                    Intent it = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(it);
                }
                else{
                    toast("已经登录");
                }
            }

            /**
             * 打开设置
             */
            @JavascriptInterface
            public void  openSettingPage() {
                Intent it = new Intent(getBaseContext(), SettingActivity.class);
                startActivity(it);
            }

            @JavascriptInterface
            public void initAppPageNavInfo(final String title, final String isHasRightBtn,final String btnDisplayName, final String btnClickFunctionFullName){
                Message msg = new Message();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("title",title);
                bundle.putString("isHasRightBtn",isHasRightBtn.toUpperCase().equals("TRUE")?"TRUE":"FALSE");
                bundle.putString("btnDisplayName",btnDisplayName);
                bundle.putString("btnClickFunctionFullName",btnClickFunctionFullName);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @JavascriptInterface
            public void getUserloginMap(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadUrl("javascript: receiveUserLoginMap('"+Config.userToken+"','"+Config.customerID+"','"+Config.status+"')");
                    }
                });
            }

            @JavascriptInterface
            public void openAppPage(final String type){
                if(type.equals("1")){
                    if(Config.userToken.equals("")) {
                        Intent it = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(it);
                    }
                    else{
                        toast("已经登录");
                    }
                }
                else if(type.equals("2")){
                    //注册用户，进入实名认证
                    Intent it=new Intent(getBaseContext(), ImmediateAuthenticationActivity.class);
                    it.putExtra("type","1");
                    startActivity(it);
                }
                else{
                    toast("类型不对");
                }
            }


            /**
             * 本地相机拍照拍照上传
             */
            @JavascriptInterface
            public void openCamara(final String imgUploadUrl,final String type){
                uploadUrl=imgUploadUrl;
                uploadType=type;
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
            }

            /**
             * 相册选择上传
             * @param imgUploadUrl
             */
            @JavascriptInterface
            public void openLocalPhotos(final String imgUploadUrl,final String type,final String len){
                uploadUrl=imgUploadUrl;
                uploadType=type;
                uploadMaxCount=len;
                Message msg = new Message();
                msg.what = 3;
                handler.sendMessage(msg);
            }



            @JavascriptInterface
            public  void showToastInfo(final String info){
                toast(info);
            }

            @JavascriptInterface
            public  void showLoadingView(){
                Message msg = new Message();
                msg.what = 5;
                handler.sendMessage(msg);
            }

            @JavascriptInterface
            public  void hideLoadingView(){
                Message msg = new Message();
                msg.what = 6;
                handler.sendMessage(msg);
            }

            /**
             * 支付宝支付
             * @param subject
             * @param body
             * @param out_trade_no
             * @param total_amount
             */
            @JavascriptInterface
            public void commitAliPayInfo(final String subject,final String body,final String out_trade_no, final String total_amount,final  String callBackUrl){
                Message msg = new Message();
                msg.what = 7;
                Bundle bundle = new Bundle();
                bundle.putString("subject",subject);
                bundle.putString("body",body);
                bundle.putString("out_trade_no",out_trade_no);
                bundle.putString("total_amount",total_amount);
                bundle.putString("callBackUrl",callBackUrl);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            /**
             * 会话接口
             * @param personId
             */
            @JavascriptInterface
            public void openChatWithPersonId(String personId){
                if(Config.userToken.equals("")) {
                    Intent it = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(it);
                } else {
                    Intent intent = OpenIMLoginHelper.getInstance().getIMKit().getChattingActivityIntent(personId, OpenIMLoginHelper.APP_KEY);
                    startActivity(intent);
                }
            }

            /**
             * 图片展示
             */
            @JavascriptInterface
            public void openKMPhotosLibrary(String tableName,String dataId ,String imgIndex){
                Message msg = new Message();
                msg.what = 8;
                Bundle bundle = new Bundle();
                bundle.putString("tableName",tableName);
                bundle.putString("dataId",dataId);
                bundle.putString("imgIndex",imgIndex);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            /**
             * 网页返回
             * @param param
             */
            @JavascriptInterface
            public void receiveBackRedirectURL(final String param){
                Message msg = new Message();
                msg.what = 10;
                Bundle bundle = new Bundle();
                bundle.putString("param",param);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            /**
             * 加群
             * @param groupid
             */
            @JavascriptInterface
            public void registerJoinTribe(final String groupid){
                Message msg = new Message();
                msg.what = 11;
                Bundle bundle = new Bundle();
                bundle.putString("groupid",groupid);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @JavascriptInterface
            public void addNewFriend(final String accountid){
                Message msg = new Message();
                msg.what = 12;
                Bundle bundle = new Bundle();
                bundle.putString("accountid",accountid);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

        };

        return insertObj;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:

                if(webview.canGoBack())
                {
                    webview.goBack();
                }
                else
                {
                    finish();
                }

                break;
            case R.id.clickFunction:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadUrl("javascript: "+clickFunctionName+"()");
                    }
                });
                break;
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {  //获取登录用户信息
                String title=  msg.getData().getString("title");
                String isHasRightBtn=  msg.getData().getString("isHasRightBtn");
                String btnDisplayName=  msg.getData().getString("btnDisplayName");
                String btnClickFunctionFullName=  msg.getData().getString("btnClickFunctionFullName");
                webTitle.setText(title);
                if(isHasRightBtn.toUpperCase().equals("TRUE"))
                    clickFunction.setVisibility(View.VISIBLE);
                else
                    clickFunction.setVisibility(View.GONE);

                clickFunction.setText(btnDisplayName);

                clickFunctionName=btnClickFunctionFullName;
            }
            else if(msg.what == 2){  //打开相机
                GalleryFinal.openCamera(1000, SApplication.getInstance().getFunctionConfig(1), new GalleryFinal.OnHanlderResultCallback() {

                            @Override
                            public void onHanlderFailure(int requestCode, String errorMsg) {
                                toast(errorMsg+","+requestCode);
                            }

                            @Override
                            public void onHanlderSuccess(int reqeustCode,List<PhotoInfo> resultList) {
                                if (resultList != null) {
                                    if (resultList.size() >= 1&&resultList.size()<=9) {
                                        countPhotos = 0;
                                        PhotosImageurl = "";
                                        isPhotosStartUpload=false;
                                        mPhotoList.clear();
                                        mPhotoList.addAll(resultList);
                                        new Thread(new MultipleUploadFileThread()).start();
                                    } else {
                                        toast("请选择最多9张图片上传");
                                    }
                                }
                            }
                        }
                );
            }
            else if(msg.what == 3){  //打开相册
                GalleryFinal.openGalleryMuti(1001, SApplication.getInstance().getFunctionConfig(Integer.parseInt(uploadMaxCount)),new GalleryFinal.OnHanlderResultCallback() {

                            @Override
                            public void onHanlderFailure(int requestCode, String errorMsg) {
                                toast(errorMsg+","+requestCode);
                            }


                            @Override
                            public void onHanlderSuccess(int reqeustCode,List<PhotoInfo> resultList) {
                                if (resultList != null) {
                                    if (resultList.size() >= 1&&resultList.size()<=9) {
                                        countPhotos = 0;
                                        PhotosImageurl = "";
                                        isPhotosStartUpload=false;
                                        mPhotoList.clear();
                                        mPhotoList.addAll(resultList);
                                        new Thread(new MultipleUploadFileThread()).start();
                                    } else {
                                        toast("请选择最多9张图片上传");
                                    }
                                }
                            }

                        }
                );
            }
            else  if(msg.what == 5){ //打开加载数据对话框
                     showLoadingDialog("正在加载数据......");
            }
            else  if(msg.what == 6){ //关闭加载数据对话框
                     closeDialog();
            }
            else if(msg.what==7){
                toast("正在打开支付宝");
                AliPay.setOnAliPay(WebPageBackActivity.this);
                AliPay.pay(msg.getData().getString("subject"),
                        msg.getData().getString("body"),
                        msg.getData().getString("total_amount"),
                        msg.getData().getString("out_trade_no"),
                        msg.getData().getString("callBackUrl"),
                        WebPageBackActivity.this);
            }
            else if(msg.what==8){
                 tableName=  msg.getData().getString("tableName");
                 dataId=  msg.getData().getString("dataId");
                 imgIndex=  msg.getData().getString("imgIndex");
                 requestImages();
            }
            else if(msg.what==10){
                String param= msg.getData().getString("param");
                if(param.equals("")||param.equals("undefined")) //关闭
                {
                    finish();
                }
                else if(param.equals("default"))  //后退
                {
                    if (webview.canGoBack()) {
                            webview.goBack();
                    }
                }
                else{  //加载页面
                    webview.loadUrl(param);
                }
            }
            else if(msg.what==11){
                groupId= msg.getData().getString("groupid");
                //toast("群号："+groupId);
                long mTribeId=0;
                try {
                    mTribeId = Long.valueOf(groupId);
                } catch (Exception e) {
                    toast("群号转换出错");
                }
                YWIMKit mIMKit =OpenIMLoginHelper.getInstance().getIMKit();
                IYWTribeService mTribeService = mIMKit.getTribeService();
                YWTribe mTribe = mTribeService.getTribe(mTribeId);
                if (mTribe == null || mTribe.getTribeRole() == null) {
                    mTribeService.getTribeFromServer(new IWxCallback() {
                        @Override
                        public void onSuccess(Object... result) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //没加入过群
                                    operateTribeCallBack(groupId);
                                }
                            });
                        }

                        @Override
                        public void onError(int code, String info) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    webview.loadUrl("javascript: receiveJoinToTribeRes('false','没有搜索到该群，请确认群id是否正确！')");
                                }
                            });
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    }, mTribeId);
                } else {
                    //已经加入群了
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webview.loadUrl("javascript: receiveJoinToTribeRes('false','已经加入过群')");
                        }
                    });
                }
            }
            else if(msg.what==12){
                accountId= msg.getData().getString("accountid");
                if(accountId.equals(Config.IMUserId)){
                    toast("这是你自己");
                }
                else {

                    final AlertDialog myDialog = new AlertDialog.Builder(WebPageBackActivity.this,AlertDialog.THEME_HOLO_LIGHT).create();
                    myDialog.show();
                    myDialog.getWindow().setContentView(R.layout.activity_add_friends_dialog);
                    //解决Android输入法的隐藏
                    myDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                    final EditText edMsg=(EditText)myDialog.getWindow().findViewById(R.id.dialog_msg);
                    edMsg.setText("我是" + Config.nickname);
                    Button butselect= (Button)myDialog.getWindow().findViewById(R.id.butselect);
                    butselect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(edMsg.getText().toString().equals("")){
                                toast("请输入好友验证信息");
                            }
                            else {
                                IYWContactService contactService = OpenIMLoginHelper.getInstance().getIMKit().getContactService();
                                List<IYWDBContact> contactsFromCache =contactService.getContactsFromCache();
                                for(IYWDBContact contact:contactsFromCache){
                                    if(contact.getUserId().equals(accountId)){
                                        toast("好友已经添加");
                                        myDialog.dismiss();
                                        return;
                                    }
                                }

                                contactService.addContact(accountId, OpenIMLoginHelper.APP_KEY, Config.nickname,edMsg.getText().toString(), new IWxCallback() {
                                    @Override
                                    public void onSuccess(Object... result) {
                                        //IMNotificationUtils.getInstance().showToast(WebPageActivity.this, "好友申请已发送成功,  id = " + accountId + ", appkey = " + OpenIMLoginHelper.APP_KEY);
                                        //发送好友申请
                                        operateContactCallBack(Config.IMUserId, accountId, "0");
                                    }

                                    @Override
                                    public void onError(int code, String info) {
                                        IMNotificationUtils.getInstance().showToast(WebPageBackActivity.this, "好友申请失败，code = " + code + ", info = " + info);
                                    }

                                    @Override
                                    public void onProgress(int progress) {

                                    }
                                });

                                myDialog.dismiss();
                            }
                        }
                    });

                    Button butcacel= (Button)myDialog.getWindow().findViewById(R.id.butcancel) ;
                    butcacel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });
                }
            }
        }
    };


    /**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            result.confirm();
            Toast.makeText(WebPageBackActivity.this, message, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    //-----------------------------支付宝-------------------------------------

    private void alipayResult(final boolean result,final String msg,final String code){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webview.loadUrl("javascript: receivcePayResult("+(result?"true":"false")+",'"+code+"')");
            }
        });
    }

    @Override
    public void finshPay(String resultInfo, String resultStatus) {
        app.setGlobalData("canClick", true);
        if (TextUtils.equals(resultStatus, "9000")) {
            Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
            alipayResult(true,"支付成功","9000");
        }
        else {
            // 判断resultStatus 为非“9000”则代表可能支付失败
            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
            if (TextUtils.equals(resultStatus, "8000")) {
                Toast.makeText(this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                //personalFailureCharge(resultStatus, "支付结果确认中");
                alipayResult(false,"支付结果确认中","8000");
            }
            else if (TextUtils.equals(resultStatus, "4000")) {
                Toast.makeText(this, "订单支付失败", Toast.LENGTH_SHORT).show();
                //personalFailureCharge(resultStatus, "订单支付失败");
                alipayResult(false,"订单支付失败","4000");
            }
            else if (TextUtils.equals(resultStatus, "6001")) {
                Toast.makeText(this, "用户中途取消", Toast.LENGTH_SHORT).show();
                //personalFailureCharge(resultStatus, "用户中途取消");
                alipayResult(false,"用户中途取消", "6001");
            }
            else if (TextUtils.equals(resultStatus, "6002")) {
                Toast.makeText(this, "网络连接出错", Toast.LENGTH_SHORT).show();
                //personalFailureCharge(resultStatus, "网络连接出错");
                alipayResult(false,"网络连接出错","6002");
            }
        }
    }


    //-----------------------------文件上传------------------------------

    @Override
    public void uploadFail(String message) {
            countPhotos++;
            isPhotosStartUpload=false;
            toast("第"+countPhotos+"图片上传失败");
    }

    @Override
    public void uploadSuccess(String result) {
        Boolean uploadResult=false;
        String imgLinkURL = "";
        try {
            L.d("上传文件返回结果：",result);

            JSONObject json = new JSONObject(result);

            uploadResult = json.getBoolean("success");

            if(!uploadResult){  toast("上传失败");	 return; }

            countPhotos++;

            isPhotosStartUpload=false;

            PhotosImageurl=json.getString("fileUrl");

            final Boolean uploadResults=uploadResult;

            //final String imgLinkURLs = json.getString("imgServer")+json.getString("fileUrl");

            final String imgLinkURLs = json.getString("fileUrl");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webview.loadUrl("javascript: receivceImgLinkURLFromClient('"
                            + uploadResults.toString() + "','" + imgLinkURLs
                            + "')");
                }
            });

        } catch (JSONException e) {
            toast("上传错误");
            e.printStackTrace();
        }
    }

    @Override
    public void uploadProcess(int uploadSize) {
        if (ufileSize != 0) {
            int pro = (int) ((uploadSize / (float) ufileSize) * 100);
            if(uploadProcess!=pro){
                uploadProcess=pro;
                if (pro==100){
                    //Toast.makeText(WebPageBackActivity.this,"正在上传,进度:" + pro + "%", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(WebPageBackActivity.this,"第"+(countPhotos+1)+"张图片上传成功" , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void uploadInit(long fileSize) {
        ufileSize = fileSize;
        uploadProcess=0;
    }

    /**
     * 上传文件
     * @author chy
     *
     */
    public class MultipleUploadFileThread implements Runnable { // thread
        @Override
        public void run() {
            int count=mPhotoList.size();
            while (countPhotos<count) {
                try {
                    if(!isPhotosStartUpload){
                        isPhotosStartUpload=true;
                        picPath =mPhotoList.get(countPhotos).getPhotoPath();
                        File imageFile = new File(picPath);
                        uri  = Uri.fromFile(imageFile);
                        scaledFile = ImageCompress.scal(uri);
                        Bitmap bitmap =BitmapFactory.decodeFile(scaledFile.getAbsolutePath());
                        float oldSize = (float) new File(uri.getPath()).length() / 1024 / 1024; // 以文件的形式
                        float newSize = (float) scaledFile.length() / 1024;
                        String mCurrentPhotoPath = uri.getPath();
                        L.d("图片路径:" + "\n" + mCurrentPhotoPath + "\n" + "文件大小:" + oldSize + "M\n" + "压缩后的大小:"
                                + newSize + "KB" + "宽度：" + bitmap.getWidth() + "高度：" + bitmap.getHeight());

                        if (bitmap!=null&&!bitmap.isRecycled()) {
                            bitmap.recycle();
                        }

                        ufileSize = 0L;
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("scale", "");
                        params.put("type", uploadType);
                        uploadUtils.uploadFile(scaledFile.getPath(), "uploadFile", uploadUrl, params);
                    }

                    Thread.sleep(500);
                }
                catch (Exception e) {

                }
            }
        }
    }

    //-----------------------------文件上传------------------------------

    //-----------------------------数据请求------------------------------

    /**
     * 获取附近动态信息
     */
    private void requestImages(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("tableName",tableName);
        params.put("id",dataId);
        post(Config.URL_PREFIX + RequestUrl.getImg, params, null,"requestImages");
    }


    /**
     * 群申请成功请求回调
     * @param groupNo
     */
    private void  operateTribeCallBack(String groupNo){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accountId", Config.IMUserId);
        params.put("imGroupNo", groupNo);
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        params.put("type","1");
        post(Config.URL_PREFIX + RequestUrl.operaterGroup, params, null,"operateTribeCallBack");
    }


    /**
     * 好友请求回调
     * @param one
     * @param two
     * @param type
     */
    private void  operateContactCallBack(String one ,String two,String type){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accountIdOne", one);
        params.put("accountIdTwo", two);
        params.put("type", type);
        post(Config.URL_PREFIX + RequestUrl.addFriend, params, null,"operateContactCallBack");
    }

    @Override
    protected void handError(Object tag, String errorInfo) {

    }

    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestImages")) {
            processImages(obj);
        }
        else if (tag.equals("operateTribeCallBack")) {
            processOperateTribeCallBack(obj);
        }
        else  if (tag.equals("operateContactCallBack")) {
            processOperateContactCallBack(obj);
        }
    }


    private  void processImages(JSONObject json) {
        try {
            JSONObject msg = json.getJSONObject("msg");
            JSONArray data = json.getJSONArray("data");
            JSONObject img=null;
            if(msg.getBoolean("success")) {
                //toast("数量" + data.length());
                String imgUrlAll="";
                for (int j=0;j<data.length();j++){
                    if (j < data.length() - 1) {
                        imgUrlAll += data.getJSONObject(j).getString("img")+ ",";
                    } else imgUrlAll += data.getJSONObject(j).getString("img");
                }
                Intent intent = new Intent(WebPageBackActivity.this, TraceDynamicImageActivity.class);
                intent.putExtra("url",imgUrlAll);
                intent.putExtra("imgIndex",imgIndex);
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群申请请求回调业务处理
     * @param json
     */
    private  void processOperateTribeCallBack(JSONObject json){
        try{
            JSONObject msg= json.getJSONObject("msg");

            //IMNotificationUtils.getInstance().showToast(getBaseContext(), msg.getString("info"));

            if(msg.getBoolean("success")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadUrl("javascript: receiveJoinToTribeRes('true','加入群成功')");
                    }
                });
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadUrl("javascript: receiveJoinToTribeRes('false','加入群失败')");
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 好友请求回调业务处理
     * @param json
     */
    private  void processOperateContactCallBack(JSONObject json){
        try{
            JSONObject msg= json.getJSONObject("msg");
            toast(msg.getString("info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
