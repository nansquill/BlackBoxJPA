package com.webtec2.auth;

import com.webtec2.DBMessage;
import com.webtec2.DBCategory;
import com.webtec2.DBUser;
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
import org.apache.shiro.subject.Subject;

public class WT2Realm extends AuthorizingRealm implements Realm {

	public final static String REALM = "WT2";

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

		final BeanManager bm = CDI.current().getBeanManager();
		final Set<Bean<?>> beans = bm.getBeans(DatabaseAuthenticator.class);

		if (beans.isEmpty()) {
			System.out.println("[Error] Authentication Exception");
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
		
		Subject user = (Subject) principals.oneByType(Subject.class);
		if(user!=null)
		{
			System.out.println(user + " couldn't be detected");
			return new AuthorizationInfo() {
				@Override
				public Collection<String> getRoles() { return Collections.emptyList();}
				
				@Override
				public Collection<String> getStringPermissions() { return Collections.emptyList();}
				
				@Override
				public Collection<Permission> getObjectPermissions() { return Collections.emptyList();}
			};
		}
		System.out.println("User " + user.getPrincipal() + " has be detected");
		
		return new AuthorizationInfo() {
			@Override
			public Collection<String> getRoles() {
				if ("admin".equals(user.getPrincipal())) {
					System.out.println("[Info] User has the admin role");
					return Collections.singleton("admin");
				}
				System.out.println("[Info] User has no role");
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
						"DeleteCategoryItemPermission",
						"ReadUserItemPermission",
						"WriteUserItemPermission",
						"DeleteUserItemPermission",};
					return Arrays.asList(words);
				}
				else if(user.isAuthenticated()) {
					String[] words = {"ReadMessageItemPermission",
							"WriteMessageItemPermission",
							"DeleteMessageItemPermission",
							"ReadCategoryItemPermission",
							"WriteCategoryItemPermission",
							"ReadUserItemPermission",
							"WriteUserItemPermission",
							"DeleteUserItemPermission",};
					return Arrays.asList(words);					
				}
				String[] words = {"WriteUserItemPermission"};
				return Arrays.asList(words);				
			}

			@Override
			public Collection<Permission> getObjectPermissions() {
				if ("admin".equals(principals.getPrimaryPrincipal())) {			
					return Arrays.asList(
						new ReadMessageItemPermission(null, user),
						new WriteMessageItemPermission(null, user),
						new DeleteMessageItemPermission(null, user),
						new ReadCategoryItemPermission(null, user),
						new WriteCategoryItemPermission(null, user),
						new DeleteCategoryItemPermission(null, user),
						new ReadUserItemPermission(null, user),
						new WriteUserItemPermission(null, user),
						new DeleteUserItemPermission(null, user));
				}
				else if(user.isAuthenticated()) {
					return Arrays.asList(
						new ReadMessageItemPermission(null, user),
						new WriteMessageItemPermission(null, user),
						new DeleteMessageItemPermission(null, user),
						new ReadCategoryItemPermission(null, user),
						new WriteCategoryItemPermission(null, user),
						new ReadUserItemPermission(null, user),
						new WriteUserItemPermission(null, user),
						new DeleteUserItemPermission(null, user));					
				}
				return Arrays.asList(
					new WriteUserItemPermission(null, user));				
			}
		};
	}
}
