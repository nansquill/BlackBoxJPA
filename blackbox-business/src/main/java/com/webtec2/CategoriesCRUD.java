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
			System.out.println("[Error] Entity manager not reachable");
			System.out.println(ex);
		}
		catch(IllegalArgumentException ex)
		{			
			System.out.println("[Error] Duplicated category found");
			System.out.println(ex);
		}
		//Check permission
		final Subject subject = SecurityUtils.getSubject();
		for(DBCategory category : result) {
			if(!subject.isPermitted(new ReadCategoryItemPermission(category, subject))) {
				result.remove(category);
				System.out.println("[Info] Result entry removed");
			}
		}	
		System.out.println("[Info] Found " + result.size() + " categories");
		return Response.ok(result).build();
	}

	@Path("/{name}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("name") String name) {
		DBCategory category = null;
		try
		{
			category =  this.entityManager.find(DBCategory.class, name);
			if (category == null) {
				category = new DBCategory(name);
				//Check permission
				final Subject subject = SecurityUtils.getSubject();
				if(!subject.isPermitted(new ReadCategoryItemPermission(category, subject))) {
					System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to read category");
				}
				this.entityManager.persist(category);
				System.out.println("[Info] Category " + category.getName() + " has been created");
			}
		}
		catch(IllegalArgumentException ex)
		{
			System.out.println("[Error] Category " + name + " couldn't be found");
			System.out.println(ex);
		}
		//Check permission
		final Subject subject = SecurityUtils.getSubject();
		if(!subject.isPermitted(new ReadCategoryItemPermission(category, subject))) {
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to read category");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		System.out.println("[Info] Found category " + category.getName());
		return Response.ok(category).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final DBCategory param) {
		//Check permission
		final Subject subject = SecurityUtils.getSubject();
		if(!subject.isPermitted(new WriteCategoryItemPermission(param, subject))) {
			System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to write category");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		final DBCategory category;
		try
		{
			category = new DBCategory(param.getName());
		}
		catch(EntityExistsException ex)
		{
			System.out.println("[Error] Duplicated category " + param.getName());
			System.out.println(ex);
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			System.out.println("[Error] Category is not valid");
			System.out.println(ex);
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
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
			//Check permission
			final Subject subject = SecurityUtils.getSubject();
			if(!subject.isPermitted(new DeleteCategoryItemPermission(category, subject))) {
				System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to delete category");
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
			this.entityManager.remove(category);
		}
		catch(IllegalArgumentException ex)
		{
			System.out.println("[Error] Category " + name + " is invalid");
			System.out.println(ex);
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		System.out.println("[Info] Deleted category " + category.getName());
		return Response.ok(category).build();	
	}
	
	@Path("/update/{name}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("name") final String name, final DBCategory cat) {
		DBCategory param = null;
		try
		{
			param = this.entityManager.find(DBCategory.class, name);
			//Check permission
			final Subject subject = SecurityUtils.getSubject();
			if(!subject.isPermitted(new DeleteCategoryItemPermission(param, subject))) {
				System.out.println("[Error] User " + subject.getPrincipal() + " is not permitted to update category");
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
			this.entityManager.refresh(cat);
		}
		catch(EntityNotFoundException ex)
		{
			System.out.println("[Error] Duplicated category " + param.getName());
			System.out.println(ex);
			return Response.status(Status.CONFLICT).build();
		}
		catch(IllegalArgumentException ex)
		{
			System.out.println("[Error] Category is not valid");
			System.out.println(ex);
			return Response.status(Status.BAD_REQUEST).build();
		}
		catch(TransactionRequiredException ex)
		{
			System.out.println("[Error] Internal server error");
			System.out.println(ex);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		System.out.println("[Info] Category " + param.getName() + " has been edited");
		return Response.ok(param).build();	
	}	
}
