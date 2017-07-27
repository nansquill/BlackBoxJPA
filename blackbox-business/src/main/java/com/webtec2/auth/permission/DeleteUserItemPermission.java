package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBUser;
import org.apache.shiro.subject.Subject;

public class DeleteUserItemPermission implements Permission {

	private final DBUser userdb;
	private final Subject user;

	public DeleteUserItemPermission(final DBUser userdb, final Subject user) {
		this.userdb = userdb;
		this.user = user;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		//Only author and admin should be permitted to delete user
		if(userdb.getUsername() == user.getPrincipal() || userdb.getUsername() == "admin")
		{
			System.out.println(user.getPrincipal() + " is allowed to delete user " + userdb.getUsername());
			return true;
		}
		System.out.println(user.getPrincipal() + " is not allowed to delete user " + userdb.getUsername());
		return false;
	}
}