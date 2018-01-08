package cn.lottery.app.activity.trace;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWChannel;
import com.alibaba.mobileim.channel.cloud.contact.YWProfileInfo;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.mobileim.contact.IYWDBContact;
import com.alibaba.mobileim.fundamental.widget.YWAlertDialog;
import com.alibaba.mobileim.kit.contact.YWContactHeadLoadHelper;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import java.util.ArrayList;
import java.util.List;
import cn.lottery.R;
import cn.lottery.app.activity.openim.contact.IFindContactParent;
import cn.lottery.framework.Config;
import cn.lottery.framework.openim.OpenIMLoginHelper;

/**
 * 查找联系人
 * Created by admin on 2017/6/29.
 */
public class TraceFindContactFragment extends Fragment implements View.OnClickListener {

    private String TAG = TraceFindContactFragment.class.getSimpleName();

    private LinearLayout contantLayout,lineLayout;
    private Button bottom_btn;
    private ImageView contactHeadimg;
    private TextView contactNickName,contactuserid;

    private YWContactHeadLoadHelper mHelper;

    private ProgressDialog mProgressView;
    private EditText searchKeywordEditText;
    private ImageView searchBtn;
    private AlertDialog dialog;
    private volatile boolean isStop;

    private Handler handler = new Handler();
    private View view;
    private boolean isFinishing;
    private Handler mHandler=new Handler();

    private String APPKEY;
    private String mUserId;



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
        view = inflater.inflate(R.layout.km_fragment_find_contact, null);
        init();
        return view;
    }

    private void initHelper() {
        mHelper = new YWContactHeadLoadHelper(this.getActivity(), null, OpenIMLoginHelper.getInstance().getIMKit().getUserContext());
    }

    private void initView() {
        contantLayout = (LinearLayout) view.findViewById(R.id.contantLayout);
        lineLayout= (LinearLayout) view.findViewById(R.id.lineLayout);

        contactHeadimg= (ImageView) view.findViewById(R.id.contactHeadimg);
        contactNickName= (TextView) view.findViewById(R.id.contactNickName);
        contactuserid= (TextView) view.findViewById(R.id.contactuserid);

        bottom_btn=(Button) view.findViewById(R.id.bottom_btn);

        contantLayout.setVisibility(View.GONE);
        lineLayout.setVisibility(View.GONE);
        bottom_btn.setVisibility(View.GONE);
        bottom_btn.setOnClickListener(this);
    }

    private void init() {
        initHelper();
        initView();
        initSearchView();
    }

    private void initSearchView() {
        searchKeywordEditText = (EditText) view.findViewById(R.id.aliwx_search_keyword);
        searchKeywordEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchKeywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==KeyEvent.ACTION_DOWN||actionId==EditorInfo.IME_ACTION_DONE){
                    String keyword = searchKeywordEditText.getText().toString();
                    if (!TextUtils.isEmpty(keyword)) {
                        searchContent(keyword);
                        return true;
                    }else{
                        return false;
                    }
                }
                return false;
            }
        });
        searchKeywordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String keyword = searchKeywordEditText.getText().toString();
                    if (!TextUtils.isEmpty(keyword)) {
                        searchContent(keyword);
                        return true;
                    }else{
                        return false;
                    }
                }
                return false;
            }
        });
        searchBtn = (ImageView) view.findViewById(R.id.aliwx_search_btn);
        searchBtn.setOnClickListener(this);
        searchBtn.setEnabled(false);

        searchKeywordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    searchBtn.setEnabled(true);
                } else {
                    searchBtn.setEnabled(false);
                }

            }
        });
        searchKeywordEditText.requestFocus();
        showKeyBoard();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.aliwx_search_btn) {
            String keyword = searchKeywordEditText.getText().toString();
            if (!TextUtils.isEmpty(keyword)) {
                searchContent(keyword);
            }
        }
        else if(i==R.id.bottom_btn){

            getSuperParent().addFragment(new TraceAddContactFragment(), true);


        }
    }

    private void searchContent(String keyword) {
        if (YWChannel.getInstance().getNetWorkState().isNetWorkNull()) {
            IMNotificationUtils.getInstance().showToast(this.getActivity(), this.getResources().getString(R.string.aliwx_net_null));
        } else {
            String key = keyword.replace(" ", "");
            String userId = key;
            ArrayList<String> userIds = new ArrayList<String>();
            userIds.add(userId);
            showProgress();
            IYWContactService contactService =getContactService();
            contactService.fetchUserProfile(userIds, APPKEY, new IWxCallback() {

                @Override
                public void onSuccess(final Object... result) {
                    if (result != null) {
                        List<YWProfileInfo> profileInfos = (List<YWProfileInfo>) (result[0]);
                        if (profileInfos == null || profileInfos.isEmpty()) {
                            handleResult((List) result[0]);
                            return;
                        }
                        YWProfileInfo mYWProfileInfo = profileInfos.get(0); //取第一个联系人
                        cancelProgress();
                        checkIfHasContact(mYWProfileInfo); //判断是否已经增加
                        showSearchResult(mYWProfileInfo);
                    } else {
                        handleResult((List) result[0]);
                    }
                }

                @Override
                public void onError(int code, String info) {
                    handleResult(null);
                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }
    }


    private void showProgress() {
        if (mProgressView == null) {
            mProgressView = new ProgressDialog(this.getActivity());
            mProgressView.setMessage(getResources().getString(
                    R.string.aliwx_search_friend_processing));
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

    private void handleResult(final List profileInfos) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing) {
                    cancelProgress();
                    if (profileInfos == null || profileInfos.isEmpty()) {
                        if (isStop) {
                            return;
                        }
                        if (dialog == null) {
                            dialog = new YWAlertDialog.Builder(
                                    TraceFindContactFragment.this.getActivity())
                                    .setTitle(R.string.aliwx_search_friend_not_found)
                                    .setMessage(
                                            R.string.aliwx_search_friend_not_found_message)
                                    .setPositiveButton(
                                            R.string.aliwx_confirm,
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    // TODO Auto-generated
                                                    // method stub
                                                }

                                            }).create();
                        }
                        dialog.show();
                    }
                }
            }
        });

    }

    public boolean onBackPressed() {
        isFinishing=true;
        if (mProgressView != null && mProgressView.isShowing()) {
            mProgressView.dismiss();
            isStop = true;
            return false;
        }
        hideKeyBoard();
        getActivity().finish();
        return true;
    }

    private IFindContactParent getSuperParent(){
        IFindContactParent superParent = (IFindContactParent) getActivity();
        return superParent;
    }
    private IYWContactService getContactService(){
        IYWContactService contactService = OpenIMLoginHelper.getInstance().getIMKit().getContactService();
        return contactService;
    }
    //--------------------------[搜索到的联系人的展示]相关实现

    private List<IYWDBContact> contactsFromCache;
    public void showSearchResult(final YWProfileInfo lmYWProfileInfo){

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (lmYWProfileInfo == null || TextUtils.isEmpty(lmYWProfileInfo.userId)) {
                    IMNotificationUtils.getInstance().showToast(TraceFindContactFragment.this.getActivity(), "服务开小差，建议您重试搜索");
                    return;
                }
                IFindContactParent superParent = getSuperParent();

                superParent.setYWProfileInfo(lmYWProfileInfo);

                hideKeyBoard();

                //跳转页面显示联系人
                //superParent.addFragment(new OpenimContactProfileFragment(), true);

                if(lmYWProfileInfo.userId.equals(Config.IMUserId)){
                    IMNotificationUtils.getInstance().showToast(getActivity(), "这是您自己");
                }else{
                    if(!superParent.isHasContactAlready()) {
                        contantLayout.setVisibility(View.VISIBLE);
                        contantLayout.setVisibility(View.VISIBLE);
                        bottom_btn.setVisibility(View.VISIBLE);

                        contactNickName.setText(lmYWProfileInfo.nick);
                        contactuserid.setText(lmYWProfileInfo.userId);
                        mHelper.setHeadView(contactHeadimg, lmYWProfileInfo.userId, APPKEY, true);

                    }
                    else{
                        IMNotificationUtils.getInstance().showToast(getActivity(),"联系人已经添加");
                    }
                }
            }
        });

    }




    private void checkIfHasContact(YWProfileInfo mYWProfileInfo){
        //修改hasContactAlready和contactsFromCache的Fragment生命周期缓存
        IFindContactParent superParent = getSuperParent();
        if(superParent!=null){
            contactsFromCache =getContactService().getContactsFromCache();
            for(IYWDBContact contact:contactsFromCache){
                if(contact.getUserId().equals(mYWProfileInfo.userId)){
                    superParent.setHasContactAlready(true);
                    return;
                }
            }
            superParent.setHasContactAlready(false);
        }
    }

    private void showKeyBoard() {
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(view, 0);
        }
    }

    protected void hideKeyBoard() {
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager)  this.getActivity().getSystemService(this.getActivity().INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
