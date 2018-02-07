package com.absir.redis.service;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelLang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.util.*;

@Base
@Bean
public class RedisService {

    public static final RedisService ME = BeanFactoryUtils.get(RedisService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    @Value("redis.uri")
    private String uri = "localhost";

    @Value("redis.timeout")
    private int timeout = 2000;

    @Value("redis.maxIdle")
    private int maxIdle = 10;

    @Value("redis.maxTotal")
    private int maxTotal = 30;

    @Value("redis.maxWaitMillis")
    private int maxWaitMillis = 3000;

    private JedisPool jedisPool;

    private Jedis listeningJedis;

    private List<KernelLang.CallbackTemplate<byte[]>> subRestarts;

    private Map<byte[], List<KernelLang.CallbackTemplate<byte[]>>> subMap;

    private boolean started;

    @Value("redis.subTimeout")
    private int subTimeout = 30000;

    public String getUri() {
        return uri;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public JedisPool getJedisPool() {
        if (jedisPool == null && maxTotal > 0) {
            synchronized (this) {
                if (jedisPool == null) {
                    JedisPoolConfig config = new JedisPoolConfig();
                    config.setMaxIdle(maxIdle);
                    config.setMaxTotal(maxTotal);
                    config.setMaxWaitMillis(maxWaitMillis);
                    jedisPool = new JedisPool(config, URI.create(uri), timeout);
                }
            }
        }

        return jedisPool;
    }

    public Jedis getJedis() {
        return getJedisPool().getResource();
    }

    public Jedis getListeningJedis() {
        if (listeningJedis == null) {
            synchronized (this) {
                if (listeningJedis == null) {
                    listeningJedis = new Jedis(URI.create(uri), timeout) {
                        @Override
                        public void close() {
                            throw new RuntimeException("listeningJedis could not be closed");
                        }
                    };
                }
            }
        }

        return listeningJedis;
    }

    public void addPubSub(byte[] channel, KernelLang.CallbackTemplate<byte[]> callback) {
        if (started) {
            throw new RuntimeException("please addPubSub before startListening");
        }

        if (channel == null) {
            if (subRestarts == null) {
                subRestarts = new ArrayList<KernelLang.CallbackTemplate<byte[]>>();
            }

            subRestarts.add(callback);

        } else {
            if (subMap == null) {
                subMap = new HashMap<byte[], List<KernelLang.CallbackTemplate<byte[]>>>();
            }

            List<KernelLang.CallbackTemplate<byte[]>> callbacks = subMap.get(channel);
            if (callbacks == null) {
                callbacks = new ArrayList<KernelLang.CallbackTemplate<byte[]>>();
                subMap.put(channel, callbacks);
            }

            callbacks.add(callback);
        }
    }

    @Started
    protected void startListening() {
        started = true;
        if (subMap != null) {
            subMap = Collections.unmodifiableMap(subMap);
            new Thread() {
                @Override
                public void run() {
                    byte[][] channels = KernelCollection.toArray(subMap.keySet(), byte[].class);
                    final BinaryJedisPubSub pubSub = new BinaryJedisPubSub() {
                        @Override
                        public void onMessage(byte[] channel, byte[] message) {
                            pubSub(channel, message);
                        }
                    };

                    int waiteTime;
                    while (true) {
                        try {
                            waiteTime = ContextUtils.getContextShortTime();
                            getListeningJedis().subscribe(pubSub, channels);
                            waiteTime = subTimeout - (ContextUtils.getContextShortTime() - waiteTime);
                            if (waiteTime > 0) {
                                try {
                                    Thread.sleep(waiteTime < subTimeout ? waiteTime : subTimeout);

                                } catch (Exception e) {
                                    break;
                                }
                            }

                        } catch (Exception e) {
                            pubSub(null, null);
                            continue;
                        }
                    }
                }
            }.start();
        }
    }

    protected void pubSub(byte[] channel, byte[] message) {
        List<KernelLang.CallbackTemplate<byte[]>> callbacks = channel == null ? subRestarts : subMap.get(channel);
        if (callbacks != null) {
            for (KernelLang.CallbackTemplate<byte[]> callback : callbacks) {
                try {
                    callback.doWith(message);

                } catch (Throwable e) {
                    LOGGER.error("pubSub error at " + channel + " : " + message, e);
                }
            }
        }
    }
}
