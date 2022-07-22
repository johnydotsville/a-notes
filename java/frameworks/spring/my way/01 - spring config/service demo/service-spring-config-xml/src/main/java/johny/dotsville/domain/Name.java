package johny.dotsville.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class Name {
    private String firstName;
    private String lastName;

    @Override
    public String toString() {
        return String.format("%s %s", firstName, lastName);
    }
}
