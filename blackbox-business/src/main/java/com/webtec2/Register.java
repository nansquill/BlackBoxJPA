package com.webtec2;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.lang.IllegalStateException;
import java.lang.IllegalArgumentException;
import javax.persistence.TransactionRequiredException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.session.Session;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;

@Path("/register")
@Transactional
public class Register {
	
	/**
	 * API overview:
	 *  
	 *  / @POST -> retrieve session data for the user
	 *  /login @POST -> retrieve session data for the user
	 *  /logout @POST -> retrieve session data for the user
	 */	

	@PersistenceContext
	private EntityManager entityManager;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(final String username, final String password) {
		Subject currentUser = SecurityUtils.getSubject();		
		Session session = currentUser.getSession();
		session.setAttribute("key", "value");
		if(currentUser.isAuthenticated())
		{
			return Response.status(Status.BAD_REQUEST).build();
		}
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		token.setRememberMe(true);			
		try
		{
			currentUser.login(token);				
		}
		catch(UnknownAccountException uae) 
		{
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		return Response.ok(currentUser).build();	
	}
	
	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(final String username, final String password, final boolean rememberMe) {
		Subject currentUser = SecurityUtils.getSubject();		
		Session session = currentUser.getSession();
		session.setAttribute("key", "value");
		if(!currentUser.isAuthenticated())
		{
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			token.setRememberMe(rememberMe);			
			try
			{
				currentUser.login(token);				
			}
			catch(UnknownAccountException uae) 
			{
				return Response.status(Status.BAD_REQUEST).build();
			}
		}
		return Response.ok(currentUser).build();		
	}
	
	@Path("/logout")
	@GET
	@POST
	@Consumes("*/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout() {
		Subject currentUser = SecurityUtils.getSubject();
		
		Session session = currentUser.getSession();
		session.setAttribute("key", "value");
		if(currentUser.isAuthenticated())
		{
			currentUser.logout();
		}	
		return Response.ok(currentUser).build();		
	}

}
