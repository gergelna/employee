package com.training.camel.cameltraining.component.util;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    public static String getFirstName(String name) {
        return (StringUtils.isEmpty(name) && name.contains(" ")) ? name.split(" ")[0] : "";
    }

    public static String getLastName(String name) {
        return (StringUtils.isEmpty(name) && name.contains(" ")) ? name.split(" ")[1] : "";
    }
}
