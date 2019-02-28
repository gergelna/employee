package com.training.camel.cameltraining.service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@Data
@CsvRecord(separator = "\\,")
public class InEmployeeCsv {

    @DataField(pos = 1)
    private Long id;

    @DataField(pos = 2)
    private String name;

    @DataField(pos = 3)
    private String gender;

    @DataField(pos = 4)
    private Integer birthYear;

    @DataField(pos = 5)
    private String position;

    @DataField(pos = 6)
    private String address;

    @DataField(pos = 7)
    private String phoneNumber;

    //@DataField(pos = 6, pattern = "yyyy-MM-dd hh:mm:ss.nnn")
    @DataField(pos = 8, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @DataField(pos = 9)
    private Integer salary;

    private String companyCarBrand;
}
