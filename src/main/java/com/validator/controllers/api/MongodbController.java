package com.validator.controllers.api;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.validator.beans.MongodbValidationResult;
import java.time.Duration;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ComponentScan("com.validator.beans")
public class MongodbController {

  private static Logger LOGGER = LoggerFactory.getLogger(MongodbValidationResult.class);
  private final MongodbValidationResult mgdbValidationResult;

  public MongodbController(MongodbValidationResult serviceValidationResult) {
    this.mgdbValidationResult = serviceValidationResult;
  }

  @RequestMapping("/api/v1/ping/mgdb")
  MongodbValidationResult mgdb(HttpServletRequest request) {
    try {
      MongoDatabase database = mgdbValidationResult.getMongoClient().getDatabase("testdb");
      MongoCollection<Document> collection = database.getCollection("items");
      LocalDateTime oldDate = LocalDateTime.now();
      LOGGER.info("集合选择成功");
      Document document = new Document("fruit", "apple");
      collection.insertOne(document);
      LOGGER.info("文档插入成功");

      Document queryResult = collection.find(Filters.eq("fruit", "apple")).first();
      LOGGER.info(queryResult.toJson());

      LocalDateTime newDate = LocalDateTime.now();
      // count seconds between dates
      Duration duration = Duration.between(oldDate, newDate);
      this.mgdbValidationResult.setResponseTime(duration.toMillis());

    } catch (Exception e) {
      LOGGER.error(e.getClass().getName() + ":[EXCEPtION] " + e.getMessage());
    }
    return this.mgdbValidationResult;
  }
}
