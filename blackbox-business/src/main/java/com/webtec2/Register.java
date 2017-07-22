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
	
	@Path("/{username}/{password}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("username") final String username, @PathParam("password") final String password) {
		DBUser user = new DBUser(username, password);
		this.entityManager.persist(user);
		
		return login(username, password);
	}
	
	@Path("/login/{username}/{password}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@PathParam("username") String username, @PathParam("password") String password) {
		Subject currentUser = SecurityUtils.getSubject();
		if(!currentUser.isAuthenticated())
		{
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			token.setRememberMe(true);			
			try
			{
				currentUser.login(token);
				System.out.println("User [" + username + "] has logged in");
				currentUser.getSession().setAttribute("username", username);
				return Response.ok(currentUser).build();	
			}
			catch(UnknownAccountException uae) 
			{
				System.out.println("Error: There is no user with username of " + username);
				return Response.status(Status.BAD_REQUEST).build();
			}
			catch(IncorrectCredentialsException ice)
			{
				System.out.println("Password for account " + username + " was incorrect!");
				return Response.status(Status.BAD_REQUEST).build();
			}
			catch(LockedAccountException lae)
			{
				System.out.println("The account for username " + username + " is locked. Please contact admin.");
				return Response.status(Status.BAD_REQUEST).build();
			}	
		}
		System.out.println("User " + username + " is already logged in");
		return Response.status(Status.BAD_REQUEST).build();		
	}
	
	@Path("/logout")
	@GET
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout() {
		Subject currentUser = SecurityUtils.getSubject();
		if(currentUser != null)
			currentUser.logout();	
		return Response.ok(currentUser).build();		
	}

}
