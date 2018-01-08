package cn.lottery.framework.openim;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMConversationListOperation;
import com.alibaba.mobileim.channel.util.AccountUtils;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWConversationType;
import com.alibaba.mobileim.conversation.YWCustomConversationBody;
import com.alibaba.mobileim.utility.AccountInfoTools;

import cn.lottery.R;
import cn.lottery.framework.SApplication;
import cn.lottery.app.activity.openim.contact.OpenimContactSystemMessageActivity;
import cn.lottery.app.activity.openim.tribe.OpenimTribeSystemMessageActivity;
import cn.lottery.framework.openim.common.Constant;


/**
 * 最近会话界面的定制点(根据需要实现相应的接口来达到自定义会话列表界面)，不设置则使用openIM默认的实现
 * 调用方设置的回调，必须继承BaseAdvice 根据不同的需求实现 不同的 开放的 Advice
 * com.alibaba.mobileim.aop.pointcuts包下开放了不同的Advice.通过实现多个接口，组合成对不同的ui界面的定制
 * 这里设置了自定义会话的定制
 * 1.CustomConversationAdvice 实现自定义会话的ui定制
 * 2.CustomConversationTitleBarAdvice 实现自定义会话列表的标题的ui定制
 * <p/>
 * 另外需要在application中将这个Advice绑定。设置以下代码
 * AdviceBinder.bindAdvice(PointCutEnum.CONVERSATION_FRAGMENT_POINTCUT, CustomChattingAdviceDemo.class);
 *
 * @author jing.huai
 */
public class ConversationListOperationCustomSample extends IMConversationListOperation {

    public ConversationListOperationCustomSample(Pointcut pointcut) {
        super(pointcut);
    }

    /**
     * 返回自定义会话和群会话的头像 url
     * 该方法只适用设置自定义会话和群会话的头像，设置单聊会话头像请参考{@link cn.lottery.framework.openim.UserProfileSampleHelper}
     *
     * @param fragment
     * @param conversation 会话 可以通过 conversation.getConversationId拿到用户设置的会话id以根据不同的逻辑显示不同的头像
     * @return
     */
    @Override
    public String getConversationHeadPath(Fragment fragment, YWConversation conversation) {
        if (conversation.getConversationType() == YWConversationType.Tribe){
            return null;
        }

        //如果开发者需要异步获取url的话，先return null，在url获取到后，调用notifyContactProfileUpdate进行刷新
        //刷新后，SDK会再次调用getConversationHeadPath方法
        //刷新调用，请参考：LoginSampleHelper.getInstance().getIMKit().getContactService().notifyContactProfileUpdate();
        if(conversation.getConversationType() == YWConversationType.Custom){
            YWCustomConversationBody body = (YWCustomConversationBody) conversation.getConversationBody();
            String conversationId = body.getIdentity();
            if (conversationId.equals(Constant.SYSTEM_CHY_NOTIFY)) {
                return null; //返回错误图片，加载默认图片
            }
        }

        //return "http://tp2.sinaimg.cn/1721410501/50/40033657718/0";

        return null;
    }

    /**
     * 返回自定义会话和群会话的默认头像 如返回本地的 R.drawable.test
     * 该方法只适用设置自定义会话和群会话的头像，设置单聊会话头像请参考{@link cn.lottery.framework.openim.UserProfileSampleHelper}
     *
     * @param fragment
     * @param conversation
     * @return
     */
    @Override
    public int getConversationDefaultHead(Fragment fragment,YWConversation conversation) {
        if (conversation.getConversationType() == YWConversationType.Custom) {
            YWCustomConversationBody body = (YWCustomConversationBody) conversation.getConversationBody();
            String conversationId = body.getIdentity();
            if (conversationId.equals(Constant.SYSTEM_TRIBE_CONVERSATION)) {
                return R.drawable.aliwx_tribe_head_default;
            } else if (conversationId.equals(Constant.SYSTEM_FRIEND_REQ_CONVERSATION)) {
                return R.drawable.aliwx_head_default;
            } else if (conversationId.equals(Constant.SYSTEM_CHY_NOTIFY)) {
                return R.drawable.ic_launcher;
            }  else {
                return R.drawable.aliwx_head_default;
            }
        } else if (conversation.getConversationType() == YWConversationType.Tribe){
            return R.drawable.aliwx_tribe_head_default;
        }
        return 0;
    }

    /**
     * 返回自定义会话的名称
     *
     * @param fragment
     * @param conversation
     * @return 这里的myconversatoin或者custom_view_conversation是调用sdk方法插入的自定义会话id.
     * 具体参考CustomConversationHelper
     */
    @Override
    public String getConversationName(Fragment fragment,
                                      YWConversation conversation) {
        if (conversation.getConversationBody() instanceof YWCustomConversationBody) {
            YWCustomConversationBody body = (YWCustomConversationBody) conversation.getConversationBody();
            if (body.getIdentity().equals(Constant.SYSTEM_TRIBE_CONVERSATION)) {
                return "群系统通知";
            } else if (body.getIdentity().equals(Constant.SYSTEM_FRIEND_REQ_CONVERSATION)) {
                return "联系人通知";
            }
            else if (body.getIdentity().equals(Constant.SYSTEM_CHY_NOTIFY)) {
                return "咖忙通知";
            }
        }
        return null;
    }

    /**
     * 定制会话点击事件，该方法可以定制所有的会话类型，包括单聊、群聊、自定义会话
     *
     * @param fragment     会话列表fragment
     * @param conversation 当前点击的会话对象
     * @return true: 使用用户自定义的点击事件  false：使用SDK默认的点击事件
     */
    @Override
    public boolean onItemClick(Fragment fragment, YWConversation conversation) {
//        YWConversationType type = conversation.getConversationType();
//        if (type == YWConversationType.P2P){
//            //TODO 单聊会话点击事件
//            return true;
//        } else if (type == YWConversationType.Tribe){
//            //TODO 群会话点击事件
//            return true;
//        } else if (type == YWConversationType.Custom){
//            //TODO 自定义会话点击事件
//            return true;
//        }
        if (conversation.getConversationType() == YWConversationType.Custom) {
            YWCustomConversationBody body = (YWCustomConversationBody) conversation.getConversationBody();
            String conversationId = body.getIdentity();
            if (conversationId.startsWith(Constant.SYSTEM_FRIEND_REQ_CONVERSATION)) { //联系人通知
                //IMNotificationUtils.getInstance().showToast("SYSTEM_FRIEND_REQ_CONVERSATION", fragment.getContext());
                Intent intent = new Intent(SApplication.getContext(), OpenimContactSystemMessageActivity.class);
                fragment.getActivity().startActivity(intent);
                return true;
            } else if (conversationId.startsWith(Constant.SYSTEM_TRIBE_CONVERSATION)) { //群系统通知
                //IMNotificationUtils.getInstance().showToast("SYSTEM_TRIBE_CONVERSATION", fragment.getContext());
                Intent intent = new Intent(SApplication.getContext(), OpenimTribeSystemMessageActivity.class);
                fragment.getActivity().startActivity(intent);
                return true;
            } else if (conversationId.startsWith(Constant.RELATED_ACCOUNT)){
                String loginId = YWAPI.getLoginAccountList().get(1);
                //IMNotificationUtils.getInstance().showToast(loginId, fragment.getContext());
                YWIMKit imKit = YWAPI.getIMKitInstance(AccountUtils.getShortUserID(loginId), AccountInfoTools.getAppkeyFromUserId(loginId));
                Intent intent = imKit.getConversationActivityIntent();
                fragment.getActivity().startActivity(intent);
            }
            else if (conversationId.startsWith(Constant.SYSTEM_CHY_NOTIFY)){  //自定义系统消息
                //IMNotificationUtils.getInstance().showToast("SYSTEM_CHY_NOTIFY", fragment.getContext());
            }
        }

        return false;
    }

    /**
     * 定制会话长按事件，该方法可以定制所有的会话类型
     *
     * @param fragment     会话列表fragment
     * @param conversation 当前点击的会话对象
     * @return true: 使用用户自定义的长按事件  false：使用SDK默认的长按事件
     */
    @Override
    public boolean onConversationItemLongClick(Fragment fragment, YWConversation conversation) {
//        YWConversationType type = conversation.getConversationType();
//        if (type == YWConversationType.P2P){
//            //TODO 单聊会话长按事件
//            return true;
//        } else if (type == YWConversationType.Tribe){
//            //TODO 群会话长按事件
//            return true;
//        } else if (type == YWConversationType.Custom){
//            //TODO 自定义会话长按事件
//            return true;
//        }
        return false;
    }
}
