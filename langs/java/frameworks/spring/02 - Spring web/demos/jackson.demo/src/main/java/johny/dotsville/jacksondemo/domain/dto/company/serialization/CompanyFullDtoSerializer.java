package johny.dotsville.jacksondemo.domain.dto.company.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import johny.dotsville.jacksondemo.domain.dto.company.CompanyFullDto;
import johny.dotsville.jacksondemo.domain.dto.company.supply.DescriptionObject;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class CompanyFullDtoSerializer extends JsonSerializer<CompanyFullDto> {
    @Override
    public void serialize(CompanyFullDto dto, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartObject();  // {

        generator.writeFieldName("data");  // "data":
        generator.writeStartObject();  // {

        generator.writeFieldName("company");  // "company":
        generator.writeStartObject();  // {

        // Способ записать поле одной командой - разом имя и значение
        generator.writeStringField("id", dto.getId());
        generator.writeStringField("shortName", dto.getShortName());
        generator.writeStringField("address", dto.getAddress());
        generator.writeStringField("url", dto.getUrl());
        generator.writeNumberField("ogrn", dto.getOgrn());
        generator.writeNumberField("inn", dto.getInn());
        generator.writeNumberField("kpp", dto.getKpp());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        generator.writeStringField("registerDate", dto.getRegisterDate().format(dateFormat));

        generator.writeFieldName("contacts");  // "contacts":
        generator.writeStartObject(); // {
        // Первый способ записи массива
        generator.writeArrayFieldStart("phones");  // "phones": [
        for (DescriptionObject dob : dto.getContacts().getPhones()) {
            generator.writeStartObject();  // {
            // Способ B записать поле - через две команды
            generator.writeFieldName("value");  // Сначала объявить поле
            generator.writeString(dob.getValue());  // Потом записать его значение
            generator.writeFieldName("description");
            generator.writeString(dob.getDescription());
            generator.writeEndObject();  // }
        }
        generator.writeEndArray();  // ] (phones)
        // Второй способ записи массива
        generator.writeFieldName("emails");  // "emails":
        generator.writeStartArray();  // [
        for (DescriptionObject dob : dto.getContacts().getEmails()) {
            generator.writeObject(dob);  // Пишем объект целиком на этот раз
        }
        generator.writeEndArray();  // ] (emails)
        generator.writeEndObject();  // } (contacts)

        generator.writeStringField("description", dto.getDescription());

        int[] activityTypes = Arrays.stream(dto.getActivityTypes())
                .mapToInt(i -> i).toArray();
        generator.writeFieldName("activityTypes");
        // Метод умеет писать только простые типы, и кажется вообще не умеет пользовательские
        generator.writeArray(activityTypes, 0, activityTypes.length);

        generator.writeBooleanField("isValidated", dto.isValidated());
        generator.writeFieldName("geo");
        generator.writeObject(dto.getGeo());

        generator.writeEndObject();  // } (company)
        generator.writeEndObject();  // } (data)

        generator.writeEndObject();  // }
    }
}
