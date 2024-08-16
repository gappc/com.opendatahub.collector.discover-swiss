package com.opendatahub.collector;

import com.opendatahub.collector.mongo.MongoService;
import com.opendatahub.collector.restclient.DiscoverSwissService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.bson.Document;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;

@Path("/api")
public class CollectorResource {

    @Inject
    @RestClient
    DiscoverSwissService discoverSwissService;

    @Inject
    MongoService mongoService;

    @GET
    @Path("/{collection}/fromDiscoverSwiss")
    @Produces(MediaType.APPLICATION_JSON)
    public String fetchDiscoverSwiss(@PathParam("collection") String collection) {
        return discoverSwissService.fetch(collection, null);
    }

    @GET
    @Path("/{collection}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Document> fetchMongo(@PathParam("collection") String collection) {
        return mongoService.getAllDocuments(collection);
    }

    @POST
    @Path("/{collection}/import")
    @Produces(MediaType.APPLICATION_JSON)
    public String importIntoMongo(@PathParam("collection") String collection) {
        List<Document> documents = fetchAll(collection);
        return mongoService.saveDocuments(collection, documents);
    }

    private List<Document> fetchAll(String collection) {
        List<Document> result = new ArrayList<>();

        String continuationToken = null;
        boolean hasNextPage = true;

        while (hasNextPage) {
            String fetch = discoverSwissService.fetch(collection, continuationToken);
            Document document = Document.parse(fetch);
            result.addAll(document.getList("data", Document.class));

            continuationToken = document.get("nextPageToken", String.class);
            hasNextPage = document.get("hasNextPage", Boolean.class);
        }

        return result;
    }

}
