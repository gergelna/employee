package com.training.camel.cameltraining.service.dto;

import lombok.Data;

@Data
public class CompanyCarDTO {

    private Integer id;
    private String brand;
    private String color;
    private String engineType;
    private Integer employeeId;

    public CompanyCarDTO() {

    }

    public CompanyCarDTO(Integer id, String brand, String color, String engineType, Integer employeeId) {
        this.id = id;
        this.brand = brand;
        this.color = color;
        this.engineType = engineType;
        this.employeeId = employeeId;
    }

}
