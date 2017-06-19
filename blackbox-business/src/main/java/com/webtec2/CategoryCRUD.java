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
public class CategoryCRUD implements CRUDInterface<DBCategory> {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final DBCategory category) {
		this.entityManager.persist(category);

		return Response.ok(category).build();
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DBCategory> readAll() {
		final CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		final CriteriaQuery<DBCategory> query = builder.createQuery(DBCategory.class);

		final Root<DBCategory> from = query.from(DBCategory.class);

		query.select(from);

		return entityManager.createQuery(query).getResultList();
	}

	@Override
	@Path("/{id}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public DBCategory read(@PathParam("id") final long id) {
		return entityManager.find(DBCategory.class, id);
	}

	@Override
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(DBCategory dbCategory) {
		entityManager.merge(dbCategory);

		return Response.ok(dbCategory).build();
	}

	@Override
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") final long id) {
		DBCategory tmp = entityManager.find(DBCategory.class, id);
		entityManager.remove(tmp);

		return Response.ok(tmp).build();
	}
}
