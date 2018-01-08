package cn.lottery.app.activity.myself;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.lottery.R;
import cn.lottery.app.activity.HomePageActivity;
import cn.lottery.app.activity.SettingActivity;
import cn.lottery.app.activity.login.ImmediateAuthenticationActivity;
import cn.lottery.app.activity.login.LoginActivity;
import cn.lottery.app.activity.trace.TraceActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.util.DensityUtils;
import cn.lottery.framework.util.ImageCompress;
import cn.lottery.framework.util.L;
import cn.lottery.framework.util.ScreenUtils;
import cn.lottery.framework.util.UploadUtils;
import cn.lottery.framework.widget.RoundedBitmapDisplayer.MyRoundedBitmapDisplayer;

/**
 * 我的
 * Created by admin on 2017/6/9.
 */
public class MyActivity extends BaseActivity  implements View.OnClickListener,UploadUtils.OnUploadProcessListener{

    private LinearLayout layoutMy, layoutTrace, layoutHomepage,layOutBg;

    private LinearLayout myData,myWallet,mySetting;

    private TextView nickName,desc;

    private ImageView imgHead,imgStatus,imgHeadBg;


    //==================上传文件=========================
    Uri uri;
    File scaledFile;
    String picPath = null;
    String uploadurl =Config.uploadurl;
    Long ufileSize;
    int countPhotos=0;
    int uploadProcess=0;
    Boolean isPhotosStartUpload=false;
    UploadUtils uploadUtils;
    List<PhotoInfo> mPhotoList;
    String imgPath="";
    //==================上传文件=========================


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_my);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView(){

        layoutMy = (LinearLayout) findViewById(R.id.layoutMy);
        layoutMy.setOnClickListener(this);

        layoutTrace = (LinearLayout) findViewById(R.id.layoutTrace);
        layoutTrace.setOnClickListener(this);

        layoutHomepage = (LinearLayout) findViewById(R.id.layoutHomepage);
        layoutHomepage.setOnClickListener(this);

        layOutBg= (LinearLayout) findViewById(R.id.layOutBg);
        layOutBg.setOnClickListener(this);

        myData = (LinearLayout) findViewById(R.id.myData);
        myData.setOnClickListener(this);

        myWallet = (LinearLayout) findViewById(R.id.myWallet);
        myWallet.setOnClickListener(this);

        mySetting = (LinearLayout) findViewById(R.id.mySetting);
        mySetting.setOnClickListener(this);

        nickName= (TextView) findViewById(R.id.nickName);
        desc= (TextView) findViewById(R.id.desc);

        imgHead= (ImageView) findViewById(R.id.imgHead);
        imgHead.setOnClickListener(this);

        imgStatus= (ImageView) findViewById(R.id.imgStatus);
        imgStatus.setOnClickListener(this);

        imgHeadBg= (ImageView) findViewById(R.id.imgHeadBg);
    }

    private void initData(){
        if(uploadUtils==null) {
            uploadUtils = UploadUtils.getInstance();
            uploadUtils.setOnUploadProcessListener(this);
            mPhotoList = new ArrayList<PhotoInfo>();
        }

        if(!Config.userToken.equals("")){
            requestFindUserInfo();
        }
        else{
            nickName.setText("未登录");
            desc.setText("");
            DisplayImageOptions option = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.login_head)
                    .showImageOnFail(R.drawable.login_head).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .displayer(new MyRoundedBitmapDisplayer(0))
                    .build();
            app.getImageLoader().displayImage(Config.icon, imgHead, option);

            option = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.headbg)
                    .showImageOnFail(R.drawable.headbg).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new SimpleBitmapDisplayer())
                    .build();
            app.getImageLoader().displayImage(Config.backgroundImg, imgHeadBg, option);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==2)
        {
            //登录成功加载用户信息
            requestFindUserInfo();
        }
        else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layOutBg:
                if(Config.userToken.equals("")){
                    startActivityForResult(new Intent(this, LoginActivity.class),1);
                }
                else {
                    upload();
                }
                break;
            case R.id.myData:
                if(Config.userToken.equals("")){
                    startActivityForResult(new Intent(this, LoginActivity.class),1);
                }
                else {
                    startActivity(new Intent(this, MyDataActivity.class));
                }
                break;
            case R.id.myWallet:
                if(Config.userToken.equals("")){
                    startActivityForResult(new Intent(this, LoginActivity.class),1);
                }
                else {
                    startActivity(new Intent(this, MyWalletActivity.class));
                }
                break;
            case R.id.mySetting:
                startActivity(new Intent(this,SettingActivity.class));
                break;
            case R.id.layoutHomepage:
                startActivity(new Intent(this,HomePageActivity.class));
                break;
            case R.id.layoutTrace:
                startActivity(new Intent(this, TraceActivity.class));
                break;
            case R.id.imgStatus:
                //1：注册用户 2：认证中 3：认证用户 4：认证失败
                if(Config.status.equals("1")){
                    Intent it=new Intent(MyActivity.this, ImmediateAuthenticationActivity.class);
                    it.putExtra("type","1");
                    startActivity(it);
                }
                else  if(Config.status.equals("2")){
                    Intent it=new Intent(MyActivity.this, ImmediateAuthenticationActivity.class);
                    it.putExtra("type","2");
                    startActivity(it);
                }
                else if(Config.status.equals("3")){
                    Intent it=new Intent(MyActivity.this, ImmediateAuthenticationActivity.class);
                    it.putExtra("type","3");
                    startActivity(it);
                }
                else{
                    Intent it=new Intent(MyActivity.this, ImmediateAuthenticationActivity.class);
                    it.putExtra("type","4");
                    startActivity(it);
                }
                break;
            case R.id.imgHead:
                if(Config.userToken.equals("")){
                    startActivityForResult(new Intent(this, LoginActivity.class),1);
                }
                else {
                    startActivity(new Intent(this, MyDataActivity.class));
                }
                break;
        }
    }


    /**
     * 请求用户信息
     */
    private void requestFindUserInfo() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.findUserInfo, params, null,"requestFindUserInfo");
    }


    /**
     * 更新背景
     */
    private void  requestUpdateBackImg(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("imgUrl",imgPath);
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        post(Config.URL_PREFIX + RequestUrl.updateBackImg, params, null,"requestUpdateBackImg");
    }

    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestFindUserInfo")) {
            closeDialog();
            processFindUserInfo(obj);
        }
        else if(tag.equals("requestUpdateBackImg")){
            processUpdateBackImg(obj);
        }
    }


    /**
     * 请求出错
     */
    @Override
    protected void handError(Object tag, String errorInfo) {

    }

    /**
     * 获取用户信息
     * @param json
     */
    private void processFindUserInfo(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            if(msg.getBoolean("success")) {

                JSONObject user = json.getJSONObject("user");

                nickName.setText(user.getString("nickName"));

                desc.setText(user.getString("desc"));

                Config.nickname=user.getString("nickName");

                Config.icon=user.getString("img");

                Config.status=user.getString("status");

                Config.backgroundImg=user.getString("backgroundImg");

                //1：注册用户 2：认证中 3：认证用户 4：认证失败
                if(Config.status.equals("1")) {
                    imgStatus.setImageResource(R.drawable.sm_wrz);
                }
                else if(Config.status.equals("2")) {
                    imgStatus.setImageResource(R.drawable.sm_rzz);
                }
                else if(Config.status.equals("3")) {
                    imgStatus.setImageResource(R.drawable.sm_yrz);
                }
                else{
                    imgStatus.setImageResource(R.drawable.sm_rzsb);
                }

                DisplayImageOptions option = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.login_head)
                        .showImageOnFail(R.drawable.login_head).cacheInMemory(true)
                        .cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.ARGB_8888)
                        .displayer(new MyRoundedBitmapDisplayer(0))
                        .build();

                app.getImageLoader().displayImage(Config.icon, imgHead, option);


                option = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.headbg)
                        .showImageOnFail(R.drawable.headbg).cacheInMemory(true)
                        .cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .displayer(new SimpleBitmapDisplayer())
                        .build();

                app.getImageLoader().displayImage(Config.backgroundImg, imgHeadBg, option);
            }
            else{
                toast("获取用户信息出错！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新背景业务处理
     * @param json
     */
    private void processUpdateBackImg(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            if(msg.getBoolean("success")) {

                DisplayImageOptions option = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.headbg)
                        .showImageOnFail(R.drawable.headbg).cacheInMemory(true)
                        .cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .displayer(new SimpleBitmapDisplayer())
                        .build();

                app.getImageLoader().displayImage(Config.backgroundImg,imgHeadBg, option);

                //L.d("backgroundImg：",Config.backgroundImg);

                toast("更新背景成功！");
            }
            else{
                toast("更新背景出错！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //====================================文件上传===========================

    /**
     * GalleryFinal文件上传
     * @author chy
     *
     */
    public class UploadFileThread implements Runnable {
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
                        Bitmap bitmap = BitmapFactory.decodeFile(scaledFile.getAbsolutePath());
                        float oldSize = (float) new File(uri.getPath()).length() / 1024 / 1024; // 以文件的形式
                        float newSize = (float) scaledFile.length() / 1024;
                        String mCurrentPhotoPath = uri.getPath();
                        L.d("图片路径:" + "\n" + mCurrentPhotoPath + "\n" + "文件大小:" + oldSize + "M\n" + "压缩后的大小:"
                                + newSize + "KB" + "宽度：" + bitmap.getWidth() + "高度：" + bitmap.getHeight());

                        if (bitmap!=null&&!bitmap.isRecycled()) {
                            bitmap.recycle();
                        }

                        L.d("服务器地址：",uploadurl);

                        ufileSize = 0L;
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("scale", "");
                        params.put("type", "head");
                        uploadUtils.uploadFile(scaledFile.getPath(), "uploadFile", uploadurl, params);
                    }

                    Thread.sleep(500);
                }
                catch (Exception e) {
                    L.d("上传图片错误",e.getMessage());
                }
            }
        }
    }



    /**
     * Corp文件上传
     * @author chy
     *
     */
    public class UploadFileCropThread implements Runnable {
        @Override
        public void run() {
            int count=mPhotoList.size();
            while (countPhotos<count) {
                try {
                    if(!isPhotosStartUpload){
                        isPhotosStartUpload=true;
                        //picPath =mPhotoList.get(countPhotos).getPhotoPath();
                        //File imageFile = new File(picPath);
                        //uri  = Uri.fromFile(imageFile);
                        //scaledFile = ImageCompress.scal(uri);

                        File scaledFile = new File(uri.getPath());

                        Bitmap bitmap = BitmapFactory.decodeFile(scaledFile.getAbsolutePath());
                        float oldSize = (float) new File(uri.getPath()).length() / 1024 / 1024; // 以文件的形式
                        float newSize = (float) scaledFile.length() / 1024;
                        String mCurrentPhotoPath = uri.getPath();
                        L.d("图片路径:" + "\n" + mCurrentPhotoPath + "\n" + "文件大小:" + oldSize + "M\n" + "压缩后的大小:"
                                + newSize + "KB" + "宽度：" + bitmap.getWidth() + "高度：" + bitmap.getHeight());

                        if (bitmap!=null&&!bitmap.isRecycled()) {
                            bitmap.recycle();
                        }

                        L.d("服务器地址：",uploadurl);

                        ufileSize = 0L;
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("scale", "");
                        params.put("type", "head");
                        uploadUtils.uploadFile(scaledFile.getPath(), "uploadFile", uploadurl, params);
                    }

                    Thread.sleep(500);
                }
                catch (Exception e) {
                    L.d("上传图片错误",e.getMessage());
                }
            }
        }
    }


    /**
     * 文件上传失败
     * @param message
     */
    @Override
    public void uploadFail(String message) {

        countPhotos++;

        isPhotosStartUpload=false;

        toast("图片上传失败");

        closeDialog();
    }

    /**
     * 文件上传成功
     * @param result
     */
    @Override
    public void uploadSuccess(String result) {

        Boolean uploadResult=false;

        try {
            L.d("上传文件返回结果：",result);

            JSONObject json = new JSONObject(result);

            uploadResult = json.getBoolean("success");

            if(!uploadResult){  toast("上传失败");	 return; }

            countPhotos++;

            isPhotosStartUpload=false;

            Config.backgroundImg = json.getString("imgServer") + json.getString("fileUrl");

            imgPath = json.getString("fileUrl");

            requestUpdateBackImg();

        } catch (JSONException e) {
            toast("上传错误");
            e.printStackTrace();
        }
    }

    /**
     * 文件上传进度
     * @param uploadSize
     */
    @Override
    public void uploadProcess(int uploadSize) {
        if (ufileSize != 0) {
            int pro = (int) ((uploadSize / (float) ufileSize) * 100);
            if(uploadProcess!=pro){
                uploadProcess=pro;
                if (pro== 100) {
                    //toast("正在上传第"+(countPhotos+1)+"张图片,进度:" + pro + "%");
                }
            }
        }
    }

    /**
     * 文件上传初始化
     * @param fileSize
     */
    @Override
    public void uploadInit(long fileSize) {
        ufileSize = fileSize;
        uploadProcess=0;
    }

    /**
     * 文件上传业务
     */
    private  void upload(){

        GalleryFinal.openGalleryMuti(1001, SApplication.getInstance().getFunctionConfig(1), new GalleryFinal.OnHanlderResultCallback() {

                    @Override
                    public void onHanlderFailure(int requestCode, String errorMsg) {
                        toast(errorMsg + "," + requestCode);
                    }

                    @Override
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                        if (resultList != null) {
                            if (resultList.size() != 1) {
                                toast("请选择1张图片上传");
                            } else {

                                countPhotos = 0;
                                isPhotosStartUpload = false;
                                imgPath="";
                                mPhotoList.clear();
                                mPhotoList.addAll(resultList);


                                picPath =mPhotoList.get(countPhotos).getPhotoPath();
                                File imageFile = new File(picPath);
                                uri  = Uri.fromFile(imageFile);
                                //String mCurrentPhotoPath = uri.getPath();

                                /*
                                // 有bug选择截图会闪退
                                GalleryFinal.openCrop(1002,picPath,new GalleryFinal.OnHanlderResultCallback() {
                                    @Override
                                    public void onHanlderFailure(int requestCode, String errorMsg) {
                                        toast(errorMsg + "," + requestCode);
                                    }

                                    @Override
                                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                                        showLoadingDialog("正在上传图片......");
                                        countPhotos = 0;
                                        isPhotosStartUpload = false;
                                        imgPath="";
                                        mPhotoList.clear();
                                        mPhotoList.addAll(resultList);
                                        new Thread(new UploadFileThread()).start();
                                    }
                                });
                                */

                                //获取扩展名
                                String prefix=imageFile.getName().substring(imageFile.getName().lastIndexOf(".")+1);

                                //初始化裁剪存储目录
                                Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped."+prefix));

                                //裁剪完成后调用 onActivityResult
                                Crop.of(uri, destination)
                                        .withMaxSize(ScreenUtils.getScreenWidth(MyActivity.this),ScreenUtils.getScreenHeight(MyActivity.this))
                                        .withAspect(ScreenUtils.getScreenWidth(MyActivity.this),DensityUtils.dp2px(MyActivity.this,120))
                                        .start(MyActivity.this);

                            }
                        }
                    }

                }
        );

    }


    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            //toast(Crop.getOutput(result).getPath());
            showLoadingDialog("正在上传图片......");
            countPhotos = 0;
            isPhotosStartUpload = false;
            imgPath="";
            mPhotoList.clear();
            mPhotoList.add(new PhotoInfo());
            uri=Crop.getOutput(result);
            new Thread(new UploadFileCropThread()).start();

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
