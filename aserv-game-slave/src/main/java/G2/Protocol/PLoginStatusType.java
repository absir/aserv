package G2.Protocol;

import com.baidu.bjf.remoting.protobuf.EnumReadable;

public enum PLoginStatusType implements EnumReadable {

    Success(1), ServerClosed(2), LoginFailed(3), LoginLose(4), DinedDevice(5), PlayerBaned(6);

    private final int value;

    PLoginStatusType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
