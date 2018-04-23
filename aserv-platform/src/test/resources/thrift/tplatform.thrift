namespace * tplatform

// 平台来源
struct DPlatformFrom {
    1: string platform;
    2: string channel;
    // 安卓.包名 IOS.BunlderId
    3: string packageName;
    // 版本号(x.[xx]{n=3}.xxx)|由客户端计算上传
    4: double versionDouble;
    // 来源(国际化|别版|测试等)
    5: string fromStr;
}

// 平台来源审核
struct DPlatformFromSetting {
    // 平台来源信息编号
    1: i32 fromId;
    // 审核状态
    2: bool review;
    // 客户端配置
    3: optional DFromSetting setting;
    // 客户端分组
    4: optional string group;
}

// 来源设置
struct DFromSetting {
    // 标题
    1: optional string title = "";
    // 消息
    2: optional string content = "";
    // 类型
    3: optional i32 type;
    // 打开地址
    4: optional string openUrl = "";
    // 最小资源版本
    5: optional string minVersion = "";
    // 服务地址
    6: optional string serverUrl = "";
    // CDN地址
    7: optional string cdnUrl = "";
}

// 公告
struct DAnnouncement {
    // 标题
    1: string title;
    // 内容
    2: string content;
    // 图片
    3: optional string image;
    // 图片隐藏
    4: optional bool imageHide;
    // 打开地址
    5: optional string openUrl = "";
}

// 服务
struct DServer {
    // 编号
    1: i64 id;
    // 名称
    2: string name;
    // 服务地址
    3: string sAddress;
    // 服务地址
    4: string sAddressV6;
    // 端口号
    5: i32 port;
    // 下载地址
    6: optional string dAddress = "";
    // 权重
    7: optional i32 weight;
    // 状态
    8: optional EServerStatus status;
}

// 服务状态
enum EServerStatus {
    // 开放,
    open,
    // 新服
    newly,
    // 等待
    wait,
    // 维护
    maintain,
    // 爆满,
    full,
    // 审核
    review,
    // 测试
    test,
}

// 授权结果
struct DIdentityResult {
    // 用户编号
    1: i64 userId;
    // 用户数据(透传)
    2: optional string userData;
    // 登陆SessionId
    3: optional string sessionId;
    // 服务记录
    4: optional list<i64> serverIds;
}

// 登陆错误
enum ELoginError
{
    // 成功
    success,
    // 用户不存在
    userNotExist,
    // 密码错误
    passwordError,
    // 密码错误次数太多
    passwordErrorMax,
    // 未知
    unkown,
}

// 登陆结果
struct DLoginResult {
    // 错误
    1: optional ELoginError error;
    // 授权结果
    2: optional DIdentityResult result;
    // 用户编号
    3: optional i64 userId;
}

// 注册错误
enum ERegisterError
{
    // 成功
    success,
    // 用户名错误
    usernameError,
    // 密码错误
    passwordError,
    // 用户已存在
    usernameExist,
    // 未知
    unkown,
}

// 注册结果
struct DRegisterResult {
    // 错误
    1: optional ERegisterError error;
    // 授权结果
    2: optional DIdentityResult result;
    // 用户编号
    3: optional i64 userId;
}

// 修改密码
enum EPasswordResult
{
    // 成功
    success,
    // 格式错误
    validateError,
    // 密码错误
    passwordError,
    // 密码错误次数太多
    passwordErrorMax,
    // 未知
    unkown,
}

// 下单信息
struct DOrderInfo {
    // 准备参数
    1: optional string prepare;
    // 配置编号
    2: optional i32 configureId;
    // 平台
    3: optional string platform;
    // 平台参数
    4: optional string platformData;
    // 商品编号
    5: optional string goodsId;
    // 商品数量
    6: optional i32 goodsNumber;
    // 金额
    7: optional double amount;
    // 用户编号
    8: optional i64 userId;
    // 服务区编号
    9: optional i64 serverId;
    // 角色编号
    10: optional i64 playerId;
    // 短订单编号
    11: optional bool shortTradeId;
    // 订单参数
    12: optional i32 tradeData;
    // 更多参数
    13: optional list<string> moreDatas;
}

// 下单结果
struct DOrderResult {
    // 订单编号
    1: string tradeId;
    // 透传参数
    2: optional string tradeData;
}

// 订单验证
struct DOrderValidator {
    // 订单编号
    1: string tradeId;
    // 配置编号
    2: optional i32 configureId;
    // 平台
    3: optional string platform;
    // 平台参数
    4: optional string platformData;
    // 交易号
    5: optional string tradeNo;
    // 交易票据
    6: optional string tradeReceipt;
    // 沙盒测试
    7: optional bool sanbox;
    // 更多参数
    8: optional list<string> moreDatas;
}

service PlatformFromService {

	// 来源设置
    DPlatformFromSetting setting(1:DPlatformFrom platformFrom, 2:string versionName)

    // 公告列表
    list<DAnnouncement> announcements(1:i32 fromId, 2:bool review, 3:string group)

    // 服务列表
    list<DServer> servers(1:i32 fromId, 2:bool review, 3:string group)

    // 授权
    DIdentityResult identity(1:i32 fromId, 2:i64 lastUserId, 3:string identity)

    // 登陆账号
    DLoginResult login(1:i32 fromId, 2:i64 lastUserId, 3:string username, 4:string password)

    // 游客登录
    DIdentityResult loginUUID(1:i32 fromId, 2:i64 lastUserId, 3:string uuid)

    // 注册账号
    DRegisterResult sign(1:i32 fromId, 2:string username, 3:string password)

    // 注册账号(绑定游客)
    DRegisterResult signUUID(1:i32 fromId, 2:string username, 3:string password, 4:string uuid)

    // 修改密码
    EPasswordResult password(1:i64 userId, 2:string sessionId, 3:string oldPassword, 4:string newPassword)

    // 进入游戏
    void enter(1:i64 userId, 2:string sessionId, 3:i64 serverId);

    // 下订单
    DOrderResult order(1:i32 fromId, 2:DOrderInfo info)

    // 验证订单
    bool validate(1:i32 fromId, 2:DOrderValidator validator)

}