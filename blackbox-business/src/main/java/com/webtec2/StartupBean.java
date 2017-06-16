package com.webtec2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Singleton
@Startup
public class StartupBean {

	@PersistenceContext
	private EntityManager entityManager;

	@PostConstruct
	public void startup() {

		/**
		DBUser user = new DBUser();
		benutzer.setUsername("Max");
		benutzer.setPassword("1234");
		benutzer.setCreatedOn(new Date());
		benutzer.setLastVisitedOn(new Date());
		benutzer.setIsAdmin(false);
		
		this.entityManager.persist(user);**/
		
		final DBMessage firstMessageItem = this.entityManager.find(DBMessage.class, 1L);
		
		if(firstMessageItem == null) {
			final DBMessage msg = new DBMessage();
			msg.setHeadline("Info");
			msg.setContent("Project has been successfully created.");
			msg.setPublishedOn(new Date());

			this.entityManager.persist(msg);
		}		
	}

	@PreDestroy
	public void shutdown() {
		// potential cleanup work
	}
}
