package com.training.camel.cameltraining.route;

import com.training.camel.cameltraining.component.PhoneNumberTransformProcessor;
import com.training.camel.cameltraining.component.Validator;
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

    public static final String URI_DIRECT_OUTPUT_EMPLOYEE_CSV = "direct:outputCsv";
    public static final String URI_DIRECT_OUTPUT_MANAGER_CSV = "direct:outputManagerCsv";
    public static final String URI_DIRECT_OUTPUT_FEMALE_EMPLOYEE_CSV = "direct:outputFemaleCsv";
    public static final String URI_DIRECT_OUTPUT_EMPLOYEE_FIXED_LEN = "direct:outputFixedLen";
    public static final String URI_DIRECT_UNMARSHAL_IN_EMPLOYEE_TRY_CATCH = "direct:unmarshalInEmployee";
    public static final String URI_DIRECT_UNMARSHAL_IN_EMPLOYEE_ONEXCEPTION = "direct:unmarshalInEmployeeOnException";

    private static final String ARCHIVE_FOLDER = "{{training.folder.archive}}/${date:now:yyyyMMddHHmm}/${file:name}";
    private static final String ERROR_FOLDER = "{{training.folder.error}}/${date:now:yyyyMMddHHmm}/${file:name}";

    private final EmployeeToCsvMapper employeeToCsvMapper;
    private final EmployeeToFixedLenConverter employeeToFixedLenConverter;
    private final EmployeeToCsvConverter employeeToCsvConverter;
    private final EmployeeEnricher employeeEnricher;
    private final PhoneNumberTransformProcessor phoneNumberTransformProcessor;
    private final Util util;

    public EmployeeCsvProcessRoute(EmployeeToCsvMapper employeeToCsvMapper,
                                   EmployeeToFixedLenConverter employeeToFixedLenConverter,
                                   EmployeeToCsvConverter employeeToCsvConverter,
                                   EmployeeEnricher employeeEnricher,
                                   PhoneNumberTransformProcessor phoneNumberTransformProcessor,
                                   Util util) {
        this.employeeToCsvMapper = employeeToCsvMapper;
        this.employeeToFixedLenConverter = employeeToFixedLenConverter;
        this.employeeToCsvConverter = employeeToCsvConverter;
        this.employeeEnricher = employeeEnricher;
        this.phoneNumberTransformProcessor = phoneNumberTransformProcessor;
        this.util = util;
    }

    @Override
    public void configure() throws Exception {
        DataFormat bindyInEmployeeCsv = new BindyCsvDataFormat(InEmployeeCsv.class);
        DataFormat bindyOutEmployeeToCsv = new BindyCsvDataFormat(OutEmployeeCsv.class);
        DataFormat bindyOutEmployeeToFixedLen = new BindyFixedLengthDataFormat(OutEmployeeFixedLen.class);

        Predicate maleAndNagy = PredicateBuilder.and(body().isNotNull(),
                                                      body().method("getGender").isEqualTo("male"),
                                                      body().method("getName").contains("Nagy"));

        //@formatter:off
        //from("file:src/data/csv?noop=true")
        from("file:src/data/csv?move=" + ARCHIVE_FOLDER + "&moveFailed=" + ERROR_FOLDER)
            .routeId("csvFileReading")
            .log("csv file reading is started")
            .onException(IllegalArgumentException.class)
                .log("exception: ${exception.message}")
                .handled(true)
                //.bean(util, "throwException")
            .end()
            .split().tokenize("\n").streaming()
                //.stopOnException()
                .log("body before unmarshal = ${body}")
                //.unmarshal(bindyInEmployeeCsv)
                .to(URI_DIRECT_UNMARSHAL_IN_EMPLOYEE_TRY_CATCH)
                //.to(URI_DIRECT_UNMARSHAL_IN_EMPLOYEE_ONEXCEPTION)
                .log("id after unmarshal = ${body.id}")
                .setProperty(Util.EMPLOYEE_ID, simple("${body.id}"))
                .enrich(RestRoutes.URI_DIRECT_FETCH_COMPANY_CAR_BY_EMPLOYEE_ID, employeeEnricher)
                .process(phoneNumberTransformProcessor)
                .bean(Validator.class,"validatePhoneNumber(${body.phoneNumber})")
                .choice()
                    .when(simple("${body.position} == 'manager'"))
                        .to(URI_DIRECT_OUTPUT_MANAGER_CSV)
                    .otherwise()
                        .multicast()
                            .to(URI_DIRECT_OUTPUT_EMPLOYEE_CSV)
                            .to(URI_DIRECT_OUTPUT_FEMALE_EMPLOYEE_CSV)
                            .to(URI_DIRECT_OUTPUT_EMPLOYEE_FIXED_LEN)
                .endChoice()
                //.bean(employeeToCsvMapper, "employeeToOutput")
                //.marshal(bindyEmployeeOutputCsv)
                //.log("body after marshal = ${body}")
                //.to("file:src/data/output/?fileExist=append")
            .end();

        from(URI_DIRECT_UNMARSHAL_IN_EMPLOYEE_TRY_CATCH)
            .routeId("unmarshalEmployee")
            .doTry()
                .unmarshal(bindyInEmployeeCsv)
            .doCatch(IllegalArgumentException.class)
                //.throwException(IllegalArgumentException.class, "parsing error: ${body}")
                .bean(util, "throwException")
            .end();

  /*      from(URI_DIRECT_UNMARSHAL_IN_EMPLOYEE_ONEXCEPTION)
            .routeId("unmarshalEmployeeOnException")
            .log("unmarshal with onException route is started")
            .onException(IllegalArgumentException.class)
                .handled(true)
                .bean(util, "throwException")
                .to("direct:onExceptionLog")
            .end()
            .unmarshal(bindyInEmployeeCsv)
            //.throwException(IllegalArgumentException.class, "parsing error: ${body}")
            //.bean(util, "throwException")
            .end();*/

 /*       from("direct:onExceptionLog")
            .log("onExceptionLog from unmarshal route: ${exception.message}")
            .to("mock:1");

        from("direct:onExceptionLogFromMain")
            .log("onExceptionLog from main route")
            .to("mock:2");*/

        from(URI_DIRECT_OUTPUT_EMPLOYEE_CSV)
            .routeId("outCsv")
            .bean(employeeToCsvConverter, "convertEmployee")
            .marshal(bindyOutEmployeeToCsv)
            .to("file:src/data/output/?fileName=OutEmployee.csv&fileExist=append")
            .end();

        from(URI_DIRECT_OUTPUT_FEMALE_EMPLOYEE_CSV)
            .routeId("outFemaleCsv")
            //.filter().simple("${body.gender} == 'female'")
            //.filter(body().method("getGender").isEqualTo("female"))
            .filter(maleAndNagy)
            .bean(employeeToCsvConverter, "convertEmployee")
            .marshal(bindyOutEmployeeToCsv)
            .to("file:src/data/output/?fileName=OutFemaleEmployee.csv&fileExist=append")
            .end();

        from(URI_DIRECT_OUTPUT_MANAGER_CSV)
            .routeId("outManagerCsv")
            .bean(employeeToCsvConverter, "convertEmployee")
            .marshal(bindyOutEmployeeToCsv)
            .to("file:src/data/output/?fileName=OutManager.csv&fileExist=append")
            .end();

        from(URI_DIRECT_OUTPUT_EMPLOYEE_FIXED_LEN)
            .routeId("outFixed")
            .bean(employeeToFixedLenConverter, "convertEmployee")
            .marshal(bindyOutEmployeeToFixedLen)
            .to("file:src/data/output/?fileName=OutEmployee.fixed&fileExist=append")
            .end();
        //@formatter:on
    }
}
