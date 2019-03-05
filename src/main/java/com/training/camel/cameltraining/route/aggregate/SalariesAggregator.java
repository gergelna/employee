package com.training.camel.cameltraining.route.aggregate;

import com.training.camel.cameltraining.service.dto.SalaryCsv;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class SalariesAggregator implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        SalaryCsv salary = newExchange.getIn().getBody(SalaryCsv.class);

        if (oldExchange == null) {
            List<SalaryCsv> salaries = new ArrayList<>();
            salaries.add(salary);
            newExchange.getIn().setBody(salaries);

            return newExchange;
        } else {
            List<SalaryCsv> salaries = oldExchange.getIn().getBody(List.class);
            salaries.add(salary);
            return oldExchange;
        }
    }
}
