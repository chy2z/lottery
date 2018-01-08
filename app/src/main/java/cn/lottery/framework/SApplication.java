package cn.lottery.framework;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PauseOnScrollListener;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.lottery.R;
import cn.lottery.app.activity.login.LoginActivity;
import cn.lottery.app.activity.openim.contact.OpenimContactSystemMessageActivity;
import cn.lottery.app.activity.web.WebPageActivity;
import cn.lottery.framework.openim.OpenIMInitHelper;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.util.L;
import cn.lottery.framework.util.SPUtils;
import cn.lottery.framework.widget.galleryfinal.UILImageLoader;
import cn.lottery.framework.widget.galleryfinal.UILPauseOnScrollListener;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.alibaba.sdk.android.media.MediaService;
import com.alibaba.wxlib.util.SysUtil;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;

/**
 * Android应用使用Multidex突破64K方法数限制
 */
public class SApplication extends MultiDexApplication {
	private static final String TAG = "SApplication";
	private static SApplication mInstance;
	private static Context sContext;
	private ImageLoader mImageLoader;
    private DisplayImageOptions options;
	private RequestQueue mRequestQueue;
	private Map<String, Object> mGlobalData;
	private PushAgent mPushAgent;
	private List<PhotoInfo> mPhotoList;
	private FunctionConfig functionConfig;

	public static Context getContext(){

		return sContext;
	}

	public RequestQueue getRequestQueue() {
		return mRequestQueue;
	}

	public Object getGlobalData(String key) {
		return mGlobalData.get(key);
	}

	public void setGlobalData(String key, Object value) {
		mGlobalData.put(key, value);
	}

	public static SApplication getInstance() {
		return mInstance;
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	public DisplayImageOptions getDisplayImageOptions() {
		return options;
	}

	/**
	 * 若主工程的 targetSdkVersion 为 23 及以上，需要运行时申请 存储权限 （ WRITE_EXTERNAL_STORAGE ），
	 * 否则在 Android 6.0 及以上机型可能出现无法选举宿主的情况。
	 * @return
     */
	public PushAgent getPushAgent() {
		return mPushAgent;
	}

	public List<PhotoInfo> getmPhotoList() {
		return mPhotoList;
	}

	public void setmPhotoList(List<PhotoInfo> mPhotoList) {
		this.mPhotoList = mPhotoList;
	}

	public FunctionConfig getFunctionConfig() {
		return functionConfig;
	}

	public void setFunctionConfig(FunctionConfig functionConfig) {
		this.functionConfig = functionConfig;
	}
	
	/**
	 * 加载缓存图片
	 * @param imageUri
	 * @return
	 */
	public Bitmap getImageFromCache(String imageUri) {		
		try{		
	        Bitmap bitmap = mImageLoader.getMemoryCache().get(imageUri);
	        if (null != bitmap) {
	            return bitmap;
	        } else {	        	
	        	File file = mImageLoader.getDiskCache().get(imageUri);  
	        	if (file.exists()) {  	        	
		            String path =file.getPath();
		            bitmap = BitmapFactory.decodeFile(path);
		            return bitmap;
	        	}
	        }
		}
		catch(Exception e)
		{			
		  
		}		
		return null;
	}

	/**
	 * 设置缓存图片
	 * @param img
	 * @param key
     */
	public void setImageFromCache(ImageView img, String key) {
		String url = SPUtils.get(getBaseContext(), key, "").toString(); // 获取缓存的文件名称

		if (!url.equals("")) {

			Bitmap bmp = getImageFromCache(url); // 读取缓存文件

			if (bmp != null) {
				img.setScaleType(ScaleType.FIT_XY); 
				img.setImageBitmap(bmp);
			}
		}
	}

    /**
	 * 指定选择图片的数量
	 * @param count
	 * @return
     */
	public FunctionConfig getFunctionConfig(int count) {

		return new FunctionConfig.Builder()
				.setEnableCamera(true)
				//.setEnableEdit(true)
				//.setEnableCrop(true)
				//.setEnableRotate(true)
				//.setCropSquare(true)
				.setEnablePreview(true)
				.setMutiSelectMaxSize(count)
				.setSelected(mPhotoList)
				.build();

	}

	/**
	 * 获取裁剪配置
	 * @param count
	 * @return
     */
	public FunctionConfig getCropFunctionConfig(int count) {

		return new FunctionConfig.Builder()
				.setEnableCamera(true)
				.setEnableEdit(true) //开启编辑功能
				.setEnableCrop(true) //开启裁剪功能
		        //.setCropWidth(50)//裁剪宽度
		        //.setCropHeight(50)//裁剪高度
		        .setCropSquare(false)//裁剪正方形
				//.setEnableRotate(true)
				//.setCropSquare(true)
		        .setForceCrop(true)//启动强制裁剪功能,一进入编辑页面就开启图片裁剪，不需要用户手动点击裁剪，此功能只针对单选操作
		        .setForceCropEdit(false)//在开启强制裁剪功能时是否可以对图片进行编辑
				.setEnablePreview(true)
				.setMutiSelectMaxSize(count)
				.setSelected(mPhotoList)
				.build();

		/*

		setMutiSelect(boolean)//配置是否多选
		setMutiSelectMaxSize(int maxSize)//配置多选数量
		setEnableEdit(boolean)//开启编辑功能
		setEnableCrop(boolean)//开启裁剪功能
		setEnableRotate(boolean)//开启选择功能
		setEnableCamera(boolean)//开启相机功能
		setCropWidth(int width)//裁剪宽度
		setCropHeight(int height)//裁剪高度
		setCropSquare(boolean)//裁剪正方形
		setSelected(List)//添加已选列表,只是在列表中默认呗选中不会过滤图片
		setFilter(List list)//添加图片过滤，也就是不在GalleryFinal中显示
		takePhotoFolter(File file)//配置拍照保存目录，不做配置的话默认是/sdcard/DCIM/GalleryFinal/
		setRotateReplaceSource(boolean)//配置选择图片时是否替换原始图片，默认不替换
		setCropReplaceSource(boolean)//配置裁剪图片时是否替换原始图片，默认不替换
		setForceCrop(boolean)//启动强制裁剪功能,一进入编辑页面就开启图片裁剪，不需要用户手动点击裁剪，此功能只针对单选操作
		setForceCropEdit(boolean)//在开启强制裁剪功能时是否可以对图片进行编辑（也就是是否显示旋转图标和拍照图标）
		setEnablePreview(boolean)//是否开启预览功能

		* */

	}

	
	/**
	 * 判断当前应用程序处于前台还是后台
	 */
	public static boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onCreate() {

		super.onCreate();

		mInstance = this;

		mGlobalData = new HashMap<String, Object>();

		mRequestQueue = Volley.newRequestQueue(this);

		mRequestQueue.start();

		initImageLoader();
		initGalleryFinal();
		initUShare();
		initUPush();
		initOpenIM();
	}	

    private void initImageLoader(){
		// 图像加载参数.
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.bg_no_colcor)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.bg_no_colcor)// 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) //缓存保存在内存
				.cacheOnDisk(true)   //设置下载的图片是否缓存在SD卡中
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888)
				.displayer(new FadeInBitmapDisplayer(500))
				.build();


		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY+2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				.threadPoolSize(10)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				//.writeDebugLogs()
				.defaultDisplayImageOptions(options)
				.build();

		ImageLoader.getInstance().init(config);

		mImageLoader = ImageLoader.getInstance();
	}

	private void initGalleryFinal(){
		//===============图片选择器配置=====================

		//配置主题
		ThemeConfig themeConfigme = new ThemeConfig.Builder()
				.setTitleBarBgColor(Color.rgb(0xF4, 0xbc, 0x1a))
				.setTitleBarTextColor(Color.WHITE)
				.setTitleBarIconColor(Color.BLACK)
				.setFabNornalColor(Color.BLACK)
				.setFabPressedColor(Color.BLUE)
				.setCheckNornalColor(Color.WHITE)
				.setCheckSelectedColor(Color.BLACK)
				//.setIconBack(R.mipmap.ic_action_previous_item)
				//.setIconRotate(R.mipmap.ic_action_repeat)
				//.setIconCrop(R.mipmap.ic_action_crop)
				//.setIconCamera(R.mipmap.ic_action_camera)
				.build();



		mPhotoList = new ArrayList<PhotoInfo>();

		//配置功能
		functionConfig = new FunctionConfig.Builder()
				.setEnableCamera(true)
				//.setEnableEdit(true)
				//.setEnableCrop(true)
				//.setEnableRotate(true)
				//.setCropSquare(true)
				.setEnablePreview(true)
				.setMutiSelectMaxSize(9)
				.setSelected(mPhotoList)
				.build();

		//配置imageloader
		cn.finalteam.galleryfinal.ImageLoader imageloader = new UILImageLoader();

		PauseOnScrollListener pauseOnScrollListener =  new UILPauseOnScrollListener(false, true);

		CoreConfig coreConfig = new CoreConfig.Builder(getApplicationContext(), imageloader, themeConfigme)
				.setFunctionConfig(functionConfig)
				.setPauseOnScrollListener(pauseOnScrollListener)
				.setNoAnimcation(false)
				.build();

		GalleryFinal.init(coreConfig);

		//===============图片选择器配置=====================
	}

	private void initUShare(){
		// 友盟社会化分享
		// 微信 appid appsecret
		PlatformConfig.setWeixin("wx9d535c5ebd2e9e91","21965712ec95d5280b56f4447f401065");

		// 友盟社会化分享
		// QQ appid appsecret
		PlatformConfig.setQQZone("101393815", "5f0f2f6fa6fed67d79e9ac183eedbc81");

		// 新浪微博分享
		// sinaweibo appid appsecret
		PlatformConfig.setSinaWeibo("186309238", "3b4b0f07ef91153a89d16a5e5aa8fd49");
	}

	private void initUPush(){

		// 友盟推送
		mPushAgent = PushAgent.getInstance(getBaseContext());

		mPushAgent.setDebugMode(false);

		mPushAgent.setResourcePackageName(R.class.getPackage().getName());

		//注册推送服务，每次调用register方法都会回调该接口
		mPushAgent.register(new IUmengRegisterCallback() {

			@Override
			public void onSuccess(String deviceToken) {
				//注册成功会返回device token
			}

			@Override
			public void onFailure(String s, String s1) {

			}
		});

		/**
		 * 该Handler是在BroadcastReceiver中被调用，故
		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK 推送消息
		 * 启动的Activity，如果设置android:launchMode="singleTask"，那么只能弹出1个，新的将启动后关闭
		 * */
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

			@Override
			public void dealWithCustomAction(Context context, UMessage msg) {

				String str = "";

				Map<String, String> map = new HashMap<String, String>();

				for (Entry<String, String> entry : msg.extra.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					str += key + "," + value + ";";
					map.put(entry.getKey(), entry.getValue());
				}

				//Toast.makeText(context, str, Toast.LENGTH_LONG).show();

				L.d("友盟消息参数:",str);

				String notifycationType=map.get("notifycationType");

				Intent localIntent=null;

				if(notifycationType.equals("applyAddFriend")){  //好友请求
					localIntent = new Intent(SApplication.getContext(), OpenimContactSystemMessageActivity.class);
					localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(localIntent);
				}
				else if(notifycationType.equals("agreeAddFriend")) {  //聊天界面
					localIntent = OpenIMLoginHelper.getInstance().getIMKit().getChattingActivityIntent(map.get("accountId"), OpenIMLoginHelper.APP_KEY);
					localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(localIntent);
				}
				else if(notifycationType.equals("createTribeSuccess")) {
					localIntent = OpenIMLoginHelper.getInstance().getIMKit().getTribeChattingActivityIntent(Long.parseLong(map.get("tribeId")));
					localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(localIntent);
				}
				else if(notifycationType.equals("joinTribeSuccess")) {
					localIntent = OpenIMLoginHelper.getInstance().getIMKit().getTribeChattingActivityIntent(Long.parseLong(map.get("tribeId")));
					localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(localIntent);
				}
				else if(notifycationType.equals("notifyTribePublisher")) {
					//收到别人参加聚会的消息  跳转到 我的聚会界面
					localIntent = new Intent(context, WebPageActivity.class);
					localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					localIntent.putExtra("httpURL", Config.H5_URL_PREFIX+"/kamang/html/home/mypartyList.html");
					context.startActivity(localIntent);
				}
				else if(notifycationType.equals("joinPartTimeSuccess")) {
                    //参加兼职成功   跳转到 我的兼职界面
					localIntent = new Intent(context, WebPageActivity.class);
					localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					localIntent.putExtra("httpURL", Config.H5_URL_PREFIX+"/kamang/html/trace/parttime/mypartTimeList.html");
					context.startActivity(localIntent);
				}
				else if(notifycationType.equals("notifyPartTimePublisher")) {
                    //收到别人参加兼职的消息 跳转到我的兼职界面
					localIntent = new Intent(context, WebPageActivity.class);
					localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					localIntent.putExtra("httpURL", Config.H5_URL_PREFIX+"/kamang/html/trace/parttime/mypartTimeList.html");
					context.startActivity(localIntent);
				}

				//Intent localIntent = OpenIMLoginHelper.getInstance().getIMKit().getChattingActivityIntent("15907488", OpenIMLoginHelper.APP_KEY);
				//Intent localIntent = new Intent(context,LoginActivity.class);
				//Bundle bundle = new Bundle();
				//bundle.putString("httpURL", "");
				//bundle.putString("notify","true");
				//localIntent.putExtras(bundle);
				//localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//context.startActivity(localIntent);
			}
		};

		mPushAgent.setNotificationClickHandler(notificationClickHandler);

		UmengMessageHandler messageHandler = new UmengMessageHandler() {

			/**
			 * 对自定义消息的处理方式，点击或者忽略
			 */
			@Override
			public void dealWithCustomMessage(final Context context,final UMessage msg) {
				new Handler(getMainLooper()).post(new Runnable() {

					@Override
					public void run() {

						// 对自定义消息的处理方式，点击或者忽略
						boolean isClickOrDismissed = true;

						if (isClickOrDismissed) {
							// 统计自定义消息的打开
							UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
						} else {
							// 统计自定义消息的忽略
							UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
						}

						//String str="";

						Map<String, String> map = new HashMap<String, String>();

						for (Entry<String, String> entry : msg.extra.entrySet()) {

							map.put(entry.getKey(), entry.getValue());

							//str+=entry.getKey()+":"+entry.getValue()+",";
						}

						//Toast.makeText(context,map.get("pushWebURL"),Toast.LENGTH_LONG).show();

						//Toast.makeText(context,str,Toast.LENGTH_LONG).show();

						String notificationID=map.get("notificationID");

						if(notificationID==null||notificationID.equals(""))
						{
							notificationID="";
						}

						NotificationManager appMange = (NotificationManager) getSystemService("notification");

						Intent localIntent = new Intent(context,LoginActivity.class);

						Bundle bundle = new Bundle();
						bundle.putString("httpURL", map.get("pushWebURL"));
						bundle.putString("notificationID",notificationID);
						bundle.putString("notify","true");
						localIntent.putExtras(bundle);

						// localIntent.setAction("android.intent.action.MAIN");
						// localIntent.addCategory("android.intent.category.LAUNCHER");
						localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

						// requestCode 设置成不同，加载新的activity
						PendingIntent appDIndent = PendingIntent.getActivity(
								getBaseContext(), Config.notifyid, localIntent,
								PendingIntent.FLAG_UPDATE_CURRENT);

						Notification appNF = new Notification();
						appNF.icon = R.drawable.ic_launcher;
						// appNF.tickerText = "11";
						appNF.defaults = (0x1 | appNF.defaults);
						appNF.flags = Notification.FLAG_AUTO_CANCEL; // Notification.FLAG_ONGOING_EVENT|Notification.FLAG_AUTO_CANCEL;
						RemoteViews myNotificationView = new RemoteViews(
								getPackageName(), R.layout.um_notification_view);

						String pushContent=map.get("pushContent");
						pushContent=pushContent.length()<=36?pushContent:pushContent.subSequence(0,35)+"......";

						myNotificationView.setTextViewText(R.id.notification_title, map.get("pushTitle"));
						myNotificationView.setTextViewText(R.id.notification_text,pushContent);

						appNF.contentView = myNotificationView;

						appNF.contentIntent = appDIndent;

						appMange.notify(Config.notifyid++, appNF);

						if (!isApplicationBroughtToBackground(context)) {

							String httpURL = "https://www.so.com/";

							if (map.get("pushWebURL") != null) {
								httpURL = map.get("pushWebURL");
							}

							//Intent intent = new Intent(context,DialogSelectUMMsgActivity.class);
							//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							//intent.putExtra("httpURL", map.get("pushWebURL"));
							//intent.putExtra("pushTitle", map.get("pushTitle"));
							//intent.putExtra("pushContent",map.get("pushContent"));
							//intent.putExtra("notificationID",notificationID);
							//startActivity(intent);

						}

						Toast.makeText(context, "接收到一个通知",Toast.LENGTH_LONG).show();
					}
				});
			}

			/**
			 * 推送消息（自定义通知样式编号）
			 * 若要使用自定义通知栏样式，请勿重写方法dealWithNotificationMessage()；否则，自定义通知栏样式将失效。
			 */
			@Override
			public Notification getNotification(Context context, UMessage msg) {

				switch (msg.builder_id) {
					case 1:
						NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
						RemoteViews myNotificationView = new RemoteViews(
								context.getPackageName(),
								R.layout.um_notification_view);
						myNotificationView.setTextViewText(R.id.notification_title,
								msg.title);
						myNotificationView.setTextViewText(R.id.notification_text,
								msg.text);
						myNotificationView.setImageViewBitmap(
								R.id.notification_large_icon,
								getLargeIcon(context, msg));

					/*
					 * myNotificationView.setImageViewResource(
					 * R.id.notification_small_icon, getSmallIconId(context,
					 * msg));
					 */

						builder.setContent(myNotificationView);

						Notification mNotification = builder.build();
						// 由于Android
						// v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
						mNotification.contentView = myNotificationView;
						return mNotification;
					default:
						// 默认为0，若填写的builder_id并不存在，也使用默认。
						return super.getNotification(context, msg);
				}
			}
		};

		mPushAgent.setMessageHandler(messageHandler);
	}

    private void initOpenIM(){
		// todo Application.onCreate中，首先执行这部分代码，以下代码固定在此处，不要改动，
		// 这里return是为了退出Application.onCreate！！！
		if(mustRunFirstInsideApplicationOnCreate()){
			//todo 如果在":TCMSSevice"进程中，无需进行openIM和app业务的初始化，以节省内存
			return;
		}


		//初始化云旺SDK
		OpenIMInitHelper.initYWSDK(this);

		//初始化反馈功能(未使用反馈功能的用户无需调用该初始化)

		//OpenIMInitHelper.initFeedBack(this);

		//初始化多媒体SDK，小视频和阅后即焚功能需要使用多媒体SDK
		AlibabaSDK.asyncInit(this, new InitResultCallback() {
			@Override
			public void onSuccess() {
				Log.e(TAG, "-----initTaeSDK----onSuccess()-------" );
				MediaService mediaService = AlibabaSDK.getService(MediaService.class);
				mediaService.enableHttpDNS(); //果用户为了避免域名劫持，可以启用HttpDNS
				mediaService.enableLog();     //在调试时，可以打印日志。正式上线前可以关闭
			}

			@Override
			public void onFailure(int code, String msg) {
				Log.e(TAG, "-------onFailure----msg:" + msg + "  code:" + code);
			}
		});

	}

	private boolean mustRunFirstInsideApplicationOnCreate() {
		//必须的初始化
		SysUtil.setApplication(this);
		sContext = getApplicationContext();
		return SysUtil.isTCMSServiceProcess(sContext);
	}
}
