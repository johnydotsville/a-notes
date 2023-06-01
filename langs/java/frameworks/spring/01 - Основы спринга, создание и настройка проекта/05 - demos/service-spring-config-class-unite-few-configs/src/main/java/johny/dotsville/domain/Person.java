package johny.dotsville.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter @AllArgsConstructor
public class Person {
    private Name name;
    private LocalDate birth;
}
