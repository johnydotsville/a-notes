package johny.dotsville.jacksondemo.domain.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import johny.dotsville.jacksondemo.domain.dto.company.serialization.CompanyFullDtoDeserializer;
import johny.dotsville.jacksondemo.domain.dto.company.serialization.CompanyFullDtoSerializer;
import johny.dotsville.jacksondemo.domain.dto.company.supply.Contacts;
import johny.dotsville.jacksondemo.domain.dto.company.supply.Geolocation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Полная информация о компании
 */
@Getter @Setter
@JsonDeserialize(using = CompanyFullDtoDeserializer.class)
@JsonSerialize(using = CompanyFullDtoSerializer.class)
public class CompanyFullDto {
    private String id;
    private String shortName;
    private String address;
    private String url;
    private long ogrn;
    private long inn;
    private long kpp;
    private LocalDate registerDate;
    private Contacts contacts;
    private String description;
    private Integer[] activityTypes;
    @JsonProperty("isValidated")
    private boolean isValidated;
    private Geolocation geo;
}
