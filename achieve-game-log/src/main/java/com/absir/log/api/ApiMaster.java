/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年4月13日 下午4:10:30
 */
package com.absir.log.api;

import com.absir.master.InputMaster;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.Input;
import com.absir.server.value.Before;
import com.absir.server.value.Body;
import com.absir.server.value.Mapping;
import com.absir.server.value.NoBody;

/**
 * @author absir
 *
 */
@Mapping("/api")
public class ApiMaster {

	@Body
	@NoBody
	@Before
	protected void onAuthentication(Input input) throws Throwable {
		if (!InputMaster.onAuthentication(input)) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}
	}

}
