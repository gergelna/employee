package com.training.camel.cameltraining.route.aggregate;

import com.training.camel.cameltraining.service.dto.CompanyCarDTO;
import com.training.camel.cameltraining.service.dto.InEmployeeCsv;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEnricher implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        List<CompanyCarDTO> companyCars = newExchange.getIn().getBody(ArrayList.class);
        InEmployeeCsv inEmployeeCsv = oldExchange.getIn().getBody(InEmployeeCsv.class);

        inEmployeeCsv.setCompanyCarBrand(companyCars.stream().map(car -> car.getBrand()).collect(Collectors.joining(",")));
        oldExchange.getIn().setBody(inEmployeeCsv);
        return oldExchange;
    }
}
