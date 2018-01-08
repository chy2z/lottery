package cn.lottery.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.app.activity.myself.MyActivity;
import cn.lottery.app.activity.trace.TraceActivity;
import cn.lottery.app.activity.trace.TraceDynamicDetailActivity;
import cn.lottery.app.activity.web.WebPageActivity;
import cn.lottery.app.adapter.GVMenuAdapter;
import cn.lottery.app.adapter.HomePageDynamicListViewAdapter;
import cn.lottery.app.fragment.ImageLoadFragment;
import cn.lottery.app.adapter.ViewPageFragmentAdapter;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.openim.NotificationInitSampleHelper;
import cn.lottery.framework.openim.OpenIMLoginHelper;
import cn.lottery.framework.openim.UserProfileSampleHelper;
import cn.lottery.framework.widget.listview.NoScrollListView;
import cn.lottery.framework.widget.scrollView.BottomScrollView;
import cn.lottery.framework.widget.viewpagerindicator.CirclePageIndicator;

import com.alibaba.mobileim.YWChannel;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.login.YWLoginCode;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


/**
 * 主页面
 * Created by admin on 2017/6/3.
 */
public class HomePageActivity extends BaseActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener {

    private static final String TAG = HomePageActivity.class.getSimpleName();

    private LinearLayout layoutMy, layoutTrace, layoutHomepage;

    private ViewPager mViewPager;

    private ViewPageFragmentAdapter vpmAdapter;

    private SwipeRefreshLayout mSwipeLayout;

    private BottomScrollView scrollViewParent;

    private CirclePageIndicator indicator;

    private JSONArray bannerList,menuList,resultList;

    private ArrayList<Fragment> fragmentsaArrayList;

    private String[] CONTENT=null;

    private ArrayList<HashMap<String, Object>> dataGV = new ArrayList<HashMap<String, Object>>();

    private ArrayList<HashMap<String, Object>> dataLV = new ArrayList<HashMap<String, Object>>();

    private NoScrollListView listViewBottom;

    private HomePageDynamicListViewAdapter lvAdapter;

    private GridView gvMenu;

    private GVMenuAdapter gvAdapter;

    private LocationClient mLocationClient = null;

    private BDLocationListener myListener = new MyLocationListener();

    private TextView dyTv;

    private Button dyWatch;

    private ImageView dyImg;

    private EditText dyComment;

    private int dyPosition;

    private int locationCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_homepage);

        Config.isActive++;

        Config.mainActivity = this;

        if(Config.userToken.equals("")) Config.isLogin=false;
        else{
            Config.isLogin=true;
            openIMLogin();
            requestFindUserInfo();
            if(Config.deviceToken.equals("")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        while (Config.deviceToken.equals("")) {
                            loadDeviceToken();
                        }
                    }
                }, 2000);
            }
            else{
                requestEditUUID();
            }
        }

        initLoading();

        initView();

        initBaiDuLocation();

        requestHomeData();

        //toast("w:"+ScreenUtils.getScreenWidth(this)+",h:"+ScreenUtils.getScreenHeight(this)+",densityDpi:"+ScreenUtils.getScreenDPI(this)+",density"+ScreenUtils.getDensity(this));

        //延迟加载动态信息数据
        new Thread(new DelayThread()).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭百度定位
        if(mLocationClient!=null) {
            mLocationClient.stop();
        }
        if (Config.isActive >= 1) {
            Config.isActive--;
        }
        outOpenIMlogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Config.isLogin) {
            if(!Config.userToken.equals("")) {
                Config.isLogin=true;
                toast("重新加载动态数据");
                onRefresh();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            exitApplication(this, true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2&&resultCode==2){
            if(data.getStringExtra("data").equals("delDynamic")) {
                lvAdapter.getData().remove(data.getIntExtra("positon",-1));
                lvAdapter.notifyDataSetChanged();
            }
            else if(data.getStringExtra("data").equals("EditDynamic")) {
                if(Config.traceDynamDetail!=null) {
                    lvAdapter.getData().set(data.getIntExtra("positon", -1), Config.traceDynamDetail);
                    lvAdapter.notifyDataSetChanged();
                    Config.traceDynamDetail=null;
                }
            }
        }
    }

    private void initLoading(){
        Intent intent = new Intent(getBaseContext(), MaskingLayerActivity.class);
        startActivity(intent);
    }

    private void initView() {
        layoutMy = (LinearLayout) findViewById(R.id.layoutMy);
        layoutMy.setOnClickListener(this);

        layoutTrace = (LinearLayout) findViewById(R.id.layoutTrace);
        layoutTrace.setOnClickListener(this);

        layoutHomepage = (LinearLayout) findViewById(R.id.layoutHomepage);
        layoutHomepage.setOnClickListener(this);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);

        indicator = (CirclePageIndicator) findViewById(R.id.indicator);

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        //mViewPager.getLayoutParams().height=(int)(ScreenUtils.getScreenWidth(getApplicationContext())*(240.0/360.0));

        // 菜单
        gvMenu = (GridView) findViewById(R.id.gvMenu);

        scrollViewParent=(BottomScrollView) findViewById(R.id.scrollViewParent);

        scrollViewParent.setOnScrollToBottomLintener(new BottomScrollView.OnScrollListener(){
            @Override
            public void onScrollListener(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
                hideInputMethod(HomePageActivity.this);
            }

            @Override
            public void onScrollBottomListener(int scrollX, int scrollY, boolean clampedX, boolean isBottom) {

            }
        });

        listViewBottom = (NoScrollListView) findViewById(R.id.listViewBottom);
    }

    private void initBaiDuLocation(){

        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());

        //注册监听函数
        mLocationClient.registerLocationListener( myListener );

        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span=1000;

        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutMy:
                startActivity(new Intent(this, MyActivity.class));
                break;
            case R.id.layoutTrace:
                startActivity(new Intent(this, TraceActivity.class));
                break;
        }
    }

    /**
     * girdview Item点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> adapt, View arg1, int index, long arg3) {
            HashMap<String, Object> item = (HashMap<String, Object>) adapt.getItemAtPosition(index);
            Intent intent = new Intent(HomePageActivity.this, WebPageActivity.class);
            intent.putExtra("httpURL", item.get("httpURL").toString());
            startActivity(intent);
    }

    /**
     * 刷新事件
     */
    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(0X110, 2000);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0X110:
                    mSwipeLayout.setRefreshing(false);
                    dataLV.clear();
                    requestDynamic();
                    break;
            }
        };
    };

    /**
     * 请主页面数据
     */
    private void requestHomeData() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type","1");
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.getHomeData, params, null,"requestHomeData");
    }


    /**
     * 请求用户信息
     */
    private void requestFindUserInfo() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        post(Config.URL_PREFIX + RequestUrl.findUserInfo, params, null,"requestFindUserInfo");
    }


    /**
     * 更新友盟信息
     */
    private void requestEditUUID(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", Config.customerID);
        params.put("uuid", Config.UUID);
        params.put("deviceToken", Config.deviceToken);
        params.put("type", "2");
        post(Config.URL_PREFIX + RequestUrl.editUUID, params, null,"requestEditUUID");
    }

    /**
     * 获取附近动态信息
     */
    private void requestDynamic(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lat",Config.gpslat+"");
        params.put("lng",Config.gpslon+"");
        params.put("userId",(Config.userToken.equals("")?"":Config.customerID));
        post(Config.URL_PREFIX + RequestUrl.getNearByDynamic, params, null,"requestDynamic");
    }

    /**
     * 增加回复
     */
    private void requestAddReComment(String content, String dynamicId, String commentUserId, String userId, String user_token, String uuid){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("content",content);
        params.put("dynamicId",dynamicId);
        params.put("commentUserId",commentUserId);
        params.put("userId",userId);
        params.put("user_token",user_token);
        params.put("uuid",uuid);
        post(Config.URL_PREFIX + RequestUrl.addReComment, params, null,"requestAddReComment");
    }

    /**
    * 增加评论
    */
    private void requestAddComment(String content, String dynamicId, String userId, String user_token){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("content",content);
        params.put("dynamicId",dynamicId);
        params.put("userId",userId);
        params.put("user_token",user_token);
        post(Config.URL_PREFIX + RequestUrl.addComment, params, null,"requestAddComment");
    }

    /**
     * 赞
     */
    private void requestAddLike(String dynamicId, String userId, String user_token){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("dynamicId",dynamicId);
        params.put("userId",userId);
        params.put("user_token",user_token);
        post(Config.URL_PREFIX + RequestUrl.addLike, params, null,"requestAddLike");
    }

    /**
     * 关注
     */
    private void  requestAddWatch (String dynamicId, String userId, String user_token){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("dynamicId",dynamicId);
        params.put("userId",userId);
        params.put("user_token",user_token);
        post(Config.URL_PREFIX + RequestUrl.addWatch, params, null,"requestAddWatch");
    }

    @Override
    protected void handError(Object tag, String errorInfo) {

    }

    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestHomeData")) {
            closeDialog();
            processGetHomeData(obj);
        }
        else if (tag.equals("requestDynamic")) {
            processDynamic(obj);
        }
        else  if (tag.equals("requestAddReComment")) {
            processAddReComment(obj);
        } else  if (tag.equals("requestAddComment")) {
            processAddComment(obj);
        }
        else if(tag.equals("requestAddLike")){
            processAddLike(obj);
        } else if(tag.equals("requestAddWatch")){
            processAddWatch(obj);
        }
        else  if (tag.equals("requestFindUserInfo")) {
            processFindUserInfo(obj);
        }
        else if(tag.equals("requestEditUUID")){
            processEditUUID(obj);
        }
    }

    /**
     * 处理主页数据
     * @param json
     */
    private void processGetHomeData(JSONObject json) {
        try {

            JSONObject jobj = null;

            bannerList = json.getJSONArray("bannerList");

            CONTENT = new String[bannerList.length()];

            fragmentsaArrayList = new ArrayList<Fragment>();

            for (int i = 0; i < bannerList.length(); i++) {
                jobj = bannerList.getJSONObject(i);
                CONTENT[i] = jobj.getString("url");
                fragmentsaArrayList.add(new ImageLoadFragment(app
                        .getImageLoader(),jobj.getString("img"), jobj.getString("url"),i,true, ImageView.ScaleType.FIT_XY,false));
            }

            vpmAdapter = new ViewPageFragmentAdapter(getSupportFragmentManager(),
                    fragmentsaArrayList, CONTENT);

            mViewPager.setAdapter(vpmAdapter);

            indicator.setViewPager(mViewPager);


            //===========================================

            menuList=json.getJSONArray("menuList");

            HashMap<String, Object> hashMap = null;

            for (int i = 0; i < menuList.length(); i++) {
                hashMap = new HashMap<String, Object>();
                jobj = menuList.getJSONObject(i);
                hashMap.put("httpURL", jobj.getString("menuUrl"));
                hashMap.put("Name", jobj.getString("menuName"));
                hashMap.put("imgURL",jobj.getString("img"));
                dataGV.add(hashMap);
            }

            gvAdapter = new GVMenuAdapter(this, dataGV,app.getImageLoader());

            gvMenu.setAdapter(gvAdapter);

            gvMenu.setOnItemClickListener(this);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取用户业务处理
     * @param json
     */
    private void processFindUserInfo(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            if(msg.getBoolean("success")) {
                JSONObject user = json.getJSONObject("user");
                Config.status=user.getString("status");
                Config.icon=user.getString("img");
            }
            else{
                toast("获取用户出错！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新友盟信息
     * @param json
     */
    private  void processEditUUID(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            if(msg.getBoolean("success")) {
                //toast("更新友盟信息成功！");
            }
            else{
                toast("更新友盟信息出错！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态数据
     * @param json
     */
    private void processDynamic(JSONObject json) {
        try {

            JSONObject jobj = null;

            resultList = json.getJSONArray("resultList");

            HashMap<String, Object> hashMap = null;

            String imgUrlAll="";

            for (int i = 0; i < resultList.length(); i++) {

                hashMap = new HashMap<String, Object>();

                jobj = resultList.getJSONObject(i);

                imgUrlAll="";

                hashMap.put("id", jobj.getString("id"));
                hashMap.put("userId", jobj.getString("userId"));
                hashMap.put("accountId", jobj.getString("accountId"));

                hashMap.put("content", jobj.getString("content"));
                hashMap.put("createTime", jobj.getString("createTime"));
                hashMap.put("nickName", jobj.getString("nickName"));
                hashMap.put("userImg",jobj.getString("userImg"));
                hashMap.put("address",jobj.getString("address"));
                hashMap.put("likeCount",jobj.getString("likeCount"));

                hashMap.put("isLike",jobj.getBoolean("isLike"));
                hashMap.put("watchStatus",jobj.getString("watchStatus"));

                JSONArray imges= jobj.getJSONArray("img");

                hashMap.put("contentImg1", "");
                hashMap.put("contentImg2", "");
                hashMap.put("contentImg3", "");
                hashMap.put("contentImg4", "");
                hashMap.put("contentImg5", "");
                hashMap.put("contentImg6", "");
                hashMap.put("contentImg7", "");
                hashMap.put("contentImg8", "");
                hashMap.put("contentImg9", "");
                hashMap.put("layoutimgRow1", "0");
                hashMap.put("layoutimgRow2", "0");
                hashMap.put("layoutimgRow3", "0");

                for (int j=0;j<imges.length();j++) {
                    hashMap.put("contentImg" + (j + 1), imges.getString(j));
                    if (j <= 2 && j >= 0) hashMap.put("layoutimgRow1", "1");
                    if (j <= 5 && j > 2) hashMap.put("layoutimgRow2", "1");
                    if (j > 5) hashMap.put("layoutimgRow3", "1");

                    if (j < imges.length() - 1) {
                        imgUrlAll += imges.getString(j) + ",";
                    } else imgUrlAll += imges.getString(j);
                }

                hashMap.put("imgUrlAll",imgUrlAll);

                JSONArray commentList= jobj.getJSONArray("commentList");

                hashMap.put("commentList",commentList);

                dataLV.add(hashMap);
            }

            lvAdapter = new HomePageDynamicListViewAdapter(this,HomePageDynamicListViewAdapter.Default_Type, dataLV,app.getImageLoader());

            lvAdapter.setCommentListener(new HomePageDynamicListViewAdapter.CommentListener() {
                /**
                 * 回复
                 * @param content
                 * @param dynamicId
                 * @param commentUserId
                 * @param userId
                 * @param user_token
                 * @param uuid
                 */
                @Override
                public void addReComment(int position, EditText comment, TextView tv, String content, String dynamicId, String commentUserId, String userId, String user_token, String uuid) {
                    //toast("content:"+content+",dynamicId:"+dynamicId+",commentUserId:"+commentUserId+",userId:"+userId+",user_token:"+user_token+",uuid:"+uuid);
                    dyPosition=position;
                    dyComment=comment;
                    dyTv=tv;
                    requestAddReComment(content,dynamicId,commentUserId,userId,user_token,uuid);
                }

                /**
                 * 评论
                 * @param content
                 * @param dynamicId
                 * @param userId
                 * @param user_token
                 */
                @Override
                public void addComment(int position,EditText comment,TextView tv,String content, String dynamicId, String userId, String user_token) {
                    //toast("content:"+content+",dynamicId:"+dynamicId+",userId:"+userId+",user_token:"+user_token);
                    dyPosition=position;
                    dyComment=comment;
                    dyTv=tv;
                    requestAddComment(content,dynamicId,userId,user_token);
                }

                @Override
                public void addLike(int position, ImageView img , TextView tv, String dynamicId, String userid, String user_token){
                    //toast("dynamicId:"+dynamicId+",userId:"+userid+",user_token:"+user_token);
                    dyPosition=position;
                    dyImg=img;
                    dyTv=tv;
                    requestAddLike(dynamicId,userid,user_token);
                }

                @Override
                public void addWatch(int position, Button watch, String dynamicId, String userid, String user_token){
                    //toast("dynamicId:"+dynamicId+",userId:"+userid+",user_token:"+user_token);
                    dyPosition=position;
                    dyWatch=watch;
                    requestAddWatch(dynamicId,userid,user_token);
                }

                @Override
                public void showDetail(int position,String dynamicId,String userid, String user_token){
                    Intent it=new Intent(HomePageActivity.this, TraceDynamicDetailActivity.class);
                    it.putExtra("userId",userid);
                    it.putExtra("dynamicId",dynamicId);
                    it.putExtra("position",position);
                    startActivityForResult(it,2);
                }

            });

            listViewBottom.setAdapter(lvAdapter);

            //解决嵌套listview时，默认滚动位置不是顶部
            scrollViewParent.smoothScrollTo(0,20);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 动态回复业务处理
     * @param json
     */
    private void processAddReComment(JSONObject json){
        try {
            JSONObject msg = json.getJSONObject("msg");
            if(dyTv!=null&&msg.getBoolean("success")){
                dyTv.setTag(json.getJSONObject("map"));
                dyTv.setEnabled(true);
                //动态改变ListVew高度
                JSONArray arry=(JSONArray)(dataLV.get(dyPosition).get("commentList"));
                arry.put(json.getJSONObject("map"));
                lvAdapter.notifyDataSetChanged();
                //界面刷新时，导致requestFocus无效
                dyComment.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dyComment.setFocusable(true);
                        dyComment.setFocusableInTouchMode(true);
                        dyComment.requestFocus();
                        }
                }, 500);
            }
            toast(msg.getString("info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态评论业务处理
     * @param json
     */
    private void processAddComment(JSONObject json){
        try {
            JSONObject msg = json.getJSONObject("msg");
            if(dyTv!=null&&msg.getBoolean("success")){
                dyTv.setTag(json.getJSONObject("map"));
                dyTv.setEnabled(true);
                //动态改变ListVew高度
                JSONArray arry=(JSONArray)(dataLV.get(dyPosition).get("commentList"));
                arry.put(json.getJSONObject("map"));
                lvAdapter.notifyDataSetChanged();
                //界面刷新时，导致requestFocus无效
                dyComment.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dyComment.setFocusable(true);
                        dyComment.setFocusableInTouchMode(true);
                        dyComment.requestFocus();
                    }
                }, 500);
            }
            toast(msg.getString("info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态点赞
     */
    private void processAddLike(JSONObject json){
        try {
            JSONObject msg = json.getJSONObject("msg");
            if(msg.getBoolean("success")){
                if(msg.getString("info").contains("取消")){
                    int count=Integer.parseInt(dyTv.getTag().toString());
                    count--;
                    //dyTv.setTag(""+count);
                    //dyTv.setText("(" + count + ")");
                    //dyImg.setImageResource(R.drawable.zancancel);
                    dataLV.get(dyPosition).put("isLike",false);
                    dataLV.get(dyPosition).put("likeCount",""+count);
                    lvAdapter.notifyDataSetChanged();
                    hideInputMethod(HomePageActivity.this);
                }
                else{
                    int count=Integer.parseInt(dyTv.getTag().toString());
                    count++;
                    //dyTv.setTag(""+count);
                    //dyTv.setText("(" + count + ")");
                    //dyImg.setImageResource(R.drawable.zan);
                    dataLV.get(dyPosition).put("isLike",true);
                    dataLV.get(dyPosition).put("likeCount",""+count);
                    lvAdapter.notifyDataSetChanged();
                    hideInputMethod(HomePageActivity.this);
                }
            }
            toast(msg.getString("info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关注
     */
    private void processAddWatch(JSONObject json){
        try {
            JSONObject msg = json.getJSONObject("msg");
            if(msg.getBoolean("success")){
                    if(msg.getString("info").contains("取消")){
                        //dyWatch.setText("未关注");
                        //dyWatch.setTextColor(getResources().getColor(R.color.txt_gray));
                        //dyWatch.setBackgroundResource(R.drawable.graybox);
                        dataLV.get(dyPosition).put("watchStatus","未关注");
                        lvAdapter.notifyDataSetChanged();
                        hideInputMethod(HomePageActivity.this);
                    }
                    else{
                        //dyWatch.setText("已关注");
                        //dyWatch.setTextColor(getResources().getColor(R.color.dyred));
                        //dyWatch.setBackgroundResource(R.drawable.redbox);
                        dataLV.get(dyPosition).put("watchStatus","已关注");
                        lvAdapter.notifyDataSetChanged();
                        hideInputMethod(HomePageActivity.this);
                    }
            }
            toast(msg.getString("info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 延迟线程
     */
    public class DelayThread implements Runnable {
        @Override
        public void run() {
            try {
                    Thread.sleep(3000);
                    requestDynamic();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 百度地图定位数据监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onConnectHotSpotMessage(String var1, int var){

        }

        @Override
        public void onReceiveLocation(BDLocation location) {

            //获取定位结果
            StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型

            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息

            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度

            Config.gpslat=location.getLatitude();

            Config.gpslon=location.getLongitude();

            if (location.getLocType() == BDLocation.TypeGpsLocation){

                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时

                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数

                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度

                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

                Config.gpsAddr=location.getAddrStr();

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");

                Config.gpsAddr=location.getAddrStr();

            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");

            } else if (location.getLocType() == BDLocation.TypeServerError) {

                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

            }

            sb.append("\nlocationdescribe : ");

            sb.append(location.getLocationDescribe());    //位置语义化信息

            Config.gpslocationDescribe=location.getLocationDescribe();

            /*

            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }

            */

            //Log.i("BaiduLocationApiDem", sb.toString());

            //toast(sb.toString());

            if(locationCount++>5) {
                if (mLocationClient != null)
                    mLocationClient.stop();
            }
        }
    }


    /**
     * 阿里云旺登录
     */
    private void openIMLogin(){

        //判断当前网络状态，若当前无网络则提示用户无网络
        if (YWChannel.getInstance().getNetWorkState().isNetWorkNull()) {
            toast("网络已断开，请稍后再试哦~");
            return;
        }

        final String userId=Config.IMUserId;
        final String password=Config.IMPassword;
        final String appKey= OpenIMLoginHelper.APP_KEY;

        //初始化imkit
        OpenIMLoginHelper.getInstance().initIMKit(userId, appKey);

        //自定义头像和昵称回调初始化(如果不需要自定义头像和昵称，则可以省去)
        UserProfileSampleHelper.initProfileCallback();

        //通知栏相关的初始化
        NotificationInitSampleHelper.init();

        OpenIMLoginHelper.getInstance().login_Sample(userId.toString(), password.toString(), appKey.toString(), new IWxCallback() {

            @Override
            public void onSuccess(Object... arg0) {
                //IMNotificationUtils.getInstance().showToast(HomePageActivity.this, "登录成功");
            }

            @Override
            public void onProgress(int arg0) {

            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                if (errorCode == YWLoginCode.LOGON_FAIL_INVALIDUSER) { //若用户不存在，则提示使用游客方式登陆
                    toast("云旺用户不存在");
                } else {
                    YWLog.w(TAG, "登录失败，错误码：" + errorCode + "  错误信息：" + errorMessage);
                    IMNotificationUtils.getInstance().showToast(HomePageActivity.this, errorMessage);
                }
            }
        });
    }

    /**
     * 获取友盟token
     */
    private void loadDeviceToken() {
        Config.deviceToken= app.getPushAgent().getRegistrationId();
        if(Config.UUID.equals("")) {
            Config.UUID=getUUID();
        }
        requestEditUUID();
    }
}