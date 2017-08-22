/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-25 上午10:43:47
 */
package com.absir.server.route;

import com.absir.bean.basis.Base;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.config.IBeanFactoryStarted;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelUtil;
import com.absir.server.in.IDispatcher;
import com.absir.server.in.InMatcher;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Base
@Bean
public class RouteAdapter implements IBeanFactoryStarted {

    public static final Comparator<InMatcher> IN_MATCHER_COMPARATOR = new Comparator<InMatcher>() {

        @Override
        public int compare(InMatcher o1, InMatcher o2) {
            int compare = KernelUtil.compareNo(o1.getMapping(), o2.getMapping());
            if (compare == 0) {
                compare = KernelUtil.compareEndNoNull(o1.getSuffix(), o2.getSuffix());
                if (compare == 0) {
                    compare = o1.getMapping().length + o1.getSuffixLength() - o2.getMapping().length
                            - o2.getSuffixLength();
                    if (compare == 0) {
                        compare = o1.getParameterLength() - o2.getParameterLength();
                    }
                }
            }

            return compare;
        }
    };

    public static final Comparator<RouteMatcher> ROUTE_MATCHER_COMPARATOR = new Comparator<RouteMatcher>() {

        @Override
        public int compare(RouteMatcher o1, RouteMatcher o2) {
            int compare = IN_MATCHER_COMPARATOR.compare(o1, o2);
            if (compare == 0) {
                int len1 = o1.getInMethodLength();
                int len2 = o2.getInMethodLength();
                compare = len1 == 0 || len2 == 0 ? len1 - len2 : len2 - len1;
            }

            return compare;
        }

    };

    protected static final Logger LOGGER = LoggerFactory.getLogger(RouteAdapter.class);
    private static int varintsMapUriIndex;
    private static Map<Integer, String> varintsMapUri;
    private static Map<String, Integer> uriMapVarints;
    private boolean started;
    private List<RouteMatcher> routeMatchers = new ArrayList<RouteMatcher>();

    // URI 字典压缩
    public static int addVarintsMapUri(String uri) {
        if (varintsMapUri == null) {
            synchronized (RouteAdapter.class) {
                if (varintsMapUri == null) {
                    varintsMapUri = new HashMap<Integer, String>();
                    uriMapVarints = new HashMap<String, Integer>();
                }
            }
        }

        Integer varints = uriMapVarints.get(uri);
        if (varints != null) {
            return varints;
        }

        synchronized (varintsMapUri) {
            varints = uriMapVarints.get(uri);
            if (varints != null) {
                return varints;
            }

            varintsMapUri.put(++varintsMapUriIndex, uri);
            uriMapVarints.put(uri, varintsMapUriIndex);
            return varintsMapUriIndex;
        }
    }

    public static String UriForVarints(Integer varints) {
        return varintsMapUri == null ? null : varintsMapUri.get(varints);
    }

    /**
     * 路由匹配比较
     */
    public static int compare(InMatcher inMatcher, byte[] uries, int length) {
        byte[] to = inMatcher.getMapping();
        int toLength = to.length;
        int compare = KernelUtil.compareNo(uries, to, length, toLength);
        if (compare == 0) {
            to = inMatcher.getSuffix();
            if (to != null) {
                compare = KernelUtil.compareEndNo(uries, to, length - toLength, to.length);
            }
        }

        return compare;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public List<RouteMatcher> getRouteMatchers() {
        return routeMatchers;
    }

    /**
     * 路由匹配
     */
    public <T> Object[] route(String uri, IDispatcher<T> dispatcher, T req) {
        if (!started) {
            return null;
        }

        List<RouteMatcher> matchers = routeMatchers;
        byte[] uries = uri.getBytes();
        int length = uries.length;
        int max = matchers.size();
        if (max < 1) {
            return null;
        }

        int min = 0;
        if (max == 1) {
            max = compare(matchers.get(0), uries, length) == 0 ? 0 : -1;

        } else {
            // find match min - max
            int m;
            int compare = 0;
            int mMin = Integer.MAX_VALUE;
            int mMax = -1;
            int mmMin = -1;
            while (min < max) {
                m = (min + max) >> 1;
                if (m == min) {
                    if (min == max || mMax != mmMin) {
                        break;
                    }
                }

                compare = compare(matchers.get(m), uries, length);
                if (compare < 0) {
                    max = m;

                } else {
                    if (compare == 0) {
                        if (mMin > m) {
                            mMin = m;
                        }

                        if (mMax < m) {
                            mMax = m;
                        }

                    } else {
                        if (mmMin < m) {
                            mmMin = m;
                        }
                    }

                    min = m;
                }
            }

            if (mMax == -1) {
                max = -1;

            } else {
                while (mmMin++ < mMin) {
                    if (compare(matchers.get(mmMin), uries, length) == 0) {
                        mMin = mmMin;
                        break;
                    }
                }

                min = mMin;
                max = mMax;
            }
        }

        if (max >= 0) {
            InMethod inMethod = dispatcher.getInMethod(req);
            String parameterPath = null;
            String[] parameters = null;
            int mlen = -1, slen = -1;
            int mmlen, imlen, islen;
            RouteParameter mRouteParameter;
            RouteParameter routeParameter = null;
            boolean urlDecode = false;
            for (; max >= min; max--) {
                // 路由匹配
                RouteMatcher routeMatcher = matchers.get(max);
                if (routeMatcher.find(inMethod)) {
                    imlen = routeMatcher.getMapping().length;
                    islen = routeMatcher.getSuffixLength();
                    // 新路由参数
                    if (!urlDecode && routeMatcher.getRouteAction().isUrlDecode()) {
                        // 需要URL解码
                        urlDecode = true;
                        String duri = dispatcher.decodeUri(uri, req);
                        if (duri != null && uri.equals(duri)) {
                            uries = uri.getBytes();
                            length = uries.length;
                        }
                    }

                    // 参数剩余长度
                    mmlen = length - (imlen + islen);
                    if (mmlen < 0) {
                        continue;
                    }

                    // 路由参数对象
                    mRouteParameter = routeMatcher.getRouteParameter();
                    if (mmlen == 0) {
                        if (mRouteParameter == null) {
                            return new Object[]{routeMatcher, inMethod, new InModel()};
                        }

                        continue;
                    }

                    if (mRouteParameter == null) {
                        continue;
                    }

                    // 路由参数位数
                    if (mmlen < routeMatcher.getParameterLength() * 2 - 1) {
                        continue;
                    }

                    // 通用路由参数
                    if (mlen == imlen && slen == islen) {
                        if (routeParameter != mRouteParameter && (parameters.length != 1 || mRouteParameter.getClass() != RouteParameter.class)) {
                            routeParameter = mRouteParameter;
                            parameters = routeParameter.findParameters(parameterPath);
                        }

                    } else {
                        mlen = imlen;
                        slen = islen;
                        parameterPath = new String(uries, mlen, length - slen - mlen);
                        String[] mParameters = mRouteParameter.findParameters(parameterPath);
                        if (mParameters == null) {
                            continue;
                        }

                        parameters = mParameters;
                        routeParameter = mRouteParameter;
                    }

                    // 路由参数匹配
                    InModel model = routeMatcher.find(parameters);
                    if (model != null) {
                        return urlDecode ? new Object[]{routeMatcher, inMethod, model, uri}
                                : new Object[]{routeMatcher, inMethod, model};
                    }
                }
            }
        }

        return null;
    }

    /**
     * 注册全部匹配
     */
    public void registerAllMatcher() {
        registerAllMatcher(routeMatchers);
    }

    public void registerAllMatcher(List<RouteMatcher> matchers) {
        Collections.sort(matchers, ROUTE_MATCHER_COMPARATOR);
        if (routeMatchers.size() > 1) {
            RouteMatcher routeMatcher = routeMatchers.get(0);
            if (routeMatcher.getMapping().length == 0 && routeMatcher.getSuffixLength() == 0) {
                matchers.add(routeMatcher);
            }
        }

        routeMatchers = matchers;
        for (RouteMatcher routeMatcher : routeMatchers) {
            LOGGER.info(routeMatcher.toString());
        }
    }

    @Override
    public int getOrder() {
        return 2048;
    }

    @Override
    public void started(BeanFactory beanFactory) {
        LOGGER.info("started server");
        started = true;
    }

}
