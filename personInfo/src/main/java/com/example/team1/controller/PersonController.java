package com.example.team1.controller;

import com.example.team1.domain.*;
import com.example.team1.entity.Address;
import com.example.team1.entity.Contact;
import com.example.team1.entity.Employee;
import com.example.team1.entity.Person;
import com.example.team1.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/personInfo")
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private PersonalDocService personalDocService;

    @Autowired
    private VisaService visaService;

    @GetMapping("/")
    public ResponseEntity<List<Object>> getPersonalInfo(@RequestParam("id") Integer personId){
        Person person = personService.findPersonById(personId);
        List<Object> result = new ArrayList<>();
        if(person!=null){
            PersonDomain personDomain = personService.findPersonInfoById(person);
            ContactDomain contactDomain = ContactDomain.builder()
                    .cellPhone(person.getCellPhone())
                    .workPhone(person.getAlternatePhone())
                    .email(person.getEmail())
                    .workEmail(person.getEmail()).build();
            EmployeeDomain employeeDomain = employeeService.getEmployeeInfoByPersonId(person);
            List<AddressDomain> addressDomains = addressService.getAddressByPersonId(person);
            List<EmergencyContactDomain> emergencyContactDomains = contactService.getEmergencyByPersonId(person);
            Employee employee = employeeService.getEmployeeByPersonId(person);
            VisaStatusDomain visaStatusDomain = visaService.getVisaByPersonId(person);
            if(employee!=null){
                List<PersonalDocsDomain> docsDomains = personalDocService.getPersonalDocInfoByEmployeeId(employee);
                Collections.addAll(result,
                        personDomain,
                        employeeDomain,
                        addressDomains,
                        contactDomain,
                        emergencyContactDomains,
                        visaStatusDomain,
                        docsDomains);
                return ResponseEntity.ok(result);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/updateAddress")
    public ResponseEntity<List<AddressDomain>> updateAddress(@RequestBody List<AddressDomain> addressDomains,
                                                       @RequestParam("id") Integer personId){
        if(personId<=0){
            return ResponseEntity.notFound().build();
        }
        Person person = personService.findPersonById(personId);
        addressService.updateAddress(addressDomains, person);
        return ResponseEntity.ok(addressDomains);
    }

    @PostMapping("/updateEmployee")
    public ResponseEntity<EmployeeDomain> updateEmployee(@RequestBody EmployeeDomain employeeDomain, @RequestParam("id") Integer personId){
        if(personId<=0){
            return ResponseEntity.notFound().build();
        }
        Person person = personService.findPersonById(personId);
        employeeService.updateEmployee(employeeDomain, person);
        return ResponseEntity.ok(employeeDomain);
    }

    @PostMapping("/updateVisa")
    public ResponseEntity<VisaStatusDomain> updateVisa(@RequestBody VisaStatusDomain visaStatusDomain,
                                                       @RequestParam("id") Integer personId){
        if(personId<=0){
            return ResponseEntity.notFound().build();
        }
        Person person = personService.findPersonById(personId);
        visaService.updateVisaWithPerson(visaStatusDomain, person);
        return ResponseEntity.ok(visaStatusDomain);
    }

    @PostMapping("/updateDocs")
    public ResponseEntity<List<PersonalDocsDomain>> updateDocs(
            @RequestBody List<PersonalDocsDomain> personalDocsDomainList,
            @RequestParam("id") Integer personId){
        if(personId<=0){
            return ResponseEntity.notFound().build();
        }
        Person person = personService.findPersonById(personId);
        Employee employee = employeeService.getEmployeeByPersonId(person);
        if(person==null || employee==null){
            return ResponseEntity.notFound().build();
        }
        personalDocService.updatePersonalDocs(personalDocsDomainList, employee);
        return ResponseEntity.ok(personalDocsDomainList);
    }

    @PostMapping("/updateContact")
    public ResponseEntity<ContactDomain> updateContact(@RequestBody ContactDomain contactDomain, @RequestParam("id") Integer personId){
        if(personId<=0){
            return ResponseEntity.notFound().build();
        }
        personService.updateContact(contactDomain, personId);
        return ResponseEntity.ok(contactDomain);
    }

    @PostMapping("/updatePersonInfo")
    public ResponseEntity<PersonDomain> updatePerson(@RequestBody PersonDomain personDomain, @RequestParam("id") Integer personId){
        if(personId<=0){
            return ResponseEntity.notFound().build();
        }
        personService.updateInfo(personDomain, personId);
        return ResponseEntity.ok(personDomain);
    }

}