package com.training.camel.cameltraining.route;

import com.training.camel.cameltraining.component.converter.EmployeeToCsvConverter;
import com.training.camel.cameltraining.component.converter.EmployeeToFixedLenConverter;
import com.training.camel.cameltraining.component.mapper.EmployeeToCsvMapper;
import com.training.camel.cameltraining.component.util.Util;
import com.training.camel.cameltraining.route.aggregate.EmployeeEnricher;
import com.training.camel.cameltraining.service.dto.InEmployeeCsv;
import com.training.camel.cameltraining.service.dto.OutEmployeeCsv;
import com.training.camel.cameltraining.service.dto.OutEmployeeFixedLen;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.dataformat.bindy.fixed.BindyFixedLengthDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.stereotype.Component;

@Component
public class EmployeeCsvProcessRoute extends RouteBuilder {

    public static final String URI_DIRECT_OUTPUT_CSV = "direct:outputCsv";
    public static final String URI_DIRECT_OUTPUT_FEMALE_CSV = "direct:outputFemaleCsv";
    public static final String URI_DIRECT_OUTPUT_FIXED_LEN = "direct:outputFixedLen";

    private final EmployeeToCsvMapper employeeToCsvMapper;
    private final EmployeeToFixedLenConverter employeeToFixedLenConverter;
    private final EmployeeToCsvConverter employeeToCsvConverter;
    private final EmployeeEnricher employeeEnricher;

    public EmployeeCsvProcessRoute(EmployeeToCsvMapper employeeToCsvMapper,
                                   EmployeeToFixedLenConverter employeeToFixedLenConverter,
                                   EmployeeToCsvConverter employeeToCsvConverter,
                                   EmployeeEnricher employeeEnricher) {
        this.employeeToCsvMapper = employeeToCsvMapper;
        this.employeeToFixedLenConverter = employeeToFixedLenConverter;
        this.employeeToCsvConverter = employeeToCsvConverter;
        this.employeeEnricher = employeeEnricher;
    }

    @Override
    public void configure() throws Exception {
        DataFormat bindyInEmployeeCsv = new BindyCsvDataFormat(InEmployeeCsv.class);
        DataFormat bindyOutEmployeeToCsv = new BindyCsvDataFormat(OutEmployeeCsv.class);
        DataFormat bindyOutEmployeeToFixedLen = new BindyFixedLengthDataFormat(OutEmployeeFixedLen.class);

        Predicate maleNagy = PredicateBuilder.and(body().isNotNull(),
                                                      body().method("getGender").isEqualTo("male"),
                                                      body().method("getName").contains("Nagy"));

        //@formatter:off
        from("file:src/data/csv?noop=true")
            .routeId("csvFileReading")
            .log("csv file reading is started")
            .split().tokenize("\n").streaming()
                .log("body before unmarshal = ${body}")
                .unmarshal(bindyInEmployeeCsv)
                .log("id after unmarshal = ${body.id}")
                .setProperty(Util.EMPLOYEE_ID, simple("${body.id}"))
                .enrich(RestRoutes.URI_DIRECT_FETCH_COMPANY_CAR_BY_EMPLOYEE_ID, employeeEnricher)
                .multicast()
                    .to(URI_DIRECT_OUTPUT_CSV)
                    .to(URI_DIRECT_OUTPUT_FEMALE_CSV)
                    .to(URI_DIRECT_OUTPUT_FIXED_LEN)
                //.bean(employeeToCsvMapper, "employeeToOutput")
                //.marshal(bindyEmployeeOutputCsv)
                //.log("body after marshal = ${body}")
                //.to("file:src/data/output/?fileExist=append")
            .end();

        from(URI_DIRECT_OUTPUT_CSV)
            .routeId("outCsv")
            .bean(employeeToCsvConverter, "convertEmployee")
            .marshal(bindyOutEmployeeToCsv)
            .to("file:src/data/output/?fileName=OutEmployee.csv&fileExist=append")
            .end();

        from(URI_DIRECT_OUTPUT_FEMALE_CSV)
            .routeId("outFemaleCsv")
            //.filter().simple("${body.gender} == 'female'")
            //.filter(body().method("getGender").isEqualTo("female"))
            .filter(maleNagy)
            .bean(employeeToCsvConverter, "convertEmployee")
            .marshal(bindyOutEmployeeToCsv)
            .to("file:src/data/output/?fileName=OutFemaleEmployee.csv&fileExist=append")
            .end();

        from(URI_DIRECT_OUTPUT_FIXED_LEN)
            .routeId("outFixed")
            .bean(employeeToFixedLenConverter, "convertEmployee")
            .marshal(bindyOutEmployeeToFixedLen)
            .to("file:src/data/output/?fileName=OutEmployee.fixed&fileExist=append")
            .end();
        //@formatter:on
    }
}
