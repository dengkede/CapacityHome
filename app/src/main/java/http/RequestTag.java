package http;

/**
 * Created by Administrator on 2018/3/29.
 */

public class RequestTag {

    public static  String BaseUrl = "http://app.scyoue.com/xiao_api_193v1.php/";//app.scyoue.com

    public static String BaseImageUrl = "http://x.scttaf.com";

    public static String BaseVieoUrl = "http://x.scttaf.com";//视屏文件地址

    public static String ArticaleUrl = "http://x.scttaf.com/";//文章视屏详情

    public static final String KEY = "098f6bcd4621d373cade4e832627b4f6";//请求key

    public static final String SOCKET_KEY = "A3gfDS9CMnXZ1dPseEWjsLSCV";//衣柜打开请求key

    public static final String APPID = "20180601";//标识id
    /**
     * http socket长连接地址
     */
    public static final String HOST = "211.149.154.203";

    public  static final int HOST_PORT = 2346;
    /**
     * http socket长连接图片地址
     */
    public static final String HOST_PIC = " http://211.149.154.203:85";
    /**
     * 检查更新
     */
    public static final String CHECK_UPDATE = "pub/checkupdate";
    /**
     * 获取各项配置参数
     */
    public static final String CONFIGURATION = "pub/cfg";
    /**
     * 会员综合统计
     */
    public static final String MENBER_STATISTICS = "pub/CountNumber";
    /**
     * 会员忘记密码
     */
    public static final String SET_PASSWEOR ="logreg/forget";
    /**
     * 获取图片验证码
     */
    public static final String GETVERFICATIONCODE = "image/image_code";
    /**
     * 获取短信验证码
     */
    public static final String GETNOTECODE = "code/send";
    /**
     * 用户登录
     */
    public static final String LOGIN = "logreg/dologin";
    /**
     * 用户注册
     */
    public static final String REGISTER = "logreg/doregister";
    /**
     * 获取用户基本信息
     */
    public static final String USERINFO = "member/info";
    /**
     * 修改用户头像
     */
    public static final String MODIFY_USEIMAGE = "member/head";
    /**
     * 修改用户昵称
     */
    public static final String MODIFY_USER_NICKNAME = "member/nickname";
    /**
     * 修改用户邮箱、qq、微信
     */
    public static final String MODIFY_QQ_WX_EMAIL = "member/pub_info";
    /**
     * 修改手机
     */
    public static final String MODIFY_MOBILE = "member/mobile";
    /**
     * 修改用户生日
     */
    public static final String MODIFY_BIRTHDAY = "member/birthday";
    /**
     * 用户门锁列表
     */
    public static final String DOORLOCK_LIST = "door/lists";
    /**
     * 用户门锁报警记录
     */
    public static final String DOOR_POLICE_RECORD = "door/police_logs";
    /**
     * 洗衣订单列表
     */
    public static final String LAUNDY_ORDER = "order/user_line";
    /**
     * 洗衣订单回复列表
     */
    public static final String ORDER_REPLY = "books/booksrflists";
    /**
     * 回复订单疑问 回复某条
     */
    public static final String REPLY_DOUBT = "books/replay_books";
    /**
     * 洗衣订单疑问列表或单条
     */
    public static final String LAUNDY_ORDER_DOUBT = "books/bookslists";
    /**
     * 发表疑问
     */
    public static final String POST_DOUBT = "books/create_books";

    /**
     * 用户备注订单
     */
    public static final String USER_REMARK_ORDER = "order/UserRemark";
    /**
     * 洗衣订单加急
     */
    public static final String ORDER_ADDITIONAL = "order/additional";

    /**
     * 洗衣订单详情
     */
    public static final String LAUNDY_ORDER_DETAIL = "order/details_o";
    /**
     * 获取城市列表
     */
    public static final String GETCITYLIST = "area/lists";
    /**
     * 根据最后id获取对应城市名字
     */
    public static final String GETCITY_ADDRESS = "area/recursive";
    /**
     * 上传图片
     */
    public static final String POSTIMAGE = "upimg/base64imgupload";

    /**
     * 提现申请
     */
    public static final String WITHDRAW = "money/tx";
    /**
     * 提现记录
     */
    public static final String WITHDRAW_RECORD = "money/txjl";
    /**
     * 地址管理
     */
    public static final String ADDRESS_MANAGE = "address/lists";

    /**
     * 设置为默认地址
     */
    public static final String SET_DEFALT_ADDRESS = "address/defau";
    /**
     * 删除地址
     */
    public static final String DELETE_ADDRESS = "address/delete";
    /**
     * 添加修改地址
     */
    public static final String ADDED_ADDRESS = "address/aded";
    /**
     * 获取银行卡列表
     */
    public static final String BANK_LIST = "bank/lists";
    /**
     * 删除银行卡
     */
    public static final String DELETE_BANK = "bank/delete";
    /**
     * 添加、修改银行卡
     */
    public static final String Bank_ADDED = "bank/aded";
    /**
     * 某条银行卡详情
     */
    public static final String Bank_DETAIL = "bank/info";
    /**
     * 设置默认银行卡
     */
    public static final String BANK_DEFAULT = "bank/defau";
    /**
     * 设置修改支付密码
     */
    public static final String SET_PAY_PASSWORD = "password/pay";
    /**
     * 修改登录密码
     */
    public static final String SET_LOGIN_PASSWORD = "password/login";
    /**
     * 栏目列表
     */
    public static final String COLUMN = "menu/lists";
    /**
     * 支付列表
     */
    public static final String PAY_LIST = "pub/payment";
    /**
     * 资金明细
     */
    public static final String MONEY_DETAIL = "money/lists";
    /**
     * 意见反馈列表
     */
    public static String FEEDBACK_LIST = "message/message_lists";
    /**
     *问题反馈-问题详情+回复列表
     */
    public static final String FEEDBACK_DETAIL = "message/message";
    /**
     * 意见反馈回复
     */
    public static final String FEEDBACL_REPLY = "message/message_replay";
    /**
     * 意见反馈.发表意见
     */
    public  static final String FEEDBACK_SUBMIT = "message/message_submit";
    /**
     * 消息列表
     */
    public static final String MESSAGE_LIST = "message/mlists";
    /**
     * 提交实名认证
     */
    public static final String REAL_authentication = "tied/doiden";
    /**
     * 读取实名认证资料
     */
    public static final String GET_authentication = "tied/iden";
    /**
     * 创建随机码
     */
    public static final String CREATE_RANDOM_CODE = "member/create_random_code";
    /**
     * 获取单页文章
     */
    public static final String ONE_ARTICAL = "news/single";
    /**
     * 文章详情
     */
    public static  final String ARTICAL_DETAIL = "news/details";
    /**
     * 获取平台衣柜列表
     */
    public static final String ARK_LIST = "ark/lists";
    /**
     * 扫码开箱apiType
     */
    public  static final String OPEN_ARK = "UserOpenBox";
    /**
     * 发送心跳apiType
     */
    public  static final String HEART_BEAT = "heartbeat";
    /**
     * 接收衣柜开箱结果 apiType
     */
    public  static  final String GET_OPENARJ_RESULT = "UserOpenBox";
    /**
     * 查询待支付订单接口
     */
    public  static final String QUERY_ORDER_NOPAY ="order/UserQuery";
    /**
     * 余额充值
     */
    public static final String BALANCE_RECHARGE = "money/cz";
    /**
     * 发起支付
     */
    public static final String POST_PAY = "order/PayOrder";
    /**
     * 查询用户资金接口
     */
    public static final String QUARY_MONEY  = "money/money";
    /**
     * 消息已读处理
     */
    public static final String MESSAGE_READ = "message/mread";
    /**
     * 用户存件扫码成功后再根据衣柜的ID返回当前衣柜所支持的洗衣品牌
     */
    public static final String laundy_brand = "ark/QueryBrand";
    /**
     * 用于根据品牌读取洗衣商品列表
     */
    public static final String getBrandGoodsList = "goods/BrandGoods";
    /**
     * 会员喜欢的品牌
     */
    public static final String likeBrand = "member/QueryBrand";
    /**
     * 保存会员喜欢的品牌
     */
    public static final String saveLikeBrand = "member/SaveBrand";
    /**
     * 充值卡充值
     */
    public static  final String recharge_card = "paycard/pay";
    /**
     * 读取所有套餐列表
     */
    public static final String READ_COMBO_LIST = "package/lists";
    /**
     * 套餐详情
     */
    public static final String COMBO_DETAIL = "package/details";
    /**
     * 购买套餐
     */
    public static final String BUY_COMBO = "package/Buy";
    /**
     * 文章列表
     */
    public static final String ARTICAL_LIST = "news/lists";
    /**
     * 获取广告位
     */
    public  static final String HOME_BANNER = "ad/getAdList";
    /**
     * 获取单页文章
     */
    public static final String ARTICAL_SIGLE = "mobile/Article/single?&app=1";
    /**
     * 获取文章详情
     */
    public static final String Artical_DETAIL = "mobile/Article/detail?&app=1";
    /**
     * 查询包月时间
     */
    public static final String MOUTH_COMBO_TIME = "member/QueryPackage";
    /**
     * 绑定微信登录
     */
    public static final String BIND_WX_LOGIN = "logreg/weixin_login";
    /**
     * h5门锁管理界面
     */
    public static final String door_manage_h5 = "user/door/manage?";
    /**
     * 门锁详情接口
     */
    public static final String door_detail = "door/detail";
    /**
     * 门锁事件上报
     */
    public static final String door_event_upService = "door/police_upload";
}
