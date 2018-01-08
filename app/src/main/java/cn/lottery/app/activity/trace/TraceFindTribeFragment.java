package cn.lottery.app.activity.trace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.tribe.IYWTribeService;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.app.activity.openim.tribe.OpenimTribeInfoActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.fragment.BaseFragment;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.openim.tribe.TribeConstants;

/**
 * 查找群
 * Created by admin on 2017/6/29.
 */
public class TraceFindTribeFragment extends BaseFragment implements View.OnClickListener  {

    private String TAG = TraceFindTribeFragment.class.getSimpleName();
    private String APPKEY;
    private String mUserId;
    private YWIMKit mIMKit;
    private IYWTribeService mTribeService;
    private YWTribe mTribe;
    private long mTribeId;
    private ProgressBar mProgressBar;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private View view;

    private EditText searchKeywordEditText;
    private ImageView searchBtn;


    private LinearLayout tribeLayout,lineLayout;
    private Button bottom_btn;
    private ImageView tribeHeadimg;
    private TextView tribeName,tribeid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = OpenIMLoginHelper.getInstance().getIMKit().getIMCore().getLoginUserId();
        if (TextUtils.isEmpty(mUserId)) {
            YWLog.i(TAG, "user not login");
        }
        APPKEY= OpenIMLoginHelper.getInstance().getIMKit().getIMCore().getAppKey();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if(view!=null){
            ViewGroup parent = (ViewGroup)view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }
        view = inflater.inflate(R.layout.km_activity_search_tribe, null);
        init();
        return view;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i==R.id.bottom_btn){
            //getSuperParent().addFragment(new TraceAddContactFragment(), true);
        }
    }

    private void init() {
        mIMKit = OpenIMLoginHelper.getInstance().getIMKit();
        mTribeService = mIMKit.getTribeService();

        tribeLayout = (LinearLayout) view.findViewById(R.id.tribeLayout);
        lineLayout= (LinearLayout) view.findViewById(R.id.lineLayout);

        tribeHeadimg= (ImageView) view.findViewById(R.id.tribeHeadimg);
        tribeName= (TextView) view.findViewById(R.id.tribeName);
        tribeid= (TextView) view.findViewById(R.id.tribeid);

        bottom_btn=(Button) view.findViewById(R.id.bottom_btn);

        tribeLayout.setVisibility(View.GONE);
        lineLayout.setVisibility(View.GONE);
        bottom_btn.setVisibility(View.GONE);
        bottom_btn.setOnClickListener(this);


        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        searchKeywordEditText = (EditText) view.findViewById(R.id.input_tribe_id);
        searchBtn = (ImageView) view.findViewById(R.id.search_tribe);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tribeId = searchKeywordEditText.getText().toString();
                try {
                    mTribeId = Long.valueOf(tribeId);
                } catch (Exception e) {
                    IMNotificationUtils.getInstance().showToast(getActivity(), "请输入群id");
                }
                mTribe = mTribeService.getTribe(mTribeId);
                if (mTribe == null || mTribe.getTribeRole() == null) {
                    searchBtn.setClickable(false);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTribeService.getTribeFromServer(new IWxCallback() {
                        @Override
                        public void onSuccess(Object... result) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    searchBtn.setClickable(true);
                                    mProgressBar.setVisibility(View.GONE);
                                    //没加入过群
                                    //startTribeInfoActivity(TribeConstants.TRIBE_JOIN);

                                    getTribeInfo(false);

                                    //IMNotificationUtils.getInstance().showToast(getActivity(), "没加入过群！");
                                }
                            });
                        }

                        @Override
                        public void onError(int code, String info) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    searchBtn.setClickable(true);
                                    mProgressBar.setVisibility(View.GONE);
                                    IMNotificationUtils.getInstance().showToast(getActivity(), "没有搜索到该群，请确认群id是否正确！");
                                }
                            });
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    }, mTribeId);
                } else {
                    //已经加入群了
                    //startTribeInfoActivity(null);
                    getTribeInfo(true);
                    IMNotificationUtils.getInstance().showToast(getActivity(), "已经加入过群！");
                }
            }
        });
    }

    private void startTribeInfoActivity(String tribeOp) {
        Intent intent = new Intent(getActivity(), OpenimTribeInfoActivity.class);
        intent.putExtra(TribeConstants.TRIBE_ID, mTribeId);
        if (!TextUtils.isEmpty(tribeOp)) {
            intent.putExtra(TribeConstants.TRIBE_OP, tribeOp);
        }
        startActivity(intent);
    }

    /**
     * 获取群系信息
     * @param jr
     */
    private void getTribeInfo(Boolean jr) {
        if(mTribe!=null) {
            if(!jr) {
                bottom_btn.setVisibility(View.VISIBLE);
                bottom_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        operateTribeCallBack(mTribe.getTribeId()+"");
                    }
                 });

                /* app 调用api加入群
                bottom_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTribeService.joinTribe(new IWxCallback() {
                            @Override
                            public void onSuccess(Object... result) {
                                if (result != null && result.length > 0) {
                                    Integer retCode = (Integer) result[0];
                                    if (retCode == 0) {
                                        YWLog.i(TAG, "加入群成功！");
                                        IMNotificationUtils.getInstance().showToast(getActivity(), "加入群成功！");
                                        operateTribeCallBack(mTribe.getTribeId()+"");
                                     }
                                }
                            }

                            @Override
                            public void onError(int code, String info) {
                                //YWLog.i(TAG, "加入群失败， code = " + code + ", info = " + info);
                                //IMNotificationUtils.getInstance().showToast(getActivity(), "加入群失败, code = " + code + ", info = " + info);
                                if(code==11){
                                    IMNotificationUtils.getInstance().showToast(getActivity(), "加入群请求已发送，等待身份验证");
                                }
                                else if(code==3){
                                    IMNotificationUtils.getInstance().showToast(getActivity(), "此群不允许任何人主动加入群");
                                }
                                else if(code==8){
                                    IMNotificationUtils.getInstance().showToast(getActivity(), "加入群,需要密码");
                                }
                            }

                            @Override
                            public void onProgress(int progress) {

                            }
                        }, mTribeId);
                    }
                });
                */
            }
            tribeLayout.setVisibility(View.VISIBLE);
            lineLayout.setVisibility(View.VISIBLE);
            tribeName.setText(mTribe.getTribeName());
            tribeid.setText(mTribe.getTribeId()+"");
            DisplayImageOptions option = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.aliwx_tribe_head_default)
                    .showImageOnFail(R.drawable.aliwx_tribe_head_default).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new SimpleBitmapDisplayer())
                    .build();
            SApplication.getInstance().getImageLoader().displayImage(mTribe.getTribeIcon(), tribeHeadimg, option);
        }
    }


    //=============================群请求回调======================================

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
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("operateTribeCallBack")) {
            processOperateTribeCallBack(obj);
        }
    }


    /**
     * 请求出错
     */
    @Override
    protected void handError(Object tag, String errorInfo) {

    }

    /**
     * 群申请请求回调业务处理
     * @param json
     */
    private  void processOperateTribeCallBack(JSONObject json){
        try{
            JSONObject msg= json.getJSONObject("msg");
            IMNotificationUtils.getInstance().showToast(getActivity(), msg.getString("info"));
            bottom_btn.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
