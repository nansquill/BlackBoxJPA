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
import com.webtec2.auth.permission.FirstMessageItemPermission;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.session.Session;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;

@Path("/persons")
@Transactional
public class UsersCRUD implements CRUDInterface<DBUser> {
	
	/**
	 * API overview:
	 *  / @GET -> read all user data
	 *  /id @GET -> read data from user {id}
	 *  / @POST -> create new user
	 *  /create/{username}/{password} @GET -> create new user by username/password
	 *  /delete @POST -> remove user
	 *  /delete/{id} @GET -> remove user {id}
	 *  /update @POST -> update user data
	 *  /login @POST -> retrieve session data for the user
	 */	

	@PersistenceContext
	private EntityManager entityManager;
	
	@GET 
	@Produces(MediaType.APPLICATION_JSON)
	public List<DBUser> readAll()	{
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
			//if the entity manager has been closed
		}
		catch(IllegalArgumentException ex)
		{			
			//if the selection is a compound selection and more than one selection item has the same assigned alias
		}
		return result;	
	}
	
	@Path("/{id}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public DBUser read(@PathParam("id") final long id) {
		DBUser user = null;
		try
		{
			user =  this.entityManager.find(DBUser.class, id);
		}
		catch(IllegalArgumentException ex)
		{
			//if the first argument does not denote an entity type or the second argument is is not a valid type for that entity primary key or is null
		}
		return user;
	}
	
	@Path("/create/{username}/{password}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createByUsernameAndPassword(@PathParam("username") final String username, @PathParam("password") final String password) {

		final DBUser user = new DBUser(username, password);
		user.setIsAdmin(true);

		try
		{
			this.entityManager.persist(user);
		}
		catch(EntityExistsException ex)
		{
			//if the entity already exists.
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			//if the instance is not an entity
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(user).build();
	}
	
	
	@Path("/create")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final DBUser param) {

		final DBUser user = new DBUser(param.getUsername(), param.getPassword());
		user.setIsAdmin(param.getIsAdmin());

		try
		{
			this.entityManager.persist(user);
		}
		catch(EntityExistsException ex)
		{
			//if the entity already exists.
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			//if the instance is not an entity
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(user).build();		
	}
	
	@Path("/delete/{id}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteById(@PathParam("id") final long id) {
		DBUser user = null;
		try
		{
			user = this.entityManager.find(DBUser.class, id);
			this.entityManager.remove(user);
		}
		catch(IllegalArgumentException ex)
		{
			//if the first argument does not denote an entity type or the second argument is is not a valid type for that entitys primary
			//if instance is not an entity or is a removed entity
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}		
		return Response.ok(user).build();	
	}
	
	@Path("/delete")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(final DBUser param) {
		try
		{
			this.entityManager.remove(param);
		}
		catch(IllegalArgumentException ex)
		{
			//if instance is not an entity or is a removed entity
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}		
		return Response.ok(param).build();	
	}	
	
	@Path("/update")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(final DBUser param) {		
		try
		{
			this.entityManager.refresh(param);
		}
		catch(EntityNotFoundException ex)
		{
			//if the entity no longer exists in the database
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			//if the instance is not an entity or the entity is not managed
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(param).build();	
	}
	
	@Path("/login")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
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
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login() {
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
