namespace * tplatform

// 平台来源
struct TPlatformFrom {
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
struct TPlatformFromSetting {
    // 平台来源信息编号
    1: i32 fromId;
    // 审核状态
    2: bool review;
    // 客户端配置
    3: optional TFromSetting setting;
}

// 来源设置
struct TFromSetting {
    // 标题
    1: optional string title = "";
    // 消息
    2: optional string message = "";
    // 强制
    3: optional bool force;
    // 打开地址
    4: optional string openUrl = "";
    // CDN地址
    5: optional string cdnUrl = "";
    // 其他地址
    6: optional string otherUrl = "";
}

// 公告
struct TAnnouncement {
    // 标题
    1: string title;
    // 内容
    2: string content;
    // 附件
    3: string attach;
    // 打开地址
    4: optional string openUrl = "";
}

// 服务
struct TServer {
    // 编号
    1: i64 id;
    // 名称
    2: string name;
    // 服务地址
    3: string sAddress;
    // 端口号
    4: i32 port;
    // 下载地址
    5: optional string dAddress = "";
    // 权重
    6: optional i32 weight;
}

// 授权结果
struct TIdentityResult {
    // 用户编号
    1: i64 userId;
    // 用户数据(透传)
    2: optional string userData;
    // 登陆SessionId
    3: string sessionId;
    // 服务记录
    4: optional list<i64> serverIds;
}

// 登陆错误
enum LoginError
{
    // 成功
    success,
    // 用户不存在
    userNotExist,
    // 密码错误
    passwordError,
    // 未知
    unkown,
}

// 登陆结果
struct TLoginResult {
    // 错误
    1: optional LoginError error;
    // 授权结果
    2: optional TIdentityResult result;
}

// 注册错误
enum RegisterError
{
    // 成功
    success,
    // 格式错误
    validateError,
    // 用户不存在
    usernameExist,
    // 未知
    unkown,
}

// 注册结果
struct TRegisterResult {
    // 错误
    1: optional RegisterError error;
    // 授权结果
    2: optional TIdentityResult result;
}

// 修改密码
enum TPasswordResult
{
    // 成功
    success,
    // 格式错误
    validateError,
    // 密码错误
    passwordError,
    // 未知
    unkown,
}

// 下单信息
struct OrderInfo {
    // 来源编号
    1: optional i32 fromId;
    // 平台
    2: optional string platform;
    // 服务编号
    3: optional i64 serverId;
    // 角色编号
    4: i64 playerId;
    // 编号
    5: string id;
    // 名称
    6: string name;
    // 描述
    7: optional string desc;
    // 价格
    8: optional i32 price;
    // 数量
    9: optional i32 number;
    // 下单参数
    10: optional string orderData;
}

// 下单结果
struct OrderResult {
    // 订单编号
    1: string tradeId;
    // 透传参数
    2: optional string tradeData;
}

// 订单验证
struct OrderValidator {
    // 来源编号
    1: optional i32 fromId;
    // 平台
    2: optional string platform;
    // 服务编号
    3: optional i64 serverId;
    // 角色编号
    4: i64 playerId;
    // 编号
    5: string id;
    // 数量
    6: optional i32 number;
    // 下单参数
    7: optional string orderData;
    // 票据
    8: string receiptId;
}


service PlatformFromService {

	// 来源设置
    TPlatformFromSetting setting(1:TPlatformFrom platformFrom)

    // 公告列表
    list<TAnnouncement> announcements(1:i32 fromId, 2:bool review)

    // 服务列表
    list<TServer> servers(1:i32 fromId, 2:bool review)

    // 授权
    TIdentityResult identity(1:i32 fromId, 2:i64 serverId, 3:string identities)

    // 登陆账号
    TLoginResult login(1:i32 fromId, 2:i64 serverId, 3:string username, 4:string password)

    // 注册账号
    TRegisterResult sign(1:i32 fromId, 2:i64 serverId, 3:string username, 4:string password)

    // 修改密码
    TPasswordResult password(1:string sessionId, 2:string oldPassword, 3:string newPassword)

    // 下订单
    OrderResult order(1:OrderInfo info)

    // 验证订单
    bool validate(1:OrderValidator validator)

}