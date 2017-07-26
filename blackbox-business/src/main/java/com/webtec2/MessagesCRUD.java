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
		try{
			subject.checkPermission("ReadMessageItemPermission");
		}
		catch(AuthorizationException ex){
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to read message item");
			System.out.println(ex);
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		Query query = this.entityManager.createQuery("SELECT m FROM DBMessage m WHERE m.user = :username");
		query.setParameter("username", subject.getPrincipal().toString());
		final List<DBMessage> result = query.getResultList();
		
		System.out.println("[Info] Found " + result.size() + " messages for user " + subject.getPrincipal().toString());
		return Response.ok(result).build();
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
		final Subject subject = SecurityUtils.getSubject();
		for(DBMessage message: result) {
			if(!subject.isPermitted(new ReadMessageItemPermission(message, subject))) {
				result.remove(message);
				System.out.println("[Info] Result entry removed");
			}
		}
		System.out.println("[Info] Found " + result.size() + " messages");
		return Response.ok(result).build();	
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
			System.out.println("[Error] Message " + id + " is not valid");
			System.out.println(ex);
		}
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
		final DBMessage message;				
		try
		{
			message = new DBMessage(param.getUser(), param.getCategory(), param.getHeadline(), param.getContent());
			this.entityManager.persist(message);
		}
		catch(EntityExistsException ex)
		{
			System.out.println("[Error] Duplicated message " + param.getId());
			System.out.println(ex);
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			System.out.println("[Error] Invalid message");
			System.out.println(ex);
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
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
		catch(IllegalArgumentException ex)
		{
			System.out.println("[Error] Message " + id + " couldn't be found");
			System.out.println(ex);
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		System.out.println("[Info] Message " + message.getId() + " has been deleted");
		return Response.ok(message).build();
	}
	
	@Path("/update/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") final long id, final DBMessage msg) {
		DBMessage param;
		try
		{
			param = this.entityManager.find(DBMessage.class, id);
			//Check permission
			final Subject subject = SecurityUtils.getSubject();
			if(!subject.isPermitted(new DeleteMessageItemPermission(param, subject))) {
				System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to update message");
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
			this.entityManager.refresh(msg);
		}
		catch(EntityNotFoundException ex)
		{
			System.out.println("[Error] Invalid message");
			System.out.println(ex);
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			System.out.println("[Error] Invalid message");
			System.out.println(ex);
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		System.out.println("[Info] Message " + param.getId() + " has been edited");
		return Response.ok(param).build();	
	}	
}