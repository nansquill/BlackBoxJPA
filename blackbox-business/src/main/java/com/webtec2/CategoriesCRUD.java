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

@Path("/types")
@Transactional
public class CategoriesCRUD {
	
	/**
	 * API overview:
	 *  / @GET -> read all category data
	 *  /id @GET -> read data from category {id}
	 *  / @POST -> create new category
	 *  /{name}/messages -> get all messages of cat
	 *  /delete @DELETE -> remove category
	 *  /delete/{id} @DELETE -> remove category {id}
	 *  /update @PUT -> update category data
	 */	

	@PersistenceContext
	private EntityManager entityManager;
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readAll()	{
		//check subject first
		final Subject subject = SecurityUtils.getSubject();
		final Permission readCategoryItemPermission = new ReadCategoryItemPermission(subject.getPrincipal().toString());
		try{	
			subject.checkPermission("ReadCategoryItemPermission");
		}
		catch(AuthorizationException ex){
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to read category item");
			System.out.println(ex);
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		final CriteriaBuilder builder;
		List<DBCategory> result = new ArrayList<DBCategory>();
		try
		{
			builder = this.entityManager.getCriteriaBuilder();
			CriteriaQuery<DBCategory> query = builder.createQuery(DBCategory.class);			
			Root<DBCategory> from = query.from(DBCategory.class);			
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
			System.out.println("[Error] Duplicated category found");
			System.out.println(ex);
		}
		System.out.println("[Info] Found " + result.size() + " categories");
		return Response.ok(result).build();
	}

	@Path("/{name}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("name") String name) {
		final Subject subject = SecurityUtils.getSubject();
		final Permission readCategoryItemPermission = new ReadCategoryItemPermission(subject.getPrincipal().toString());
		try{
			subject.checkPermission("ReadCategoryItemPermission");
		}
		catch(AuthorizationException ex){
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to read category item");
			System.out.println(ex);
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		DBCategory category = null;
		try
		{
			category =  this.entityManager.find(DBCategory.class, name);
			if (category == null) {
				category = new DBCategory(name);
				this.entityManager.persist(category);
				System.out.println("[Info] Category " + category.getName() + " has been created");
			}
		}
		catch(IllegalArgumentException ex)
		{
			//if the first argument does not denote an entity type or the second argument is is not a valid type for that entity primary key or is null
			System.out.println("[Error] Category " + name + " couldn't be found");
			System.out.println(ex);
		}
		System.out.println("[Info] Found category " + category.getName());
		return Response.ok(category).build();
	}

	@Path("/{name}/messages")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMessages(@PathParam("name") String name) {
		final Subject subject = SecurityUtils.getSubject();
		final Permission readCategoryItemPermission = new ReadCategoryItemPermission(subject.getPrincipal().toString());
		try{
			subject.checkPermission("ReadCategoryItemPermission");
		}
		catch(AuthorizationException ex){
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to read category item");
			System.out.println(ex);
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		DBCategory category = null;
		try
		{
			category =  this.entityManager.find(DBCategory.class, name);
		}
		catch(IllegalArgumentException ex)
		{
			//if the first argument does not denote an entity type or the second argument is is not a valid type for that entity primary key or is null
			System.out.println("[Error] Category " + name + " couldn't be found");
			System.out.println(ex);
		}
		if (category == null) {
			System.out.println("[Error] Category " + name + " couldn't be found");
			return Response.status(Status.BAD_REQUEST).build();
		}
		Query query = this.entityManager.createQuery("SELECT m FROM DBMessage m JOIN m.category c WHERE c = :category");
		query.setParameter("category", category);
		final List<DBMessage> result = query.getResultList();
		
		System.out.println("[Info] Found " + result.size() + " messages with category " + category.getName());
		return Response.ok(result).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final DBCategory param) {
		final DBCategory category;
		try
		{
			category = new DBCategory(param.getName());
		}
		catch(EntityExistsException ex)
		{
			//if the entity already exists.
			System.out.println("[Error] Duplicated category " + param.getName());
			System.out.println(ex);
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			//if the instance is not an entity
			System.out.println("[Error] Category is not valid");
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
		final Subject subject = SecurityUtils.getSubject();
		final Permission writeCategoryItemPermission = new WriteCategoryItemPermission(category, subject.getPrincipal().toString());
		try{subject.checkPermission("WriteCategoryItemPermission");}
		catch(AuthorizationException ex){
			System.out.println("Error WriteCategoryItemPermission not given");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		this.entityManager.persist(category);
		System.out.println("[Info] Created category " + category.getName());
		return Response.ok(category).build();
	}
	

	@Path("/delete/{name}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteByName(@PathParam("name") final String name) {
		DBCategory category = null;
		try
		{
			category = this.entityManager.find(DBCategory.class, name);
		}
		catch(IllegalArgumentException ex)
		{
			//if the first argument does not denote an entity type or the second argument is is not a valid type for that entitys primary
			//if instance is not an entity or is a removed entity
			System.out.println("[Error] Category " + name + " is invalid");
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
		final Subject subject = SecurityUtils.getSubject();
		final Permission deleteCategoryItemPermission = new DeleteCategoryItemPermission(category, subject.getPrincipal().toString());
		try{
			subject.checkPermission("DeleteCategoryItemPermission");
		}
		catch(AuthorizationException ex){
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to delete category");
			System.out.println(ex);
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		try{this.entityManager.remove(category);}
		catch(IllegalArgumentException ex) {
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		catch(TransactionRequiredException ex) {
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		System.out.println("[Info] Deleted category " + category.getName());
		return Response.ok(category).build();	
	}
	
	@Path("/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(final DBCategory param) {
		final Subject subject = SecurityUtils.getSubject();
		final Permission deleteCategoryItemPermission = new DeleteCategoryItemPermission(param, subject.getPrincipal().toString());
		try{
			subject.checkPermission("DeleteCategoryItemPermission");
		}
		catch(AuthorizationException ex){
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to delete category");
			System.out.println(ex);
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		try
		{
			this.entityManager.remove(param);
		}
		catch(IllegalArgumentException ex)
		{
			//if instance is not an entity or is a removed entity
			System.out.println("[Error] Category is invalid");
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
		System.out.println("[Info] Category " + param.getName() + " has been deleted");
		return Response.ok(param).build();	
	}	
	
	@Path("/update")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(final DBCategory param) {
		final Subject subject = SecurityUtils.getSubject();
		final Permission writeCategoryItemPermission = new WriteCategoryItemPermission(param, subject.getPrincipal().toString());
		try{
			subject.checkPermission("WriteCategoryItemPermission");
		}
		catch(AuthorizationException ex){
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to edit category");
			System.out.println(ex);
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		try
		{
			this.entityManager.refresh(param);
		}
		catch(EntityNotFoundException ex)
		{
			//if the entity no longer exists in the database
			System.out.println("[Error] Duplicated category " + param.getName());
			System.out.println(ex);
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			//if the instance is not an entity or the entity is not managed
			System.out.println("[Error] Category is not valid");
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
		System.out.println("[Info] Category " + param.getName() + " has been edited");
		return Response.ok(param).build();	
	}	
}
