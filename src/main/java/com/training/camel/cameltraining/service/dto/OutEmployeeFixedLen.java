package com.training.camel.cameltraining.service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FixedLengthRecord;

@Data
@FixedLengthRecord(eol = "\r\n")
public class OutEmployeeFixedLen {

    @DataField(pos = 1, length = 4, paddingChar = '0')
    private Long employeeId;

    @DataField(pos = 2, length = 20)
    private String fullName;

    @DataField(pos = 3, length = 20)
    private String firstName;

    @DataField(pos = 4, length = 20)
    private String lastName;

    @DataField(pos = 5, length = 20)
    private String companyCarBrand;

    @DataField(pos = 6, length = 10, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @DataField(pos = 7, length = 20, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime today;
}
