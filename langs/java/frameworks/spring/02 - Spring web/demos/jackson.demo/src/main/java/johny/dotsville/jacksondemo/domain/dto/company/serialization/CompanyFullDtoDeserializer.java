package johny.dotsville.jacksondemo.domain.dto.company.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import johny.dotsville.jacksondemo.domain.dto.company.CompanyFullDto;
import johny.dotsville.jacksondemo.domain.dto.company.supply.Contacts;
import johny.dotsville.jacksondemo.domain.dto.company.supply.Geolocation;

import java.io.IOException;
import java.time.LocalDate;

public class CompanyFullDtoDeserializer extends JsonDeserializer<CompanyFullDto> {
    @Override
    public CompanyFullDto deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JacksonException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        try {
            CompanyFullDto dto = new CompanyFullDto();
            dto.setId(node.findValue("id").asText());
            dto.setShortName(node.findValue("shortName").asText());
            dto.setAddress(node.findValue("address").asText());
            dto.setUrl(node.findValue("url").asText());
            dto.setOgrn(node.findValue("ogrn").asLong());
            dto.setInn(node.findValue("inn").asLong());
            dto.setKpp(node.findValue("kpp").asLong());

            JsonNode registerDateNode = node.findValue("registerDate");
            dto.setRegisterDate(codec.treeToValue(registerDateNode, LocalDate.class));

            JsonNode contactsNode = node.findValue("contacts");
            dto.setContacts(codec.treeToValue(contactsNode, Contacts.class));

            dto.setDescription(node.findValue("description").asText());

            JsonNode activityTypesNode = node.findValue("activityTypes");
            dto.setActivityTypes(codec.treeToValue(activityTypesNode, Integer[].class));

            dto.setValidated(node.findValue("isValidated").asBoolean());

            JsonNode geoNode = node.findValue("geo");
            dto.setGeo(codec.treeToValue(geoNode, Geolocation.class));

            return dto;
        } catch (Exception ex) {
            RuntimeException rex = new RuntimeException(
                    "Ошибка при десериализации класса " + CompanyFullDtoDeserializer.class.getName());
            throw rex;
        }
    }
}
