package johny.dotsville.service;

import johny.dotsville.domain.MarriageCertificate;
import johny.dotsville.domain.Person;

public class CityService {
    private final MarriageService marriageService;

    public CityService(MarriageService marriageService) {
        this.marriageService = marriageService;
    }

    public MarriageCertificate marry(Person man, Person woman) {
        return marriageService.marry(man, woman);
    }
}
