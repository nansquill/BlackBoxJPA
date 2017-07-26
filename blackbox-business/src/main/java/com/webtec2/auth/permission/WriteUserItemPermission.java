package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBUser;
import org.apache.shiro.subject.Subject;

public class WriteUserItemPermission implements Permission {

	private final DBUser userdb;
	private final Subject user;

	public WriteUserItemPermission(final DBUser userdb, final Subject user) {
		this.userdb = userdb;
		this.user = user;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		//Everyone should be permitted to write user
		System.out.println(user.getPrincipal() + " is allowed to write user " + userdb.getUsername());
		return true;
	}
}