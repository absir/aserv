package G2.Protocol;

import com.absir.data.value.IProto;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class PLoginData implements IProto {

    public static final Codec<PLoginData> CODEC = ProtobufProxy.create(PLoginData.class);

    @Protobuf(fieldType = FieldType.STRING, order = 1, required = true)
    protected String sessionId;

    @Protobuf(fieldType = FieldType.INT64, order = 2, required = true)
    protected long loginTime;

    @Protobuf(fieldType = FieldType.INT64, order = 3, required = true)
    protected long serverId;

    @Protobuf(fieldType = FieldType.INT64, order = 4, required = true)
    protected long playerId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public PLoginData create() {
        return new PLoginData();
    }

    public PLoginData clone() {
        return cloneDepth(0);
    }

    public void cloneMore(PLoginData _clone, int _depth) {
    }

    public PLoginData cloneDepth(int _depth) {
        PLoginData _clone = create();
        _clone.sessionId = sessionId;
        _clone.loginTime = loginTime;
        _clone.serverId = serverId;
        _clone.playerId = playerId;
        cloneMore(_clone, _depth);
        return _clone;
    }
}
