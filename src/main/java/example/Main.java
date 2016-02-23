package example;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mongodb.MongoClientOptions.builder;

public class Main {

  public static void main(String[] args) {

    System.setProperty("javax.net.ssl.trustStore", args[0]);
    System.setProperty("javax.net.ssl.trustStorePassword", args[1]);
    MongoClientOptions.Builder options = builder().sslEnabled(true).sslInvalidHostNameAllowed(true);
    String uri = "mongodb://127.0.0.1:27017/";
    MongoClientURI connectionString = new MongoClientURI(uri, options);
    MongoClient mongoClient = new MongoClient(connectionString);

    // with username and password

    char[] pwd = "mypasswrd!".toCharArray();

    MongoCredential credential = MongoCredential.createCredential("myadmin", "admin", pwd); // user "myadmin" on admin database

    List<MongoCredential> credentials = Collections.singletonList(credential);

    List<ServerAddress> hosts = Arrays.asList(
        new ServerAddress("mongodb01.host.dblayer.com:10054"),
        new ServerAddress("mongodb02.host.1.dblayer.com:10071"));

    mongoClient = new MongoClient(hosts, credentials, options.build());
    MongoDatabase foxtrot = mongoClient.getDatabase("foxtrot");
    MongoIterable<String> collectionNames = foxtrot.listCollectionNames();
  }
}
