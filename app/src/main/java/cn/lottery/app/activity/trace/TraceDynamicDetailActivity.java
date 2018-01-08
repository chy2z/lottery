package cn.lottery.app.activity.trace;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cn.lottery.framework.activity.BaseActivity;
import cn.lottery.framework.widget.scrollView.BottomScrollView;

/**
 * 动态详情页面
 * Created by admin on 2017/7/15.
 */
public class TraceDynamicDetailActivity extends BaseActivity implements View.OnClickListener {

    String userId="0";

    String dynamicId="0";

    int delPosition=0;

    private TextView delDynamic;

    private LinearLayout layoutBack;

    private ListView listViewBottom;

    private HomePageDynamicListViewAdapter lvAdapter;

    private BottomScrollView scrollViewParent;

    private ArrayList<HashMap<String, Object>> dataLV = new ArrayList<HashMap<String, Object>>();

    private JSONArray resultList;

    private TextView dyTv;

    private Button dyWatch;

    private ImageView dyImg;

    private EditText dyComment;

    private int dyPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_trace_dynamic_detail);

        Intent it = getIntent();

        userId=it.getStringExtra("userId"); //当前登录人的id

        dynamicId=it.getStringExtra("dynamicId");

        delPosition=it.getIntExtra("position",0);

        initView();

        initDate();
    }

    private void initView(){
        delDynamic = (TextView) findViewById(R.id.delDynamic);
        delDynamic.setOnClickListener(this);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        scrollViewParent=(BottomScrollView) findViewById(R.id.scrollViewParent);
        listViewBottom = (ListView) findViewById(R.id.listViewBottom);
        scrollViewParent.setOnScrollToBottomLintener(new BottomScrollView.OnScrollListener(){
            @Override
            public void onScrollListener(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
                hideInputMethod(TraceDynamicDetailActivity.this);
            }

            @Override
            public void onScrollBottomListener(int scrollX, int scrollY, boolean clampedX, boolean isBottom) {

            }
        });
    }

    private void initDate(){
        requestDynamic();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            editClose();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                editClose();
                break;
            case R.id.delDynamic:
                requestDelDynamic();
                break;
        }
    }

    private  void editClose(){
        Config.traceDynamDetail=dataLV.get(0);
        Intent resultData = new Intent();
        resultData.putExtra("data", "EditDynamic");
        resultData.putExtra("positon", delPosition);
        setResult(2, resultData);
        finish();
    }

    /**
     * 获取附近动态信息
     */
    private void requestDynamic(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id",dynamicId);
        params.put("userId",Config.customerID);
        params.put("user_token",Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.getDynamic, params, null,"requestDynamic");
    }

    /**
     * 删除动态
     */
    private void requestDelDynamic(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id",dynamicId);
        params.put("userId",Config.customerID);
        params.put("user_token",Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.delDynamic, params, null,"requestDelDynamic");
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
        else if(tag.equals("requestDelDynamic")){
            closeDialog();
            processDelDynamic(obj);
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
     * 关注动态业务处理
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

                //动态列表中没有，单独动态详情中有useid
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

                if(!jobj.getString("userId").equals(Config.customerID)){
                    delDynamic.setVisibility(View.GONE);
                }
            }

            lvAdapter = new HomePageDynamicListViewAdapter(TraceDynamicDetailActivity.this,HomePageDynamicListViewAdapter.DynamicDetial_Type, dataLV,app.getImageLoader());

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
                    Intent it=new Intent(TraceDynamicDetailActivity.this, TraceDynamicDetailActivity.class);
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
     * 删除动态业务
     * @param json
     */
    private void processDelDynamic(JSONObject json){
        try {
            JSONObject msg = json.getJSONObject("msg");
            toast(msg.getString("info"));
            if(msg.getBoolean("success")) {
                Intent resultData = new Intent();
                resultData.putExtra("data", "delDynamic");
                resultData.putExtra("positon", delPosition);
                setResult(2, resultData);
                finish();
            }
        }
        catch (JSONException e) {
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
                    hideInputMethod(TraceDynamicDetailActivity.this);
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
                    hideInputMethod(TraceDynamicDetailActivity.this);
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
                    hideInputMethod(TraceDynamicDetailActivity.this);
                }
                else{
                    //dyWatch.setText("已关注");
                    //dyWatch.setTextColor(getResources().getColor(R.color.dyred));
                    //dyWatch.setBackgroundResource(R.drawable.redbox);
                    dataLV.get(dyPosition).put("watchStatus","已关注");
                    lvAdapter.notifyDataSetChanged();
                    hideInputMethod(TraceDynamicDetailActivity.this);
                }
            }
            toast(msg.getString("info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
