package com.training.camel.cameltraining.web;

import com.training.camel.cameltraining.service.dto.CompanyCarDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MockedRestResource {

    @GetMapping(value = {"/companycars"})
    public ResponseEntity<List> getAllCompanyCars() {
        return ResponseEntity.ok().header("x-custom", "custom value").body(createCompanyCars());
    }

    @GetMapping(value = {"/companycars/{employeeId}"})
    public ResponseEntity<List> getCompanyCarsByEmployeeId(@PathVariable Integer employeeId) {

        List<CompanyCarDTO> companyCars = createCompanyCars().stream()
                                                             .filter(car -> car.getEmployeeId() == employeeId)
                                                             .collect(
                                                                 Collectors.toList());

        /*if (companyCars.isEmpty()){
            throw new IllegalArgumentException("Company Car is not found for Employee: " + employeeId);
        }*/

        return ResponseEntity.ok(companyCars);
    }

    private List<CompanyCarDTO> createCompanyCars() {
        List<CompanyCarDTO> companyCars = new ArrayList<>();
        companyCars.add(new CompanyCarDTO(1, "Ford", "White", "Gasoline", 1));
        companyCars.add(new CompanyCarDTO(2, "Mercedes", "Brown", "Gasoline", 2));
        companyCars.add(new CompanyCarDTO(3, "Kia", "Red", "Diesel", 3));
        companyCars.add(new CompanyCarDTO(4, "Alfa Romeo", "White", "Gasoline", 2));
        companyCars.add(new CompanyCarDTO(5, "Audi", "Green", "Diesel", 1));

        return companyCars;
    }
}
