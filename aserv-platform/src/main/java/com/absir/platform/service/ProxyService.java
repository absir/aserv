package com.absir.platform.service;

import com.absir.aserv.system.domain.DCacheOpenEntity;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.client.helper.HelperClient;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperIO;
import com.absir.platform.bean.JProxy;
import com.absir.platform.bean.JProxyRecord;
import com.absir.servlet.IFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.Map;

@Base
@Bean
public class ProxyService implements IFilter {

    public static final ProxyService ME = BeanFactoryUtils.get(ProxyService.class);

    protected DCacheOpenEntity<JProxy> proxyDCacheOpen;

    @Override
    public boolean doFilter(String uri, HttpServletRequest req, HttpServletResponse res) throws Throwable {
        Map<Serializable, JProxy> cacheMap = proxyDCacheOpen.getCacheMap();
        if (cacheMap != null && !cacheMap.isEmpty()) {
            for (JProxy redirect : cacheMap.values()) {
                if (uri.startsWith(redirect.getId())) {
                    String url = redirect.getRedirectAddress() + uri.substring(redirect.getId().length()) + "?" + req.getQueryString();
                    byte[] postData = null;
                    try {
                        InputStream inputStream = req.getInputStream();
                        if (inputStream != null) {
                            postData = HelperIO.toByteArray(inputStream);
                        }

                    } catch (Exception e) {
                        Environment.throwable(e);
                    }

                    if (redirect.isRecord()) {
                        JProxyRecord proxyRecord = new JProxyRecord();
                        proxyRecord.setUrl(url);
                        proxyRecord.setPostData(postData);
                        BeanService.ME.persist(proxyRecord);
                    }

                    proxyRequest(url, postData, res);
                    return true;
                }
            }
        }

        return false;
    }

    @Inject
    protected void initService() {
        proxyDCacheOpen = new DCacheOpenEntity<JProxy>(JProxy.class, null);
        proxyDCacheOpen.addEntityMerges();
        proxyDCacheOpen.reloadCacheTransaction();
    }

    public void proxyRequest(String url, byte[] postData, HttpServletResponse res) throws IOException {
        HttpURLConnection httpURLConnection = HelperClient.openConnection(url, false, postData, 0, 0);
        if (res == null) {
            HelperIO.toByteArray(httpURLConnection.getInputStream());

        } else {
            HelperIO.copy(httpURLConnection.getInputStream(), res.getOutputStream());
        }
    }

}
