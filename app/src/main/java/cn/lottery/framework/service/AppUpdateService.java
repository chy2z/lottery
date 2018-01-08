package cn.lottery.framework.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;
import cn.lottery.R;
import cn.lottery.framework.Config;
import cn.lottery.framework.util.L;
import cn.lottery.app.activity.test.TestMainActivity;

/**
 * 下载appservice.
 *
 */
public class AppUpdateService extends Service {
	private static final String TAG = Config.DEBUG_TAG;

	// 标题
	private String titleId = "car";
	private String appName;
	private String appId;
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	// 文件存储
	private File updateDir = null;
	private File updateFile = null;
	// 通知栏
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	// 通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;
	private RemoteViews myNotificationView=null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		L.d("----------------------->");
		if(intent==null) return 0;
		// 获取传值
		titleId = intent.getStringExtra("titleId");
		appName = intent.getStringExtra("appname");
		appId = intent.getStringExtra("downurl");
		// 创建文件
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory(), "qx/app/");
			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			updateFile = new File(updateDir.getPath(), titleId + ".apk");
		}
		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		this.updateNotification = new Notification();

		// 设置下载过程中，点击通知栏，回到主界面
		updateIntent = new Intent(this, TestMainActivity.class);// app现在显示界面.

		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);

		// 设置通知栏显示内容
		updateNotification.icon = R.drawable.ic_launcher;
		updateNotification.flags |= updateNotification.FLAG_AUTO_CANCEL;
		//updateNotification.tickerText = "开始下载";
		//updateNotification.setLatestEventInfo(this, appName, "0%", updatePendingIntent);
		myNotificationView = new RemoteViews(getPackageName(), R.layout.um_notification_view);
		myNotificationView.setTextViewText(R.id.notification_title,	Config.appName);
		myNotificationView.setTextViewText(R.id.notification_text,"开始下载");
		updateNotification.contentView = myNotificationView;
		updateNotification.contentIntent = updatePendingIntent;


		// 发出通知
		updateNotificationManager.notify(0, updateNotification);

		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new updateRunnable()).start();// 这个是下载的重点，是下载的过程
		return super.onStartCommand(intent, flags, startId);
	}

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				updateNotification.flags |= updateNotification.FLAG_AUTO_CANCEL;
				// 点击安装PendingIntent
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				installIntent.setDataAndType(uri, "application/vnd.android.package-archive");

				updatePendingIntent = PendingIntent.getActivity(AppUpdateService.this, 0, installIntent, 0);
				updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
				updateNotification.contentView.setTextViewText(R.id.notification_title,	Config.appName);
				updateNotification.contentView.setTextViewText(R.id.notification_text,"下载完成,点击安装");
				updateNotification.contentIntent = updatePendingIntent;
				//updateNotification.setLatestEventInfo(AppUpdateService.this, appName, "下载完成,点击安装", updatePendingIntent);

				Toast.makeText(AppUpdateService.this, "下载完成!", Toast.LENGTH_SHORT).show();

				updateNotificationManager.notify(0, updateNotification);

				// 停止服务
				startActivity(installIntent);
				stopService(updateIntent);
				break;
			case DOWNLOAD_FAIL:

				updateNotification.contentView.setTextViewText(R.id.notification_title,	Config.appName);
				updateNotification.contentView.setTextViewText(R.id.notification_text,"下载失败,请重试!");
				updateNotification.contentIntent = updatePendingIntent;

				// 下载失败
				//updateNotification.setLatestEventInfo(AppUpdateService.this, appName, "下载失败,请重试!", updatePendingIntent);

				updateNotificationManager.notify(0, updateNotification);
				stopService(updateIntent);
				break;
			default:
				stopService(updateIntent);
			}
		}
	};

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			try {
				if (!updateDir.exists()) { 
					updateDir.mkdirs();
				}
				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}
				long downloadSize = downloadUpdateFile(appId, updateFile);
				if (downloadSize > 0) {
					// 下载成功
					updateHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// 下载失败
				updateHandler.sendMessage(message);
			}
		}
	}

	public long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {

		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
				if ((downloadCount == 0) || (int) (totalSize * 100 / updateTotalSize) - 10 > downloadCount) {
					downloadCount += 10;

					updateNotification.contentView.setTextViewText(R.id.notification_title,	"正在下载. 秀生活...");
					updateNotification.contentView.setTextViewText(R.id.notification_text,(int) totalSize * 100/ updateTotalSize + "%");
					updateNotification.contentIntent = updatePendingIntent;

					//updateNotification.setLatestEventInfo(AppUpdateService.this, "正在下载. 秀生活...", (int) totalSize * 100
					//		/ updateTotalSize + "%", updatePendingIntent);

					updateNotificationManager.notify(0, updateNotification);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

}
