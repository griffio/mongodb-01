package example;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

import static com.mongodb.MongoClientOptions.builder;

public class Main {

  public static void main(String[] args) {

    System.setProperty("javax.net.ssl.trustStore", args[0]);
    System.setProperty("javax.net.ssl.trustStorePassword", args[1]);
    MongoClientOptions.Builder builder = builder().sslEnabled(true).sslInvalidHostNameAllowed(true);
    String uri = "mongodb://127.0.0.1:27017/";
    MongoClientURI connectionString = new MongoClientURI(uri, builder);
    MongoClient mongoClient = new MongoClient(connectionString);
  }
}
