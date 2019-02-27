package com.training.camel.cameltraining.route;

import com.training.camel.cameltraining.component.util.Util;
import com.training.camel.cameltraining.service.dto.CompanyCarDTO;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.springframework.stereotype.Component;

@Component
public class RestRoutes extends RouteBuilder {

    private final static String REST_BASE_URL = "http4://localhost:9001/camel/api/";

    public final static String ROUTE_ID_FETCH_REST_DATA = "fetchAllRestData";
    public final static String ROUTE_ID_FETCH_COMPANY_CAR_BY_EMPLOYEE_ID = "fetchCompanyCarByEmployeeId";

    public final static String URI_DIRECT_FETCH_ALL_REST_DATA = "direct:" + ROUTE_ID_FETCH_REST_DATA;
    public final static String URI_DIRECT_FETCH_COMPANY_CAR_BY_EMPLOYEE_ID = "direct:" + ROUTE_ID_FETCH_COMPANY_CAR_BY_EMPLOYEE_ID;

    private final Util util;

    public RestRoutes(Util util){
        this.util = util;
    }

    @Override
    public void configure() throws Exception {
        JacksonDataFormat companyCarListJsonFormat = new ListJacksonDataFormat(CompanyCarDTO.class);

        //@formatter:off
        from(URI_DIRECT_FETCH_ALL_REST_DATA)
            .routeId(ROUTE_ID_FETCH_REST_DATA)
            .log("Fetch all Rest data is route started")
            //.to(REST_BASE_URL + "companycars")
            .bean(util, "debug")
            .toD("{{training.rest.url}}/companycars")
            .convertBodyTo(String.class)
            .log("rest body: ${body}")
            .bean(util, "debug")
            .unmarshal(companyCarListJsonFormat)

            .split().simple("${body}").streaming()
                .log("spl body.id:${body.id}; whole body:${body}")
            .end()
            .log("Fetch all Rest data is route ended")
            .end();

        from(URI_DIRECT_FETCH_COMPANY_CAR_BY_EMPLOYEE_ID)
            .routeId(ROUTE_ID_FETCH_COMPANY_CAR_BY_EMPLOYEE_ID)
            .log("Fetch companyCar by Employee is route started")
            .bean(util, "validateEmployeeIdProperty")
            .log("EmployeeId = ${property." + Util.EMPLOYEE_ID + "}")
            .setHeader(Exchange.HTTP_METHOD, constant("GET"))
            .setBody(constant(null))
            .toD("{{training.rest.url}}/companycars/${property." + Util.EMPLOYEE_ID + "}")
            .convertBodyTo(String.class)
            .log("rest body: ${body}")
            .unmarshal(companyCarListJsonFormat)
            .log("Found companyCars:${body.size}")

            /*.split().simple("${body}").streaming()
                .log("body:'${body}'")
            .end()*/
            .log("Fetch companyCar by Employee route is ended")
            .end();
        //@formatter:on
    }
}