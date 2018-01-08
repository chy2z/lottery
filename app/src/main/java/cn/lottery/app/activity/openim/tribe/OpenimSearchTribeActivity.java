package cn.lottery.app.activity.openim.tribe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.tribe.IYWTribeService;
import com.alibaba.mobileim.utility.IMNotificationUtils;

import cn.lottery.R;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.openim.tribe.TribeConstants;

/**
 * 搜群
 */
public class OpenimSearchTribeActivity extends Activity {

    private static final String TAG = "OpenimSearchTribeActivity";

    private YWIMKit mIMKit;
    private IYWTribeService mTribeService;
    private YWTribe mTribe;
    private long mTribeId;

    private ProgressBar mProgressBar;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openim_activity_search_tribe);
        init();
    }

    private void init() {
        initTitle();
        mIMKit = OpenIMLoginHelper.getInstance().getIMKit();
        mTribeService = mIMKit.getTribeService();

        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        final EditText tribeIdEditText = (EditText) findViewById(R.id.input_tribe_id);
        final ImageView searchButton = (ImageView) findViewById(R.id.search_tribe);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tribeId = tribeIdEditText.getText().toString();
                try {
                    mTribeId = Long.valueOf(tribeId);
                } catch (Exception e) {
                    IMNotificationUtils.getInstance().showToast(OpenimSearchTribeActivity.this, "请输入群id");
                }
                mTribe = mTribeService.getTribe(mTribeId);
                if (mTribe == null || mTribe.getTribeRole() == null) {
                    searchButton.setClickable(false);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTribeService.getTribeFromServer(new IWxCallback() {
                        @Override
                        public void onSuccess(Object... result) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    searchButton.setClickable(true);
                                    mProgressBar.setVisibility(View.GONE);
                                    startTribeInfoActivity(TribeConstants.TRIBE_JOIN);
                                }
                            });
                        }

                        @Override
                        public void onError(int code, String info) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    searchButton.setClickable(true);
                                    mProgressBar.setVisibility(View.GONE);
                                    IMNotificationUtils.getInstance().showToast(OpenimSearchTribeActivity.this, "没有搜索到该群，请确认群id是否正确！");
                                }
                            });
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    }, mTribeId);
                } else {
                    startTribeInfoActivity(null);
                }
            }
        });
    }

    private void startTribeInfoActivity(String tribeOp) {
        Intent intent = new Intent(this, OpenimTribeInfoActivity.class);
        intent.putExtra(TribeConstants.TRIBE_ID, mTribeId);
        if (!TextUtils.isEmpty(tribeOp)) {
            intent.putExtra(TribeConstants.TRIBE_OP, tribeOp);
        }
        startActivity(intent);
    }

    private void initTitle() {
        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.title_bar);
        titleBar.setVisibility(View.VISIBLE);
        TextView titleView = (TextView) findViewById(R.id.title_self_title);

        TextView leftButton = (TextView) findViewById(R.id.left_button);
        leftButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.openim_common_back_btn_white, 0, 0, 0);
        leftButton.setTextColor(Color.WHITE);
        leftButton.setText("返回");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleBar.setBackgroundColor(getResources().getColor(R.color.title_bg));
        titleView.setTextColor(Color.WHITE);
        titleView.setText("搜索加入群");


        TextView rightButton = (TextView) findViewById(R.id.right_button);
        rightButton.setVisibility(View.GONE);

    }
}
