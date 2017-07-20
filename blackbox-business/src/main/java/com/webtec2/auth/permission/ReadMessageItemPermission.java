package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBMessage;

public class ReadMessageItemPermission implements Permission {

	private final String username;

	public ReadMessageItemPermission(final String username) {
		this.username = username;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		return true;
	}
}