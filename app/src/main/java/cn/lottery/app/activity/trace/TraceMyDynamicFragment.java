package cn.lottery.app.activity.trace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.app.adapter.HomePageDynamicListViewAdapter;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.fragment.BaseFragment;
import cn.lottery.framework.widget.scrollView.BottomScrollView;

/** 我的动态
 * Created by admin on 2017/7/11.
 */
public class TraceMyDynamicFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private String TAG = TraceMyDynamicFragment.class.getSimpleName();

    private View view;

    private SwipeRefreshLayout mSwipeLayout;

    private ArrayList<HashMap<String, Object>> dataLV = new ArrayList<HashMap<String, Object>>();

    private JSONArray resultList;

    private ListView listViewBottom;

    private HomePageDynamicListViewAdapter lvAdapter;

    private BottomScrollView scrollViewParent;

    private TextView dyTv;

    private Button dyWatch;

    private ImageView dyImg;

    private EditText dyComment;

    private int dyPosition;

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
        view = inflater.inflate(R.layout.km_fragment_dynamic, null);
        init();
        initData();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==2){
            if(data.getStringExtra("data").equals("myDynamic")){
                onRefresh();
            }
        }
        else if (requestCode==2&&resultCode==2){
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

    /**
     * 刷新事件
     */
    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(0X110, 100);
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

    private void init() {
        scrollViewParent=(BottomScrollView) view.findViewById(R.id.scrollViewParent);
        listViewBottom = (ListView) view.findViewById(R.id.listViewBottom);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);

        scrollViewParent.setOnScrollToBottomLintener(new BottomScrollView.OnScrollListener(){
            @Override
            public void onScrollListener(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
                hideInputMethod(getActivity());
            }

            @Override
            public void onScrollBottomListener(int scrollX, int scrollY, boolean clampedX, boolean isBottom) {

            }
        });
    }

    private  void initData(){
      requestDynamic();
   }


    /**
     * 获取附近动态信息
     */
    private void requestDynamic(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId",Config.customerID);
        params.put("user_token",Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.getMyDynamic, params, null,"requestDynamic");
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
         if (tag.equals("requestDynamic")) {
             closeDialog();
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

                hashMap.put("watchStatus","无");

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

                for (int j=0;j<imges.length();j++){
                    hashMap.put("contentImg"+(j+1), imges.getString(j));
                    if(j<=2&&j>=0)   hashMap.put("layoutimgRow1", "1");
                    if(j<=5&&j>2)   hashMap.put("layoutimgRow2", "1");
                    if(j>5)   hashMap.put("layoutimgRow3", "1");

                    if (j < imges.length() - 1) {
                        imgUrlAll += imges.getString(j) + ",";
                    } else imgUrlAll += imges.getString(j);
                }

                hashMap.put("imgUrlAll",imgUrlAll);

                JSONArray commentList= jobj.getJSONArray("commentList");

                hashMap.put("commentList",commentList);

                dataLV.add(hashMap);
            }

            lvAdapter = new HomePageDynamicListViewAdapter(getContext(),HomePageDynamicListViewAdapter.MyDynamic_Type, dataLV,app.getImageLoader());

            lvAdapter.setCommentListener(new HomePageDynamicListViewAdapter.CommentListener() {
                /**
                 * 回复
                 */
                @Override
                public void addReComment(int position,EditText comment, TextView tv, String content, String dynamicId, String commentUserId, String userId, String user_token, String uuid) {
                    //toast("content:"+content+",dynamicId:"+dynamicId+",commentUserId:"+commentUserId+",userId:"+userId+",user_token:"+user_token+",uuid:"+uuid);
                    dyPosition=position;
                    dyComment=comment;
                    dyTv=tv;
                    requestAddReComment(content,dynamicId,commentUserId,userId,user_token,uuid);
                }

                /**
                 * 评论
                 */
                @Override
                public void addComment(int position,EditText comment, TextView tv,String content, String dynamicId, String userId, String user_token) {
                    //toast("content:"+content+",dynamicId:"+dynamicId+",userId:"+userId+",user_token:"+user_token);
                    dyPosition=position;
                    dyComment=comment;
                    dyTv=tv;
                    requestAddComment(content,dynamicId,userId,user_token);
                }

                @Override
                public void addLike(int position,ImageView img , TextView tv,String dynamicId,String userid, String user_token){
                    //toast("dynamicId:"+dynamicId+",userId:"+userid+",user_token:"+user_token);
                    dyPosition=position;
                    dyImg=img;
                    dyTv=tv;
                    requestAddLike(dynamicId,userid,user_token);
                }

                @Override
                public void addWatch(int position,Button watch,String dynamicId,String userid, String user_token){
                    //toast("dynamicId:"+dynamicId+",userId:"+userid+",user_token:"+user_token);
                    dyPosition=position;
                    dyWatch=watch;
                    requestAddWatch(dynamicId,userid,user_token);
                }

                @Override
                public void showDetail(int position,String dynamicId,String userid, String user_token){
                    Intent it=new Intent(getContext(), TraceDynamicDetailActivity.class);
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
                    hideInputMethod(getActivity());
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
                    hideInputMethod(getActivity());
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
                    hideInputMethod(getActivity());
                }
                else{
                    //dyWatch.setText("已关注");
                    //dyWatch.setTextColor(getResources().getColor(R.color.dyred));
                    //dyWatch.setBackgroundResource(R.drawable.redbox);
                    dataLV.get(dyPosition).put("watchStatus","已关注");
                    lvAdapter.notifyDataSetChanged();
                    hideInputMethod(getActivity());
                }
            }
            toast(msg.getString("info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
