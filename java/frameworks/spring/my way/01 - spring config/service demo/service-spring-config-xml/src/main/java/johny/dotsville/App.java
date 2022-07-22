package johny.dotsville;

import johny.dotsville.domain.MarriageCertificate;
import johny.dotsville.domain.Name;
import johny.dotsville.domain.Person;
import johny.dotsville.service.CityService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.time.LocalDate;

public class App
{
    public static void main( String[] args )
    {
        System.out.println( "Simple service demo with spring configured with xml-style" );

        ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
        CityService cityService = (CityService) context.getBean("cityService");

        Person harry = new Person(
                new Name("Гарри", "Поттер"),
                LocalDate.of(1980, 7, 31));
        Person ginny = new Person(
                new Name("Джинни", "Уизли"),
                LocalDate.of(1981, 8, 11));

        MarriageCertificate cert = cityService.marry(harry, ginny);

        System.out.println(cert);
    }
}
