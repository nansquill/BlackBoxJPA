package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBCategory;
import org.apache.shiro.subject.Subject;

public class DeleteCategoryItemPermission implements Permission {

	private final DBCategory category;
	private final Subject user;

	public DeleteCategoryItemPermission(final DBCategory category, final Subject user) {
		this.category = category;
		this.user = user;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		//Only admin should be permitted to delete category
		if(user.getPrincipal() == "admin")
		{
			System.out.println(user.getPrincipal() + " is allowed to delete category " + category.getName());
			return true;
		}
		System.out.println(user.getPrincipal() + " is not allowed to delete category " + category.getName());
		return false;
	}
}