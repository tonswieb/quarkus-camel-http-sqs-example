package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import net.sf.saxon.TransformerFactoryImpl;

@ApplicationScoped
public class XsltConfiguration {

    @Produces
    @Named("saxon")
    public TransformerFactoryImpl saxonFactory() {
        return new TransformerFactoryImpl();
    }
} 