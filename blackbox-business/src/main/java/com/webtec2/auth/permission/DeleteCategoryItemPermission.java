package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;
import com.webtec2.DBCategory;

public class DeleteCategoryItemPermission implements Permission {

	private final DBCategory category;
	private final String username;

	public DeleteCategoryItemPermission(final DBCategory category, final String username) {
		this.category = category;
		this.username = username;
	}
	@Override
	public boolean implies(Permission p) {
		return false;
	}

	public boolean check() {
		return false;
	}
}