package org.acme;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderProcessingRoute extends RouteBuilder {

    @ConfigProperty(name = "order.polling.url")
    String pollingUrl;

    @ConfigProperty(name = "aws.sqs.queue")
    String sqsQueue;

    @Override
    public void configure() throws Exception {

        from("timer:pollOrders?delay=10000&period=10000")
            .to("http://" + pollingUrl)
            .to("validator:schemas/input.xsd")
            .to("xslt:classpath:xslt/transform.xslt?transformerFactory=#saxon")
            .to("validator:schemas/output.xsd")
            .to("aws2-sqs:" + sqsQueue + "?autoCreateQueue=true");
    }
} 