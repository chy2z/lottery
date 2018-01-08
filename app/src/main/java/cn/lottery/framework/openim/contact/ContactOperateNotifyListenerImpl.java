package cn.lottery.framework.openim.contact;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.IYWContactOperateNotifyListener;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.lottery.framework.Config;
import cn.lottery.framework.RequestUrl;
import cn.lottery.framework.SApplication;
import cn.lottery.framework.net.ErrorListener;
import cn.lottery.framework.net.JsonPostRequest;
import cn.lottery.framework.net.Net;
import cn.lottery.framework.net.SuccessListener;
import cn.lottery.framework.util.DateUtils;
import cn.lottery.framework.util.L;
import cn.lottery.framework.util.NetUtils;

/**
 * 好友操作回调通知
 * Created by ShuHeng on 16/2/26.
 */
public class ContactOperateNotifyListenerImpl implements IYWContactOperateNotifyListener,SuccessListener<JSONObject>, ErrorListener {

    /**
     * 用户请求加你为好友
     * todo 该回调在UI线程回调 ，请勿做太重的操作
     *
     * @param contact 用户的信息
     * @param message 附带的备注
     */
    @Override
    public void onVerifyAddRequest(IYWContact contact, String message) {
        //IMNotificationUtils.getInstance().showToast(SApplication.getContext(), contact.getUserId()+"用户请求加你为好友");
        //operateContactCallBack(Config.IMUserId,contact.getUserId(),"0");
    }

    /**
     * 用户接受了你的好友请求
     * todo 该回调在UI线程回调 ，请勿做太重的操作
     *
     * @param contact 用户的信息
     */
    @Override
    public void onAcceptVerifyRequest(IYWContact contact) {
        //IMNotificationUtils.getInstance().showToast(SApplication.getContext(),contact.getUserId()+"用户接受了你的好友请求");
        //operateContactCallBack(Config.IMUserId,contact.getUserId(),"1");
    }

    /**
     * 用户拒绝了你的好友请求
     * todo 该回调在UI线程回调 ，请勿做太重的操作
     * @param  contact 用户的信息
     */
    @Override
    public void onDenyVerifyRequest(IYWContact contact) {
        //IMNotificationUtils.getInstance().showToast(SApplication.getContext(),contact.getUserId()+"用户拒绝了你的好友请求");
        //operateContactCallBack(Config.IMUserId,contact.getUserId(),"2");
    }

    /**
     * 云旺服务端（或其它终端）进行了好友添加操作
     * todo 该回调在UI线程回调 ，请勿做太重的操作
     *
     * @param contact 用户的信息
     */
    @Override
    public void onSyncAddOKNotify(IYWContact contact) {
        //IMNotificationUtils.getInstance().showToast(SApplication.getContext(),"云旺服务端（或其它终端）进行了好友添加操作对"+contact.getUserId());
    }

    /**
     * 用户从好友名单删除了您
     * todo 该回调在UI线程回调 ，请勿做太重的操作
     *
     * @param contact 用户的信息
     */
    @Override
    public void onDeleteOKNotify(IYWContact contact) {
        //IMNotificationUtils.getInstance().showToast(SApplication.getContext(),contact.getUserId()+"用户从好友名单删除了您");
        //operateContactCallBack(Config.IMUserId,contact.getUserId(),"3");
    }

    /**
     * 用户添加我为好友
     * @param contact
     */
    @Override
    public void onNotifyAddOK(IYWContact contact) {
        //IMNotificationUtils.getInstance().showToast(SApplication.getContext(),contact.getUserId()+"用户添加您为好友了");
    }

    private void  operateContactCallBack(String one ,String two,String type){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accountIdOne", one);
        params.put("accountIdTwo", two);
        params.put("type", type);
        post(Config.URL_PREFIX + RequestUrl.addFriend, params, null,"requestAddFriend");
    }

    protected void post(final String url, final HashMap<String, String> params, final String userToken, Object tag) {
        if (NetUtils.isConnected(SApplication.getContext())) {
            if (Config.IS_DEBUG) {
                L.e("----------网络请求开始[ 时间:" + DateUtils.getCurrentDate()
                        + " 标识:" + tag.toString() + " ]----------");
            }
            JsonPostRequest request = new JsonPostRequest(url,
                    Net.addPublicParam(url, params, userToken), this, this);
            DefaultRetryPolicy policy = new DefaultRetryPolicy(5000, 0, 1f);// 禁止重连.
            request.setRetryPolicy(policy);
            request.setTag(tag);
            SApplication.getInstance().getRequestQueue().add(request);
        } else {
            IMNotificationUtils.getInstance().showToast(SApplication.getContext(),"网络故障,请检查网络后重试.");
        }
    }

    @Override
    public void onSuccessResponse(JSONObject response, Object tag) {
        if (Config.IS_DEBUG) {
            L.i("---4.请求成功,服务器返回数据如下---");
            L.d(response.toString());
            L.e("----------网络请求结束[ 时间:" + DateUtils.getCurrentDate() + " 标识:"
                    + tag.toString() + " ]----------");
        }
        try {
            String taged = tag.toString();
                JSONObject msg = response.getJSONObject("msg");
                boolean result = msg.getBoolean("success");
                if (!result) {
                    //hiddenDialog();
                    //showTipDialog(msg.getString("info"));
                    //handError(tag, msg.getString("info"));
                } else {
                    //handSuccess(response, tag);
                }

        } catch (JSONException e) {
            IMNotificationUtils.getInstance().showToast(SApplication.getContext(),"返回数据格式错误.");
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, Object tag) {
        if (Config.IS_DEBUG) {
            L.i("---4.请求出错,出错信息如下---");
            L.e("----------网络请求结束[ 时间:" + DateUtils.getCurrentDate() + " 标识:"
                    + tag.toString() + " ]----------");
        }
        IMNotificationUtils.getInstance().showToast(SApplication.getContext(),"请求出错,请重试.");
    }
}