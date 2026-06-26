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
    <artifactId>mulika-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```
## Configuration

Add the following properties to your `application.properties` or `application.yml`.

### application.properties

```properties
mulika.application=starter
mulika.module=starter
mulika.report-interval=60000
mulika.url=https://mulika.natujenge.ke
mulika.api-key=YOUR-API-KEY
```

### application.yml

```yaml
mulika:
  application: starter
  module: starter
  report-interval: 60000
  url: https://mulika.natujenge.ke
  api-key: YOUR-API-KEY
```

