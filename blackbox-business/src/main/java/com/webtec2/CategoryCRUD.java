package com.webtec2;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

/**
 * blackbox-parent | 6/19/17 8:56 AM | yunus
 */
@Path("/categories")
@Transactional
public class CategoryCRUD {

	@PersistenceContext
	private EntityManager entityManager;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DBCategory> readAllAsJSON() {
		final CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		final CriteriaQuery<DBCategory> query = builder.createQuery(DBCategory.class);

		final Root<DBCategory> from = query.from(DBCategory.class);

		query.select(from);

		return this.entityManager.createQuery(query).getResultList();
	}

	@Path("/{name}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public DBCategory readAsJSON(@PathParam("name") final String name) {
		return this.entityManager.find(DBCategory.class, name);
	}

	// More idiomatic way of creating items
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final DBCategory param) {
		final DBCategory msg = new DBCategory();

		msg.setName(param.getName());
		msg.setDescription(param.getDescription());
		msg.setCreatedOn(new Date());

		this.entityManager.persist(msg);

		return Response.ok(msg).build();
	}
}
