package com.training.camel.cameltraining.component.converter;

import com.training.camel.cameltraining.component.mapper.EmployeeToFixedLenMapper;
import com.training.camel.cameltraining.component.util.Util;
import com.training.camel.cameltraining.service.dto.InEmployeeCsv;
import com.training.camel.cameltraining.service.dto.OutEmployeeFixedLen;
import java.time.LocalDateTime;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class EmployeeToFixedLenConverter {

    private final EmployeeToFixedLenMapper employeeMapper;

    public EmployeeToFixedLenConverter(EmployeeToFixedLenMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public OutEmployeeFixedLen convertEmployee(Exchange exchange) {
        InEmployeeCsv inputEmployee = exchange.getIn().getBody(InEmployeeCsv.class);
        OutEmployeeFixedLen outEmployeeFixedLen = employeeMapper.employeeToOutput(inputEmployee);

        outEmployeeFixedLen.setToday(LocalDateTime.now());
        outEmployeeFixedLen.setFirstName(Util.getFirstName(inputEmployee.getName()));
        outEmployeeFixedLen.setLastName(Util.getLastName(inputEmployee.getName()));
        return outEmployeeFixedLen;
    }
}
