package com.webtec2;

import com.webtec2.auth.permission.*;
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
public class CategoriesCRUD implements CRUDInterface<DBCategory>{
	
	/**
	 * API overview:
	 *  / @GET -> read all category data
	 *  /{name} @GET -> read data from category {name}
	 *  / @POST -> create new category
	 *  /delete @POST -> remove category
	 *  /delete/{name} @GET -> remove category {name}
	 *  /update @POST -> update category data
	 */	

	@PersistenceContext
	private EntityManager entityManager;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response readAll()	{
		final Subject subject = SecurityUtils.getSubject();
		final Permission readCategoryItemPermission = new ReadCategoryItemPermission(subject.getPrincipal().toString());
		if(!subject.isPermitted(readCategoryItemPermission))
		{
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
		}
		catch(IllegalArgumentException ex)
		{			
			//if the selection is a compound selection and more than one selection item has the same assigned alias
		}
		return Response.ok(result).build();	
	}
	
	@Path("/{name}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("id") final String name) {
		final Subject subject = SecurityUtils.getSubject();
		final Permission readCategoryItemPermission = new ReadCategoryItemPermission(subject.getPrincipal().toString());
		if(!subject.isPermitted(readCategoryItemPermission))
		{
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
		}
		return Response.ok(category).build();
	}	
	
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
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
		final Subject subject = SecurityUtils.getSubject();
		final Permission writeCategoryItemPermission = new WriteCategoryItemPermission(category, subject.getPrincipal().toString());
		if(!subject.isPermitted(writeCategoryItemPermission))
		{
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		this.entityManager.persist(category);
		return Response.ok(category).build();		
	}
	
	
	@Path("/delete/{name}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
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
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			//if invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		final Subject subject = SecurityUtils.getSubject();
		final Permission deleteCategoryItemPermission = new DeleteCategoryItemPermission(category, subject.getPrincipal().toString());
		if(!subject.isPermitted(deleteCategoryItemPermission))
		{
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}		
		this.entityManager.remove(category);
		return Response.ok(category).build();	
	}
	
	@Path("/delete")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(final DBCategory param) {
		final Subject subject = SecurityUtils.getSubject();
		final Permission deleteCategoryItemPermission = new DeleteCategoryItemPermission(param, subject.getPrincipal().toString());
		if(!subject.isPermitted(deleteCategoryItemPermission))
		{
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
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
	public Response update(final DBCategory param) {	
		final Subject subject = SecurityUtils.getSubject();
		final Permission writeCategoryItemPermission = new WriteCategoryItemPermission(param, subject.getPrincipal().toString());
		if(!subject.isPermitted(writeCategoryItemPermission))
		{
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
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
