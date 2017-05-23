NW-NM
=========

## How to include in a web-application

### For Maven Users: 
Add dependency to your POM: 

    <dependency>
      <groupId>dk.dma.enav.services</groupId>
      <artifactId>msi</artifactId>
      <version>1.6</version>
    </dependency>

### For Non-Maven Users
Download and add jar files to your WAR file: 
- 

### Configure JAX-RS endpoint
Can be done by adding NwNmRestService.class to class extending javax.ws.rs.core.Application, e.g. 

    @ApplicationPath("/rest")
    public class ApplicationConfig extends Application {
        public Set<Class<?>> getClasses() {
            return new HashSet<Class<?>>(Arrays.asList(NwNmRestService.class));
        }
    }

Your NwNm endpoint is then accessible on /rest//nw-nm//messages

### Application Configuration
Add NwNm properties by one (or several) of:
- add the default property file '/msi-default-configuration.properties' to the WARs default-configuration.properties file.
- add or overwrite the properties in the WARs default-configuration.properties file. 
- add or overwrite the properties in the external .properties file. 

### Security Configuration


### Web resources

