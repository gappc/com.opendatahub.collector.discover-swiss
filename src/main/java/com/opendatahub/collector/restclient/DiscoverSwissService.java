package com.opendatahub.collector.restclient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://api.discover.swiss/info/v2")
public interface DiscoverSwissService {

    @GET
    @Path("/{collection}")
    String fetch(
            @PathParam("collection") String collection,
            @QueryParam("continuationToken") String continuationToken
    );
}
