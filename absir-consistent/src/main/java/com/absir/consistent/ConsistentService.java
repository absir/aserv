package com.absir.consistent;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.consistent.ConsistentUtils;
import com.absir.aserv.consistent.IConsistent;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Value;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.data.helper.HelperDataFormat;
import com.absir.group.service.ClusterService;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.redis.service.RedisService;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.lang.reflect.Type;

@Base
@Bean
public class ConsistentService implements IConsistent {

    protected static final byte[] ConfigureChannel = "Cns@Cnf".getBytes();

    protected static final byte[] MergeEntityChannel = "Cns@Entity".getBytes();

    @Value("consistent.mergeEntity")
    private boolean mergeEntity = true;

    protected static final Type[] ReadTypes = new Type[]{String.class, JsonParser.class};

    @Inject
    protected void initService() {
        RedisService.ME.addPubSub(null, new KernelLang.CallbackTemplate<byte[]>() {
            @Override
            public void doWith(byte[] template) {
                // 重连重载
                ConsistentUtils.reloadAll();
            }
        });

        RedisService.ME.addPubSub(ConfigureChannel, new KernelLang.CallbackTemplate<byte[]>() {
            @Override
            public void doWith(byte[] template) {
                try {
                    Object[] res = HelperDataFormat.PACK.readArray(template, ReadTypes);
                    if (!ClusterService.ME.getNodeId().equals(res[0])) {
                        JsonParser jsonParser = (JsonParser) res[1];
                        Class<JConfigureBase> configureClass = KernelClass.forName((String) HelperDataFormat.PACK.getMapper().readValue(jsonParser, String.class));
                        if (configureClass != null) {
                            Object configure = JConfigureUtils.findConfigure(configureClass);
                            if (configure != null) {
                                HelperDataFormat.PACK.getMapper().readerForUpdating(configure).readValue(jsonParser);
                            }
                        }
                    }

                } catch (IOException e) {
                    Environment.throwable(e);
                }
            }
        });

        RedisService.ME.addPubSub(MergeEntityChannel, new KernelLang.CallbackTemplate<byte[]>() {
            @Override
            public void doWith(byte[] template) {
                try {
                    Object[] res = HelperDataFormat.PACK.readArray(template, ReadTypes);
                    if (!ClusterService.ME.getNodeId().equals(res[0])) {
                        JsonParser jsonParser = (JsonParser) res[1];
                        String entityKey = HelperDataFormat.PACK.getMapper().readValue(jsonParser, String.class);
                        if (ConsistentUtils.support(entityKey)) {
                            String entityName = HelperDataFormat.PACK.getMapper().readValue(jsonParser, String.class);
                            Class<?> entityClass = KernelClass.forName(entityName);
                            if (entityClass != null) {
                                Object entity = HelperDataFormat.PACK.getMapper().readValue(jsonParser, entityClass);
                                IEntityMerge.MergeType mergeType = HelperDataFormat.PACK.getMapper().readValue(jsonParser, IEntityMerge.MergeType.class);
                                ConsistentUtils.merge(entityKey, entityName, entity, mergeType);
                            }
                        }
                    }

                } catch (IOException e) {
                    Environment.throwable(e);
                }
            }
        });
    }

    @Override
    public void pubConfigure(JConfigureBase configureBase) {
        byte[] message;
        try {
            message = HelperDataFormat.PACK.writeAsBytesArray(ClusterService.ME.getNodeId(), configureBase.getClass().getName(), configureBase);

        } catch (IOException e) {
            Environment.throwable(e);
            return;
        }

        RedisService.ME.getListeningJedis().publish(ConfigureChannel, message);
    }

    @Override
    public boolean isMergeEntity(String entityName, Class<?> entityClass) {
        return mergeEntity;
    }

    @Override
    public void pubMergeEntity(String entityKey, String entityName, Object entity, IEntityMerge.MergeType mergeType) {
        byte[] message;
        try {
            message = HelperDataFormat.PACK.writeAsBytesArray(ClusterService.ME.getNodeId(), entityKey, entityName, entity, mergeType.ordinal());

        } catch (IOException e) {
            Environment.throwable(e);
            return;
        }

        RedisService.ME.getListeningJedis().publish(MergeEntityChannel, message);
    }
}
