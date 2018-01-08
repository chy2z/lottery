package cn.lottery.app.activity.myself;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.PopupMenu.OnMenuItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.lottery.R;
import cn.lottery.app.activity.login.ImmediateAuthenticationActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.util.ImageCompress;
import cn.lottery.framework.util.L;
import cn.lottery.framework.util.UploadUtils;

/**
 * 我的信息
 * Created by admin on 2017/6/10.
 */
public class MyDataActivity extends BaseActivity implements View.OnClickListener,UploadUtils.OnUploadProcessListener {

    LinearLayout layoutBack;

    LinearLayout layoutRealName,layoutHead,layoutBackground;

    ImageView imgStatus,imgHead,imgBackground;

    EditText nickName,address,school,desc;

    TextView phone,save,accoutId,gender,birth;

    Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

    boolean isSaveModel=false;

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
    boolean uploadHeadImg=false;
    //==================上传文件=========================


    PopupMenu popupMenu;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_my_data);

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

        layoutRealName = (LinearLayout) findViewById(R.id.layoutRealName);
        layoutRealName.setOnClickListener(this);

        imgStatus = (ImageView) findViewById(R.id.imgStatus);
        imgStatus.setOnClickListener(this);

        layoutHead = (LinearLayout) findViewById(R.id.layoutHead);
        layoutHead.setOnClickListener(this);

        layoutBackground = (LinearLayout) findViewById(R.id.layoutBackground);
        layoutBackground.setOnClickListener(this);

        imgHead = (ImageView) findViewById(R.id.imgHead);

        imgBackground = (ImageView) findViewById(R.id.imgBackground);

        nickName= (EditText) findViewById(R.id.nickName);

        gender= (TextView) findViewById(R.id.gender);
        gender.setOnClickListener(this);


        birth= (TextView) findViewById(R.id.birth);
        birth.setOnClickListener(this);


        phone= (TextView) findViewById(R.id.phone);
        accoutId= (TextView) findViewById(R.id.accoutId);

        address= (EditText) findViewById(R.id.address);
        school= (EditText) findViewById(R.id.school);
        desc= (EditText) findViewById(R.id.desc);

        save= (TextView) findViewById(R.id.save);
        save.setOnClickListener(this);


        popupMenu = new PopupMenu(this, findViewById(R.id.gender));
        menu = popupMenu.getMenu();

        //通过代码添加菜单项
        menu.add(Menu.NONE, Menu.FIRST + 0, 0, "男");
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "女");

        //监听事件
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case Menu.FIRST + 0:
                       gender.setText("男");
                        break;
                    case Menu.FIRST + 1:
                        gender.setText("女");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

    }

    private void initData(){
        if(uploadUtils==null) {
            uploadUtils = UploadUtils.getInstance();
            uploadUtils.setOnUploadProcessListener(this);
            mPhotoList = new ArrayList<PhotoInfo>();
        }
        requestFindUserInfo();
    }

    private void setStauts(){
        layoutHead.setEnabled(isSaveModel);
        nickName.setEnabled(isSaveModel);
        gender.setEnabled(isSaveModel);
        birth.setEnabled(isSaveModel);
        address.setEnabled(isSaveModel);
        school.setEnabled(isSaveModel);
        desc.setEnabled(isSaveModel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.gender:
                popupMenu.show();
                break;
            case  R.id.birth:
                DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                        dateAndTime.set(Calendar.YEAR, year);
                        dateAndTime.set(Calendar.MONTH, monthOfYear);
                        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        DateFormat fmtDate = new SimpleDateFormat("yyyy-MM-dd");
                        birth.setText(fmtDate.format(dateAndTime.getTime()));
                    }
                };
                DatePickerDialog dateDlg = new DatePickerDialog(MyDataActivity.this, AlertDialog.THEME_HOLO_LIGHT,
                        d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH));
                dateDlg.show();
                break;
            case R.id.save:
                if(isSaveModel) {
                    if (gender.getText().equals("男") || gender.getText().equals("女")) {
                        requestUpdateUserData();
                    } else {
                        toast("性别不正确");
                    }
                }
                else{
                    isSaveModel=true;
                    save.setText("保存");
                    setStauts();
                }

                break;
            case  R.id.layoutBackground:
                uploadHeadImg=false;
                upload();
                break;
            case R.id.layoutHead:
                uploadHeadImg=true;
                upload();
                break;
            case R.id.layoutRealName:
                //1：注册用户 2：认证中 3：认证用户 4：认证失败
                if(Config.status.equals("1")){
                    Intent it=new Intent(MyDataActivity.this, ImmediateAuthenticationActivity.class);
                    it.putExtra("type","1");
                    startActivity(it);
                }
                else  if(Config.status.equals("2")){
                    Intent it=new Intent(MyDataActivity.this, ImmediateAuthenticationActivity.class);
                    it.putExtra("type","2");
                    startActivity(it);
                }
                else if(Config.status.equals("3")){
                    Intent it=new Intent(MyDataActivity.this, ImmediateAuthenticationActivity.class);
                    it.putExtra("type","3");
                    startActivity(it);
                }
                else{
                    Intent it=new Intent(MyDataActivity.this, ImmediateAuthenticationActivity.class);
                    it.putExtra("type","4");
                    startActivity(it);
                }
                break;
        }
    }

    //==============================Http请求====================

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
     * 更新用户头像
     */
    private void  requestUpdateUserHeadImg(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accountId",Config.IMUserId);
        params.put("img",imgPath);
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        post(Config.URL_PREFIX + RequestUrl.updateHeadImg, params, null,"requestUpdateUserHeadImg");
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
     * 更新用户数据
     */
    private void requestUpdateUserData(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accountId",Config.IMUserId);
        params.put("address",address.getText().toString());
        params.put("birthday",birth.getText().toString());
        params.put("desc",desc.getText().toString());
        params.put("gender",gender.getText().toString());
        params.put("nickName",nickName.getText().toString());
        params.put("school",school.getText().toString());
        params.put("img",Config.icon);
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.updateUserData, params, null,"requestUpdateUserData");
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
        else if (tag.equals("requestUpdateUserHeadImg")) {
            closeDialog();
            processUpdateUserHeadImg(obj);
        }
        else if(tag.equals("requestUpdateUserData")){
            closeDialog();
            processUpdateUserData(obj);
        }
        else if(tag.equals("requestUpdateBackImg")){
            closeDialog();
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
     * 获取用户业务处理
     * @param json
     */
    private void processFindUserInfo(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            if(msg.getBoolean("success")) {

                JSONObject user = json.getJSONObject("user");

                nickName.setText(user.getString("nickName"));

                birth.setText(user.getString("birthday"));

                phone.setText(user.getString("phone"));

                address.setText(user.getString("address"));

                school.setText(user.getString("school"));

                desc.setText(user.getString("desc"));

                gender.setText(user.getString("gender"));

                accoutId.setText(user.getString("accountId"));

                Config.status=user.getString("status");

                Config.icon=user.getString("img");

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
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .displayer(new SimpleBitmapDisplayer())
                        .build();

                app.getImageLoader().displayImage(Config.icon, imgHead, option);


                option = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.headbg)
                        .showImageOnFail(R.drawable.headbg).cacheInMemory(true)
                        .cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .displayer(new SimpleBitmapDisplayer())
                        .build();

                app.getImageLoader().displayImage(Config.backgroundImg,imgBackground, option);

                setStauts();
            }
            else{
                toast("获取用户出错！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新头像业务处理
     * @param json
     */
    private void processUpdateUserHeadImg(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            if(msg.getBoolean("success")) {
                DisplayImageOptions option = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.login_head)
                        .showImageOnFail(R.drawable.login_head).cacheInMemory(true)
                        .cacheOnDisk(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .displayer(new SimpleBitmapDisplayer())
                        .build();
                app.getImageLoader().displayImage(Config.icon, imgHead, option);
                toast("上传头像成功！");
            }
            else{
                toast("上传头像出错！");
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
                app.getImageLoader().displayImage(Config.backgroundImg,imgBackground, option);
                toast("上传背景成功！");
            }
            else{
                toast("上传背景出错！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新用户数据
     */
    private void processUpdateUserData(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            if(msg.getBoolean("success")) {
                isSaveModel=false;
                save.setText("编辑");
                setStauts();
                toast("个人资料已保存！");
            }
            else{
                toast("个人资料提交出错！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 文件上传业务
     */
    private  void upload(){
        GalleryFinal.openGalleryMuti(1001, SApplication.getInstance().getFunctionConfig(), new GalleryFinal.OnHanlderResultCallback() {

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
                                showLoadingDialog("正在上传图片......");
                                countPhotos = 0;
                                isPhotosStartUpload = false;
                                imgPath="";
                                mPhotoList.clear();
                                mPhotoList.addAll(resultList);
                                new Thread(new UploadFileThread()).start();
                            }
                        }
                    }

                }
        );
    }


    //====================================文件上传===========================

    /**
     * 文件上传
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

            //更新用户头像
            if(uploadHeadImg) {
                Config.icon = json.getString("imgServer") + json.getString("fileUrl");
                imgPath = json.getString("fileUrl");
                requestUpdateUserHeadImg();
            }
            else{ //更新背景
                Config.backgroundImg = json.getString("imgServer") + json.getString("fileUrl");
                imgPath = json.getString("fileUrl");
                requestUpdateBackImg();
            }

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
}
