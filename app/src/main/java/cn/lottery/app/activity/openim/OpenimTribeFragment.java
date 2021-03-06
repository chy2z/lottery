package cn.lottery.app.activity.openim;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.fundamental.widget.refreshlist.YWPullToRefreshBase;
import com.alibaba.mobileim.fundamental.widget.refreshlist.YWPullToRefreshListView;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeType;
import com.alibaba.mobileim.tribe.IYWTribeService;
import com.alibaba.mobileim.utility.ResourceLoader;
import java.util.ArrayList;
import java.util.List;
import cn.lottery.R;
import cn.lottery.app.activity.openim.tribe.OpenimEditTribeInfoActivity;
import cn.lottery.app.activity.openim.tribe.OpenimSearchTribeActivity;
import cn.lottery.app.activity.openim.tribe.OpenimTribeAdapterSample;
import cn.lottery.app.activity.openim.tribe.OpenimTribeAndRoomList;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.openim.tribe.OpenimTribeSampleHelper;
import cn.lottery.framework.openim.tribe.TribeConstants;

/**
 * 踪迹群组
 * Created by admin on 2017/4/25.
 */
public class OpenimTribeFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    protected static final long POST_DELAYED_TIME = 300;
    private static final String TAG = "TribeFragment";
    private final Handler handler = new Handler();


    private IYWTribeService mTribeService;
    private ListView mMessageListView;      // 消息列表视图
    private OpenimTribeAdapterSample adapter;
    private int max_visible_item_count = 0; // 当前页面列表最多显示个数
    private OpenimTribeAndRoomList mTribeAndRoomList;
    private List<YWTribe> mList;      //群组和讨论组的所有数据
    private List<YWTribe> mTribeList; //群组
    private List<YWTribe> mRoomsList; //讨论组

    private PopupWindow mPopupBackground;
    private PopupWindow mPopupWindow;

    private View mView;
    private YWIMKit mIMKit;
    private Activity mContext;
    private String mUserId;
    private View mProgress;
    private YWPullToRefreshListView mPullToRefreshListView;

    public IYWTribeService getTribeService() {
        mIMKit = OpenIMLoginHelper.getInstance().getIMKit();
        mTribeService = mIMKit.getTribeService();
        return mTribeService;
    }

    private Runnable cancelRefresh = new Runnable() {
        @Override
        public void run() {
            if (mPullToRefreshListView != null) {
                mPullToRefreshListView.onRefreshComplete(false, false,
                        R.string.aliwx_sync_failed);
            }
        }
    };

    public OpenimTribeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
            return mView;
        }
        mContext = getActivity();
        mView = inflater.inflate(R.layout.activity_openim_fragment_tribe, container, false);
        mProgress = mView.findViewById(R.id.progress);
        mIMKit = OpenIMLoginHelper.getInstance().getIMKit();
        mUserId = mIMKit.getIMCore().getLoginUserId();
        if (TextUtils.isEmpty(mUserId)) {
            YWLog.i(TAG, "user not login");
        }
        mTribeService = mIMKit.getTribeService();
        init();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initTribeListView(); //绑定适配器
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (!TextUtils.isEmpty(intent.getStringExtra(TribeConstants.TRIBE_OP))) {
                mList = mTribeService.getAllTribes(); //获取群组数据
                updateTribeList(); //跟新列表
            }
        }
    }

    protected void init() {
        mContext.getWindow().setWindowAnimations(0);
        initTitle();
        mList = new ArrayList<YWTribe>();
        mTribeList = new ArrayList<YWTribe>();
        mRoomsList = new ArrayList<YWTribe>();
        mTribeAndRoomList = new OpenimTribeAndRoomList(mTribeList, mRoomsList); //群组和讨论组合并在一起
        adapter = new OpenimTribeAdapterSample(mContext, mTribeAndRoomList); //适配器
        mPullToRefreshListView = (YWPullToRefreshListView) mView.findViewById(R.id.message_list);
        mMessageListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setMode(YWPullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mPullToRefreshListView.setShowIndicator(false);
        mPullToRefreshListView.setDisableScrollingWhileRefreshing(false);
        mPullToRefreshListView.setRefreshingLabel("同步群组");
        mPullToRefreshListView.setReleaseLabel("松开同步群组");
        //mPullToRefreshListView.resetHeadLayout();
        mPullToRefreshListView.setDisableRefresh(false);
        mPullToRefreshListView.setOnRefreshListener(new YWPullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.removeCallbacks(cancelRefresh);
                        IYWTribeService tribeService = OpenimTribeSampleHelper.getTribeService();
                        if (tribeService != null) {
                            tribeService.getAllTribesFromServer(new IWxCallback() {
                                @Override
                                public void onSuccess(Object... arg0) {
                                    // 返回值为列表
                                    mList.clear();
                                    mList.addAll((ArrayList<YWTribe>) arg0[0]);
                                    if (mList.size() > 0) {
                                        mTribeList.clear();
                                        mRoomsList.clear();
                                        for (YWTribe tribe : mList) {
                                            if (tribe.getTribeType() == YWTribeType.CHATTING_TRIBE) {
                                                mTribeList.add(tribe);
                                            } else {
                                                mRoomsList.add(tribe);
                                            }
                                        }
                                    }
                                    mPullToRefreshListView.onRefreshComplete(false, true, R.string.aliwx_sync_success);
                                    refreshAdapter();
                                }

                                @Override
                                public void onError(int code, String info) {
                                    mPullToRefreshListView.onRefreshComplete(false, false,
                                            R.string.aliwx_sync_failed);
                                }

                                @Override
                                public void onProgress(int progress) {

                                }
                            });
                        }
                    }
                }, POST_DELAYED_TIME);
            }
        });
    }

    private void initTitle() {
        //隐藏阴影
        View titleBarShadow = (View) mView.findViewById(R.id.title_bar_shadow_view);
        titleBarShadow.setVisibility(View.GONE);

        //隐藏标题
        RelativeLayout titleBar = (RelativeLayout) mView.findViewById(R.id.title_bar);
        TextView titleView = (TextView) mView.findViewById(R.id.title_self_title);
        TextView leftButton = (TextView) mView.findViewById(R.id.left_button);
        leftButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        leftButton.setPadding(10, 0, 0, 0);
        leftButton.setText("搜索加入群");
        leftButton.setTextColor(getResources().getColor(R.color.aliwx_white));
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OpenimSearchTribeActivity.class);
                startActivity(intent);
            }
        });

        //定义标题背景
        titleBar.setBackgroundColor(Color.parseColor("#f4bc1a")); //#00b4ff
        titleView.setTextColor(Color.WHITE);
        titleView.setText("群组");

        titleBar.setVisibility(View.VISIBLE);

        TextView rightButton = (TextView) mView.findViewById(ResourceLoader.getIdByName(mContext, "id", "right_button"));
        rightButton.setText("创建群");
        rightButton.setTextColor(getResources().getColor(R.color.aliwx_white));
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    /**
     * 弹出要创建的群类型选项
     *
     * @param v
     */
    private void showPopupMenu(View v) {
        final View bgView = View.inflate(SApplication.getContext(), R.layout.openim_popup_window_bg, null);
        bgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopupWindow();
            }
        });
        if (mPopupBackground == null) {
            mPopupBackground = new PopupWindow(bgView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        //显示弹出背景
        mPopupBackground.showAtLocation(v, Gravity.BOTTOM, 0, 0);

        View view = View.inflate(SApplication.getContext(), R.layout.openim_popup_menu, null);
        //创建群组
        TextView tribe = (TextView) view.findViewById(R.id.create_tribe);
        tribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopupWindow();
                Intent intent = new Intent(getActivity(), OpenimEditTribeInfoActivity.class);
                intent.putExtra(TribeConstants.TRIBE_OP, TribeConstants.TRIBE_CREATE);
                intent.putExtra(TribeConstants.TRIBE_TYPE, YWTribeType.CHATTING_TRIBE.toString());
                startActivityForResult(intent, 0);
            }
        });

        //创建讨论组
        TextView room = (TextView) view.findViewById(R.id.create_room);
        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopupWindow();
                Intent intent = new Intent(getActivity(), OpenimEditTribeInfoActivity.class);
                intent.putExtra(TribeConstants.TRIBE_OP, TribeConstants.TRIBE_CREATE);
                intent.putExtra(TribeConstants.TRIBE_TYPE, YWTribeType.CHATTING_GROUP.toString());
                startActivityForResult(intent, 0);
            }
        });

        TextView cancel = (TextView) view.findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopupWindow();
            }
        });


        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        //显示弹出菜单
        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    private void hidePopupWindow() {
        if (mPopupBackground != null) {
            mPopupBackground.dismiss();
        }
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 初始化群列表行为
     */
    private void initTribeListView() {
        if (mMessageListView != null) {
            mMessageListView.setAdapter(adapter);
            mMessageListView.setOnItemClickListener(this);
            mMessageListView.setOnScrollListener(this);
        }

        getAllTribesFromServer();
    }

    /**
     *获取群和讨论组信息
     */
    private void getAllTribesFromServer() {
        getTribeService().getAllTribesFromServer(new IWxCallback() {
            @Override
            public void onSuccess(Object... arg0) {
                // 返回值为列表
                mList.clear();
                mList.addAll((ArrayList<YWTribe>) arg0[0]);
                updateTribeList();
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onError(int code, String info) {
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    /**
     * 更新集合数据
     */
    private void updateTribeList() {
        mTribeList.clear();
        mRoomsList.clear();
        if (mList.size() > 0) {
            for (YWTribe tribe : mList) {
                if (tribe.getTribeType() == YWTribeType.CHATTING_TRIBE) {
                    mTribeList.add(tribe);
                } else {
                    mRoomsList.add(tribe);
                }
            }
        }
        refreshAdapter();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position1,
                            long id) {
        final int position = position1 - mMessageListView.getHeaderViewsCount();
        if (position >= 0 && position < mTribeAndRoomList.size()) {

            YWTribe tribe = (YWTribe) mTribeAndRoomList.getItem(position);
            YWIMKit imKit = OpenIMLoginHelper.getInstance().getIMKit();
            //参数为群ID号
            Intent intent = imKit.getTribeChattingActivityIntent(tribe.getTribeId());
            startActivity(intent);
        }
    }

    /**
     * 刷新当前列表
     */
    private void refreshAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChangedWithAsyncLoad();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE && adapter != null) {
            adapter.loadAsyncTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        max_visible_item_count = visibleItemCount > max_visible_item_count ? visibleItemCount
                : max_visible_item_count;
        if (adapter != null) {
            adapter.setMax_visible_item_count(max_visible_item_count);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mList = mTribeService.getAllTribes();
        updateTribeList();
    }
}
