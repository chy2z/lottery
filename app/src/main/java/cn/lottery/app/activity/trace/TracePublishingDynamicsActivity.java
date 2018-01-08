package cn.lottery.app.activity.trace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

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
import cn.lottery.framework.util.UploadUtils;

/**
 * 发布动态
 * 
 */
public class TracePublishingDynamicsActivity extends BaseActivity implements View.OnClickListener,UploadUtils.OnUploadProcessListener {

	public static final String TAG = "TracePublishingDynamicsActivity";

	private LocationClient mLocationClient = null;

	private BDLocationListener myListener = new MyLocationListener();

	private LinearLayout layoutBack;

	private TextView writeDynamic;

	private EditText comment;

	private ImageView selectFile1,selectFile2,selectFile3,
			selectFile4,selectFile5,selectFile6,selectFile7,
			selectFile8,selectFile9;

	private ImageView delSelectFile1,delSelectFile2,delSelectFile3,delSelectFile4,
			delSelectFile5,delSelectFile6,delSelectFile7,delSelectFile8,delSelectFile9;

	private ImageView[] selectFiles=new ImageView[9];

	private ImageView[] delSelectFiles=new ImageView[9];

	private String[] serverUrls=new String[9];

	private String[] imageUrls=new String[9];

	private int selectImgIndex=1;

	private String imgUrl="";

	private int locationCount=0;

	//上传文件
	Uri uri;
	File scaledFile;
	String picPath = null;
	String uploadUrl = Config.uploadurl;
	String uploadType="dynamic";
	String PhotosImageurl="";
	Long ufileSize;
	int countPhotos=0;
	int uploadProcess=0;
	Boolean isPhotosStartUpload=false;
	UploadUtils uploadUtils;
	List<PhotoInfo> mPhotoList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_km_trace_publish_dynamic);
		initView();
		initData();
		initBaiDuLocation();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mLocationClient!=null) {
			//关闭百度定位
			mLocationClient.stop();
		}
	}

	/**
	 * 暂停时
	 */
	@Override
	protected  void onPause(){
		super.onPause();
	}

	private void initBaiDuLocation(){

		//声明LocationClient类
		mLocationClient = new LocationClient(getApplicationContext());

		//注册监听函数
		mLocationClient.registerLocationListener( myListener );

		LocationClientOption option = new LocationClientOption();

		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

		option.setCoorType("bd09ll");
		//可选，默认gcj02，设置返回的定位结果坐标系

		int span=2000;

		option.setScanSpan(span);
		//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

		option.setIsNeedAddress(true);
		//可选，设置是否需要地址信息，默认不需要

		option.setOpenGps(true);
		//可选，默认false,设置是否使用gps

		option.setLocationNotify(true);
		//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

		option.setIsNeedLocationDescribe(true);
		//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

		option.setIsNeedLocationPoiList(true);
		//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

		option.setIgnoreKillProcess(false);
		//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

		option.SetIgnoreCacheException(false);
		//可选，默认false，设置是否收集CRASH信息，默认收集

		option.setEnableSimulateGps(false);
		//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

		mLocationClient.setLocOption(option);

		mLocationClient.start();
	}

	private void initView(){
		layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(this);

		writeDynamic=(TextView)findViewById(R.id.writeDynamic);
		writeDynamic.setOnClickListener(this);

		comment=(EditText) findViewById(R.id.comment);

		selectFile1 = (ImageView) findViewById(R.id.selectFile1);
		selectFile1.setOnClickListener(this);

		delSelectFile1 = (ImageView) findViewById(R.id.delSelectFile1);
		delSelectFile1.setOnClickListener(this);

		selectFiles[0]=selectFile1;
		delSelectFiles[0]=delSelectFile1;

		selectFile2 = (ImageView) findViewById(R.id.selectFile2);
		selectFile2.setOnClickListener(this);

		delSelectFile2 = (ImageView) findViewById(R.id.delSelectFile2);
		delSelectFile2.setOnClickListener(this);

		selectFiles[1]=selectFile2;
		delSelectFiles[1]=delSelectFile2;

		selectFile3 = (ImageView) findViewById(R.id.selectFile3);
		selectFile3.setOnClickListener(this);

		delSelectFile3 = (ImageView) findViewById(R.id.delSelectFile3);
		delSelectFile3.setOnClickListener(this);

		selectFiles[2]=selectFile3;
		delSelectFiles[2]=delSelectFile3;

		selectFile4 = (ImageView) findViewById(R.id.selectFile4);
		selectFile4.setOnClickListener(this);

		delSelectFile4 = (ImageView) findViewById(R.id.delSelectFile4);
		delSelectFile4.setOnClickListener(this);

		selectFiles[3]=selectFile4;
		delSelectFiles[3]=delSelectFile4;

		selectFile5 = (ImageView) findViewById(R.id.selectFile5);
		selectFile5.setOnClickListener(this);

		delSelectFile5 = (ImageView) findViewById(R.id.delSelectFile5);
		delSelectFile5.setOnClickListener(this);

		selectFiles[4]=selectFile5;
		delSelectFiles[4]=delSelectFile5;

		selectFile6 = (ImageView) findViewById(R.id.selectFile6);
		selectFile6.setOnClickListener(this);

		delSelectFile6 = (ImageView) findViewById(R.id.delSelectFile6);
		delSelectFile6.setOnClickListener(this);

		selectFiles[5]=selectFile6;
		delSelectFiles[5]=delSelectFile6;

		selectFile7 = (ImageView) findViewById(R.id.selectFile7);
		selectFile7.setOnClickListener(this);

		delSelectFile7 = (ImageView) findViewById(R.id.delSelectFile7);
		delSelectFile7.setOnClickListener(this);

		selectFiles[6]=selectFile7;
		delSelectFiles[6]=delSelectFile7;

		selectFile8 = (ImageView) findViewById(R.id.selectFile8);
		selectFile8.setOnClickListener(this);

		delSelectFile8 = (ImageView) findViewById(R.id.delSelectFile8);
		delSelectFile8.setOnClickListener(this);

		selectFiles[7]=selectFile8;
		delSelectFiles[7]=delSelectFile8;

		selectFile9 = (ImageView) findViewById(R.id.selectFile9);
		selectFile9.setOnClickListener(this);

		delSelectFile9 = (ImageView) findViewById(R.id.delSelectFile9);
		delSelectFile9.setOnClickListener(this);

		selectFiles[8]=selectFile9;
		delSelectFiles[8]=delSelectFile9;

		hidden(1,false);
		hidden(2,true);
		hidden(3,true);
		hidden(4,true);
		hidden(5,true);
		hidden(6,true);
		hidden(7,true);
		hidden(8,true);
		hidden(9,true);
	}

	private void initData(){
		uploadUtils = UploadUtils.getInstance();
		uploadUtils.setOnUploadProcessListener(this);
		mPhotoList = new ArrayList<PhotoInfo>();
	}

	private void hidden(int index,boolean selectHide) {

		if(index>selectFiles.length) return ;

		if (selectHide) {
			selectFiles[index - 1].setVisibility(View.INVISIBLE);
			selectFiles[index - 1].setTag(null);
			DisplayImageOptions option = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.selectfile)
					.showImageOnFail(R.drawable.selectfile).cacheInMemory(true)
					.cacheOnDisk(true).considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new SimpleBitmapDisplayer())
					.build();

			app.getImageLoader().displayImage(serverUrls[index-1], selectFiles[index - 1], option);
		}
		else {
			selectFiles[index - 1].setVisibility(View.VISIBLE);
		}

		delSelectFiles[index - 1].setVisibility(View.INVISIBLE);
	}

	private void show(int index) {

		selectFiles[index - 1].setVisibility(View.VISIBLE);

		delSelectFiles[index - 1].setVisibility(View.VISIBLE);

		selectFiles[index - 1].setTag(imageUrls[index-1]);

		DisplayImageOptions option = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.selectfile)
				.showImageOnFail(R.drawable.selectfile).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new SimpleBitmapDisplayer())
				.build();

		app.getImageLoader().displayImage(serverUrls[index-1], selectFiles[index - 1], option);
	}

	private void del(int index){

		for(int i=0;i<serverUrls.length;i++){
			if(i==index-1){
				serverUrls[i]="";
				imageUrls[i]="";
			}

			if(i>index-1) {
				serverUrls[i-1]=serverUrls[i];
				imageUrls[i-1]=imageUrls[i];
				serverUrls[i]="";
				imageUrls[i]="";
			}
		}

		selectImgIndex--;

		hidden(1,true);
		hidden(2,true);
		hidden(3,true);
		hidden(4,true);
		hidden(5,true);
		hidden(6,true);
		hidden(7,true);
		hidden(8,true);
		hidden(9,true);

		if(selectImgIndex==1) hidden(1,false);
		else {
			for (int i = 0; i < serverUrls.length; i++) {
				if (i < selectImgIndex - 1) {
					show(i + 1);
				}

				if (i == selectImgIndex - 1) {
					hidden(selectImgIndex, false);
				}
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case  R.id.layoutBack:
				finish();
				break;
			case R.id.delSelectFile1:
				del(1);
				break;
			case R.id.delSelectFile2:
				del(2);
				break;
			case R.id.delSelectFile3:
				del(3);
				break;
			case R.id.delSelectFile4:
				del(4);
				break;
			case R.id.delSelectFile5:
				del(5);
				break;
			case R.id.delSelectFile6:
				del(6);
				break;
			case R.id.delSelectFile7:
				del(7);
				break;
			case R.id.delSelectFile8:
				del(8);
				break;
			case R.id.delSelectFile9:
				del(9);
				break;
			case R.id.selectFile1:
				selectImgIndex=1;
				selectFile((ImageView) v);
				break;
			case R.id.selectFile2:
				selectImgIndex=2;
				selectFile((ImageView) v);
				break;
			case R.id.selectFile3:
				selectImgIndex=3;
				selectFile((ImageView) v);
				break;
			case R.id.selectFile4:
				selectImgIndex=4;
				selectFile((ImageView) v);
				break;
			case R.id.selectFile5:
				selectImgIndex=5;
				selectFile((ImageView) v);
				break;
			case R.id.selectFile6:
				selectImgIndex=6;
				selectFile((ImageView) v);
				break;
			case R.id.selectFile7:
				selectImgIndex=7;
				selectFile((ImageView) v);
				break;
			case R.id.selectFile8:
				selectImgIndex=8;
				selectFile((ImageView) v);
				break;
			case R.id.selectFile9:
				selectImgIndex=9;
				selectFile((ImageView) v);
				break;
			case R.id.writeDynamic:
                if(comment.getText().toString().trim().length()==0){
					toast("请说点什么吧......");
				}
				else{
					imgUrl="";
					if(selectFile1.getTag()!=null)
					      imgUrl+=selectFile1.getTag().toString()+",";
					if(selectFile2.getTag()!=null)
						imgUrl+=selectFile2.getTag().toString()+",";
					if(selectFile3.getTag()!=null)
						imgUrl+=selectFile3.getTag().toString()+",";
					if(selectFile4.getTag()!=null)
						imgUrl+=selectFile4.getTag().toString()+",";
					if(selectFile5.getTag()!=null)
						imgUrl+=selectFile5.getTag().toString()+",";
					if(selectFile6.getTag()!=null)
						imgUrl+=selectFile6.getTag().toString()+",";
					if(selectFile7.getTag()!=null)
						imgUrl+=selectFile7.getTag().toString()+",";
					if(selectFile8.getTag()!=null)
						imgUrl+=selectFile8.getTag().toString()+",";
					if(selectFile9.getTag()!=null)
						imgUrl+=selectFile9.getTag().toString()+",";

					//toast(imgUrl.substring(0,imgUrl.length()-1));

					requestPublishingDynamic();
				}
				break;
		}
	}

	/**
	 * 选择图片
	 */
	private void selectFile(ImageView v) {
		if (v.getTag() == null)
			GalleryFinal.openGalleryMuti(1001, SApplication.getInstance().getFunctionConfig(), new GalleryFinal.OnHanlderResultCallback() {

						@Override
						public void onHanlderFailure(int requestCode, String errorMsg) {
							toast(errorMsg + "," + requestCode);
						}

						@Override
						public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
							if (resultList != null) {
								if (resultList.size() < 1) {
									toast("请选择至少1张图片上传");
								} else if (resultList.size() > (9 - selectImgIndex + 1)) {
									toast("只能选择" + (9 - selectImgIndex + 1) + "图片上传");
								} else {
									showLoadingDialog("正在上传图片......");
									countPhotos = 0;
									isPhotosStartUpload = false;
									mPhotoList.clear();
									mPhotoList.addAll(resultList);
									new Thread(new MultipleUploadFileThread()).start();
								}
							}
						}

					}
			);
	}


	//-----------------------------文件上传 开始------------------------------

	@Override
	public void uploadFail(String message) {
		countPhotos++;
		isPhotosStartUpload=false;
		closeDialog();
		toast("第"+countPhotos+"图片上传失败");
	}

	@Override
	public void uploadSuccess(String result) {
		try {
			L.d("上传文件返回结果：",result);

			JSONObject json = new JSONObject(result);

			Boolean uploadResult = json.getBoolean("success");

			if(!uploadResult){  toast("上传失败");	return; }

			countPhotos++;

			isPhotosStartUpload=false;

			PhotosImageurl=json.getString("fileUrl");

			final Boolean uploadResults=uploadResult;

			final String url = json.getString("imgServer")+json.getString("fileUrl");

			final String imgLinkURLs = json.getString("fileUrl");

			serverUrls[selectImgIndex-1]=url;

			imageUrls[selectImgIndex-1]=imgLinkURLs;

			show(selectImgIndex);

			selectImgIndex++;

			hidden(selectImgIndex,false);

			if(countPhotos==mPhotoList.size()){
				closeDialog();
			}

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
					//Toast.makeText(TracePublishingDynamicsActivity.this,"正在上传,进度:" + pro + "%", Toast.LENGTH_SHORT).show();
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
						Bitmap bitmap = BitmapFactory.decodeFile(scaledFile.getAbsolutePath());
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

	//-----------------------------文件上传 结束------------------------------

	/**
	 * 获取关注动态信息
	 */
	private void requestPublishingDynamic(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userId",Config.customerID);
		params.put("user_token",Config.userToken);
		params.put("address",Config.gpsAddr);
		params.put("content",comment.getText().toString());
		params.put("imgUrl",imgUrl);
		params.put("lat",Config.gpslat+"");
		params.put("lng",Config.gpslon+"");
		showLoadingDialog();
		post(Config.URL_PREFIX + RequestUrl.getAddDynamic, params, null,"requestPublishingDynamic");
	}


	@Override
	protected void handError(Object tag, String errorInfo) {

	}

	@Override
	protected void handSuccess(JSONObject obj, Object tag) {
		if (tag.equals("requestPublishingDynamic")) {
			closeDialog();
			processPublishingDynamic(obj);
		}
	}

	private void processPublishingDynamic(JSONObject json){
		try {
			JSONObject msg = json.getJSONObject("msg");
			if(msg.getBoolean("success")){
				Intent resultData=new Intent();
				resultData.putExtra("data","myDynamic");
				setResult(2,resultData);
				finish();
			}
			toast(msg.getString("info"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 百度地图定位数据监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onConnectHotSpotMessage(String var1, int var){

		}

		@Override
		public void onReceiveLocation(BDLocation location) {

			//获取定位结果
			StringBuffer sb = new StringBuffer(256);

			sb.append("time : ");
			sb.append(location.getTime());    //获取定位时间

			sb.append("\nerror code : ");
			sb.append(location.getLocType());    //获取类型类型

			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());    //获取纬度信息

			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());    //获取经度信息

			sb.append("\nradius : ");
			sb.append(location.getRadius());    //获取定位精准度

			Config.gpslat=location.getLatitude();

			Config.gpslon=location.getLongitude();

			if (location.getLocType() == BDLocation.TypeGpsLocation){

				// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());    // 单位：公里每小时

				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());    //获取卫星数

				sb.append("\nheight : ");
				sb.append(location.getAltitude());    //获取海拔高度信息，单位米

				sb.append("\ndirection : ");
				sb.append(location.getDirection());    //获取方向信息，单位度

				sb.append("\naddr : ");
				sb.append(location.getAddrStr());    //获取地址信息

				sb.append("\ndescribe : ");
				sb.append("gps定位成功");

				Config.gpsAddr=location.getAddrStr();

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

				// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());    //获取地址信息

				sb.append("\noperationers : ");
				sb.append(location.getOperators());    //获取运营商信息

				sb.append("\ndescribe : ");
				sb.append("网络定位成功");

				Config.gpsAddr=location.getAddrStr();

			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

				// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");

			} else if (location.getLocType() == BDLocation.TypeServerError) {

				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {

				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");

			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {

				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

			}

			sb.append("\nlocationdescribe : ");

			sb.append(location.getLocationDescribe());    //位置语义化信息

			Config.gpslocationDescribe=location.getLocationDescribe();

            /*
            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            */

			//Log.i("BaiduLocationApiDem", sb.toString());

			//toast(sb.toString());

			if(locationCount++>5){
				if(mLocationClient!=null) {
					//关闭百度定位
					mLocationClient.stop();
				}
			}
		}
	}
}

