package johny.dotsville.service.config;

import johny.dotsville.service.MarriageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfigPart2 {
    @Bean
    public MarriageService marriageService() {
        return new MarriageService();
    }
}
