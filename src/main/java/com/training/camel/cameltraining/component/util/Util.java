package com.training.camel.cameltraining.component.util;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class Util {

    public static final String EMPLOYEE_ID = "employeeId";

    public void validateEmployeeIdProperty(Exchange exchange){
        if (exchange.getProperty(EMPLOYEE_ID) ==  null){
            throw new IllegalStateException("EmployeeId property does not exist");
        }
    }

    public void debug(Exchange exchange){
        int i = 4;
    }
}
