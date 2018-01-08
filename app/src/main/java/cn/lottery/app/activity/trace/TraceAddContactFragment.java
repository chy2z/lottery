package cn.lottery.app.activity.trace;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.mobileim.channel.cloud.contact.YWProfileInfo;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.mobileim.utility.IMNotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.app.activity.openim.contact.IFindContactParent;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.fragment.BaseFragment;
import cn.lottery.framework.openim.OpenIMLoginHelper;

/**
 * 发送增加用户请求
 * Created by admin on 2017/6/29.
 */
public class TraceAddContactFragment extends BaseFragment implements View.OnClickListener {

    private ProgressDialog mProgressView;
    private EditText mAddFriendMessageEditText;
    private volatile boolean isStop;

    private View view;
    private boolean isFinishing;

    private String TAG = TraceAddContactFragment.class.getSimpleName();
    private String mAppKey;
    private String mUserId;
    private String mNickName;
    private String mMsg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        view = inflater.inflate(R.layout.km_fragment_add_contact, null);
        init();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //sendAddContactRequest();
    }

    private void initView(){
        EditText addFriendMessage = (EditText) view.findViewById(R.id.aliwx_add_friend_message);
        //addFriendMessage.setVisibility(View.GONE);
        addFriendMessage.setText("我是"+Config.nickname);
        addFriendMessage.setImeOptions(EditorInfo.IME_ACTION_DONE);
        addFriendMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==KeyEvent.ACTION_DOWN||actionId==EditorInfo.IME_ACTION_DONE) {
                    sendAddContactRequest();
                }
                return false;
            }
        });

        Button bottom_btn = (Button) view.findViewById(R.id.bottom_btn);
        bottom_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAddContactRequest();
            }
        });
    }

    private void sendAddContactRequest() {
        sendAddContactRequest(mUserId, mAppKey, mNickName, new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                YWLog.i(TAG, "好友申请已发送成功,  id = " + mUserId + ", appkey = " + mAppKey+ ", mMsg = " + mMsg);
                //IMNotificationUtils.getInstance().showToast(getActivity(), "好友申请已发送成功,  id = " + mUserId + ", appkey = " + mAppKey);
                //cancelProgress();
                //getSuperParent().finish(false);
                //发送好友申请
                operateContactCallBack(Config.IMUserId,mUserId,"0");
            }

            @Override
            public void onError(int code, String info) {
                YWLog.i(TAG, "好友申请失败，code = " + code + ", info = " + info);
                IMNotificationUtils.getInstance().showToast(getActivity(), "好友申请失败，code = " + code + ", info = " + info);
                cancelProgress();
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }


    private void sendAddContactRequest(String userId, String appKey,String nickName,IWxCallback callback) {
        mMsg=mAddFriendMessageEditText.getText().toString();
        showProgress();
        getContactService().addContact(userId, appKey, nickName, mMsg, callback);
    }

    private IFindContactParent getSuperParent(){
        IFindContactParent superParent = (IFindContactParent) getActivity();
        return superParent;
    }

    private IYWContactService getContactService(){
        IYWContactService contactService = OpenIMLoginHelper.getInstance().getIMKit().getContactService();
        return contactService;
    }

    private void init() {
        initView();
        initAddContactView();
        initData();
    }

    private void initData() {
        IFindContactParent superParent = getSuperParent();
        YWProfileInfo ywProfileInfo = superParent.getYWProfileInfo();
        if(ywProfileInfo!=null){
            mUserId=ywProfileInfo.userId;
            mAppKey= OpenIMLoginHelper.getInstance().getIMKit().getIMCore().getAppKey();
        }
    }


    private void initAddContactView() {
        mAddFriendMessageEditText = (EditText) view.findViewById(R.id.aliwx_add_friend_message);
        mAddFriendMessageEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mAddFriendMessageEditText.requestFocus();
        showKeyBoard();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

    }


    public boolean onBackPressed() {
        isFinishing=true;
        if (mProgressView != null && mProgressView.isShowing()) {
            mProgressView.dismiss();
            isStop = true;
            return false;
        }
        hideKeyBoard();
        getSuperParent().finish(false);
        return true;
    }

    private void showKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(view, 0);
        }
    }

    protected void hideKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager)  getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showProgress() {
        if (mProgressView == null) {
            mProgressView = new ProgressDialog(this.getActivity());
            mProgressView.setMessage(getResources().getString(
                    R.string.aliwx_add_friend_processing));
            mProgressView.setIndeterminate(true);
            mProgressView.setCancelable(true);
            mProgressView.setCanceledOnTouchOutside(false);
        }
        mProgressView.show();
    }

    private void cancelProgress() {
        if (mProgressView != null && mProgressView.isShowing()) {
            mProgressView.dismiss();
        }
    }

    //=============================好友请求回调======================================

    /**
     * 好友请求回调
     * @param one
     * @param two
     * @param type
     */
    private void  operateContactCallBack(String one ,String two,String type){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accountIdOne", one);
        params.put("accountIdTwo", two);
        params.put("type", type);
        post(Config.URL_PREFIX + RequestUrl.addFriend, params, null,"operateContactCallBack");
    }


    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("operateContactCallBack")) {
            processOperateContactCallBack(obj);
        }
    }


    /**
     * 请求出错
     */
    @Override
    protected void handError(Object tag, String errorInfo) {
        if (tag.equals("operateContactCallBack")) {
            cancelProgress();
            getSuperParent().finish(false);
        }
    }

    /**
     * 好友请求回调业务处理
     * @param json
     */
    private  void processOperateContactCallBack(JSONObject json){
        try{
            JSONObject msg= json.getJSONObject("msg");
            if(msg.getBoolean("success")){
                cancelProgress();
                getSuperParent().finish(false);
            }
            else{
                cancelProgress();
                getSuperParent().finish(false);
            }
            IMNotificationUtils.getInstance().showToast(getActivity(), msg.getString("info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}