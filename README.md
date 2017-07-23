# BlackBoxJPA

ver:0.0.2.alpha
last modified: 23.07.17

contributors: 
Marcel Marchewka
Yunus Öztürk
Milosz Sobieraj
Irina Solovyeva

* Aktuelle .war-Datei ist unter diesem Link zu finden -> http://www85.zippyshare.com/v/DcTBdof9/file.html
* Ergebnisse zu ALEX sind unter diesem Link zu finden -> (kommt gleich)
* Screenshots: 
+ Pic1: ![alt Pic1](http://imgur.com/SAoGHBU)
+ Pic2: ![alt Pic2](http://imgur.com/uFUufcR)
+ Pic3: ![alt Pic3](http://imgur.com/QxmcKna)

#aktuelle Bugs:
+ Unsupported Media Type (415 response) -> Rest api kann die korrekten Abfragen über POST/PUT nicht bearbeiten, Lösungsweg: header hinzufügen mit 'content-type':'application/json'


# 0.0.2-alpha released:
+ Add new entities (if needed) (blackbox-persistence) 
+ Extend REST-Interface with CRUD-classes (blackbox-business)
+ Update web interface (blackbox-presentation)
+ Create Unit test cases (blackbox-test)

# 0.0.1-SNAPSHOT released
+ :8080/blackbox -> web application
+ :8080/rest/{messages|types|persons)/ -> REST Interface

# DESCRIPTION

A small project that exposes rudimentary (RESTful) web services.

-----------
Structure:

The application follows a basic 3 layer structure, separating the persistence, business and presentation layer.

* persistence: A maven module to manage the database entities of the application.

Similar to examples 04 and 05, some configuration can be found in the persistence.xml file. A noteworthy difference:
Since we don't want to manually manage the persistence provider but use the one provided by the application server, some
settings have to applied to the application server, rather than the application. In detail: the database connection is
configured in the blackbox-ds.xml (in the blackbox-presentation module) and configured to be available under the jndi
name 'java:jboss/datasources/blackbox'. In the persistence.xml, this jndi name is configured as the source of our
entity manager.


* business: A maven module containing the RESTful application interface.

The MessageCRUD is an exemplary class for handling resource requests using the tools of the JAX-RS specification presented
in the lecture. The 'conf' package holds additional configurations, such as the activator for the JAX-RS subsystem and
a provider for the JSON (de-)serialization library 'Jackson'. Additionally an EJB Startup Bean is provided, that allows
to create some dummy data during the application startup.


* presentation: A maven module for the web resources accessed by the users browser:

The dart sourcecode is located under 'src/main/dart'. It is a variant of the angular-quickstart (see
https://github.com/angular-examples/quickstart) with some extensions to show the communication with the REST-backend.
Namely, components for posting messages and viewing the latest messages have been added, as well as a Dart-DAO for encapsulating
the data of the backend.

The dart-compiler is configured in the pom.xml using the dart-maven-plugin. For this plugin to work, it is necessary to
provide a path to the dart-sdk. In the given example, this is configured via a build property, which is defined the
parent pom.xml. Currently this value is hardcorded and may have to be changed according to your setup to work properly.
For a more flexible configuration that can be shared across multiple developers, an environment variable
(e.g. ${env.DART_SDK}) may be used, which each developer can set for himself/herself accordingly.

After successfully building the example app and deploying it in an application server, the application should be
available under http://localhost:8080/blackbox.

Attention: If you use the wildfly-swarm plugin to run your application, note that the wildfly-swarm:run goal is prone to
errors. The wildfly-swarm:package goal (or rather the resulting ...-swarm.jar) should work fine.

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
* Copy your web archive (located in blackbox-presentation/target/blackbox-presentation-0.0.1.SNAPSHOT.war) to
$WILDFLY_HOME/standalone/deployments/
* Run $WILDFLY_HOME/bin/standalone.sh (or .bat if on windows) to start your application server.


Using the wildfly-swarm maven plugin:

* After 'mvn install'ing your application, switch to the blackbox-presentation directory and execute the 'package' goal
of the 'wildfly-swarm' plugin (mvn wildfly-swarm:package).
* Wait (a lot to download) until this steps succeeds.
* You can either directly start the target/blackbox-...-swarm.jar (via java -jar) or
* Run mvn wildfly-swarm:run to start the application server.


------------

Once the application server successfully started, the provided message resources should be available under
'http://localhost:8080/blackbox/message' etc.
