package cn.lottery.app.activity.login;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.lottery.R;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.util.ImageCompress;
import cn.lottery.framework.util.L;
import cn.lottery.framework.util.SPUtils;
import cn.lottery.framework.util.UploadUtils;
import cn.lottery.framework.util.UploadUtils.OnUploadProcessListener;
import cn.lottery.framework.util.Valid;

/**
 * 实名认证
 * Created by admin on 2017/5/28.
 */
public class CertificationActivity  extends BaseActivity implements View.OnClickListener,  OnUploadProcessListener{

    EditText name,identification;
    LinearLayout layoutBack;
    ImageView selectFile;

    //上传文件
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

    String idCardA="";
    String idCardB="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_certification);

        initView();

        initData();
    }

    private  void initView(){
        name = (EditText) findViewById(R.id.name);
        identification = (EditText) findViewById(R.id.identification);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        selectFile = (ImageView) findViewById(R.id.selectFile);
        selectFile.setOnClickListener(this);
    }

    private void initData(){
        uploadUtils = UploadUtils.getInstance();
        uploadUtils.setOnUploadProcessListener(this);
        mPhotoList = new ArrayList<PhotoInfo>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
            case R.id.selectFile:
                if(name.getText().toString().trim().equals("")||identification.getText().toString().trim().equals("")){
                    toast("请填写真实姓名和身份证号后在上传图片");
                    return ;
                }
                if(Valid.isIdcard(identification.getText().toString().trim())) {
                    GalleryFinal.openGalleryMuti(1001, SApplication.getInstance().getFunctionConfig(), new GalleryFinal.OnHanlderResultCallback() {

                                @Override
                                public void onHanlderFailure(int requestCode, String errorMsg) {
                                    toast(errorMsg + "," + requestCode);
                                }


                                @Override
                                public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                                    if (resultList != null) {
                                        if (resultList.size() != 2) {
                                            toast("请选择2张图片上传");
                                        } else {
                                            showLoadingDialog("正在上传图片......");
                                            countPhotos = 0;
                                            isPhotosStartUpload = false;
                                            idCardA = "";
                                            idCardB = "";
                                            mPhotoList.clear();
                                            mPhotoList.addAll(resultList);
                                            new Thread(new MultipleUploadFileThread()).start();
                                        }
                                    }
                                    else
                                    {
                                        toast("请选择2张图片上传");
                                    }
                                }
                            }
                    );
                }
                else{
                    toast("身份证号不正确");
                }
                break;
        }
    }


    //=====================================多文件上传 start=======================

    /**
     * 多文件上传
     * @author chy
     *
     */
    public class MultipleUploadFileThread implements Runnable {
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

                        L.d("服务器地址：",uploadurl);

                        ufileSize = 0L;
                        HashMap<String, String> params = new HashMap<String, String>();
                        //params.put("uploadFileFileName", picPath.substring(picPath.lastIndexOf("/")+1));
                        //params.put("uploadFileContentType",picPath.substring(picPath.lastIndexOf(".")+1));
                        params.put("scale", "");
                        params.put("type", "idcard");
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

        toast("第"+countPhotos+"图片上传失败");

        closeDialog();
    }

    /**
     * 文件上传成功
     * @param result
     */
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

            if(countPhotos==1){
                idCardA=json.getString("fileUrl");
                //idCardA=json.getString("imgServer")+json.getString("fileUrl");
                //toast("第一张身份证图片上传成功");
            }
            else  if(countPhotos==2){
                closeDialog();
                idCardB=json.getString("fileUrl");
                //idCardB=json.getString("imgServer")+json.getString("fileUrl");
                //toast("第二身份证图片上传成功");
                toast("图片上传成功");
                requestValidatePerson(); //进行实名认证
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

    //=====================================多文件上传 end=======================

    //=====================================请求数据 start=======================

    /**
     * 实名认证
     */
    private void requestValidatePerson()
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("aUrl",idCardA);
        params.put("bUrl",idCardB);
        params.put("idCard",identification.getText().toString().trim());
        params.put("name",name.getText().toString().trim());
        params.put("userId",Config.customerID);
        params.put("user_token",Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.validatePerson, params, null,"requestValidatePerson");
    }


    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestValidatePerson")) {
            closeDialog();
            processRequestValidatePerson(obj);
        }
    }

    /**
     * 实名认证结果
     * @param json
     */
    private void processRequestValidatePerson(JSONObject json){
        try {

            JSONObject msg=json.getJSONObject("msg");

            if(msg.getBoolean("success")){

                Config.status=json.getString("status");

                SPUtils.put(getBaseContext(), "status", Config.status);

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

    //=====================================请求数据 end=========================
}
