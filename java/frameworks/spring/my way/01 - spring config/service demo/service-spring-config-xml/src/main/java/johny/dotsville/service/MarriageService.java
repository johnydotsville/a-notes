package johny.dotsville.service;

import johny.dotsville.domain.MarriageCertificate;
import johny.dotsville.domain.Person;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MarriageService {
    public MarriageCertificate marry(Person man, Person woman) {
        return new MarriageCertificate(man, woman, LocalDate.now());
    }
}
