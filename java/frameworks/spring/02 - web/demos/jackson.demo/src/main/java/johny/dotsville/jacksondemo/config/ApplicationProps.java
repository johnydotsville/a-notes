package johny.dotsville.jacksondemo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ApplicationProps {
    @Value("${company-info.file}")
    private String companyInfoFilename;
}
