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
import javax.persistence.*;
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
import org.apache.shiro.authc.*;
import com.webtec2.DBUser;

@Path("/register")
@Transactional
public class RegisterCRUD {
	
	/**
	 * API overview:
	 *  
	 *  / @POST -> retrieve session data for the user
	 *  /login @POST -> retrieve session data for the user
	 *  /logout @POST -> retrieve session data for the user
	 *  /auth @GET -> retrieve if user authenticated
	 *  
	 */	

	@PersistenceContext
	private EntityManager entityManager;
	
	@GET
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(final String username, final String password) {
		DBUser user = new DBUser(username, password);
		try {
			this.entityManager.persist(user);
		}
		catch(EntityExistsException ex)
		{
			//if the entity already exists.
			System.out.println("[Error] Duplicated user " + username);
			System.out.println(ex);
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			//if the instance is not an entity
			System.out.println("[Error] Invalid user");
			System.out.println(ex);
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}		
		
		System.out.println("[Info] User " + user.getUsername() + " has been created");
		return Response.ok(user).build();
	}
	
	@Path("/login")
	@GET
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(String username, String password) {
		Subject currentUser = SecurityUtils.getSubject();
		DBUser user = new DBUser(username, password);
		if(!currentUser.isAuthenticated())
		{
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			token.setRememberMe(true);			
			try
			{
				currentUser.login(token);
				System.out.println("[Info] User " + username + " has logged in");
				currentUser.getSession().setAttribute("username", username);
				return Response.ok(user).build();	
			}
			catch(UnknownAccountException uae) 
			{
				System.out.println("[Error] User " + username + " couldn't be found");
				System.out.println(uae);
				return Response.status(Status.BAD_REQUEST).build();
			}
			catch(IncorrectCredentialsException ice)
			{
				System.out.println("[Error] Password does not match");
				System.out.println(ice);
				return Response.status(Status.BAD_REQUEST).build();
			}
			catch(LockedAccountException lae)
			{
				System.out.println("[Error] User " + username + " has been deactivated");
				System.out.println(lae);
				return Response.status(Status.BAD_REQUEST).build();
			}	
		}
		System.out.println("[Info] User " + username + " is already logged in");
		return Response.ok(user).build();		
	}
	
	@Path("/logout")
	@GET
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout() {
		Subject currentUser = SecurityUtils.getSubject();
		if(currentUser != null)
		{
			currentUser.logout();
			System.out.println("[Info] User " + currentUser.getPrincipal() + " has logged out");
		}		
		DBUser user = new DBUser((String) currentUser.getPrincipal(), "");
		return Response.ok(user).build();		
	}

	@Path("/auth")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response isAuth() {
		Subject currentUser = SecurityUtils.getSubject();
		if(currentUser.isAuthenticated())
		{
			System.out.println("[Info] User " + currentUser.getPrincipal() + " is authenticated");
			DBUser user = new DBUser((String) currentUser.getPrincipal(), "");
			return Response.ok(user).build();
		}		
		System.out.println("[Error] User " + currentUser.getPrincipal() + " is not authenticated");
		return Response.status(Status.BAD_REQUEST).build();		
	}
	
}
