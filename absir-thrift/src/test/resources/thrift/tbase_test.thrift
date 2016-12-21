namespace * tbase_test

struct TPlatformFrom {
    1: optional string platform;
    2: optional string channel;
    // 安卓.包名 IOS.BunlderId
    3: optional string packageName;
    // 版本号(x.[xx]{n=3}.xxx)|由客户端计算上传
    4: optional double versionDouble;
    // 来源(国际化|别版|测试等)
    5: optional string fromStr;
}

service RpcService {

	// 来源设置
    TPlatformFrom setting(1:TPlatformFrom platformFrom)

    TPlatformFrom setting2(1:TPlatformFrom platformFrom)

    TPlatformFrom setting3(1:TPlatformFrom platformFrom)

}

service PushService {

	// 来源设置
    oneway void setting(1:TPlatformFrom platformFrom)

}