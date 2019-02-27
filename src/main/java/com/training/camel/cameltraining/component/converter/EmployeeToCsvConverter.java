package com.training.camel.cameltraining.component.converter;

import com.training.camel.cameltraining.component.mapper.EmployeeToCsvMapper;
import com.training.camel.cameltraining.component.util.Util;
import com.training.camel.cameltraining.service.dto.InEmployeeCsv;
import com.training.camel.cameltraining.service.dto.OutEmployeeCsv;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class EmployeeToCsvConverter {

    private final EmployeeToCsvMapper employeeMapper;

    public EmployeeToCsvConverter(EmployeeToCsvMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public OutEmployeeCsv convertEmployee(Exchange exchange) {
        InEmployeeCsv inputEmployee = exchange.getIn().getBody(InEmployeeCsv.class);
        OutEmployeeCsv outEmployeeCsv = employeeMapper.employeeToOutput(inputEmployee);

        outEmployeeCsv.setFirstName(Util.getFirstName(inputEmployee.getName()));
        outEmployeeCsv.setLastName(Util.getLastName(inputEmployee.getName()));
        return outEmployeeCsv;
    }

}
