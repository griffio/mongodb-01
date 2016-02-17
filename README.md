# MongoDB SSL/TLS

Only install a MongoDB distribution with SSL included. You can check here for the install packages: https://www.mongodb.org/downloads?jmp=docs#production
This shows Windows, Linux, Mac OS X or Solaris. Currently you will see the OS X and Solaris packages are not linked with SSL libraries.
For example, the official documentation to build the OS X distribution is here: https://docs.mongodb.org/manual/tutorial/install-mongodb-on-os-x/

### Mongodb 3.x Driver connection with JDK 7 or 8

These are all the official connection settings for your reference: https://mongodb.github.io/mongo-java-driver/3.0/driver/reference/connecting/connection-settings/

Using a Gradle build file, the driver dependency used with this example will be downloaded from jcenter.

``` gradle
repositories {
  jcenter()
}

compile(“org.mongodb:mongo-java-driver:3.2.1”)
```

The Java SSL/TLS platform requires all certificates to be trusted and be installed into a Key Store.

>Tip: You can create a Key Store using the JDK’s “keytool” utility. Requires you to enter a password at the end.

```
keytool -importcert -trustcacerts -file <cert filename>.crt -keystore <output filename>.jks
```

Your application needs System properties to provide the Java platform SSL library with the location and password of the Key Store file:-

```
-Djavax.net.ssl.trustStore=<path to KeyStore file>
-Djavax.net.ssl.trustStorePassword=<password>
```

The connection string used with the Java driver:-

``` java

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

String uri = “mongoldb://<hostname>:27017?ssl=true”;
MongoClientURI connectionString = new MongoClientURI(uri);
MongoClient mongoClient = new MongoClient(connectionString);
MongoDatabase database = mongoClient.getDatabase(“foxtrot”);
```

>Tip: If SSL works when using the hostname but connecting with the IP address fails with a validation error, check the hostname matches the certificate’s CN (Common Name) field.

To allow connecting with an IP address, as this would fail hostname validation, use the MongoClientOptions.Builder instead:-

``` java

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

String uri = “mongodb://<ip address>:27017/”;
MongoClientURI connectionString;
MongoClientOptions.Builder builder;
builder = MongoClientOptions.builder().sslEnabled(true).sslInvalidHostNameAllowed(true);
connectionString = new MongoClientURI(uri, builder);
MongoClient mongoClient = new MongoClient(connectionString);
```