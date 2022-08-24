```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>3.1.3</version>
</dependency>
```





```java
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("cookie",
                    "token=B48y8rOQS6juc99BlOn1P8Iu1jo0sm7wis2TKoc8LxNtx0zExwq7dzvkjelfT9Zs");
        };
    }
}
```

```java
import ati.home.core.config.FeignConfig;
import ati.home.core.context.cargomart.company.dto.CompanyCargomartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "fcCargomartCompany", configuration = FeignConfig.class, url = "${cargomart.api}")
public interface CompanyCargomartFeignClient {
    @GetMapping(value = "/company/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    CompanyCargomartDto findCompanyById(@PathVariable(name = "companyId") String companyId);
}

```

