# MongoDB SSL/TLS

Only install a MongoDB distribution with SSL included. You can check here: https://www.mongodb.org/downloads?jmp=docs#production

Shows Windows, Linux, Mac OS X or Solaris. Currently you will see the OS X and Solaris packages aren’t linked with SSL libraries.

This is the official documentation to build the OS X distribution: https://docs.mongodb.org/manual/tutorial/install-mongodb-on-os-x/

### Mongodb 3.x Driver connection with JDK 7 or 8

These are all the official connection settings for reference:-
https://mongodb.github.io/mongo-java-driver/3.0/driver/reference/connecting/connection-settings/

The Gradle dependency used with this example will be downloaded from jcenter.

``` gradle
repositories {
  jcenter()
}

compile(“org.mongodb:mongo-java-driver:3.2.1”)
```

The Java SSL/TLS platform requires all certificates to be trusted and be install into a Key Store.

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
String uri = “mongoldb://<hostname>:27017?ssl=true”;
MongoClientURI connectionString = new MongoClientURI(uri);
MongoClient mongoClient = new MongoClient(connectionString);
MongoDatabase database = mongoClient.getDatabase(“foxtrot”);
```

>Tip: If SSL works when using the hostname but connecting with the IP address fails with a validation error. Check the hostname matches the certificate’s CN (Common Name) field.

To allow connecting with an IP address, as this would fail hostname validation, use the MongoClientOptions.Builder instead:-

``` java
String uri = “mongodb://<ip address>:27017/”;
MongoClientURI connectionString;
MongoClientOptions.Builder builder;
builder = MongoClientOptions.builder().sslEnabled(true).sslInvalidHostNameAllowed(true);
connectionString = new MongoClientURI(uri, builder);
MongoClient mongoClient = new MongoClient(connectionString);
```