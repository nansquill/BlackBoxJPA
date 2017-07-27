package com.webtec2.auth;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.SimpleAccount;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.apache.shiro.SecurityUtils;
import com.webtec2.DBUser;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.authc.*;

@Transactional
public class DatabaseAuthenticator {

	@PersistenceContext
	private EntityManager entityManager;

	public AuthenticationInfo fetchAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

		final String user = (String) token.getPrincipal();
		try {
			DBUser userobj = entityManager.find(DBUser.class, user);
			System.out.println("[" + userobj.getUsername() + "/" + userobj.getPassword() + "] are accepted");
			return new SimpleAccount(userobj.getUsername(), userobj.getPassword(), WT2Realm.REALM);
		}
		catch(Exception ex)
		{
			System.out.println("[" + user + "/] exception occured");
			System.out.println(ex);
			//throw new AuthenticationException("again");
			return new SimpleAccount(user, user.toCharArray(), WT2Realm.REALM);
		}
		
		/**		
		try {
			final UsernamePasswordToken uptoken = (UsernamePasswordToken) token;
			final char[] password = uptoken.getPassword();
			DBUser userobj = this.entityManager.find(DBUser.class, user);
			if(userobj != null) {
				try {
					Subject currentUser = SecurityUtils.getSubject();
					currentUser.login(uptoken);
					System.out.println("[" + user + "/" + password + "] are accepted");
					return new SimpleAccount(user, password, WT2Realm.REALM);
				}
				catch(UnknownAccountException uae) 
				{
					System.out.println("[ErrorDB] User " + user + " couldn't be found");
					System.out.println(uae);
				}
				catch(IncorrectCredentialsException ice)
				{
					System.out.println("[ErrorDB] Password does not match");
					System.out.println(ice);
				}
				catch(LockedAccountException lae)
				{
					System.out.println("[ErrorDB] User " + user + " has been deactivated");
					System.out.println(lae);
				}				
				System.out.println("[" + user + "/" + password + "] credentials has been found, but db object has [" + userobj.getUsername() + "/" + userobj.getPassword() + "]");
				return new SimpleAccount(user, password, WT2Realm.REALM);
			}
			System.out.println("[" + user + "/" + password + "] credentials has been found, but no db object");
			**/
	}

}