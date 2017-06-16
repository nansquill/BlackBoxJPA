A small project that exposes rudimentary (RESTful) web services.

-----------
Structure:

The application follows a basic 3 layer structure, separating the persistence, business and presentation layer.

* persistence: A maven module to manage the database entities of the application.

Similar to examples 04 and 05, some configuration can be found in the persistence.xml file. A noteworthy difference:
Since we don't want to manually manage the persistence provider but use the one provided by the application server, some
settings have to applied to the application server, rather than the application. In detail: the database connection is
configured in the example06-ds.xml (in the example06-presentation module) and configured to be available under the jndi
name 'java:jboss/datasources/example06'. In the persistence.xml, this jndi name is configured as the source of our
entity manager.


* business: A maven module containing the RESTful application interface.

The NewsCRUD is an exemplary class for handling resource requests using the tools of the JAX-RS specification presented
in the lecture. The 'conf' package holds additional configurations, such as the activator for the JAX-RS subsystem and
a provider for the JSON (de-)serialization library 'Jackson'. Additionally an EJB Startup Bean is provided, that allows
to create some dummy data during the application startup.


* presentation: A maven module for the web resources accessed by the users browser:

Currently, this module holds no real resources, since this application has no web frontend yet. However, it aggregates
the other components of the application to a web application archive. Since this module will represent the artifact
being picked up by the application server, some further settings can be configured here. Most notably for now is the
context root of the application, configured in the jboss-web.xml file.


* test: A module containing test resources for the RESTful application interface.

By default, the module is not part of the build lifecycle, since it requires the application to be already running,
which is not the case during the build lifecycle. It holds a small test case that fetches data over the REST api and
validates the content of the response. It can be activated by activating the "withTest" profile.


-----------
Deployment:


Using only classes and annotations of the Java EE 7 API, the application should run in any Java EE 7 compliant
application server. However, some configurations are specific to the Wildfly application server, which is the suggested
application server for this application. While other application servers are possible (e.g. Glassfish) they may need
additional configuration.


For using the Wildfly application server, you have two possible deployment configurations:


The manual way:

* Download the Wildfly Application Server 10.1 Full Profile (http://wildfly.org/downloads/) and extract the archive to a
location of your choice.
* Copy your web archive (located in example06-presentation/target/example06-presentation-0.1.SNAPSHOT.war) to
$WILDFLY_HOME/standalone/deployments/
* Run $WILDFLY_HOME/bin/standalone.sh (or .bat if on windows) to start your application server.


Using the wildfly-swarm maven plugin:

* After 'mvn install'ing your application, switch to the example06-presentation directory and execute the 'package' goal
of the 'wildfly-swarm' plugin (mvn wildfly-swarm:package).
* Wait (a lot to download) until this steps succeeds.
* You can either directly start the target/example06-...-swarm.jar (via java -jar) or
* Run mvn wildfly-swarm:run to start the application server.


------------

Once the application server successfully started, the provided news resources should be available under
'http://localhost:8080/example06/news' etc.
