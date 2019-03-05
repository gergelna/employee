package com.training.camel.cameltraining.component;

import com.training.camel.cameltraining.service.dto.InEmployeeCsv;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberTransformProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        InEmployeeCsv inEmployeeCsv = exchange.getIn().getBody(InEmployeeCsv.class);
        if (inEmployeeCsv.getPhoneNumber().startsWith("06")) {
            inEmployeeCsv.setPhoneNumber(inEmployeeCsv.getPhoneNumber().replaceFirst("06", "+36"));
        }
        exchange.getIn().setBody(inEmployeeCsv);
    }
}
