package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBMessage;

public class DeleteMessageItemPermission implements Permission {

	private final DBMessage message;
	private final String username;

	public DeleteMessageItemPermission(final DBMessage message, final String username) {
		this.message = message;
		this.username = username;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		return (this.message.getUser() == this.username) || (this.username == "admin");
	}
}