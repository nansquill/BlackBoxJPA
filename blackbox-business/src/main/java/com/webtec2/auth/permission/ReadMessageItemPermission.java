package com.webtec2.auth.permission;

import org.apache.shiro.authz.Permission;

public class ReadMessageItemPermission implements Permission {

	@Override
	public boolean implies(Permission p) {

		if (p instanceof FirstMessageItemPermission) {
			final FirstMessageItemPermission fnip = (FirstMessageItemPermission) p;
			if (fnip.check()) {
				return true;
			}
		}

		return false;
	}
}