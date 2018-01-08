package cn.lottery.app.activity.myself;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.lottery.R;
import cn.lottery.app.adapter.CashDetailListViewAdapter;
import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.activity.BaseActivity;

/**
 * 我的消费明细
 * Created by admin on 2017/6/20.
 */
public class MyWalletCashDetailsActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout layoutBack;

    ListView listViewBottom;

    CashDetailListViewAdapter lvAdapter;

    private ArrayList<HashMap<String, Object>> dataLV = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_km_my_wallet_cashdetail);

        initView();

        initData();
    }

    private void initView() {
        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(this);

        listViewBottom = (ListView) findViewById(R.id.listViewBottom);
    }

    private void initData(){
        requestCashDetail();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                finish();
                break;
        }
    }


    /**
     * 请求用户信息
     */
    private void requestCashDetail() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", Config.customerID);
        params.put("user_token", Config.userToken);
        showLoadingDialog();
        post(Config.URL_PREFIX + RequestUrl.getCashDetail, params, null,"requestCashDetail");
    }

    /**
     * 请求成功返回
     */
    @Override
    protected void handSuccess(JSONObject obj, Object tag) {
        if (tag.equals("requestCashDetail")) {
            closeDialog();
            processCashDetail(obj);
        }
    }

    /**
     * 获取用户业务处理
     * @param json
     */
    private void processCashDetail(JSONObject json){
        try {
            JSONObject msg=json.getJSONObject("msg");
            if(msg.getBoolean("success")) {
                JSONArray list= json.getJSONArray("list");
                if(list.length()>0){
                    HashMap<String, Object> hashMap = null;
                    JSONObject jobj = null;
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data=list.getJSONObject(i);
                        JSONArray listchild=data.getJSONArray("list");
                        for (int j = 0; j < listchild.length(); j++) {
                            hashMap = new HashMap<String, Object>();
                            jobj = listchild.getJSONObject(j);
                            if(j==0)
                                hashMap.put("group","0");
                            else
                                hashMap.put("group","1");
                            hashMap.put("year", jobj.getString("year"));
                            hashMap.put("addBalance", jobj.getString("addBalance"));
                            hashMap.put("createTime",jobj.getString("createTime"));


                            //1:聚会收入 2:闲货收入 3:兼职收入 4:聚会支出 5:闲货支出 6:兼职支出 7:余额提现）

                            switch (jobj.getString("type")){
                                case "1":
                                    hashMap.put("type","聚会收入");
                                    break;
                                case "2":
                                    hashMap.put("type","闲货收入");
                                    break;
                                case "3":
                                    hashMap.put("type","兼职收入");
                                    break;
                                case "4":
                                    hashMap.put("type","聚会支出");
                                    break;
                                case "5":
                                    hashMap.put("type","闲货支出");
                                    break;
                                case "6":
                                    hashMap.put("type","兼职支出");
                                    break;
                                case "7":
                                    hashMap.put("type","余额提现");
                                    break;
                                default:
                                    hashMap.put("type","无");
                                    break;
                            }

                            dataLV.add(hashMap);
                        }
                    }

                    //toast(""+dataLV.size());

                    lvAdapter = new CashDetailListViewAdapter(this,dataLV);

                    listViewBottom.setAdapter(lvAdapter);
                }
                else{
                    toast("无现金交易明细！");
                }
            }
            else{
                toast("获取现金明细出错！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
