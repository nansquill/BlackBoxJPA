package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBMessage;

public class FirstMessageItemPermission implements Permission {

	private final DBMessage message;

	public FirstMessageItemPermission(final DBMessage message) {
		this.message = message;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		return this.message.getId() == 1;
	}
}