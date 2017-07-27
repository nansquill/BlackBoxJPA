package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBMessage;
import org.apache.shiro.subject.Subject;

public class DeleteMessageItemPermission implements Permission {

	private final DBMessage message;
	private final Subject user;

	public DeleteMessageItemPermission(final DBMessage message, final Subject user) {
		this.message = message;
		this.user = user;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		//Only author and admin should be permitted to delete message
		if(message.getUser() == user.getPrincipal() || message.getUser() == "admin")
		{
			System.out.println(user.getPrincipal() + " is allowed to delete message " + message.getId());
			return true;
		}
		System.out.println(user.getPrincipal() + " is not allowed to delete message " + message.getId());
		return false;
	}
}