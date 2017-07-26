package com.webtec2.auth;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.SimpleAccount;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import com.webtec2.DBUser;

@Transactional
public class DatabaseAuthenticator {

	@PersistenceContext
	private EntityManager entityManager;

	public AuthenticationInfo fetchAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		final String username = (String) token.getPrincipal();
		final UsernamePasswordToken uptoken = (UsernamePasswordToken) token;
		final char[] password = uptoken.getPassword();
		//check if user is admin
		if("admin".equals(username) && "admin".equals(password)) {
			System.out.println("User " + username + " has been authenticated");
			return new SimpleAccount(username, password, WT2Realm.REALM);
		}
		
		//check if user already registered
		DBUser user;
		try	{
			user = this.entityManager.find(DBUser.class, username);
			if(!user.getPassword().equals(password))	{throw new Exception();	}
		}
		catch(Exception e)	{
			System.out.println("User credentials for " + username + " are not valid");
			throw new AuthenticationException("User credentials for \" + username + \" are not valid");
		}
		System.out.println("User " + username + " has been authenticated");
		return new SimpleAccount(username, password, WT2Realm.REALM);
	}

}