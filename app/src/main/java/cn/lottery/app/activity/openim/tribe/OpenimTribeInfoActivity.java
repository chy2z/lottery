package cn.lottery.app.activity.openim.tribe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.constant.YWProfileSettingsConstants;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.WxLog;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.fundamental.widget.WxAlertDialog;
import com.alibaba.mobileim.gingko.model.settings.YWTribeSettingsModel;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeCheckMode;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeRole;
import com.alibaba.mobileim.gingko.presenter.tribe.IYWTribeChangeListener;
import com.alibaba.mobileim.tribe.IYWTribeService;
import com.alibaba.mobileim.utility.IMNotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cn.lottery.R;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseOpenimActivity;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.openim.tribe.TribeConstants;

/**
 * 群信息展示
 * 如何获取群消息接收状态以及对群消息接收状态的设置
 */
public class OpenimTribeInfoActivity extends BaseOpenimActivity {

    private static final String TAG = "TribeInfoActivity";
    private static final int SET_MSG_REC_TYPE_REQUEST_CODE = 10000;
    private static final int EDIT_MY_TRIBE_NICK_REQUEST_CODE = 10001;
    private static final int SET_TRIBE_CHECK_MODE_REQUEST_CODE = 10002;
    private YWIMKit mIMKit;
    private IYWTribeService mTribeService;
    private YWTribe mTribe;
    private long mTribeId;
    private String mTribeOp;
    private int mTribeMemberCount;
    List<YWTribeMember> mList = new ArrayList<YWTribeMember>();

    private IYWTribeChangeListener mTribeChangedListener;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private TextView mTribeName;
    private TextView mTribeMaster;  //群主
    private TextView mTribeDesc;
    private TextView mMemberCount;
    private TextView mQuiteTribe;
    private TextView mMangeTribeMembers;
    private TextView mMyTribeNick;
    private RelativeLayout mMangeTribeMembersLayout;
    private RelativeLayout mEditTribeInfoLayout;
    private RelativeLayout mEditMyTribeProfileLayout;
    private RelativeLayout mEditPersonalSettings;

    private RelativeLayout mTribeMsgRecTypeLayout;
    private TextView mTribeMsgRecType;
    private ImageView mAtMsgRecSwitch;
    private RelativeLayout mTribeCheckModeLayout;
    private TextView mTribeCheckMode;

    private TextView mCLearTribeMsgs;

    private int mMsgRecFlag = YWProfileSettingsConstants.TRIBE_MSG_REJ;
    private int mAtMsgRecFlag = YWProfileSettingsConstants.TRIBE_AT_MSG_REC;

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    private IYWConversationService conversationService;
    private YWConversation conversation;

    private YWTribeMember mLoginUser;

    private RelativeLayout enableAtAllLayout;
    private ImageView enableAtAllSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openim_activity_tribe_info);

        Intent intent = getIntent();

        mTribeId = intent.getLongExtra(TribeConstants.TRIBE_ID, 0);

        mTribeOp = intent.getStringExtra(TribeConstants.TRIBE_OP);

        mIMKit = OpenIMLoginHelper.getInstance().getIMKit();

        conversationService = mIMKit.getConversationService();

        conversation = conversationService.getTribeConversation(mTribeId);

        mTribeService = mIMKit.getTribeService();

        initTribeChangedListener();

        initTribeInfo();

        initView();

        getTribeMsgRecSettings();
    }

    private void initTitle() {
        View titleView = findViewById(R.id.title_bar);
        titleView.setVisibility(View.VISIBLE);
        titleView.setBackgroundColor(getResources().getColor(R.color.title_bg)); //#00b4ff

        TextView leftButton = (TextView) findViewById(R.id.left_button);
        leftButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.openim_common_back_btn_black, 0, 0, 0);
        leftButton.setText("返回");
        leftButton.setTextColor(Color.BLACK);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView) findViewById(R.id.title_self_title);
        title.setText("群资料");
        title.setTextColor(Color.BLACK);
    }

    private void initView() {
        initTitle();
        mTribeName = (TextView) findViewById(R.id.tribe_name);
        final TextView tribeId = (TextView) findViewById(R.id.tribe_id);
        tribeId.setText("群号 " + mTribeId);

        mTribeMaster = (TextView) findViewById(R.id.tribe_master);

        mTribeDesc = (TextView) findViewById(R.id.tribe_description);
        mMemberCount = (TextView) findViewById(R.id.member_count);

        //编辑群成员
        mMangeTribeMembers = (TextView) findViewById(R.id.manage_tribe_members);
        mMangeTribeMembersLayout = (RelativeLayout) findViewById(R.id.manage_tribe_members_layout);
        mMangeTribeMembersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "没权限");
                if(false) {
                    //IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "TribeMembersActivity");
                    Intent intent = new Intent(OpenimTribeInfoActivity.this, OpenimTribeMembersActivity.class);
                    intent.putExtra(TribeConstants.TRIBE_ID, mTribeId);
                    startActivity(intent);
                }
            }
        });

        //编辑群信息
        mEditTribeInfoLayout = (RelativeLayout) findViewById(R.id.edit_tribe_info_layout);
        mEditTribeInfoLayout.setVisibility(View.GONE);
        mEditTribeInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "EditTribeInfoActivity");
                Intent intent = new Intent(OpenimTribeInfoActivity.this, OpenimEditTribeInfoActivity.class);
                intent.putExtra(TribeConstants.TRIBE_ID, mTribeId);
                intent.putExtra(TribeConstants.TRIBE_OP, TribeConstants.TRIBE_EDIT);
                startActivity(intent);
            }
        });

        //群名片
        mMyTribeNick = (TextView) findViewById(R.id.my_tribe_nick);
        mMyTribeNick.setText(getLoginUserTribeNick());
        mEditMyTribeProfileLayout = (RelativeLayout) findViewById(R.id.my_tribe_profile_layout);
        mEditMyTribeProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "没权限");
                if(false) {
                    //IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "EditMyTribeProfileActivity");
                    Intent intent = new Intent(OpenimTribeInfoActivity.this, OpenimEditMyTribeProfileActivity.class);
                    intent.putExtra(TribeConstants.TRIBE_ID, mTribeId);
                    intent.putExtra(TribeConstants.TRIBE_NICK, mMyTribeNick.getText());
                    startActivityForResult(intent, EDIT_MY_TRIBE_NICK_REQUEST_CODE);
                }
            }
        });

        mEditPersonalSettings = (RelativeLayout) findViewById(R.id.personal_tribe_setting_layout);
        mEditPersonalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "TribePersonalSettingActivity");
                //Intent intent = new Intent(OpenimTribeInfoActivity.this, TribePersonalSettingActivity.class);
                //intent.putExtra("tribeId", mTribeId);
                //startActivity(intent);
            }
        });

        mTribeMsgRecTypeLayout = (RelativeLayout) findViewById(R.id.tribe_msg_rec_type_layout);
        mTribeMsgRecTypeLayout.setVisibility(View.GONE);
        mTribeMsgRecType = (TextView) findViewById(R.id.tribe_msg_rec_type);
        mAtMsgRecSwitch = (ImageView) findViewById(R.id.receive_tribe_at_msg);

        //群消息状态设置
        mTribeMsgRecTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "没权限");
                if(false) {
                    //IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "mTribeMsgRecTypeLayout");
                    Intent intent = OpenimTribeMsgRecTypeSetActivity.getTribeMsgRecTypeSetActivityIntent(OpenimTribeInfoActivity.this, mMsgRecFlag);
                    startActivityForResult(intent, SET_MSG_REC_TYPE_REQUEST_CODE);
                }
            }
        });

        //接收群@消息开关
        mAtMsgRecSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "没权限");
                if(false) {
                    if (mMsgRecFlag != YWProfileSettingsConstants.TRIBE_MSG_REJ) {
                        IMNotificationUtils.getInstance().showToast("接收群消息时不能屏蔽群@消息哦!", OpenimTribeInfoActivity.this);
                    } else {
                        if (mAtMsgRecFlag == YWProfileSettingsConstants.TRIBE_AT_MSG_REC) {
                            setMsgRecType(YWProfileSettingsConstants.TRIBE_MSG_REJ);
                            setAtMsgRecType(YWProfileSettingsConstants.TRIBE_AT_MSG_REJ);
                        } else {
                            setAtMsgRecType(YWProfileSettingsConstants.TRIBE_AT_MSG_REC);
                        }
                    }
                }
            }
        });

        //身份验证设置
        mTribeCheckModeLayout = (RelativeLayout) findViewById(R.id.tribe_verify_layout);

        mTribeCheckModeLayout.setVisibility(View.GONE);

        mTribeCheckMode = (TextView) findViewById(R.id.tribe_verify);
        mTribeCheckModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "没权限");
                if(false) {
                    Intent intent = new Intent(OpenimTribeInfoActivity.this, OpenimSetTribeCheckModeActivity.class);
                    intent.putExtra(TribeConstants.TRIBE_CHECK_MODE, mTribe.getTribeCheckMode().ordinal());
                    intent.putExtra(TribeConstants.TRIBE_ID, mTribe.getTribeId());
                    startActivityForResult(intent, SET_TRIBE_CHECK_MODE_REQUEST_CODE);
                }
            }
        });

        //清理群消息
        mCLearTribeMsgs = (TextView) findViewById(R.id.clear_tribe_msg);
        mCLearTribeMsgs.setVisibility(View.GONE);
        mCLearTribeMsgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMsgRecord();
            }
        });

        mQuiteTribe = (TextView) findViewById(R.id.quite_tribe);

        //是否启用@All
        enableAtAllLayout = (RelativeLayout) findViewById(R.id.config_at_all_for_normal_members_layout);
        enableAtAllLayout.setVisibility(View.GONE);
        enableAtAllSwitch = (ImageView) findViewById(R.id.config_at_all_switch);
        enableAtAllSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTribe.isEnableAtAll()) {
                    mTribeService.disableAtAllForNormalMembers(mTribeId, new ConfigAtAllCallback());
                } else {
                    mTribeService.enableAtAllForNormalMembers(mTribeId, new ConfigAtAllCallback());
                }
            }
        });
    }

    /**
     * 群普通消息接收状态以及群@消息接收状态，SDK会在用户首次登录时或者清数据重登后自动获取并同步DB以及cache，
     * 并且会在一天后重新获取，如果开发者想实施获取可以调用{@link com.alibaba.mobileim.tribe.YWTribeManager#getTribesMsgRecSettingsFromServer(List, int, IWxCallback)}
     * 获取最新配置（如果考虑到用户会经常跨终端登录，为了多端同步，开发者需要自行根据情况考虑缓存策略）
     */
    private void initMsgRecFlags() {
        if (mTribe == null) {
            mTribe = mTribeService.getTribe(mTribeId);
        }
        if (mTribe != null) {
            mMsgRecFlag = mTribe.getMsgRecType();
            mAtMsgRecFlag = mTribe.getAtFlag();
        }
        if (mAtMsgRecFlag == YWProfileSettingsConstants.TRIBE_AT_MSG_REJ) {
            mAtMsgRecSwitch.setImageResource(R.drawable.off_switch);
        } else {
            mAtMsgRecSwitch.setImageResource(R.drawable.on_switch);
        }
        setMsgRecTypeLabel(mMsgRecFlag);
    }

    private void setMsgRecTypeLabel(int flag) {
        switch (flag) {
            case YWProfileSettingsConstants.TRIBE_MSG_REC:
                mTribeMsgRecType.setText("接收并提醒");
                break;
            case YWProfileSettingsConstants.TRIBE_MSG_REC_NOT_REMIND:
                mTribeMsgRecType.setText("接收不提醒");
                break;
            case YWProfileSettingsConstants.TRIBE_MSG_REJ:
                mTribeMsgRecType.setText("不接收");
                break;
        }
    }

    private void setMsgRecType(int msgRecType) {
        switch (msgRecType) {
            case YWProfileSettingsConstants.TRIBE_MSG_REC:
                mTribeService.unblockTribe(mTribe, new TribeMsgRecSetCallback());
                break;
            case YWProfileSettingsConstants.TRIBE_MSG_REJ:
                mTribeService.blockTribe(mTribe, new TribeMsgRecSetCallback());
                break;
            case YWProfileSettingsConstants.TRIBE_MSG_REC_NOT_REMIND:
                mTribeService.receiveNotAlertTribeMsg(mTribe, new TribeMsgRecSetCallback());
                break;
        }
    }

    private void setAtMsgRecType(int atFlag) {
        switch (atFlag) {
            case YWProfileSettingsConstants.TRIBE_AT_MSG_REC:
                mTribeService.unblockAtMessage(mTribe, new TribeMsgRecSetCallback());
                break;
            case YWProfileSettingsConstants.TRIBE_AT_MSG_REJ:
                mTribeService.blockAtMessage(mTribe, new TribeMsgRecSetCallback());
                break;
        }
    }

    private void updateView() {
        mTribeName.setText(mTribe.getTribeName());
        IYWContact master = mTribe.getTribeMaster();
        if (master != null) {
            mTribeMaster.setText(master.getUserId());
        }
        mTribeDesc.setText(mTribe.getTribeNotice());
        mMyTribeNick.setText(getLoginUserTribeNick());
        if (mTribeMemberCount > 0) {
            mMemberCount.setText(mTribeMemberCount + "人");
        }
        mTribeCheckMode.setText(mTribe.getTribeCheckMode().description);
        initMsgRecFlags();
        if(getLoginUserRole() == YWTribeRole.TRIBE_MANAGER.type || getLoginUserRole() == YWTribeRole.TRIBE_HOST.type) {
            //enableAtAllLayout.setVisibility(View.VISIBLE);
            if(mTribe.isEnableAtAll()) {
                enableAtAllSwitch.setImageResource(R.drawable.on_switch);
            } else {
                enableAtAllSwitch.setImageResource(R.drawable.off_switch);
            }
        } else {
            enableAtAllLayout.setVisibility(View.GONE);
        }
        if (getLoginUserRole() == YWTribeRole.TRIBE_HOST.type) {
            mMangeTribeMembers.setText("群成员管理");
            //mEditTribeInfoLayout.setVisibility(View.VISIBLE);
            mQuiteTribe.setText("解散群");
            mQuiteTribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog myDialog = new AlertDialog.Builder(OpenimTribeInfoActivity.this,AlertDialog.THEME_HOLO_LIGHT).create();
                    myDialog.show();
                    myDialog.getWindow().setContentView(R.layout.activity_dialog_confirm);
                    Button butselect= (Button)myDialog.getWindow().findViewById(R.id.butselect);
                    TextView dialog_msg=(TextView)myDialog.getWindow().findViewById(R.id.dialog_msg);
                    dialog_msg.setText("确定解散群吗？");
                    butselect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                            operateTribeCallBack(mTribe.getTribeId()+"","3");
                        }
                    });

                    Button butcacel= (Button)myDialog.getWindow().findViewById(R.id.butcancel) ;
                    butcacel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });

                    RelativeLayout imageViewClose= (RelativeLayout)myDialog.getWindow().findViewById(R.id.layoutClose) ;
                    imageViewClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });


                    /*
                    IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "没权限");
                    if(false) {
                        mTribeService.disbandTribe(new IWxCallback() {
                            @Override
                            public void onSuccess(Object... result) {
                                YWLog.i(TAG, "解散群成功！");
                                IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "解散群成功！");
                                openTribeListFragment();
                            }

                            @Override
                            public void onError(int code, String info) {
                                YWLog.i(TAG, "解散群失败， code = " + code + ", info = " + info);
                                IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "解散群失败, code = " + code + ", info = " + info);
                            }

                            @Override
                            public void onProgress(int progress) {

                            }
                        }, mTribeId);
                    }
                    */
                }
            });
        } else {
            mMangeTribeMembers.setText("群成员列表");
            mEditTribeInfoLayout.setVisibility(View.GONE);
            mQuiteTribe.setText("退出群");
            mQuiteTribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog myDialog = new AlertDialog.Builder(OpenimTribeInfoActivity.this,AlertDialog.THEME_HOLO_LIGHT).create();
                    myDialog.show();
                    myDialog.getWindow().setContentView(R.layout.activity_dialog_confirm);
                    Button butselect= (Button)myDialog.getWindow().findViewById(R.id.butselect);
                    TextView dialog_msg=(TextView)myDialog.getWindow().findViewById(R.id.dialog_msg);
                    dialog_msg.setText("确定退出群吗？");
                    butselect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                            operateTribeCallBack(mTribe.getTribeId()+"","2");
                        }
                    });

                    Button butcacel= (Button)myDialog.getWindow().findViewById(R.id.butcancel) ;
                    butcacel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });

                    RelativeLayout imageViewClose= (RelativeLayout)myDialog.getWindow().findViewById(R.id.layoutClose) ;
                    imageViewClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });

                }
            });

            /*
            mQuiteTribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTribeService.exitFromTribe(new IWxCallback() {
                        @Override
                        public void onSuccess(Object... result) {
                            YWLog.i(TAG, "退出群成功！");
                            IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "退出群成功！");
                            openTribeListFragment();
                        }

                        @Override
                        public void onError(int code, String info) {
                            YWLog.i(TAG, "退出群失败， code = " + code + ", info = " + info);
                            IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "退出群失败, code = " + code + ", info = " + info);
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    }, mTribeId);
                }
            });
            */
        }

        if (!TextUtils.isEmpty(mTribeOp)) {
            mMangeTribeMembersLayout.setVisibility(View.GONE);
            mEditTribeInfoLayout.setVisibility(View.GONE);
            mQuiteTribe.setText("加入群");
            mQuiteTribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTribeService.joinTribe(new IWxCallback() {
                        @Override
                        public void onSuccess(Object... result) {
                            if (result != null && result.length > 0) {
                                Integer retCode = (Integer) result[0];
                                if (retCode == 0) {
                                    YWLog.i(TAG, "加入群成功！");
                                    IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "加入群成功！");
                                    mTribeOp = null;
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateView();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onError(int code, String info) {
                            YWLog.i(TAG, "加入群失败， code = " + code + ", info = " + info);
                            IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "加入群失败, code = " + code + ", info = " + info);
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    }, mTribeId);
                }
            });
        } else {
            if (getLoginUserRole() == YWTribeRole.TRIBE_MEMBER.type) {
                mMangeTribeMembersLayout.setVisibility(View.VISIBLE);
                mEditTribeInfoLayout.setVisibility(View.GONE);
            } else {
                mMangeTribeMembersLayout.setVisibility(View.VISIBLE);
                //mEditTribeInfoLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateTribeMemberCount() {
        if (mTribeMemberCount > 0) {
            mMemberCount.setText(mTribeMemberCount + "人");
        }
    }

    private void openTribeListFragment() {
        //Intent intent = new Intent(this, OpenimFragmentTabs.class);
        //intent.putExtra(TribeConstants.TRIBE_OP, TribeConstants.TRIBE_OP);
        //startActivity(intent);
        finish();
    }

    private void initTribeInfo() {
        mTribe = mTribeService.getTribe(mTribeId);
        mTribeService.addTribeListener(mTribeChangedListener);
        initTribeMemberList();
        getTribeInfoFromServer();
    }

    private void getTribeInfoFromServer() {
        mTribeService.getTribeFromServer(new IWxCallback() {
            @Override
            public void onSuccess(final Object... result) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTribe = (YWTribe)result[0];
                        mTribeMemberCount = mTribe.getMemberCount();
                        updateView();
                    }
                });
            }

            @Override
            public void onError(int code, String info) {

            }

            @Override
            public void onProgress(int progress) {

            }
        }, mTribeId);
    }

    private void initTribeMemberList() {
        getTribeMembersFromLocal();
        getTribeMembersFromServer();
    }

    private void getTribeMembersFromLocal() {
        mTribeService.getMembers(new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                mList.clear();
                mList.addAll((List<YWTribeMember>) result[0]);
                if (mList != null || mList.size() > 0) {
                    mTribeMemberCount = mList.size();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            initLoginUser();
                            updateView();
                        }
                    });
                }
            }

            @Override
            public void onError(int code, String info) {

            }

            @Override
            public void onProgress(int progress) {

            }
        }, mTribeId);
    }

    private void getTribeMembersFromServer() {
        mTribeService.getMembersFromServer(new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                mList.clear();
                mList.addAll((List<YWTribeMember>) result[0]);
                if (mList != null || mList.size() > 0) {
                    mTribeMemberCount = mList.size();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            initLoginUser();
                            updateView();
                        }
                    });
                }
            }

            @Override
            public void onError(int code, String info) {
                IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "error, code = " + code + ", info = " + info);
            }

            @Override
            public void onProgress(int progress) {

            }
        }, mTribeId);
    }

    private void initLoginUser(){
        String loginUser = mIMKit.getIMCore().getLoginUserId();
        for (YWTribeMember member : mList) {
            if (member.getUserId().equals(loginUser)) {
                mLoginUser = member;
                break;
            }
        }
    }

    /**
     * 判断当前登录用户在群组中的身份
     *
     * @return
     */
    private int getLoginUserRole() {
        if(mTribe.getTribeRole() == null)
            return YWTribeRole.TRIBE_MEMBER.type;
        return mTribe.getTribeRole().type;
    }

    /**
     * 获取登录用户的群昵称
     *
     * @return
     */
    private String getLoginUserTribeNick() {
        if (mLoginUser != null && !TextUtils.isEmpty(mLoginUser.getTribeNick())) {
            return mLoginUser.getTribeNick();
        }
        String tribeNick = null;
        IYWContactService contactService = mIMKit.getContactService();
        IYWContact contact = contactService.getContactProfileInfo(mIMKit.getIMCore().getLoginUserId(), mIMKit.getIMCore().getAppKey());
        if (contact != null){
            if (!TextUtils.isEmpty(contact.getShowName())){
                tribeNick = contact.getShowName();
            } else {
                tribeNick =  contact.getUserId();
            }
        }
        if(TextUtils.isEmpty(tribeNick)) {
            tribeNick = mIMKit.getIMCore().getLoginUserId();
        }
        return tribeNick;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTribeService.removeTribeListener(mTribeChangedListener);
    }

    /**
     *  群改变监听
     */
    private void initTribeChangedListener() {
        mTribeChangedListener = new IYWTribeChangeListener() {
            @Override
            public void onInvite(YWTribe tribe, YWTribeMember user) {

            }

            @Override
            public void onUserJoin(YWTribe tribe, YWTribeMember user) {
                mTribeMemberCount = tribe.getMemberCount();
                if(mIMKit.getIMCore().getLoginUserId().equals(user.getUserId())) {
                    getTribeInfoFromServer();
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateTribeMemberCount();
                        }
                    });
                }
            }

            @Override
            public void onUserQuit(YWTribe tribe, YWTribeMember user) {
                mTribeMemberCount = tribe.getMemberCount();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateTribeMemberCount();
                    }
                });
            }

            @Override
            public void onUserRemoved(YWTribe tribe, YWTribeMember user) {
                openTribeListFragment();
            }

            @Override
            public void onTribeDestroyed(YWTribe tribe) {
                openTribeListFragment();
            }

            @Override
            public void onTribeInfoUpdated(YWTribe tribe) {
                mTribe = tribe;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateView();
                    }
                });
            }

            @Override
            public void onTribeManagerChanged(YWTribe tribe, YWTribeMember user) {
                String loginUser = mIMKit.getIMCore().getLoginUserId();
                if (loginUser.equals(user.getUserId())) {
                    for (YWTribeMember member : mList) {
                        if (member.getUserId().equals(loginUser)) {
                            mList.remove(member);
                            mList.add(user);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateView();
                                }
                            });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onTribeRoleChanged(YWTribe tribe, YWTribeMember user) {
                String loginUser = mIMKit.getIMCore().getLoginUserId();
                if (loginUser.equals(user.getUserId())) {
                    for (YWTribeMember member : mList) {
                        if (member.getUserId().equals(loginUser)) {
                            mList.remove(member);
                            mList.add(user);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateView();
                                }
                            });
                            break;
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDIT_MY_TRIBE_NICK_REQUEST_CODE) {
                if (data != null) {
                    String newUserNick = data.getStringExtra(TribeConstants.TRIBE_NICK);
                    mMyTribeNick.setText(newUserNick);
                }
            } else if (requestCode == SET_MSG_REC_TYPE_REQUEST_CODE) {
                int flag = data.getIntExtra("Flag", mTribe.getMsgRecType());
                setMsgRecType(flag);
            } else if (requestCode == SET_TRIBE_CHECK_MODE_REQUEST_CODE){
                final int checkMode = resultCode;
                mTribeCheckMode.setText(YWTribeCheckMode.getEnumType(checkMode).description);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取群消息接收状态设置值
     */
    private void getTribeMsgRecSettings() {
        if (mTribe == null){
            WxLog.w(TAG, "getTribeMsgRecSettings mTribe is null");
            return;
        }
        List<Long> list = new ArrayList<Long>();
        list.add(mTribe.getTribeId());
        mTribeService.getTribesMsgRecSettingsFromServer(
                list,
                10,
                new IWxCallback() {
                    @Override
                    public void onSuccess(Object... result) {
                        if (result != null && result.length > 0) {
                            ArrayList<YWTribeSettingsModel> models = (ArrayList<YWTribeSettingsModel>) result[0];
                            YWTribeSettingsModel model = models.get(0);
                            if(model.getTribeId() == mTribe.getTribeId()) {
                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        initMsgRecFlags();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(int code, String info) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                initMsgRecFlags();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }
        );
    }

    class TribeMsgRecSetCallback implements IWxCallback {

        private Handler uiHandler = new Handler(Looper.getMainLooper());

        public TribeMsgRecSetCallback() {

        }

        @Override
        public void onError(final int code, final String info) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    IMNotificationUtils.getInstance().showToast("设置失败: code:" + code + " info:" + info, OpenimTribeInfoActivity.this);
                }
            });
        }

        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onSuccess(Object... result) {
            mMsgRecFlag = mTribe.getMsgRecType();
            mAtMsgRecFlag = mTribe.getAtFlag();
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    initMsgRecFlags();
                    IMNotificationUtils.getInstance().showToast("设置成功: atFlag:" + mTribe.getAtFlag() + " flag:" + mTribe.getMsgRecType(), OpenimTribeInfoActivity.this);
                }
            });
        }
    }

    protected void clearMsgRecord() {
        String message = "清空的消息再次漫游时不会出现。你确定要清空聊天消息吗？";
//        if (mConversation.getConversationType() == ConversationType.WxConversationType.Room) {
//            message = getResources().getString(
//                    R.string.clear_roomchatting_msg_confirm);
//        }
        AlertDialog.Builder builder = new WxAlertDialog.Builder(OpenimTribeInfoActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                conversation.getMessageLoader().deleteAllMessage();
                                IMNotificationUtils.getInstance().showToast("记录已清空",
                                        OpenimTribeInfoActivity.this);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
    }

    private class ConfigAtAllCallback implements IWxCallback {
        @Override
        public void onSuccess(Object... result) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "设置成功：" + mTribe.isEnableAtAll());
                    if(mTribe.isEnableAtAll()) {
                        enableAtAllSwitch.setImageResource(R.drawable.on_switch);
                    } else {
                        enableAtAllSwitch.setImageResource(R.drawable.off_switch);
                    }
                }
            });
        }

        @Override
        public void onError(int code, String info) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, "设置失败：" + mTribe.isEnableAtAll());
                }
            });
        }

        @Override
        public void onProgress(int progress) {

        }
    }

    //=============================群请求回调======================================

    /**
     * 群申请成功请求回调
     * @param groupNo
     */
    private void  operateTribeCallBack(String groupNo,String type){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accountId", Config.IMUserId);
        params.put("imGroupNo", groupNo);
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        params.put("type",type);
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
            IMNotificationUtils.getInstance().showToast(OpenimTribeInfoActivity.this, msg.getString("info"));
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}