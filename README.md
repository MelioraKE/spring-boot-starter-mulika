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
#mulika.api-key=YOUR-API-KEY
mulika.api-key=eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJia2l0dW5kYUBtZWxpb3JhLnRlY2giLCJhdXRoIjoiUk9MRV9BUEkiLCJvaWQiOjIsIm90eXBlIjoiTUFTVEVSIiwidWlkIjoxMDAsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6MTk2NDUiLCJhdWQiOiJtdWxpa2EtYXBpIiwiaWF0IjoxNzgwNTA0NTM2MzM5LCJqdGkiOiI3OTljOTFkYy1iOTA4LTQxNjYtODM5ZC00ZDNmODhlYjk3NmEiLCJ1dCI6dHJ1ZSwiZXhwIjoyMDk1ODY0NTM2fQ.abo-ErZ8Zf-ly9g8G4nM6AfPpcnF7gENaRHmapIO0sw9M_Ue5zDTUT94oUOLXUFqrfT_rpZQHrfgfSCWltoBoCdsgCYrLK0GZphzKHbzN1oQPZU2vOYEkroHze5aXVY6DqbNIY0mP_zNnWWLOgWXp3kyjRtup3yCCgzsaD0GNfC3sAr7o4Ck-0qoz154-YaTJ2CszusZ6VV95g0fRaQs4kfCzKCUPuc2W7qdhqYilp37ZqvRmyAW8NpX29ZnvsM-jzvvM9TB4odpz83FYy8jUMpC7PpbDhoU0MDZ9ISDGG459iNf1a3AbG2Ofo31WtrA3b21Be3nKai49SrLe7VozA
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

