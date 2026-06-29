# Build Starter

### Build locally

```bash
mvn clean install
```

This installs the starter into your local Maven repository.

# Usage

Add the dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>tech.meliora.mulika</groupId>
    <artifactId>spring-boot-starter-mulika</artifactId>
    <version>3.5.15</version> <!-- your spring boot version --> 
</dependency>
```
## Configuration

Add the following properties to your `application.properties` or `application.yml`.

### application.properties

```properties
mulika.application=<your-application-name>
mulika.module=<your-module-name>
mulika.report-interval=<time in ms after which you will report to Mulika eg 60_000>
mulika.url=https://mulika.natujenge.ke
mulika.api-key=YOUR-API-KEY
```

### application.yml

```yaml
mulika:
  application: <your-application-name>
  module: <your-module-name>
  report-interval: <time in ms after which you will report to Mulika eg 60_000>
  url: https://mulika.natujenge.ke
  api-key: YOUR-API-KEY
```