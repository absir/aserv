package com.absir.aserv.lang;

import com.absir.context.lang.LangBundle;
import com.absir.core.kernel.KernelLang;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 16/7/27.
 */
public class LangProperty<T> {

    protected String lang;

    protected T property;

    protected Map<Integer, Object[]> langMapProperty;

    public T getProperty(String value, KernelLang.GetTemplate<T, String> getTemplate) {
        if (LangBundle.isI18n() && LangBundle.ME != null) {
            OnPut onPut = OnPut.get();
            if (onPut != null) {
                Input input = onPut.getInput();
                Integer localCode = input.getLocalCode();
                if (localCode != LangBundle.ME.getLocaleCode()) {
                    if (langMapProperty == null) {
                        langMapProperty = new HashMap<Integer, Object[]>();
                    }

                    Object[] properties = langMapProperty.get(localCode);
                    boolean newProperties = false;
                    if (properties == null) {
                        newProperties = true;
                        properties = new Object[]{null, null};
                    }

                    if (properties[0] != value) {
                        properties[0] = value;
                        properties[1] = getTemplate.getWith(value);
                    }

                    if (newProperties) {
                        langMapProperty.put(localCode, properties);
                    }

                    return (T) properties[1];
                }
            }
        }

        if (lang != value) {
            property = getTemplate.getWith(value);
        }

        return property;
    }

}
