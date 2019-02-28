package com.training.camel.cameltraining.route.aggregate;

import com.training.camel.cameltraining.service.dto.InEmployeeCsv;
import com.training.camel.cameltraining.service.dto.SalaryCsv;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.PreCompletionAwareAggregationStrategy;

public class SalaryAggregator implements AggregationStrategy, PreCompletionAwareAggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        InEmployeeCsv inEmployeeCsv = newExchange.getIn().getBody(InEmployeeCsv.class);

        if (oldExchange == null){
            SalaryCsv salary = new SalaryCsv(inEmployeeCsv.getPosition());
            salary.addSalary(inEmployeeCsv);
            salary.increaseCount();
            newExchange.getIn().setBody(salary);

            return newExchange;
        }else {
            SalaryCsv salary = oldExchange.getIn().getBody(SalaryCsv.class);
            salary.addSalary(inEmployeeCsv);
            salary.increaseCount();
            return oldExchange;
        }
    }

    @Override
    public boolean preComplete(Exchange oldExchange, Exchange newExchange) {

        //return false;
        boolean retVal = false;
        InEmployeeCsv inEmployeeCsv= newExchange.getIn().getBody(InEmployeeCsv.class);

        if (oldExchange == null){
            retVal = inEmployeeCsv.getSalary() > 500;
            //return inEmployeeCsv.getSalary() > 500;
        } else {
            SalaryCsv salary = oldExchange.getIn().getBody(SalaryCsv.class);
            retVal = salary.getSalaries() + inEmployeeCsv.getSalary() > 500;
            //return salary.getSalaries() + inEmployeeCsv.getSalary() > 500;
        }
        return retVal;
    }
}
