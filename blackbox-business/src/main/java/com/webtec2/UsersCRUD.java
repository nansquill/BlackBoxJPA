package com.webtec2;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import com.webtec2.auth.permission.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.session.Session;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.*;

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
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;

@Path("/persons")
@Transactional
public class UsersCRUD {
	
	/**
	 * API overview:
	 *  / @GET -> read all user data
	 *  /id @GET -> read data from user{id}
	 *  / @POST -> create new user
	 *  /delete/{id} @DELETE -> remove user {id}
	 *  /update @PUT -> update user data
	 */	

	@PersistenceContext
	private EntityManager entityManager;
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readAll()	{
		final CriteriaBuilder builder;
		List<DBUser> result = new ArrayList<DBUser>();
		try
		{
			builder = this.entityManager.getCriteriaBuilder();
			CriteriaQuery<DBUser> query = builder.createQuery(DBUser.class);			
			Root<DBUser> from = query.from(DBUser.class);			
			query.select(from);
			result = this.entityManager.createQuery(query).getResultList();	
		}
		catch(IllegalStateException ex)
		{
			System.out.println("[Error] Entity manager not reachable");
			System.out.println(ex);
		}
		catch(IllegalArgumentException ex)
		{			
			System.out.println("[Error] Duplicated category found");
			System.out.println(ex);
		}
		//Check permission
		List<DBUser> res = new List<DBUser>();
		final Subject subject = SecurityUtils.getSubject();
		for(DBUser user : result) {
			if(subject.isPermitted(new ReadUserItemPermission(user, subject))) {
				res.add(user);
			}
		}		
		System.out.println("[Info] Found " + res.size() + " users");
		return Response.ok(res).build();
	}

	@Path("/{username}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("username") String username) {
		DBUser user = null;
		try
		{
			user =  this.entityManager.find(DBUser.class, username);
			//Check permission
			final Subject subject = SecurityUtils.getSubject();
			if(!subject.isPermitted(new ReadUserItemPermission(user, subject))) {
				System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to read user");
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
		}
		catch(IllegalArgumentException ex)
		{
			System.out.println("[Error] User " + username + " couldn't be found");
			System.out.println(ex);
		}		
		System.out.println("[Info] Found user " + user.getUsername());
		return Response.ok(user).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final DBUser param) {
		//Check permission
		final Subject subject = SecurityUtils.getSubject();
		if(!subject.isPermitted(new WriteUserItemPermission(param, subject))) {
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to write user");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		final DBUser user;
		try
		{
			user = new DBUser(param.getUsername(), param.getPassword());
		}
		catch(EntityExistsException ex)
		{
			System.out.println("[Error] Duplicated user " + param.getUsername());
			System.out.println(ex);
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			System.out.println("[Error] User is not valid");
			System.out.println(ex);
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}		
		this.entityManager.persist(user);
		System.out.println("[Info] Created user " + user.getUsername());
		return Response.ok(user).build();
	}
	

	@Path("/delete/{username}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteByUsername(@PathParam("username") final String username) {
		DBUser user;
		try
		{
			user = this.entityManager.find(DBUser.class, username);
			//Check permission
			final Subject subject = SecurityUtils.getSubject();
			if(!subject.isPermitted(new DeleteUserItemPermission(user, subject))) {
				System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to delete user");
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
			this.entityManager.remove(user);
		}
		catch(IllegalArgumentException ex)
		{
			System.out.println("[Error] User " + username + " is invalid");
			System.out.println(ex);
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		System.out.println("[Info] Deleted user " + user.getUsername());
		return Response.ok(user).build();	
	}
	
	@Path("/update/{username}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("username") final String username, final DBUser usr) {
		DBUser param;
		try
		{
			param = this.entityManager.find(DBUser.class, username);
			//Check permission
			final Subject subject = SecurityUtils.getSubject();
			if(!subject.isPermitted(new DeleteUserItemPermission(param, subject))) {
				System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to update user");
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
			this.entityManager.refresh(usr);
		}
		catch(EntityNotFoundException ex)
		{
			System.out.println("[Error] Duplicated user " + username);
			System.out.println(ex);
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			System.out.println("[Error] User is not valid");
			System.out.println(ex);
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		System.out.println("[Info] User " + param.getUsername() + " has been updated");
		return Response.ok(param).build();	
	}	
	
	@Path("/login")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(String username, String password) {
		Subject currentUser = SecurityUtils.getSubject();
		if(!currentUser.isAuthenticated())
		{
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			token.setRememberMe(true);			
			try
			{
				currentUser.login(token);
				System.out.println("[Info] User " + username + " has logged in");
				currentUser.getSession().setAttribute("username", username);
				return Response.ok(currentUser).build();	
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
		return Response.ok(currentUser).build();		
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
		return Response.ok(currentUser).build();		
	}

	@Path("/auth")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response isAuth() {
		Subject currentUser = SecurityUtils.getSubject();
		if(currentUser.isAuthenticated())
		{
			System.out.println("[Info] User " + currentUser.getPrincipal() + " is authenticated");
			return Response.ok(currentUser).build();
		}		
		System.out.println("[Error] User " + currentUser.getPrincipal() + " is not authenticated");
		return Response.status(Status.BAD_REQUEST).build();		
	}
}
