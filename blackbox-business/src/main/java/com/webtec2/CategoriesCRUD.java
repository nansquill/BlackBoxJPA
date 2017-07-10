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

@Path("/types")
@Transactional
public class CategoriesCRUD {
	
	/**
	 * API overview:
	 *  / @GET -> read all category data
	 *  /id @GET -> read data from category {id}
	 *  / @POST -> create new category
	 *  /create/{name}/{description} @GET -> create new category by data
	 *  /delete @POST -> remove category
	 *  /delete/{id} @GET -> remove category {id}
	 *  /update @POST -> update category data
	 */	

	@PersistenceContext
	private EntityManager entityManager;
	
	@GET 
	@Produces(MediaType.APPLICATION_JSON)
	public List<DBCategory> readAll()	{
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
		}
		catch(IllegalArgumentException ex)
		{			
			//if the selection is a compound selection and more than one selection item has the same assigned alias
		}
		return result;	
	}

	/*
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DBCategory read(@PathParam("id") final long id) {
		DBCategory category = null;
		try
		{
			category =  this.entityManager.find(DBCategory.class, id);
		}
		catch(IllegalArgumentException ex)
		{
			//if the first argument does not denote an entity type or the second argument is is not a valid type for that entity primary key or is null
		}
		return category;
	}
	 */

	@Path("/{name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DBCategory read(@PathParam("name") String name) {
		DBCategory category = null;
		try
		{
			category =  this.entityManager.find(DBCategory.class, name);
			if (category == null) {
				category = new DBCategory(name);
				this.entityManager.persist(category);
			}
		}
		catch(IllegalArgumentException ex)
		{
			//if the first argument does not denote an entity type or the second argument is is not a valid type for that entity primary key or is null
		}
		return category;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final DBCategory param) {
		final DBCategory category;
		try
		{
			category = new DBCategory(param.getName());
			this.entityManager.persist(category);
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
		return Response.ok(category).build();		
	}
	
	@Path("/delete/{name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteById(@PathParam("name") String name) {
		DBCategory category = null;
		try
		{
			category = this.entityManager.find(DBCategory.class, name);
			this.entityManager.remove(category);
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
		return Response.ok(category).build();	
	}
	
	@Path("/delete")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(final DBCategory param) {
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
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(final DBCategory param) {		
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
