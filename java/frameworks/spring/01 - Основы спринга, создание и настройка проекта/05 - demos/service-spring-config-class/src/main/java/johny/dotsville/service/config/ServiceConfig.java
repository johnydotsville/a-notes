package johny.dotsville.service.config;

import johny.dotsville.service.CityService;
import johny.dotsville.service.MarriageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "johny.dotsville.service" })
public class ServiceConfig {
    @Bean
    public CityService cityService(MarriageService marriageService) {
        return new CityService(marriageService);
    }
}
