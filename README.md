# Descartes Pet Supply Store Initial Working Git #

Working Git for Descartes Pet Store (working name). This git will be moved to
the institute gitlab when Norbert returns from vacation. We will also rename it
to a cool new name, once we have one.

## 1. Directory Structure ##

We structure all projects in the following categories
(and corresponding directories):

* _services_: all services build to WARs, they are deployed on an application
  server (tomcat 8.5 in the dev environment)
    * _tools.descartes.petstore.webui_: WebUI Component
    * _tools.descartes.petstore.store_: Buisiness Logic Component
    * _tools.descartes.petstore.recommender_: Recommender Component
    * _tools.descartes.petstore.persistence_: Persistence Provider Component
    * _tools.descartes.petstore.image_: Image Provider Component
* _interfaces_: build to Jars, services depend on them
    * _tools.descartes.petstore.entities_: Entities that are passed between
      services
* _utilities_: build to Jars, services depend on them
    * _tools.descartes.petstore.registryclient_: Common client logic for
      communicating with the Eureka registry
    * _tools.descartes.petstore.rest_: Default REST endpoints and clients
* _devenv_: Code and libraries only needed to run everything inside Eclipse
    * _tools.descartes.petstore.devenvlibraries_: Libraries used by the services
      inside the Tomcat in Eclipse. When building using Maven, these are ignored
      and replaced by the latest auto-downloaded Maven dependencies.

## 2. Setting up required Software ##

* Recommended IDE is Eclipse JavaEE Edition:
http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/oxygenr  
Download and install as usual.
    * Note: You MUST configure your Eclipse to use a __Java 8 JDK__ as default Java
      environment. A JRE will not work with Maven.
* Development also requires a Servlet Container / Application Server. We
recommend Tomcat 8.5. Eclipse must be configured to run with Tomcat (see below).

### 2.1 Setup Tomcat ###

* Download the latest Tomcat 8.5 from the tomcat website:
http://tomcat.apache.org/download-80.cgi  
Direct Links (Juli 2017):
    * Windows: http://mirror.synyx.de/apache/tomcat/tomcat-8/v8.5.16/bin/apache-tomcat-8.5.16-windows-x64.zip
    * Linux (don't apt-get!): http://mirror.synyx.de/apache/tomcat/tomcat-8/v8.5.16/bin/apache-tomcat-8.5.16.tar.gz
* Extract to a directory of your choice
* In Eclipse
    * Window -> Show View -> Servers
    * Right click in server view -> New -> Server
    * In the list select "Tomcat v8.5 Server", host name and Server name must
      remain at default values:  
    Server's host name: "localhost"  
    Server name: "Tomcat v8.5 Server at localhost"
    * click "Next"
    * Select your tomcat directory for "Tomcat installation directory", as JRE
      select "Workbench default JRE"
    * click "Next"
    * Skip adding any projects to the server for now.
    * "Finish"

<!--- No longer needed:


### Configure Eclipse for JPA ###

You must configure your Eclipse to use a JPA Provider so that the
tools.descartes.petstore.persistence service can be deployed. Unfortunately,
this is a bit annoying to do nad requires a weird workaround:

* Start the creation of a new JPA Project: File -> New -> JPA Project
* Any project name is ok; set JPA Version to 2.1 and Configuration to
  "Basic JPA Configuration" (should be default)
* Click "Next >"
* Skip source filder configuration; click "Next >"
* As JPA implementation Type, select "User Library" (should be default)  
  Then, to the right click the small Floppy disk symbol with  
  "Download Library ..." on mouseover
    * Select the newest EclipseLink version and click "Next >"
    * Accept the License and click "Finish"
* Now cancel the creation of the JPA Project
* You may have to clean the Exlipse Projects, for eclipse to discover the
  persistence library
--->


### 2.2 Deploy Application on Tomcat in Eclipse ###

For this step, all applications must have been imported into the Eclipse
workspace.

All regular "Jar" (_interfaces_ and _utilities_) and _"devenv"_ projects must
have been imported into Eclipse. Once that is achieved, we can add the
web-projects (_services_) to tomcat.

<!--- Alternate solution deploys inside the webapps:
* To add the dependent Jar-projects (interfaces and utilities):
    * In the Servers View double click on the Tomcat server
    * in the "General Information" section, click on "Open launch configuration"
    * Go to the "Classpath" tab
    * Select "user Entries", then click "Add Projects..."
    * Add all Projects from the utilities and interface directories in the Git:
        * tools.descartes.petstore.entities
        * tools.descartes.petstore.registryclient
    * click "OK", "Apply", "Ok". You can now also close the server Overview.
    --->
* Add the services to the server:
    * In the Servers View right click on the Tomcat server
      -> "Add and Remove..."
    * Select all avalable _tools.descartes.petstore projects_ and click
      "Add All >>"
    * Click "Finish"; You may have to "clean", "publish"
      and/or restart the server

<!---
### Useful Plugins ###
* JBoss Tools  
  Install from Eclipse Marketplace  
    * Features Auto-Completions for JSTL, etc.
    * Has a fancy REST endpoint auto-generator
--->

### 2.3 Install CheckStyle ###

Install Checkstyle into your Eclipse. You can find it here (Browser Link):
http://eclipse-cs.sourceforge.net/

The Pet Supply Store uses the well-established and SPEC RG approved
LIMBO-Checkstyle. It is located in the root directory of the Pett Suply Store
Git repository. To add it to your Eclipse:

* Window -> Preferences -> Checkstyle
* New...
* As Type, select _External Configuration File_, enter a name of your choice,
  as the Location select the _limbo_checkstyle.xml_ in the petstore's Git root
* "Ok"
* Select the newly added checkstyle in the list and click "Set as Default"
* "Ok"

### 2.4 Access the WebUI from Eclipse ###

Start the tomcat, the WebUI should be at:
http://localhost:8080/tools.descartes.petstore.webui/

Alternatively: right click on the WebUI Project -> "Run as ..." -> Run on Server

## 3. Using Libraries: Eclipse and Maven ##

Eclipse does somethimes not have access to the same dependencies as Maven. This
means that adding libraries, requires adding it as a Maven dependency and adding
it to Eclipse in a way so that it is deployed to the testing Tomcat.

This is what the tools.descartes.petstore.devenvlibraries Project is for. Almost
all of the libs you need are already there. To add one to your project, add the
Maven dependency, then:
* Right click your Project -> Properties -> Deployment Assembly
* Add ... -> Archives from Workspace -> Add...
* Expand the _tools.descartes.petstore.devenvlibraries_ Project
* Select all relevant libraries -> "Ok"
* "Finish" -> "Apply" -> "Ok"

If you add new libraries to _tools.descartes.petstore.devenvlibraries_ please
package them in a separate folder. There are a lot of libraries there. A common
practice is copying the auto-downloaded Maven libraries from the Maven working
directory after running "mvn clean:verify".
