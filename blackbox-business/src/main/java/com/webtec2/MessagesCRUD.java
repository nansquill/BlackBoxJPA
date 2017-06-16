package com.webtec2;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import java.util.Date;
import java.util.List;

@Path("/messages")
@Transactional
public class MessagesCRUD {

	@PersistenceContext
	private EntityManager entityManager;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DBMessage> readAllAsJSON() {
		final CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		final CriteriaQuery<DBMessage> query = builder.createQuery(DBMessage.class);

		final Root<DBMessage> from = query.from(DBMessage.class);

		query.select(from);

		return this.entityManager.createQuery(query).getResultList();
	}

	@Path("/{id}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public DBMessage readAsJSON(@PathParam("id") final long id) {
		return this.entityManager.find(DBMessage.class, id);
	}

	@Path("/{id}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_XML)
	public DBMessage readAsXML(@PathParam("id") final long id) {
		return this.entityManager.find(DBMessage.class, id);
	}

	// An example of how to misuse the API and do something unRESTful
	@Path("/new/{headline}/{content}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("headline") final String headline, @PathParam("content") final String content) {

		final DBMessage msg = new DBMessage();

		msg.setHeadline(headline);
		msg.setContent(content);
		msg.setPublishedOn(new Date());
		msg.setIsOnline(true);

		this.entityManager.persist(msg);

		return Response.ok(msg).build();
	}

	// More idiomatic way of creating items
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final DBMessage param) {

		final DBMessage msg = new DBMessage();

		msg.setHeadline(param.getHeadline());
		msg.setContent(param.getContent());
		msg.setPublishedOn(new Date());
		msg.setIsOnline(true);

		this.entityManager.persist(msg);

		return Response.ok(msg).build();
	}
}
