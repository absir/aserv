/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年5月5日 下午7:58:19
 */
package com.absir.master.bean.dto;

import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 *
 */
public class DServer {

	@JaLang(value = "服务编号", tag = "serverId")
	public long id;

	@JaLang("名称")
	public String name;

	@JaLang("开启时间")
	public int openTime;

	@JaLang("IP")
	public String ip;

	@JaLang("端口")
	public int port;

	@JaLang("状态")
	// 0 代开 1 开启 2 维护
	public int status;

	@JaLang("路径")
	public String path;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the openTime
	 */
	public int getOpenTime() {
		return openTime;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

}
