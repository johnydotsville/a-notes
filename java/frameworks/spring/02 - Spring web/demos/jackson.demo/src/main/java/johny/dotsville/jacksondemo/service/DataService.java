package johny.dotsville.jacksondemo.service;

import johny.dotsville.jacksondemo.config.ApplicationProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class DataService {
    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    private final ApplicationProps props;

    @Autowired
    public DataService(ApplicationProps props) {
        this.props = props;
    }

    public String getCompanyInfo() {
        String json = "";
        try (InputStream companyInfoStream = getClass().getClassLoader().getResourceAsStream(props.getCompanyInfoFilename())) {
            json = new String(companyInfoStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return json;
    }
}
