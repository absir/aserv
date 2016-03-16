/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-14 下午7:21:33
 */
package org.hibernate.type;

import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelObject;
import com.absir.orm.value.JiType;
import org.hibernate.MappingException;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@SuppressWarnings({"serial"})
public class TypeResolverLocal extends TypeResolver {

    private Set<JiType> jiTypes = new HashSet<JiType>();

    public TypeResolverLocal(TypeResolver typeResolver) {
        KernelObject.clone(typeResolver, this);
    }

    @Override
    public void registerTypeOverride(BasicType type) {
        super.registerTypeOverride(type);
        if (type instanceof JiType) {
            jiTypes.add((JiType) type);
        }
    }

    @Override
    public Type heuristicType(String typeName, Properties parameters) throws MappingException {
        Type type = basic(typeName);
        if (type != null) {
            return type;
        }

        Class<?> typeClass = KernelClass.forName(typeName);
        if (typeClass != null) {
            type = getTypeFactory().byClass(typeClass, parameters);
            if (type != null) {
                return type;
            }

            for (JiType jiType : jiTypes) {
                type = jiType.byClass(typeClass, parameters);
                if (type != null) {
                    return type;
                }
            }
        }

        return null;
    }

}
