package com.webtec2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import com.webtec2.DBUser;
import com.webtec2.DBMessage;
import java.util.UUID;
import java.util.HashMap;

@Singleton
@Startup
public class StartupBean {

	@PersistenceContext
	private EntityManager entityManager;

	@PostConstruct
	public void startup() {
		
		final DBMessage firstMessageItem = this.entityManager.find(DBMessage.class, 1L);
		
		if(firstMessageItem == null) {
			//Create Administrator user
			final DBUser user = new DBUser();
			user.setUsername("MaX");
			user.setPassword("1234");
			user.setIsAdmin(true);
			
			this.entityManager.persist(user);
					
			final DBCategory category = new DBCategory();
			category.setName("Verkauf");
			category.setDescription("Verkaufskategorie");
						
			this.entityManager.persist(category);
					
			//Create first message
			final DBMessage msg = new DBMessage()
			msg.setUser(user);
			msg.setCategory(category);
			msg.setHeadline("Information", "Project has been successfully created!");
			this.entityManager.persist(msg);
		}		
	}

	@PreDestroy
	public void shutdown() {
		// potential cleanup work
	}
}
