package G2.Protocol;

import com.absir.data.base.DDirtyM;
import com.absir.data.value.ADirtyM;
import com.absir.data.value.IProto;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

@ADirtyM
public class PActivateHorseGenius extends DDirtyM implements IProto {

    @Protobuf(fieldType = FieldType.INT64, order = 1, required = true)
    protected Long horseId;

    @Protobuf(fieldType = FieldType.INT64, order = 2, required = true)
    protected Long geniusId;

    @Protobuf(fieldType = FieldType.INT32, order = 4, required = true)
    protected Integer index;

    public Long getHorseId() {
        return horseId;
    }

    public void setHorseId(Long horseId) {
        this.horseId = horseId;
        setDirtyI(0);
    }

    public Long getGeniusId() {
        return geniusId;
    }

    public void setGeniusId(Long geniusId) {
        this.geniusId = geniusId;
        setDirtyI(1);
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
        setDirtyI(2);
    }

    public PActivateHorseGenius create() {
        return new PActivateHorseGenius();
    }

    public PActivateHorseGenius clone() {
        return cloneDepth(0);
    }

    public void cloneMore(PActivateHorseGenius _clone, int _depth) {
    }

    public PActivateHorseGenius cloneDepth(int _depth) {
        PActivateHorseGenius _clone = create();
        _clone.horseId = horseId;
        _clone.geniusId = geniusId;
        _clone.index = index;
        cloneMore(_clone, _depth);
        return _clone;
    }

    public void mergeDirty(PActivateHorseGenius _clone) {
        if (isDirtyI(0)) {
            _clone.horseId = horseId;
        }
        if (isDirtyI(1)) {
            _clone.geniusId = geniusId;
        }
        if (isDirtyI(2)) {
            _clone.index = index;
        }
    }
}
