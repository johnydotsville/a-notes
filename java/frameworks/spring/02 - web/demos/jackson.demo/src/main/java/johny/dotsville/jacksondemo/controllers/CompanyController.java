package johny.dotsville.jacksondemo.controllers;

import johny.dotsville.jacksondemo.domain.dto.company.CompanyFullDto;
import johny.dotsville.jacksondemo.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
public class CompanyController {
    private final DataService dataService;

    @Autowired
    public CompanyController(DataService dataService) {
        this.dataService = dataService;
    }

    /**
     * ret json в виде обычной строки
     */
    @GetMapping("/info")
    public String getCompanyInfoAsString() {
        return dataService.getCompanyInfo();
    }

    @PostMapping("/serialization")
    public CompanyFullDto serializationDemo(@RequestBody CompanyFullDto companyFullDto) {
        return companyFullDto;
    }
}
