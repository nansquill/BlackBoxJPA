package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBCategory;
import org.apache.shiro.subject.Subject;

public class WriteCategoryItemPermission implements Permission {

	private final DBCategory category;
	private final Subject user;

	public WriteCategoryItemPermission(final DBCategory category, final Subject user) {
		this.category = category;
		this.user = user;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		//Only registered users incl. admin should be permitted to write category
		if(user.isAuthenticated())
		{
			System.out.println(user.getPrincipal() + " is allowed to write category " + category.getName());
			return true;
		}
		System.out.println(user.getPrincipal() + " is not allowed to write category " + category.getName());
		return false;
	}
}