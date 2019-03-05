package com.training.camel.cameltraining.service.dto;

import java.util.ArrayList;
import java.util.List;

public class Salaries {

    private List<SalaryCsv> salaries;

    public Salaries() {
        salaries = new ArrayList<>();
    }

    public List<SalaryCsv> getSalaries() {
        return salaries;
    }

    public void addSalary(SalaryCsv salary) {
        salaries.add(salary);
    }
}
