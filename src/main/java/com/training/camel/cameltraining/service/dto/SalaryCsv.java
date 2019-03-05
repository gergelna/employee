package com.training.camel.cameltraining.service.dto;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@Data
@CsvRecord(separator = "\\|")
public class SalaryCsv {

    @DataField(pos = 1)
    private String position;

    @DataField(pos = 2)
    private int count;

    @DataField(pos = 3)
    private Integer salaries;

    public SalaryCsv(String position) {
        this.position = position;
        salaries = 0;
    }

    public void increaseCount() {
        count++;
    }

    public void addSalary(InEmployeeCsv inEmployeeCsv) {
        this.salaries += inEmployeeCsv.getSalary();
    }
}
