package com.opendatahub.collector.restclient;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;

@Provider
public class DefaultRequestParamFilter implements ClientRequestFilter {

    @ConfigProperty(name = "discoverswiss.apikey")
    String apiKey;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        URI uri = requestContext.getUri();
        UriBuilder uriBuilder = UriBuilder.fromUri(uri);

        // Check if the "includeCount" query parameter is present, if not, add it with a default value
        if (uri.getQuery() == null || !uri.getQuery().contains("includeCount")) {
            uriBuilder.queryParam("includeCount", "true");
        }

        // Check if the "top" query parameter is present, if not, add it with a default value
        if (uri.getQuery() == null || !uri.getQuery().contains("top")) {
            uriBuilder.queryParam("top", "100");
        }

        // Check if the "Ocp-Apim-Subscription-Key" header is present, if not, add it with a default value
        if (requestContext.getHeaderString("Ocp-Apim-Subscription-Key") == null) {
            requestContext.getHeaders().add("Ocp-Apim-Subscription-Key", apiKey);
        }

        // Update the request URI
        requestContext.setUri(uriBuilder.build());
    }

}
