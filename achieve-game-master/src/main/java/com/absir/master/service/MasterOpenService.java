/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月19日 下午4:24:13
 */
package com.absir.master.service;

import java.util.regex.Pattern;

import javax.crypto.spec.SecretKeySpec;

import com.absir.aserv.system.crud.PasswordCrudFactory;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Value;
import com.absir.client.helper.HelperEncrypt;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAbsir;
import com.absir.master.bean.JRegister;
import com.absir.platform.bean.JPlatformUser;
import com.absir.platform.service.PlatformService;
import com.absir.validator.ValidatorEmail;

/**
 * @author absir
 *
 */
@Base
@Bean
public class MasterOpenService {

	/** ME */
	public static final MasterOpenService ME = BeanFactoryUtils.get(MasterOpenService.class);

	/** pattern */
	public static final Pattern pattern = Pattern.compile("[^a-zA-Z0-9/_]+");

	@Value("master.open.secrect")
	protected String secrect = "absir@qq.com";

	/** aesSecretKeySpec */
	protected SecretKeySpec aesSecretKeySpec;

	@Value("master.open.lifeTime")
	protected long lifeTime = UtilAbsir.DAY_TIME;

	/**
	 * 
	 */
	@Inject
	protected void inject() {
		aesSecretKeySpec = HelperEncrypt.getSecretKeySpec("AES", secrect, 16);
	}

	/**
	 * 注册账号
	 * 
	 * 1 注册成功
	 * 
	 * 2 用户名不正确
	 * 
	 * 3 密码不正确
	 * 
	 * 4 邮箱格式不正确
	 * 
	 * 5 用户名存在
	 * 
	 * @param username
	 * @param password
	 * @param email
	 * @param platformUserId
	 * @return
	 */
	public int register(String username, String password, String email, long platformUserId) {
		if (username.length() < 4 || username.length() > 16) {
			return 2;
		}

		password = HelperEncrypt.aesDecryptBase64(password, aesSecretKeySpec);
		if (password.length() < 4 || password.length() > 16) {
			return 3;
		}

		if (!KernelString.isEmpty(email) && !ValidatorEmail.PATTERN.matcher(email).find()) {
			return 4;
		}

		JRegister register = new JRegister();
		username = username.toLowerCase();
		register.setId(username);
		String salt = Integer.toHexString(username.hashCode());
		register.setSalt(salt);
		register.setPassword(PasswordCrudFactory.getPasswordEncrypt(password, salt));
		register.setPlatformUserId(platformUserId);
		register.setEmail(email);
		try {
			BeanService.ME.persist(register);
			return 1;

		} catch (Exception e) {
			return 5;
		}
	}

	/**
	 * 登录账号
	 * 
	 * 1.成功 platform,username,sessionId
	 * 
	 * 2.用户名不存在 2
	 * 
	 * 3.失败 3
	 * 
	 * @param username
	 * @param password
	 * @param time
	 * @param channel
	 * @return
	 */
	public String login(String username, String password, int time, String channel) {
		JRegister register = BeanService.ME.get(JRegister.class, username);
		if (register == null) {
			return "2";
		}

		password = HelperEncrypt.aesDecryptBase64(password, aesSecretKeySpec);
		String timeStr = String.valueOf(time);
		if (password.startsWith(timeStr)) {
			password = password.substring(timeStr.length());
			if (register.getPassword().equals(PasswordCrudFactory.getPasswordEncrypt(password, register.getSalt()))) {
				JPlatformUser platformUser = register.getPlatformUserId() > 0
						? BeanService.ME.get(JPlatformUser.class, register.getPlatformUserId()) : null;
				if (platformUser == null) {
					platformUser = PlatformService.ME.reLoginPlatformUser("JUser", username, channel, lifeTime);
					register.setPlatformUserId(platformUser.getId());
					BeanService.ME.merge(register);

				} else {
					PlatformService.ME.loginSessionUserType(platformUser, lifeTime, 1);
				}

				return platformUser.getId() + "," + platformUser.getSessionId();
			}
		}

		return "3";
	}

	/**
	 * 修改密码邮箱
	 * 
	 * 1 注册成功
	 * 
	 * 2 用户名存在
	 * 
	 * 3 密码不正确
	 * 
	 * 4 邮箱格式不正确
	 * 
	 * 
	 * @param username
	 * @param password
	 * @param time
	 * @param newPassword
	 * @param email
	 * @return
	 */
	public int rePassword(String username, String password, int time, String newPassword, String email) {
		JRegister register = BeanService.ME.get(JRegister.class, username);
		if (register == null) {
			return 2;
		}

		if (!KernelString.isEmpty(newPassword)) {
			newPassword = HelperEncrypt.aesDecryptBase64(newPassword, aesSecretKeySpec);
			if (newPassword.length() < 4 || newPassword.length() > 16) {
				return 3;
			}
		}

		if (!KernelString.isEmpty(email) && !ValidatorEmail.PATTERN.matcher(email).find()) {
			return 4;
		}

		password = HelperEncrypt.aesDecryptBase64(password, aesSecretKeySpec);
		String timeStr = String.valueOf(time);
		if (password.startsWith(timeStr)) {
			password = password.substring(timeStr.length());
			if (register.getPassword().equals(PasswordCrudFactory.getPasswordEncrypt(password, register.getSalt()))) {
				if (!KernelString.isEmpty(newPassword)) {
					register.setPassword(PasswordCrudFactory.getPasswordEncrypt(newPassword, register.getSalt()));
				}

				if (!KernelString.isEmpty(email)) {
					register.setEmail(email);
				}

				BeanService.ME.merge(register);
				return 1;
			}
		}

		return 3;
	}

	/**
	 * 绑定平台账号
	 * 
	 * 6 账号不存在
	 * 
	 * @param platform
	 * @param platformUsername
	 * @param sessionId
	 * @param username
	 * @param password
	 * @param email
	 * @return
	 */
	public int bindPlatformUser(String platform, String platformUsername, String sessionId, String username,
			String password, String email) {
		JPlatformUser platformUser = PlatformService.ME.platformUserSession(platformUsername, username, sessionId);
		if (platformUser == null) {
			return 6;
		}

		return register(username, password, email, platformUser.getId());
	}
}
