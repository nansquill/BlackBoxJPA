package com.webtec2.auth;

import com.webtec2.DBMessage;
import com.webtec2.DBCategory;
import com.webtec2.auth.permission.*;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.*;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import java.util.*;

public class WT2Realm extends AuthorizingRealm implements Realm {

	public final static String REALM = "WT2";

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

		final BeanManager bm = CDI.current().getBeanManager();
		final Set<Bean<?>> beans = bm.getBeans(DatabaseAuthenticator.class);

		if (beans.isEmpty()) {
			throw new AuthenticationException();
		}

		final Bean<DatabaseAuthenticator> bean = (Bean<DatabaseAuthenticator>) bm.resolve(beans);
		final CreationalContext<DatabaseAuthenticator> cctx = bm.createCreationalContext(bean);
		final DatabaseAuthenticator authenticator =
				(DatabaseAuthenticator) bm.getReference(bean, DatabaseAuthenticator.class, cctx);

		return authenticator.fetchAuthenticationInfo(token);
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return new AuthorizationInfo() {

			@Override
			public Collection<String> getRoles() {
				if ("admin".equals(principals.getPrimaryPrincipal())) {
					return Collections.singleton("admin");
				}

				return Collections.emptyList();
			}
			
			@Override
			public Collection<String> getStringPermissions() {
				if ("admin".equals(principals.getPrimaryPrincipal())) {
			
					String[] words = {"ReadMessageItemPermission",
						"WriteMessageItemPermission",
						"DeleteMessageItemPermission",
						"ReadCategoryItemPermission",
						"WriteCategoryItemPermission",
						"DeleteCategoryItemPermission"};
					return Arrays.asList(words);
				}
			
				String[] words = {"ReadMessageItemPermission",
					"WriteMessageItemPermission",
					"DeleteMessageItemPermission",
					"ReadCategoryItemPermission"};
				return Arrays.asList(words);
				
			}

			@Override
			public Collection<Permission> getObjectPermissions() {
				if ("admin".equals(principals.getPrimaryPrincipal())) {
					return Arrays.asList(
						new ReadMessageItemPermission(principals.getPrimaryPrincipal().toString()),
						new WriteMessageItemPermission(new DBMessage(principals.getPrimaryPrincipal().toString(), new DBCategory("test"), "test", "test"), principals.getPrimaryPrincipal().toString()),
						new DeleteMessageItemPermission(new DBMessage(principals.getPrimaryPrincipal().toString(), new DBCategory("test"), "test", "test"), principals.getPrimaryPrincipal().toString()),
						new ReadCategoryItemPermission(principals.getPrimaryPrincipal().toString()),
						new WriteCategoryItemPermission(new DBCategory(), principals.getPrimaryPrincipal().toString()),
						new DeleteCategoryItemPermission(new DBCategory(), principals.getPrimaryPrincipal().toString())
					);
				}
				return Arrays.asList(
					new ReadMessageItemPermission(principals.getPrimaryPrincipal().toString()),
					new WriteMessageItemPermission(new DBMessage(principals.getPrimaryPrincipal().toString(), new DBCategory("test"), "test", "test"), principals.getPrimaryPrincipal().toString()),
					new DeleteMessageItemPermission(new DBMessage(principals.getPrimaryPrincipal().toString(), new DBCategory("test"), "test", "test"), principals.getPrimaryPrincipal().toString()),
					new ReadCategoryItemPermission(principals.getPrimaryPrincipal().toString())
				);
			}
		};
	}
}
