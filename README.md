E-navigation Services
=========

Is a Maven multiproject containing all server side modules, which can be used when putting together
a maritime web application like [ArcticWeb](https://github.com/maritime-web/ArcticWeb) or
[BalticWeb](https://github.com/maritime-web/BalticWeb).

## Server side technologies.

* Java 8
* Maven (for building)
* EJB3.1/JPA(Hibernate) (for persistance)
* CDI/JSR330 (for dependency injection)
* Resteasy (for JSON-webservices)
* Shiro (for security)
* Apache CXF (for SOAP-webservices)
* JUnit (for unit-test)
* Mockito (for mocking)

## Prerequisites ##

* Java JDK 1.8
* Maven 3.x
* Wildfly 8.2 (Maven setup to deploy to Wildfly)
* MySQL (Maven configures JBoss datasource to use MySQL)
* CouchDB
* a file called arcticweb.properties

## Initial setup

As root in MySQL - create a database and a user for ArcticWeb:

    create database embryo;
    create user 'embryo'@'localhost' identified by 'embryo';
    grant all on embryo.* to 'embryo'@'localhost';
    
You might need to configure the MySQL database to accept large packet sizes. This can be done in the mysql configuration file my.cnf
depending on OS it might be located in /etc/mysql/my.cnf

    [mysqld]
    max_allowed_packet=16M


## Building ##

    mvn clean install

## Checkstyle

See https://github.com/dma-dk/dma-developers

## Eclipse setup ##

Use standard Eclipse project:

* Go to command line and execute: mvn eclipse:eclipse
* Choose File > Import and then General > Existing Projects into Worksapce

Use Eclipse Maven integration:

* Choose File > Import and then Maven > Existing Maven Projects


## Database maintenaince

Hibernate can be used to maintain the database (good in development mode) where as Liquibase is used in more stable environments (like production). Which strategy is used depends on two properties hibernate.hbm2ddl.auto and embryo.liquibase.enabled.

<table>
  <tr>
    <th>Property</th><th>Values</th><th>Where</th><th>Default</th>
  </tr>
  <tr>
    <td>hibernate.hbm2ddl.auto</td><td>create, create-drop, update and validate</td><td>pom.xml or Maven command line property</td><td>validate</td>
  </tr>
  <tr>
    <td>embryo.liquibase.enabled</td><td>true/false</td><td>default or system configuration file (see above)</td><td>false (dev) / true (prod)</td>
  </tr>
  <tr>
    <td>embryo.liquibase.changelog</td><td>path to changelog file</td><td>default or system configuration file (see above)</td><td>/liquibase/changelog.xml</td>
  </tr>
</table>

hibernate.hbm2ddl.auto may be set on command line when building a war archive as follows: 

    mvn clean package -Dhibernate.hbm2ddl.auto=update


## Scheduled Jobs
The application contains a number of scheduled jobs responsible for fetching data from external systems or for calculating necessary values. These jboss are described below.

* dk.dma.embryo.vessel.job.AisReplicatorJob : This job replicates data from the external AIS server to ArcticWeb on regular schedule configured in the property embryo.vessel.aisjob.cron. The data is keeped in memory. Data might therefore not be available immidiately after a server/application (re)start.
* dk.dma.embryo.vessel.job.MaxSpeedJob : This job fetches a vessels route during the past 5 days from the AIS server and calculates the maximum speed for each vessel during those 5 days. Data is keeped in memory. 
* dk.dma.embryo.dataformats.job.DmiFtpReaderJob : This jobs transfers ice chart shape files from an FTP server to a folder in the operating system, which ArcticWeb is installed on. See property embryo.iceChart.dmi.localDirectory. The job will only transfer files not already transfered. Ice charts are not available to users before measured by dk.dma.arcticweb.filetransfer.ShapeFileMeasurerJob.
* dk.dma.embryo.dataformats.job.AariHttpReaderJob: This jobs transfers ice chart shape files from a HTTP server to a folder in the operating system, which ArcticWeb is installed on. See property embryo.iceChart.aari.localDirectory. The job will only transfer files not already transfered. Ice charts are not available to users before measured by dk.dma.arcticweb.filetransfer.ShapeFileMeasurerJob.
* dk.dma.embryo.dataformats.job.ShapeFileMeasurerJob : This job collects all shape files in the file system, measure their sizes and repopulates the database table ShapeFileMeasurements. The job will only measure new files.
* dk.dma.embryo.dataformats.inshore.DmiInshoreIceReportJob: This jobs transfers inshore ice report files from an FTP server to a folder in the operating system, which ArcticWeb is installed on. See property embryo.inshoreIceReport.dmi.localDirectory. The job will only transfer latest files.
* dk.dma.embryo.weather.service.DmiWeatherJob : This job transfers weather forecasts and warnings (XML files) from DMIs FTP server to the file system.
* dk.dma.embryo.dataformats.job.FcooFtpReaderJob : This job transfers FCOO forecast data (NetCDF) from FTP server to local file system
* dk.dma.embryo.dataformats.ForecastParserJob: This job parses forecast files received from different providers.
* dk.dma.embryo.tiles.service.TilerJob : This job deletes old tile set related files from the file system (georeference image file, log files, tiles) as well as deletes and creates TileSet entries in database.
* dk.dma.embryo.tiles.service.TilerServiceBean : This is started by TilerJob to generate a tile set from a georeferenced image file
* dk.dma.embryo.tiles.service.DmiSatelliteJob : This job transfers georeferenced images from FTP server to local file system


## Surveillance

The application contains a number of integrations with external systems. These may be either jobs running at different schedules or HTTP calls directly to the external system. The success rate of the integration executions are logged in the application database and can be retrieved using a public REST call. 

Names of the integration jobs/services of can be retrieved calling the URL:

    http(s)://host/arcticweb/rest/log/services

The latest log entry of a specific job/service can be retrieved by the URL

    http(s)://host/arcticweb/rest/log/latest?service=dk.dma.arcticweb.filetransfer.DmiFtpReaderJob

where dk.dma.arcticweb.filetransfer.DmiFtpReaderJob is the job name. This will return a JSON response in the format

    {
      "service":"dk.dma.embryo.dataformats.job.DmiFtpReaderJob",
      "status":"OK",
      "message":"Scanned DMI (ftp.ais.dk) for new files. Files transferred: 0",
      "stackTrace":null,
      "date":1387353901000
    }

where the important fields are 
* status: may have the values "OK" or "ERROR" 
* date: The time of logging in milliseconds since the standard base time known as "the epoch", namely January 1, 1970, 00:00:00 GMT.

At the time of writing the current services are candidates for surveillance

* dk.dma.embryo.vessel.job.AisReplicatorJob
* dk.dma.embryo.vessel.service.AisDataServiceImpl
* dk.dma.embryo.vessel.job.MaxSpeedJob
* dk.dma.embryo.dataformats.job.ShapeFileMeasurerJob.dmi 
* dk.dma.embryo.dataformats.job.DmiFtpReaderJob
* dk.dma.embryo.dataformats.inshore.DmiInshoreIceReportJob
* dk.dma.embryo.dataformats.service.ForecastServiceImpl
* dk.dma.embryo.weather.service.DmiWeatherJob
* dk.dma.embryo.user.json.AuthenticationService
* dk.dma.embryo.msi.MsiClientImpl
* dk.dma.embryo.common.mail.MailServiceImpl
* dk.dma.embryo.metoc.service.MetocServiceImpl
* dk.dma.embryo.dataformats.job.FcooFtpReaderJob
* dk.dma.embryo.tiles.service.TilerJob
* dk.dma.embryo.tiles.service.TilerServiceBean
* dk.dma.embryo.tiles.service.DmiSatelliteJob

## Developer Logging

Developer logging is performed using SLF4J. No binding to log4j or logback exists in deployed war. Instead it depends on a suitable SLF4J binding (http://www.slf4j.org/manual.html#swapping) to be present on the classpath (with logging framework and configuration). 

JBoss Logging is configured in configuration/standalone.xml. Development environment could be setup with the following values:

    <subsystem xmlns="urn:jboss:domain:logging:1.1">
        <console-handler name="CONSOLE">
            <level name="DEBUG"/>
        </console-handler>

	...

        <logger category="dk.dma">
            <level name="DEBUG"/>
        </logger>
        <root-logger>
            <level name="DEBUG"/>
            <handlers>
                <handler name="CONSOLE"/>
                <handler name="FILE"/>
            </handlers>
        </root-logger>
    </subsystem>





