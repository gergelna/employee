package com.training.camel.cameltraining.route;

import com.training.camel.cameltraining.component.util.Util;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class TimerRoute extends RouteBuilder {

    public static final String ROUTE_ID_LOOP = "loop";
    public static final String URI_DIRECT_LOOP = "direct:" + ROUTE_ID_LOOP;

    @Override
    public void configure() throws Exception {

        //@formatter:off
        from("timer://main?repeatCount=1&delay=1s")
            .routeId("timerRoute")
            .log("Timer route is started")
            //.setProperty(Util.EMPLOYEE_ID, constant(1))
            //.to(RestRoutes.URI_DIRECT_FETCH_ALL_REST_DATA)
            //.to(RestRoutes.URI_DIRECT_FETCH_COMPANY_CAR_BY_EMPLOYEE_ID)
            .to(URI_DIRECT_LOOP)
            .log("Timer route is ended")
            .end();

        from(URI_DIRECT_LOOP)
            .routeId(ROUTE_ID_LOOP)
            .log("Start sequence route")
            .loop(4).copy()
                .setProperty(Util.EMPLOYEE_ID, simple("${property.CamelLoopIndex}"))
                .to(RestRoutes.URI_DIRECT_FETCH_COMPANY_CAR_BY_EMPLOYEE_ID)
            .end()
            .log("End sequence route")
            .end();
        //@formatter:on
    }
}
