package johny.dotsville.jacksondemo.domain.dto.company.supply;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Contacts {
    private DescriptionObject[] phones;
    private DescriptionObject[] emails;
}
