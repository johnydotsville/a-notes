package johny.dotsville.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter @AllArgsConstructor
public class MarriageCertificate {
    private Person husband;
    private Person wife;
    private LocalDate registeredAt;

    @Override
    public String toString() {
        return "Данный документ свидетельствует о том, что гражданин " + husband.getName() +
                " и гражданка " + wife.getName() + " официально зарегистрировали свой брак " +
                registeredAt.toString();
    }
}
