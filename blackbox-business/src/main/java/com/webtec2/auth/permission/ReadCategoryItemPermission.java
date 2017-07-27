package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBCategory;
import org.apache.shiro.subject.Subject;

public class ReadCategoryItemPermission implements Permission {

	private final DBCategory category;
	private final Subject user;

	public ReadCategoryItemPermission(final DBCategory category, final Subject user) {
		this.category = category;
		this.user = user;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		//Only registered users incl. admin should be permitted to read category
		if(user.isAuthenticated())
		{
			System.out.println(user.getPrincipal() + " is allowed to read category " + category.getName());
			return true;
		}
		System.out.println(user.getPrincipal() + " is not allowed to read category " + category.getName());
		return false;
	}	
}