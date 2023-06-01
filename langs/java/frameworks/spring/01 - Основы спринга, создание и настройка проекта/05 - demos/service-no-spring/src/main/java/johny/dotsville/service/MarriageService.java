package johny.dotsville.service;

import johny.dotsville.domain.MarriageCertificate;
import johny.dotsville.domain.Person;

import java.time.LocalDate;

public class MarriageService {
    public MarriageCertificate marry(Person man, Person woman) {
        return new MarriageCertificate(man, woman, LocalDate.now());
    }
}
