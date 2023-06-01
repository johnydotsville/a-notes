package johny.dotsville.jacksondemo.domain.dto.company.supply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Geolocation {
    @JsonProperty("lat")
    private double latitude;
    @JsonProperty("long")
    private double longitude;
}
