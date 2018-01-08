package cn.lottery.framework.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import android.os.Handler;
import android.os.Message;

/**
 * 使用说明<br>
 * 
 * 获取UploadUtils对象. <br>
 * UploadUtils uploadUtils = UploadUtils.getInstance(); <br>
 * 设置上传监听器. <br>
 * uploadUtils.setOnUploadProcessListener(new OnUploadProcessListener() { <br>
 * 
 * @Override 
 * public void uploadSuccess(String result) { //上传成功,result是服务器返回的结果. } <br>
 * @Override 
 * public void uploadProcess(int uploadSize) { //上传中,uploadSize标识当前已经上传的大小. } <br>
 * @Override 
 * public void uploadInit(long fileSize) { //上传前的初始化,fileSize表示要上传的文件大小. } <br>
 * @Override 
 * public void uploadFail(String message) { //上传失败,message表示失败原因. } }); <br>
 *           开始上传. <br>
 *           uploadUtils.uploadFile(SDCardUtils.getSDCardPath() + "tomcat.zip",
 *           "upload","http://192.168.1.19:8088/merchant/system/upload/imageUpload.htm", null); <br>
 *           文件上传辅助类.
 *
 */
public class UploadUtils {
	private static final int SUCCESS = 0;
	private static final int FAIL = 1;
	private static final int INIT = 2;
	private static final int PROCESS = 3;

	private static final String BOUNDARY = "eakayappfileupload"; // UUID.randomUUID().toString(); // 文件内容的边界标识 随机生成
	private static final String PREFIX = "--";// 前缀
	private static final String LINE_END = "\r\n";
	private static final String CHARSET = "utf-8"; // 设置编码

	private static UploadUtils uploadUtil;

	private UploadUtils() {

	}

	// 单例模式.
	public static UploadUtils getInstance() {
		if (null == uploadUtil) {
			uploadUtil = new UploadUtils();
		}
		return uploadUtil;
	}

	/**
	 * android上传文件到服务器
	 * 
	 * @param filePath
	 *            需要上传的文件的路径
	 * @param name
	 *            在网页上<input type=file name=xxx/> xxx就是这里的name
	 * @param RequestURL
	 *            请求的URL
	 */
	public void uploadFile(String filePath, String name, String RequestURL, Map<String, String> param) {
		File file = null;
		try {
			file = new File(filePath);
		} catch (NullPointerException e) {
			Message msg = new Message();
			msg.what = FAIL;
			msg.obj = "文件不存在";
			handler.sendMessage(msg);

			e.printStackTrace();
		}
		uploadFile(file, name, RequestURL, param);
	}

	/**
	 * android上传文件到服务器
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param name
	 *            在网页上<input type=file name=xxx/> xxx就是这里的name
	 * @param RequestURL
	 *            请求的URL
	 */
	public void uploadFile(final File file, final String name, final String RequestURL, final Map<String, String> param) {
		if (file == null || (!file.exists())) {
			Message msg = new Message();
			msg.what = FAIL;
			msg.obj = "文件不存在";
			handler.sendMessage(msg);

			return;
		}

		// 开启线程上传文件
		new Thread(new Runnable() {
			@Override
			public void run() {
				toUploadFile(file, name, RequestURL, param);
			}
		}).start();
	}

	private void toUploadFile(File file, String name, String RequestURL, Map<String, String> param) {
		try {
			Message msg = new Message();
			msg.what = INIT;
			msg.obj = file.length();
			handler.sendMessage(msg);

			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setChunkedStreamingMode(128 * 1024);// 128K
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			StringBuffer sb = new StringBuffer();
			// 文本参数
			if (param != null && param.size() > 0) {
				Iterator<String> it = param.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					String value = param.get(key);
					// 组装参数格式.
					sb.delete(0, sb.length());// 清空
					sb.append(PREFIX).append(BOUNDARY);
					sb.append(LINE_END);
					sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"");
					sb.append(LINE_END);
					sb.append(LINE_END);
					sb.append(value);
					sb.append(LINE_END);
					// 写入输出流.
					dos.write(sb.toString().getBytes());
				}
			}

			// 文件参数
			sb.delete(0, sb.length());// 清空
			sb.append(PREFIX).append(BOUNDARY);
			sb.append(LINE_END);
			sb.append("Content-Disposition:form-data; name=\"" + name + "\"; filename=\"" + file.getName() + "\"");
			sb.append(LINE_END);
			sb.append(LINE_END);
			dos.write(sb.toString().getBytes());

			// 文件数据
			FileInputStream fit = new FileInputStream(file);
			byte[] buffer = new byte[8 * 1024];// 8KB
			int len = 0;
			int curLen = 0;
			while ((len = fit.read(buffer)) != -1) {
				curLen += len;
				dos.write(buffer, 0, len);

				Message msg1 = new Message();
				msg1.what = PROCESS;
				msg1.obj = curLen;
				handler.sendMessage(msg1);
			}
			sb.delete(0, sb.length());// 清空
			sb.append(LINE_END);
			sb.append(PREFIX + BOUNDARY + PREFIX);
			sb.append(LINE_END);
			dos.write(sb.toString().getBytes());

			fit.close();
			dos.flush();

			// 获取响应码 200=成功 当响应成功，获取响应的流
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, CHARSET);
				BufferedReader br = new BufferedReader(isr, 8 * 1024);// 8KB

				sb.delete(0, sb.length());// 清空
				String result = null;
				while ((result = br.readLine()) != null) {
					sb.append(result);
				}

				Message msg1 = new Message();
				msg1.what = SUCCESS;
				msg1.obj = sb.toString();
				handler.sendMessage(msg1);

			} else {
				Message msg1 = new Message();
				msg1.what = FAIL;
				msg1.obj = String.valueOf(code);
				handler.sendMessage(msg1);
			}
		} catch (MalformedURLException e) {
			Message msg1 = new Message();
			msg1.what = FAIL;
			msg1.obj = "网络访问错误";
			handler.sendMessage(msg1);
			e.printStackTrace();
		} catch (IOException e) {
			Message msg1 = new Message();
			msg1.what = FAIL;
			msg1.obj = "文件访问错误";
			handler.sendMessage(msg1);
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				onUploadProcessListener.uploadSuccess((String) msg.obj);
				break;
			case FAIL:
				onUploadProcessListener.uploadFail((String) msg.obj);
				break;
			case PROCESS:
				onUploadProcessListener.uploadProcess((Integer) msg.obj);
				break;
			case INIT:
				onUploadProcessListener.uploadInit((Long) msg.obj);
				break;
			}
		}
	};

	private OnUploadProcessListener onUploadProcessListener;

	public void setOnUploadProcessListener(OnUploadProcessListener onUploadProcessListener) {
		this.onUploadProcessListener = onUploadProcessListener;
	}

	public static interface OnUploadProcessListener {

		// 上传失败.
		void uploadFail(String message);

		// 上传成功.
		void uploadSuccess(String result);

		// 上传处理中.
		void uploadProcess(int uploadSize);

		// 上传初始化.
		void uploadInit(long fileSize);
	}

}
