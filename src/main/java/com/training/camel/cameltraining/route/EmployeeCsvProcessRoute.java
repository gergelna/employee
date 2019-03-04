package com.training.camel.cameltraining.route;

import com.training.camel.cameltraining.component.PhoneNumberTransformProcessor;
import com.training.camel.cameltraining.component.Validator;
import com.training.camel.cameltraining.component.converter.EmployeeToCsvConverter;
import com.training.camel.cameltraining.component.converter.EmployeeToFixedLenConverter;
import com.training.camel.cameltraining.component.mapper.EmployeeToCsvMapper;
import com.training.camel.cameltraining.component.util.Util;
import com.training.camel.cameltraining.route.aggregate.EmployeeEnricher;
import com.training.camel.cameltraining.route.aggregate.SalaryAggregator;
import com.training.camel.cameltraining.service.dto.InEmployeeCsv;
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
public class EmployeeCsvProcessRoute extends RouteBuilder {

    public static final String URI_DIRECT_UNMARSHAL_IN_EMPLOYEE_TRY_CATCH = "direct:unmarshalInEmployee";
    public static final String URI_DIRECT_TRANSFORM_EMPLOYEE = "direct:transformEmployee";
    public static final String URI_DIRECT_AGGREGATE_SALARIES = "direct:aggregateSalaries";

    private static final String ARCHIVE_FOLDER = "{{training.folder.archive}}/${date:now:yyyyMMddHHmm}/${file:name}";
    private static final String ERROR_FOLDER = "{{training.folder.error}}/${date:now:yyyyMMddHHmm}/${file:name}";

    public static DataFormat bindyInEmployeeCsv = new BindyCsvDataFormat(InEmployeeCsv.class);

    private final EmployeeEnricher employeeEnricher;
    private final PhoneNumberTransformProcessor phoneNumberTransformProcessor;
    private final Util util;

    public EmployeeCsvProcessRoute(EmployeeEnricher employeeEnricher,
                                   PhoneNumberTransformProcessor phoneNumberTransformProcessor,
                                   Util util) {
        this.employeeEnricher = employeeEnricher;
        this.phoneNumberTransformProcessor = phoneNumberTransformProcessor;
        this.util = util;
    }

    @Override
    public void configure() throws Exception {

        //@formatter:off
        errorHandler(deadLetterChannel(EmployeeOutRoutes.URI_DIRECT_OUTPUT_ERROR_ROUTE)
                         .useOriginalMessage()
                     );

        //from("file:src/data/csv?noop=true")
        from("file:src/data/csv?move=" + ARCHIVE_FOLDER + "&moveFailed=" + ERROR_FOLDER)
            .routeId("csvFileReading")
            .log("csv file reading is started")
            /*.onException(IllegalArgumentException.class)
                .log("exception: ${exception.message}")
                .handled(true)
                //.bean(util, "throwException")
            .end()*/
            .split().tokenize("\n").streaming()
                //.stopOnException()
                .log("body before unmarshal = ${body}")
                .unmarshal(bindyInEmployeeCsv)
                //.to(URI_DIRECT_UNMARSHAL_IN_EMPLOYEE_TRY_CATCH)
                .log("id after unmarshal = ${body.id}")
                .multicast()
                    .to(URI_DIRECT_AGGREGATE_SALARIES)
                    .to(URI_DIRECT_TRANSFORM_EMPLOYEE)

            .end()
            .log("after2Split")
            .end();

        from(URI_DIRECT_TRANSFORM_EMPLOYEE)
            .routeId("transferEmployeeRoute")
            .log("transferEmployee route is started")
            .setProperty(Util.EMPLOYEE_ID, simple("${body.id}"))
            .enrich(RestRoutes.URI_DIRECT_FETCH_COMPANY_CAR_BY_EMPLOYEE_ID, employeeEnricher)
            .process(phoneNumberTransformProcessor)
            .bean(Validator.class,"validatePhoneNumber(${body.phoneNumber})")
            .choice()
                .when(simple("${body.position} == 'manager'"))
                    .to(EmployeeOutRoutes.URI_DIRECT_OUTPUT_MANAGER_CSV)
                .otherwise()
                    .multicast()
                        .to(EmployeeOutRoutes.URI_DIRECT_OUTPUT_EMPLOYEE_CSV)
                        .to(EmployeeOutRoutes.URI_DIRECT_OUTPUT_FEMALE_EMPLOYEE_CSV)
                        .to(EmployeeOutRoutes.URI_DIRECT_OUTPUT_EMPLOYEE_FIXED_LEN)
            .endChoice()
            //.bean(employeeToCsvMapper, "employeeToOutput")
            //.marshal(bindyEmployeeOutputCsv)
            .log("endOfSplit")
            //.to("file:src/data/output/?fileExist=append")
            .end()
            .log("afterSplit")
        .end();

        from(URI_DIRECT_AGGREGATE_SALARIES)
            .routeId("aggregateSalaries")
            .log("Aggregate salaries route is started")
            .setHeader("position", simple("${body.position}"))
            .aggregate(header("position"), new SalaryAggregator())
                .eagerCheckCompletion()
                //.completion()
                .completionTimeout(1000)
                .to(EmployeeOutRoutes.URI_DIRECT_OUTPUT_AGGREGATED_SALARIES)
            .log("Aggregation is finished")
        .end();


        from(URI_DIRECT_UNMARSHAL_IN_EMPLOYEE_TRY_CATCH)
            .routeId("unmarshalEmployee")
            .doTry()
                .unmarshal(bindyInEmployeeCsv)
            .doCatch(IllegalArgumentException.class)
                //.throwException(IllegalArgumentException.class, "parsing error: ${body}")
                .bean(util, "throwException")
            .end();
        //@formatter:on
    }
}
