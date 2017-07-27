package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBMessage;
import org.apache.shiro.subject.Subject;

public class ReadMessageItemPermission implements Permission {

	private final DBMessage message;
	private final Subject user;

	public ReadMessageItemPermission(final DBMessage message, final Subject user) {
		this.message = message;
		this.user = user;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		//Only registered users incl. admin should be permitted to read message
		if(user.isAuthenticated())
		{
			System.out.println(user.getPrincipal() + " is allowed to read message " + message.getId());
			return true;
		}
		System.out.println(user.getPrincipal() + " is not allowed to read message " + message.getId());
		return false;
	}	
}