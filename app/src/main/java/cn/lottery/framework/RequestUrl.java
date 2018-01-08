package cn.lottery.framework;

public class RequestUrl {

	//检查版本更新
	public static final String chechAppVersion="/api/version/checkVersion.htm";

    //注册接口
    public static final String register="/api/user/register.htm";

    //绑定注册
    public static final String bindRegister="/api/user/addAccount.htm";

    //短信验证码
    public static final String getPhoneCode="/api/vercode/getPhoneCode.htm";

    //手机登录接口
    public static final String phoneLogin="/api/user/login.htm";

    //第三方登录
    public static final String thirdLogin="/api/user/thirdLogin.htm";

    //实名认证
    public static final String validatePerson="/api/user/validatePerson.htm";

    //忘记密码
    public static final String forgetPassword="/api/user/forgetPassword.htm";

    //修改密码
    public  static  final  String editPassword="/api/user/fixPassword.htm";

    //修改密码验证验证码
    public static final String validateForgetCode="/api/user/validateForgetCode.htm";

    //查找用户信息
    public static final  String findUserInfo="/api/user/findUserInfo.htm";

    //查找用户账户信息
    public static final  String findAccountInfo="/api/user/accountInfo.htm";

    //更新头像
    public static final  String updateHeadImg=  "/api/user/updateImg.htm";

    //更新背景
    public static final  String updateBackImg=  "/api/user/updateBackImg.htm";

    //更新用户数据
    public static final  String updateUserData="/api/user/updateData.htm";

	//修改注册与更新移动客户端的deviceToken
	public static final String modifyDeviceToken="/api/client/modifyDeviceToken.htm";	
	
	//获取基本配置
	public static final String getSysInfo="/api/setting/getSysInfo.htm";
	
	//获取广告图片
    public static final String getLaunchimageInfo="/api/appmenu/home/getLaunchimageInfo.htm";
    
    //获取首次登录广告
    public static final String getFirstLoginImage="/api/appmenu/home/getFirstLoginImageInfo.htm";

    //更新消息状态
    public static final String updateOneNotificationStatus="/api/client/updateOneNotificationStatus.htm";

    //获取主页数据
    public static final String getHomeData="/api/banner/mainPage.htm";

    //现金交易明细
    public static final  String getCashDetail="/api/user/accountList.htm";

    //绑定支付宝账户
    public static  final  String bindAlipay="/api/user/setAlipayAccount.htm";

    //提现
    public static final String getMoneyCash="/api/user/getMoney.htm";

    //提现密码设置短信验证
    public static final  String checkPayPwdCode="/api/user/vaPayPwd.htm";

    //设置支付密码
    public static final  String setPayPassword="/api/user/setPayPassword.htm";

    //账户验证接口
    public static final  String accountValidate="/api/user/accountValidate.htm";

    //更换手机号码
    public static final  String updatePhone="/api/user/updatePhone.htm";

    //获取附近动态信息
    public static final  String getNearByDynamic="/api/dynamic/list.htm";

    //获取好友动态信息
    public static final  String getFriendDynamic="/api/my/dynamic/friendList.htm";

    //获取关注动态信息
    public static final  String getWatchDynamic="/api/my/dynamic/concernList.htm";

    //我的动态
    public static final  String getMyDynamic="/api/my/dynamic/myList.htm";

    //增加动态
    public static final  String getAddDynamic="/api/dynamic/addDynamic.htm";

    //获取单条动态
    public static final  String getDynamic="/api/my/dynamic/detail.htm";

    //删除动态
    public static final  String delDynamic="/api/my/dynamic/delete.htm";

    //云旺好友申请操作
    public static final  String addFriend="/api/friend/addFriend.htm";

    //云旺群申请操作
    public static final  String operaterGroup="/api/group/operaterGroup.htm";

    //增加动态回复
    public static final  String addReComment="/api/comment/addReComment.htm";

    //增加评论
    public static final  String addComment="/api/comment/addComment.htm";

    //点赞
    public static final  String addLike="/api/my/dynamic/like.htm";

    //关注
    public static  final  String addWatch="/api/my/dynamic/concern.htm";

    //获取图像
    public static  final  String getImg="/api/sysFileUrls/getImgs.htm";

    //更新友盟信息
    public static final String editUUID="/api/youmeng/editUUID.htm";
}