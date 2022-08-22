package johny.dotsville.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ServiceConfigPart1.class,
        ServiceConfigPart2.class })
public class ServiceConfig {

}
