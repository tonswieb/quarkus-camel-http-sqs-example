package org.acme;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderProcessingPojoRoute extends RouteBuilder {

    @ConfigProperty(name = "order.polling.url")
    String pollingUrl;

    @ConfigProperty(name = "aws.sqs.queue")
    String sqsQueue;

    @Inject
    OrderMapper orderMapper;

    @Override
    public void configure() throws Exception {
        // Configure JAXB data formats for input and output models
        JaxbDataFormat inputFormat = new JaxbDataFormat("org.acme.model.input");
        JaxbDataFormat outputFormat = new JaxbDataFormat("org.acme.model.output");

        from("timer:pollOrdersPojo?delay=10000&period=10000")
            .to("http://" + pollingUrl)
            .to("validator:schemas/input.xsd")
            .unmarshal(inputFormat)
            .bean(orderMapper)
            .marshal(outputFormat)
            .to("validator:schemas/output.xsd")
            .to("aws2-sqs:" + sqsQueue + "?autoCreateQueue=true");
    }
} 