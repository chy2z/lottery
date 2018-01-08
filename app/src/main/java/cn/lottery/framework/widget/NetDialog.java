package cn.lottery.framework.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.lottery.R;

/**
 * 网络请求中的对话框.
 * 
 * @author specter
 *
 */
public class NetDialog extends Dialog {
	private static final int DIALOG_WIDTH = 140;// 对话框宽度.

	private Button confirm;
	private Button cancle;
	private ProgressBar loading;
	private TextView content;
	private TextView title;
	private LinearLayout buttonContainer;
	private RelativeLayout  imageclose;

	// private Window window = null;
	private boolean isTouchDismiss = true;

	public NetDialog(Context context) {

		super(context, R.style.f_net_dialog);

		setContentView(R.layout.f_widget_net_dialog);

		title = (TextView) findViewById(R.id.title);

		content = (TextView) findViewById(R.id.content);

		confirm = (Button) findViewById(R.id.btn_ok);

		cancle = (Button) findViewById(R.id.btn_cancel);

		buttonContainer = (LinearLayout) findViewById(R.id.bottom);

		imageclose=(RelativeLayout) findViewById(R.id.layoutClose);

		loading = (ProgressBar) findViewById(R.id.pb1);

		confirm.setFocusable(true);

		cancle.setFocusable(true);
	}

	/**
	 * 显示dialog.
	 */
	@Override
	public void show() {
		try {
			if (!isShowing()) {
				super.show();
			}
		}
		catch (Exception e)
		{

		}
	}

	@Override
	public boolean isShowing() {
		return super.isShowing();
	}

	public void close() {
		if (isShowing()) {
			dismiss();
		}
	}

	/**
	 * 点击空白区域是否可关闭.
	 * 
	 */
	public void isTouchDismiss(boolean isTouchDismiss) {
		this.isTouchDismiss = isTouchDismiss;
		setCanceledOnTouchOutside(isTouchDismiss);
	}

	/**
	 * 标题.
	 */
	public void addTitle(String titleStr) {
		title.setVisibility(View.VISIBLE);
		title.setText(titleStr);
	}

	/**
	 * 内容
	 */
	public void addContent(String contentStr) {
		content.setVisibility(View.VISIBLE);
		content.setText(contentStr);
	}

	/**
	 * 确定按钮
	 */
	public void addConfirmButton(String bttonText) {
		buttonContainer.setVisibility(View.VISIBLE);
		//confirm.setVisibility(View.VISIBLE); //弹出框子不显示确定按钮
		//confirm.setText(bttonText);		
		imageclose.setVisibility(View.VISIBLE);		
	}	
	

	/**
	 * 取消按钮
	 */
	public void addCancelButton(String bttonText) {
		buttonContainer.setVisibility(View.VISIBLE);
		cancle.setVisibility(View.VISIBLE);
		cancle.setText(bttonText);
	}

	/**
	 * 添加圆形进度条
	 */
	public void addProcessBar() {
		loading.setVisibility(View.VISIBLE);
		buttonContainer.setVisibility(View.INVISIBLE);
		imageclose.setVisibility(View.INVISIBLE);	
	}
	
	
	/**
	 * 删除圆形进度条
	 */
	public void delProcessBar() {
		loading.setVisibility(View.GONE);		
		buttonContainer.setVisibility(View.VISIBLE);
		imageclose.setVisibility(View.VISIBLE);	
	}
	
	/**
	 * 添加图标点击事件
	 * 
	 * @param listener
	 */
	public void setCloseClickListener(View.OnClickListener listener) {
		imageclose.setOnClickListener(listener);
	}

	/**
	 * 添加确定按钮点击事件
	 * 
	 * @param listener
	 */
	public void setConfirmClickListener(View.OnClickListener listener) {
		confirm.setOnClickListener(listener);
	}

	/**
	 * 添加取消按钮点击事件
	 * 
	 * @param listener
	 */
	public void setCancelClickListener(View.OnClickListener listener) {
		cancle.setOnClickListener(listener);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!isTouchDismiss && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
