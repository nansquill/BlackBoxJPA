package com.webtec2;

import com.webtec2.auth.permission.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.*;
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
import javax.persistence.Query;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.ws.rs.*;
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
	 *  /delete @DELETE -> remove message
	 *  /delete/{id} @DELETE -> remove message {id}
	 *  /update @PUT -> update message data
	 */	

	@PersistenceContext
	private EntityManager entityManager;
	
	@Path("/own")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOwnMessages() {
		final Subject subject = SecurityUtils.getSubject();
		Query query = this.entityManager.createQuery("SELECT m FROM DBMessage m WHERE m.user = :username");
		query.setParameter("username", subject.getPrincipal().toString());
		final List<DBMessage> result = query.getResultList();
		//Check permission
		List<DBMessage> res = new ArrayList<DBMessage>();		
		for(DBMessage message: result) {
			if(subject.isPermitted(new ReadMessageItemPermission(message, subject))) {
				res.add(message);
			}
		}
		System.out.println("[Info] Found " + res.size() + " messages");
		return Response.ok(res).build();
	}
	
	@GET 
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readAll()	{	
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
			System.out.println("[Error] Entity manager not reachable");
			System.out.println(ex);
		}
		catch(IllegalArgumentException ex)
		{			
			//if the selection is a compound selection and more than one selection item has the same assigned alias
			System.out.println("[Error] Duplicated message found");
			System.out.println(ex);
		}
		//Check permission
		List<DBMessage> res = new ArrayList<DBMessage>();
		final Subject subject = SecurityUtils.getSubject();
		for(DBMessage message: result) {
			if(subject.isPermitted(new ReadMessageItemPermission(message, subject))) {
				res.add(message);
			}
		}
		System.out.println("[Info] Found " + res.size() + " messages");
		return Response.ok(res).build();	
	}	
	
	@Path("/{id}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("id") final long id) {
		DBMessage message = null;		
		try
		{
			message =  this.entityManager.find(DBMessage.class, id);
			//Check permission
			final Subject subject = SecurityUtils.getSubject();
			if(!subject.isPermitted(new ReadMessageItemPermission(message, subject))) {
				System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to read message");
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
		}
		catch(IllegalArgumentException ex)
		{
			//if the first argument does not denote an entity type or the second argument is is not a valid type for that entity primary key or is null
			System.out.println("[Error] Message " + id + " is not valid");
			System.out.println(ex);
		}
		//Read should be permitted to everyone
		System.out.println("[Info] Found message " + message.getId());
		return Response.ok(message).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresAuthentication
	public Response create(final DBMessage param) {		
		//Check permission
		final Subject subject = SecurityUtils.getSubject();
		if(!subject.isPermitted(new WriteMessageItemPermission(param, subject))) {
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to write message");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		DBMessage message = null;				
		try
		{		
			DBCategory cat = this.entityManager.find(DBCategory.class, param.getCategory().getName());
			if(cat == null)
			{
				cat = new DBCategory(param.getCategory().getName());
				this.entityManager.persist(cat);
			}
			message = new DBMessage(subject.getPrincipal().toString(), cat, param.getHeadline(), param.getContent());
			this.entityManager.persist(message);
		}
		catch(EntityExistsException ex)
		{
			//if the entity already exists.
			System.out.println("[Error] Duplicated message " + param.getId());
			System.out.println(ex);
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			//if the instance is not an entity
			System.out.println("[Error] Invalid message");
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
		catch(Exception ex)
		{
			System.out.println("[Erro] Message create");
			System.out.println(ex);			
		}
		System.out.println("[Info] Message " + message.getId() + " has been created");
		return Response.ok(message).build();		
	}
	
	@Path("/delete/{id}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteById(@PathParam("id") final long id) {
		DBMessage message = null;
		try
		{
			message = this.entityManager.find(DBMessage.class, id);
			//Check permission
			final Subject subject = SecurityUtils.getSubject();
			if(!subject.isPermitted(new DeleteMessageItemPermission(message, subject))) {
				System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to delete message");
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
			this.entityManager.remove(message);
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}		
		System.out.println("[Info] Message " + message.getId() + " has been deleted");
		return Response.ok(message).build();
	}
	
	@Path("/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(final DBMessage param) {
		//Check permission
		final Subject subject = SecurityUtils.getSubject();
		if(!subject.isPermitted(new DeleteMessageItemPermission(param, subject))) {
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to delete message");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}				
		try
		{
			param.setCategory(null);
			this.entityManager.remove(param);
		}
		catch(IllegalArgumentException ex)
		{
			//if instance is not an entity or is a removed entity
			System.out.println("[Error] Invalid message");
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
		System.out.println("[Info] Message " + param.getId() + " has been deleted");
		return Response.ok(param).build();	
	}	
	
	@Path("/update")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(final DBMessage param) {	
		//Check permission
		final Subject subject = SecurityUtils.getSubject();
		if(!subject.isPermitted(new DeleteMessageItemPermission(param, subject))) {
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to update message");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}								
		try
		{
			DBMessage temp = this.entityManager.find(DBMessage.class, param.getId());
			DBCategory cat = this.entityManager.find(DBCategory.class, param.getCategory());
			if(cat == null)
				cat = new DBCategory(param.getCategory().getName());
			temp.setCategory(cat);
			temp.setHeadline(param.getHeadline());
			temp.setContent(param.getContent());
			this.entityManager.refresh(temp);
		}
		catch(EntityNotFoundException ex)
		{
			//if the entity no longer exists in the database
			System.out.println("[Error] Invalid message");
			System.out.println(ex);
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			//if the instance is not an entity or the entity is not managed
			System.out.println("[Error] Invalid message");
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
		System.out.println("[Info] Message " + param.getId() + " has been edited");
		return Response.ok(param).build();	
	}	
}