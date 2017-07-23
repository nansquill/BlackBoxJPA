package com.webtec2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import com.webtec2.DBMessage;
import com.webtec2.DBCategory;
import java.util.UUID;
import java.util.HashMap;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.session.Session;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;


@Singleton
@Startup
public class StartupBean {

	@PersistenceContext
	private EntityManager entityManager;

	@PostConstruct
	public void startup() {
		System.out.println("[Info] Starting application");
		DBCategory itemCategory = this.entityManager.find(DBCategory.class, "Verkaufe");
		
		if(itemCategory == null)
		{
			//Standard categories
			this.entityManager.persist(new DBCategory("Verkaufe"));
			this.entityManager.persist(new DBCategory("Tausche"));
			this.entityManager.persist(new DBCategory("Suche"));
			this.entityManager.persist(new DBCategory("Informiere"));

			//Create 3 users			
			DBMessage message = new DBMessage("admin", this.entityManager.find(DBCategory.class, "Informiere"), "Willkommen", "Die Applikation ist erfolgreich gestartet.");
			this.entityManager.persist(message);
			System.out.println("[Info] Successfully created 4 categories and 1 message");
		}		
	}

	@PreDestroy
	public void shutdown() {
		// potential cleanup work
		System.out.println("[Info] Shutting down application");
		this.entityManager.clear();
		
	}
}
