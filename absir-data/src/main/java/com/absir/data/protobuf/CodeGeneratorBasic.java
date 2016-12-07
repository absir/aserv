package com.absir.data.protobuf;

import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelReflect;
import com.baidu.bjf.remoting.protobuf.CodeGenerator;
import com.baidu.bjf.remoting.protobuf.utils.FieldInfo;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by absir on 2016/12/8.
 */
public class CodeGeneratorBasic extends CodeGenerator {

    public CodeGeneratorBasic(List<FieldInfo> fields, Class<?> cls) {
        super(fields, cls);
    }

    Object instance;

    protected Object getInstance(Class<?> cls) {
        if (instance == null) {
            instance = KernelClass.declaredNew(cls);
        }

        return instance;
    }

    private Field firstField;

    private boolean circleField;

    Map<String, String> codedConstant_isNull_field_replaces = new HashMap<String, String>();

    @Override
    protected String getAccessByField(String target, Field field, Class<?> cls) {
        String access = super.getAccessByField(target, field, cls);
        if (!circleField) {
            if (firstField == null) {
                firstField = field;

            } else {
                if (firstField == field) {
                    circleField = true;
                    return access;
                }
            }

            field.setAccessible(true);
            Object defaultValue = KernelReflect.get(getInstance(cls), field);
            if (defaultValue != null && defaultValue instanceof Number) {
                codedConstant_isNull_field_replaces.put("!CodedConstant.isNull(" + access + ")", access + " != " + defaultValue);
            }
        }

        return access;
    }

    @Override
    public String getCode() {
        String code = super.getCode();
        if (!codedConstant_isNull_field_replaces.isEmpty()) {
            for (Map.Entry<String, String> entry : codedConstant_isNull_field_replaces.entrySet()) {
                code = code.replaceAll(entry.getKey(), entry.getValue());
                code = StringUtils.replaceEach(code, KernelCollection.toArray(codedConstant_isNull_field_replaces.keySet(), String.class), KernelCollection.toArray(codedConstant_isNull_field_replaces.values(), String.class));
            }
        }

        return code;
    }
}
