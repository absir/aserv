package com.absir.proto.test;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class ActivateHorseGeniusJProtoBufProtoClass {
    @Protobuf(fieldType = FieldType.INT64, order = 1, required = true)
    public Long horseId;
    @Protobuf(fieldType = FieldType.INT64, order = 2, required = true)
    public Long geniusId;
    @Protobuf(fieldType = FieldType.INT32, order = 3, required = true)
    public Integer index;
}
