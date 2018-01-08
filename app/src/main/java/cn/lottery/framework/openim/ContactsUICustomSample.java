package cn.lottery.framework.openim;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMContactsUI;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.mobileim.fundamental.widget.YWAlertDialog;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import cn.lottery.R;
import cn.lottery.app.activity.openim.contact.OpenimFindContactActivity;

/**
 * 联系人界面UI的定制点(根据需要实现相应的接口来达到联系人界面)，不设置则使用云旺默认的实现
 * 调用方设置的回调，必须继承BaseAdvice 根据不同的需求实现 不同的 开放的 Advice
 * com.alibaba.mobileim.aop.pointcuts包下开放了不同的Advice.通过实现多个接口，组合成对不同的ui界面的定制
需要在application中将这个Advice绑定。设置以下代码
 * AdviceBinder.bindAdvice(PointCutEnum.CONTACTS_UI_POINTCUT, ContactsUICustomSample.class);
 *
 * @author shuheng
 */
public class ContactsUICustomSample extends IMContactsUI {

    public ContactsUICustomSample(Pointcut pointcut) {
        super(pointcut);
    }

    /**
     * 返回联系人自定义标题
     *
     * @param fragment
     * @param context
     * @param inflater
     * @return
     */
    @Override
    public View getCustomTitle(final Fragment fragment, final Context context, LayoutInflater inflater) {
        //TODO 重要：必须以该形式初始化customView---［inflate(R.layout.**, new RelativeLayout(context),false)］------，以让inflater知道父布局的类型，否则布局xml**中定义的高度和宽度无效，均被默认的wrap_content替代

        RelativeLayout customView = (RelativeLayout) inflater.inflate(R.layout.openim_custom_contacts_title_bar, new RelativeLayout(context), false);
        customView.setBackgroundColor(Color.parseColor("#f4bc1a")); //#00b4ff
        TextView title = (TextView) customView.findViewById(R.id.title_txt);
        title.setText("好友"); //联系人
        title.setTextSize(15);
        title.setTextColor(Color.WHITE);
        title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Toast.makeText(context, "click ", Toast.LENGTH_SHORT).show();
            }
        });

        customView.setVisibility(View.GONE);

        //返回按钮
        TextView backButton = (TextView) customView.findViewById(R.id.left_button);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                fragment.getActivity().finish();
                //ShowAddContactDialog(fragment.getActivity());
            }
        });

        backButton.setVisibility(View.GONE);

        //增加查找联系人
        ImageView rightButton = (ImageView) customView.findViewById(R.id.title_button);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //增加查找联系人
                Intent intent = new Intent().setClass(context, OpenimFindContactActivity.class);
                context.startActivity(intent);
            }
        });

        rightButton.setVisibility(View.GONE);

        return customView;
    }

    /**
     * 群组邀请联系人
     * @param activity
     */
    private void ShowAddContactDialog(final Activity activity){
        View view = View.inflate(activity, R.layout.openim_dialog_invite_tribe_member, null);
        final EditText mUserId = (EditText) view.findViewById(R.id.userid);
        final EditText mAppKey = (EditText) view.findViewById(R.id.appkey);
        mAppKey.setText(OpenIMLoginHelper.APP_KEY);
        AlertDialog dialog = new YWAlertDialog.Builder(activity)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userId = mUserId.getText().toString();
                        String appKey = mAppKey.getText().toString();
                        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(appKey)){
                            IMNotificationUtils.getInstance().showToast(activity, "userId 和 appKey 均不能为空！");
                            return;
                        }
                        YWIMKit imKit = OpenIMLoginHelper.getInstance().getIMKit();
                        IYWContactService contactService = imKit.getContactService();
                        contactService.addContact(userId, appKey, "", "", new IWxCallback() {
                            @Override
                            public void onSuccess(Object... result) {
                                Toast.makeText(activity.getBaseContext(), "增加联系成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(int code, String info) {
                                Toast.makeText(activity.getBaseContext(), "增加联系失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onProgress(int progress) {

                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        if (!dialog.isShowing()){
            dialog.show();
        }
    }
}
