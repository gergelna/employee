package com.training.camel.cameltraining.service.dto;

import java.time.LocalDate;
import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@Data
@CsvRecord(separator = "\\|")
public class OutEmployeeCsv {

    @DataField(pos = 1, length = 4, paddingChar = '0')
    private Long employeeId;

    @DataField(pos = 2, length = 20)
    private String fullName;

    @DataField(pos = 3)
    private String firstName;

    @DataField(pos = 4, length = 20)
    private String lastName;

    @DataField(pos = 5)
    private String companyCarBrand;

    @DataField(pos = 6, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @DataField(pos = 7)
    private String phoneNumber;
}
