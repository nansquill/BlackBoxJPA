package com.webtec2;

import com.webtec2.auth.permission.FirstMessageItemPermission;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.session.Session;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
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

import javax.persistence.TransactionRequiredException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.lang.IllegalStateException;
import java.lang.IllegalArgumentException;



@Path("/messages")
@Transactional
public class MessagesCRUD implements CRUDInterface<DBMessage> {

	/**
	 * API overview:
	 *  / @GET -> read all message data
	 *  /id @GET -> read data from message {id}
	 *  / @POST -> create new message
	 *  /create/{message_id}/{category_id}/{headline}/{content} @GET -> create new message by data
	 *  /delete @POST -> remove message
	 *  /delete/{id} @GET -> remove message {id}
	 *  /update @POST -> update message data
	 */	

	@PersistenceContext
	private EntityManager entityManager;
	
	@GET 
	@Produces(MediaType.APPLICATION_JSON)
	public List<DBMessage> readAll()	{
		final CriteriaBuilder builder;
		List<DBMessage> result = new ArrayList<DBMessage>();		
		try
		{
			builder = this.entityManager.getCriteriaBuilder();
			CriteriaQuery<DBMessage> query = builder.createQuery(DBMessage.class);			
			Root<DBMessage> from = query.from(DBMessage.class);			
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
	
	@GET
	@Path("/newest")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readNewestMessage() {
		final CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		final CriteriaQuery<DBMessage> query = builder.createQuery(DBMessage.class);

		final Root<DBMessage> from = query.from(DBMessage.class);

		query.select(from);

		final DBMessage result = this.entityManager.createQuery(query).setMaxResults(1).getSingleResult();

		// Attribute based permission check using permissions
		final Subject subject = SecurityUtils.getSubject();
		final Permission firstMessageItemPermission = new FirstMessageItemPermission(result);

		if (!subject.isPermitted(firstMessageItemPermission)) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		return Response.ok(result).build();
	}
	
	
	@Path("/{id}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public DBMessage read(@PathParam("id") final long id) {
		DBMessage message = null;		
		try
		{
			message =  this.entityManager.find(DBMessage.class, id);
		}
		catch(IllegalArgumentException ex)
		{
			//if the first argument does not denote an entity type or the second argument is is not a valid type for that entity primary key or is null
		}
		return message;
	}

	@Path("/create/{user_id}/{category_id}/{headline}/{content}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createByUserAndCategoryAndHeadlineAndContent(@PathParam("user_id") final int user_id, @PathParam("category_id") final int category_id, 
			@PathParam("headline") final String headline, @PathParam("content") final String content) {
		final DBMessage message;
		try
		{
			final DBUser user = this.entityManager.find(DBUser.class, user_id);
			final DBCategory category = this.entityManager.find(DBCategory.class, category_id);		
			message = new DBMessage(user, category, headline, content);
			this.entityManager.persist(message);
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
		return Response.ok(message).build();
	}
	
	
	@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresAuthentication
	public Response create(final DBMessage param) {
		final DBMessage message;
		try
		{
			message = new DBMessage(new DBUser(),new DBCategory(), param.getHeadline(), param.getContent());
			this.entityManager.persist(message);
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
		return Response.ok(message).build();		
	}
	
	@Path("/delete/{id}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteById(@PathParam("id") final long id) {
		DBMessage message = null;
		try
		{
			message = this.entityManager.find(DBMessage.class, id);
			this.entityManager.remove(message);
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
		return Response.ok(message).build();	
	}
	
	@Path("/delete")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(final DBMessage param) {
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
	public Response update(final DBMessage param) {		
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
}