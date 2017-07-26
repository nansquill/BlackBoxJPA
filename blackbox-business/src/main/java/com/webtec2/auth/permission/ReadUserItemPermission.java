package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBUser;
import org.apache.shiro.subject.Subject;

public class ReadUserItemPermission implements Permission {

	private final DBUser userdb;
	private final Subject user;

	public ReadUserItemPermission(final DBUser userdb, final Subject user) {
		this.userdb = userdb;
		this.user = user;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		//Only registered users incl. admin should be permitted to read user
		if(user.isAuthenticated())
		{
			System.out.println(user.getPrincipal() + " is allowed to read user " + userdb.getUsername());
			return true;
		}
		System.out.println(user.getPrincipal() + " is not allowed to read user " + userdb.getUsername());
		return false;
	}
}