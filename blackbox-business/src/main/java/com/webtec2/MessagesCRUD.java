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
	 *  /newest @GET -> read newest message data
	 */	

	@PersistenceContext
	private EntityManager entityManager;
	
	@GET 
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
			System.out.println("Error the entity manager has been closed");
		}
		catch(IllegalArgumentException ex)
		{			
			//if the selection is a compound selection and more than one selection item has the same assigned alias
			System.out.println("Error the selection is a compound selection and more than one selection item has the same assigned alias");
		}
		
		//Read All should be permitted to everyone		
		final Subject subject = SecurityUtils.getSubject();
		final Permission readMessageItemPermission = new ReadMessageItemPermission(subject.getPrincipal().toString());
		try{subject.checkPermission("ReadMessageItemPermission");}
		catch(AuthorizationException ex){

			System.out.println("Error ReadMessageItemPermission not given");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		return Response.ok(result).build();	
	}	
	
	@Path("/{id}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("id") final long id) {
		final Subject subject = SecurityUtils.getSubject();
		final Permission readMessageItemPermission = new ReadMessageItemPermission(subject.getPrincipal().toString());
		try{subject.checkPermission("ReadMessageItemPermission");}
		catch(AuthorizationException ex){
			System.out.println("Error ReadMessageItemPermission not given");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		DBMessage message = null;		
		try
		{
			message =  this.entityManager.find(DBMessage.class, id);
		}
		catch(IllegalArgumentException ex)
		{
			//if the first argument does not denote an entity type or the second argument is is not a valid type for that entity primary key or is null
			System.out.println("Error the first argument does not denote an entity type or the second argument is is not a valid type for that entity primary key or is null");
		}
		//Read should be permitted to everyone
		return Response.ok(message).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresAuthentication
	public Response create(final DBMessage param) {		
		final DBMessage message;
		try
		{
			message = new DBMessage(param.getUser() , param.getCategory(), param.getHeadline(), param.getContent());
			
		}
		catch(EntityExistsException ex)
		{
			//if the entity already exists.
			System.out.println("Error the entity already exists.");
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			//if the instance is not an entity
			System.out.println("Error the instance is not an entity");
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			System.out.println("Error invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		//Permission denied if not registered or not allowed
		final Subject subject = SecurityUtils.getSubject();
		final Permission writeMessageItemPermission = new WriteMessageItemPermission(message, subject.getPrincipal().toString());
		try{subject.checkPermission("WriteMessageItemPermission");}
		catch(AuthorizationException ex){
			System.out.println("Error WriteMessageItemPermission not given");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}			
		this.entityManager.persist(message);
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
		}
		catch(IllegalArgumentException ex)
		{
			//if the first argument does not denote an entity type or the second argument is is not a valid type for that entitys primary
			//if instance is not an entity or is a removed entity
			System.out.println("Error instance is not an entity or is a removed entity");
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			System.out.println("Error invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}		
		
		//Permitted, if user owns this message or is admin
		final Subject subject = SecurityUtils.getSubject();
		final Permission deleteMessageItemPermission = new DeleteMessageItemPermission(message, subject.getPrincipal().toString());
		try{subject.checkPermission("DeleteMessageItemPermission");}
		catch(AuthorizationException ex){
			System.out.println("Error DeleteMessageItemPermission not given");
			return Response.status(Response.Status.UNAUTHORIZED).build();			
		}
		try{this.entityManager.remove(message);}
		catch(IllegalArgumentException ex) {
			System.out.println("Error the instance is not an entity or is a detached entity");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();			
		}
		catch(TransactionRequiredException ex) {
			System.out.println("Error invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();			
		}
		return Response.ok(message).build();
	}
	
	@Path("/delete")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(final DBMessage param) {
		//Permitted, if user owns this message or is admin
		final Subject subject = SecurityUtils.getSubject();
		final Permission deleteMessageItemPermission = new DeleteMessageItemPermission(param, subject.getPrincipal().toString());
		try{subject.checkPermission("DeleteMessageItemPermission");}
		catch(AuthorizationException ex){
			System.out.println("Error DeleteMessageItemPermission not given");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}					
		try
		{
			this.entityManager.remove(param);
		}
		catch(IllegalArgumentException ex)
		{
			//if instance is not an entity or is a removed entity
			System.out.println("Error instance is not an entity or is a removed entity");
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			System.out.println("Error invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}		
		return Response.ok(param).build();	
	}	
	
	@Path("/update")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(final DBMessage param) {	
		final Subject subject = SecurityUtils.getSubject();
		final Permission writeMessageItemPermission = new WriteMessageItemPermission(param, subject.getPrincipal().toString());
		try{subject.checkPermission("WriteMessageItemPermission");}
		catch(AuthorizationException ex){
			System.out.println("Error WriteMessageItemPermission not given");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}								
		try
		{
			this.entityManager.refresh(param);
		}
		catch(EntityNotFoundException ex)
		{
			//if the entity no longer exists in the database
			System.out.println("Error the entity no longer exists in the database");
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			//if the instance is not an entity or the entity is not managed
			System.out.println("Error the instance is not an entity or the entity is not managed");
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			System.out.println("Error invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(param).build();	
	}	
}