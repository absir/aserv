package com.absir.consistent;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.consistent.IConsistent;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.data.helper.HelperDataFormat;
import com.absir.redis.service.RedisService;
import com.fasterxml.jackson.core.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.IOException;
import java.lang.reflect.Type;

@Base
@Bean
public class ConsistentService implements IConsistent {

    protected static final byte[] ConfigureChannel = "Cnsstnt@Cnf".getBytes();

    @Inject
    protected void initService() {
        RedisService.ME.addPubSub(ConfigureChannel, new KernelLang.CallbackTemplate<byte[]>() {
            @Override
            public void doWith(byte[] template) {
                Type[] types = new Type[]{String.class, JSONParser.class};
                try {
                    Object[] res = HelperDataFormat.PACK.readArray(template, types);
                    Class<JConfigureBase> configureClass = KernelClass.forName((String) res[0]);
                    if (configureClass != null) {
                        Object configure = JConfigureUtils.findConfigure(configureClass);
                        if (configure != null) {
                            HelperDataFormat.PACK.getMapper().readerForUpdating(configure).readValue((JsonParser) res[1]);
                        }
                    }

                } catch (IOException e) {
                    Environment.throwable(e);
                }
            }
        });
    }

    @Override
    public void pubConfigure(JConfigureBase configureBase) throws IOException {
        byte[] message = HelperDataFormat.PACK.writeAsBytesArray(configureBase.getClass().getName(), configureBase);
        RedisService.ME.getListeningJedis().publish(ConfigureChannel, message);
    }
}
