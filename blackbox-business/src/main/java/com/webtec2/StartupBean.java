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
			
			DBUser admin = new DBUser("admin", "admin");
			DBUser user = new DBUser("user", "user");
			this.entityManager.persist(admin);
			this.entityManager.persist(user);

			//Create 3 users			
			DBMessage message = new DBMessage(admin, this.entityManager.find(DBCategory.class, "Informiere"), "Willkommen", "Die Applikation ist erfolgreich gestartet.");
			this.entityManager.persist(message);
			message = new DBMessage(user, this.entityManager.find(DBCategory.class, "Informiere"), "Willkommen", "Die Applikation ist erfolgreich gestartet.");
			this.entityManager.persist(message);
			message = new DBMessage(user, this.entityManager.find(DBCategory.class, "Informiere"), "Willkommen", "Die Applikation ist erfolgreich gestartet.");
			this.entityManager.persist(message);
			message = new DBMessage(admin, this.entityManager.find(DBCategory.class, "Verkaufe"), "Sonderpreis UBoot", "Nagelneu und aus raucherfreien Haushalt.");
			this.entityManager.persist(message);
			message = new DBMessage(user, this.entityManager.find(DBCategory.class, "Suche"), "Pizza-Lieferant gesucht", "Fur kleines Geld grossen Hunger befriedigen.");
			this.entityManager.persist(message);
			message = new DBMessage(admin, this.entityManager.find(DBCategory.class, "Kaufe"), "Lotto-Schein", "Naturlich nur einen, der Geld bringt.");
			this.entityManager.persist(message);
			
			System.out.println("[Info] Successfully created 4 categories and 5 message");
		}		
	}

	@PreDestroy
	public void shutdown() {
		// potential cleanup work
		System.out.println("[Info] Shutting down application");
		this.entityManager.clear();		
	}
}
