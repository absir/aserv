/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-2 上午11:30:49
 */
package com.absir.aserv.system.bean.value;

import com.absir.aserv.system.bean.JPermission;
import com.absir.orm.value.JePermission;

/**
 * @author absir
 * 
 */
public enum JeVotePermission {

	SELECTABLE {

		@Override
		protected JeVote voteFor(JPermission permission) {
			return permission.getSelectable();
		}

	},

	UPDATABLE {

		@Override
		protected JeVote voteFor(JPermission permission) {
			return permission.getUpdatable();
		}

	},

	INSERTABLE {

		@Override
		protected JeVote voteFor(JPermission permission) {
			return permission.getInsertable();
		}

	},

	DELETEABLE {

		@Override
		protected JeVote voteFor(JPermission permission) {
			return permission.getDeletable();
		}
	}

	;

	/**
	 * @param permission
	 * @return
	 */
	protected abstract JeVote voteFor(JPermission permission);

	/**
	 * @param permission
	 * @return
	 */
	public JeVote getJeVote(JPermission permission) {
		if (permission == null) {
			return JeVote.NONE;
		}

		return voteFor(permission);
	}

	/**
	 * @param permission
	 * @return
	 */
	public static JeVotePermission forPermission(JePermission permission) {
		if (permission == JePermission.UPDATE) {
			return JeVotePermission.UPDATABLE;
		}

		if (permission == JePermission.INSERT) {
			return JeVotePermission.INSERTABLE;
		}

		if (permission == JePermission.DELETE) {
			return JeVotePermission.DELETEABLE;
		}

		return JeVotePermission.SELECTABLE;
	}
}
