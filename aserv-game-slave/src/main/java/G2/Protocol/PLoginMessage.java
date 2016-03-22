package G2.Protocol;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.EnumReadable;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.absir.data.value.IProto;

public class PLoginMessage implements IProto {

    @Protobuf(fieldType = FieldType.ENUM, order = 1, required = true)
    protected PLoginStatusType statusType;

    @Protobuf(fieldType = FieldType.INT64, order = 2, required = false)
    protected Long statusValue;

    public PLoginStatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(PLoginStatusType statusType) {
        this.statusType = statusType;
    }

    public Long getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(Long statusValue) {
        this.statusValue = statusValue;
    }

    public PLoginMessage create() {
        return new PLoginMessage();
    }

    public PLoginMessage clone() {
        return cloneDepth(0);
    }

    public void cloneMore(PLoginMessage _clone, int _depth) {
    }

    public PLoginMessage cloneDepth(int _depth) {
        PLoginMessage _clone = create();
        _clone.statusType = statusType;
        _clone.statusValue = statusValue;
        cloneMore(_clone, _depth);
        return _clone;
    }
}
