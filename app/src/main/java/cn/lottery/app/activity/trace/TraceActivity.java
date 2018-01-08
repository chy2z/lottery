package cn.lottery.app.activity.trace;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.util.WxLog;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.IYWConversationUnreadChangeListener;
import com.alibaba.mobileim.conversation.IYWMessageLifeCycleListener;
import com.alibaba.mobileim.conversation.IYWSendMessageToContactInBlackListListener;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWConversationType;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageType;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.gingko.presenter.tribe.IYWTribeChangeListener;
import com.alibaba.mobileim.login.IYWConnectionListener;
import com.alibaba.mobileim.login.YWLoginCode;
import com.alibaba.mobileim.login.YWLoginState;
import java.util.HashMap;
import java.util.Map;
import cn.lottery.R;
import cn.lottery.app.activity.HomePageActivity;
import cn.lottery.app.activity.login.LoginActivity;
import cn.lottery.app.activity.myself.MyActivity;
import cn.lottery.app.activity.web.WebPageActivity;
import cn.lottery.framework.Config;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.openim.CustomConversationHelper;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.openim.common.Constant;

/**
 * 踪迹主界面
 * 
 */
public class TraceActivity extends BaseActivity implements View.OnClickListener {

	public static final String TAG = "TraceActivity";

	private LinearLayout layoutChat,layoutContacts,layoutDynamic,layoutBack;

	private LinearLayout layoutMy, layoutTrace, layoutHomepage;

	private LinearLayout myGathering,myFreeGoods,myParttimeJob,myWorking,myNews;

	private TextView addContact;

	private Context mContext;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private YWIMKit mIMKit;
	private IYWConversationService mConversationService;
	private IYWConversationUnreadChangeListener mConversationUnreadChangeListener;
	private IYWTribeChangeListener mTribeChangedListener;
	private IYWMessageLifeCycleListener mMessageLifeCycleListener;
	private IYWSendMessageToContactInBlackListListener mSendMessageToContactInBlackListListener;
	private IYWConnectionListener mConnectionListener;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_km_trace);

		initView();

		initOpenIM();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==1&&resultCode==2)
		{
			//data.getStringExtra("data");
			//String content=data.getStringExtra("data");
			//toast(content);
			initOpenIM();
		}
	}


	private void initView(){
		layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(this);

		layoutChat = (LinearLayout) findViewById(R.id.layoutChat);
		layoutChat.setOnClickListener(this);

		layoutContacts = (LinearLayout) findViewById(R.id.layoutContacts);
		layoutContacts.setOnClickListener(this);

		layoutDynamic = (LinearLayout) findViewById(R.id.layoutDynamic);
		layoutDynamic.setOnClickListener(this);

		layoutMy = (LinearLayout) findViewById(R.id.layoutMy);
		layoutMy.setOnClickListener(this);

		layoutTrace = (LinearLayout) findViewById(R.id.layoutTrace);
		layoutTrace.setOnClickListener(this);

		layoutHomepage = (LinearLayout) findViewById(R.id.layoutHomepage);
		layoutHomepage.setOnClickListener(this);

		myGathering = (LinearLayout) findViewById(R.id.myGathering);
		myGathering.setOnClickListener(this);

		myFreeGoods = (LinearLayout) findViewById(R.id.myFreeGoods);
		myFreeGoods.setOnClickListener(this);

		myParttimeJob = (LinearLayout) findViewById(R.id.myParttimeJob);
		myParttimeJob.setOnClickListener(this);

		myWorking = (LinearLayout) findViewById(R.id.myWorking);
		myWorking.setOnClickListener(this);

		myNews = (LinearLayout) findViewById(R.id.myNews);
		myNews.setOnClickListener(this);

		addContact=(TextView)findViewById(R.id.addContact);
		addContact.setOnClickListener(this);

	}

	/**
	 * 初始化即时通信
	 */
	private void initOpenIM(){
		if(!Config.userToken.equals("")) {
			//toast("initOpenIM");
			initOpenimData();
			initOpenimListeners();
		}
	}

	private void initOpenimData(){
		mContext=this;
		mIMKit = OpenIMLoginHelper.getInstance().getIMKit();
		if (mIMKit == null) {
			return;
		}
		mConversationService = mIMKit.getConversationService();
	}

	private void initOpenimListeners(){
		//初始化并添加会话变更监听
		initConversationServiceAndListener();
		//初始化并添加群变更监听
		addTribeChangeListener();
		//初始化自定义会话
		initCustomConversation();
		//设置发送消息生命周期监听
		setMessageLifeCycleListener();
		//设置发送消息给黑名单中的联系人监听
		setSendMessageToContactInBlackListListener();
		//添加IM连接状态监听
		addConnectionListener();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case  R.id.layoutBack:
				finish();
				break;
			case R.id.addContact:
				if(Config.userToken.equals("")){
					startActivityForResult(new Intent(this, LoginActivity.class),1);
				}
				else {
					Intent intent = new Intent().setClass(this, TraceAddContactOrTribeActivity.class);
					intent.putExtra("type","Friend");
					startActivity(intent);
				}
				break;
			case R.id.myGathering:
				if(Config.userToken.equals("")){
					startActivityForResult(new Intent(this, LoginActivity.class),1);
				}
				else {
					Intent intent = new Intent(this, WebPageActivity.class);
					intent.putExtra("httpURL", Config.H5_URL_PREFIX+"/kamang/html/home/mypartyList.html");
					startActivity(intent);
				}
				break;
			case R.id.myFreeGoods:
				if(Config.userToken.equals("")){
					startActivityForResult(new Intent(this, LoginActivity.class),1);
				}
				else {
					Intent intent = new Intent(this, WebPageActivity.class);
					intent.putExtra("httpURL", Config.H5_URL_PREFIX+"/kamang/html/trace/leisuregoods/myleisureGoodsList.html");
					startActivity(intent);
				}
				break;
			case R.id.myParttimeJob:
				if(Config.userToken.equals("")){
					startActivityForResult(new Intent(this, LoginActivity.class),1);
				}
				else {
					Intent intent = new Intent(this, WebPageActivity.class);
					intent.putExtra("httpURL", Config.H5_URL_PREFIX+"/kamang/html/trace/parttime/mypartTimeList.html");
					startActivity(intent);
				}
				break;
			case R.id.myWorking:
				if(Config.userToken.equals("")){
					startActivityForResult(new Intent(this, LoginActivity.class),1);
				}
				else {
					Intent intent = new Intent(this, WebPageActivity.class);
					intent.putExtra("httpURL",Config.H5_URL_PREFIX+ "/kamang/html/trace/maker/mymakerList.html");
					startActivity(intent);
				}
				break;
			case R.id.myNews:
				if(Config.userToken.equals("")){
					startActivityForResult(new Intent(this, LoginActivity.class),1);
				}
				else {
					Intent intent = new Intent(this, WebPageActivity.class);
					intent.putExtra("httpURL", Config.H5_URL_PREFIX+"/kamang/html/trace/message/messageList.html");
					startActivity(intent);
				}
				break;
			case R.id.layoutChat:
				if(Config.userToken.equals("")){
					startActivityForResult(new Intent(this, LoginActivity.class),1);
				}
				else {
					startActivity(new Intent(this, TraceChatActivity.class));
				}
				break;
			case R.id.layoutContacts:
				if(Config.userToken.equals("")){
					startActivityForResult(new Intent(this, LoginActivity.class),1);
				}
				else {
					startActivity(new Intent(this, TraceContactActivity.class));
				}
				break;
			case R.id.layoutDynamic:
				if(Config.userToken.equals("")){
					startActivityForResult(new Intent(this, LoginActivity.class),1);
				}
				else {
					startActivity(new Intent(this, TraceDynamicActivity.class));
				}
				break;
			case R.id.layoutHomepage:
				startActivity(new Intent(this,HomePageActivity.class));
				break;
			case R.id.layoutMy:
				startActivity(new Intent(this,MyActivity.class));
				break;
		}
	}

	//===========================即时通信监听====================

	/**
	 * 自定义会话示例展示系统通知的示例
	 */
	private void initCustomConversation() {
		//联系人消息
		CustomConversationHelper.addCustomConversation(Constant.SYSTEM_FRIEND_REQ_CONVERSATION, "");
		//自定义消息
		//CustomConversationHelper.addCustomConversation(Constant.SYSTEM_CHY_NOTIFY, "咖忙欢迎你！");
	}

	/**
	 * 消息未读数量
	 */
	private void initConversationServiceAndListener() {
		mConversationUnreadChangeListener = new IYWConversationUnreadChangeListener() {

			//当未读数发生变化时会回调该方法，开发者可以在该方法中更新未读数
			@Override
			public void onUnreadChange() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						OpenIMLoginHelper loginHelper = OpenIMLoginHelper.getInstance();
						final YWIMKit imKit = loginHelper.getIMKit();
						mConversationService = imKit.getConversationService();
						//获取当前登录用户的所有未读数
						int unReadCount = mConversationService.getAllUnreadCount();
						//设置桌面角标的未读数
						mIMKit.setShortcutBadger(unReadCount);
						/*
						if (unReadCount > 0) {
							mUnread.setVisibility(View.VISIBLE);
							if (unReadCount < 100) {
								mUnread.setText(unReadCount + "");
							} else {
								mUnread.setText("99+");
							}
						} else {
							mUnread.setVisibility(View.INVISIBLE);
						}
						*/
					}
				});
			}
		};
		mConversationService.addTotalUnreadChangeListener(mConversationUnreadChangeListener);
	}

	/**
	 * 群监听
	 */
	private void addTribeChangeListener(){
		mTribeChangedListener = new IYWTribeChangeListener() {

			/**
			 * 被邀请加入群
			 * @param tribe 群信息
			 * @param user 邀请发起者
			 */
			@Override
			public void onInvite(YWTribe tribe, YWTribeMember user) {
				Map<YWTribe, YWTribeMember> map = new HashMap<YWTribe, YWTribeMember>();
				map.put(tribe, user);
				OpenIMLoginHelper.getInstance().getTribeInviteMessages().add(map);
				String userName = user.getShowName();
				if (TextUtils.isEmpty(userName)){
					userName = user.getUserId();
				}
				WxLog.i(TAG, "onInvite");
			}

			/**
			 * 用户加入群
			 * @param tribe
			 * @param user
			 */
			@Override
			public void onUserJoin(YWTribe tribe, YWTribeMember user) {
				//用户user加入群tribe
			}

			/**
			 * 用户退出群
			 * @param tribe
			 * @param user
			 */
			@Override
			public void onUserQuit(YWTribe tribe, YWTribeMember user) {
				//用户user退出群tribe
			}

			/**
			 * 用户被请出群
			 * @param tribe
			 * @param user
			 */
			@Override
			public void onUserRemoved(YWTribe tribe, YWTribeMember user) {
				//用户user被提出群tribe
			}

			/**
			 * 停用群
			 * @param tribe
			 */
			@Override
			public void onTribeDestroyed(YWTribe tribe) {
				//群组tribe被解散了
			}

			/**
			 * 群信息更新`
			 * @param tribe
			 */
			@Override
			public void onTribeInfoUpdated(YWTribe tribe) {
				//群组tribe的信息更新了（群名称、群公告、群校验模式修改了）
			}

			/**
			 * 群管理员变更
			 * @param tribe
			 * @param user
			 */
			@Override
			public void onTribeManagerChanged(YWTribe tribe, YWTribeMember user) {
				//用户user被设置为群管理员或者被取消管理员
			}

			/**
			 * 成员群角色变更
			 * @param tribe
			 * @param user
			 */
			@Override
			public void onTribeRoleChanged(YWTribe tribe, YWTribeMember user) {
				//用户user的群角色发生改变了
			}
		};
		mIMKit.getTribeService().addTribeListener(mTribeChangedListener);
	}

	/**
	 * 发送消息监听
	 */
	private void setMessageLifeCycleListener(){
		mMessageLifeCycleListener = new IYWMessageLifeCycleListener() {
			/**
			 * 发送消息前回调
			 * @param conversation 当前消息所在会话
			 * @param message      当前将要发送的消息
			 * @return  需要发送的消息，若为null，则表示不发送消息
			 */
			@Override
			public YWMessage onMessageLifeBeforeSend(YWConversation conversation, YWMessage message) {
				//todo 以下代码仅仅是示例，开发者无需按照以下方式设置，应该根据自己的需求对消息进行修改
				String cvsType = "单聊";
				if (conversation.getConversationType() == YWConversationType.Tribe){
					cvsType = "群聊：";
				}
				String msgType = "文本消息";
				if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_IMAGE){
					msgType = "图片消息";
				} else if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_GEO){
					msgType = "地理位置消息";
				} else if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_AUDIO){
					msgType = "语音消息";
				} else if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_P2P_CUS || message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_TRIBE_CUS){
					msgType = "自定义消息";
				}

				//TODO 设置APNS Push，如果开发者需要对APNS Push进行定制可以调用message.setPushInfo(YWPushInfo)方法进行设置，如果不需要APNS Push定制则不需要调用message.setPushInfo(YWPushInfo)方法
				//TODO Demo默认发送消息不需要APNS Push定制,所以注释掉以下两行代码
				//YWPushInfo pushInfo = new YWPushInfo(1, cvsType + msgType, "dingdong", "我是自定义数据");
				//message.setPushInfo(pushInfo);

				//根据消息类型对消息进行修改，切记这里只是示例，具体怎样对消息进行修改开发者可以根据自己的需求进行处理
				if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_TEXT){
					String content = message.getContent();
					/*
					if (content.equals("55")) {
						message.setContent("我修改了消息内容, 原始内容：55");
						return message;
					} else if (content.equals("66")){
						YWMessage newMsg = YWMessageChannel.createTextMessage("我创建了一条新消息, 原始消息内容：66");
						return newMsg;
					} else if (content.equals("77")){
						IMNotificationUtils.getInstance().showToast(getBaseContext(), "不发送该消息，消息内容为：77");
						return null;
					}
					*/
				}
				return message;
			}

			/**
			 * 发送消息结束后回调
			 * @param message   当前发送的消息
			 * @param sendState 消息发送状态，具体状态请参考{@link com.alibaba.mobileim.conversation.YWMessageType.SendState}
			 */
			@Override
			public void onMessageLifeFinishSend(YWMessage message, YWMessageType.SendState sendState) {
				//IMNotificationUtils.getInstance().showToast(FragmentTabs.this, sendState.toString());
			}
		};
		mConversationService.setMessageLifeCycleListener(mMessageLifeCycleListener);
	}

	/**
	 * 设置连接状态的监听
	 */
	private void addConnectionListener() {
		mConnectionListener = new IYWConnectionListener() {
			@Override
			public void onDisconnect(int code, String info) {
				if (code == YWLoginCode.LOGON_FAIL_KICKOFF) {
					//在其它终端登录，当前用户被踢下线
					Toast.makeText(SApplication.getContext(), "被踢下线", Toast.LENGTH_LONG).show();
					YWLog.i("TraceActivity", "被踢下线");
					OpenIMLoginHelper.getInstance().setAutoLoginState(YWLoginState.idle);
					finish();
					Intent intent = new Intent(SApplication.getContext(), LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					SApplication.getContext().startActivity(intent);
				}
			}

			@Override
			public void onReConnecting() {

			}

			@Override
			public void onReConnected() {

			}
		};
		mIMKit.getIMCore().addConnectionListener(mConnectionListener);
	}

	/**
	 * 黑名单
	 */
	private void setSendMessageToContactInBlackListListener(){
		mSendMessageToContactInBlackListListener = new IYWSendMessageToContactInBlackListListener() {
			/**
			 * 是否发送消息给黑名单中的联系人，当用户发送消息给黑名单中的联系人时我们会回调该接口
			 * @param conversation 当前发送消息的会话
			 * @param message      要发送的消息
			 * @return true：发送  false：不发送
			 */
			@Override
			public boolean sendMessageToContactInBlackList(YWConversation conversation, YWMessage message) {
				return true; //TODO 开发者可用根据自己的需求决定是否要发送该消息，SDK默认不发送
			}
		};
		mConversationService.setSendMessageToContactInBlackListListener(mSendMessageToContactInBlackListListener);
	}

}
