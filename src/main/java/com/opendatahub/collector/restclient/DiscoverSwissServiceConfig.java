package com.opendatahub.collector.restclient;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.net.URI;

@RegisterRestClient(configKey = "discover-swiss-service")
public class DiscoverSwissServiceConfig {

    // This method configures the REST client
    // The @SuppressWarnings annotation prevents tools from marking this method as unused
    @SuppressWarnings("unused")
    public static DiscoverSwissService createDiscoverSwissService() {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create("https://api.discover.swiss/info/v2"))
                .register(DefaultRequestParamFilter.class)
                .build(DiscoverSwissService.class);
    }
}
