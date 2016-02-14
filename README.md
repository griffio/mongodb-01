# mongodb-01

[install-mongodb-on-os-x](https://docs.mongodb.org/manual/tutorial/install-mongodb-on-os-x/)

### Mongodb 3.x Driver connection with JDK 7 or 8

[official mongodb connection settings](https://mongodb.github.io/mongo-java-driver/3.0/driver/reference/connecting/connection-settings/)

The Gradle dependency used with this example:-
```
compile(“org.mongodb:mongo-java-driver:3.2.1”)
```
The Java SSL platform requires all certificates to be trusted are install into a Key Store.

>Support-tip: You can create a Key Store using the JDK’s “keytool” utility. Requires you to enter a password at the end

```
keytool -importcert -trustcacerts -file <cert filename>.crt -keystore <output filename>.jks
```
Your application needs System properties to provide the Java platform SSL library with the location and password of the Key Store file:-
```
-Djavax.net.ssl.trustStore=<path to KeyStore file>
-Djavax.net.ssl.trustStorePassword=<password>
```
The Connection string used with the Java driver:-
```
String uri = “mongoldb://<hostname>:27017?ssl=true”;
MongoClientURI connectionString = new MongoClientURI(uri);
MongoClient mongoClient = new MongoClient(connectionString);
MongoDatabase database = mongoClient.getDatabase(“foxtrot”);
```
>Support-tip: If SSL works when using the hostname but connecting with the IP address fails with a validation error. Check the hostname matches the certificate’s CN (Common Name) field.

To allow connecting with an ip address, that would fail hostname validation, use the MongoClientOptions.Builder instead:-
```
String uri = “mongodb://<ip address>:27017/”;
MongoClientURI connectionString;
MongoClientOptions.Builder builder;
builder = MongoClientOptions.builder().sslEnabled(true).sslInvalidHostNameAllowed(true);
connectionString = new MongoClientURI(uri, builder);
MongoClient mongoClient = new MongoClient(connectionString);
```