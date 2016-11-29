package com.absir.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by absir on 2016/11/29.
 */
public interface IFilter {

    public boolean doFilter(String uri, HttpServletRequest req, HttpServletResponse res) throws Throwable;

}
