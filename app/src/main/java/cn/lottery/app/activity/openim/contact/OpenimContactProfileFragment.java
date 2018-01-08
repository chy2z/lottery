package cn.lottery.app.activity.openim.contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWConstants;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.cloud.contact.YWProfileInfo;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.IYWContactCacheUpdateListener;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.mobileim.contact.IYWDBContact;
import com.alibaba.mobileim.conversation.EServiceContact;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.kit.common.IMUtility;
import com.alibaba.mobileim.kit.common.YWAsyncBaseAdapter;
import com.alibaba.mobileim.kit.contact.ContactHeadLoadHelper;
import com.alibaba.mobileim.kit.contact.YWContactHeadLoadHelper;
import com.alibaba.mobileim.lib.model.message.YWSystemMessage;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.alibaba.mobileim.utility.UserContext;
import java.util.List;
import cn.lottery.R;
import cn.lottery.framework.openim.OpenIMLoginHelper;

/**
 * 联系人信息界面
 */
public  class OpenimContactProfileFragment extends Fragment implements OnClickListener{


    private View view;

	private YWContactHeadLoadHelper mHelper;
	private YWProfileInfo mYWProfileInfo;
	private LinearLayout mBottomLayout;
	private String APPKEY;
	private String mUserId;
	private String TAG = OpenimContactProfileFragment.class.getSimpleName();
	private IYWContactCacheUpdateListener mContactCacheUpdateListener;
	private List<IYWDBContact> contactsFromCache;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserId = OpenIMLoginHelper.getInstance().getIMKit().getIMCore().getLoginUserId();
		if (TextUtils.isEmpty(mUserId)) {
			YWLog.i(TAG, "user not login");
		}
		APPKEY=getIMkit().getIMCore().getAppKey();
	}

	private void addContactCacheUpdateListener() {
		if(mContactCacheUpdateListener!=null&& OpenIMLoginHelper.getInstance().getIMKit()!=null)
			OpenIMLoginHelper.getInstance().getIMKit().getContactService().addContactCacheUpdateListener(mContactCacheUpdateListener);
	}

	private void removeContactCacheUpdateListener() {
		if(mContactCacheUpdateListener!=null&& OpenIMLoginHelper.getInstance().getIMKit()!=null)
			OpenIMLoginHelper.getInstance().getIMKit().getContactService().removeContactCacheUpdateListener(mContactCacheUpdateListener);
	}

	private void initContactCacheUpdateListener() {
		mContactCacheUpdateListener=new IYWContactCacheUpdateListener(){

			/**
			 * 好友缓存发生变化(联系人备注修改、联系人新增和减少等)，可以刷新使用联系人缓存的UI
			 * todo 该回调在UI线程回调 ，请勿做太重的操作
			 *
			 * @param currentUserid                 当前登录账户
			 * @param currentAppkey                 当前Appkey
			 */
			@Override
			public void onFriendCacheUpdate(String currentUserid, String currentAppkey) {
				IFindContactParent superParent = getSuperParent();
				if(superParent!=null){
					checkIfHasContact(superParent.getYWProfileInfo());
					initData();
				}
			}
		};
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





	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
		if(view==null){
			initContactCacheUpdateListener();
		}
		addContactCacheUpdateListener();

        if(view!=null){
			ViewGroup parent = (ViewGroup)view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
            return view;
        }
        view = inflater.inflate(R.layout.openim_fragment_contact_profile, null);
        init();
        return view;
    }
    private void initTitle(){

		RelativeLayout titleBar = (RelativeLayout) view.findViewById(R.id.title_bar);
		titleBar.setBackgroundColor(Color.TRANSPARENT);
		titleBar.setVisibility(View.VISIBLE);
        //titleBar.setBackgroundColor(Color.parseColor("#00b4ff"));
		TextView leftButton = (TextView) view.findViewById(R.id.left_button);
		leftButton.setText("");
		leftButton.setTextColor(Color.WHITE);
		leftButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.aliwx_common_back_btn_bg_white, 0, 0, 0);
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		TextView title = (TextView) view.findViewById(R.id.title_self_title);
		title.setTextColor(Color.WHITE);
		title.setText("个人资料");
    }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		removeContactCacheUpdateListener();
	}

	private void init() {
		initTitle();
		initSearchResultView();
		initHelper();
		initData();

	}
	private IFindContactParent getSuperParent(){
		IFindContactParent superParent = (IFindContactParent) getActivity();
		return superParent;
	}
	private IYWContactService getContactService(){
		IYWContactService contactService = OpenIMLoginHelper.getInstance().getIMKit().getContactService();
		return contactService;
	}
	private YWIMKit getIMkit(){
		YWIMKit imkit = OpenIMLoginHelper.getInstance().getIMKit();
		return imkit;
	}
	private void initData() {
		IFindContactParent superParent = getSuperParent();
		mYWProfileInfo=superParent.getYWProfileInfo();
		hasContactAlready=superParent.isHasContactAlready();
		clearProfileResult();
		showProfileResult( mYWProfileInfo);
	}

	private void initHelper() {
		mHelper = new YWContactHeadLoadHelper(this.getActivity(), null, getIMkit().getUserContext());
	}



	@Override
	public void onClick(View v) {
		int i = v.getId();
		 if (i == R.id.aliwx_bottom_btn) {  //加为好友
			 getSuperParent().addFragment(new OpenimAddContactFragment(), true);
		} else if (i == R.id.aliwx_btn_send_message) {
			sendMessage(mYWProfileInfo);    //发送消息
		}
	}


	public boolean onBackPressed() {
        getSuperParent().finish(false);
		return true;
	}

	//--------------------------[搜索到的联系人的展示]相关实现

	private View parallaxView;
	private  Button mBottomButton;
	private Button mSendMessageBtn;
	private ImageView mHeadBgView;
	private ImageView mHeadView;
	private TextView mSelfDescview;
	private boolean hasContactAlready;
	private void initSearchResultView() {
		parallaxView =view.findViewById(R.id.aliwx_parallax_view);
		parallaxView.setVisibility(View.GONE);
		mHeadBgView = (ImageView) view.findViewById(R.id.aliwx_block_bg);
		mHeadBgView.setImageResource(R.drawable.aliwx_head_bg_0);
		mHeadView = (ImageView) view.findViewById(R.id.aliwx_people_head);
		mSelfDescview = (TextView) view.findViewById(R.id.aliwx_people_desc);
		mSelfDescview.setMaxLines(2); // 必须函数设置，否则无效

		mBottomLayout = (LinearLayout)view. findViewById(R.id.aliwx_bottom_layout);
		mBottomButton = (Button) view.findViewById(R.id.aliwx_bottom_btn);
		mBottomButton.setOnClickListener(this);
		mSendMessageBtn = (Button) view.findViewById(R.id.aliwx_btn_send_message);
		mSendMessageBtn.setOnClickListener(this);
	}
	public  void clearProfileResult(){
		parallaxView.setVisibility(View.GONE);
	}

	public void showProfileResult( final YWProfileInfo lmYWProfileInfo){


		if(lmYWProfileInfo==null||TextUtils.isEmpty(lmYWProfileInfo.userId)){
            IMNotificationUtils.getInstance().showToast(OpenimContactProfileFragment.this.getActivity(), "服务开小差，建议您重试搜索");
			return;
		}

		mYWProfileInfo=lmYWProfileInfo;
		setProfileResult(lmYWProfileInfo);
		setBottomView(lmYWProfileInfo);
		parallaxView.setVisibility(View.VISIBLE);

	}
	public void setProfileResult(YWProfileInfo profileInfo) {
		if(profileInfo!=null){
			RelativeLayout useridLayout = (RelativeLayout) view.findViewById(R.id.aliwx_userid_layout);
			if (TextUtils.isEmpty(profileInfo.userId)) {
				useridLayout.setVisibility(View.GONE);
			} else {
				useridLayout.setVisibility(View.VISIBLE);
				TextView textView = (TextView) view.findViewById(R.id.aliwx_userid_text);
				textView.setText(new StringBuilder("  ").append(profileInfo.userId));
			}
			RelativeLayout settingRemarkNameLayout = (RelativeLayout) view.findViewById(R.id.aliwx_remark_name_layout);
			if (TextUtils.isEmpty(profileInfo.nick)) {
				settingRemarkNameLayout.setVisibility(View.GONE);
			} else {
				settingRemarkNameLayout.setVisibility(View.VISIBLE);
				TextView textView = (TextView) view.findViewById(R.id.aliwx_remark_name_text);
				textView.setText(new StringBuilder("  ").append(profileInfo.nick));
			}
//			if (TextUtils.isEmpty(profileInfo.icon)) {
//				mHeadView.setImageResource(R.drawable.aliwx_head_default);
//			} else if(!TextUtils.isEmpty(profileInfo.userId)&&!TextUtils.isEmpty(profileInfo.icon)) {
				mHelper.setHeadView(mHeadView, profileInfo.userId, APPKEY, true);
//			}
		}
	}

	private void sendMessage(YWProfileInfo mYWProfileInfo) {
		if(mYWProfileInfo.userId.equals(OpenIMLoginHelper.getInstance().getIMKit().getIMCore().getLoginUserId())) {
            IMNotificationUtils.getInstance().showToast(this.getActivity(), "这是您自己，无法发送消息");
			return;
		}
		if (APPKEY.equals(YWConstants.YWSDKAppKey)) {
			//TB或千牛客的服账号
			EServiceContact eServiceContact = new EServiceContact(mYWProfileInfo.userId, 0);//
			Intent intent =  getIMkit().getChattingActivityIntent(eServiceContact);
			startActivity(intent);
		} else { //打开聊天对话框
			Intent intent =  getIMkit().getChattingActivityIntent(mYWProfileInfo.userId, APPKEY);
			startActivity(intent);
		}
	}




	private void setBottomView(YWProfileInfo lmYWProfileInfo){
		/* 对应账号以及好友关系所能够进行的操作*/
		if(mYWProfileInfo.userId.equals(OpenIMLoginHelper.getInstance().getIMKit().getIMCore().getLoginUserId())){
			mBottomLayout.setVisibility(View.GONE);
            IMNotificationUtils.getInstance().showToast(this.getActivity(), "这是您自己");
			return;
		}else if(hasContactAlready){ //已有该联系人
			mBottomLayout.setVisibility(View.VISIBLE);
			mBottomButton.setVisibility(View.GONE);
			mSendMessageBtn.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mSendMessageBtn
					.getLayoutParams();
			layoutParams.width = getResources().getDimensionPixelSize(
					R.dimen.aliwx_friend_info_btn_width);
			layoutParams.weight = 0;
			mSendMessageBtn.setLayoutParams(layoutParams);
			//ToastHelper.showToastMsg(this.getActivity(), "已有该联系人");
		}else { //新的联系人
			mBottomLayout.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams bLayoutParams = (LinearLayout.LayoutParams) mBottomButton
					.getLayoutParams();
			bLayoutParams.width = 0;
			bLayoutParams.weight = 1;
			mBottomButton.setLayoutParams(bLayoutParams);
			mBottomButton.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mSendMessageBtn
					.getLayoutParams();
			layoutParams.width = 0;
			layoutParams.weight = 1;
			mSendMessageBtn.setLayoutParams(layoutParams);
			mSendMessageBtn.setVisibility(View.VISIBLE);
		}

	}

	public static class ContactSystemMessageAdapter extends YWAsyncBaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;
        private List<YWMessage> mMessageList;
        private ContactHeadLoadHelper mContactHeadLoadHelper;
        private String mAppKey;
        private UserContext userContext;

        public ContactSystemMessageAdapter(UserContext userContext, Context context, List<YWMessage> messages) {
            this.userContext = userContext;
            mContext = context;
            mMessageList = messages;
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final YWIMKit ywimKit = YWAPI.getIMKitInstance(userContext.getShortUserId(), userContext.getAppkey());
            mContactHeadLoadHelper = new ContactHeadLoadHelper((Activity)context, null, ywimKit.getUserContext());
            mAppKey = OpenIMLoginHelper.getInstance().getIMKit().getIMCore().getAppKey();
        }

        private class ViewHolder {
            TextView showName;
            TextView message;
            TextView agreeButton;
			TextView refuseButton;
            TextView result;
            ImageView head;
        }

        public void refreshData(List<YWMessage> list){
            mMessageList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mMessageList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMessageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private IYWContactService getContactService(){
            return OpenIMLoginHelper.getInstance().getIMKit().getContactService();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.openim_contact_system_message_item, parent, false);
                holder = new ViewHolder();
                holder.showName = (TextView) convertView.findViewById(R.id.contact_title);
                holder.head = (ImageView) convertView.findViewById(R.id.head);
                holder.message = (TextView) convertView.findViewById(R.id.invite_message);
                holder.agreeButton = (TextView) convertView.findViewById(R.id.agree);
				holder.refuseButton=(TextView) convertView.findViewById(R.id.refuse);
                holder.result = (TextView) convertView.findViewById(R.id.result);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (mMessageList != null) {
                final YWMessage msg = mMessageList.get(position);
                final YWSystemMessage message = (YWSystemMessage) msg;
                String authorUserId = message.getAuthorUserId();
                IYWContact contact = IMUtility.getContactProfileInfo(userContext, message.getAuthorUserId(), message.getAuthorAppkey());
                if(contact != null) {
                    holder.showName.setText(contact.getShowName() + " 申请加你为好友");
                } else {
                    holder.showName.setText(authorUserId + " 申请加你为好友");
                }
                holder.message.setText("备注: "+message.getMessageBody().getContent());
                holder.agreeButton.setText("接受");
                holder.agreeButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((OpenimContactSystemMessageActivity) mContext).acceptToBecomeFriend(msg);
                    }
                });

				holder.refuseButton.setText("拒绝");
				holder.refuseButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						((OpenimContactSystemMessageActivity) mContext).refuseToBecomeFriend(msg);
					}
				});

                if (message.isAccepted()){
                    holder.agreeButton.setVisibility(View.GONE);
					holder.refuseButton.setVisibility(View.GONE);
                    holder.result.setVisibility(View.VISIBLE);
                    holder.result.setText("已添加");
                } else if (message.isIgnored()){
                    holder.agreeButton.setVisibility(View.GONE);
					holder.refuseButton.setVisibility(View.GONE);
                    holder.result.setVisibility(View.VISIBLE);
                    holder.result.setText("已忽略");
                } else {
                    holder.agreeButton.setVisibility(View.VISIBLE);
					holder.refuseButton.setVisibility(View.VISIBLE);
                    holder.result.setVisibility(View.GONE);
                }

                mContactHeadLoadHelper.setHeadView(holder.head,authorUserId, mAppKey,true);
            }
            return convertView;
        }

        @Override
        public void loadAsyncTask() {

        }

    }
}
