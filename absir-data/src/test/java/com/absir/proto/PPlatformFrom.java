package com.absir.proto;

import com.absir.data.value.IProto;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class PPlatformFrom implements IProto {

    @Protobuf(fieldType = FieldType.INT64, order = 1, required = false)
    protected long id = -1;

    @Protobuf(fieldType = FieldType.INT64, order = 2, required = false)
    protected Long id2;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PPlatformFrom create() {
        return new PPlatformFrom();
    }

    public PPlatformFrom clone() {
        return cloneDepth(0);
    }

    public void cloneMore(PPlatformFrom _clone, int _depth) {
    }

    public Long getId2() {
        return id2;
    }

    public void setId2(Long id2) {
        this.id2 = id2;
    }

    public PPlatformFrom cloneDepth(int _depth) {
        PPlatformFrom _clone = create();
        _clone.id = id;
        _clone.id2 = id2;
        cloneMore(_clone, _depth);
        return _clone;
    }
}
