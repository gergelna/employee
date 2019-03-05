package com.training.camel.cameltraining.route;

import com.training.camel.cameltraining.component.converter.EmployeeToCsvConverter;
import com.training.camel.cameltraining.component.converter.EmployeeToFixedLenConverter;
import com.training.camel.cameltraining.component.mapper.EmployeeToCsvMapper;
import com.training.camel.cameltraining.service.dto.OutEmployeeCsv;
import com.training.camel.cameltraining.service.dto.OutEmployeeFixedLen;
import com.training.camel.cameltraining.service.dto.SalaryCsv;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.dataformat.bindy.fixed.BindyFixedLengthDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.stereotype.Component;

@Component
public class EmployeeOutRoutes extends RouteBuilder {

    public static final String URI_DIRECT_OUTPUT_EMPLOYEE_CSV = "direct:outputCsv";
    public static final String URI_DIRECT_OUTPUT_MANAGER_CSV = "direct:outputManagerCsv";
    public static final String URI_DIRECT_OUTPUT_CUSTOM_EMPLOYEE_CSV = "direct:outputFemaleCsv";
    public static final String URI_DIRECT_OUTPUT_EMPLOYEE_FIXED_LEN = "direct:outputFixedLen";
    public static final String URI_DIRECT_OUTPUT_AGGREGATED_SALARIES = "direct:outAggregatedSalaries";
    public static final String URI_DIRECT_OUTPUT_ERROR_ROUTE = "direct:outError";

    private final EmployeeToCsvMapper employeeToCsvMapper;
    private final EmployeeToFixedLenConverter employeeToFixedLenConverter;
    private final EmployeeToCsvConverter employeeToCsvConverter;

    public EmployeeOutRoutes(EmployeeToCsvMapper employeeToCsvMapper,
                             EmployeeToFixedLenConverter employeeToFixedLenConverter,
                             EmployeeToCsvConverter employeeToCsvConverter) {
        this.employeeToCsvMapper = employeeToCsvMapper;
        this.employeeToFixedLenConverter = employeeToFixedLenConverter;
        this.employeeToCsvConverter = employeeToCsvConverter;
    }

    @Override
    public void configure() throws Exception {
        DataFormat bindyOutEmployeeToCsv = new BindyCsvDataFormat(OutEmployeeCsv.class);
        DataFormat bindyOutAggregatedToCsv = new BindyCsvDataFormat(SalaryCsv.class);
        DataFormat bindyOutEmployeeToFixedLen = new BindyFixedLengthDataFormat(OutEmployeeFixedLen.class);

        Predicate maleAndNagy = PredicateBuilder.and(body().isNotNull(),
                                                     body().method("getGender").isEqualTo("male"),
                                                     body().method("getName").contains("Nagy"));

        //@formatter:off
        from(URI_DIRECT_OUTPUT_EMPLOYEE_CSV)
            .routeId("outCsv")

            .log("outCsv route is started")
            .bean(employeeToCsvConverter, "convertEmployee")
            .process(exchange -> {
                int i = 2;
            })
            .marshal(bindyOutEmployeeToCsv)
            .to("file:src/data/output/?fileName=OutEmployee.csv&fileExist=append")
            .end();

        from(URI_DIRECT_OUTPUT_CUSTOM_EMPLOYEE_CSV)
            .routeId("outCustomCsv")
            .log("outCustomCsv route is started")
            .filter().simple("${body.gender} == 'female'")
            //.filter(body().method("getGender").isEqualTo("female"))
            //.filter(maleAndNagy)
            .bean(employeeToCsvConverter, "convertEmployee")
            .marshal(bindyOutEmployeeToCsv)
            .to("file:src/data/output/?fileName=OutFemaleEmployee.csv&fileExist=append")
            .end();

        from(URI_DIRECT_OUTPUT_MANAGER_CSV)
            .routeId("outManagerCsv")
            .log("outManagerCsv route is started")
            .bean(employeeToCsvConverter, "convertEmployee")
            .marshal(bindyOutEmployeeToCsv)
            .to("file:src/data/output/?fileName=OutManager.csv&fileExist=append")
            .end();

        from(URI_DIRECT_OUTPUT_AGGREGATED_SALARIES)
            .routeId("outAggregatedCsv")
            .log("outAggregatedCsv route is started")
            //.bean(employeeToCsvConverter, "convertEmployee")
            .marshal(bindyOutAggregatedToCsv)
            .to("file:src/data/output/?fileName=OutAggregatedSalaries.csv&fileExist=append")
            .end();

        from(URI_DIRECT_OUTPUT_EMPLOYEE_FIXED_LEN)
            .routeId("outFixed")
            .log("outFixedLen route is started")
            .bean(employeeToFixedLenConverter, "convertEmployee")
            .marshal(bindyOutEmployeeToFixedLen)
            .to("file:src/data/output/?fileName=OutEmployee.fixed&fileExist=append")
            .end();

        from(URI_DIRECT_OUTPUT_ERROR_ROUTE)
            .routeId("errorRoute")
            .log("Error route is started")
            .marshal(EmployeeCsvProcessRoute.bindyInEmployeeCsv)
            .to("file:src/data/output/?fileName=OutErrorEmployee.csv&fileExist=append")
            .end();
        //@formatter:on
    }
}
