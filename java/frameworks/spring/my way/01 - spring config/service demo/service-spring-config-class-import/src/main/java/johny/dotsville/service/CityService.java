package johny.dotsville.service;

import johny.dotsville.domain.MarriageCertificate;
import johny.dotsville.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    private final MarriageService marriageService;

    @Autowired
    public CityService(MarriageService marriageService) {
        this.marriageService = marriageService;
    }

    public MarriageCertificate marry(Person man, Person woman) {
        return marriageService.marry(man, woman);
    }
}
