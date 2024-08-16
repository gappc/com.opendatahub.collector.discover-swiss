package com.opendatahub.collector.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MongoService {

    @ConfigProperty(name = "quarkus.mongodb.connection-string")
    String connectionString;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String dbName;

    private MongoClient mongoClient;
    private MongoDatabase database;

    @PostConstruct
    public void init() {
        // Initialize the MongoDB client and database
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(dbName);
    }

    @PreDestroy
    public void close() {
        // Clean up resources
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public List<Document> getAllDocuments(String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<Document> documents = new ArrayList<>();
        collection.find().into(documents);
        return documents;
    }

    public String saveDocuments(String collectionName, List<Document> documents) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<WriteModel<Document>> bulkOps = new ArrayList<>();

        for (Document doc : documents) {
            // Assuming the document has a unique identifier field called "_id"
            Document filter = new Document("_id", doc.get("identifier"));
            Document update = new Document("$set", doc);

            // Create an update operation with upsert option
            UpdateOneModel<Document> updateOneModel = new UpdateOneModel<>(filter, update, new UpdateOptions().upsert(true));
            bulkOps.add(updateOneModel);
        }

        // Execute the bulk operations
        collection.bulkWrite(bulkOps, new BulkWriteOptions().ordered(false));

        return "Import complete";
    }
}

