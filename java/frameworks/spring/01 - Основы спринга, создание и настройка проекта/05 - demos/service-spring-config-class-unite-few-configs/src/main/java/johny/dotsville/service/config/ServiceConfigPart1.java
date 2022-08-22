package johny.dotsville.service.config;

import johny.dotsville.service.CityService;
import johny.dotsville.service.MarriageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfigPart1 {
    @Bean
    public CityService cityService(MarriageService marriageService) {
        return new CityService(marriageService);
    }
}
